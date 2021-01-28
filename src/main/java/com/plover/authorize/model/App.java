package com.plover.authorize.model;

import com.plover.authorize.common.BaseModel;
import lombok.Data;

/**
 * @author xywei
 * app应用
 */

@Data
public class App extends BaseModel {

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用地址
     */
    private String appUrl;

    /**
     * 应用唯一标识
     */
    private String appId;

    /**
     * 描述
     */
    private String description;

    /**
     * app 授权哪些角色
     * 角色id,逗号隔开
     */
    private String role;

    /**
     * 删除
     * 0:未删除
     * 1:已删除
     */
    private int deleted;
}