package com.plover.authorize.service.impl;

import com.mongodb.client.result.UpdateResult;
import com.plover.authorize.data.WarnTaskMsgData;
import com.plover.authorize.form.WarnTaskMsgQueryForm;
import com.plover.authorize.service.WarnTaskMsgService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WarnTaskMsgServiceImpl implements WarnTaskMsgService {


    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 列表查询
     *
     * @param queryForm
     * @return
     */
    @Override
    public List<WarnTaskMsgData> list(WarnTaskMsgQueryForm queryForm) {
        Integer staffId = queryForm.getStaffId();
        Integer status = queryForm.getStatus();
        Criteria criteria = new Criteria();
        if (staffId != null) {
            criteria.and("staffId").is(staffId);
        }
        if (status != null) {
            criteria.and("status").is(status);
        }
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "createDate"))
                .skip(queryForm.getStart()).limit(queryForm.getLimit());
        return mongoTemplate.find(query, WarnTaskMsgData.class);
    }

    /**
     * count
     *
     * @param queryForm
     * @return
     */
    @Override
    public int count(WarnTaskMsgQueryForm queryForm) {
        Integer staffId = queryForm.getStaffId();
        Integer status = queryForm.getStatus();
        Criteria criteria = new Criteria();
        if (staffId != null) {
            criteria.and("staffId").is(staffId);
        }
        if (status != null) {
            criteria.and("status").is(status);
        }
        return (int) mongoTemplate.count(Query.query(criteria), WarnTaskMsgData.class);
    }

    /**
     * 根据id 查询
     *
     * @param id
     * @return
     */
    @Override
    public WarnTaskMsgData findById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return mongoTemplate.findById(id, WarnTaskMsgData.class);
    }

    /**
     * 新增
     *
     * @param taskMsgData
     * @return
     */
    @Override
    public boolean add(WarnTaskMsgData taskMsgData) {
        mongoTemplate.insert(taskMsgData);
        return StringUtils.isNotBlank(taskMsgData.getId());
    }

    /**
     * @param id
     * @param status 0:未读 1:已读
     * @return
     */
    @Override
    public boolean updateStatus(String id, int status) {
        Update update = Update.update("updateDate", new Date())
                .set("status", status);
        Query query = Query.query(Criteria.where("id").is(id));
        UpdateResult result = mongoTemplate.updateFirst(query, update, WarnTaskMsgData.class);
        return result.getModifiedCount() > 0;
    }
}
