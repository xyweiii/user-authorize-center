package com.plover.authorize.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import com.plover.authorize.data.RoleAppResourceData;
import com.plover.authorize.service.RoleAppResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Project:user-authorize-center
 * Package: com.plover.authorize.service.impl
 *
 * @author : xywei
 * @date : 2021-01-26
 */
@Service
public class RoleAppResourceServiceImpl implements RoleAppResourceService {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 根据id 查询
     *
     * @param id
     * @return
     */
    @Override
    public RoleAppResourceData findById(String id) {
        if (StringUtils.isBlank(id)) {
            return null;
        }
        return mongoTemplate.findById(id, RoleAppResourceData.class);
    }

    /**
     * 根据 roleId appId 查询
     *
     * @param roleId
     * @param appId
     * @return
     */
    @Override
    public RoleAppResourceData findByRoleIdAndAppId(Integer roleId, Integer appId) {
        if (roleId == null || appId == null) {
            return null;
        }
        Criteria criteria = Criteria.where("roleId").is(roleId)
                .and("appId").is(appId);
        return mongoTemplate.findOne(Query.query(criteria), RoleAppResourceData.class);
    }

    /**
     * 新增
     *
     * @param roleAppResource
     * @return
     */
    @Override
    public boolean add(RoleAppResourceData roleAppResource) {
        mongoTemplate.insert(roleAppResource);
        return StringUtils.isNotBlank(roleAppResource.getId());
    }

    /**
     * 更新
     *
     * @param roleAppResource
     * @return
     */
    @Override
    public boolean update(RoleAppResourceData roleAppResource) {
        Update update = Update.update("updateDate", new Date())
                .set("updateBy", roleAppResource.getUpdateBy())
                .set("resources", roleAppResource.getResources());
        Criteria criteria = Criteria.where("roleId").is(roleAppResource.getRoleId())
                .and("appId").is(roleAppResource.getAppId());
        Query query = Query.query(criteria);
        UpdateResult result = mongoTemplate.updateFirst(query, update, RoleAppResourceData.class);
        return result.getModifiedCount() > 0;
    }

    /**
     * 删除
     *
     * @param id
     * @return
     */
    @Override
    public boolean deleteById(String id) {
        if (StringUtils.isBlank(id)) {
            return false;
        }
        Query query = Query.query(Criteria.where("id").is(id));
        DeleteResult result = mongoTemplate.remove(query, RoleAppResourceData.class);
        return result.getDeletedCount() > 0;
    }
}
