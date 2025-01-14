package com.ymd.cloud.eventCenter.web;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.common.base.AbstractController;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.ConvertRequestUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.common.utils.PageRequest;
import com.ymd.cloud.eventCenter.model.vo.EventCenterLog;
import com.ymd.cloud.eventCenter.service.EventCenterLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@Controller
@RequestMapping("ymd/web/center/eventCenter/log")
public class EventCenterLogController extends AbstractController {
    @Autowired
    private EventCenterLogService eventCenterLogService;
    /**
     * 事件日志列表
     * @param param 接口请求数据
     * @return 接口返回数据
     */
    @RequestMapping("/list")
    public ResponseEntity<ModelMap> list(EventCenterLog param){
        try {
            log.info("【事件日志列表---业务】 参数：{}", JSONObject.toJSONString(param));
            String createAuthUserId = param.getCreateAuthUserId();
            param.setCreateAuthUserId(createAuthUserId);
            PageRequest pageRequest= ConvertRequestUtil.pageRequest();
            pageRequest.setPageNum(EmptyUtil.isNotEmpty(pageRequest.getPageNum())?pageRequest.getPageNum():10);
            pageRequest.setPageSize(EmptyUtil.isNotEmpty(pageRequest.getPageSize())?pageRequest.getPageSize():1);
            PageInfo<EventCenterLog> pageInfo = eventCenterLogService.pageEventCenterLogList(pageRequest, param);
            log.info("【事件日志列表---业务】 ,返回：{}", JSONObject.toJSONString(pageInfo));
            return setSuccessModelMap(pageInfo);
        }catch (Exception e){
            log.error("事件日志列表--error:",e);
            return setModelMap(ErrorCodeEnum.SYSTEM_ERROR_STATUS.code(),e.getMessage());
        }
    }

}
