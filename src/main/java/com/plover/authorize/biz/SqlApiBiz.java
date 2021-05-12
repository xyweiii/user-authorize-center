package com.plover.authorize.biz;

import com.plover.authorize.entity.SqlApiEntity;
import com.plover.authorize.model.SqlApi;
import com.plover.authorize.service.SqlApiService;
import com.plover.authorize.utils.NumberUtils;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Project:p804-sql-api
 * Package: com.x04.sql.biz
 *
 * @author : xywei
 * @date : 2021-03-01
 */
@Component
@Slf4j
public class SqlApiBiz implements InitializingBean {

    public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static final String DATE_FORMAT = "yyyy-MM-dd";

    private static JdbcTemplate jdbcTemplate;

    @Value("${sqlApi.datasource.url}")
    private String url;

    @Value("${sqlApi.datasource.username}")
    private String username;

    @Value("${sqlApi.datasource.password}")
    private String password;

    @Value("${sqlApi.query.host}")
    private String host;

    @Value("${sqlApi.query.port}")
    private String port;

    @Autowired
    private SqlApiService sqlApiService;

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
     * 列表查询
     *
     * @return
     */
    public List<SqlApiEntity> list() {
        List<SqlApi> sqlApiList = sqlApiService.list();
        return convert(sqlApiList);
    }

    /**
     * 数据转换
     *
     * @param sqlApiList
     * @return
     */
    public List<SqlApiEntity> convert(List<SqlApi> sqlApiList) {
        return sqlApiList.stream().map(sqlApi -> {
            SqlApiEntity entity = new SqlApiEntity();
            BeanUtils.copyProperties(sqlApi, entity);
            if (sqlApi.getCreateDate() != null) {
                entity.setCreateDate(DateFormatUtils.format(sqlApi.getCreateDate(), DATE_TIME_FORMAT));
            }
            if (sqlApi.getUpdateDate() != null) {
                entity.setUpdateDate(DateFormatUtils.format(sqlApi.getUpdateDate(), DATE_TIME_FORMAT));
            }
            return entity;
        }).collect(Collectors.toList());
    }

    /**
     * 新增
     *
     * @param sqlApi
     * @return
     */
    public synchronized int add(SqlApi sqlApi) {
        //最新的一条
        SqlApi newOne = sqlApiService.findNewOne();
        Integer id = 1;
        if (newOne != null) {
            id = newOne.getId() + 1;
        }
        sqlApi.setId(id);
        sqlApi.setApiUrl("http://" + host + ":" + port + "/sqlApi/query/" + id);
        return sqlApiService.add(sqlApi);
    }

    /**
     * 更新
     *
     * @param sqlApi
     * @return
     */
    public int update(SqlApi sqlApi) {
        return sqlApiService.update(sqlApi);
    }


    /**
     * 删除
     *
     * @param id
     * @return
     */
    public int deleteById(Integer id) {
        if (id == null) {
            return 0;
        }
        return sqlApiService.deleteById(id);
    }


    /**
     * 查询
     *
     * @param id
     * @return
     */
    public List<Map<String, Object>> query(Integer id) {
        if (id == null) {
            return new ArrayList<>();
        }
        SqlApi sqlApi = sqlApiService.findById(id);
        if (sqlApi == null || StringUtils.isBlank(sqlApi.getSqlStr())) {
            return null;
        }
        log.info("query sql :{}", sqlApi.getApiUrl());
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sqlApi.getSqlStr());
        return result;
    }


    /**
     * 根据psnCode 查询 userId
     *
     * @param psnCode
     * @return
     */
    public String getUserIdByPsnCode(String psnCode) {
        String userId = "";
        String sql = "SELECT\n" +
                "\tsu.CUSERID \n" +
                "FROM\n" +
                "\tsm_user su\n" +
                "\tLEFT JOIN sm_userandclerk smul ON su.CUSERID = smul.USERID\n" +
                "\tLEFT JOIN bd_psndoc bp ON bp.PK_PSNBASDOC = smul.PK_PSNDOC \n" +
                "WHERE\n" +
                "\tbp.PSNCODE = '" + psnCode + "'";
        userId = jdbcTemplate.queryForObject(sql, String.class);
        log.info(" getUserIdByPsnCode , userId:{}", userId);
        return userId;
    }


    /**
     * 采购员-根据状态统计订单数量
     */
    public Map<String, Object> caigouyuan1(String userId) {

        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "status");
        schema1.put("title", "状态");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "count");
        schema2.put("title", "数量");
        schema2.put("type", "measure");
        schemas.add(schema1);
        schemas.add(schema2);
        map.put("schemas", schemas);

        List<Map<String, Object>> data = new ArrayList<>();
        if (StringUtils.isBlank(userId)) {
            map.put("data", data);
            return map;
        }
        List<Integer> statusList = Arrays.asList(1, 2, 3, 4, 5, 6);
        Map<Integer, String> statusMap = new HashMap<>();
        statusMap.put(1, "订单未接收");
        statusMap.put(2, "完全到货");
        statusMap.put(3, "采购过程中");
        statusMap.put(4, "临近到货");
        statusMap.put(5, "部分到货");
        statusMap.put(6, "逾期未到货");
        statusList.forEach(status -> {
            String sql = " SELECT count(1) from  po_praybill_b_copy1 b  where b.dr=0 AND c_currentstatus=" + status + " and  p_purchaser='" + userId + "'";
            Integer count = jdbcTemplate.queryForObject(sql, Integer.class);
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("status", statusMap.get(status));
            dataMap.put("count", count);
            data.add(dataMap);
        });
        map.put("data", data);
        return map;
    }


    /**
     * 采购员- 未到货统计统计
     */
    public Map<String, Object> caigouyuan2(String userId) {
        Map<String, Object> map = new HashMap<>();

        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "status");
        schema1.put("title", "状态");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "count");
        schema2.put("title", "数量");
        schema2.put("type", "measure");
        schemas.add(schema1);
        schemas.add(schema2);
        map.put("schemas", schemas);
        List<Map<String, Object>> data = new ArrayList<>();
        //到货(订单数量)
       /* String sql1 = "SELECT COUNT(1) FROM po_order_b_copy1 orders \n" +
                "left join po_praybill_b_copy1 bill on bill.CPRAYBILL_BID = orders.cupsourcebillrowid \n" +
                "WHERE orders.DR = 0\n" +
                "\tand bill.p_purchaser='" + userId + "'" +
                "AND bill.DR = 0 \n" +
                "AND bill.p_DPRAYDATE>='2018-01-01'";*/
        String sql1 = "SELECT\n" +
                "COUNT(1) FROM \n" +
                " po_praybill_b_copy1 bill \n" +
                "WHERE \n" +
                " bill.c_total_purchase_order_num >= bill.NPRAYNUM\n" +
                "\tand bill.p_purchaser='" + userId + "'" +
                "AND bill.DR = 0 \n" +
                "AND bill.p_DPRAYDATE>='2018-01-01'";

        int count1 = jdbcTemplate.queryForObject(sql1, Integer.class);
        Map<String, Object> data1 = new HashMap<>();
        data1.put("status", "到货");
        data1.put("count", count1);
        data.add(data1);

        //完全未到货
        String sql2 = "SELECT\n" +
                "\tcount( 1 ) \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill\n" +
                "\tLEFT JOIN po_order_b_copy1 orders ON orders.cupsourcebillrowid = bill.CPRAYBILL_BID \n" +
                "WHERE\n" +
                "\tbill.DR = 0 \n" +
                "\tAND orders.DR = 0 \n" +
                "\tand bill.p_purchaser='" + userId + "'" +

                "\tAND orders.cupsourcebillrowid IS NULL \n" +
                " AND (STR_TO_DATE( LEFT(bill.p_DPRAYDATE, 10), '%Y-%m-%d' ) BETWEEN DATE_SUB(NOW(),INTERVAL 180 DAY) and DATE_ADD(NOW(),INTERVAL 30 DAY) )\n";
        int count2 = jdbcTemplate.queryForObject(sql2, Integer.class);
        Map<String, Object> data2 = new HashMap<>();
        data2.put("status", "完全未到货");
        data2.put("count", count2);
        data.add(data2);

        //部分未到货
        String sql3 = "SELECT\n" +
                "\tcount( 1 ) \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill\n" +
                "\n" +
                "WHERE\n" +
                "\tbill.DR = 0 \n" +
                "\tand bill.p_purchaser='" + userId + "'" +
                "\tand p_DPRAYDATE>='2018-01-01' " +
                "\tAND bill.c_total_purchase_order_num < bill.npraynum\n";

        int count3 = jdbcTemplate.queryForObject(sql3, Integer.class);
        Map<String, Object> data3 = new HashMap<>();
        data3.put("status", "部分未到货");
        data3.put("count", count3);
        data.add(data3);

        map.put("data", data);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 采购员- 逾期趋势按月份统计
     */
    public Map<String, Object> caigouyuan3(String userId) {
        Map<String, Object> map = new HashMap<>();

        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "month");
        schema1.put("title", "月份");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "count");
        schema2.put("title", "数量");
        schema2.put("type", "measure");
        schemas.add(schema1);
        schemas.add(schema2);
        map.put("schemas", schemas);

        List<Map<String, Object>> data = new ArrayList<>();
        //逾期趋势

        String sql = "SELECT\n" +
                "\tc_month as month ,\n" +
                "\tCOUNT( 1 ) as count \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill \n" +
                "WHERE\n" +
                "\t( bill.c_total_purchase_order_num IS NULL OR bill.c_total_purchase_order_num < bill.NPRAYNUM ) \n" +
                "\tAND (\n" +
                "\t\tbill.vdef15 IS NOT NULL AND bill.dr = 0 \n" +
                "\t\t\tAND bill.vdef15 < DATE_FORMAT( NOW( ), '%Y-%m-%d' ) \n" +
                "\t\tOR ( bill.vdef15 IS NULL AND bill.dsuggestdate IS NOT NULL AND bill.dsuggestdate < DATE_FORMAT( NOW( ), '%Y-%m-%d' ) ) \n" +
                "\t\tOR ( DATE_ADD( STR_TO_DATE( LEFT ( bill.p_receive_time, 10 ), '%Y-%m-%d' ), INTERVAL 70 DAY ) < NOW( ) ) \n" +
                "\t)  " +
                "\tand bill.p_purchaser='" + userId + "'" +
                "\tand p_DPRAYDATE>='2018-01-01' " +
                "\tGROUP BY c_month";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        data.addAll(list);
        map.put("data", data);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 采购员之未到货看板之逾期未到货明细
     *
     * @param userId
     **/
    public Map<String, Object> cgy1(String userId) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "DPRAYDATE");
        schema2.put("title", "请购日期");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "receiveTime");
        schema3.put("title", "请购单接受时间");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "vdef15");
        schema4.put("title", "供应商返回的交期");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "invcode");
        schema5.put("title", "存货编码");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invname");
        schema6.put("title", "存货名称");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invspec");
        schema7.put("title", "规格");
        schema7.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);

        map.put("schemas", schemas);
        //逾期未到货统计明细
        String sql = "SELECT\n" +
                "\n" +
                "\tpb.p_pray_bill_num as prayBillNum,\n" +
                "\tpb.p_DPRAYDATE as DPRAYDATE,\n" +
                "\tpb.p_receive_time as receiveTime,\n" +
                "\tpb.vdef15 as vdef15,\n" +
                "\tbd.invcode as invcode,\n" +
                "\tbd.invname as invname,\n" +
                "\tbd.invspec as invspec\n" +
                "\t\n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 pb\n" +
                "\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\tAND pob.dr = 0\n" +
                "\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "WHERE\n" +
                "\tpb.dr = 0   AND  bd.dr = 0 \n" +
                "\tAND pb.p_receive_time IS NOT NULL \n" +
                "\tAND pob.cupsourcebillrowid IS NULL \n" +
                "\tAND pb.vdef15 IS NOT NULL " +
                "AND pb.vdef15 < DATE_FORMAT( NOW(), '%Y-%m-%d' ) \n" +
                "\tand pb.p_purchaser='" + userId + "'" +
                "\tAND pb.p_DPRAYDATE>='2018-01-01'\n" +
                "union\n" +
                "\t\n" +
                "\tSELECT\n" +
                "\t\t\n" +
                "\tpb.p_pray_bill_num  as prayBillNum,\n" +
                "\tpb.p_DPRAYDATE  as DPRAYDATE,\n" +
                "\tpb.p_receive_time  as receiveTime,\n" +
                "\tpb.vdef15  as vdef15,\n" +
                "\tbd.invcode  as invcode,\n" +
                "\tbd.invname  as invname,\n" +
                "\tbd.invspec   as invspec\n" +
                "\t\t\n" +
                "\tFROM\n" +
                "\t\tpo_praybill_b_copy1 pb\n" +
                "\t\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\t\tAND pob.dr = 0\n" +
                "\t\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "\tWHERE\n" +
                "\t\tpb.dr = 0 AND  bd.dr = 0 \n" +
                "\t\tAND pb.p_receive_time IS NOT NULL \n" +
                "\t\tAND pob.cupsourcebillrowid IS NULL \n" +
                "\t\tAND pb.vdef15 IS NULL AND pb.dsuggestdate IS NOT NULL " +
                "AND pb.dsuggestdate < DATE_FORMAT( NOW(), '%Y-%m-%d' ) \n" +
                "\tand pb.p_purchaser='" + userId + "'" +
                "\t\tAND pb.p_DPRAYDATE>='2018-01-01'\n" +
                "union \t\n" +
                "\n" +
                "\tSELECT\n" +
                "\t\t\n" +
                "\tpb.p_pray_bill_num  as prayBillNum,\n" +
                "\tpb.p_DPRAYDATE  as DPRAYDATE,\n" +
                "\tpb.p_receive_time  as receiveTime,\n" +
                "\tpb.vdef15  as vdef15,\n" +
                "\tbd.invcode  as invcode,\n" +
                "\tbd.invname  as invname,\n" +
                "\tbd.invspec   as invspec\n" +
                "\t\t\n" +
                "\tFROM\n" +
                "\t\tpo_praybill_b_copy1 pb\n" +
                "\t\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\t\tAND pob.dr = 0\n" +
                "\t\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "\tWHERE\n" +
                "\t\tpb.dr = 0 AND  bd.dr = 0 \n" +
                "\t\tAND pb.p_receive_time IS NOT NULL \n" +
                "\t\tAND pob.cupsourcebillrowid IS NULL \n" +
                "\tAND\tpb.vdef15 IS NULL AND pb.dsuggestdate IS NULL \n" +
                "\tAND DATE_ADD( STR_TO_DATE( LEFT(pb.p_receive_time, 10), '%Y-%m-%d' ), INTERVAL 70 DAY ) < NOW()\t\n" +
                "\tand pb.p_purchaser='" + userId + "'" +
                "\tAND pb.p_DPRAYDATE>='2018-01-01'";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            //    BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            //    data.put("amount", amount.longValue());
            //    BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            //   data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;

    }

    /**
     * 采购员之未到货看板之部分到货明细
     *
     * @param userId
     **/
    public Map<String, Object> cgy2(String userId) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();

        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "DPRAYDATE");
        schema2.put("title", "请购日期");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "receiveTime");
        schema3.put("title", "请购单接受时间");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "vdef15");
        schema4.put("title", "供应商返回的交期");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "orderCode");
        schema5.put("title", "订单号");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invcode");
        schema6.put("title", "存货编码");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invname");
        schema7.put("title", "存货名称");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "invspec");
        schema8.put("title", "规格");
        schema8.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);
        map.put("schemas", schemas);
        //部分到货明细
        String sql = "SELECT\n" +
                "\tbill.p_pray_bill_num  as prayBillNum,\n" +
                "\tbill.p_DPRAYDATE  as DPRAYDATE,\n" +
                "\tbill.p_receive_time  as receiveTime,\n" +
                "\tbill.vdef15  as vdef15,\n" +
                "\tpob.p_order_code as orderCode,\n" +
                "\tbd.invcode  as invcode,\n" +
                "\tbd.invname  as invname,\n" +
                "\tbd.invspec   as invspec\n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill \n" +
                "\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = bill.cpraybill_bid \n" +
                "\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = bill.cbaseid \n" +
                "WHERE\n" +
                "\tbill.c_total_purchase_order_num IS NOT NULL \n" +
                "\tAND bill.c_total_purchase_order_num > 0 \n" +
                "\tAND bill.c_total_purchase_order_num < bill.NPRAYNUM \n" +
                "\tAND bill.DR = 0\n" +
                "\tAND pob.DR=0\n" +
                "\tAND bd.DR=0\n" +
                "\tand bill.p_purchaser='" + userId + "'" +
                "\tAND bill.p_DPRAYDATE>='2018-01-01'";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            //   BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            //   data.put("amount", amount.longValue());
            //   BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            //   data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;

    }

    /**
     * 采购员之未到货看板之临近到货明细
     *
     * @param userId
     **/
    public Map<String, Object> cgy3(String userId) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "DPRAYDATE");
        schema2.put("title", "请购日期");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "receiveTime");
        schema3.put("title", "请购单接受时间");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "vdef15");
        schema4.put("title", "供应商返回的交期");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "invcode");
        schema5.put("title", "存货编码");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invname");
        schema6.put("title", "存货名称");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invspec");
        schema7.put("title", "规格");
        schema7.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);

        map.put("schemas", schemas);
        //临近到货明细
        String sql = "SELECT\n" +
                "\tpb.p_pray_bill_num  as prayBillNum,\n" +
                "\tpb.p_DPRAYDATE  as DPRAYDATE,\n" +
                "\tpb.p_receive_time  as receiveTime,\n" +
                "\tpb.vdef15  as vdef15,\n" +
                "\tbd.invcode  as invcode,\n" +
                "\tbd.invname  as invname,\n" +
                "\tbd.invspec   as invspec\n" +
                "\tFROM\n" +
                "\t\tpo_praybill_b_copy1 pb\n" +
                "\t\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\t\tAND pob.dr = 0\n" +
                "\t\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "\tWHERE\n" +
                "\t\tpb.dr = 0 and  bd.dr = 0 \n" +
                "\t\tAND pb.p_receive_time IS NOT NULL \n" +
                "\t\tAND pob.cupsourcebillrowid IS NULL \n" +
                "\t\tAND pb.vdef15 IS NOT NULL \n" +
                "\t\tAND pb.vdef15 >= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.vdef15 <= DATE_FORMAT( DATE_ADD( NOW( ), INTERVAL 1 MONTH ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.p_purchaser='" + userId + "'" +
                "\t\tAND pb.p_DPRAYDATE>='2018-01-01'\n" +
                "UNION\n" +
                "\tSELECT\n" +
                "\tpb.p_pray_bill_num  as prayBillNum,\n" +
                "\tpb.p_DPRAYDATE  as DPRAYDATE,\n" +
                "\tpb.p_receive_time  as receiveTime,\n" +
                "\tpb.vdef15  as vdef15,\n" +
                "\tbd.invcode  as invcode,\n" +
                "\tbd.invname  as invname,\n" +
                "\tbd.invspec   as invspec\n" +
                "\t\t\n" +
                "\tFROM\n" +
                "\t\tpo_praybill_b_copy1 pb\n" +
                "\t\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\t\tAND pob.dr = 0 \n" +
                "\t\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "\tWHERE\n" +
                "\t\tpb.dr = 0 AND bd.dr = 0  \n" +
                "\t\tAND pb.p_receive_time IS NOT NULL \n" +
                "\t\tAND pob.cupsourcebillrowid IS NULL \n" +
                "\t\tAND pb.vdef15 IS NULL \n" +
                "\t\tAND pb.dsuggestdate IS NOT NULL \n" +
                "\t\tAND pb.vdef15 >= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.vdef15 <= DATE_FORMAT( DATE_ADD( NOW( ), INTERVAL 1 MONTH ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.dsuggestdate IS NOT NULL \n" +
                "\t\tAND pb.dsuggestdate >= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.dsuggestdate <= DATE_FORMAT( DATE_ADD( NOW( ), INTERVAL 1 MONTH ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.p_purchaser='" + userId + "'" +
                "\t\tAND pb.p_DPRAYDATE>='2018-01-01'\n" +
                "UNION\n" +
                "\tSELECT\n" +
                "\tpb.p_pray_bill_num  as prayBillNum,\n" +
                "\tpb.p_DPRAYDATE  as DPRAYDATE,\n" +
                "\tpb.p_receive_time  as receiveTime,\n" +
                "\tpb.vdef15  as vdef15,\n" +
                "\tbd.invcode  as invcode,\n" +
                "\tbd.invname  as invname,\n" +
                "\tbd.invspec   as invspec\n" +
                "\tFROM\n" +
                "\t\tpo_praybill_b_copy1 pb\n" +
                "\t\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\t\tAND pob.dr = 0\n" +
                "\t\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "\tWHERE\n" +
                "\t\tpb.dr = 0 AND bd.dr = 0  \n" +
                "\t\tAND pb.p_receive_time IS NOT NULL \n" +
                "\t\tAND pob.cupsourcebillrowid IS NULL \n" +
                "\t\tAND pb.vdef15 IS NOT NULL \n" +
                "\t\tAND pb.vdef15 >= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.vdef15 <= DATE_FORMAT( DATE_ADD( NOW( ), INTERVAL 1 MONTH ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.vdef15 IS NULL \n" +
                "\t\tAND pb.dsuggestdate IS NULL \n" +
                "\t\tAND DATE_ADD( STR_TO_DATE( pb.p_receive_time, '%Y-%m-%d %H' ), INTERVAL 70 DAY ) >= NOW( ) \n" +
                "\t\tAND DATE_ADD( STR_TO_DATE( pb.p_receive_time, '%Y-%m-%d %H' ), INTERVAL 40 DAY ) <= NOW( ) \n" +
                "\t\tAND pb.p_purchaser='" + userId + "'" +
                "\t\tAND pb.p_DPRAYDATE>='2018-01-01'";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            //   BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            //    data.put("amount", amount.longValue());
            //    BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            //    data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }

    /**
     * 采购员之未到货看板之采购过程中明细
     *
     * @param userId
     **/
    public Map<String, Object> cgy4(String userId) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "DPRAYDATE");
        schema2.put("title", "请购日期");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "receiveTime");
        schema3.put("title", "请购单接受时间");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "vdef15");
        schema4.put("title", "供应商返回的交期");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "invcode");
        schema5.put("title", "存货编码");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invname");
        schema6.put("title", "存货名称");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invspec");
        schema7.put("title", "规格");
        schema7.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);


        map.put("schemas", schemas);
        //采购过程中明细
        String sql = "SELECT\n" +
                "pb.p_pray_bill_num  as prayBillNum,\n" +
                "pb.p_DPRAYDATE  as DPRAYDATE,\n" +
                "pb.p_receive_time  as receiveTime,\n" +
                "pb.vdef15  as vdef15,\n" +
                "bd.invcode  as invcode,\n" +
                "bd.invname  as invname,\n" +
                "bd.invspec   as invspec\n" +
                "FROM\n" +
                "po_praybill_b_copy1 pb\n" +
                "LEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "LEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "AND pob.dr = 0\n" +
                "WHERE\n" +
                "pb.p_receive_time IS NOT NULL AND bd.dr = 0 \n" +
                "AND pob.cupsourcebillrowid IS NULL\n" +
                "AND pb.c_currentstatus= 3\n" +
                "AND pb.dr = 0\n" +
                "\tand pb.p_purchaser='" + userId + "'" +
                "AND pb.p_DPRAYDATE>='2018-01-01'";


        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            //    BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            //    data.put("amount", amount.longValue());
            //    BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            //   data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;

    }

    /**
     * 采购员之未到货看板之完全到货明细
     *
     * @param userId
     **/
    public Map<String, Object> cgy5(String userId) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "DPRAYDATE");
        schema2.put("title", "请购日期");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "receiveTime");
        schema3.put("title", "请购单接受时间");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "vdef15");
        schema4.put("title", "供应商返回的交期");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "orderCode");
        schema5.put("title", "订单号");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invcode");
        schema6.put("title", "存货编码");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invname");
        schema7.put("title", "存货名称");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "invspec");
        schema8.put("title", "规格");
        schema8.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);

        map.put("schemas", schemas);
        //完全到货明细
        String sql = "SELECT\n" +
                "\tbill.p_pray_bill_num  as prayBillNum,\n" +
                "\tbill.p_DPRAYDATE  as DPRAYDATE,\n" +
                "\tbill.p_receive_time  as receiveTime,\n" +
                "\tbill.vdef15  as vdef15,\n" +
                "\tpob.p_order_code as orderCode,\n" +
                "\tbd.invcode  as invcode,\n" +
                "\tbd.invname  as invname,\n" +
                "\tbd.invspec   as invspec\n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill \n" +
                "\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = bill.cpraybill_bid\n" +
                "\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = bill.cbaseid \n" +
                "WHERE\n" +
                "\tbill.c_total_purchase_order_num IS NOT NULL AND  pob.dr = 0  AND  bd.dr = 0 \n" +
                "\t AND bill.c_total_purchase_order_num > 0 \n" +
                "\tAND bill.c_total_purchase_order_num >= bill.NPRAYNUM \n" +
                "\tAND bill.DR = 0\n" +
                "\tand bill.p_purchaser='" + userId + "'" +
                "\tAND bill.p_DPRAYDATE>'2018-01-01'\n";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            //    BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            //   data.put("amount", amount.longValue());
            //   BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            //   data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;

    }

    /**
     * 采购员之未到货看板之订单未接收明细
     *
     * @param userId
     **/
    public Map<String, Object> cgy6(String userId) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "vdef20");
        schema1.put("title", "采购计划号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "prayBillNum");
        schema2.put("title", "请购单号");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "orderTime");
        schema3.put("title", "请购单制单时间");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "invcode");
        schema4.put("title", "存货编码");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "invname");
        schema5.put("title", "存货名称");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invspec");
        schema6.put("title", "规格");
        schema6.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);


        map.put("schemas", schemas);
        //订单未接收明细
        String sql = "\tSELECT\n" +
                "\tsb.vdef20 as vdef20,\n" +
                "\tpb.p_pray_bill_num as prayBillNum,\n" +
                "\tpb.p_order_time  as orderTime,\n" +
                "\tbi.invcode  as invcode,\n" +
                "\tbi.invname  as invname,\n" +
                "\tbi.invspec  as  invspec\n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 pb\n" +
                "\tLEFT JOIN shht_stock_b sb ON pb.csourcebillrowid = sb.pk_stock_b\n" +
                "\tLEFT JOIN bd_invbasdoc bi ON bi.pk_invbasdoc = sb.pk_invbasdoc \n" +
                "WHERE\n" +
                "\tpb.p_receive_time IS NULL and bi.dr = 0  \n" +
                "\tAND sb.dr = 0 \n" +
                "\tAND pb.dr = 0\n" +
                "\tand pb.p_purchaser='" + userId + "'" +
                "\tAND pb.p_order_time>='2018-01-01'";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            //   BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            //   data.put("amount", amount.longValue());
            //   BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            //   data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }

    /**
     * 采购员之未到货看板之其他明细
     **/
    public Map<String, Object> cgy7(String purchaser) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "vdef20");
        schema1.put("title", "采购计划号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "prayBillNum");
        schema2.put("title", "请购单号");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "orderTime");
        schema3.put("title", "请购单制单时间");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "DPRAYDATE");
        schema4.put("title", "请购日期");
        schema4.put("type", "measure");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "receiveTime");
        schema5.put("title", "请购单接受时间");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "vdef15");
        schema6.put("title", "供应商返回的交期");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "orderCode");
        schema7.put("title", "订单号");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "invcode");
        schema8.put("title", "存货编码");
        schema8.put("type", "dimension");
        Map<String, String> schema9 = new HashMap<>();
        schema9.put("field", "invname");
        schema9.put("title", "存货名称");
        schema9.put("type", "dimension");
        Map<String, String> schema10 = new HashMap<>();
        schema10.put("field", "invspec");
        schema10.put("title", "规格");
        schema10.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);


        map.put("schemas", schemas);
        //List<Map<String, Object>> data = new ArrayList<>();
        //订单未接收明细
        String sql = "SELECT\n" +
                "\t\tsb.vdef20  as vdef20,\n" +
                "\t pb.p_pray_bill_num  as prayBillNum ,\n" +
                "\t pb.p_order_time  as orderTime,-- 1\n" +
                "\tpb.p_DPRAYDATE  as DPRAYDATE,-- 1\n" +
                "\tpb.p_receive_time  as receiveTime,-- 1\n" +
                "\tpb.vdef15  as vdef15,-- 1\n" +
                "\tpob.p_order_code  as orderCode,\n" +
                "\t bi.invcode  as invcode,\n" +
                "\t bi.invname  as invname,\n" +
                "\t bi.invspec  as  invspec\n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 pb\n" +
                "\t LEFT JOIN shht_stock_b sb ON pb.csourcebillrowid = sb.pk_stock_b\n" +
                "\t LEFT JOIN bd_invbasdoc bi ON bi.pk_invbasdoc = sb.pk_invbasdoc \n" +
                "\t LEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid\n" +
                "WHERE\n" +
                "\t  sb.dr = 0  AND  pb.dr = 0 AND  bi.dr = 0 AND  pob.dr = 0  \n" +
                "\t AND c_currentstatus='0'\n" +
                "\tand pb.p_purchaser= " + purchaser +
                "\t AND pb.p_DPRAYDATE>='2018-01-01'";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            //    BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            //    data.put("amount", amount.longValue());
            //   BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            //   data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }


/***************采购员领导************/


    /**
     * 采购员领导 - 按照 领域,订单状态 统计
     */
    public Map<String, Object> cgbld1() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "region");
        schema1.put("title", "领域");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "count");
        schema2.put("title", "数量");
        schema2.put("type", "measure");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "status");
        schema3.put("title", "状态");
        schema3.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);

        map.put("schemas", schemas);

        List<Map<String, Object>> data = new ArrayList<>();
        //按照状态统计
        String sql = "SELECT\n" +
                "CASE\n" +
                "c_region WHEN 1 THEN '运载'\n" +
                "         WHEN 2 THEN '战术武器' \n" +
                "\t\t\t\t WHEN 3 THEN '卫星' \n" +
                "\t\t\t\t WHEN 4 THEN '空间科学' \n" +
                "\t\t\t\t WHEN 5 THEN '其他'\n" +
                "\t\t\t\t ELSE '其他' \n" +
                "\t\t\t\t END AS region,\n" +
                "\t\t\t\t CASE c_currentstatus \n" +
                "\t\t\t\t WHEN 1 THEN '订单未接收' \n" +
                "\t\t\t\t WHEN 2 THEN '完全到货' \n" +
                "\t\t\t\t WHEN 3 THEN '采购过程中' \n" +
                "\t\t\t\t WHEN 4 THEN '临近到货' \n" +
                "\t\t\t\t WHEN 5 THEN '部分到货' \n" +
                "\t\t\t\t WHEN 6 THEN '逾期未到货' \n" +
                "\t\t\t\t ELSE '其他' END AS status,\n" +
                "\t\t\t\t COUNT( 1 ) AS count \n" +
                "\t\t\t\t FROM po_praybill_b_copy1 b \n" +
                "\t\t\t\t WHERE b.p_DPRAYDATE > '2018-01-01' AND b.dr = 0\n" +
                "\t\t\t\t GROUP BY\n" +
                "\t\t\t\t c_region,\n" +
                "\t\t\t\t c_currentstatus";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        data.addAll(list);
        map.put("data", data);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 采购员领导 - 采购统计
     */
    public Map<String, Object> cgbld2() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        //采购员
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "userName");
        schema1.put("title", "采购员");
        schema1.put("type", "dimension");

        //按时完成 百分比
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "percent1");
        schema2.put("title", "完成百分比");
        schema2.put("type", "measure");

        //逾期完成 百分比
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "percent2");
        schema3.put("title", "逾期完成百分比");
        schema3.put("type", "measure");

        //逾期未完成 百分比
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "percent3");
        schema4.put("title", "逾期未完成百分比");
        schema4.put("type", "measure");


        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        map.put("schemas", schemas);

        List<Map<String, Object>> dataList = new ArrayList<>();

        // 订单总数 统计结果
        String sql1 = "SELECT\n" +
                "po_praybill.COPERATOR AS cuserid,\n" +
                "u.USER_NAME AS userName,\n" +
                "count( 1 ) AS count \n" +
                "FROM\n" +
                "po_praybill_b_copy1 bill\n" +
                "LEFT JOIN po_praybill ON po_praybill.CPRAYBILLID = bill.CPRAYBILLID\n" +
                "LEFT JOIN sm_user u ON bill.p_purchaser = u.CUSERID \n" +
                "WHERE\n" +
                "po_praybill.COPERATOR IS NOT NULL  AND  bill.dr = 0 and  po_praybill.dr = 0 and u.dr = 0 \n" +
                "AND u.USER_NAME='陆洋' OR u.USER_NAME='夏昉'  OR u.USER_NAME='宋菲'  OR u.USER_NAME='徐夏铭'  OR u.USER_NAME='赵俊'  OR u.USER_NAME='廉政雍'  OR u.USER_NAME='葛骏'  OR u.USER_NAME='周韵'  OR u.USER_NAME='刘斐斐'  OR u.USER_NAME='张旻珺' OR u.USER_NAME='穆海玥'   OR u.USER_NAME='孙晓毅'   OR u.USER_NAME='孙春娜'  OR u.USER_NAME='杨勇' OR u.USER_NAME='郑菁' OR u.USER_NAME='周立奇' OR u.USER_NAME='高天为'\n" +
                "AND u.USER_NAME IS NOT NULL\n" +
                "AND u.USER_NAME !=('answer') \n" +
                "GROUP BY\n" +
                "po_praybill.COPERATOR;";
        List<Map<String, Object>> list1 = jdbcTemplate.queryForList(sql1);


        // 按时完成 统计结果
        String sql2 = "SELECT\n" +
                "\tpo_praybill.COPERATOR AS cuserid,\n" +
                "\tu.USER_NAME AS userName,\n" +
                "\tcount( 1 ) AS count \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill\n" +
                "\tLEFT JOIN po_praybill ON po_praybill.CPRAYBILLID = bill.CPRAYBILLID\n" +
                "\tLEFT JOIN sm_user u ON bill.p_purchaser = u.CUSERID \n" +
                "WHERE\n" +
                "\tbill.c_currentstatus = 2 and bill.dr = 0 and  po_praybill.dr = 0 and u.dr = 0 \n" +
                "\tAND u.USER_NAME IS NOT NULL\n" +
                "\tAND u.USER_NAME !=('answer') \n" +
                "GROUP BY\n" +
                "\tpo_praybill.COPERATOR;";
        List<Map<String, Object>> list2 = jdbcTemplate.queryForList(sql2);

        // 逾期未完成 统计结果
        String sql3 = "SELECT\n" +
                "    pray_bill.COPERATOR as cuserid,\n" +
                "    u.USER_NAME AS userName,\n" +
                "    count( 1 )  as count\n" +
                "FROM\n" +
                "    po_praybill_b_copy1 bill\n" +
                "    LEFT JOIN po_praybill pray_bill ON pray_bill.CPRAYBILLID = bill.CPRAYBILLID \n" +
                "    LEFT JOIN sm_user u ON bill.p_purchaser = u.CUSERID\n" +
                "WHERE\n" +
                "    ( bill.c_total_purchase_order_num IS NULL \n" +
                "        OR \n" +
                "    bill.c_total_purchase_order_num < bill.NPRAYNUM )\n" +
                "    AND (\n" +
                "    bill.vdef15 IS NOT NULL AND bill.vdef15 < DATE_FORMAT(NOW(), '%Y-%m-%d') \n" +
                "    OR (bill.vdef15 IS NULL AND bill.dsuggestdate IS NOT NULL AND bill.dsuggestdate < DATE_FORMAT(NOW(), '%Y-%m-%d'))\n" +
                "    OR (DATE_ADD( STR_TO_DATE( LEFT(bill.p_receive_time, 10), '%Y-%m-%d' ), INTERVAL 70 DAY ) < NOW())\n" +
                "    ) and pray_bill.COPERATOR is not  null\n" +
                "    \n" +
                "\tAND u.USER_NAME IS NOT NULL and bill.dr = 0 and pray_bill.dr = 0 and u.dr = 0 \n" +
                "\tAND u.USER_NAME !=('answer') \n" +
                "GROUP BY\n" +
                "    pray_bill.COPERATOR;";
        List<Map<String, Object>> list3 = jdbcTemplate.queryForList(sql3);

        list1.stream().forEach(data -> {

            Map<String, Object> dataMap = new HashMap<>();
            String cuserid = (String) data.getOrDefault("cuserid", "");
            String userName = (String) data.getOrDefault("userName", "");
            dataMap.put("userName", userName);
            // 订单总数
            Long totalCount = (Long) data.getOrDefault("count", 0L);
            //  按时完成 数量
            Optional<Map<String, Object>> optional2 = list2.stream().filter(data2 -> StringUtils.equals(cuserid, (String) data2.getOrDefault("cuserid", ""))).findFirst();
            Long count2 = 0L;
            if (optional2.isPresent()) {
                Map<String, Object> map2 = optional2.get();
                count2 = (Long) map2.getOrDefault("count", 0L);
            }

            //  逾期未完成 数量
            Optional<Map<String, Object>> optional3 = list3.stream().filter(data3 -> StringUtils.equals(cuserid, (String) data3.getOrDefault("cuserid", ""))).findFirst();
            Long count3 = 0L;
            if (optional3.isPresent()) {
                Map<String, Object> map3 = optional3.get();
                count3 = (Long) map3.getOrDefault("count", 0L);
            }

            // 逾期完成 数量  =  订单总数 - 按时完成单数- 逾期未完成
            Long count4 = totalCount - count2 - count3;

            //按时完成率
            String percent1 = NumberUtils.format2(count2 * 100.0 / totalCount) + "%";
            //逾期未完成率
            String percent2 = NumberUtils.format2(count3 * 100.0 / totalCount) + "%";
            //逾期完成率
            //double percent3 = (count4 * 100 / totalCount);
            String percent3 = NumberUtils.format2(count4 * 100.0 / totalCount) + "%";

            dataMap.put("percent1", percent1);
            //dataMap.put("percent2", NumberUtils.format2(percent2));
            dataMap.put("percent2", percent2);
            //dataMap.put("percent3", NumberUtils.format2(percent3));
            dataMap.put("percent3", percent3);
            dataList.add(dataMap);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 采购员领导 - 检验统计 目前只支持查询 已检 数量
     */
    public Map<String, Object> cgbld3() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "userName");
        schema1.put("title", "检验员");
        schema1.put("type", "dimension");

        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "count1");
        schema2.put("title", "已检");
        schema2.put("type", "measure");

        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "count2");
        schema3.put("title", "未检");
        schema3.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        map.put("schemas", schemas);

        // 检验统计
        String sql = "SELECT\n" +
                "users.USER_NAME  as userName,\n" +
                "count( 1 ) as count1\n" +
                "FROM\n" +
                "( SELECT * FROM po_arriveorder_b p WHERE checker IS NOT NULL AND cprojectid = 'Y' and p.dr = 0) AS arrive_order\n" +
                "LEFT JOIN sm_user users ON users.CUSERID = arrive_order.checker \n" +
                "where  users.CUSERID  is not null  and  users.dr = 0   \n" +
                "AND users.USER_NAME='丁辰' OR  users.USER_NAME='唐亭'  OR users.USER_NAME='樊孝忠'   OR users.USER_NAME='曹艺晴'   \n" +
                "OR users.USER_NAME='王斌' OR users.USER_NAME='葛骏'  OR users.USER_NAME='孙彦军'\n" +
                "GROUP BY   \n" +
                "USER_NAME";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            data.put("count2", 0);
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 采购员领导 - 厂家合同top20
     */
    public Map<String, Object> cgbld4() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "custname");
        schema1.put("title", "厂家名称");
        schema1.put("type", "dimension");

        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "amount");
        schema2.put("title", "金额(万元)");
        schema2.put("type", "measure");

        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "orderQuantity");
        schema3.put("title", "器件数量");
        schema3.put("type", "measure");

        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "orderNum");
        schema4.put("title", "订单数量");
        schema4.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        map.put("schemas", schemas);

        // 厂家合同top20
        String sql = "SELECT\n" +
                "\tcus.custname AS custname,\n" +
                "\tsum( IFNULL( price.costprice, IFNULL( price.planprice, 0 ) ) * bill.npraynum ) AS amount,\n" +
                "\tsum(bill.npraynum) AS orderQuantity ,-- 零件数量???\n" +
                "\tcount(bill.CPRAYBILL_BID) AS orderNum\n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill\n" +
                "\tLEFT JOIN bd_cubasdoc cus ON cus.pk_cubasdoc = bill.cvendorbaseid\n" +
                "\tLEFT JOIN bd_invmandoc_copy1 price ON price.pk_invbasdoc = bill.cbaseid \n" +
                "WHERE\n" +
                "\tcus.custname IS NOT NULL and bill.dr = 0 and cus.dr = 0 and price.dr = 0 \n" +
                "\t AND bill.p_order_time>='2018-01-01'\n" +
                "GROUP BY\n" +
                "\tbill.cvendorbaseid\n" +
                "ORDER BY\n" +
                "\tamount DESC\n" +
                "\tLIMIT 20";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            long amount1 = amount.longValue();
            String amount2 = NumberUtils.format2(amount1 / 10000.0);
            data.put("amount", amount2);

           /* amount=amount.divide(new BigDecimal(10000.0),2,BigDecimal.ROUND_HALF_UP);
            data.put("amount", amount.longValue());*/

           /* BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            -- BigDecimal amount1=new BigDecimal("10000.0");
           --  amount=(amount.divide(amount1)).setScale(2, RoundingMode.HALF_UP);
            data.put("amount", amount.longValue());*/

           /* Long amount1 = (Long) data.getOrDefault("amount", 0L);
            String amount =  NumberUtils.format2(amount1 / 10000.0 )+"万元";
            data.put("amount", amount);*/

            BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 采购员领导 - 元器件规格top20
     */
    public Map<String, Object> cgbld5() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prodmodelCode");
        schema1.put("title", "规格");
        schema1.put("type", "dimension");

        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "amount");
        schema2.put("title", "金额(万元)");
        schema2.put("type", "measure");

        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "orderQuantity");
        schema3.put("title", "器件数量");
        schema3.put("type", "measure");

        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "custname");
        schema4.put("title", "生产厂商");
        schema4.put("type", "measure");

        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "zldj");
        schema5.put("title", "质量等级");
        schema5.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        map.put("schemas", schemas);

        // 元器件规格top20
        String sql = "SELECT bd.invspec as  prodmodelCode,\n" +
                "sum( IFNULL( price.costprice, IFNULL( price.planprice, 0 ) ) * bill.npraynum ) AS amount,\n" +
                "sum(bill.npraynum) AS orderQuantity,\n" +
                "cus.CUSTSHORTNAME AS custname,\n" +
                "szb.ZLDJ AS zldj\n" +
                "FROM po_praybill_b_copy1 bill\n" +
                "LEFT JOIN bd_cubasdoc cus ON cus.pk_cubasdoc = bill.cvendorbaseid\n" +
                "LEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = bill.cbaseid \n" +
                "LEFT JOIN shht_zldj_b szb ON szb.PK_ZLDJ_B = bd.DEF4\n" +
                "LEFT JOIN bd_invmandoc_copy1 price ON price.pk_invbasdoc = bill.cbaseid \n" +
                "WHERE bd.invspec IS NOT NULL and bill.dr = 0 and cus.dr = 0 and bd.dr = 0 and szb.dr = 0\n" +
                "\t AND bill.p_order_time>='2018-01-01'\n" +
                "\t AND cus.CUSTSHORTNAME is not  null \t" +
                "GROUP BY bd.invspec\n" +
                "ORDER BY amount DESC LIMIT 20";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            amount = amount.divide(new BigDecimal(10000.0), 2, BigDecimal.ROUND_HALF_UP);
            data.put("amount", amount);
           /* long amount1=amount.longValue();
            String amount2 =  NumberUtils.format2(amount1 / 10000.0 )+"万元";
            data.put("amount", amount2);*/

            //data.put("amount", amount.longValue());
            BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 采购部领导 订单未接收 运载  战术武器  其他 明细 1.运载 2.战术武器  3、4空间平台（3.卫星 4.空间科学） 5.其他
     *
     * @param region
     * @return
     */
    public Map<String, Object> cgbld01(Integer region) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "vdef20");
        schema1.put("title", "采购计划号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "prayBillNum");
        schema3.put("title", "请购单号");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "orderTime");
        schema4.put("title", "请购单制单时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "invcode");
        schema5.put("title", "存货编码");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invname");
        schema6.put("title", "存货名称");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invspec");
        schema7.put("title", "规格");
        schema7.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);

        map.put("schemas", schemas);
        //List<Map<String, Object>> data = new ArrayList<>();
        //订单未接收明细
        String sql = "SELECT\n" +
                "\tsb.vdef20  as vdef20,\n" +
                "\tu.USER_NAME  as userName,\n" +
                "\tpb.p_pray_bill_num  as prayBillNum,\n" +
                "\tpb.p_order_time  as orderTime,\n" +
                "\tbi.invcode  as invcode,\n" +
                "\tbi.invname  as  invname,\n" +
                "\tbi.invspec   as invspec\n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 pb\n" +
                "\tLEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                "\tLEFT JOIN shht_stock_b sb ON pb.csourcebillrowid = sb.pk_stock_b\n" +
                "\tLEFT JOIN bd_invbasdoc bi ON bi.pk_invbasdoc = sb.pk_invbasdoc \n" +
                "WHERE\n" +
                "\tpb.p_receive_time IS NULL  \n" +
                "\tAND sb.dr = 0 \n" +
                "\tAND pb.dr = 0\n" +
                "\tAND pb.p_DPRAYDATE>='2018-01-01'\n";


        String sql1 = null;
        if (region != null) {
            sql1 = sql + "AND c_region= " + region;
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql1);
        map.put("data", list);
        map.put("schemas", schemas);
        return map;
    }

    /**
     * 采购部领导 完全到货 运载  战术武器  其他 明细 1.运载 2.战术武器  3、4空间平台（3.卫星 4.空间科学） 5.其他
     *
     * @return
     */
    public Map<String, Object> cgbld02(Integer region) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "DPRAYDATE");
        schema3.put("title", "请购日期");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "receiveTime");
        schema4.put("title", "请购单接受时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "vdef15");
        schema5.put("title", "供应商返回的交期");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "orderCode");
        schema6.put("title", "订单号");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invcode");
        schema7.put("title", "存货编码");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "invname");
        schema8.put("title", "存货名称");
        schema8.put("type", "dimension");
        Map<String, String> schema9 = new HashMap<>();
        schema9.put("field", "invspec");
        schema9.put("title", "规格");
        schema9.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);
        schemas.add(schema9);

        map.put("schemas", schemas);
        //完全到货明细
        String sql = "SELECT\n" +
                "\tbill.p_pray_bill_num as prayBillNum,\n" +
                "\tu.USER_NAME  as userName ,\n" +
                "\tbill.p_DPRAYDATE  as DPRAYDATE,\n" +
                "\tbill.p_receive_time  as receiveTime,\n" +
                "\tbill.vdef15  as vdef15,\n" +
                "\tpob.p_order_code  as orderCode,\n" +
                "\tbd.invcode  as invcode,\n" +
                "\tbd.invname  as invname,\n" +
                "\tbd.invspec  as invspec\n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill \n" +
                "\t\tLEFT JOIN sm_user u ON bill.p_purchaser = u.CUSERID\n" +
                "\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = bill.cpraybill_bid\n" +
                "\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = bill.cbaseid \n" +
                "WHERE\n" +
                "\tbill.c_total_purchase_order_num IS NOT NULL " +
                "\tAND bill.DR = 0\n" +
                "\tAND bill.p_DPRAYDATE>'2018-01-01'\n" +
                "\tAND c_currentstatus='2'\n";

        String sql1 = null;
        if (region != null) {
            sql1 = sql + "AND c_region= " + region;
        }

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql1);
        map.put("data", list);
        map.put("schemas", schemas);
        return map;
    }

    /**
     * 采购部领导 采购过程中 运载  战术武器  其他 明细 1.运载 2.战术武器  3、4空间平台（3.卫星 4.空间科学） 5.其他
     *
     * @return
     */
    public Map<String, Object> cgbld03(Integer region) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "DPRAYDATE");
        schema3.put("title", "请购日期");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "receiveTime");
        schema4.put("title", "请购单接受时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "vdef15");
        schema5.put("title", "供应商返回的交期");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invcode");
        schema6.put("title", "存货编码");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invname");
        schema7.put("title", "存货名称");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "invspec");
        schema8.put("title", "规格");
        schema8.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);

        map.put("schemas", schemas);
        //List<Map<String, Object>> data = new ArrayList<>();
        //采购过程中明细
        String sql = "SELECT\n" +
                "\tpb.p_pray_bill_num as prayBillNum,\n" +
                "\tu.USER_NAME  as userName,\n" +
                "\tpb.p_DPRAYDATE  as DPRAYDATE,\n" +
                "\tpb.p_receive_time as receiveTime,\n" +
                "\tpb.vdef15 as vdef15 ,\n" +
                "\tbd.invcode as invcode,\n" +
                "\tbd.invname as invname,\n" +
                "\tbd.invspec as invspec \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 pb\n" +
                "\tLEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                "\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\tAND pob.dr = 0\n" +
                "\n" +
                "WHERE\n" +
                "\tpb.p_receive_time IS NOT NULL   \n" +
                "\tAND pob.cupsourcebillrowid IS NULL\n" +
                "\tAND pb.dr = 0\n" +
                "\tAND pb.p_DPRAYDATE>'2018-01-01'\n" +
                "\t\tAND c_currentstatus=3\n";

        if (region != null) {
            sql = sql + "AND pb.c_region= " + region;
        }

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        map.put("data", list);
        map.put("schemas", schemas);
        return map;
    }

    /**
     * 采购部领导 临近到货 运载 战术武器  其他 明细 1.运载 2.战术武器  3、4空间平台（3.卫星 4.空间科学） 5.其他
     *
     * @return
     */
    public Map<String, Object> cgbld04(Integer region) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "DPRAYDATE");
        schema3.put("title", "请购日期");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "receiveTime");
        schema4.put("title", "请购单接受时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "vdef15");
        schema5.put("title", "供应商返回的交期");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invcode");
        schema6.put("title", "存货编码");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invname");
        schema7.put("title", "存货名称");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "invspec");
        schema8.put("title", "规格");
        schema8.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);

        map.put("schemas", schemas);
        //List<Map<String, Object>> data = new ArrayList<>();
        //临近到货明细
        String sql = null;
        if (region != null) {
            sql = "SELECT pb.p_pray_bill_num as prayBillNum,\n" +
                    "            u.USER_NAME as userName,\n" +
                    "                    pb.p_DPRAYDATE as DPRAYDATE,\n" +
                    "            pb.p_receive_time as receiveTime,\n" +
                    "                    pb.vdef15 as vdef15,\n" +
                    "            bd.invcode as invcode,\n" +
                    "                    bd.invname as invname,\n" +
                    "            bd.invspec as invspec\n" +
                    "                    FROM\n" +
                    "            po_praybill_b_copy1 pb\n" +
                    "            LEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                    "            LEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid\n" +
                    "            LEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid\n" +
                    "            where pb.c_currentstatus='4' AND pb.p_DPRAYDATE>='2018-01-01' " +
                    "and pb.dr = 0  " +
                    "AND c_region= " + region;
        }

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

        map.put("data", list);
        map.put("schemas", schemas);
        return map;
    }

    /**
     * 采购部领导 部分到货 运载 战术武器  其他 明细 1.运载 2.战术武器  3、4空间平台（3.卫星 4.空间科学） 5.其他
     *
     * @return
     */
    public Map<String, Object> cgbld05(Integer region) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "DPRAYDATE");
        schema3.put("title", "请购日期");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "receiveTime");
        schema4.put("title", "请购单接受时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "vdef15");
        schema5.put("title", "供应商返回的交期");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "orderCode");
        schema6.put("title", "订单号");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invcode");
        schema7.put("title", "存货编码");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "invname");
        schema8.put("title", "存货名称");
        schema8.put("type", "dimension");
        Map<String, String> schema9 = new HashMap<>();
        schema9.put("field", "invspec");
        schema9.put("title", "规格");
        schema9.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);
        schemas.add(schema9);

        map.put("schemas", schemas);
        //List<Map<String, Object>> data = new ArrayList<>();
        //部分到货明细
        String sql = "SELECT\n" +
                "\tbill.p_pray_bill_num as prayBillNum,\n" +
                "\tu.USER_NAME as userName,\n" +
                "\tbill.p_DPRAYDATE as DPRAYDATE,\n" +
                "\tbill.p_receive_time as receiveTime,\n" +
                "\tbill.vdef15 as vdef15,\n" +
                "\tpob.p_order_code as orderCode,\n" +
                "\tbd.invcode  as invcode,\n" +
                "\tbd.invname as invname,\n" +
                "\tbd.invspec as invspec\n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill \n" +
                "\tLEFT JOIN sm_user u ON bill.p_purchaser = u.CUSERID\n" +
                "\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = bill.cpraybill_bid \n" +
                "\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = bill.cbaseid \n" +
                "WHERE\n" +
                "\tbill.c_total_purchase_order_num IS NOT NULL  " +

                "\tAND bill.DR = 0\n" +
                "\tAND pob.DR=0\n" +
                "\tAND bd.DR=0\n" +
                "\tAND bill.p_DPRAYDATE>='2018-01-01'\n" +
                "\tAND c_currentstatus='5'\n";

        String sql1 = null;
        if (region != null) {
            sql1 = sql + "AND c_region= " + region;
        }

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql1);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            //   BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            //   data.put("amount", amount.longValue());
            //   BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            //   data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }

    /**
     * 采购部领导 逾期未到货 运载 战术武器  其他 明细 1.运载 2.战术武器  3、4空间平台（3.卫星 4.空间科学） 5.其他
     *
     * @return
     */
    public Map<String, Object> cgbld06(Integer region) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "DPRAYDATE");
        schema3.put("title", "请购日期");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "receiveTime");
        schema4.put("title", "请购单接受时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "vdef15");
        schema5.put("title", "供应商返回的交期");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invcode");
        schema6.put("title", "存货编码");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invname");
        schema7.put("title", "存货名称");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "invspec");
        schema8.put("title", "规格");
        schema8.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);

        map.put("schemas", schemas);

        String sql = "SELECT\n" +
                "\t\t\t\t pb.p_pray_bill_num as prayBillNum,\n" +
                "\t\t\t\t u.USER_NAME as userName,\n" +
                "\t\t\t\t pb.p_DPRAYDATE as DPRAYDATE,\n" +
                "\t\t\t\t pb.p_receive_time as receiveTime,\n" +
                "\t\t\t\t pb.vdef15 as vdef15,\n" +
                "\t\t\t\t bd.invcode as invcode,\n" +
                "\t\t\t\t bd.invname as invname,\n" +
                "\t\t\t\t bd.invspec as invspec\n" +
                "\t\t\t\t FROM\n" +
                "\t\t\t\t po_praybill_b_copy1 pb\n" +
                "\t\t\t\t LEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                "\t\t\t\t LEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\t\t\t\t AND pob.dr = 0\n" +
                "\t\t\t\t LEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "\t\t\t\t WHERE\n" +
                "\t\t\t\t pb.dr = 0  \n" +
                "\t\t\t\t\n" +
                "\t\t\t\t AND pb.p_DPRAYDATE>='2018-01-01'\n" +
                "\t\t\t\t AND c_currentstatus='6'" +
                "\tAND c_region= " + region;

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);

        map.put("data", list);
        map.put("schemas", schemas);
        return map;

    }

    /**
     * 采购部领导 其他 运载 战术武器  其他 明细 1.运载 2.战术武器  3、4空间平台（3.卫星 4.空间科学） 5.其他
     *
     * @return
     */
    public Map<String, Object> cgbld07(Integer region) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "vdef20");
        schema1.put("title", "采购计划号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "prayBillNum");
        schema3.put("title", "请购单号");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "orderTime");
        schema4.put("title", "请购单制单时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "DPRAYDATE");
        schema5.put("title", "请购日期");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "receiveTime");
        schema6.put("title", "请购单接受时间");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "vdef15");
        schema7.put("title", "供应商返回的交期");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "p_order_code");
        schema8.put("title", "订单号");
        schema8.put("type", "dimension");
        Map<String, String> schema9 = new HashMap<>();
        schema9.put("field", "invcode");
        schema9.put("title", "存货编码");
        schema9.put("type", "dimension");
        Map<String, String> schema10 = new HashMap<>();
        schema10.put("field", "invname");
        schema10.put("title", "存货名称");
        schema10.put("type", "dimension");
        Map<String, String> schema11 = new HashMap<>();
        schema11.put("field", "invspec");
        schema11.put("title", "规格");
        schema11.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);
        schemas.add(schema9);
        schemas.add(schema10);
        schemas.add(schema11);

        map.put("schemas", schemas);
        //List<Map<String, Object>> data = new ArrayList<>();
        //完全到货明细
        String sql = "SELECT\n" +
                "\t\tsb.vdef20 as vdef20,\n" +
                "\t\tu.USER_NAME as userName,\n" +
                "\t pb.p_pray_bill_num as prayBillNum, -- 1\n" +
                "\t pb.p_order_time as orderTime,-- 1\n" +
                "\tpb.p_DPRAYDATE as DPRAYDATE,-- 1\n" +
                "\tpb.p_receive_time as receiveTime,-- 1\n" +
                "\tpb.vdef15 as vdef15,-- 1\n" +
                "\tpob.p_order_code as orderCode,\n" +
                "\t bi.invcode as invcode,\n" +
                "\t bi.invname as invname,\n" +
                "\t bi.invspec as invspec \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 pb\n" +
                "\tLEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                "\t LEFT JOIN shht_stock_b sb ON pb.csourcebillrowid = sb.pk_stock_b\n" +
                "\t LEFT JOIN bd_invbasdoc bi ON bi.pk_invbasdoc = sb.pk_invbasdoc \n" +
                "\t LEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid\n" +
                "WHERE\n" +
                "\t  sb.dr = 0 and pb.dr = 0  \n" +
                "\t AND c_currentstatus='0'\n" +
                "\t AND pb.p_DPRAYDATE>='2018-01-01'\n";

        String sql1 = null;
        if (region != null) {
            sql1 = sql + "AND c_region= " + region;
        }

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql1);

        map.put("data", list);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 采购部领导 订单未接收 空间平台
     **/

    public Map<String, Object> cgbld21() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "vdef20");
        schema1.put("title", "采购计划号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "prayBillNum");
        schema3.put("title", "请购单号");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "orderTime");
        schema4.put("title", "请购单制单时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "invcode");
        schema5.put("title", "存货编码");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invname");
        schema6.put("title", "存货名称");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invspec");
        schema7.put("title", "规格");
        schema7.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);

        map.put("schemas", schemas);
        //List<Map<String, Object>> data = new ArrayList<>();
        //订单未接收明细
        String sql = "SELECT\n" +
                "\tsb.vdef20 as vdef20,\n" +
                "\tu.USER_NAME as userName,\n" +
                "\tpb.p_pray_bill_num as prayBillNum,\n" +
                "\tpb.p_order_time as orderTime,\n" +
                "\tbi.invcode as invcode,\n" +
                "\tbi.invname as invname,\n" +
                "\tbi.invspec as invspec \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 pb\n" +
                "\tLEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                "\tLEFT JOIN shht_stock_b sb ON pb.csourcebillrowid = sb.pk_stock_b\n" +
                "\tLEFT JOIN bd_invbasdoc bi ON bi.pk_invbasdoc = sb.pk_invbasdoc \n" +
                "WHERE\n" +
                "\tpb.p_receive_time IS NULL\n" +
                "\tAND sb.dr = 0 \n" +
                "\tAND pb.dr = 0\n" +
                "\tAND pb.p_order_time>='2018-01-01'\n" +
                "\tAND c_currentstatus='1'\n" +
                "\t\tAND c_region='3' or c_region='4' ";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            data.put("amount", amount.longValue());
            BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }

    /**
     * 采购部领导 完全到货 空间平台
     **/

    public Map<String, Object> cgbld22() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "DPRAYDATE");
        schema3.put("title", "请购日期");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "receiveTime");
        schema4.put("title", "请购单接受时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "vdef15");
        schema5.put("title", "供应商返回的交期");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invcode");
        schema6.put("title", "存货编码");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invname");
        schema7.put("title", "存货名称");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "invspec");
        schema8.put("title", "规格");
        schema8.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);

        map.put("schemas", schemas);
        //List<Map<String, Object>> data = new ArrayList<>();
        //完全到货明细
        String sql = "SELECT\n" +
                "\tbill.p_pray_bill_num as prayBillNum,\n" +
                "\tu.USER_NAME as userName,\n" +
                "\tbill.p_DPRAYDATE as DPRAYDATE,\n" +
                "\tbill.p_receive_time as receiveTime,\n" +
                "\tbill.vdef15 as vdef15,\n" +
                "\tpob.p_order_code as orderCode,\n" +
                "\tbd.invcode as invcode,\n" +
                "\tbd.invname as invname,\n" +
                "\tbd.invspec as invspec\n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill \n" +
                "\t\tLEFT JOIN sm_user u ON bill.p_purchaser = u.CUSERID\n" +
                "\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = bill.cpraybill_bid\n" +
                "\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = bill.cbaseid \n" +
                "WHERE\n" +
                "\tbill.c_total_purchase_order_num IS NOT NULL \n" +
                "\t AND bill.c_total_purchase_order_num > 0 \n" +
                "\tAND bill.c_total_purchase_order_num >= bill.NPRAYNUM \n" +
                "\tAND bill.DR = 0\n" +
                "\tAND bill.p_DPRAYDATE>'2018-01-01'\n" +
                "\tAND c_currentstatus='2'\n" +
                "\t\t AND c_region='3' or c_region='4' ";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            data.put("amount", amount.longValue());
            BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }

    /**
     * 采购部领导 采购过程中 空间平台
     **/

    public Map<String, Object> cgbld23() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "DPRAYDATE");
        schema3.put("title", "请购日期");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "receiveTime");
        schema4.put("title", "请购单接受时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "vdef15");
        schema5.put("title", "供应商返回的交期");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invcode");
        schema6.put("title", "存货编码");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invname");
        schema7.put("title", "存货名称");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "invspec");
        schema8.put("title", "规格");
        schema8.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);

        map.put("schemas", schemas);
        //List<Map<String, Object>> data = new ArrayList<>();
        //采购过程中明细
        String sql = "SELECT\n" +
                "\tpb.p_pray_bill_num as prayBillNum,\n" +
                "\tu.USER_NAME as userName,\n" +
                "\tpb.p_DPRAYDATE as DPRAYDATE,\n" +
                "\tpb.p_receive_time as receiveTime,\n" +
                "\tpb.vdef15 as vdef15,\n" +
                "\tbd.invcode as invcode,\n" +
                "\tbd.invname as invname,\n" +
                "\tbd.invspec as invspec \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 pb\n" +
                "\tLEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                "\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\tAND pob.dr = 0\n" +
                "\n" +
                "WHERE\n" +
                "\tpb.p_receive_time IS NOT NULL \n" +
                "\tAND pob.cupsourcebillrowid IS NULL\n" +
                "\tAND pb.dr = 0\n" +
                "\tAND pb.p_DPRAYDATE>'2018-01-01'\n" +
                "\t\tAND c_currentstatus='3'\n" +
                "\t\t\tAND c_region='3' or c_region='4' ";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            data.put("amount", amount.longValue());
            BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }

    /**
     * 采购部领导 临近到货 空间平台
     **/

    public Map<String, Object> cgbld24() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "DPRAYDATE");
        schema3.put("title", "请购日期");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "receiveTime");
        schema4.put("title", "请购单接受时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "vdef15");
        schema5.put("title", "供应商返回的交期");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invcode");
        schema6.put("title", "存货编码");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invname");
        schema7.put("title", "存货名称");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "invspec");
        schema8.put("title", "规格");
        schema8.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);

        map.put("schemas", schemas);
        //List<Map<String, Object>> data = new ArrayList<>();
        //临近到货明细
        String sql = "SELECT\n" +
                "\t\tpb.p_pray_bill_num as prayBillNum,\n" +
                "\t\tu.USER_NAME as userName,\n" +
                "\t\tpb.p_DPRAYDATE as DPRAYDATE,\n" +
                "\t\tpb.p_receive_time as receiveTime,\n" +
                "\t\tpb.vdef15 as vdef15,\n" +
                "\t\tbd.invcode as invcode,\n" +
                "\t\tbd.invname as invname,\n" +
                "\t\tbd.invspec as invspec\n" +
                "\tFROM\n" +
                "\t\tpo_praybill_b_copy1 pb\n" +
                "\t\tLEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                "\t\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\t\tAND pob.dr = 0\n" +
                "\t\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "\tWHERE\n" +
                "\t\tpb.dr = 0 \n" +
                "\t\tAND pb.p_receive_time IS NOT NULL \n" +
                "\t\tAND pob.cupsourcebillrowid IS NULL \n" +
                "\t\tAND pb.vdef15 IS NOT NULL \n" +
                "\t\tAND pb.vdef15 >= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.vdef15 <= DATE_FORMAT( DATE_ADD( NOW( ), INTERVAL 1 MONTH ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.p_DPRAYDATE>='2018-01-01'\n" +
                "\t\tAND c_currentstatus='4'\n" +
                "\t\t\t AND c_region='3' or c_region='4' \n" +
                "UNION\n" +
                "\tSELECT\n" +
                "\t\tpb.p_pray_bill_num as prayBillNum,\n" +
                "\t\tu.USER_NAME as userName,\n" +
                "\t\tpb.p_DPRAYDATE as DPRAYDATE,\n" +
                "\t\tpb.p_receive_time as receiveTime,\n" +
                "\t\tpb.vdef15 as vdef15,\n" +
                "\t\tbd.invcode as invcode,\n" +
                "\t\tbd.invname as invname,\n" +
                "\t\tbd.invspec as invspec\n" +
                "\t\t\n" +
                "\tFROM\n" +
                "\t\tpo_praybill_b_copy1 pb\n" +
                "\t\tLEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                "\t\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\t\tAND pob.dr = 0\n" +
                "\t\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "\tWHERE\n" +
                "\t\tpb.dr = 0 \n" +
                "\t\tAND pb.p_receive_time IS NOT NULL \n" +
                "\t\tAND pob.cupsourcebillrowid IS NULL \n" +
                "\t\tAND pb.vdef15 IS NULL \n" +
                "\t\tAND pb.dsuggestdate IS NOT NULL \n" +
                "\t\tAND pb.vdef15 >= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.vdef15 <= DATE_FORMAT( DATE_ADD( NOW( ), INTERVAL 1 MONTH ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.dsuggestdate IS NOT NULL \n" +
                "\t\tAND pb.dsuggestdate >= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.dsuggestdate <= DATE_FORMAT( DATE_ADD( NOW( ), INTERVAL 1 MONTH ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.p_DPRAYDATE>='2018-01-01'\n" +
                "\t\tAND c_currentstatus='4'\n" +
                "\t\t\t AND c_region='3' or c_region='4' \n" +
                "UNION\n" +
                "\tSELECT\n" +
                "\t\tpb.p_pray_bill_num as prayBillNum,\n" +
                "\t\tu.USER_NAME as userName,\n" +
                "\t\tpb.p_DPRAYDATE as DPRAYDATE,\n" +
                "\t\tpb.p_receive_time as receiveTime,\n" +
                "\t\tpb.vdef15 as vdef15,\n" +
                "\t\tbd.invcode as invcode,\n" +
                "\t\tbd.invname as invname,\n" +
                "\t\tbd.invspec as invspec\n" +
                "\tFROM\n" +
                "\t\tpo_praybill_b_copy1 pb\n" +
                "\t\tLEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                "\t\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\t\tAND pob.dr = 0\n" +
                "\t\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "\tWHERE\n" +
                "\t\tpb.dr = 0 \n" +
                "\t\tAND pb.p_receive_time IS NOT NULL \n" +
                "\t\tAND pob.cupsourcebillrowid IS NULL \n" +
                "\t\tAND pb.vdef15 IS NOT NULL \n" +
                "\t\tAND pb.vdef15 >= DATE_FORMAT( NOW( ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.vdef15 <= DATE_FORMAT( DATE_ADD( NOW( ), INTERVAL 1 MONTH ), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.vdef15 IS NULL \n" +
                "\t\tAND pb.dsuggestdate IS NULL \n" +
                "\t\tAND DATE_ADD( STR_TO_DATE( pb.p_receive_time, '%Y-%m-%d %H' ), INTERVAL 70 DAY ) >= NOW( ) \n" +
                "\t\tAND DATE_ADD( STR_TO_DATE( pb.p_receive_time, '%Y-%m-%d %H' ), INTERVAL 40 DAY ) <= NOW( ) \n" +
                "\t\tAND pb.p_DPRAYDATE>='2018-01-01'\n" +
                "\t\tAND c_currentstatus='4'\n" +
                "\t\t\t AND c_region='3' or c_region='4' ";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            data.put("amount", amount.longValue());
            BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }

    /**
     * 采购部领导 部分到货 空间平台
     **/

    public Map<String, Object> cgbld25() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "DPRAYDATE");
        schema3.put("title", "请购日期");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "receiveTime");
        schema4.put("title", "请购单接受时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "vdef15");
        schema5.put("title", "供应商返回的交期");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invcode");
        schema6.put("title", "存货编码");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invname");
        schema7.put("title", "存货名称");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "invspec");
        schema8.put("title", "规格");
        schema8.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);

        map.put("schemas", schemas);
        //List<Map<String, Object>> data = new ArrayList<>();
        //部分到货明细
        String sql = "SELECT\n" +
                "\tbill.p_pray_bill_num as prayBillNum,\n" +
                "\tu.USER_NAME as userName,\n" +
                "\tbill.p_DPRAYDATE as DPRAYDATE,\n" +
                "\tbill.p_receive_time as receiveTime,\n" +
                "\tbill.vdef15 as vdef15,\n" +
                "\tpob.p_order_code as orderCode,\n" +
                "\tbd.invcode as invcode,\n" +
                "\tbd.invname as invname,\n" +
                "\tbd.invspec as invspec\n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill \n" +
                "\tLEFT JOIN sm_user u ON bill.p_purchaser = u.CUSERID\n" +
                "\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = bill.cpraybill_bid \n" +
                "\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = bill.cbaseid \n" +
                "WHERE\n" +
                "\tbill.c_total_purchase_order_num IS NOT NULL \n" +
                "\tAND bill.c_total_purchase_order_num > 0 \n" +
                "\tAND bill.c_total_purchase_order_num < bill.NPRAYNUM \n" +
                "\tAND bill.DR = 0\n" +
                "\tAND pob.DR=0\n" +
                "\tAND bd.DR=0\n" +
                "\tAND bill.p_DPRAYDATE>='2018-01-01'\n" +
                "\tAND c_currentstatus='5'\n" +
                "\t\t AND c_region='3' or c_region='4' ";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            data.put("amount", amount.longValue());
            BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }

    /**
     * 采购部领导 逾期未到货 空间平台
     **/

    public Map<String, Object> cgbld26() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prayBillNum");
        schema1.put("title", "请购单号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "DPRAYDATE");
        schema3.put("title", "请购日期");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "receiveTime");
        schema4.put("title", "请购单接受时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "vdef15");
        schema5.put("title", "供应商返回的交期");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "invcode");
        schema6.put("title", "存货编码");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "invname");
        schema7.put("title", "存货名称");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "invspec");
        schema8.put("title", "规格");
        schema8.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);

        map.put("schemas", schemas);
        //List<Map<String, Object>> data = new ArrayList<>();
        //逾期未到货统计明细
        String sql = "SELECT\n" +
                "\tpb.p_pray_bill_num as prayBillNum,\n" +
                "\tu.USER_NAME as userName,\n" +
                "\tpb.p_DPRAYDATE as DPRAYDATE,\n" +
                "\tpb.p_receive_time as receiveTime,\n" +
                "\tpb.vdef15 as vdef15,\n" +
                "\tbd.invcode as invcode,\n" +
                "\tbd.invname as invname,\n" +
                "\tbd.invspec as invspec\t\n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 pb\n" +
                "\tLEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                "\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\tAND pob.dr = 0\n" +
                "\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "WHERE\n" +
                "\tpb.dr = 0 \n" +
                "\tAND pb.p_receive_time IS NOT NULL \n" +
                "\tAND pob.cupsourcebillrowid IS NULL \n" +
                "\tAND pb.vdef15 IS NOT NULL AND pb.vdef15 < DATE_FORMAT( NOW(), '%Y-%m-%d' ) \n" +
                "\tAND pb.p_DPRAYDATE>='2018-01-01'\n" +
                "\tAND c_currentstatus='6'\n" +
                "\tAND c_region='3' or c_region='4' \n" +
                "union\n" +
                "\tSELECT\n" +
                "\tpb.p_pray_bill_num as prayBillNum,\n" +
                "\tu.USER_NAME as userName,\n" +
                "\tpb.p_DPRAYDATE as DPRAYDATE,\n" +
                "\tpb.p_receive_time as receiveTime,\n" +
                "\tpb.vdef15 as vdef15,\n" +
                "\tbd.invcode as invcode,\n" +
                "\tbd.invname as invname,\n" +
                "\tbd.invspec as invspec\t\n" +
                "\tFROM\n" +
                "\t\tpo_praybill_b_copy1 pb\n" +
                "\t\tLEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                "\t\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\t\tAND pob.dr = 0\n" +
                "\t\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "\tWHERE\n" +
                "\t\tpb.dr = 0 \n" +
                "\t\tAND pb.p_receive_time IS NOT NULL \n" +
                "\t\tAND pob.cupsourcebillrowid IS NULL \n" +
                "\t\tAND pb.vdef15 IS NULL AND pb.dsuggestdate IS NOT NULL AND pb.dsuggestdate < DATE_FORMAT( NOW(), '%Y-%m-%d' ) \n" +
                "\t\tAND pb.p_DPRAYDATE>='2018-01-01'\n" +
                "\t\tAND c_currentstatus='6'\n" +
                "\t\t\t AND c_region='3' or c_region='4' \n" +
                "union \t\n" +
                "\tSELECT\n" +
                "\tpb.p_pray_bill_num as prayBillNum,\n" +
                "\tu.USER_NAME as userName,\n" +
                "\tpb.p_DPRAYDATE as DPRAYDATE,\n" +
                "\tpb.p_receive_time as receiveTime,\n" +
                "\tpb.vdef15 as vdef15,\n" +
                "\tbd.invcode as invcode,\n" +
                "\tbd.invname as invname,\n" +
                "\tbd.invspec as invspec\t\n" +
                "\tFROM\n" +
                "\t\tpo_praybill_b_copy1 pb\n" +
                "\t\tLEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                "\t\tLEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid \n" +
                "\t\tAND pob.dr = 0\n" +
                "\t\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "\tWHERE\n" +
                "\t\tpb.dr = 0 \n" +
                "\t\tAND pb.p_receive_time IS NOT NULL \n" +
                "\t\tAND pob.cupsourcebillrowid IS NULL \n" +
                "\tAND\tpb.vdef15 IS NULL AND pb.dsuggestdate IS NULL \n" +
                "\tAND DATE_ADD( STR_TO_DATE( LEFT(pb.p_receive_time, 10), '%Y-%m-%d' ), INTERVAL 70 DAY ) < NOW()\t\n" +
                "\tAND pb.p_DPRAYDATE>='2018-01-01'\n" +
                "\tAND c_currentstatus='6'\n" +
                "\t\t AND c_region='3' or c_region='4' ";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            data.put("amount", amount.longValue());
            BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;

    }

    /**
     * 采购部领导 其他 空间平台明细
     **/

    public Map<String, Object> cgbld27() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "vdef20");
        schema1.put("title", "采购计划号");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "prayBillNum");
        schema3.put("title", "请购单号");
        schema3.put("type", "dimension");
        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "orderTime");
        schema4.put("title", "请购单制单时间");
        schema4.put("type", "dimension");
        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "DPRAYDATE");
        schema5.put("title", "请购日期");
        schema5.put("type", "dimension");
        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "receiveTime");
        schema6.put("title", "请购单接受时间");
        schema6.put("type", "dimension");
        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "vdef15");
        schema7.put("title", "供应商返回的交期");
        schema7.put("type", "dimension");
        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "orderCode");
        schema8.put("title", "订单号");
        schema8.put("type", "dimension");
        Map<String, String> schema9 = new HashMap<>();
        schema9.put("field", "invcode");
        schema9.put("title", "存货编码");
        schema9.put("type", "dimension");
        Map<String, String> schema10 = new HashMap<>();
        schema10.put("field", "invname");
        schema10.put("title", "存货名称");
        schema10.put("type", "dimension");
        Map<String, String> schema11 = new HashMap<>();
        schema11.put("field", "invspec");
        schema11.put("title", "规格");
        schema11.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);
        schemas.add(schema9);
        schemas.add(schema10);
        schemas.add(schema11);

        map.put("schemas", schemas);
        //List<Map<String, Object>> data = new ArrayList<>();
        //完全到货明细
        String sql = "SELECT\n" +
                "\t\tsb.vdef20 as vdef20,\n" +
                "\t\tu.USER_NAME as userName,\n" +
                "\t pb.p_pray_bill_num as prayBillNum, -- 1\n" +
                "\t pb.p_order_time as orderTime,-- 1\n" +
                "\tpb.p_DPRAYDATE as DPRAYDATE,-- 1\n" +
                "\tpb.p_receive_time as receiveTime,-- 1\n" +
                "\tpb.vdef15 as vdef15,-- 1\n" +
                "\tpob.p_order_code as orderCode,\n" +
                "\t bi.invcode as invcode,\n" +
                "\t bi.invname as invname,\n" +
                "\t bi.invspec as invspec \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 pb\n" +
                "\tLEFT JOIN sm_user u ON pb.p_purchaser = u.CUSERID\n" +
                "\t LEFT JOIN shht_stock_b sb ON pb.csourcebillrowid = sb.pk_stock_b\n" +
                "\t LEFT JOIN bd_invbasdoc bi ON bi.pk_invbasdoc = sb.pk_invbasdoc \n" +
                "\t LEFT JOIN po_order_b_copy1 pob ON pob.cupsourcebillrowid = pb.cpraybill_bid\n" +
                "WHERE\n" +
                "\t  sb.dr = 0 \n" +
                "\t AND c_currentstatus='0'\n" +
                "\t AND p_DPRAYDATE>='2018-01-01'\n" +
                "\t  AND c_region='3' or c_region='4' \n";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(data -> {
            BigDecimal amount = (BigDecimal) data.getOrDefault("amount", BigDecimal.ZERO);
            data.put("amount", amount.longValue());
            BigDecimal orderQuantity = (BigDecimal) data.getOrDefault("orderQuantity", BigDecimal.ZERO);
            data.put("orderQuantity", orderQuantity.intValue());
            dataList.add(data);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }


/***********采购部领域管理员**********/

    /**
     * 采购部领域管理员 - 按照领域,状态统计
     */
    public Map<String, Object> cgblyAdmin1() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "status");
        schema1.put("title", "状态");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "count");
        schema2.put("title", "数量");
        schema2.put("type", "measure");
        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "region");
        schema3.put("title", "领域");
        schema3.put("type", "dimension");
        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        map.put("schemas", schemas);

        List<Map<String, Object>> data = new ArrayList<>();
        //按照领域,状态统计
        String sql = "SELECT\n" +
                "CASE\n" +
                "\t\tc_region \n" +
                "\t\tWHEN 1 THEN\n" +
                "\t\t'运载' \n" +
                "\t\tWHEN 2 THEN\n" +
                "\t\t'战术武器' \n" +
                "\t\tWHEN 3 THEN\n" +
                "\t\t'卫星' \n" +
                "\t\tWHEN 4 THEN\n" +
                "\t\t'空间科学' \n" +
                "\t\tWHEN 5 THEN\n" +
                "\t\t'其他' ELSE '其他' \n" +
                "\tEND AS region,\n" +
                "CASE\n" +
                "\t\tc_currentstatus \n" +
                "\t\tWHEN 1 THEN '订单未接收' \n" +
                "\t\t\n" +
                "\t\tWHEN 2 THEN '完全到货' \n" +
                "\t\t\n" +
                "\t\tWHEN 3 THEN '采购过程中' \n" +
                "\t\t\n" +
                "\t\tWHEN 4 THEN '临近到货' \n" +
                "\t\t\n" +
                "\t\tWHEN 5 THEN '部分到货' \n" +
                "\t\t\n" +
                "\t\tWHEN 6 THEN '逾期未到货'\n" +
                "\t\tELSE '其他'\n" +
                "\tEND AS status,\n" +
                "COUNT( 1 ) AS count \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 b " +
                "where b.dr = 0 \n" +
                "AND b.p_DPRAYDATE>='2018-01-01' \n" +
                "GROUP BY\n" +
                "\tc_region,\n" +
                "\tc_currentstatus";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        data.addAll(list);
        map.put("data", data);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 采购部领域管理员  - 按照状态统计  查全部
     **/
    public Map<String, Object> cgblyAdmin2() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "status");
        schema1.put("title", "状态");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "count");
        schema2.put("title", "数量");
        schema2.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        map.put("schemas", schemas);

        List<Map<String, Object>> data = new ArrayList<>();
        //按照领域,状态统计
        String sql = "\n" +
                "SELECT\n" +
                "CASE\n" +
                "\t\tc_currentstatus \n" +
                "\t\tWHEN 1 THEN\n" +
                "\t\t'订单未接收' \n" +
                "\t\tWHEN 2 THEN\n" +
                "\t\t'完全到货' \n" +
                "\t\tWHEN 3 THEN\n" +
                "\t\t'采购过程中' \n" +
                "\t\tWHEN 4 THEN\n" +
                "\t\t'临近到货' \n" +
                "\t\tWHEN 5 THEN\n" +
                "\t\t'部分到货' \n" +
                "\t\tWHEN 6 THEN\n" +
                "\t\t'逾期未到货' ELSE '其他' \n" +
                "\tEND AS status,\n" +
                "\tCOUNT( 1 ) AS count \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 b " +
                "where b.dr = 0 " +
                "AND b.p_DPRAYDATE>='2018-01-01' \n" +
                "GROUP BY\n" +
                "\tc_currentstatus";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        data.addAll(list);
        map.put("data", data);
        map.put("schemas", schemas);
        return map;
    }

    /**
     * 采购部领域管理员  - 按照状态统计  根据领域 包括 卫星、运载、战术、空间、其他
     */
    public Map<String, Object> cgblyAdmin21(Integer region) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "status");
        schema1.put("title", "状态");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "count");
        schema2.put("title", "数量");
        schema2.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        map.put("schemas", schemas);

        List<Map<String, Object>> data = new ArrayList<>();
        //按照领域,状态统计
        String sql = "\n" +
                "SELECT\n" +
                "CASE\n" +
                "\t\tc_currentstatus \n" +
                "\t\tWHEN 1 THEN\n" +
                "\t\t'订单未接收' \n" +
                "\t\tWHEN 2 THEN\n" +
                "\t\t'完全到货' \n" +
                "\t\tWHEN 3 THEN\n" +
                "\t\t'采购过程中' \n" +
                "\t\tWHEN 4 THEN\n" +
                "\t\t'临近到货' \n" +
                "\t\tWHEN 5 THEN\n" +
                "\t\t'部分到货' \n" +
                "\t\tWHEN 6 THEN\n" +
                "\t\t'逾期未到货' ELSE '其他' \n" +
                "\tEND AS status,\n" +
                "\tCOUNT( 1 ) AS count \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 b " +
                "where b.dr = 0 " +
                "AND b.p_DPRAYDATE>='2018-01-01' \n" +
                "GROUP BY\n" +
                "\tc_currentstatus";
        String sql1 = null;
        if (region != null) {
            sql1 = sql + "AND c_region= " + region;
        }
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql1);
        data.addAll(list);
        map.put("data", data);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 采购部领域管理员  - 逾期统计
     */
    public Map<String, Object> cgblyAdmin3() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "month");
        schema1.put("title", "月份");
        schema1.put("type", "dimension");
        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "count");
        schema2.put("title", "数量");
        schema2.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        map.put("schemas", schemas);

        List<Map<String, Object>> data = new ArrayList<>();
        //按照领域,状态统计
        String sql = "SELECT\n" +
                "\tc_month as month,\n" +
                "\tCOUNT( 1 ) as count \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill \n" +
                "WHERE\n" +
                "\t( bill.c_total_purchase_order_num IS NULL OR bill.c_total_purchase_order_num < bill.NPRAYNUM ) \n" +
                "\tAND (\n" +
                "\t\tbill.vdef15 IS NOT NULL \n" +
                "\t\tAND bill.vdef15 < DATE_FORMAT( NOW( ), '%Y-%m-%d' ) \n" +
                "\t\tOR ( bill.vdef15 IS NULL AND bill.dsuggestdate IS NOT NULL AND bill.dsuggestdate < DATE_FORMAT( NOW( ), '%Y-%m-%d' ) ) \n" +
                "\t\tOR ( DATE_ADD( STR_TO_DATE( LEFT ( bill.p_receive_time, 10 ), '%Y-%m-%d' ), INTERVAL 70 DAY ) < NOW( ) ) \n" +
                "\t) \n" +
                "\t AND bill.p_DPRAYDATE>='2018-01-01' and  bill.dr = 0 \n" +
                "GROUP BY\n" +
                "\tc_month";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        data.addAll(list);
        map.put("data", data);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 采购部领域管理员  - 根据 采购员,订单状态统计
     */
    public Map<String, Object> cgblyAdmin4(Integer region) {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "status");
        schema1.put("title", "状态");
        schema1.put("type", "dimension");


        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "userName");
        schema2.put("title", "采购员");
        schema2.put("type", "dimension");

        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "count");
        schema3.put("title", "数量");
        schema3.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);

        map.put("schemas", schemas);

        List<Map<String, Object>> data = new ArrayList<>();
        //按照领域,状态统计
        String sql = "SELECT  CASE\n" +
                "\t\t\tc_currentstatus WHEN 1 THEN '订单未接收' \n" +
                "\t\t\t                WHEN 2 THEN '完全到货' \n" +
                "\t\t\t\t\t\t\t\t\t\t\tWHEN 3 THEN '采购过程中' \n" +
                "\t\t\t\t\t\t\t\t\t\t\tWHEN 4 THEN '临近到货' \n" +
                "                      WHEN 5 THEN '部分到货'\n" +
                "\t\t\t\t\t\t\t\t\t\t\tWHEN 6 THEN '逾期未到货'\n" +
                "\t\t\t\t\t\t\t\t\t\t\tELSE '其他'\n" +
                "\t\t\t\t\t\t\t\t\t\t\tEND AS status,\n" +
                "\t\t\t\t\t\t\t\t\t\t\tu.USER_NAME AS userName,count( 1 ) AS count\n" +
                "\t\t\t\t\t\t\t\t\t\t\tFROM po_praybill_b_copy1 ppb\n" +
                "\t\t\t\t\t\t\t\t\t\t\tLEFT JOIN sm_user u ON ppb.p_purchaser = u.CUSERID \n" +
                "\t\t\t\t\t\t\t\t\t\t\tWHERE u.USER_NAME IS NOT NULL and ppb.dr = 0 and u.dr = 0 \n" +
                "\t\t\t\t\t\t\t        AND u.USER_NAME !=('answer') " +
                "AND ppb.p_DPRAYDATE>='2018-01-01'\n" +
                "\t\t\t\t\t\t\t\t\t\t\tGROUP BY\n" +
                "\t\t\t\t\t\t\t\t\t\t\tc_currentstatus,\n" +
                "\t\t\t\t\t\t\t\t\t\t\tu.CUSERID,\n" +
                "\t\t\t\t\t\t\t\t\t\t\tu.USER_NAME,\n";

        List<Map<String, Object>> list = null;
        String sql1 = null;
        if (region != null) {
            sql1 = sql + " AND c_region= " + region;
            list = jdbcTemplate.queryForList(sql1);
        } else {
            list = jdbcTemplate.queryForList(sql);
        }
        //List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        data.addAll(list);
        map.put("data", data);
        map.put("schemas", schemas);
        return map;
    }


/***********事业部领导**********/

    /**
     * 事业部领导  - 型号到货率
     */
    public Map<String, Object> sybld1() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prodmodelCode");
        schema1.put("title", "型号");
        schema1.put("type", "dimension");

        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "percent");
        schema2.put("title", "到货率");
        schema2.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        map.put("schemas", schemas);

        // 查询 各个型号 总数
        String sql = "SELECT\n" +
                "   \tcount( po_praybill_b_copy1.CPRAYBILL_BID ) as count,\n" +
                "   \tshht_nworkorder_copy1.c_prodmodelcode  as prodmodelCode\n" +
                "   FROM\n" +
                "   \tpo_praybill_b_copy1\n" +
                "   \tLEFT JOIN shht_nworkorder_copy1 ON po_praybill_b_copy1.p_working_key = shht_nworkorder_copy1.workorder \n" +
                "   \twhere  shht_nworkorder_copy1.c_prodmodelcode is not null " +
                "and po_praybill_b_copy1.dr = 0  \n" +
                "   GROUP BY\n" +
                "   \tshht_nworkorder_copy1.c_prodmodelcode";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);


        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(item -> {
            Map<String, Object> dataMap = new HashMap<>();
            String prodmodelCode = (String) item.getOrDefault("prodmodelCode", "");
            Long totalCount = (Long) item.getOrDefault("count", 0L);

            // 单个型号到货数量
            String daohuoSql = "SELECT\n" +
                    "\tcount( po_praybill_b_copy1.CPRAYBILL_BID ) as count\n" +
                    "FROM\n" +
                    "\tpo_praybill_b_copy1\n" +
                    "\tLEFT JOIN shht_nworkorder_copy1 ON po_praybill_b_copy1.p_working_key = shht_nworkorder_copy1.workorder \n" +
                    "WHERE\n" +
                    "\tpo_praybill_b_copy1.npraynum >= po_praybill_b_copy1.c_total_arrive_order_num " +
                    "and po_praybill_b_copy1.dr = 0 and shht_nworkorder_copy1.dr = 0 \n" +
                    "\tand  shht_nworkorder_copy1.c_prodmodelcode ='" + prodmodelCode + "'";
            Long daohuoCount = jdbcTemplate.queryForObject(daohuoSql, Long.class);
            Double percent = daohuoCount * 1.0 / totalCount;
            String percentStr = NumberUtils.format2(percent);
            dataMap.put("prodmodelCode", prodmodelCode);
            dataMap.put("percent", percentStr);
            dataList.add(dataMap);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 事业部领导-型号到货率明细    HZT
     ***/
    public Map<String, Object> sybldlistAndspec() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "spec");
        schema1.put("title", "规格");
        schema1.put("type", "dimension");

        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "count");
        schema2.put("title", "数量");
        schema2.put("type", "measure");

        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "workingkey");
        schema3.put("title", "工作令");
        schema3.put("type", "dimension");

        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "custname");
        schema4.put("title", "厂家");
        schema4.put("type", "dimension");

        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "billnum");
        schema5.put("title", "请购单号");
        schema5.put("type", "dimension");

        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "receiveTime");
        schema6.put("title", "接收时间");
        schema6.put("type", "dimension");


        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        map.put("schemas", schemas);

        // 查询 规格的信息
        String sql = "(SELECT\n" +
                "bd.invspec as spec,\n" +
                "count( pb.CPRAYBILL_BID ) as count,\n" +
                "p_working_key as workingkey,\n" +
                "p_pray_bill_num as billnum,\n" +
                "cus.custname as custname,\n" +
                "DATE_FORMAT(p_receive_time,'%Y-%m-%d') as receiveTime\n" +
                "FROM po_praybill_b_copy1 pb\n" +
                "LEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = pb.cbaseid \n" +
                "LEFT JOIN bd_cubasdoc cus ON cus.pk_cubasdoc = pb.cvendorbaseid\n" +
                "WHERE bd.invspec is not null and pb.dr = 0  \n" +
                "AND pb.p_order_time>='2018-01-01'\n" +
                "AND cus.custname is not null\n" +
                "GROUP BY\n" +
                "bd.invspec)\n" +
                "ORDER BY p_receive_time DESC";

        List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql);
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 事业部领导  ---型号到货明细
     **/
    public Map<String, Object> sybldlist() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prodmodelCode");
        schema1.put("title", "型号");
        schema1.put("type", "dimension");

        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "percent");
        schema2.put("title", "到货率");
        schema2.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        map.put("schemas", schemas);

        // 查询 各个型号 总数
        String sql = "SELECT\n" +
                "   \tcount( po_praybill_b_copy1.CPRAYBILL_BID ) as count,\n" +
                "   \tshht_nworkorder_copy1.c_prodmodelcode  as prodmodelCode\n" +
                "   FROM\n" +
                "   \tpo_praybill_b_copy1\n" +
                "   \tLEFT JOIN shht_nworkorder_copy1 ON po_praybill_b_copy1.p_working_key = shht_nworkorder_copy1.workorder \n" +
                "   \twhere  shht_nworkorder_copy1.c_prodmodelcode is not null " +
                "and po_praybill_b_copy1.dr = 0 and shht_nworkorder_copy1.dr = 0 \n" +
                "   GROUP BY\n" +
                "   \tshht_nworkorder_copy1.c_prodmodelcode";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);


        List<Map<String, Object>> dataList = new ArrayList<>();
        list.stream().forEach(item -> {
            Map<String, Object> dataMap = new HashMap<>();
            String prodmodelCode = (String) item.getOrDefault("prodmodelCode", "");
            Long totalCount = (Long) item.getOrDefault("count", 0L);

            // 单个型号到货数量
            String daohuoSql = "SELECT\n" +
                    "\tcount( po_praybill_b_copy1.CPRAYBILL_BID ) as count\n" +
                    "FROM\n" +
                    "\tpo_praybill_b_copy1\n" +
                    "\tLEFT JOIN shht_nworkorder_copy1 ON po_praybill_b_copy1.p_working_key = shht_nworkorder_copy1.workorder \n" +
                    "WHERE\n" +
                    "\tpo_praybill_b_copy1.npraynum >= po_praybill_b_copy1.c_total_arrive_order_num " +
                    "and po_praybill_b_copy1.dr = 0 and shht_nworkorder_copy1.dr = 0 \n" +
                    "\tand  shht_nworkorder_copy1.c_prodmodelcode ='" + prodmodelCode + "'";
            Long daohuoCount = jdbcTemplate.queryForObject(daohuoSql, Long.class);
            Double percent = daohuoCount * 1.0 / totalCount;
            String percentStr = NumberUtils.format2(percent);
            dataMap.put("prodmodelCode", prodmodelCode);
            dataMap.put("percent", percentStr);
            dataList.add(dataMap);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 事业部领导  - 领域到货率
     */
    public Map<String, Object> sybld2() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "region");
        schema1.put("title", "领域");
        schema1.put("type", "dimension");

        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "percent");
        schema2.put("title", "到货率");
        schema2.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        map.put("schemas", schemas);

        // 查询  各个领域 总数
        String sql = "SELECT\n" +
                "    CASE\n" +
                "    \t\tc_region \n" +
                "    \t\tWHEN 1 THEN\n" +
                "    \t\t'运载' \n" +
                "    \t\tWHEN 2 THEN\n" +
                "    \t\t'战术武器' \n" +
                "    \t\tWHEN 3 THEN\n" +
                "    \t\t'卫星' \n" +
                "    \t\tWHEN 4 THEN\n" +
                "    \t\t'空间科学' \n" +
                "    \t\tWHEN 5 THEN\n" +
                "    \t\t'其他' ELSE '其他' \n" +
                "    \tEND AS region,\n" +
                "    \tcount( po_praybill_b_copy1.CPRAYBILL_BID ) AS count \n" +
                "    FROM\n" +
                "    \tpo_praybill_b_copy1 " +
                "where po_praybill_b_copy1.dr = 0   \n" +
                "    GROUP BY\n" +
                "    \tpo_praybill_b_copy1.c_region";
        List<Map<String, Object>> regionTotalList = jdbcTemplate.queryForList(sql);

        // 查询  各个领域 到货数量
        String daohuoSql = "SELECT\n" +
                "CASE\n" +
                "\t\tc_region \n" +
                "\t\tWHEN 1 THEN\n" +
                "\t\t'运载' \n" +
                "\t\tWHEN 2 THEN\n" +
                "\t\t'战术武器' \n" +
                "\t\tWHEN 3 THEN\n" +
                "\t\t'卫星' \n" +
                "\t\tWHEN 4 THEN\n" +
                "\t\t'空间科学' \n" +
                "\t\tWHEN 5 THEN\n" +
                "\t\t'其他' ELSE '其他' \n" +
                "\tEND AS region,\n" +
                "\tcount( po_praybill_b_copy1.CPRAYBILL_BID ) AS count \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 \n" +
                "WHERE\n" +
                "\tpo_praybill_b_copy1.npraynum < po_praybill_b_copy1.c_total_arrive_order_num and po_praybill_b_copy1.dr = 0 \n" +
                "GROUP BY\n" +
                "\tpo_praybill_b_copy1.c_region";
        List<Map<String, Object>> regionDaoHuoList = jdbcTemplate.queryForList(daohuoSql);


        //计算 各领域到货率
        List<Map<String, Object>> dataList = new ArrayList<>();
        regionTotalList.stream().forEach(item -> {
            Map<String, Object> dataMap = new HashMap<>();
            String region = (String) item.getOrDefault("region", "");
            Long totalCount = (Long) item.getOrDefault("count", 0L);

            dataMap.put("region", region);
            //领域到货信息
            Optional<Map<String, Object>> optional = regionDaoHuoList.stream()
                    .filter(daohuo -> StringUtils.equals(region, (String) daohuo.getOrDefault("region", ""))).findFirst();

            if (optional.isPresent()) {
                Map<String, Object> daohuoMap = optional.get();
                Long daohuoCount = (Long) daohuoMap.getOrDefault("count", 0L);

                Double percent = daohuoCount * 1.0 / totalCount;
                String percentStr = NumberUtils.format2(percent);
                dataMap.put("percent", percentStr);
            } else {
                dataMap.put("percent", 0.0);
            }
            dataList.add(dataMap);
        });
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 事业部领导  - 器件未来到货 top10
     */
    public Map<String, Object> sybld3() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();
        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "prodmodelCode");
        schema1.put("title", "型号");
        schema1.put("type", "dimension");

        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "overdueDays");
        schema2.put("title", "逾期天数");
        schema2.put("type", "measure");

        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "count");
        schema3.put("title", "数量");
        schema3.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        map.put("schemas", schemas);

        // 器件逾期时间 top10
        String sql = "SELECT\n" +
                "\tbd.invspec as prodmodelCode  ,\n" +
                "\tDATEDIFF(\n" +
                "\t\tNOW( ),\n" +
                "\t\tIFNULL(\n" +
                "\t\t\tbill.vdef15,\n" +
                "\t\t\tIFNULL( bill.dsuggestdate, DATE_ADD( STR_TO_DATE( LEFT ( bill.p_receive_time, 10 ), '%Y-%m-%d' ), INTERVAL 70 DAY ) ) \n" +
                "\t\t) \n" +
                "\t) AS overdueDays,\n" +
                "\tcount( bill.CPRAYBILL_BID ) count \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill\n" +
                "\tLEFT JOIN bd_invbasdoc bd ON bd.pk_invbasdoc = bill.cbaseid \n" +
                "WHERE\n" +
                "\tbd.invspec IS NOT NULL and bill.dr = 0 and bd.dr = 0 " +
                "AND bill.p_DPRAYDATE>='2018-01-01'  \n" +
                "\tAND IFNULL( bill.npraynum, 0 ) < bill.c_total_arrive_order_num \n" +
                "\tAND (\n" +
                "\t\tbill.vdef15 IS NOT NULL \n" +
                "\t\tAND bill.vdef15 < DATE_FORMAT( NOW( ), '%Y-%m-%d' ) \n" +
                "\t\tOR ( bill.vdef15 IS NULL AND bill.dsuggestdate IS NOT NULL AND bill.dsuggestdate < DATE_FORMAT( NOW( ), '%Y-%m-%d' ) ) \n" +
                "\t\tOR ( DATE_ADD( STR_TO_DATE( LEFT ( bill.p_receive_time, 10 ), '%Y-%m-%d' ), INTERVAL 70 DAY ) < NOW( ) ) \n" +
                "\t) \n" +
                "GROUP BY\n" +
                "\tbd.invspec\n" +
                "ORDER BY\n" +
                "\toverdueDays DESC \n" +
                "\tLIMIT 10;";
        List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql);
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 事业部领导  - 厂家未到货top10
     */
    public Map<String, Object> sybld4() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();

        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "cusname");
        schema1.put("title", "厂家名称");
        schema1.put("type", "dimension");

        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "count");
        schema2.put("title", "单数");
        schema2.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        map.put("schemas", schemas);

        // 器件逾期时间 top10
        String sql = "SELECT\n" +
                "\tcus.custname AS cusname,\n" +
                "\tcount( bill.CPRAYBILL_BID ) count \n" +
                "FROM\n" +
                "\tpo_praybill_b_copy1 bill\n" +
                "\tLEFT JOIN bd_cubasdoc cus ON cus.pk_cubasdoc = bill.cvendorbaseid \n" +
                "WHERE\n" +
                "\t( bill.npraynum IS NULL OR bill.npraynum < bill.c_total_arrive_order_num ) \n" +
                "\tAND cus.custname IS NOT NULL and bill.dr = 0 " +
                "and cus.dr = 0 " +
                "and bill.p_DPRAYDATE>='2018-01-01'\n" +
                "GROUP BY\n" +
                "\tbill.cvendorbaseid \n" +
                "ORDER BY\n" +
                "\tcount DESC LIMIT 10;";
        List<Map<String, Object>> dataList = jdbcTemplate.queryForList(sql);
        map.put("data", dataList);
        map.put("schemas", schemas);
        return map;
    }


    /**
     * 事业部领导  - 厂家合同金额top10
     */
    public Map<String, Object> sybld5() {
        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();

        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "custname");
        schema1.put("title", "厂家名称");
        schema1.put("type", "dimension");

        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "amount");
        schema2.put("title", "金额");
        schema2.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        map.put("schemas", schemas);

        // 厂家合同金额 top10
        String sql = "SELECT\n" +
                "cus.custname AS custname,\n" +
                "sum( IFNULL( price.costprice, IFNULL( price.planprice, 0 ) ) * bill.npraynum ) AS amount \n" +
                "FROM\n" +
                "po_praybill_b_copy1 bill\n" +
                "LEFT JOIN bd_cubasdoc cus ON cus.pk_cubasdoc = bill.cvendorbaseid\n" +
                "LEFT JOIN bd_invmandoc_copy1 price ON price.pk_invbasdoc = bill.cbaseid \n" +
                "WHERE\n" +
                "cus.custname IS NOT NULL and bill.dr = 0 and cus.dr = 0 and price.dr = 0\n" +
                "AND bill.p_DPRAYDATE >= '2018-01-01' \n" +
                "GROUP BY\n" +
                "bill.cvendorbaseid \n" +
                "ORDER BY\n" +
                "amount DESC \n" +
                "LIMIT 10;";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> dataMapList = new ArrayList<>();
        list.stream().forEach(data -> {
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("custname", data.get("custname"));
            BigDecimal amount = (BigDecimal) data.get("amount");
            String amountStr = NumberUtils.format2(amount.doubleValue());
            dataMap.put("amount", amountStr);
            dataMapList.add(dataMap);
        });
        map.put("data", dataMapList);
        map.put("schemas", schemas);
        return map;
    }


    /***********采购部领域管理员之我的看板之明细 全部**********/

    public Map<String, Object> cgyleaderdetail() {

        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();


        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "userName");
        schema1.put("title", "采购员");
        schema1.put("type", "dimension");


        Map<String, String> schema3 = new HashMap<>();
        schema3.put("field", "count2");
        schema3.put("title", "订单未接收");
        schema3.put("type", "measure");

        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "count3");
        schema4.put("title", "完全到货");
        schema4.put("type", "measure");

        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "count4");
        schema5.put("title", "采购过程中");
        schema5.put("type", "measure");

        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "count5");
        schema6.put("title", "临近到货");
        schema6.put("type", "measure");

        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "count6");
        schema7.put("title", "部分到货");
        schema7.put("type", "measure");

        Map<String, String> schema8 = new HashMap<>();
        schema8.put("field", "count7");
        schema8.put("title", "逾期未到货");
        schema8.put("type", "measure");


        schemas.add(schema1);

        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        schemas.add(schema8);
        map.put("schemas", schemas);

        String sql = "SELECT  \n" +
                "u.CUSERID as userId,\n" +
                "u.USER_NAME AS userName,\n" +
                "ppb.c_currentstatus as status ,\n" +
                "count(1) as count\n" +
                "FROM po_praybill_b_copy1 ppb\n" +
                "LEFT JOIN sm_user u ON ppb.p_purchaser = u.CUSERID\n" +
                "WHERE u.USER_NAME IS NOT NULL and ppb.dr = 0 and u.dr = 0 \n" +
                "AND u.USER_NAME !=('answer')\n" +
                "\tAND ppb.p_order_time>='2018-01-01'\n" +
                "GROUP BY\n" +
                "u.CUSERID,\n" +
                "u.USER_NAME,\n" +
                "ppb.c_currentstatus";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<String> userIdList = list.stream().map(item -> (String) item.get("userId")).distinct().collect(Collectors.toList());

        List<Map<String, Object>> dataMapList = new ArrayList<>();
        userIdList.stream().forEach(userId -> {
            List<Map<String, Object>> fileDataList = list.stream().filter(item -> StringUtils.equals(userId, (String) item.get("userId"))).collect(Collectors.toList());
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("userId", userId);
            dataMap.put("userName", fileDataList.get(0).getOrDefault("userName", ""));

           /* // 0 其他
            Optional<Map<String, Object>> optional1 = fileDataList.stream().filter(item -> 0 == (Integer) item.get("status")).findFirst();
            dataMap.put("count1", optional1.isPresent() ? optional1.get().getOrDefault("count", 0) : 0);*/

            //1订单未接收
            Optional<Map<String, Object>> optional2 = fileDataList.stream().filter(item -> 1 == (Integer) item.get("status")).findFirst();
            dataMap.put("count2", optional2.isPresent() ? optional2.get().getOrDefault("count", 0) : 0);

            //2 完全到货3
            Optional<Map<String, Object>> optional3 = fileDataList.stream().filter(item -> 2 == (Integer) item.get("status")).findFirst();
            dataMap.put("count3", optional3.isPresent() ? optional3.get().getOrDefault("count", 0) : 0);

            //3 采购过程中
            Optional<Map<String, Object>> optional4 = fileDataList.stream().filter(item -> 3 == (Integer) item.get("status")).findFirst();
            dataMap.put("count4", optional4.isPresent() ? optional4.get().getOrDefault("count", 0) : 0);

            //4 临近到货
            Optional<Map<String, Object>> optional5 = fileDataList.stream().filter(item -> 4 == (Integer) item.get("status")).findFirst();
            dataMap.put("count5", optional5.isPresent() ? optional5.get().getOrDefault("count", 0) : 0);

            //5 部分到货
            Optional<Map<String, Object>> optional6 = fileDataList.stream().filter(item -> 5 == (Integer) item.get("status")).findFirst();
            dataMap.put("count6", optional6.isPresent() ? optional6.get().getOrDefault("count", 0) : 0);

            //6 逾期未到货
            Optional<Map<String, Object>> optional7 = fileDataList.stream().filter(item -> 6 == (Integer) item.get("status")).findFirst();
            dataMap.put("count7", optional7.isPresent() ? optional7.get().getOrDefault("count", 0) : 0);

            dataMapList.add(dataMap);
        });
        map.put("data", dataMapList);
        return map;
    }

    /***********采购部领域管理员之我的看板之明细 卫星 运载 战术 空间 其他**********/

    public Map<String, Object> cgyleaderdetail1(Integer region) {

        Map<String, Object> map = new HashMap<>();
        List<Map<String, String>> schemas = new ArrayList<>();

        Map<String, String> schema1 = new HashMap<>();
        schema1.put("field", "userName");
        schema1.put("title", "姓名");
        schema1.put("type", "dimension");

        Map<String, String> schema2 = new HashMap<>();
        schema2.put("field", "count1");
        schema2.put("title", "订单未接收");
        schema2.put("type", "measure");

        Map<String, String> schema3 = new HashMap<>();
        schema2.put("field", "count2");
        schema2.put("title", "完全到货");
        schema2.put("type", "measure");

        Map<String, String> schema4 = new HashMap<>();
        schema4.put("field", "count2");
        schema4.put("title", "采购过程中");
        schema4.put("type", "measure");

        Map<String, String> schema5 = new HashMap<>();
        schema5.put("field", "count2");
        schema5.put("title", "临近到货");
        schema5.put("type", "measure");

        Map<String, String> schema6 = new HashMap<>();
        schema6.put("field", "count2");
        schema6.put("title", "部分到货");
        schema6.put("type", "measure");

        Map<String, String> schema7 = new HashMap<>();
        schema7.put("field", "count2");
        schema7.put("title", "逾期未到货");
        schema7.put("type", "measure");

        schemas.add(schema1);
        schemas.add(schema2);
        schemas.add(schema3);
        schemas.add(schema4);
        schemas.add(schema5);
        schemas.add(schema6);
        schemas.add(schema7);
        map.put("schemas", schemas);

        String sql = "SELECT  \n" +
                "u.CUSERID as userId,\n" +
                "u.USER_NAME AS userName,\n" +
                "ppb.c_currentstatus as status ,\n" +
                "count(1) as count\n" +
                "FROM po_praybill_b_copy1 ppb\n" +
                "LEFT JOIN sm_user u ON ppb.p_purchaser = u.CUSERID\n" +
                "WHERE u.USER_NAME IS NOT NULL and ppb.dr = 0 and u.dr = 0 " +
                "AND  ppb.p_DPRAYDATE>='2018-01-01' \n" +
                "AND u.USER_NAME !=('answer')\n" +
                "\tAND c_region= " + region +
                "\tGROUP BY\n" +
                "u.CUSERID,\n" +
                "u.USER_NAME,\n" +
                "ppb.c_currentstatus";

        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        List<String> userIdList = list.stream().map(item -> (String) item.get("userId")).distinct().collect(Collectors.toList());

        List<Map<String, Object>> dataMapList = new ArrayList<>();
        userIdList.stream().forEach(userId -> {
            List<Map<String, Object>> fileDataList = list.stream().filter(item -> StringUtils.equals(userId, (String) item.get("userId"))).collect(Collectors.toList());
            Map<String, Object> dataMap = new HashMap<>();
            dataMap.put("userId", userId);
            dataMap.put("userName", fileDataList.get(0).getOrDefault("userName", ""));

            // 0 其他
            Optional<Map<String, Object>> optional1 = fileDataList.stream().filter(item -> 0 == (Integer) item.get("status")).findFirst();
            dataMap.put("count1", optional1.isPresent() ? optional1.get().getOrDefault("count", 0) : 0);

            //1订单未接收
            Optional<Map<String, Object>> optional2 = fileDataList.stream().filter(item -> 1 == (Integer) item.get("status")).findFirst();
            dataMap.put("count2", optional2.isPresent() ? optional2.get().getOrDefault("count", 0) : 0);

            //2 完全到货3
            Optional<Map<String, Object>> optional3 = fileDataList.stream().filter(item -> 2 == (Integer) item.get("status")).findFirst();
            dataMap.put("count3", optional3.isPresent() ? optional3.get().getOrDefault("count", 0) : 0);

            //3 采购过程中
            Optional<Map<String, Object>> optional4 = fileDataList.stream().filter(item -> 3 == (Integer) item.get("status")).findFirst();
            dataMap.put("count4", optional4.isPresent() ? optional4.get().getOrDefault("count", 0) : 0);

            //4 临近到货
            Optional<Map<String, Object>> optional5 = fileDataList.stream().filter(item -> 4 == (Integer) item.get("status")).findFirst();
            dataMap.put("count5", optional5.isPresent() ? optional5.get().getOrDefault("count", 0) : 0);

            //5 部分到货
            Optional<Map<String, Object>> optional6 = fileDataList.stream().filter(item -> 5 == (Integer) item.get("status")).findFirst();
            dataMap.put("count6", optional6.isPresent() ? optional6.get().getOrDefault("count", 0) : 0);

            //6 逾期未到货
            Optional<Map<String, Object>> optional7 = fileDataList.stream().filter(item -> 6 == (Integer) item.get("status")).findFirst();
            dataMap.put("count7", optional7.isPresent() ? optional7.get().getOrDefault("count", 0) : 0);

            dataMapList.add(dataMap);
        });
        map.put("data", dataMapList);
        return map;
    }

    /**
     * 最新数据更新时间
     */
    @SneakyThrows
    public String getLastUpdateDate() {
        String sql = "select DORDERDATE from po_order order by DORDERDATE desc limit 1";
        String dateStr = jdbcTemplate.queryForObject(sql, String.class);
        Date date = DateUtils.parseDate(dateStr, DATE_FORMAT);
        date = DateUtils.addDays(date, -1);
        return DateFormatUtils.format(date, DATE_FORMAT);
    }

}
