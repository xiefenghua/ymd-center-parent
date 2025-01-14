package com.ymd.cloud.eventCenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Maps;
import com.ymd.cloud.common.enumsSupport.EventCenterConstant;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.eventCenter.config.RabbitMqConfig;
import com.ymd.cloud.eventCenter.model.domain.EventCenterTaskQueue;
import com.ymd.cloud.eventCenter.model.vo.EventCenterRule;
import com.ymd.cloud.eventCenter.service.RabbitMqTopicQueueService;
import com.ymd.cloud.eventCenter.service.EventCenterRuleService;
import com.ymd.cloud.eventCenter.service.RabbitMqService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Service
@Slf4j
public class RabbitMqServiceImpl implements RabbitMqService {
    public static final String X_DELAYED_MESSAGE = "x-delayed-message";
    public List<EventCenterTaskQueue> delayTaskQueueList;
    public List<String> queueList;
    public String durable;
    public Integer ruleLevel=15;
    public String ack;

    @Autowired
    EventCenterRuleService eventCenterRuleService;
    @Autowired
    RabbitMqTopicQueueService delayTaskQueueToTopicJobService;
    @Override
    public void initRabbitQueue(RabbitMqConfig rabbitMqConfig) {
        //获取全局规则配置表
        EventCenterRule eventCenterRule=eventCenterRuleService.selectIsAllExist();
        try {
            if(EmptyUtil.isNotEmpty(eventCenterRule)&& EmptyUtil.isNotEmpty(eventCenterRule.getRuleAction())){
                ruleLevel=Integer.valueOf(eventCenterRule.getRuleLevel());
                JSONObject actionJSON= JSONObject.parseObject(eventCenterRule.getRuleAction());
                durable=actionJSON.getString("durable");
                ack=actionJSON.getString("ack");
                EventCenterConstant.retryCount=actionJSON.getIntValue("retry_count");
            }
        }catch (Exception e){}
        delayTaskQueueList=delayTaskQueueToTopicJobService.eventCenterTaskQueueList();
        if(EmptyUtil.isNotEmpty(delayTaskQueueList)) {
            for (EventCenterTaskQueue delayTaskQueue : delayTaskQueueList) {
                Queue queue = "0".equals(durable)?createQueue(delayTaskQueue.getQueue(),ruleLevel):createDelayQueue(delayTaskQueue.getQueue(),ruleLevel);
                CustomExchange customExchange = createDelayExchange(delayTaskQueue.getExchange());
                Binding binding = bindingDelayCustomExchangeQueue(queue, customExchange, delayTaskQueue.getRoutingKey());
                rabbitMqConfig.getRabbitAdmin().declareQueue(queue);
                rabbitMqConfig.getRabbitAdmin().declareExchange(customExchange);
                rabbitMqConfig.getRabbitAdmin().declareBinding(binding);
            }
        }
    }
    @Override
    public String[] queues() {
        String[] queueArr= new String[]{};
        queueList=delayTaskQueueToTopicJobService.queueList();
        if(EmptyUtil.isNotEmpty(queueList)) {
            queueArr = queueList.toArray(new String[queueList.size()]);
        }
        return queueArr;
    }

    @Override
    public Queue createQueue(String queue,int queueLevel) {
        Map<String, Object> args = Maps.newHashMap();
        args.put("x-max-priority", queueLevel);
        return QueueBuilder.nonDurable(queue).withArguments(args).build();
    }

    @Override
    public Queue createDelayQueue(String queue,int queueLevel) {
        Map<String, Object> args = Maps.newHashMap();
        args.put("x-max-priority", queueLevel);
        return QueueBuilder.durable(queue).withArguments(args).build();
    }

    @Override
    public CustomExchange createDelayExchange(String exchange) {
        HashMap<String, Object> args = new HashMap<>(8);
        args.put("x-delayed-type", "direct");
        return new CustomExchange(exchange, X_DELAYED_MESSAGE, true, false, args);
    }

    @Override
    public Binding bindingDelayCustomExchangeQueue(Queue queue, CustomExchange exchange, String routingKey) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey).noargs();
    }
}
