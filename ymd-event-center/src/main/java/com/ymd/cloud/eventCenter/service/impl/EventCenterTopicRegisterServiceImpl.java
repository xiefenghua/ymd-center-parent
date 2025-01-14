package com.ymd.cloud.eventCenter.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.common.enumsSupport.Constants;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.ClzUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.common.utils.PageRequest;
import com.ymd.cloud.common.utils.ParamValiUtil;
import com.ymd.cloud.eventCenter.config.TopicConfig;
import com.ymd.cloud.eventCenter.mapper.EventCenterRuleMapper;
import com.ymd.cloud.eventCenter.mapper.EventCenterTopicRegisterMapper;
import com.ymd.cloud.eventCenter.mapper.EventCenterTopicRuleMapper;
import com.ymd.cloud.eventCenter.model.vo.EventCenterRule;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRegister;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRule;
import com.ymd.cloud.eventCenter.service.EventCenterLogService;
import com.ymd.cloud.eventCenter.service.EventCenterTopicRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class EventCenterTopicRegisterServiceImpl extends ServiceImpl<EventCenterTopicRegisterMapper,EventCenterTopicRegister> implements EventCenterTopicRegisterService {
    @Resource
    EventCenterTopicRuleMapper eventCenterTopicRuleMapper;
    @Resource
    EventCenterRuleMapper eventCenterRuleMapper;
    @Autowired
    EventCenterLogService eventCenterLogService;
    @Override
    public PageInfo<EventCenterTopicRegister> pageEventCenterTopicRegisterList(PageRequest request, EventCenterTopicRegister model) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<EventCenterTopicRegister> list=this.baseMapper.selectEventCenterTopicRegisterPage(model);
        return new PageInfo<>(list);
    }
    @Override
    public JSONObject saveTopic(EventCenterTopicRegister model) {
        JSONObject result = new JSONObject();
        try {
            ParamValiUtil.checkArgumentNotEmpty(model.getJobType(), "业务分类");
            ParamValiUtil.checkArgumentNotEmpty(model.getType(), "主题类别");
            ParamValiUtil.checkArgumentNotEmpty(model.getOptType(), "操作类型");
            ParamValiUtil.checkArgumentNotEmpty(model.getCallBackType(), "回调接口形式");
            if (EmptyUtil.isNotEmpty(model.getTopic())) {
                ParamValiUtil.checkRegEx("主题名称topic", model.getTopic(), "[\u4e00-\u9fa5]", "不允许存在中文");
            }
            ParamValiUtil.checkRegEx("业务分类", model.getJobType(), "[\u4e00-\u9fa5]", "不允许存在中文");
            ParamValiUtil.checkRegEx("主题类别", model.getType(), "[\u4e00-\u9fa5]", "不允许存在中文");
            ParamValiUtil.checkRegEx("操作类型", model.getOptType(), "[\u4e00-\u9fa5]", "不允许存在中文");
            if (model.getCallBackType() == 1) {//内部调用=1
                ParamValiUtil.checkArgumentNotEmpty(model.getClassName(), "类名称");
            } else if (model.getCallBackType() == 2) {//http形式=2
                ParamValiUtil.checkArgumentNotEmpty(model.getHttpUrl(), "http地址");
            }
            EventCenterTopicRegister eventCenterTopicRegister = this.baseMapper.selectByType(model.getType());
            if (EmptyUtil.isNotEmpty(eventCenterTopicRegister)) {
                result.put("status", ErrorCodeEnum.MESSAGE_PARAM_LESS.code());
                result.put("message", "主题类别不允许重复");
                return result;
            }

            if (EmptyUtil.isNotEmpty(model.getTopic())) {
                EventCenterTopicRegister queryModel = this.baseMapper.selectByTopic(model.getTopic());
                if (EmptyUtil.isNotEmpty(queryModel)) {
                    result.put("status", ErrorCodeEnum.MESSAGE_PARAM_LESS);
                    result.put("message", "topic不允许重复");
                    return result;
                }
            } else {
                //  topic：主题名称topic(业务类型_主题类别_操作类型_topic)
                String topic = generateTopic(model);
                model.setTopic(topic);
            }
            model.setCreateTime(new Date());
            model.setStatus(Constants.STATUS_CODE);
            this.baseMapper.insert(model);
            if (model.getRuleId() != null) {
                EventCenterTopicRule eventCenterTopicRule = new EventCenterTopicRule();
                eventCenterTopicRule.setRuleId(model.getRuleId());
                eventCenterTopicRule.setTopicId(model.getId());
                doBindRule(eventCenterTopicRule);
            }
            result.put("status", ErrorCodeEnum.SUCCESS.code());
            result.put("message", "成功");
            result.put("topic", model.getTopic());
            return result;
        }catch(Exception e){
            log.error("【{}接口--业务】异常", ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }
    public String generateTopic(EventCenterTopicRegister model) {
        String topic= TopicConfig.topicFix+model.getJobType()+"_"+model.getType()+"_"+model.getOptType()+"_topic";
        EventCenterTopicRegister queryModel = this.baseMapper.selectByTopic(topic);
        if(EmptyUtil.isNotEmpty(queryModel)) {
            return generateTopic(model);
        }
        return topic;
    }
    @Override
    public JSONObject delTopic(String topic, String currUserAccount) {
        JSONObject result = new JSONObject();
        try {
            ParamValiUtil.checkArgumentNotEmpty(topic,"topic");
            EventCenterTopicRegister model = this.baseMapper.selectByTopic(topic);
            if(EmptyUtil.isNotEmpty(model)) {
                model.setStatus(Constants.STATUS_DEL_CODE);
                model.setUpdateTime(new Date());
                this.baseMapper.updateById(model);
                result.put("status", ErrorCodeEnum.SUCCESS.code());
                result.put("message", "成功");
                return result;
            }else{
                result.put("status", ErrorCodeEnum.FAIL.code());
                result.put("message", "topic不存在");
                return result;
            }
        }catch(Exception e){
            log.error("【{}接口--业务】异常",ClzUtil.getMethodName(),e);
            result.put("status", ErrorCodeEnum.SYSTEM_ERROR_STATUS.code());
            result.put("message", ErrorCodeEnum.SYSTEM_ERROR_STATUS.msg());
        }
        return result;
    }

    @Override
    public JSONObject doBindRule(EventCenterTopicRule model) {
        JSONObject result = new JSONObject();
        try {
            ParamValiUtil.checkArgumentNotEmpty(model.getTopicId(),"topicId");
            ParamValiUtil.checkArgumentNotEmpty(model.getRuleId(),"规则id");
            EventCenterTopicRegister eventCenterTopicRegister =this.baseMapper.selectById(model.getTopicId());
            if(EmptyUtil.isEmpty(eventCenterTopicRegister)){
                result.put("status",  ErrorCodeEnum.SYSTEM_PARAM_FORMAT_ERROR.code());
                result.put("message", "topic不存在");
                return result;
            }
            EventCenterRule eventCenterRule=eventCenterRuleMapper.selectById(model.getRuleId());
            if(EmptyUtil.isEmpty(eventCenterRule)){
                result.put("status", ErrorCodeEnum.SYSTEM_PARAM_FORMAT_ERROR.code());
                result.put("message", "规则不存在");
                return result;
            }
            if(eventCenterTopicRegister.getStatus()==Constants.STATUS_DEL_CODE){
                result.put("status", ErrorCodeEnum.SYSTEM_PARAM_FORMAT_ERROR.code());
                result.put("message", "topic可不用");
                return result;
            }
            if(eventCenterRule.getRuleStatus()==Constants.STATUS_DEL_CODE){
                result.put("status", ErrorCodeEnum.SYSTEM_PARAM_FORMAT_ERROR.code());
                result.put("message", "规则可不用");
                return result;
            }
            EventCenterTopicRule query= eventCenterTopicRuleMapper.selectByTopicIdAndRuleId(model);
            if(EmptyUtil.isNotEmpty(query)){
                model.setUpdateTime(new Date());
                model.setId(query.getId());
                eventCenterTopicRuleMapper.updateById(model);
            }else {
                model.setCreateTime(new Date());
                eventCenterTopicRuleMapper.insert(model);
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

    @Override
    public List<String> selectAllMqttTopicList(String topicFix)    {
        return this.baseMapper.selectAllMqttTopicList(topicFix);
    }

    @Override
    public List<EventCenterTopicRegister> selectSystemInitAllRabbitMqTopic() {
        return this.baseMapper.selectSystemInitAllRabbitMqTopic();
    }

    @Override
    public List<String> selectAllRabbitMqJobType() {
        return this.baseMapper.selectAllRabbitMqJobType();
    }

    @Override
    public EventCenterTopicRegister selectByTopic(String topic) {
        return this.baseMapper.selectByTopic(topic);
    }

    @Override
    public EventCenterTopicRegister selectByType(String topicType) {
        return this.baseMapper.selectByType(topicType);
    }

    @Override
    public EventCenterTopicRegister selectByJobType(String jobType) {
        return this.baseMapper.selectByJobType(jobType);
    }
}
