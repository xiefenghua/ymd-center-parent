package com.ymd.cloud.eventCenter.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.common.utils.PageRequest;
import com.ymd.cloud.eventCenter.model.vo.EventCenterRule;

public interface EventCenterRuleService extends IService<EventCenterRule> {
    PageInfo<EventCenterRule> pageEventCenterRuleList(PageRequest request, EventCenterRule model);
    JSONObject saveRule(EventCenterRule model);
    JSONObject delRule(String ids, String createAuthUserId);
    JSONObject updateRule(EventCenterRule model);
    EventCenterRule selectIsAllExist();
}
