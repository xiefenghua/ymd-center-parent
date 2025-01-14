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
import com.ymd.cloud.eventCenter.mapper.EventCenterRuleMapper;
import com.ymd.cloud.eventCenter.model.vo.EventCenterRule;
import com.ymd.cloud.eventCenter.service.EventCenterRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;


@Service
@Slf4j
public class EventCenterRuleServiceImpl extends ServiceImpl<EventCenterRuleMapper,EventCenterRule> implements EventCenterRuleService {

    @Override
    public PageInfo<EventCenterRule> pageEventCenterRuleList(PageRequest request, EventCenterRule model) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize());
        List<EventCenterRule> list=this.baseMapper.selectEventCenterRulePage(model);
        return new PageInfo<>(list);
    }

    @Override
    public JSONObject saveRule(EventCenterRule model) {
        JSONObject result = new JSONObject();
        try {
            model.setRuleIsAll(EmptyUtil.isEmpty(model.getRuleIsAll())?0:model.getRuleIsAll());
            if(model.getRuleIsAll()==1){
                EventCenterRule allExist=this.baseMapper.selectIsAllExist();
                if(EmptyUtil.isNotEmpty(allExist)){
                    result.put("status", ErrorCodeEnum.FAIL.code());
                    result.put("message", "全局规则已存在");
                    return result;
                }
            }
            model.setRuleStatus(Constants.STATUS_CODE);
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
    public JSONObject delRule(String ids, String createAuthUserId) {
        JSONObject result = new JSONObject();
        try {
            ParamValiUtil.checkArgumentNotEmpty(ids,"ids");
            String [] idsArr=ids.split(",");
            for (String id:idsArr) {
                EventCenterRule model=this.baseMapper.selectById(id);
                if(EmptyUtil.isNotEmpty(model)){
                    model.setRuleStatus(Constants.STATUS_DEL_CODE);
                    model.setUpdateTime(new Date());
                    if(EmptyUtil.isNotEmpty(createAuthUserId)) {
                        model.setCreateAuthUserId(createAuthUserId);
                    }
                    this.baseMapper.updateById(model);
                }
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
    public JSONObject updateRule(EventCenterRule model) {
        JSONObject result = new JSONObject();
        try {
            ParamValiUtil.checkArgumentNotEmpty(model.getId(),"id");
            if(model.getRuleIsAll()!=null&&model.getRuleIsAll()==1){
                EventCenterRule allExist=this.baseMapper.selectIsAllExist();
                if(EmptyUtil.isNotEmpty(allExist)){
                    result.put("status", ErrorCodeEnum.FAIL.code());
                    result.put("message", "全局规则已存在");
                    return result;
                }
            }
            model.setRuleStatus(Constants.STATUS_CODE);
            model.setUpdateTime(new Date());
            this.baseMapper.updateById(model);
            result.put("status", ErrorCodeEnum.SUCCESS);
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
    public EventCenterRule selectIsAllExist() {
        return this.baseMapper.selectIsAllExist();
    }
}
