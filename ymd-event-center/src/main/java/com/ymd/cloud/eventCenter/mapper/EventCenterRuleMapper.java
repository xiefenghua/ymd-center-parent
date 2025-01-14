package com.ymd.cloud.eventCenter.mapper;

import com.ymd.cloud.eventCenter.model.vo.EventCenterRule;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface EventCenterRuleMapper extends BaseMapper<EventCenterRule> {
    List<EventCenterRule> selectEventCenterRulePage(EventCenterRule model);
    EventCenterRule selectIsAllExist();
}