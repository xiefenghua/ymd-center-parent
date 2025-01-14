package com.ymd.cloud.eventCenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.ClzUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.common.utils.PageRequest;
import com.ymd.cloud.common.utils.ParamValiUtil;
import com.ymd.cloud.eventCenter.mapper.EventCenterTopicTaskRecordMapper;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicTaskRecord;
import com.ymd.cloud.eventCenter.service.EventCenterTopicTaskRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class EventCenterTopicTaskRecordServiceImpl extends ServiceImpl<EventCenterTopicTaskRecordMapper,EventCenterTopicTaskRecord> implements EventCenterTopicTaskRecordService {

    @Override
    public PageInfo<EventCenterTopicTaskRecord> pageEventCenterTopicTaskRecordList(PageRequest request, EventCenterTopicTaskRecord model) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<EventCenterTopicTaskRecord> list=this.baseMapper.selectEventCenterTopicTaskRecordPage(model);
        return new PageInfo<>(list);
    }

    @Override
    public JSONObject saveTaskRecord(EventCenterTopicTaskRecord model) {
        JSONObject result = new JSONObject();
        try {
            ParamValiUtil.checkArgumentNotEmpty(model.getTopic(),"主题名称topic");
            ParamValiUtil.checkArgumentNotEmpty(model.getTaskNo(),"事件号");
            model.setCreateTime(new Date());
            this.baseMapper.insert(model);
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

    @Override
    public JSONObject updateTaskRecord(EventCenterTopicTaskRecord model) {
        JSONObject result = new JSONObject();
        try {
            ParamValiUtil.checkArgumentNotEmpty(model.getTopic(),"主题名称topic");
            ParamValiUtil.checkArgumentNotEmpty(model.getTaskNo(),"事件号");
            EventCenterTopicTaskRecord eventCenterTopicTaskRecord=this.baseMapper.selectEventCenterTopicTaskRecordByTopicAndTaskNo(model);
            if(EmptyUtil.isNotEmpty(eventCenterTopicTaskRecord)){
                eventCenterTopicTaskRecord.setUpdateTime(new Date());
                this.baseMapper.updateById(eventCenterTopicTaskRecord);
            }
            result.put("status", ErrorCodeEnum.SUCCESS.code());
            result.put("message", "成功");
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常",ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }
}
