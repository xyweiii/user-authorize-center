package com.plover.authorize.entity;

import com.plover.authorize.common.BaseEntity;
import com.plover.authorize.model.StaffRole;
import lombok.Data;

import java.util.List;

/**
 * Project:user-center
 * Package: com.plover.user.entity
 *
 * @author : xywei
 * @date : 2021-01-14
 */
@Data
public class StaffEntity extends BaseEntity {

    /**
     * 用户名: 内部员工登陆使用的名称 如: yuanchengzong
     */
    private String userName;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 真实姓名
     */
    private String realName;

    /**
     * 身份证号
     */
    private String idNumber;

    /**
     * 手机号码
     */
    private String mobile;

    /**
     * 性别
     */
    private String sex;

    /**
     * 出生日期
     */
    private String birthDay;

    /**
     * 户籍所在地编码
     */
    private String ntPlaceCode;

    /**
     * 户籍所在地名称
     */
    private String ntPlaceName;

    /**
     * 出生地地编码
     */
    private String birthPlaceCode;

    /**
     * 出生地名称
     */
    private String birthPlaceName;

    /**
     * 民族
     */
    private String nation;

    /**
     * 婚姻情况
     */
    private String marriage;

    /**
     * 户口所在地
     */
    private String permanreside;

    /**
     * 居住地址
     */
    private String address;

    /**
     * 技术方向
     */
    private String technical;

    /**
     * 角色id, 逗号隔开
     */
    private String role;

    /**
     * role 信息
     */
    private List<StaffRole> roleList;

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
