package com.plover.authorize.controller;


import com.plover.authorize.biz.SqlApiBiz;
import com.plover.authorize.common.HttpBizCode;
import com.plover.authorize.common.Response;
import com.plover.authorize.entity.SqlApiEntity;
import com.plover.authorize.model.SqlApi;
import com.plover.authorize.model.Staff;
import com.plover.authorize.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project:p804-sql-api
 * Package: com.x04.sql.controller
 *
 * @author : xywei
 * @date : 2021-03-01
 */
@RestController
@RequestMapping("/sqlApi")
@Slf4j
public class SqlApiController {

    @Autowired
    private SqlApiBiz sqlApiBiz;

    /**
     * 列表查询
     *
     * @return
     */
    @GetMapping("/list")
    public Response<List<SqlApiEntity>> list() {
        Response<List<SqlApiEntity>> resp = new Response<>();
        try {
            List<SqlApiEntity> entityList = sqlApiBiz.list();
            resp.setData(entityList);
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("sqlApi list occur error,", e);
        }
        return resp;
    }

    /**
     * 新增
     *
     * @param sqlApi
     * @return
     */
    @PostMapping("/add")
    public Response<Boolean> add(@RequestBody SqlApi sqlApi, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");

            sqlApi.setCreateBy(user.getUserName());
            sqlApi.setUpdateBy(user.getUserName());
            int result = sqlApiBiz.add(sqlApi);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("新增失败");
            }
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("sqlApi add occur error,", e);
        }
        return resp;
    }

    /**
     * 更新
     *
     * @param sqlApi
     * @return
     */
    @PostMapping("/update")
    public Response<Boolean> update(@RequestBody SqlApi sqlApi, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            User user = (User) request.getSession().getAttribute("user");
            sqlApi.setUpdateBy(user.getUserName());
            int result = sqlApiBiz.update(sqlApi);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("更新失败");
            }
        } catch (Exception e) {
            resp.setCode(HttpBizCode.BIZERROR.getCode());
            resp.setMessage(e.getMessage());
            log.error("sqlApi update occur error,", e);
        }
        return resp;
    }

    /**
     * 删除
     *
     * @param id
     * @param request
     * @return
     */
    @PostMapping(value = "/delete")
    public Response<Boolean> delete(@RequestParam Integer id, HttpServletRequest request) {
        Response<Boolean> resp = new Response<>();
        try {
            int result = sqlApiBiz.deleteById(id);
            if (result == 0) {
                resp.setCode(HttpBizCode.BIZERROR.getCode());
                resp.setMessage("删除失败");
            }
        } catch (Exception e) {
            log.error("app delete occur error", e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }

    /**
     * @param id
     * @return
     */
    @GetMapping(value = "/query/{id}")
    public Response<Object> query(@PathVariable("id") Integer id) {
        Response<Object> resp = new Response<>();
        try {
            List<Map<String, Object>> result = sqlApiBiz.query(id);
            resp.setData(result);
        } catch (Exception e) {
            log.error("sqlApi  query occur error,id:{}", id, e);
            resp.setCode(HttpBizCode.SYSERROR.getCode());
            resp.setMessage(e.getMessage());
        }
        return resp;
    }


/**********************采购员****************************/


    /**
     * 采购员-根据状态统计各订单数量
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgy/countByStatus")
    public Map<String, Object> caigouyuan1(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            //获取 userId
            Staff staff = (Staff) request.getSession().getAttribute("user");
            String userId = sqlApiBiz.getUserIdByPsnCode(staff.getPsnCode());
            map = sqlApiBiz.caigouyuan1(userId);
        } catch (Exception e) {
            log.error("sqlApi  cgy countByStatus  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购员- 未到货情况 比对统计
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgy/noArrived")
    public Map<String, Object> caigouyuan2(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            //获取 userId
            Staff staff = (Staff) request.getSession().getAttribute("user");
            String userId = sqlApiBiz.getUserIdByPsnCode(staff.getPsnCode());
            return sqlApiBiz.caigouyuan2(userId);
        } catch (Exception e) {
            log.error("sqlApi  cgy noArrived  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购员-逾期统计情况
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgy/yuqi")
    public Map<String, Object> caigouyuan3(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            //获取 userId
            Staff staff = (Staff) request.getSession().getAttribute("user");
            String userId = sqlApiBiz.getUserIdByPsnCode(staff.getPsnCode());
            return sqlApiBiz.caigouyuan3(userId);
        } catch (Exception e) {
            log.error("sqlApi  cgy noArrived  query occur error,", e);
        }
        return map;
    }


    /**
     * 采购员之未到货看到之逾期未到货详细
     *
     * @return
     */
    @GetMapping(value = "/cgy/yuqinolist")
    public Map<String, Object> cgy1(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            //获取 userId
            Staff staff = (Staff) request.getSession().getAttribute("user");
            String userId = sqlApiBiz.getUserIdByPsnCode(staff.getPsnCode());
            return sqlApiBiz.cgy1(userId);
        } catch (Exception e) {
            log.error("sqlApi cgy yuqinolist query occur error,", e);
        }
        return map;
    }

    /**
     * 采购员之未到货看到之部分到货详细
     *
     * @return
     */
    @GetMapping(value = "/cgy/partialarrivallist")
    public Map<String, Object> cgy2(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            //获取 userId
            Staff staff = (Staff) request.getSession().getAttribute("user");
            String userId = sqlApiBiz.getUserIdByPsnCode(staff.getPsnCode());
            return sqlApiBiz.cgy2(userId);
        } catch (Exception e) {
            log.error("sqlApi cgy partialarrivallist query occur error,", e);
        }
        return map;
    }

    /**
     * 采购员之未到货看到之临近到货详细
     *
     * @return
     */
    @GetMapping(value = "/cgy/neararrivallist")
    public Map<String, Object> cgy3(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            //获取 userId
            Staff staff = (Staff) request.getSession().getAttribute("user");
            String userId = sqlApiBiz.getUserIdByPsnCode(staff.getPsnCode());
            return sqlApiBiz.cgy3(userId);
        } catch (Exception e) {
            log.error("sqlApi cgy neararrivallist query occur error,", e);
        }
        return map;
    }

    /**
     * 采购员之未到货看到之采购过程中详细
     *
     * @return
     */
    @GetMapping(value = "/cgy/cgprocesslist")
    public Map<String, Object> cgy4(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            //获取 userId
            Staff staff = (Staff) request.getSession().getAttribute("user");
            String userId = sqlApiBiz.getUserIdByPsnCode(staff.getPsnCode());
            return sqlApiBiz.cgy4(userId);
        } catch (Exception e) {
            log.error("sqlApi cgy cgprocesslist query occur error,", e);
        }
        return map;
    }

    /**
     * 采购员之未到货看到之完全到货详细
     *
     * @return
     */
    @GetMapping(value = "/cgy/fullarrivallist")
    public Map<String, Object> cgy5(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            //获取 userId
            Staff staff = (Staff) request.getSession().getAttribute("user");
            String userId = sqlApiBiz.getUserIdByPsnCode(staff.getPsnCode());
            return sqlApiBiz.cgy5(userId);
        } catch (Exception e) {
            log.error("sqlApi cgy fullarrivallist query occur error,", e);
        }
        return map;
    }

    /**
     * 采购员之未到货看到之订单未接收详细
     *
     * @return
     */
    @GetMapping(value = "/cgy/orderrejectlist")
    public Map<String, Object> cgy6(HttpServletRequest request) {
        Map<String, Object> map = new HashMap<>();
        try {
            //获取 userId
            Staff staff = (Staff) request.getSession().getAttribute("user");
            String userId = sqlApiBiz.getUserIdByPsnCode(staff.getPsnCode());
            return sqlApiBiz.cgy6(userId);
        } catch (Exception e) {
            log.error("sqlApi cgy orderrejectlist query occur error,", e);
        }
        return map;
    }

//    /**
//     * 采购员之未到货看到之其他详细
//     *
//     * @return
//     */
//    @GetMapping(value = "/cgy/otherlist")
//    public Map<String, Object> cgy7(@RequestParam(value = "purchaser", required = false) String purchaser) {
//        Map<String, Object> map = new HashMap<>();
//        try {
//            return sqlApiBiz.cgy7(purchaser);
//        } catch (Exception e) {
//            log.error("sqlApi cgy yuqinoArrivedList query occur error,", e);
//        }
//        return map;
//    }


/**************************采购部领导*******************/

    /**
     * 采购部领导- 根据 领域,订单状态统计
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgbld/countByRegionAndStatus")
    public Map<String, Object> cgbld1() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld1();
        } catch (Exception e) {
            log.error("sqlApi  cgbld countByRegionAndStatus  query occur error,", e);
        }
        return map;
    }


    /**
     * 采购部领导- 采购统计
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgbld/cgByUser")
    public Map<String, Object> cgbld2() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld2();
        } catch (Exception e) {
            log.error("sqlApi  cgbld countByUser  query occur error,", e);
        }
        return map;
    }


    /**
     * 采购部领导- 检验统计
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgbld/jyByUser")
    public Map<String, Object> cgbld3() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld3();
        } catch (Exception e) {
            log.error("sqlApi  cgbld jyByUser  query occur error,", e);
        }
        return map;
    }


    /**
     * 采购部领导- 厂家合同top20
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgbld/cjhtTop20")
    public Map<String, Object> cgbld4() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld4();
        } catch (Exception e) {
            log.error("sqlApi  cgbld jyByUser  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购部领导- 元器件规格 top20
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgbld/yqjTop20")
    public Map<String, Object> cgbld5() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld5();
        } catch (Exception e) {
            log.error("sqlApi  cgbld yqjTop20  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购部领导 订单未接收     运载 战术武器  其他
     **/

    @GetMapping(value = "/cgbld/orderrefuse")
    public Map<String, Object> cgbld01(@RequestParam(value = "region", required = false) Integer region) {
        Map<String, Object> map = new HashMap<>();
        try {
            return this.sqlApiBiz.cgbld01(region);
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购部领导 完全到货       运载 战术武器  其他
     **/
    @GetMapping(value = "/cgbld/fullarrival")
    public Map<String, Object> cgbld02(@RequestParam(value = "region", required = false) Integer region) {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld02(region);
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购部领导 采购过程中    运载 战术武器  其他
     **/
    @GetMapping(value = "/cgbld/cgprocess")
    public Map<String, Object> cgbld03(@RequestParam(value = "region", required = false) Integer region) {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld03(region);
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }


    /**
     * 采购部领导 临近到货    运载 战术武器  其他
     **/
    @GetMapping(value = "/cgbld/neararrival")
    public Map<String, Object> cgbld04(@RequestParam(value = "region", required = false) Integer region) {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld04(region);
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }


    /**
     * 采购部领导 部分到货    运载 战术武器  其他
     **/
    @GetMapping(value = "/cgbld/partialarrival")
    public Map<String, Object> cgbld05(@RequestParam(value = "region", required = false) Integer region) {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld05(region);
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购部领导 逾期未到货     运载 战术武器  其他
     **/
    @GetMapping(value = "/cgbld/yuqino")
    public Map<String, Object> cgbld06(@RequestParam(value = "region", required = false) Integer region) {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld06(region);
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购部领导 其他      运载 战术武器  其他
     **/
    @GetMapping(value = "/cgbld/other")
    public Map<String, Object> cgbld07(@RequestParam(value = "region", required = false) Integer region) {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld07(region);
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }


    /**
     * 采购部领导 订单未接收 空间平台
     **/

    @GetMapping(value = "/cgbld/orderrefuseroom")
    public Map<String, Object> cgbld21() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld21();
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }


    /**
     * 采购部领导 完全到货 空间平台
     **/
    @GetMapping(value = "/cgbld/fullarrivalroom")
    public Map<String, Object> cgbld22() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld22();
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }


    /**
     * 采购部领导 采购过程中 空间平台
     **/
    @GetMapping(value = "/cgbld/cgprocessroom")
    public Map<String, Object> cgbld23() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld23();
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }


    /**
     * 采购部领导 临近到货 空间平台
     **/
    @GetMapping(value = "/cgbld/neararrivalroom")
    public Map<String, Object> cgbld24() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld24();
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }


    /**
     * 采购部领导 部分到货 空间平台
     **/
    @GetMapping(value = "/cgbld/partialarrivalroom")
    public Map<String, Object> cgbld25() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld25();
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购部领导 逾期未到货 空间平台
     **/
    @GetMapping(value = "/cgbld/yuqinoroom")
    public Map<String, Object> cgbld26() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld26();
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购部领导 其他 空间平台
     **/
    @GetMapping(value = "/cgbld/otherroom")
    public Map<String, Object> cgbld27() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgbld27();
        } catch (Exception e) {
            log.error("sqlApi  cgbld orderrefuseyunzai  query occur error,", e);
        }
        return map;
    }


/*******************采购部领域管理员*********/


    /**
     * 采购部领域管理员- 根据状态,领域统计
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgblyAdmin/countByStatusAndRegion")
    public Map<String, Object> cgblyAdmin1() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgblyAdmin1();
        } catch (Exception e) {
            log.error("sqlApi  cgbld countByStatusAndRegion  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购部领域管理员- 根据状态统计、以及根据领域，这个是全部,
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgblyAdmin/countByStatus")
    public Map<String, Object> cgblyAdmin2() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgblyAdmin2();
        } catch (Exception e) {
            log.error("sqlApi  cgbld yuqi  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购部领域管理员- 根据状态统计、以及根据领域，需要传参,这个是卫星、运载、战术、空间、其他
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgblyAdmin/countByStatus1")
    public Map<String, Object> cgblyAdmin21(@RequestParam(value = "region", required = false) Integer region) {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgblyAdmin21(region);
        } catch (Exception e) {
            log.error("sqlApi  cgbld yuqi  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购部领域管理员- 根据状态统计
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgblyAdmin/yuqi")
    public Map<String, Object> cgblyAdmin3() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgblyAdmin3();
        } catch (Exception e) {
            log.error("sqlApi  cgbld yuqi  query occur error,", e);
        }
        return map;
    }


    /**
     * 采购部领域管理员- 根据采购员,状态,从前端接收领域参数，去查看明细
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgblyAdmin/detail/countByUserAndStatus")
    public Map<String, Object> cgblyAdmin4(@RequestParam(value = "region", required = false) Integer region) {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgblyAdmin4(region);
        } catch (Exception e) {
            log.error("sqlApi  cgbld countByUserAndRegion  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购部领域管理员- 根据采购员,领域,状态 统计
     *
     * @param
     * @return
     */
    @GetMapping(value = "/cgblyAdmin/detail/countByUserAndRegionAndStatus")
    public Map<String, Object> cgblyAdmin5(@RequestParam(value = "region", required = false) Integer region) {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.cgblyAdmin4(region);
        } catch (Exception e) {
            log.error("sqlApi  cgbld countByUserAndRegion  query occur error,", e);
        }
        return map;
    }

    /**
     * 采购部领域管理员之我的看板之明细  全部的不分领域
     *
     * @return
     */

    @GetMapping(value = "/cgblyAdmin/detail/qblistAndRegion")
    public Map<String, Object> cgyly() {
        try {
            return sqlApiBiz.cgyleaderdetail();
        } catch (Exception e) {
            log.error("sqlApi  sybld cjNoArrived  query occur error,", e);
        }
        return new HashMap<>();
    }


    /**
     * 采购部领域管理员之我的看板之明细  卫星 运载 战术  空间 其他
     *
     * @return
     */

    @GetMapping(value = "/cgblyAdmin/detail/listAndRegion")
    public Map<String, Object> cgyly1(@RequestParam(value = "region", required = false) Integer region) {
        try {
            return sqlApiBiz.cgyleaderdetail1(region);
        } catch (Exception e) {
            log.error("sqlApi  sybld cjNoArrived  query occur error,", e);
        }
        return new HashMap<>();
    }


/*******************************事业部领导*****************************/

    /**
     * 事业部领导 - 型号到货率
     *
     * @param
     * @return
     */
    @GetMapping(value = "/sybld/xhdhl")
    public Map<String, Object> sybld1() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.sybld1();
        } catch (Exception e) {
            log.error("sqlApi  sybld xhdhl  query occur error,", e);
        }
        return map;
    }


    /**
     * 事业部领导 - 型号到货明细
     * <p>
     * hzt
     *
     * @param
     * @return
     */
    @GetMapping(value = "/sybld/sybldlistAndspec")
    public Map<String, Object> sybldlist1() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.sybldlistAndspec();
        } catch (Exception e) {
            log.error("sqlApi  sybld xhdhl  query occur error,", e);
        }
        return map;
    }

    /**
     * 事业部领导 - 型号到货明细
     *
     * @param
     * @return
     */
    @GetMapping(value = "/sybld/xhdhllist")
    public Map<String, Object> sybldlist() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.sybldlist();
        } catch (Exception e) {
            log.error("sqlApi  sybld xhdhl  query occur error,", e);
        }
        return map;
    }

    /**
     * 事业部领导 - 领域到货率
     *
     * @param
     * @return
     */
    @GetMapping(value = "/sybld/lydhl")
    public Map<String, Object> lydhl() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.sybld2();
        } catch (Exception e) {
            log.error("sqlApi  sybld lydhl  query occur error,", e);
        }
        return map;
    }


    /**
     * 事业部领导 - 器件逾期时间top10
     *
     * @param
     * @return
     */
    @GetMapping(value = "/sybld/qjyuqi")
    public Map<String, Object> qjyuqi() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.sybld3();
        } catch (Exception e) {
            log.error("sqlApi  sybld qjyuqi  query occur error,", e);
        }
        return map;
    }

    /**
     * 事业部领导 - 厂家未到货top10
     *
     * @param
     * @return
     */
    @GetMapping(value = "/sybld/cjNoArrived")
    public Map<String, Object> cjNoArrived() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.sybld4();
        } catch (Exception e) {
            log.error("sqlApi  sybld cjNoArrived  query occur error,", e);
        }
        return map;
    }

    /**
     * 事业部领导 - 厂家合同金额top10
     *
     * @param
     * @return
     */
    @GetMapping(value = "/sybld/cjhetong")
    public Map<String, Object> cjhetong() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.sybld5();
        } catch (Exception e) {
            log.error("sqlApi  sybld cjNoArrived  query occur error,", e);
        }
        return map;
    }


    /**
     * 所领导 - 型号到货明细
     * <p>
     * hzt
     *
     * @param
     * @return
     */
    @GetMapping(value = "/sld/sybldlistAndspec")
    public Map<String, Object> sldlist() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.sybldlistAndspec();
        } catch (Exception e) {
            log.error("sqlApi  sybld xhdhl  query occur error,", e);
        }
        return map;
    }

    /**
     * 最新数据更新时间
     *
     * @return
     */
    /*@GetMapping(value = "/dateupdate")
    public Map<String, Object> dateupdate() {
        Map<String, Object> map = new HashMap<>();
        try {
            return sqlApiBiz.dateupdate();
        } catch (Exception e) {
            log.error("sqlApi  sybld xhdhl  query occur error,", e);
        }

        return map;
    }*/
    @GetMapping(value = "/dateupdate")
    public String dateupdate() {
        return sqlApiBiz.getLastUpdateDate();
    }


}
