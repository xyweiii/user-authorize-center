package com.plover.authorize.service.impl;

import com.plover.authorize.data.WarnTaskLogData;
import com.plover.authorize.form.WarnTaskLogQueryForm;
import com.plover.authorize.service.WarnTaskLogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WarnTaskLogServiceImpl implements WarnTaskLogService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 列表查询
     *
     * @param queryForm
     * @return
     */
    @Override
    public List<WarnTaskLogData> list(WarnTaskLogQueryForm queryForm) {
        Integer status = queryForm.getStatus();
        String taskId = queryForm.getTaskId();
        Criteria criteria = new Criteria();
        if (status != null) {
            criteria.and("status").is(status);
        }
        if (StringUtils.isNotBlank(taskId)) {
            criteria.and("taskId").is(taskId);
        }
        Query query = Query.query(criteria).with(Sort.by(Sort.Direction.DESC, "createDate"))
                .skip(queryForm.getStart()).limit(queryForm.getLimit());
        return mongoTemplate.find(query, WarnTaskLogData.class);
    }

    /**
     * count
     *
     * @param queryForm
     * @return
     */
    @Override
    public int count(WarnTaskLogQueryForm queryForm) {
        Integer status = queryForm.getStatus();
        String taskId = queryForm.getTaskId();
        Criteria criteria = new Criteria();
        if (status != null) {
            criteria.and("status").is(status);
        }
        if (StringUtils.isNotBlank(taskId)) {
            criteria.and("taskId").is(taskId);
        }
        return (int) mongoTemplate.count(Query.query(criteria), WarnTaskLogData.class);
    }

    /***
     * 根据 任务Id count
     * @param taskId
     * @return
     */
    @Override
    public int countByTaskId(String taskId) {
        if (StringUtils.isBlank(taskId)) {
            return 0;
        }
        Query query = Query.query(Criteria.where("taskId").is(taskId));
        return (int) mongoTemplate.count(query, WarnTaskLogData.class);
    }

    /**
     * 增加
     *
     * @param taskLogData
     * @return
     */
    @Override
    public boolean add(WarnTaskLogData taskLogData) {
        mongoTemplate.insert(taskLogData);
        return StringUtils.isNotBlank(taskLogData.getId());
    }
}
