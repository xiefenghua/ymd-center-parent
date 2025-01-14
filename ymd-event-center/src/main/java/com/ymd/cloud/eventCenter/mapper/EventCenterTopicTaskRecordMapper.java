package com.ymd.cloud.eventCenter.mapper;


import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicTaskRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface EventCenterTopicTaskRecordMapper extends BaseMapper<EventCenterTopicTaskRecord> {
    List<EventCenterTopicTaskRecord> selectEventCenterTopicTaskRecordPage(EventCenterTopicTaskRecord model);
    EventCenterTopicTaskRecord selectEventCenterTopicTaskRecordByTopicAndTaskNo(EventCenterTopicTaskRecord model);
    EventCenterTopicTaskRecord selectLastNotPushDelayTask();
    EventCenterTopicTaskRecord selectDelayTaskByMessageId(@Param("mqMessageId") String mqMessageId);
    EventCenterTopicTaskRecord selectDelayTaskByTaskNo(@Param("taskNo") String taskNo);
    List<EventCenterTopicTaskRecord> selectDelayTaskByWeight(@Param("retryCount") Integer retryCount,@Param("pageSize") int pageSize);
    void updatePushStatusByTaskNo(EventCenterTopicTaskRecord mqDelayTask);
    void updateErrorRetryCountByTaskNo(@Param("taskNo") String taskNo);
    void updateRetryCountByTaskNo(@Param("taskNo") String taskNo);
    List<EventCenterTopicTaskRecord> selectTaskByLikeMsgBody(@Param("taskMsgBody") String taskMsgBody);
    void updatePushStatus();
    void clearReset();
    void delTaskBefore1Month();
}