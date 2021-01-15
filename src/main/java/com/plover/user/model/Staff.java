package com.plover.user.model;

import com.alibaba.excel.annotation.ExcelProperty;
import com.plover.user.common.BaseModel;
import lombok.Data;

/**
 * Project:user-center
 * Package: com.plover.user.model
 *
 * @author : xywei
 * @date : 2021-01-14
 */

/**
 * 工作人员表
 */
@Data
public class Staff extends BaseModel {

    /**
     * 用户名: 内部员工登陆使用的名称 如: yuanchengzong
     */
    @ExcelProperty(value = "userName")
    private String userName;

    /**
     * 邮箱
     */
    @ExcelProperty(value = "email")
    private String email;

    /**
     * 真实姓名
     */
    @ExcelProperty(value = "realName")
    private String realName;

    /**
     * 身份证号
     */
    @ExcelProperty(value = "idNumber")
    private String idNumber;

    /**
     * 手机号码
     */
    @ExcelProperty(value = "mobile")
    private String mobile;

    /**
     * 性别
     */
    @ExcelProperty(value = "sex")
    private String sex;

    /**
     * 出生日期
     */
    @ExcelProperty(value = "birthDay")
    private String birthDay;

    /**
     * 户籍所在地编码
     */
    @ExcelProperty(value = "ntPlaceCode")
    private String ntPlaceCode;

    /**
     * 户籍所在地名称
     */
    @ExcelProperty(value = "ntPlaceName")
    private String ntPlaceName;

    /**
     * 出生地地编码
     */
    @ExcelProperty(value = "birthPlaceCode")
    private String birthPlaceCode;

    /**
     * 出生地名称
     */
    @ExcelProperty(value = "birthPlaceName")
    private String birthPlaceName;

    /**
     * 民族
     */
    @ExcelProperty(value = "nation")
    private String nation;

    /**
     * 婚姻情况
     */
    @ExcelProperty(value = "marriage")
    private String marriage;

    /**
     * 户口所在地
     */
    @ExcelProperty(value = "permanreside")
    private String permanreside;

    /**
     * 居住地址
     */
    @ExcelProperty(value = "address")
    private String address;

    /**
     * 技术方向
     */
    @ExcelProperty(value = "technical")
    private String technical;

    /**
     * 状态
     * 0: 正常
     * 1: 禁用
     */
    private int status;

    /**
     * 删除
     * 0:未删除
     * 1:已删除
     */
    private int deleted;
}
