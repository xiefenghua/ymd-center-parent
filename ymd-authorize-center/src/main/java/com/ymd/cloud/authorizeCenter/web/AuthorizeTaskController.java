package com.ymd.cloud.authorizeCenter.web;

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
@RequestMapping("ymd/web/center/authorizeCenter/task")
@Api(value = "授权任务中心", tags = "授权任务中心接口")
public class AuthorizeTaskController {
    @Autowired
    AuthorizeCenterTaskService authorizeCenterTaskService;
    @Autowired
    AuthorizeCenterTaskNotesService authorizeCenterTaskNotesService;
    @Autowired
    AuthorizeCenterTaskResultService authorizeCenterTaskResultService;
    @Autowired
    AuthorizeService authorizeService;
    //=============================授权任务相关接口==================================
    @ApiOperation(value = "授权任务列表")
    @PostMapping(value = "/pageList")
    public PageInfo<AuthorizeCenterTask> taskPageList(PageRequest pageRequest, @RequestBody TaskParam model) {
        log.info("授权任务列表 参数:{}",JSONObject.toJSONString(model));
        PageInfo<AuthorizeCenterTask> list=authorizeCenterTaskService.listPage(pageRequest,model);
        log.info("授权任务列表 响应:{}",JSONObject.toJSONString(list));
        return list;
    }

    @ApiOperation(value = "根据授权任务号获取任务")
    @PostMapping(value = "/getByTaskNo")
    public JSONObject getByTaskNo(@RequestBody TaskParam model) {
        log.info("根据授权任务号获取任务 参数:{}",JSONObject.toJSONString(model));
        JSONObject result = new JSONObject();
        AuthorizeCenterTask authorizeCenterTask= authorizeCenterTaskService.selectByTaskNo(model.getTaskNo());
        result.put("data", authorizeCenterTask);
        result.put("status", ErrorCodeEnum.SUCCESS.code());
        result.put("message", "成功");
        log.info("根据授权任务号获取任务 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "根据授权任务号更新同步状态")
    @PostMapping(value = "/updateSyncStatusByTaskNo")
    public JSONObject updateSyncStatusByTaskNo(@RequestBody TaskParam model) {
        log.info("根据授权任务号更新同步状态 参数:{}",JSONObject.toJSONString(model));
        JSONObject result = new JSONObject();
        authorizeCenterTaskService.updateSyncStatusByTaskNo(model.getTaskNo(),model.getTaskStatus(),model.getExceptionDesc());
        result.put("status", ErrorCodeEnum.SUCCESS.code());
        result.put("message", "成功");
        log.info("根据授权任务号更新同步状态 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "删除过期授权任务")
    @PostMapping(value = "/delBefore1Month")
    public JSONObject delBefore1Month(@RequestBody TaskParam model) {
        log.info("删除过期授权任务 参数:{}",JSONObject.toJSONString(model));
        JSONObject result = new JSONObject();
        authorizeCenterTaskService.delTaskBefore1Month();
        result.put("status", ErrorCodeEnum.SUCCESS.code());
        result.put("message", "成功");
        log.info("删除过期授权任务 响应:{}",JSONObject.toJSONString(result));
        return result;
    }

    @ApiOperation(value = "重新处理失败授权得任务")
    @PostMapping(value = "/processNoFinish")
    public JSONObject processNoFinish(@RequestBody TaskParam model) {
        log.info("重新处理失败授权得任务 参数:{}",JSONObject.toJSONString(model));
        JSONObject result = new JSONObject();
        authorizeService.processTaskByNoFinish();
        result.put("status", ErrorCodeEnum.SUCCESS.code());
        result.put("message", "成功");
        log.info("重新处理失败授权得任务 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    //=============================授权任务执行记录相关接口==================================
    @ApiOperation(value = "授权任务执行记录列表")
    @PostMapping(value = "/notes/list")
    public PageInfo<AuthorizeCenterTaskNotes>  notesList(PageRequest pageRequest, @RequestBody TaskParam model) {
        log.info("授权任务执行记录列表 参数:{}",JSONObject.toJSONString(model));
        JSONObject result = new JSONObject();
        PageInfo<AuthorizeCenterTaskNotes> list=authorizeCenterTaskNotesService.listPage(pageRequest,model);
        log.info("授权任务执行记录列表 响应:{}",JSONObject.toJSONString(result));
        return list;
    }
    //=============================授权任务结果记录相关接口==================================
    @ApiOperation(value = "查询授权任务结果记录列表")
    @PostMapping(value = "/result/list")
    public PageInfo<AuthorizeCenterTaskResult> resultList(PageRequest pageRequest, @RequestBody TaskParam model) {
        log.info("查询授权任务结果记录列表 参数:{}",JSONObject.toJSONString(model));
        JSONObject result = new JSONObject();
        PageInfo<AuthorizeCenterTaskResult> list=authorizeCenterTaskResultService.listPage(pageRequest,model);
        log.info("查询授权任务结果记录列表 响应:{}",JSONObject.toJSONString(result));
        return list;
    }


}
