package com.ymd.cloud.eventCenter.mq;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.common.config.SpringContextUtil;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.ClzUtil;
import com.ymd.cloud.common.utils.DateUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.eventCenter.config.TopicConfig;
import com.ymd.cloud.eventCenter.enums.EventCenterEnum;
import com.ymd.cloud.eventCenter.model.domain.MqSendVo;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRegister;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicTaskRecord;
import com.ymd.cloud.eventCenter.service.ConsumeCallBackService;
import com.ymd.cloud.eventCenter.service.EventCenterLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class BaseConsumerProxy {
    @Autowired
    private ConsumeCallBackService consumeCallBackService;
    @Autowired
    TopicConfig topicConfig;
    @Autowired
    EventCenterLogService eventCenterLogService;
    BaseConsumer consumer=null;
    public BaseConsumerProxy getProxy(String jobType) {
        try {
            this.consumer = (BaseConsumer) SpringContextUtil.getBean(toHump(jobType) + "Consumer");
        }catch (Exception e){
            this.consumer= (BaseConsumer)SpringContextUtil.getBean(toHump(EventCenterEnum.event_center.job)+"Consumer");
        }
        return this;
    }
    /**
     * 下划线转驼峰
     * @param name 属性名
     * @return name 驼峰命名
     */
    public String toHump(String name){
        if (name.contains("_")){
            Pattern pattern = Pattern.compile("_(\\w)");
            name = name.toLowerCase();
            Matcher matcher = pattern.matcher(name);
            StringBuffer sb = new StringBuffer();
            while (matcher.find()){
                matcher.appendReplacement(sb,matcher.group(1).toUpperCase());
            }
            matcher.appendTail(sb);
            return sb.toString();
        }
        return name;
    }
    public void consume(Message message) {
        MessageProperties properties = message.getMessageProperties();
        String mqMessageId = properties.getMessageId();
        String body = new String(message.getBody());
        Integer delay = properties.getReceivedDelay()/1000;
        EventCenterTopicTaskRecord delayTask = consumeCallBackService.getMessageByMessageId(mqMessageId);
        if(EmptyUtil.isEmpty(delayTask)){
            return;
        }
        if (isConsumed(delayTask)) {// 消费幂等性, 防止消息被重复消费
            eventCenterLogService.saveEventLog(mqMessageId,"【事件中心消费阶段】--重复消费",
                    JSONObject.toJSONString(mqMessageId), ErrorCodeEnum.SUCCESS.code(),ErrorCodeEnum.SUCCESS.msg()
                    , 0l, delayTask.getCreateAuthUserId());
            log.info("重复消费, mqMessageId: {}", mqMessageId);
        }else {
            eventCenterLogService.saveEventLog(mqMessageId,"【事件中心消费阶段】--参数准备完毕,进入事件消费方法",
                    JSONObject.toJSONString(message), ErrorCodeEnum.SUCCESS.code(),ErrorCodeEnum.SUCCESS.msg()
                    , 0l, delayTask.getCreateAuthUserId());
        }
        String taskNo = delayTask.getTaskNo();
        String replyCode = ErrorCodeEnum.SUCCESS.code(), replyText = "业务处理成功";
        boolean retry = consumeCallBackService.retryCount(mqMessageId);
        if (retry) {
            long start=System.currentTimeMillis();
            try {
                if(EmptyUtil.isNotEmpty(body)) {
                    MqSendVo rabbitDelayVO= JSONObject.parseObject(body, MqSendVo.class);
                    String topic = rabbitDelayVO.getTopic();
                    String msgBody = rabbitDelayVO.getMsgBody();
                    String pushTime = rabbitDelayVO.getPushTime();
                    String date = DateUtil.format(new Date(), DateUtil.yyyy_MM_ddHH_mm_ss);
                    String jobType =null;
                    EventCenterTopicRegister eventCenterTopicRegister=topicConfig.getByTopic(topic);
                    if(EmptyUtil.isNotEmpty(eventCenterTopicRegister)) {
                        jobType=eventCenterTopicRegister.getJobType();
                    }
                    log.info("[=====consume>延迟队列=====]taskTopic:{}限时{}秒 提交时间:{} 消费时间:{}  消息内容:{}", topic,delay, pushTime, date,msgBody);
                    String result=consumer.consume(taskNo,topic,jobType,msgBody);
                    replyCode = ErrorCodeEnum.SUCCESS.code();
                    replyText = "业务处理成功,业务返回："+result;
                }
            } catch (Exception e) {
                replyCode = ErrorCodeEnum.SYSTEM_ERROR_STATUS.code();
                replyText = "【事件中心消费阶段】业务队列消费--业务异常Returned message callback,消息异常" + e.fillInStackTrace();
                log.error("【事件中心消费阶段】{}接口-业务队列消费异常", ClzUtil.getMethodName(),e);
            } finally {
                consumeCallBackService.returnTo(taskNo,mqMessageId, body, replyCode, replyText,(System.currentTimeMillis()-start));
            }
        }else{
            eventCenterLogService.saveEventLog(mqMessageId,"【事件中心消费阶段】--超过重试次数",
                    mqMessageId, ErrorCodeEnum.SUCCESS.code(),ErrorCodeEnum.SUCCESS.msg()
                    , 0l, delayTask.getCreateAuthUserId());
        }
    }
    /**
     * 消息是否已被消费
     *
     * @return
     */
    private boolean isConsumed(EventCenterTopicTaskRecord delayTask) {
        return EmptyUtil.isEmpty(delayTask);
    }
}
