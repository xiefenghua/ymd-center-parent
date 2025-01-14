package com.ymd.cloud.eventCenter.mapper;


import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
public interface EventCenterTopicRuleMapper extends BaseMapper<EventCenterTopicRule> {
    EventCenterTopicRule selectByTopicIdAndRuleId(EventCenterTopicRule model);
}