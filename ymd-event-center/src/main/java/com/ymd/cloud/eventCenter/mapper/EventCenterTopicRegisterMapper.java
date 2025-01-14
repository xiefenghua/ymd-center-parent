package com.ymd.cloud.eventCenter.mapper;

import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRegister;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
public interface EventCenterTopicRegisterMapper extends BaseMapper<EventCenterTopicRegister> {
    EventCenterTopicRegister selectByTopic(@Param("topic") String topic);
    EventCenterTopicRegister selectByType(@Param("type") String type);
    EventCenterTopicRegister selectByJobType(@Param("jobType") String jobType);
    List<String> selectAllMqttTopicList(@Param("topicFix") String topicFix);
    List<EventCenterTopicRegister> selectEventCenterTopicRegisterPage(EventCenterTopicRegister model);
    List<EventCenterTopicRegister> selectSystemInitAllRabbitMqTopic();
    List<String> selectAllRabbitMqJobType();
}