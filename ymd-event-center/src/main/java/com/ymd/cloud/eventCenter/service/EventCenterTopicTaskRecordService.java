package com.ymd.cloud.eventCenter.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.common.utils.PageRequest;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicTaskRecord;

public interface EventCenterTopicTaskRecordService extends IService<EventCenterTopicTaskRecord> {
    PageInfo<EventCenterTopicTaskRecord> pageEventCenterTopicTaskRecordList(PageRequest request, EventCenterTopicTaskRecord model);
    JSONObject saveTaskRecord(EventCenterTopicTaskRecord model);
    JSONObject updateTaskRecord(EventCenterTopicTaskRecord model);

}
