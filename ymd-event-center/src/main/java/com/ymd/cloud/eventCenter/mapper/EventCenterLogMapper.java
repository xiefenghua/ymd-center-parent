package com.ymd.cloud.eventCenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.eventCenter.model.vo.EventCenterLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface EventCenterLogMapper extends BaseMapper<EventCenterLog> {
    List<EventCenterLog> selectEventCenterLogPage(EventCenterLog model);
    List<EventCenterLog>selectLogListByTaskNo(@Param("taskNo") String taskNo);
    EventCenterLog selectEventCenterLogByTaskNo(@Param("topic") String topic, @Param("taskNo") String taskNo);
    int selectExistByTaskNo(@Param("taskNo") String taskNo);
    void delBefore1Month();
}