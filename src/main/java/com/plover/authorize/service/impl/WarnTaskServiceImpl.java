package com.plover.authorize.service.impl;

import com.mongodb.client.result.UpdateResult;
import com.plover.authorize.data.WarnTaskData;
import com.plover.authorize.service.WarnTaskService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class WarnTaskServiceImpl implements WarnTaskService {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 列表查询
     *
     * @return
     */
    @Override
    public List<WarnTaskData> list() {
        Query query = Query.query(Criteria.where("deleted").is(0));
        return mongoTemplate.find(query, WarnTaskData.class);
    }

    /**
     * 根据id 查询
     *
     * @param id
     * @return
     */
    @Override
    public WarnTaskData findById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return mongoTemplate.findById(id, WarnTaskData.class);
    }

    /**
     * 新增
     *
     * @param warnTask
     * @return
     */
    @Override
    public boolean add(WarnTaskData warnTask) {
        mongoTemplate.insert(warnTask);
        return StringUtils.isNotBlank(warnTask.getId());
    }

    /**
     * 更新
     *
     * @param warnTask
     * @return
     */
    @Override
    public boolean update(WarnTaskData warnTask) {
        Update update = Update.update("updateDate", new Date())
                .set("appId", warnTask.getAppId())
                .set("name", warnTask.getName())
                .set("sql", warnTask.getSql())
                .set("roleIds", warnTask.getRoleIds())
                .set("pushTemplate", warnTask.getPushTemplate())
                .set("warnLevel", warnTask.getWarnLevel())
                .set("desc", warnTask.getDesc());
        Query query = Query.query(Criteria.where("id").is(warnTask.getId()));
        UpdateResult result = mongoTemplate.updateFirst(query, update, WarnTaskData.class);
        return result.getModifiedCount() > 0;
    }

    /**
     * 停用
     *
     * @param id
     * @return
     */
    @Override
    public boolean updateStatus(String id, int status) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        Update update = Update.update("updateDate", new Date())
                .set("status", status);
        Query query = Query.query(Criteria.where("id").is(id));
        UpdateResult result = mongoTemplate.updateFirst(query, update, WarnTaskData.class);
        return result.getModifiedCount() > 0;
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @Override
    public boolean delete(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        Update update = Update.update("updateDate", new Date())
                .set("deleted", 1);
        Query query = Query.query(Criteria.where("id").is(id));
        UpdateResult result = mongoTemplate.updateFirst(query, update, WarnTaskData.class);
        return result.getModifiedCount() > 0;
    }
}
