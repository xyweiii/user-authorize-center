package com.plover.authorize.biz;

import com.plover.authorize.data.RoleAppResourceData;
import com.plover.authorize.entity.AppEntity;
import com.plover.authorize.model.App;
import com.plover.authorize.model.AppResource;
import com.plover.authorize.model.Staff;
import com.plover.authorize.model.StaffRole;
import com.plover.authorize.service.AppResourceService;
import com.plover.authorize.service.AppService;
import com.plover.authorize.service.RoleAppResourceService;
import com.plover.authorize.service.StaffRoleService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Project:user-center
 * Package: com.plover.user.biz
 *
 * @author : xywei
 * @date : 2021-01-19
 */
@Component
@Slf4j
public class AppBiz {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * app 原始文件存储位置
     */
    @Value("${app.file.origin}")
    private String originFilePath;

    /**
     * app 解压文件存储位置
     */
    @Value("${app.file.unzip}")
    private String unzipFilePath;

    @Autowired
    private AppService appService;

    @Autowired
    private StaffRoleService roleService;

    @Autowired
    private AppResourceService appResourceService;

    @Autowired
    private RoleAppResourceService roleAppResourceService;


    /**
     * 根据Staff 获取其被授权的App应用
     * (1) app 如果没有设置授权 ,用户可以访问
     * (2) staff 如果没有设置授权 ,用户可以访问所有应用
     * (3) staff & app 都设置了 授权角色,规则: 只要 staff角色包含app授权角色中任意一项就可以访问该app
     *
     * @param staff
     * @return
     * @see App#getRole()
     * @see Staff#getRole()
     */
    public List<App> findAuthorizedAppByStaff(Staff staff) {
        List<App> appList = appService.list();
        String staffRole = staff.getRole();
        //满足规则 (2)
        if (StringUtils.isBlank(staffRole)) {
            return appList;
        }
        List<String> staffRoleIdList = Arrays.asList(staffRole.split(","));
        List<App> filterAppList = appList.stream().filter(app -> {
            try {
                String appRole = app.getRole();
                //满足规则 (1)
                if (StringUtils.isBlank(appRole)) {
                    return true;
                }
                //满足规则 (3)
                AtomicReference<Boolean> containsFlag = new AtomicReference<>(false);
                List<String> appRoleIdList = Arrays.asList(appRole.split(","));
                appRoleIdList.stream().forEach(roleId -> {
                    if (staffRoleIdList.contains(roleId)) {
                        containsFlag.set(true);
                    }
                });
                return containsFlag.get();
            } catch (Exception e) {
                log.error("occur error app:{}", app, e);
                return false;
            }
        }).collect(Collectors.toList());
        return filterAppList;
    }


    /**
     * 获取该用户在该app中的授权资源信息
     *
     * @param id    应用id
     * @param staff 用户
     * @return
     */
    public List<AppResource> getRoleAppResourceInfo(Integer id, Staff staff) {
        String role = staff.getRole();
        List<StaffRole> roleList = new ArrayList<>();
        if (StringUtils.isNotBlank(role)) {
            roleList = Arrays.stream(role.split(","))
                    .map(roleId -> roleService.findById(Integer.valueOf(roleId)))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
        }
        //  获取该 权限在 该app 中的 访问资源集合
        List<AppResource> appResourceList = new ArrayList<>();
        roleList.stream().forEach(staffRole -> {
            List<AppResource> appResources = getAppResourceByAppIdAndRoleId(staffRole.getId(), id);
            if (!CollectionUtils.isEmpty(appResources)) {
                appResourceList.addAll(appResources);
            }
        });
        return appResourceList;
    }


    /**
     * 根据 appId 和 roleId 获取 访问资源集合
     *
     * @param roleId
     * @param appId
     * @return
     */
    public List<AppResource> getAppResourceByAppIdAndRoleId(Integer roleId, Integer appId) {
        RoleAppResourceData data = roleAppResourceService.findByRoleIdAndAppId(roleId, appId);
        if (data == null || CollectionUtils.isEmpty(data.getResources())) {
            return new ArrayList<>();
        }
        return data.getResources().stream()
                .map(id -> appResourceService.findById(id))
                .collect(Collectors.toList());
    }


    /**
     * 获取所有应用列表
     *
     * @return
     */
    public List<AppEntity> list() {
        List<App> appList = appService.list();
        return convert(appList);
    }

    /**
     * app数据转换
     * app -->  appEntity
     *
     * @param appList
     * @return
     */
    public List<AppEntity> convert(List<App> appList) {
        return appList.stream().map(app -> {
            AppEntity appEntity = new AppEntity();
            BeanUtils.copyProperties(app, appEntity);
            if (app.getCreateDate() != null) {
                appEntity.setCreateDate(DateFormatUtils.format(app.getCreateDate(), DATE_TIME_FORMAT));
            }
            if (app.getUpdateDate() != null) {
                appEntity.setUpdateDate(DateFormatUtils.format(app.getUpdateDate(), DATE_TIME_FORMAT));
            }
            String role = app.getRole();
            if (StringUtils.isNotBlank(role)) {
                List<StaffRole> roleList = Arrays.stream(role.split(","))
                        .map(roleId -> roleService.findById(Integer.valueOf(roleId))).collect(Collectors.toList());
                appEntity.setRoleList(roleList);
            }
            return appEntity;
        }).collect(Collectors.toList());
    }

    /**
     * 新增
     *
     * @param app
     * @return
     */
    public int add(App app) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        app.setAppId(uuid);
        return appService.add(app);
    }

    /**
     * 更新
     *
     * @param app
     * @return
     */
    public int update(App app) {
        return appService.update(app);
    }

    /**
     * 删除
     *
     * @param id
     * @param updateBy 更新人
     * @return
     */
    public int delete(Integer id, String updateBy) {
        if (id == null) {
            return 0;
        }
        return appService.delete(id, updateBy);
    }

    /**
     * 上传 轻应用文件
     *
     * @param file
     * @param id   应用id
     */
    public void uploadFile(MultipartFile file, Integer id) throws IOException, InterruptedException {
        App app = appService.findById(id);
        if (app == null) {
            return;
        }
        String originalFilename = file.getOriginalFilename();
        File originPathFile = new File(originFilePath);
        if (!originPathFile.exists()) {
            originPathFile.mkdirs();
        }
        //重复文件删除
        File existFile = new File(originFilePath + originalFilename);
        if (existFile.exists()) {
            existFile.delete();
        }
        existFile.createNewFile();
        FileOutputStream outputStream = new FileOutputStream(existFile);
        //上传app 文件至 指定目录
        FileCopyUtils.copy(file.getInputStream(), outputStream);
        //unzip -O CP936 -o test.zip -d /tmp/
        Thread.sleep(3000);
        //
        String command = " unzip -o " + originFilePath + originalFilename + " -d " + unzipFilePath + id;
        System.out.println(command);
        Process process = Runtime.getRuntime().exec(command);

        printMessage(process.getInputStream());
        printMessage(process.getErrorStream());
        int value = process.waitFor();
        log.info("command exec result:{}", value);
    }


    private static void printMessage(final InputStream input) {
        new Thread(() -> {
            Reader reader = new InputStreamReader(input);
            BufferedReader bf = new BufferedReader(reader);
            String line = null;
            try {
                while ((line = bf.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    /**
     * 泥融 新增 app应用
     *
     * @param app
     * @return
     */
    public App addByNiRong(App app) {
        String uuid = UUID.randomUUID().toString().replace("-", "");
        app.setAppId(uuid);
        app.setCreateBy("NiRong");
        app.setUpdateBy("NiRong");
        app.setType(1);
        int id = appService.addByNiRong(app);
        System.out.println(id);
        if (id > 0) {
            return appService.findById(id);
        }
        return null;
    }
}
