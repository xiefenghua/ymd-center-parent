package com.ymd.cloud.eventCenter.web;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.api.eventCenter.model.EventCenterPush;
import com.ymd.cloud.common.base.AbstractController;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.eventCenter.mq.consumer.BasicConsume;
import com.ymd.cloud.eventCenter.service.EventCenterLogService;
import com.ymd.cloud.eventCenter.service.EventCenterService;
import com.ymd.cloud.eventCenter.service.RabbitMqTopicQueueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Random;

@RestController
@Api(value = "事件中心", tags = "事件中心接口")
@RequestMapping("ymd/web/center/eventCenter")
@Slf4j
public class EventCenterController extends AbstractController {
    @Autowired
    EventCenterService eventCenterService;
    @Autowired
    BasicConsume basicConsume;
    @Autowired
    private RabbitMqTopicQueueService eventCenterTopicQueueService;
    @Autowired
    EventCenterLogService eventCenterLogService;
    @PostMapping(value = "/batch")
    @ApiOperation(value = "批量事件推送")
    public ResponseEntity<ModelMap> batch(@RequestBody JSONObject param){
        log.info("批量事件推送 参数:{}",JSONObject.toJSONString(param));
        int MAX_REQUEST_COUNT=param.getIntValue("count");
        Random random=new Random();
        EventCenterPush eventCenterPush= param.getObject("model",EventCenterPush.class);
        Integer a=eventCenterPush.getDelayTime();
        for(int i=0; i<MAX_REQUEST_COUNT; i++){
            Integer delay=random.nextInt(a);
            eventCenterPush.setDelayTime(delay);
            eventCenterService.push(eventCenterPush);
        }
        log.info("批量事件推送 响应:{}",setSuccessModelMap());
        return setSuccessModelMap();
    }

    @PostMapping(value = "/push")
    @ApiOperation(value = "事件推送")
    public JSONObject push(@RequestBody EventCenterPush dto) {
        log.info("事件推送  参数:{}",JSONObject.toJSONString(dto));
        JSONObject result = eventCenterService.push(dto);
        log.info("事件推送 响应:{}",JSONObject.toJSONString(result));
        return result;
    }

    @PostMapping("/list")
    @ApiOperation(value = "缓存队列列表数据")
    public ResponseEntity<ModelMap> getCacheList(){
        log.info("缓存队列列表数据  参数:{}");
        JSONObject result = eventCenterService.getCacheList();
        log.info("缓存队列列表数据 响应:{}",JSONObject.toJSONString(result));
        return setSuccessModelMap(result);
    }
    @PostMapping("/clearResetDelayTask")
    @ApiOperation(value = "清除所有事件任务")
    public ResponseEntity<ModelMap> clearResetDelayTask(){
        log.info("清除所有事件任务  参数:{}");
        eventCenterService.clearResetDelayTask();
        log.info("清除所有事件任务 响应:{}",setSuccessModelMap());
        return setSuccessModelMap();
    }

    @PostMapping("/basicConsume")
    @ApiOperation(value = "手动消费rabbitmq中队列名QUEUE的消息")
    public ResponseEntity<ModelMap> basicConsume(){
        log.info("手动消费rabbitmq中队列名QUEUE的消息  参数:{}");
        List<String> queueList= eventCenterTopicQueueService.queueList();
        for (String queue:queueList) {
            basicConsume.basicConsume(queue,"600秒实时监控堆积");
        }
        eventCenterLogService.saveEventLog(System.currentTimeMillis()+"","手动消费rabbitmq中队列名QUEUE的消息",
                String.join(",",queueList), ErrorCodeEnum.SUCCESS.code(),ErrorCodeEnum.SUCCESS.msg()
                , 0l, null);
        log.info("手动消费rabbitmq中队列名QUEUE的消息 响应:{}",setSuccessModelMap());
        return setSuccessModelMap();
    }
    /**
     * 事件订阅接口
     * @param param
     * @return
     */
    @RequestMapping("/subscribe")
    public ResponseEntity<ModelMap> subscribe(@RequestBody EventCenterPush param){
        try {
            log.info("【事件订阅接口---业务】 参数：{}",JSONObject.toJSONString(param));
            String createAuthUserId = param.getCreateAuthUserId();
            param.setCreateAuthUserId(createAuthUserId);
            JSONObject result = eventCenterService.subscribe(param);
            log.info("【事件订阅接口---业务】 ,返回：{}", result.toJSONString());
            return setSuccessModelMap(result);
        }catch (Exception e){
            log.error("事件订阅接口--error:",e);
            return setModelMap(ErrorCodeEnum.SYSTEM_ERROR_STATUS.code(),e.getMessage());
        }
    }
}
