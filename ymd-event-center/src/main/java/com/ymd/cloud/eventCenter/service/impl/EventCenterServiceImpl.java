package com.ymd.cloud.eventCenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ymd.cloud.api.eventCenter.model.EventCenterPush;
import com.ymd.cloud.common.enumsSupport.Constants;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.enumsSupport.EventCenterConstant;
import com.ymd.cloud.api.RedisService;
import com.ymd.cloud.common.utils.*;
import com.ymd.cloud.eventCenter.mapper.EventCenterLogMapper;
import com.ymd.cloud.eventCenter.mapper.EventCenterTopicRegisterMapper;
import com.ymd.cloud.eventCenter.mapper.EventCenterTopicTaskRecordMapper;
import com.ymd.cloud.eventCenter.model.domain.EventCenterTaskQueue;
import com.ymd.cloud.eventCenter.model.domain.MqSendVo;
import com.ymd.cloud.eventCenter.model.vo.EventCenterLog;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRegister;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicTaskRecord;
import com.ymd.cloud.eventCenter.mq.mqtt.producer.MqttMessage;
import com.ymd.cloud.eventCenter.mq.rabbitmq.producer.RabbitMqMessage;
import com.ymd.cloud.eventCenter.proxy.BaseSpringBeanInstallProxy;
import com.ymd.cloud.eventCenter.service.EventCenterLogService;
import com.ymd.cloud.eventCenter.service.EventCenterService;
import com.ymd.cloud.eventCenter.service.RabbitMqTopicQueueService;
import com.ymd.cloud.eventCenter.service.EventCenterTopicTaskRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;


@Service
@Slf4j
public class EventCenterServiceImpl extends ServiceImpl<EventCenterTopicTaskRecordMapper,EventCenterTopicTaskRecord> implements EventCenterService {
    private Integer delayQueueLength=100;
    @Resource
    EventCenterTopicRegisterMapper eventCenterTopicRegisterMapper;
    @Resource
    EventCenterTopicTaskRecordMapper eventCenterTopicTaskRecordMapper;
    @Resource
    EventCenterLogMapper eventCenterLogMapper;
    @Autowired
    EventCenterTopicTaskRecordService eventCenterTopicTaskRecordService;
    @Autowired
    EventCenterLogService eventCenterLogService;
    @Autowired
    private BaseSpringBeanInstallProxy baseSpringBeanInstallProxy;
    @Autowired
    RabbitMqTopicQueueService eventCenterTopicQueueService;
    @Autowired
    RedisService redisService;
    @Autowired
    RabbitMqMessage rabbitMqMessage;
    @Autowired
    MqttMessage mqttMessage;

    @Override
    public String consumerHandler(String className,String taskNo ,String topic,String jobType,String msgBody) {
        return baseSpringBeanInstallProxy.getEventCenterConsumerProxy(className).handler(taskNo,topic,jobType,msgBody);
    }

    @Override
    public JSONObject subscribe(EventCenterPush model) {
        return null;
    }

    @Override
    public JSONObject push(EventCenterPush model) {
        JSONObject result = new JSONObject();
        String taskNo=generateTaskNo();
        try {
            ParamValiUtil.checkArgumentNotEmpty(model.getTopic(),"主题名称topic");
            ParamValiUtil.checkArgumentNotEmpty(model.getMsgBody(),"报文体");
            model.setDelayTime(model.getDelayTime()==null?0:model.getDelayTime());
            if(EmptyUtil.isNotEmpty(model.getWeight())){
                if(model.getWeight()<1){
                    ParamValiUtil.checkArgumentNotEmpty(model.getWeight(),"自定义权重等级只支持范围1-99之间,如果不传则系统生成时间戳");
                }else if(model.getWeight()>99){
                    ParamValiUtil.checkArgumentNotEmpty(model.getWeight(),"自定义权重等级只支持范围1-99之间,如果不传则系统生成时间戳");
                }
                model.setWeight(model.getWeight());
            }else{
                model.setWeight(System.currentTimeMillis());
            }

            if (EmptyUtil.isNotEmpty(model.getWeight())) {
                if(model.getWeight()>0&&model.getWeight()<100){//代表自定义
                    model.setWeight(Long.valueOf(model.getWeight()+""+System.currentTimeMillis()).longValue());
                }
            }

            EventCenterTopicRegister eventCenterTopicRegister = eventCenterTopicRegisterMapper.selectByTopic(model.getTopic());
            if(EmptyUtil.isNotEmpty(eventCenterTopicRegister)) {
                String taskName=EmptyUtil.isNotEmpty(model.getRemark())?model.getRemark():eventCenterTopicRegister.getRemark();
                EventCenterTopicTaskRecord eventCenterTopicTaskRecord=new EventCenterTopicTaskRecord();
                eventCenterTopicTaskRecord.setTaskNo(taskNo);
                eventCenterTopicTaskRecord.setTopic(model.getTopic());
                eventCenterTopicTaskRecord.setDelayTime(model.getDelayTime()<0?0:model.getDelayTime());
                eventCenterTopicTaskRecord.setTaskMsgBody(model.getMsgBody());
                eventCenterTopicTaskRecord.setCreateTime(new Date());
                eventCenterTopicTaskRecord.setRetryCount(0);
                eventCenterTopicTaskRecord.setCreateAuthUserId(model.getCreateAuthUserId());
                eventCenterTopicTaskRecord.setErrorRetryCount(0);
                eventCenterTopicTaskRecord.setMqMessageId(taskNo);
                eventCenterTopicTaskRecord.setTaskName(taskName);
                String jobType=eventCenterTopicRegister.getJobType();
                EventCenterTaskQueue delayTaskQueue=eventCenterTopicQueueService.analysis(jobType);
                eventCenterTopicTaskRecord.setExchange(delayTaskQueue.getExchange());
                eventCenterTopicTaskRecord.setRoutingKey(delayTaskQueue.getRoutingKey());
                if(model.getWeight()!=null) {
                    eventCenterTopicTaskRecord.setWeight(model.getWeight()<0?System.currentTimeMillis():model.getWeight());
                }
                result= eventCenterTopicTaskRecordService.saveTaskRecord(eventCenterTopicTaskRecord);
                eventCenterLogService.saveEventLog(taskNo,"【事件中心接收阶段】--接收事件开始",
                        JSONObject.toJSONString(model), result.getString("status"),result.toJSONString() ,
                        0l, eventCenterTopicTaskRecord.getCreateAuthUserId());
                if(ErrorCodeEnum.SUCCESS.code().equals(result.getString("status"))) {
                    //延迟数据双备份,幂等性
                    saveCacheQueue(eventCenterTopicTaskRecord);
                    batchSendDelayMsgPolicy(true, true);
                    result.put("taskNo", taskNo);
                    result.put("status", ErrorCodeEnum.SUCCESS.code());
                    result.put("message", "成功");
                }
            }else{
                result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
                result.put("message", "topic非法数据,请注册后再发布事件");
            }
            return result;
        }catch(Exception e){
            log.error("【事件中心接收阶段】{}接口-异常",ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }finally {
        }
        return result;
    }

    private void sendMsg(String mqttType,String exchange,String routingKey,String topic,String msgBody,Integer delay,String messageId,Integer retryCount,String createUser){
        try {
            MqSendVo mqSendVo=new MqSendVo();
            mqSendVo.setExchange(exchange);
            mqSendVo.setRoutingKey(routingKey);
            mqSendVo.setTopic(topic);
            mqSendVo.setMsgBody(msgBody);
            mqSendVo.setPushTime(DateUtil.getTime());
            mqSendVo.setDelay(delay);
            mqSendVo.setMessageId(messageId);
            //发送订单到消息队列 超时n秒订单处理自动关闭
            String pushTime= DateUtil.format(new Date(), DateUtil.yyyy_MM_ddHH_mm_ss);
            mqSendVo.setPushTime(pushTime);
            if("rabbitmq".equals(mqttType)){
                rabbitMqMessage.send(mqSendVo);
            }else {
                mqttMessage.send(mqSendVo);
            }
            EventCenterTopicTaskRecord model=eventCenterTopicTaskRecordMapper.selectDelayTaskByTaskNo(messageId);
            if(EmptyUtil.isNotEmpty(model)) {
                model.setPushStatus(1);
                model.setPushTime(new Date());
                model.setRetryCount(EmptyUtil.isNotEmpty(model.getRetryCount())?model.getRetryCount()+1:1);
                eventCenterTopicTaskRecordMapper.updatePushStatusByTaskNo(model);
                eventCenterTopicTaskRecordMapper.updateRetryCountByTaskNo(messageId);
                redisService.zremRangeByScore(Constants.MQ_QUEUE_TASK_REDIS_KEY,getScore(model),getScore(model));
                redisService.zadd(Constants.MQ_QUEUE_TASK_REDIS_KEY,getScore(model),JSONObject.toJSONString(model));
            }
            eventCenterLogService.saveEventLog(messageId,"【事件中心mq发送阶段】"+mqttType+"第"+(retryCount+1)+"次发送消息业务成功",
                    JSONObject.toJSONString(mqSendVo), ErrorCodeEnum.SUCCESS.code(),ErrorCodeEnum.SUCCESS.msg()
                    , 0l, createUser);
        }catch(Exception e){
            log.error("【事件中心mq发送阶段】{}接口-异常",ClzUtil.getMethodName(),e);
            eventCenterLogService.saveEventLog(messageId,"【事件中心mq发送阶段】异常",
                    messageId, ErrorCodeEnum.SYSTEM_ERROR_STATUS.code(),e.fillInStackTrace().toString()
                    , 0l, createUser);
        }
    }

    //--------------------------业务类-----------------------------
    private String generateTaskNo(){
        String taskNo= UUIDUtil.generateNo();
        int count=eventCenterLogMapper.selectExistByTaskNo(taskNo);
        if(count>0) {
            return generateTaskNo();
        }
        return taskNo;
    }


    /**
     * 批量缓存队列处理推送
     */
    @Override
    public String batchSendDelayMsgPolicy(boolean compelRefresh,boolean action) {
        SpinLock.lock();
        StringBuffer msg=new StringBuffer();
        try {
            //判断当前队列是否已全部执行完毕
            Long count = redisService.zcount(Constants.MQ_QUEUE_TASK_REDIS_KEY);
            int size=0;
            count=EmptyUtil.isEmpty(count)?0:count;
            if (count==0l||compelRefresh) {
                //支持权重划分执行
                refreshCacheQueue();
            }
            //判断缓存队列是否已经执行完毕，缓存排队机制做高并发防消息堆积能力处理
            if ((count<=delayQueueLength)) {
                if(action) {
                    //获取下一批缓存排队队列（数据库和redis）
                    Set<String> delayTaskCacheSet = redisService.zrange(Constants.MQ_QUEUE_TASK_REDIS_KEY, 0, delayQueueLength - 1);
                    if (EmptyUtil.isNotEmpty(delayTaskCacheSet)&&delayTaskCacheSet.size()>0) {
                        //根据taskNo去重
                        Map<String,String> unRepeatTaskCacheMap=new HashMap<>();
                        for (String delayTaskCache : delayTaskCacheSet) {
                            EventCenterTopicTaskRecord pushModel = JSONObject.parseObject(delayTaskCache, EventCenterTopicTaskRecord.class);
                            if(!unRepeatTaskCacheMap.containsKey(pushModel.getTaskNo())){
                                unRepeatTaskCacheMap.put(pushModel.getTaskNo(),JSONObject.toJSONString(pushModel));
                            }else{
                                redisService.zremRangeByScore(Constants.MQ_QUEUE_TASK_REDIS_KEY,getScore(pushModel),getScore(pushModel));
                            }
                        }
                        if (EmptyUtil.isNotEmpty(unRepeatTaskCacheMap)&&unRepeatTaskCacheMap.size()>0) {
                            delayTaskCacheSet = new HashSet<>(unRepeatTaskCacheMap.values());
                            size = delayTaskCacheSet.size();
                            for (String delayTaskCache : delayTaskCacheSet) {
                                EventCenterTopicTaskRecord pushModel = JSONObject.parseObject(delayTaskCache, EventCenterTopicTaskRecord.class);
                                if (pushModel.getPushStatus() == 0) {
                                    if (pushModel.getRetryCount()<=EventCenterConstant.retryCount) {
                                        //添加延迟队列
                                        EventCenterTopicRegister eventCenterTopicRegister = eventCenterTopicRegisterMapper.selectByTopic(pushModel.getTopic());
                                        if (EmptyUtil.isNotEmpty(eventCenterTopicRegister)) {
                                            sendMsg(eventCenterTopicRegister.getMqttType()
                                                    , pushModel.getExchange(),
                                                    pushModel.getRoutingKey()
                                                    , pushModel.getTopic(), pushModel.getTaskMsgBody()
                                                    , pushModel.getDelayTime(), pushModel.getMqMessageId(),pushModel.getRetryCount(),pushModel.getCreateAuthUserId());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            msg.append("当前缓存队列大小:"+count).append(",配置队列大小:"+delayQueueLength)
                    .append(",获取缓存start:0").append(",end:").append(delayQueueLength-1)
                    .append(",缓存set-size:").append(size);
        }finally {
            SpinLock.unLock();
        }
        return msg.toString();
    }

    @Override
    public JSONObject getCacheList() {
        JSONObject result = new JSONObject();
        List<EventCenterTopicTaskRecord> list=new ArrayList<>();
        Set<String> delayTaskCacheSet = redisService.zrange(Constants.MQ_QUEUE_TASK_REDIS_KEY, 0, - 1);
        if (EmptyUtil.isNotEmpty(delayTaskCacheSet)) {
            for (String delayTaskCache : delayTaskCacheSet) {
                list.add(JSONObject.parseObject(delayTaskCache, EventCenterTopicTaskRecord.class));
            }
            result.put("total",delayTaskCacheSet.size());
            result.put("data",delayTaskCacheSet);
        }
        String time=redisService.get(Constants.MQ_QUEUE_TASK_REDIS_KEY+"_uploadTime");
        if( EmptyUtil.isNotEmpty(time)) {
            result.put("uploadTime", DateUtil.format(Long.valueOf(time)));
        }else{
            result.put("uploadTime",null);
        }
        return result;
    }

    @Override
    public void clearResetDelayTask() {
        redisService.set(Constants.MQ_QUEUE_TASK_REDIS_KEY+"_uploadTime","0");
        eventCenterTopicTaskRecordMapper.clearReset();
        redisService.remove(Constants.MQ_QUEUE_TASK_REDIS_KEY);
        batchSendDelayMsgPolicy(true,true);
    }


    /**
     * 刷新缓存排队队列（数据库和redis）
     * @param eventCenterTopicTaskRecord
     * @return
     */
    @Override
    public JSONObject saveCacheQueue(EventCenterTopicTaskRecord eventCenterTopicTaskRecord) {
        JSONObject result = new JSONObject();
        try {
            //判断任务号是否已存在
            EventCenterTopicTaskRecord delayTask=eventCenterTopicTaskRecordMapper.selectDelayTaskByTaskNo(eventCenterTopicTaskRecord.getTaskNo());
            if(EmptyUtil.isNotEmpty(delayTask)){
                eventCenterTopicTaskRecord.setId(delayTask.getId());
                eventCenterTopicTaskRecord.setOpt("update");
            }
            //处理排队队列缓存和数据库
            processCacheQueue(eventCenterTopicTaskRecord);
            result.put("status", ErrorCodeEnum.SUCCESS.code());
            result.put("message", "成功");
            return result;
        }catch(Exception e){
            log.error("【事件中心队列处理阶段】{}接口---刷新缓存排队队列（数据库和redis）异常",ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
            eventCenterLogService.saveEventLog(eventCenterTopicTaskRecord.getTaskNo(),"【事件中心队列处理阶段】--刷新缓存排队队列（数据库和redis）异常】",
                    eventCenterTopicTaskRecord.getTaskNo(), ErrorCodeEnum.SYSTEM_ERROR_STATUS.code(),e.getMessage()
                    , 0l, eventCenterTopicTaskRecord.getCreateAuthUserId());
        }
        return result;
    }

    /**
     * 处理排队队列缓存和数据库
     * @param eventCenterTopicTaskRecord
     * @return
     */
    @Override
    public JSONObject processCacheQueue(EventCenterTopicTaskRecord eventCenterTopicTaskRecord) {
        JSONObject result = new JSONObject();
        try {
            Long weight=eventCenterTopicTaskRecord.getWeight();
            if(weight==null) {
                weight=System.currentTimeMillis();
                eventCenterTopicTaskRecord.setWeight(weight);
            }
            long start=System.currentTimeMillis();
            JSONObject remarkJson=new JSONObject();
            remarkJson.put("业务","【事件中心队列处理阶段】--排队队列缓存和数据库");
            if("update".equals(eventCenterTopicTaskRecord.getOpt())&&eventCenterTopicTaskRecord.getId()!=null){
                eventCenterTopicTaskRecord.setPushStatus(eventCenterTopicTaskRecord.getPushStatus());
                eventCenterTopicTaskRecord.setPushTime(eventCenterTopicTaskRecord.getPushTime());
                eventCenterTopicTaskRecord.setUpdateTime(new Date());
                eventCenterTopicTaskRecord.setTaskNo(eventCenterTopicTaskRecord.getTaskNo());
                eventCenterTopicTaskRecordMapper.updateById(eventCenterTopicTaskRecord);
            }else{
                eventCenterTopicTaskRecord.setCreateTime(new Date());
                eventCenterTopicTaskRecordMapper.insert(eventCenterTopicTaskRecord);
            }
            remarkJson.put("操作",eventCenterTopicTaskRecord.getOpt());
            remarkJson.put("taskNo",eventCenterTopicTaskRecord.getTaskNo());
            eventCenterLogService.saveEventLog(eventCenterTopicTaskRecord.getTaskNo(),
                    "【事件中心队列处理阶段】--处理排队队列缓存和数据库",
                    JSONObject.toJSONString(eventCenterTopicTaskRecord), ErrorCodeEnum.SUCCESS.code(),remarkJson.toJSONString()
                    , (System.currentTimeMillis()-start), eventCenterTopicTaskRecord.getCreateAuthUserId());
            result.put("status", ErrorCodeEnum.SUCCESS.code());
            result.put("message", "成功");
            return result;
        }catch(Exception e){
            log.error("【事件中心队列处理阶段】{}接口-处理排队队列缓存和数据库业务异常",ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
            eventCenterLogService.saveEventLog(eventCenterTopicTaskRecord.getTaskNo(),"【事件中心队列处理阶段】--处理排队队列缓存和数据库业务】",
                    eventCenterTopicTaskRecord.getTaskNo(), ErrorCodeEnum.SYSTEM_ERROR_STATUS.code(),ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg()
                    , 0l, eventCenterTopicTaskRecord.getCreateAuthUserId());
        }finally {
        }
        return result;
    }

    /**
     * 队列处理结果
     * @param taskNo
     * @return
     */
    @Override
    public JSONObject processReturn(String taskNo,String mqMessageId,String mqReturnCode,String returnBody,long postTime) {
        JSONObject result = new JSONObject();
        try {
            JSONObject remarkJson = new JSONObject();
            StringBuffer log = new StringBuffer();
            remarkJson.put("业务", "【事件中心结果阶段】队列处理结果");
            log.append("[1]taskNo=").append(taskNo).append(",");
            EventCenterTopicTaskRecord model = eventCenterTopicTaskRecordMapper.selectDelayTaskByTaskNo(taskNo);
            if (EmptyUtil.isEmpty(model)) {
                log.append("[2]根据taskNo查询为null,");
                model = eventCenterTopicTaskRecordMapper.selectDelayTaskByMessageId(mqMessageId);
                log.append("[3]根据mqMessageId查询为null,");
            }
            if (EmptyUtil.isNotEmpty(model)) {
                String resultStr = "";
                if ((model.getRetryCount()<=EventCenterConstant.retryCount)) {
                    model.setMqMessageId(mqMessageId);
                    model.setReturnBody(returnBody);
                    model.setMqReturnCode(mqReturnCode);
                    log.append("[4]查询到model数据,mqReturnCode=").append(mqReturnCode).append(",");
                    //判断结果是true还是false
                    if (ErrorCodeEnum.SUCCESS.code().equals(mqReturnCode)) {
                        //移除当前队列和数据库
                        redisService.zremRangeByScore(Constants.MQ_QUEUE_TASK_REDIS_KEY, getScore(model), getScore(model));
                        model.setUpdateTime(new Date());
                        eventCenterTopicTaskRecordMapper.deleteById(model.getId());
                        resultStr += "业务处理成功," + returnBody + "，移除缓存队列和数据库。";
                    } else {
                        //重新刷新延迟时间和状态
                        model.setPushStatus(0);
                        model.setErrorRetryCount(model.getErrorRetryCount() + 1);
                        model.setUpdateTime(new Date());
                        resultStr = "业务处理失败，重新刷新延迟时间和状态,已重试" + model.getErrorRetryCount() + "次," + returnBody;
                        model.setReturnBody(resultStr);
                        eventCenterTopicTaskRecordMapper.updateById(model);
                    }
                    remarkJson.put("处理结果", resultStr);
                    log.append("[5]处理结果").append(resultStr);
                    String queueStr = "";
                    //重试机制，等待redis数据清0后 重新加载新数据
                    if (model.getErrorRetryCount()<=EventCenterConstant.retryCount) {
                        queueStr += batchSendDelayMsgPolicy(false, false);
                    }else{
                        redisService.zremRangeByScore(Constants.MQ_QUEUE_TASK_REDIS_KEY, getScore(model), getScore(model));
                    }
                    log.append("[6]队列信息").append(queueStr);
                    remarkJson.put("队列信息", queueStr);
                    log.append("[7]任务历史数据添加成功");
                    redisService.set(Constants.MQ_QUEUE_TASK_REDIS_KEY + "_uploadTime", System.currentTimeMillis() + "");
                    eventCenterLogService.saveEventLog(model.getTaskNo(), "【事件中心结果阶段】--事件中心处理结束",
                            JSONObject.toJSONString(model), mqReturnCode, remarkJson.toJSONString(), postTime, model.getCreateAuthUserId(), model.getRetryCount());
                    result.put("status", ErrorCodeEnum.SUCCESS);
                    result.put("message", "成功");
                    result.put("log", log);
                }else{
                    //移除当前队列和数据库
                    redisService.zremRangeByScore(Constants.MQ_QUEUE_TASK_REDIS_KEY, getScore(model), getScore(model));
                    result.put("status", ErrorCodeEnum.FAIL);
                    result.put("message", "失败，任务重试超限");
                    eventCenterLogService.saveEventLog(model.getTaskNo(), "【事件中心结果阶段】--任务重试超限",
                            JSONObject.toJSONString(model), mqReturnCode, remarkJson.toJSONString(),
                            postTime, model.getCreateAuthUserId(), model.getRetryCount());
                }
            } else {
                result.put("status", ErrorCodeEnum.FAIL);
                result.put("message", "失败，未查询到任务号" + taskNo + "相关任务");
            }
        }finally {
        }
        return result;
    }

    @Override
    public List<EventCenterTopicTaskRecord> selectTaskByLikeMsgBody(String taskMsgBody) {
        return eventCenterTopicTaskRecordMapper.selectTaskByLikeMsgBody(taskMsgBody);
    }
    @Override
    public void updatePushStatus() {
        eventCenterTopicTaskRecordMapper.updatePushStatus();
    }
    @Override
    public EventCenterTopicTaskRecord selectDelayTaskByMessageId(String mqMessageId) {
        return eventCenterTopicTaskRecordMapper.selectDelayTaskByMessageId(mqMessageId);
    }
    @Override
    public EventCenterTopicTaskRecord selectDelayTaskByTaskNo(String taskNo) {
        return eventCenterTopicTaskRecordMapper.selectDelayTaskByTaskNo(taskNo);
    }
    /**
     *  继续从数据库获取按照权重划分前150条排队队列数据根据权重同步到redis排队队列
     */
    public void refreshCacheQueue(){
        List<EventCenterTopicTaskRecord> mqDelayTasks=eventCenterTopicTaskRecordMapper.selectDelayTaskByWeight(EventCenterConstant.retryCount,delayQueueLength);
        redisService.remove(Constants.MQ_QUEUE_TASK_REDIS_KEY);
        if(EmptyUtil.isNotEmpty(mqDelayTasks)){
            for (EventCenterTopicTaskRecord eventCenterTopicTaskRecord:mqDelayTasks ) {
                redisService.zadd(Constants.MQ_QUEUE_TASK_REDIS_KEY,getScore(eventCenterTopicTaskRecord)
                        ,JSONObject.toJSONString(eventCenterTopicTaskRecord));
            }
        }
    }
    private double getScore(EventCenterTopicTaskRecord eventCenterTopicTaskRecord){
        Long score=eventCenterTopicTaskRecord.getWeight();
        return Double.valueOf(score).doubleValue();
    }
}
