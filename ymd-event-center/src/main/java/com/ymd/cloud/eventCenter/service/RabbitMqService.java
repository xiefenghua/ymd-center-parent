package com.ymd.cloud.eventCenter.service;

import com.ymd.cloud.eventCenter.config.RabbitMqConfig;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;

public interface RabbitMqService {
    void initRabbitQueue(RabbitMqConfig rabbitMqConfig);
    Queue createQueue(String queue, int queueLevel);
    Queue createDelayQueue(String queue, int queueLevel);
    CustomExchange createDelayExchange(String exchange);
    Binding bindingDelayCustomExchangeQueue(Queue queue, CustomExchange exchange, String routingKey);
    String[] queues();
}
