package com.ymd.cloud.eventCenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.*;
import com.ymd.cloud.eventCenter.mapper.EventCenterLogMapper;
import com.ymd.cloud.eventCenter.mapper.EventCenterTopicTaskRecordMapper;
import com.ymd.cloud.eventCenter.model.vo.EventCenterLog;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRegister;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicTaskRecord;
import com.ymd.cloud.eventCenter.service.EventCenterLogService;
import com.ymd.cloud.eventCenter.service.RabbitMqTopicQueueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;


@Service
@Slf4j
public class EventCenterLogServiceImpl extends ServiceImpl<EventCenterLogMapper, EventCenterLog> implements EventCenterLogService {
    @Resource
    private EventCenterLogMapper eventCenterLogMapper;
    @Resource
    private EventCenterTopicTaskRecordMapper eventCenterTopicTaskRecordMapper;
    @Autowired
    RabbitMqTopicQueueService eventCenterTopicQueueService;
    public void saveEventLog(String taskNo,String postUrl,String param,String resultCode,String result,long start,String account,Integer retryCount){
        EventCenterTopicTaskRecord eventCenterTopicTaskRecord=eventCenterTopicTaskRecordMapper.selectDelayTaskByTaskNo(taskNo);
        String topic="event_center";
        String logType="event";
        if(EmptyUtil.isNotEmpty(eventCenterTopicTaskRecord)){
            topic=eventCenterTopicTaskRecord.getTopic();
            logType=eventCenterTopicQueueService.exchangeToJobType(eventCenterTopicTaskRecord.getExchange());
            if(EmptyUtil.isEmpty(account)){
                account=eventCenterTopicTaskRecord.getCreateAuthUserId();
            }
        }
        EventCenterLog eventCenterLog=new EventCenterLog();
        eventCenterLog.setTopic(topic);
        eventCenterLog.setTaskNo(taskNo);
        eventCenterLog.setLogType(logType);
        eventCenterLog.setCreateTime(new Date());
        eventCenterLog.setUpdateTime(new Date());
        eventCenterLog.setPostContent(postUrl);
        eventCenterLog.setParamBody(param);
        eventCenterLog.setResultCode(resultCode);
        eventCenterLog.setResult(result);
        eventCenterLog.setPostTime((System.currentTimeMillis()-start));
        eventCenterLog.setRetryCount(retryCount);
        eventCenterLog.setId(eventCenterLog.getId());
        eventCenterLog.setCreateAuthUserId(account);
        eventCenterLog.setPartitionDate(Integer.valueOf(DateUtil.format(new Date(),DateUtil.yyyyMM)));
        saveLog(eventCenterLog);
    }

    @Override
    public void saveEventLog(String taskNo, String postUrl, String param, String resultCode, String result, long start, String account) {
        saveEventLog(taskNo,postUrl,param,resultCode,result,start,account,0);
    }


    @Override
    public int selectExistByTaskNo(String taskNo) {
        return eventCenterLogMapper.selectExistByTaskNo(taskNo);
    }

    /**
     * 更新异步转同步算法方法
     * @param taskNo
     * @param timeStemp
     * @return
     */
    @Override
    public List<EventCenterLog> listenResult(String taskNo, Long timeStemp) {
        List<EventCenterLog> results=null;
        try {
            long timeoutMills = 30000;
            final long endTimeMills = timeStemp + timeoutMills;
            while (EmptyUtil.isEmpty(results)) {
                long currTime=System.currentTimeMillis();
                // 超时判断
                if( currTime>= endTimeMills) {
                    log.info("异步转同步结果超时, 开始时间:{} 超时时间点:{} 设置超时{}秒", DateUtil.format(timeStemp),DateUtil.format(endTimeMills),timeoutMills/1000 );
                    return results;
                }else {
                    //判断任务是否完成
                    results=eventCenterLogMapper.selectLogListByTaskNo(taskNo);
                    if(EmptyUtil.isEmpty(results)) {
                        //循环等待一下
                        TimeUnit.MILLISECONDS.sleep(200);
                    }
                }
            }
        } catch (InterruptedException e) {
        }
        return results;
    }

    @Override
    public PageInfo<EventCenterLog> pageEventCenterLogList(PageRequest request, EventCenterLog model) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<EventCenterLog> list=eventCenterLogMapper.selectEventCenterLogPage(model);
        return new PageInfo<>(list);
    }

    @Override
    public JSONObject saveLog(EventCenterLog eventCenterLog) {
        JSONObject result = new JSONObject();
        try {
            ParamValiUtil.checkArgumentNotEmpty(eventCenterLog.getTopic(),"主题名称topic");
            ParamValiUtil.checkArgumentNotEmpty(eventCenterLog.getLogType(),"日志分类");
            ParamValiUtil.checkArgumentNotEmpty(eventCenterLog.getTaskNo(),"事件号");
            eventCenterLog.setCreateTime(new Date());
            //表分区
            eventCenterLog.setPartitionDate(Integer.valueOf(DateUtil.format(new Date(),DateUtil.yyyyMM)));
            this.baseMapper.insert(eventCenterLog);
            result.put("status", ErrorCodeEnum.SUCCESS.code());
            result.put("message", "成功");
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }
}
