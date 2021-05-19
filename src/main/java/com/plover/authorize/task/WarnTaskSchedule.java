package com.plover.authorize.task;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Lists;
import com.plover.authorize.data.WarnTaskData;
import com.plover.authorize.data.WarnTaskLogData;
import com.plover.authorize.data.WarnTaskMsgData;
import com.plover.authorize.model.Staff;
import com.plover.authorize.service.StaffService;
import com.plover.authorize.service.WarnTaskLogService;
import com.plover.authorize.service.WarnTaskMsgService;
import com.plover.authorize.service.WarnTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@Component
@Slf4j
public class WarnTaskSchedule implements InitializingBean {

    /**
     * 用户id对应关系
     * key:  采购系统中的 userId
     * value: 统一用户中的 Staff
     *
     * @see Staff
     */
    public static Cache<String, Staff> staffCache = CacheBuilder.newBuilder()
            .expireAfterWrite(10, TimeUnit.MINUTES).build();


    public static final Pattern pattern = Pattern.compile("(\\$\\{)([\\w]+)(\\})");

    private static JdbcTemplate jdbcTemplate;

    @Autowired
    private StaffService staffService;

    @Autowired
    private WarnTaskService taskService;

    @Autowired
    private WarnTaskLogService taskLogService;

    @Autowired
    private WarnTaskMsgService taskMsgService;


    @Value("${sqlApi.datasource.url}")
    private String url;

    @Value("${sqlApi.datasource.username}")
    private String username;

    @Value("${sqlApi.datasource.password}")
    private String password;

    @Override
    public void afterPropertiesSet() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        jdbcTemplate = new JdbcTemplate(dataSource);
    }


    /**
     * 每天凌晨1点执行任务
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void runTask() {
        taskService.list().forEach(task -> CompletableFuture.runAsync(() -> {
            log.info("start excute  task:{}", task);
            //增加 预警执行日志
            WarnTaskLogData taskLog = new WarnTaskLogData();
            try {
                String sql = task.getSql();
                List<Staff> pushStaffList = Lists.newArrayList();
                List<Map<String, Object>> dataList = Lists.newArrayList();
                if (StringUtils.isNotBlank(sql)) {
                    //根据sql查询  生成预警消息记录
                    pushStaffList = getPushStaffList(task);
                    dataList = jdbcTemplate.queryForList(sql);
                    for (Map<String, Object> data : dataList) {
//                        String userId = (String) data.get("userId");
//                        Staff staff = getStaffByUserId(userId);
//                        if (staff == null) {
//                            continue;
//                        }
                        //解析内容
                        String content = handleContent(data, task);
                        log.info("WarnTaskMsg: content :{}", content);
                        pushStaffList.stream().forEach(staff -> {
                            //为每位
                            WarnTaskMsgData taskMsg = new WarnTaskMsgData();
                            taskMsg.setTaskId(task.getId());
                            taskMsg.setAppId(task.getAppId());
                            taskMsg.setStaffId(staff.getId());
                            taskMsg.setPsnCode(staff.getPsnCode());
                            taskMsg.setContent(content);
                            taskMsgService.add(taskMsg);
                        });
                    }
                }
                taskLog.setTaskId(task.getId());
                taskLog.setMsg("执行成功,推送消息条数为:" + dataList.size());
                List<Integer> staffIds = pushStaffList.stream().map(Staff::getId).collect(Collectors.toList());
                taskLog.setStaffIds(staffIds);
            } catch (Exception e) {
                log.error("WarnTaskSchedule occur error", e);
                taskLog.setMsg("执行失败,异常原因:" + e.getMessage());
            }
            taskLogService.add(taskLog);
        }));
    }

    /**
     * 获取预警任务 需推送人员
     *
     * @param task
     * @return
     */
    public List<Staff> getPushStaffList(WarnTaskData task) {
        List<Integer> roleIds = task.getRoleIds();
        List<Staff> allStaffList = Lists.newArrayList();
        roleIds.stream().forEach(roleId -> {
            List<Staff> staffList = staffService.findByRoleId(roleId);
            allStaffList.addAll(staffList);
        });
        return allStaffList.stream().distinct().filter(Objects::nonNull).collect(Collectors.toList());
    }

    /**
     * 根据推送模版  替换其中变量 生成最终内容
     *
     * @param data
     * @param task
     * @return
     */
    public static String handleContent(Map<String, Object> data, WarnTaskData task) {
        String pushTemplate = task.getPushTemplate();
        Matcher m = pattern.matcher(pushTemplate);//
        StringBuffer content = new StringBuffer();
        while (m.find()) {
            String group = m.group();// 匹配 符合规则的字符串
            log.info("matched String  is :{}", group);
            String key = group.replace("${", "").replace("}", "");
            String value = (String) data.getOrDefault(key, "");
            m.appendReplacement(content, value);
        }
        m.appendTail(content);
        return content.toString();
    }


    /**
     * 根据  采购系统中的userId 查询psnCode 并 根据psnCode 转换为 Staff
     *
     * @param userId 采购系统中的useId
     * @return
     * @see Staff
     */
    public Staff getStaffByUserId(String userId) {
        Staff staff = staffCache.getIfPresent(userId);
        if (staff != null) {
            return staff;
        } else {
            String sql = "SELECT " +
                    " bp.PSNCODE  " +
                    " FROM " +
                    " sm_user su " +
                    " LEFT JOIN sm_userandclerk smul ON su.CUSERID = smul.USERID " +
                    " LEFT JOIN bd_psndoc bp ON bp.PK_PSNBASDOC = smul.PK_PSNDOC " +
                    "WHERE " +
                    " su.CUSERID = '" + userId + "'";
            String psnCode = jdbcTemplate.queryForObject(sql, String.class);
            log.info(" query psnCode userId , userId:{},psnCode:{}", userId, psnCode);
            if (StringUtils.isBlank(psnCode)) {
                return null;
            }
            staff = staffService.findByPsnCode(psnCode);
            staffCache.put(userId, staff);
        }
        return staff;
    }


    public static void main(String[] args) {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", "张三");
        WarnTaskData taskData = new WarnTaskData();
        taskData.setPushTemplate("${userName},你的订单已经逾期，请及时处理。");
        String content = handleContent(data, taskData);
        System.out.println(content);
    }
}
