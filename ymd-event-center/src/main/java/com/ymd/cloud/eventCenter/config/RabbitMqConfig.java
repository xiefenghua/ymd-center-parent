package com.ymd.cloud.eventCenter.config;


import com.rabbitmq.client.Channel;
import com.ymd.cloud.eventCenter.service.RabbitMqService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * RabbitMQ配置
 * 项目使用默认的rabbitTemplate
 * 三方数据对接为每个第三方配置不同的rabbitTemplate
 */
@Component
@Slf4j
public class RabbitMqConfig {
    //并发数量:根据实际的服务器性能进行配置即可
    public static final int DEFAULT_CONCURRENT = 10;
    public static final int prefetchCount = 10;
    AcknowledgeMode acknowledgeMode= AcknowledgeMode.AUTO;
    public Channel channel;
    public Connection connection;
    public ConnectionFactory connectionFactory;
    public RabbitTemplate rabbitTemplate;
    public RabbitAdmin rabbitAdmin;
    @Autowired
    RabbitMqService rabbitMqService;

    @SneakyThrows
    @Bean(name="connectionFactory")
    @Primary
    public ConnectionFactory connectionFactory(
            @Value("${spring.rabbitmq.addresses}") String addresses,
            @Value("${spring.rabbitmq.username}") String username,
            @Value("${spring.rabbitmq.password}") String password,
            @Value("${spring.rabbitmq.virtual-host}") String virtualHost,
            @Value("${spring.rabbitmq.publisher-returns}") boolean publisherReturns,
            @Value("${spring.rabbitmq.publisher-confirms}") boolean publisherConfirms){
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setAddresses(addresses);
        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(virtualHost);
        connectionFactory.setPublisherConfirms(publisherConfirms);
        connectionFactory.setPublisherReturns(publisherReturns);
        this.connectionFactory=connectionFactory;
        connection= connectionFactory.createConnection();
        // 创建连接
        channel = connection.createChannel(false);
        channel.basicQos(prefetchCount); // 每次最多获取10条消息
        return connectionFactory;
    }
    @Bean(name="rabbitTemplate")
    @Primary
    public RabbitTemplate rabbitTemplate(@Qualifier("connectionFactory") ConnectionFactory connectionFactory){
        rabbitTemplate = new RabbitTemplate(connectionFactory);
        // 消息是否成功发送到Exchange
        rabbitTemplate.setConfirmCallback((correlationData, ack, cause) -> {
            if(ack){
                log.info("=========RabbitMq Confirmation callback 消息发送到交换机【成功】");
            }else{
                log.info("=========RabbitMq Confirmation callback 消息发送到交换机【失败】{}",cause);
            }
        });
        // 消息是否从Exchange路由到Queue, 注意: 这是一个失败回调, 只有消息从Exchange路由到Queue失败才会回调这个方法
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("消息从Exchange路由到Queue失败: exchange: {}, route: {}, replyCode: {}, replyText: {}, message: {}", exchange, routingKey, replyCode, replyText, message);
        });
        this.rabbitAdmin=new RabbitAdmin(this.connectionFactory);
        rabbitMqService.initRabbitQueue(this);
        return rabbitTemplate;
    }
    @Bean(name = "simpleRabbitListenerContainerFactory")
    @Primary
    public SimpleRabbitListenerContainerFactory simpleRabbitListenerContainerFactory(
            SimpleRabbitListenerContainerFactoryConfigurer configurer,
            @Qualifier("connectionFactory") ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setAcknowledgeMode(acknowledgeMode);
        factory.setPrefetchCount(prefetchCount);
        factory.setConcurrentConsumers(DEFAULT_CONCURRENT);    //设置线程数
        factory.setMaxConcurrentConsumers(DEFAULT_CONCURRENT); //最大线程数
        configurer.configure(factory, connectionFactory);
        return factory;
    }
    public Channel getChannel() {
        return channel;
    }

    public Connection getConnection() {
        return connection;
    }

    public ConnectionFactory getConnectionFactory() {
        return connectionFactory;
    }

    public RabbitTemplate getRabbitTemplate() {
        return rabbitTemplate;
    }

    public RabbitAdmin getRabbitAdmin() {
        return rabbitAdmin;
    }
}
