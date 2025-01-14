package com.ymd.cloud.eventCenter.web;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.common.base.AbstractController;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.ConvertRequestUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.common.utils.PageRequest;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRegister;
import com.ymd.cloud.eventCenter.model.vo.EventCenterTopicRule;
import com.ymd.cloud.eventCenter.service.EventCenterTopicRegisterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("ymd/web/center/eventCenter/topic")
public class EventCenterTopicRegisterController extends AbstractController {
    @Autowired
    private EventCenterTopicRegisterService eventCenterTopicRegisterService;
    /**
     * topic列表接口
     * @param param 接口请求数据
     * @return 接口返回数据
     */
    @RequestMapping("/list")
    public ResponseEntity<ModelMap> list(EventCenterTopicRegister param){
        try {
            log.info("【topic列表接口---业务】 参数：{}", JSONObject.toJSONString(param));
            String createAuthUserId = param.getCreateAuthUserId();
            param.setCreateAuthUserId(createAuthUserId);
            PageRequest pageRequest= ConvertRequestUtil.pageRequest();
            pageRequest.setPageNum(EmptyUtil.isNotEmpty(pageRequest.getPageNum())?pageRequest.getPageNum():10);
            pageRequest.setPageSize(EmptyUtil.isNotEmpty(pageRequest.getPageSize())?pageRequest.getPageSize():1);
            PageInfo<EventCenterTopicRegister> pageInfo = eventCenterTopicRegisterService.pageEventCenterTopicRegisterList(pageRequest, param);
            log.info("【topic列表接口---业务】 ,返回：{}", JSONObject.toJSONString(pageInfo));
            return setSuccessModelMap(pageInfo);
        }catch (Exception e){
            log.error("topic列表接口--error:",e);
            return setModelMap(ErrorCodeEnum.SYSTEM_ERROR_STATUS.code(),e.getMessage());
        }
    }
    @RequestMapping("/register")
    public ResponseEntity<ModelMap> register(@RequestBody EventCenterTopicRegister param){
        try {
            log.info("【topic注册接口---业务】 参数：{}",JSONObject.toJSONString(param));
            String createAuthUserId = param.getCreateAuthUserId();
            param.setCreateAuthUserId(createAuthUserId);
            JSONObject result = eventCenterTopicRegisterService.saveTopic(param);
            log.info("【topic注册接口---业务】 ,返回：{}", result.toJSONString());
            return setSuccessModelMap(result);
        }catch (Exception e){
            log.error("topic注册接口--error:",e);
            return setModelMap(ErrorCodeEnum.SYSTEM_ERROR_STATUS.code(),e.getMessage());
        }
    }
    @RequestMapping("/del")
    public ResponseEntity<ModelMap> del(@RequestBody EventCenterTopicRegister param){
        try {
            String createAuthUserId = param.getCreateAuthUserId();
            log.info("【topic删除接口---业务】 参数：{}",JSONObject.toJSONString(param));
            JSONObject result = eventCenterTopicRegisterService.delTopic(param.getTopic(),createAuthUserId);
            log.info("【topic删除接口---业务】 ,返回：{}", result.toJSONString());
            return setSuccessModelMap(result);
        }catch (Exception e){
            log.error("topic删除接口--error:",e);
            return setModelMap(ErrorCodeEnum.SYSTEM_ERROR_STATUS.code(),e.getMessage());
        }
    }
    @RequestMapping("/bindTopic")
    public ResponseEntity<ModelMap> bindTopic(@RequestBody EventCenterTopicRule param){
        try {
            String createAuthUserId = param.getCreateAuthUserId();
            param.setCreateAuthUserId(createAuthUserId);
            log.info("【topic绑定规则接口---业务】 参数：{}",JSONObject.toJSONString(param));
            JSONObject result = eventCenterTopicRegisterService.doBindRule(param);
            log.info("【topic绑定规则接口---业务】 ,返回：{}", result.toJSONString());
            return setSuccessModelMap(result);
        }catch (Exception e){
            log.error("topic绑定规则接口---error:",e);
            return setModelMap(ErrorCodeEnum.SYSTEM_ERROR_STATUS.code(),e.getMessage());
        }
    }
}
