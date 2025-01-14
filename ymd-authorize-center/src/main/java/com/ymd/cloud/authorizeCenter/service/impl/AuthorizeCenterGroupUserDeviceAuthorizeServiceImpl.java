package com.ymd.cloud.authorizeCenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ymd.cloud.api.RedisService;
import com.ymd.cloud.api.eventCenter.model.EventCenterPush;
import com.ymd.cloud.api.eventCenter.model.TopicType;
import com.ymd.cloud.api.eventCenter.service.EventCenterServiceClientApi;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterGroupRelationComparisonMapper;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterGroupUserDeviceAuthorizeMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterGroupRelationComparison;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterGroupUserDeviceAuthorize;
import com.ymd.cloud.authorizeCenter.model.entity.*;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterGroupUserDeviceAuthorizeService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeService;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.ClzUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AuthorizeCenterGroupUserDeviceAuthorizeServiceImpl extends ServiceImpl<AuthorizeCenterGroupUserDeviceAuthorizeMapper, AuthorizeCenterGroupUserDeviceAuthorize> implements AuthorizeCenterGroupUserDeviceAuthorizeService {
    @Autowired
    AuthorizeService authorizeService;
    @Resource
    AuthorizeCenterGroupRelationComparisonMapper authorizeCenterGroupRelationComparisonMapper;
    EventCenterServiceClientApi eventCenterServiceClientApi;
    @Autowired
    RedisService redisService;
    @Override
    public JSONObject saveGroupAuth(ChannelAuthorizeEntity channelAuthorizeEntity) {
        JSONObject result = new JSONObject();
        try {
            BaseAuthorizeVo baseAuthorizeVo= channelAuthorizeEntity.getBaseAuthorizeVo();
            baseAuthorizeVo.setIsGroup(1);
            baseAuthorizeVo.setAuthDesc("人员组与设备组授权");
            baseAuthorizeVo.setAuthResource("组授权");
            ChannelAuthGroupVo channelAuthGroupVo = channelAuthorizeEntity.getChannelAuthGroupVo();
            if (EmptyUtil.isNotEmpty(channelAuthGroupVo) && channelAuthGroupVo.getDeviceGroupId() != null && channelAuthGroupVo.getUserGroupId() != null) {
                List<ChannelAuthLockVo> channelAuthLockVoList = channelAuthorizeEntity.getChannelAuthLockVoList();
                List<ChannelAuthUserVo> channelAuthUserVoList = channelAuthorizeEntity.getChannelAuthUserVoList();

                AuthorizeCenterGroupUserDeviceAuthorize authorizeCenterGroupUserDeviceAuthorize = new AuthorizeCenterGroupUserDeviceAuthorize();
                authorizeCenterGroupUserDeviceAuthorize.setDeviceGroupId(channelAuthGroupVo.getDeviceGroupId());
                authorizeCenterGroupUserDeviceAuthorize.setOrgId(baseAuthorizeVo.getOrgId());
                authorizeCenterGroupUserDeviceAuthorize.setName("[" + channelAuthGroupVo.getUserGroupName() + "]与[" + channelAuthGroupVo.getDeviceGroupName() + "]授权");
                authorizeCenterGroupUserDeviceAuthorize.setGroupType("人员组与设备组授权");
                authorizeCenterGroupUserDeviceAuthorize.setUserGroupId(channelAuthGroupVo.getUserGroupId());
                authorizeCenterGroupUserDeviceAuthorize.setStartTime(baseAuthorizeVo.getStartTime());
                authorizeCenterGroupUserDeviceAuthorize.setEndTime(baseAuthorizeVo.getEndTime());
                authorizeCenterGroupUserDeviceAuthorize.setCreateBy(baseAuthorizeVo.getCreateUserAccount());
                authorizeCenterGroupUserDeviceAuthorize.setUpdateBy(baseAuthorizeVo.getCreateUserAccount());

                Date date = new Date();
                authorizeCenterGroupUserDeviceAuthorize.setLastAuthUpdateTime(date);
                AuthorizeCenterGroupUserDeviceAuthorize exist = null;
                if (authorizeCenterGroupUserDeviceAuthorize.getId() != null) {
                    exist = this.baseMapper.selectById(authorizeCenterGroupUserDeviceAuthorize.getId());
                }
                if (EmptyUtil.isEmpty(exist)) {
                    exist = this.baseMapper.selectInfoByUserDeviceGroup(authorizeCenterGroupUserDeviceAuthorize.getOrgId(),
                            authorizeCenterGroupUserDeviceAuthorize.getUserGroupId(), authorizeCenterGroupUserDeviceAuthorize.getDeviceGroupId());
                }
                if (EmptyUtil.isNotEmpty(exist)) {
                    exist.setName(authorizeCenterGroupUserDeviceAuthorize.getName());
                    authorizeCenterGroupUserDeviceAuthorize.setUpdateTime(date);
                    if (EmptyUtil.isNotEmpty(authorizeCenterGroupUserDeviceAuthorize.getSyncTaskNo())) {
                        exist.setSyncTaskNo(authorizeCenterGroupUserDeviceAuthorize.getSyncTaskNo());
                    }
                    if (EmptyUtil.isNotEmpty(authorizeCenterGroupUserDeviceAuthorize.getStartTime())) {
                        exist.setStartTime(authorizeCenterGroupUserDeviceAuthorize.getStartTime());
                    }
                    if (EmptyUtil.isNotEmpty(authorizeCenterGroupUserDeviceAuthorize.getEndTime())) {
                        exist.setEndTime(authorizeCenterGroupUserDeviceAuthorize.getEndTime());
                    }
                    if (EmptyUtil.isNotEmpty(authorizeCenterGroupUserDeviceAuthorize.getCreateBy())) {
                        exist.setCreateBy(authorizeCenterGroupUserDeviceAuthorize.getCreateBy());
                    }
                    if (EmptyUtil.isNotEmpty(authorizeCenterGroupUserDeviceAuthorize.getUpdateBy())) {
                        exist.setUpdateBy(authorizeCenterGroupUserDeviceAuthorize.getUpdateBy());
                    }
                    exist.setUpdateTime(new Date());
                    this.baseMapper.updateById(exist);
                } else {
                    authorizeCenterGroupUserDeviceAuthorize.setActionStatus(0);
                    authorizeCenterGroupUserDeviceAuthorize.setCreateTime(date);
                    this.baseMapper.insert(authorizeCenterGroupUserDeviceAuthorize);
                }
                Long groupAuthId = authorizeCenterGroupUserDeviceAuthorize.getId();

                authorizeCenterGroupRelationComparisonMapper.deleteByGroupAuthId(groupAuthId);
                if (EmptyUtil.isNotEmpty(channelAuthUserVoList)) {
                    List<String> userGroupList = channelAuthUserVoList.stream().map(p -> p.getUserAccount()).collect(Collectors.toList());
                    //更新组授权历史数据
                    AuthorizeCenterGroupRelationComparison userGroupRelationComparison = new AuthorizeCenterGroupRelationComparison();
                    userGroupRelationComparison.setGroupAuthId(groupAuthId);
                    userGroupRelationComparison.setGroupId(authorizeCenterGroupUserDeviceAuthorize.getUserGroupId());
                    userGroupRelationComparison.setGroupType(1);
                    userGroupRelationComparison.setGroupList(userGroupList);
                    authorizeCenterGroupRelationComparisonMapper.batchInsertRelationComparison(userGroupRelationComparison);
                }
                if (EmptyUtil.isNotEmpty(channelAuthLockVoList)) {
                    List<String> deviceGroupList = channelAuthLockVoList.stream().map(p -> p.getLockId()).collect(Collectors.toList());
                    AuthorizeCenterGroupRelationComparison groupRelationComparison = new AuthorizeCenterGroupRelationComparison();
                    groupRelationComparison.setGroupAuthId(groupAuthId);
                    groupRelationComparison.setGroupId(authorizeCenterGroupUserDeviceAuthorize.getDeviceGroupId());
                    groupRelationComparison.setGroupType(2);
                    groupRelationComparison.setGroupList(deviceGroupList);
                    authorizeCenterGroupRelationComparisonMapper.batchInsertRelationComparison(groupRelationComparison);
                }
                String topic = eventCenterServiceClientApi.getTopicByTopicType(TopicType.group_authorize_generate.name());
                //把授权参数放到redis
                redisService.set("channelAuthorizeEntity" + topic + groupAuthId, JSONObject.toJSONString(channelAuthorizeEntity));
                baseAuthorizeVo.setGroupAuthId(groupAuthId);
                channelAuthorizeEntity.setBaseAuthorizeVo(baseAuthorizeVo);
                JSONObject msgBody = new JSONObject();
                msgBody.put("groupAuthId", groupAuthId);
                EventCenterPush eventCenterPush = new EventCenterPush();
                eventCenterPush.setDelayTime(1);
                eventCenterPush.setTopic(topic);
                eventCenterPush.setWeight(100l);
                eventCenterPush.setMsgBody(msgBody.toJSONString());
                eventCenterPush.setCreateAuthUserId(authorizeCenterGroupUserDeviceAuthorize.getCreateBy());
                eventCenterPush.setRemark(authorizeCenterGroupUserDeviceAuthorize.getName());
                eventCenterServiceClientApi.push(eventCenterPush);

                AuthorizeCenterGroupUserDeviceAuthorize model = this.baseMapper.selectById(groupAuthId);
                if (EmptyUtil.isNotEmpty(model)) {
                    model.setUpdateTime(model.getUpdateTime());
                    model.setActionStatus(2);
                    this.baseMapper.updateById(model);
                }
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", ErrorCodeEnum.SUCCESS.msg());
            }else{
                result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
                result.put("message", "非组授权模式");
            }
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    //TODO authorize_center_channel_1_group_authorize_generate_push_topic消费 组授权 事件中心回到方法
    @Override
    public JSONObject groupAuthorizeGenerateConsumerHandler(String taskNo, String topic, String jobType, String msgBody) {
        JSONObject result = new JSONObject();
        try {
            JSONObject msgBodyJson = JSONObject.parseObject(msgBody);
            Long groupAuthId = msgBodyJson.getLong("groupAuthId");
            String authJson=redisService.get("personnelAuthorizeEntity"+topic+groupAuthId);
            if(EmptyUtil.isNotEmpty(authJson)){
                ChannelAuthorizeEntity channelAuthorizeEntity =JSONObject.parseObject(authJson, ChannelAuthorizeEntity.class);
                if(EmptyUtil.isNotEmpty(channelAuthorizeEntity)) {
                    AuthorizeCenterGroupUserDeviceAuthorize model = this.baseMapper.selectById(groupAuthId);
                    if (EmptyUtil.isNotEmpty(model)) {
                        model.setDelayTaskNo(taskNo);
                        model.setSyncTaskNo(taskNo);
                        String syncTaskNo = authorizeService.batchProcessGroupAuth(taskNo, channelAuthorizeEntity);
                        model.setSyncTaskNo(syncTaskNo);
                        model.setUpdateTime(model.getUpdateTime());
                        model.setLastAuthUpdateTime(new Date());
                        model.setActionStatus(2);
                        this.baseMapper.updateById(model);
                    }
                }
            }
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }


}
