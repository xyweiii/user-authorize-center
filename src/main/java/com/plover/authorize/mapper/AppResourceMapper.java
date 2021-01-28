package com.plover.authorize.mapper;

import com.plover.authorize.form.AppResourceQueryForm;
import com.plover.authorize.model.AppResource;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Mapper
@Repository
public interface AppResourceMapper {

    /**
     * list 列表查询
     *
     * @param queryForm
     * @return
     */
    List<AppResource> list(AppResourceQueryForm queryForm);

    /**
     * count
     *
     * @param queryForm
     * @return
     */
    int count(AppResourceQueryForm queryForm);

    /**
     * 根据id 查询
     *
     * @param id
     * @return
     */
    AppResource findById(@Param("id") Integer id);

    /**
     * 根据 appId 查询
     *
     * @param appId
     * @return
     */
    List<AppResource> findByAppId(@Param("appId") Integer appId);

    /**
     * 新增
     *
     * @param roleResource
     * @return
     */
    int add(AppResource roleResource);

    /**
     * 更新
     *
     * @param roleResource
     * @return
     */
    int update(AppResource roleResource);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int deleteById(@Param("id") Integer id, @Param("updateBy") String updateBy);
}