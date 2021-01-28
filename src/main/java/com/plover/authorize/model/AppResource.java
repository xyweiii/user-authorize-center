package com.plover.authorize.model;

import com.plover.authorize.common.BaseModel;
import lombok.Data;

/**
 * Project:user-authorize-center
 * Package: com.plover.authorize.model
 *
 * @author : xywei
 * @date : 2021-01-25
 */
@Data
public class AppResource extends BaseModel {

    /**
     * @see App#getAppId()
     */
    private Integer appId;

    /**
     * 资源描述
     */
    private String resource;

    /**
     * 描述
     */
    private String description;

    /**
     * 是否删除
     * 0: 未删除
     * 1: 已删除
     */
    private int deleted;

}
