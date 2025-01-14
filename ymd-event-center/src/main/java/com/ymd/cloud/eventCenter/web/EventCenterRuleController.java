package com.ymd.cloud.eventCenter.web;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.common.base.AbstractController;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.ConvertRequestUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.common.utils.PageRequest;
import com.ymd.cloud.eventCenter.model.vo.EventCenterRule;
import com.ymd.cloud.eventCenter.service.EventCenterRuleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("ymd/web/center/eventCenter/rule")
public class EventCenterRuleController extends AbstractController {
    @Autowired
    private EventCenterRuleService eventCenterRuleService;
    /**
     * 规则配置列表
     * @param param 接口请求数据
     * @return 接口返回数据
     */
    @RequestMapping("/list")
    public ResponseEntity<ModelMap> list(EventCenterRule param){
        try {
            log.info("【规则配置列表---业务】 参数：{}", JSONObject.toJSONString(param));
            String createAuthUserId = param.getCreateAuthUserId();
            param.setCreateAuthUserId(createAuthUserId);
            PageRequest pageRequest= ConvertRequestUtil.pageRequest();
            pageRequest.setPageNum(EmptyUtil.isNotEmpty(pageRequest.getPageNum())?pageRequest.getPageNum():10);
            pageRequest.setPageSize(EmptyUtil.isNotEmpty(pageRequest.getPageSize())?pageRequest.getPageSize():1);
            PageInfo<EventCenterRule> pageInfo = eventCenterRuleService.pageEventCenterRuleList(pageRequest, param);
            log.info("【规则配置列表---业务】 ,返回：{}", JSONObject.toJSONString(pageInfo));
            return setSuccessModelMap(pageInfo);
        }catch (Exception e){
            log.error("规则配置列表--error:",e);
            return setModelMap(ErrorCodeEnum.SYSTEM_ERROR_STATUS.code(),e.getMessage());
        }
    }
    /**
     * 规则生成接口
     * @param param 接口请求数据
     * @return 接口返回数据
     */
    @RequestMapping("/add")
    public ResponseEntity<ModelMap> add(@RequestBody EventCenterRule param){
        try {
            log.info("【规则生成接口---业务】 参数：{}",JSONObject.toJSONString(param));
            String createAuthUserId = param.getCreateAuthUserId();
            param.setCreateAuthUserId(createAuthUserId);
            JSONObject result = eventCenterRuleService.saveRule(param);
            log.info("【规则生成接口---业务】 ,返回：{}", result.toJSONString());
            return setSuccessModelMap(result);
        }catch (Exception e){
            log.error("规则生成接口--error:",e);
            return setModelMap(ErrorCodeEnum.SYSTEM_ERROR_STATUS.code(),e.getMessage());
        }
    }
    /**
     * 规则修改接口
     * @param param 接口请求数据
     * @return 接口返回数据
     */
    @RequestMapping("/update")
    public ResponseEntity<ModelMap> update(@RequestBody EventCenterRule param){
        try {
            log.info("【规则修改接口---业务】 参数：{}",JSONObject.toJSONString(param));
            String createAuthUserId = param.getCreateAuthUserId();
            param.setCreateAuthUserId(createAuthUserId);
            JSONObject result = eventCenterRuleService.updateRule(param);
            log.info("【规则修改接口---业务】 ,返回：{}", result.toJSONString());
            return setSuccessModelMap(result);
        }catch (Exception e){
            log.error("规则修改接口--error:",e);
            return setModelMap(ErrorCodeEnum.SYSTEM_ERROR_STATUS.code(),e.getMessage());
        }
    }
    /**
     * 规则删除接口
     * @param param 接口请求数据
     * @return 接口返回数据
     */
    @RequestMapping("/del")
    public ResponseEntity<ModelMap> del(@RequestBody JSONObject param){
        try {
            log.info("【规则删除接口---业务】 参数：{}",JSONObject.toJSONString(param));
            String createAuthUserId = param.getString("createAuthUserId");
            JSONObject result = eventCenterRuleService.delRule(param.getString("id"),createAuthUserId);
            log.info("【规则删除接口---业务】 ,返回：{}", result.toJSONString());
            return setSuccessModelMap(result);
        }catch (Exception e){
            log.error("规则删除接口--error:",e);
            return setModelMap(ErrorCodeEnum.SYSTEM_ERROR_STATUS.code(),e.getMessage());
        }
    }

}
