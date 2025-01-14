package com.ymd.cloud.eventCenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.common.enumsSupport.EventCenterConstant;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicTaskRecord;
import com.ymd.cloud.eventCenter.service.ConsumeCallBackService;
import com.ymd.cloud.eventCenter.service.EventCenterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConsumeCallBackServiceImpl implements ConsumeCallBackService {
    @Autowired
    private EventCenterService eventCenterService;

    @Override
    public void returnTo(String taskNo,String mqMessageId, String body, String replyCode, String replyText,long postTime) {
        if (EmptyUtil.isNotEmpty(body)) {
            JSONObject result=eventCenterService.processReturn(taskNo,mqMessageId,replyCode,replyText,postTime);
            log.info("[=====processReturn>延迟队列=====] result：{}",result.toJSONString());
        }
    }

    @Override
    public boolean retryCount(String mqMessageId) {
        EventCenterTopicTaskRecord model=getMessageByMessageId(mqMessageId);
        if(EmptyUtil.isNotEmpty(model)) {
            if(model.getRetryCount()>=EventCenterConstant.retryCount) {
                return false;
            }
        }
        return true;
    }

    @Override
    public EventCenterTopicTaskRecord getMessageByMessageId(String mqMessageId) {
        EventCenterTopicTaskRecord model=eventCenterService.selectDelayTaskByTaskNo(mqMessageId);
        return model;
    }

}
