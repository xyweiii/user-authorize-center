package com.plover.authorize.service.impl;

import com.mongodb.client.result.UpdateResult;
import com.plover.authorize.data.RoleAppResourceAuthorityData;
import com.plover.authorize.service.RoleAppResourceAuthorityService;
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
 * @date : 2021-02-23
 */
@Service
public class RoleAppResourceAuthorityServiceImpl implements RoleAppResourceAuthorityService {

    @Autowired
    private MongoTemplate mongoTemplate;


    /**
     * 根据 roleId,resourceId 查询
     *
     * @param roleId
     * @param resourceId
     */
    @Override
    public RoleAppResourceAuthorityData findByRoleIdAndResourceId(Integer roleId, Integer resourceId) {
        if (roleId == null || resourceId == null) {
            return null;
        }
        Criteria criteria = Criteria.where("roleId").is(roleId)
                .and("resourceId").is(resourceId);
        return mongoTemplate.findOne(Query.query(criteria), RoleAppResourceAuthorityData.class);
    }

    /**
     * 新增
     *
     * @param authorityData 权限控制
     * @return
     */
    @Override
    public boolean add(RoleAppResourceAuthorityData authorityData) {
        mongoTemplate.insert(authorityData);
        return StringUtils.isNotBlank(authorityData.getId());
    }

    /**
     * 更新
     *
     * @param authorityData
     * @return
     */
    @Override
    public boolean update(RoleAppResourceAuthorityData authorityData) {
        Update update = Update.update("updateDate", new Date())
                .set("updateBy", authorityData.getUpdateBy())
                .set("authority", authorityData.getAuthority());
        Criteria criteria = Criteria.where("roleId").is(authorityData.getRoleId())
                .and("resourceId").is(authorityData.getResourceId());
        Query query = Query.query(criteria);
        UpdateResult result = mongoTemplate.updateFirst(query, update, RoleAppResourceAuthorityData.class);
        return result.getModifiedCount() > 0;
    }
}
