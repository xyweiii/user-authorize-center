package com.plover.authorize.biz;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.plover.authorize.common.Constants;
import com.plover.authorize.model.Staff;
import com.plover.authorize.service.StaffService;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Project:user-center
 * Package: com.plover.user.biz
 *
 * @author : xywei
 * @date : 2021-01-14
 */
@Slf4j
public class StaffImportListener extends AnalysisEventListener<Staff> {

    /**
     * 每隔5条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 2000;

    List<Staff> list = new ArrayList<>();

    private StaffService staffService;

    /**
     * @param staffService
     */
    public StaffImportListener(StaffService staffService) {
        this.staffService = staffService;
    }

    /**
     * 这个每一条数据解析都会来调用
     *
     * @param
     * @param context
     */
    @Override
    public void invoke(Staff staff, AnalysisContext context) {
        list.add(staff);
        // 达到BATCH_COUNT了，需要去存储一次数据库，防止数据几万条数据在内存，容易OOM
        if (list.size() >= BATCH_COUNT) {
            addStaff();
            // 存储完成清理 list
            list.clear();
        }
    }

    /**
     * 所有数据解析完成了 都会来调用
     *
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        // 这里也要保存数据，确保最后遗留的数据也存储到数据库
        addStaff();
        log.info("所有数据解析完成！");
    }


    /**
     * 新增staff
     */
    private synchronized void addStaff() {
        log.info("{}条数据，开始存储数据库！", list.size());
        for (Staff data : list) {
            Staff staff = staffService.findByUserName(data.getUserName());
            if (staff == null) {
                data.setCreateBy(Constants.SYSTEM_DEFAULT_USER);
                data.setUpdateBy(Constants.SYSTEM_DEFAULT_USER);
                staffService.add(data);
            }
        }
        log.info("存储数据库成功！");
    }
}
