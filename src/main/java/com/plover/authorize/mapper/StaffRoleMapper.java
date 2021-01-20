package com.plover.authorize.mapper;

import com.plover.authorize.model.StaffRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Mapper
public interface StaffRoleMapper {

    /**
     * 角色列表
     *
     * @return
     */
    List<StaffRole> list();

    /**
     * 根据ID 查询
     *
     * @param id
     * @return
     */
    StaffRole findById(@Param("id") Integer id);

    /**
     * 新增
     *
     * @param staffRole
     * @return
     */
    int add(StaffRole staffRole);

    /**
     * 更新
     *
     * @param staffRole
     * @return
     */
    int update(StaffRole staffRole);

    /**
     * 删除
     *
     * @param id
     * @param updateBy
     * @return
     */
    int deleteById(@Param("id") Integer id, @Param("updateBy") String updateBy);
}