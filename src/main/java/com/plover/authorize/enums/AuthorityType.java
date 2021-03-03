package com.plover.authorize.enums;

import lombok.Getter;

/**
 * Project:user-authorize-center
 * Package: com.plover.authorize
 * <p>
 * 鉴权类型 枚举类
 *
 * @author : xywei
 * @date : 2021-02-23
 */
@Getter
public enum AuthorityType {

    /**
     * 查询
     */
    QUERY("查看", 1),

    /**
     * 新增
     */
    ADD("新增", 2),

    /**
     * 修改
     */
    UPDATE("修改", 3),

    /**
     * 删除
     */
    DELETE("删除", 4);

    private String name;

    private Integer code;

    AuthorityType(String name, int code) {
        this.name = name;
        this.code = code;
    }
}

