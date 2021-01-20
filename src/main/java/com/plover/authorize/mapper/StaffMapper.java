package com.plover.authorize.mapper;

import com.plover.authorize.form.StaffQueryForm;
import com.plover.authorize.model.Staff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * staff 工作人员相关mapper
 *
 * @author xywei
 */
@Repository
@Mapper
public interface StaffMapper {

    /**
     * list
     *
     * @param queryForm
     * @return
     */
    List<Staff> list(StaffQueryForm queryForm);

    /**
     * count
     *
     * @param queryForm
     * @return
     */
    int count(StaffQueryForm queryForm);

    /**
     * 根据id 查找
     *
     * @param id
     * @return
     */
    Staff findById(Integer id);

    /**
     * 根据userName进行查找
     *
     * @param userName
     * @return
     */
    Staff findByUserName(String userName);

    /**
     * 新增
     *
     * @param staff
     * @return
     */
    int add(Staff staff);

    /**
     * 更新
     *
     * @param staff
     * @return
     */
    int update(Staff staff);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int deleteById(@Param("id") Integer id, @Param("updateBy") String updateBy);
}