package com.ymd.cloud.authorizeCenter.web;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.model.entity.ChannelAuthLockVo;
import com.ymd.cloud.authorizeCenter.model.entity.MqttConsumerEntity;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterGroupUserDeviceAuthorizeService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeSyncService;
import com.ymd.cloud.authorizeCenter.service.ConsumeChannelAuthTaskNotesService;
import com.ymd.cloud.authorizeCenter.thirdServer.process.*;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
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
@RequestMapping("ymd/web/center/authorizeCenter/consumer")
@Api(value = "消息消费中心", tags = "消息消费中心接口")
public class ConsumerController {
    @Autowired
    AuthorizeService authorizeService;
    @Autowired
    AuthorizeSyncService authorizeSyncService;
    @Autowired
    ConsumeChannelAuthTaskNotesService consumeChannelAuthTaskNotesService;
    @Autowired
    AuthorizeCenterGroupUserDeviceAuthorizeService authorizeCenterGroupUserDeviceAuthorizeService;
    @Autowired
    ThirdOpenDoorReceiveProcess thirdOpenDoorReceiveProcess;
    @Autowired
    ThirdHeartReceiveProcess thirdHeartReceiveProcess;
    @Autowired
    ThirdDeviceReceiveProcess thirdDeviceReceiveProcess;
    @Autowired
    ThirdConfigPushProcess thirdConfigPushProcess;
    @Autowired
    ThirdAuthReturnReceiveProcess thirdAuthReturnReceiveProcess;
    @Autowired
    ThirdAuthPushProcess thirdAuthPushProcess;
    @Autowired
    SyncThirdAllAuthMqProcess syncThirdAllAuthMqProcess;

    //===========================rabbitmq消费接口=============================================
    @ApiOperation(value = "事件--消费接口")
    @PostMapping(value = "/rabbitmq/eventCenterMqConsumer")
    public JSONObject eventCenterMqConsumer(@RequestBody MqttConsumerEntity param) {
        log.info("事件--消费接口 参数:{}",JSONObject.toJSONString(param));
        JSONObject result = new JSONObject();
        result.put("status", ErrorCodeEnum.SUCCESS.code());
        result.put("message", ErrorCodeEnum.SUCCESS.msg());

        log.info("事件--消费接口 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "组授权授权生成消费接口")
    @PostMapping(value = "/rabbitmq/groupAuthorizeGenerateConsumer")
    public JSONObject groupAuthorizeGenerateConsumer(@RequestBody MqttConsumerEntity param) {
        log.info("组授权授权生成消费接口 参数:{}",JSONObject.toJSONString(param));
        JSONObject result = authorizeCenterGroupUserDeviceAuthorizeService.groupAuthorizeGenerateConsumerHandler(
                param.getTaskNo(),param.getTopic(),param.getJobType(),param.getMsgBody()
        );
        log.info("组授权授权生成消费接口 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "授权同步任务消费接口")
    @PostMapping(value = "/rabbitmq/userAuthorizeSyncConsumer")
    public JSONObject userAuthorizeSyncConsumer(@RequestBody MqttConsumerEntity param) {
        log.info("授权同步任务消费接口 参数:{}",JSONObject.toJSONString(param));
        JSONObject result = new JSONObject();
        result.put("status", ErrorCodeEnum.SUCCESS.code());
        result.put("message", ErrorCodeEnum.SUCCESS.msg());

        ChannelAuthLockVo channelAuthLockVo =JSONObject.parseObject(param.getMsgBody(), ChannelAuthLockVo.class);
        authorizeSyncService.userAuthorizeSyncConsumerHandler(channelAuthLockVo);
        log.info("授权同步任务消费接口 响应:{}",JSONObject.toJSONString(result));
        return result;
    }

    //===========================mqtt消费接口=============================================
    @ApiOperation(value = "授权任务mqtt开始执行--消费接口")
    @PostMapping(value = "/mqtt/authThreadToMqConsumer")
    public JSONObject authThreadToMqConsumer(@RequestBody MqttConsumerEntity param) {
        log.info("授权任务mqtt开始执行--消费接口 参数:{}",JSONObject.toJSONString(param));
        JSONObject result = authorizeService.authThreadToMqConsumerHandler(param.getTaskNo());
        log.info("授权任务mqtt开始执行--消费接口 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "授权任务notes授权处理--消费接口")
    @PostMapping(value = "/mqtt/channelAuthConsumer")
    public JSONObject channelAuthConsumer(@RequestBody MqttConsumerEntity param) {
        log.info("授权任务notes授权处理--消费接口 参数:{}",JSONObject.toJSONString(param));
        JSONObject result =  consumeChannelAuthTaskNotesService.channelAuthConsumerHandler(param.getTaskNo());
        log.info("授权任务notes授权处理--消费接口 响应:{}",JSONObject.toJSONString(result));
        return result;
    }
    @ApiOperation(value = "第三方设备授权自动同步--消费接口")
    @PostMapping(value = "/mqtt/thirdSyncThirdAllAuthMqConsumer")
    public JSONObject thirdSyncThirdAllAuthMqConsumer(@RequestBody MqttConsumerEntity param) {
        log.info("第三方设备授权自动同步--消费接口 参数:{}",JSONObject.toJSONString(param));
        JSONObject result =null;
        syncThirdAllAuthMqProcess.process(param.getTopic(),param.getMsgBody());
        log.info("第三方设备授权自动同步--消费接口 响应:{}",JSONObject.toJSONString(result));
        return result;
    }

    @ApiOperation(value = "第三方设备设备注册--消费接口")
    @PostMapping(value = "/mqtt/thirdRegisterDeviceConsumer")
    public JSONObject thirdRegisterDeviceConsumer(@RequestBody MqttConsumerEntity param) {
        log.info("第三方设备设备注册--消费接口 参数:{}",JSONObject.toJSONString(param));
        JSONObject result =null;
        thirdDeviceReceiveProcess.process(param.getTopic(),param.getMsgBody());
        log.info("第三方设备设备注册--消费接口 响应:{}",JSONObject.toJSONString(result));
        return result;
    }

    @ApiOperation(value = "第三方设备授权同步--消费接口")
    @PostMapping(value = "/mqtt/thirdSyncAuthConsumer")
    public JSONObject thirdSyncAuthConsumer(@RequestBody MqttConsumerEntity param) {
        log.info("第三方设备授权同步--消费接口 参数:{}",JSONObject.toJSONString(param));
        JSONObject result =null;
        thirdAuthPushProcess.process(param.getTopic(),param.getMsgBody());
        log.info("第三方设备授权同步--消费接口 响应:{}",JSONObject.toJSONString(result));
        return result;
    }

    @ApiOperation(value = "第三方设备配置同步--消费接口")
    @PostMapping(value = "/mqtt/thirdSyncConfigConsumer")
    public JSONObject thirdSyncConfigConsumer(@RequestBody MqttConsumerEntity param) {
        log.info("第三方设备配置同步--消费接口 参数:{}",JSONObject.toJSONString(param));
        JSONObject result =null;
        thirdConfigPushProcess.process(param.getTopic(),param.getMsgBody());
        log.info("第三方设备配置同步--消费接口 响应:{}",JSONObject.toJSONString(result));
        return result;
    }

    @ApiOperation(value = "第三方设备心跳上报--消费接口")
    @PostMapping(value = "/mqtt/thirdSyncHeartConsumer")
    public JSONObject thirdSyncHeartConsumer(@RequestBody MqttConsumerEntity param) {
        log.info("第三方设备心跳上报--消费接口 参数:{}",JSONObject.toJSONString(param));
        JSONObject result =null;
        thirdHeartReceiveProcess.process(param.getTopic(),param.getMsgBody());
        log.info("第三方设备心跳上报--消费接口 响应:{}",JSONObject.toJSONString(result));
        return result;
    }

    @ApiOperation(value = "第三方设备开门上报--消费接口")
    @PostMapping(value = "/mqtt/thirdSyncOpenDoorRecordConsumer")
    public JSONObject thirdSyncOpenDoorRecordConsumer(@RequestBody MqttConsumerEntity param) {
        log.info("第三方设备开门上报--消费接口 参数:{}",JSONObject.toJSONString(param));
        JSONObject result =null;
        thirdOpenDoorReceiveProcess.process(param.getTopic(),param.getMsgBody());
        log.info("第三方设备开门上报--消费接口 响应:{}",JSONObject.toJSONString(result));
        return result;
    }

    @ApiOperation(value = "第三方设备授权同步上报--消费接口")
    @PostMapping(value = "/mqtt/thirdSyncAuthReturnConsumer")
    public JSONObject thirdSyncAuthReturnConsumer(@RequestBody MqttConsumerEntity param) {
        log.info("第三方设备授权同步上报--消费接口 参数:{}",JSONObject.toJSONString(param));
        JSONObject result =null;
        thirdAuthReturnReceiveProcess.process(param.getTopic(),param.getMsgBody());
        log.info("第三方设备授权同步上报--消费接口 响应:{}",JSONObject.toJSONString(result));
        return result;
    }









}
