package com.ymd.cloud.authorizeCenter.client.controller;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTask;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTaskNotes;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTaskResult;
import com.ymd.cloud.authorizeCenter.model.entity.TaskParam;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskNotesService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskResultService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeService;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.PageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("${ymd.client.api}/client/task")
@Api(value = "授权任务中心", tags = "客户端授权任务中心接口")
public class AuthorizeTaskResultController {
    @Autowired
    AuthorizeCenterTaskService authorizeCenterTaskService;
    @Autowired
    AuthorizeCenterTaskResultService authorizeCenterTaskResultService;
    //=============================授权任务相关接口==================================
    @ApiOperation(value = "根据授权任务号获取任务")
    @PostMapping(value = "/get")
    public JSONObject get(@RequestBody TaskParam model) {
        log.info("客户端授权任务中心---根据授权任务号获取任务 参数:{}",JSONObject.toJSONString(model));
        JSONObject result = new JSONObject();
        AuthorizeCenterTask authorizeCenterTask= authorizeCenterTaskService.selectByTaskNo(model.getTaskNo());
        result.put("data", authorizeCenterTask);
        result.put("status", ErrorCodeEnum.SUCCESS.code());
        result.put("message", "成功");
        log.info("客户端授权任务中心---根据授权任务号获取任务 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "根据授权任务号更新同步状态")
    @PostMapping(value = "/updateSync")
    public JSONObject updateSync(@RequestBody TaskParam model) {
        log.info("客户端授权任务中心---根据授权任务号更新同步状态 参数:{}",JSONObject.toJSONString(model));
        JSONObject result = new JSONObject();
        authorizeCenterTaskService.updateSyncStatusByTaskNo(model.getTaskNo(),model.getTaskStatus(),model.getExceptionDesc());
        result.put("status", ErrorCodeEnum.SUCCESS.code());
        result.put("message", "成功");
        log.info("客户端授权任务中心---根据授权任务号更新同步状态 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    //=============================授权任务结果记录相关接口==================================
    @ApiOperation(value = "查询授权任务结果记录列表")
    @PostMapping(value = "/result/list")
    public PageInfo<AuthorizeCenterTaskResult> resultList(PageRequest pageRequest, @RequestBody TaskParam model) {
        log.info("客户端授权任务中心---查询授权任务结果记录列表 参数:{}",JSONObject.toJSONString(model));
        JSONObject result = new JSONObject();
        PageInfo<AuthorizeCenterTaskResult> list=authorizeCenterTaskResultService.listPage(pageRequest,model);
        log.info("客户端授权任务中心---查询授权任务结果记录列表 响应:{}",JSONObject.toJSONString(result));
        return list;
    }


}
