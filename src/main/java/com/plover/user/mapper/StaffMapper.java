package com.plover.user.mapper;

import com.plover.user.form.StaffQueryForm;
import com.plover.user.model.Staff;
import com.plover.user.model.User;
import org.apache.ibatis.annotations.Mapper;
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
     * @param user
     * @return
     */
    int add(Staff user);

    /**
     * 更新
     *
     * @param user
     * @return
     */
    int update(Staff user);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    int deleteById(Integer id, String updateBy);
}