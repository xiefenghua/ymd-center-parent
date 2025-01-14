package com.ymd.cloud.authorizeCenter.web;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.ymd.cloud.authorizeCenter.enums.AuthConstants;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.entity.*;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeService;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.common.utils.PageRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("ymd/web/center/authorizeCenter")
@Api(value = "授权中心", tags = "授权中心接口")
public class AuthorizeController {
    @Autowired
    AuthorizeService authorizeService;
    @Autowired
    AuthorizeCenterTaskService authorizeCenterTaskService;
    @ApiOperation(value = "人员授权")
    @PostMapping(value = "/personAuth")
    public JSONObject addUserAuth(@RequestBody ChannelAuthorizeEntity model) {
        log.info(AuthConstants.personAuthResource+" 参数:{}",JSONObject.toJSONString(model));
        BaseAuthorizeVo baseAuthorizeVo=model.getBaseAuthorizeVo();
        baseAuthorizeVo.setAuthResource(AuthConstants.personAuthResource);
        baseAuthorizeVo.setAuthDesc(AuthConstants.personAuthResource);
        model.setBaseAuthorizeVo(baseAuthorizeVo);
        JSONObject result=channelAuth(model);
        log.info(AuthConstants.personAuthResource+" 响应:{}",JSONObject.toJSONString(result));
        return result;
    }

    @ApiOperation(value = "介质授权")
    @PostMapping(value = "/channelAuth")
    public JSONObject channelAuth(@RequestBody ChannelAuthorizeEntity model) {
        BaseAuthorizeVo baseAuthorizeVo=model.getBaseAuthorizeVo();
        String authResource=EmptyUtil.isNotEmpty(baseAuthorizeVo.getAuthResource())?baseAuthorizeVo.getAuthResource():AuthConstants.channelAuthResource;
        log.info(authResource+" 参数:{}",JSONObject.toJSONString(model));
        JSONObject result = new JSONObject();
        //TODO 用户中心获取当前登录人信息
        String currAccount ="13161337653";
        String orgId ="000000001";

        baseAuthorizeVo.setOrgId(orgId);
        baseAuthorizeVo.setAuthResource(authResource);
        baseAuthorizeVo.setCreateUserAccount(currAccount);
        List<ChannelAuthLockVo> channelAuthLockVoList =model.getChannelAuthLockVoList();
        List<ChannelAuthUserVo> channelAuthUserVoList =model.getChannelAuthUserVoList();
        if (EmptyUtil.isEmpty(baseAuthorizeVo.getStartTime()) || EmptyUtil.isEmpty(baseAuthorizeVo.getEndTime())) {
            result.put("status", ErrorCodeEnum.FAIL.code());
            result.put("message","系统检测没有相关授权开始或者结束时间");
            return result;
        } else if (EmptyUtil.isEmpty(channelAuthLockVoList)) {
            result.put("status", ErrorCodeEnum.FAIL.code());
            result.put("message","系统检测没有相关设备");
            return result;
        }else if (EmptyUtil.isEmpty(channelAuthUserVoList)) {
            result.put("status", ErrorCodeEnum.FAIL.code());
            result.put("message","系统检测没有相关授权人员");
            return result;
        }
        StringBuffer taskNoBuff=new StringBuffer();
        //处理锁与账号分批
        if (EmptyUtil.isNotEmpty(channelAuthLockVoList)&&EmptyUtil.isNotEmpty(channelAuthUserVoList)) {
            List<List<ChannelAuthLockVo>> batchLockVoList = Lists.partition(channelAuthLockVoList, AuthConstants.batchLockSize);
            List<List<ChannelAuthUserVo>> batchAuthUserVoList = Lists.partition(channelAuthUserVoList, AuthConstants.batchUserSize);
            StringBuffer descBuff=new StringBuffer();
            descBuff.append("[设备数量]: "+ channelAuthLockVoList.size());
            descBuff.append("[账号数量]: "+ channelAuthUserVoList.size());
            descBuff.append("设备总: "+batchLockVoList.size()+"批,");
            descBuff.append("账号总: "+batchAuthUserVoList.size()+"批,任务号：");

            for (int j = 0; j < batchLockVoList.size(); j++) {
                List<ChannelAuthLockVo> lockVos=batchLockVoList.get(j);
                for (int i = 0; i < batchAuthUserVoList.size(); i++) {
                    List<ChannelAuthUserVo> authUserVos=batchAuthUserVoList.get(i);
                    baseAuthorizeVo.setAuthDesc(authResource+"正执行[锁]第"+(j+1)+"批,[账号]第"+(i+1)+"批"+descBuff.toString());
                    ChannelAuthorizeEntity authorizeEntity=new ChannelAuthorizeEntity();
                    authorizeEntity.setBaseAuthorizeVo(baseAuthorizeVo);
                    authorizeEntity.setChannelAuthGroupVo(model.getChannelAuthGroupVo());
                    authorizeEntity.setChannelAuthUserVoList(authUserVos);
                    authorizeEntity.setChannelAuthLockVoList(lockVos);
                    ChannelAuthTaskVo channelAuthTaskVo =new ChannelAuthTaskVo();
                    channelAuthTaskVo.setTaskNo(authorizeCenterTaskService.generateTaskNo());
                    channelAuthTaskVo.setTaskType(authResource);
                    authorizeEntity.setChannelAuthTaskVo(channelAuthTaskVo);
                    authorizeService.addChannelAuthorizeTask(authorizeEntity);
                    taskNoBuff.append(channelAuthTaskVo.getTaskNo()).append(",");
                }
            }
        }
        result.put("taskNo",taskNoBuff.toString());
        result.put("status", ErrorCodeEnum.SUCCESS.code());
        result.put("message", "操作成功，根据任务号可查询授权结果");
        log.info(authResource+" 响应:{}",JSONObject.toJSONString(result));
        return result;
    }

    @ApiOperation(value = "授权接入到授权任务")
    @PostMapping(value = "/inTask")
    public JSONObject inTask(@RequestBody ChannelAuthorizeEntity model) {
        log.info("授权接入到授权任务 参数:{}",JSONObject.toJSONString(model));
        JSONObject result = new JSONObject();
        //TODO 用户中心获取当前登录人信息
        String currAccount ="";
        String orgId ="";

        BaseAuthorizeVo baseAuthorizeVo=model.getBaseAuthorizeVo();
        baseAuthorizeVo.setOrgId(orgId);
        baseAuthorizeVo.setAuthResource(AuthConstants.personAuthResource);
        baseAuthorizeVo.setCreateUserAccount(currAccount);
        ChannelAuthTaskVo channelAuthTaskVo =new ChannelAuthTaskVo();
        channelAuthTaskVo.setTaskNo(authorizeCenterTaskService.generateTaskNo());
        channelAuthTaskVo.setTaskType(AuthConstants.personAuthResource);

        model.setBaseAuthorizeVo(baseAuthorizeVo);
        model.setChannelAuthTaskVo(channelAuthTaskVo);
        authorizeService.addChannelAuthorizeTask(model);
        result.put("taskNo", channelAuthTaskVo.getTaskNo());
        result.put("status", ErrorCodeEnum.SUCCESS.code());
        result.put("message", "操作成功，根据任务号可查询授权结果");
        log.info(AuthConstants.personAuthResource+"接入到授权任务 响应:{}",JSONObject.toJSONString(result));
        return result;
    }


    @ApiOperation(value = "授权列表")
    @PostMapping(value = "/list")
    public PageInfo<AuthorizeCenterAuthorizeRecord> list(PageRequest pageRequest, @RequestBody AuthListParamQuery authorizeRecordQuery) {
        log.info(AuthConstants.personAuthResource+"列表 参数:{}",JSONObject.toJSONString(authorizeRecordQuery));
        //TODO 用户中心获取当前登录人信息
        String currAccount ="";
        authorizeRecordQuery.setUserAccount(currAccount);
        PageInfo<AuthorizeCenterAuthorizeRecord> recordPageInfo=authorizeService.authorizeList(pageRequest,authorizeRecordQuery);
        log.info(AuthConstants.personAuthResource+"列表 响应:{}",JSONObject.toJSONString(recordPageInfo));
        return recordPageInfo;
    }
    @ApiOperation(value = "根据id授权删除")
    @PostMapping(value = "/deleteById")
    public JSONObject deleteById(@RequestBody AuthParam model) {
        log.info("根据id授权删除 参数:{}",JSONObject.toJSONString(model));
        JSONObject result=authorizeService.delAuthorizeById(model.getIds());
        log.info("根据id授权删除 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "根据设备用户钥匙信息授权删除")
    @PostMapping(value = "/deleteByAuth")
    public JSONObject deleteByAuth(@RequestBody AuthParam model) {
        log.info("根据设备用户钥匙信息授权删除 参数:{}",JSONObject.toJSONString(model));
        JSONObject result=authorizeService.delAuthorize(model.getLockId(),model.getUserAccount(),model.getAuthChannelType(),model.getAuthChannelValue());
        log.info("根据设备用户钥匙信息授权删除 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "更新同步状态",tags="")
    @PostMapping(value = "updateSyncStatus",produces = "application/json;charset=utf-8")
    @ResponseBody
    public JSONObject updateSyncStatus(@RequestBody AuthParam model){
        log.info("更新同步状态 参数:{}",JSONObject.toJSONString(model));
        JSONObject result=authorizeService.updateSyncStatus(model.getId(),model.getSync(),model.getUploadStatus());
        log.info("更新同步状态 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "授权延期")
    @PostMapping(value = "/delayById")
    public JSONObject delayById(@RequestBody AuthParam model) {
        log.info("批量删除授权 参数:{}",JSONObject.toJSONString(model));
        JSONObject result=authorizeService.delayAuthTimeById(model.getIds(),model.getEndTime());
        log.info("批量删除授权 响应:{}",JSONObject.toJSONString(result));
        return result;
    }

    @ApiOperation(value = "根据设备用户钥匙信息授权延期")
    @PostMapping(value = "/delayByAuth")
    public JSONObject delayByAuth(@RequestBody AuthParam model) {
        log.info("根据设备用户钥匙信息授权延期 参数:{}",JSONObject.toJSONString(model));
        JSONObject result=authorizeService.delayAuthTime(model.getLockId(),model.getUserAccount(),model.getAuthChannelType(),model.getAuthChannelValue()
                ,model.getEndTime());
        log.info("根据设备用户钥匙信息授权延期 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "冻结授权")
    @PostMapping(value = "/freezeAuthById")
    public JSONObject freezeAuthById(@RequestBody AuthParam model) {
        log.info("冻结授权 参数:{}",JSONObject.toJSONString(model));
        JSONObject result=authorizeService.freezeAuthById(model.getIds(),model.getFreezeStatus());
        log.info("冻结授权 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "根据设备用户钥匙信息冻结授权")
    @PostMapping(value = "/freezeAuth")
    public JSONObject freezeAuth(@RequestBody AuthParam model) {
        log.info("根据设备用户钥匙信息冻结授权 参数:{}",JSONObject.toJSONString(model));
        JSONObject result=authorizeService.freezeAuth(model.getLockId(),model.getUserAccount(),model.getAuthChannelType(),model.getAuthChannelValue()
                ,model.getFreezeStatus());
        log.info("根据设备用户钥匙信息冻结授权 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "授权立即同步")
    @PostMapping(value = "/syncById")
    public JSONObject syncById(@RequestBody AuthParam model) {
        log.info("授权立即同步 参数:{}",JSONObject.toJSONString(model));
        JSONObject result=authorizeService.optSyncAuthById(model.getIds());
        log.info("授权立即同步 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "根据设备用户钥匙信息同步授权")
    @PostMapping(value = "/syncByAuth")
    public JSONObject syncByAuth(@RequestBody AuthParam model) {
        log.info("根据设备用户钥匙信息同步授权 参数:{}",JSONObject.toJSONString(model));
        JSONObject result=authorizeService.optSyncAuth(model.getLockId(),model.getUserAccount(),model.getAuthChannelType(),model.getAuthChannelValue());
        log.info("根据设备用户钥匙信息同步授权 响应:{}",JSONObject.toJSONString(result));
        return result;
    }



}
