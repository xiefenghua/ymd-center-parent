package com.ymd.cloud.authorizeCenter;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.enums.ChannelTypeEnums;
import com.ymd.cloud.authorizeCenter.model.entity.*;
import com.ymd.cloud.common.utils.DateUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AddUserAuthModelTest {
    public static void main(String[] args) {
        BaseAuthorizeVo baseAuthorizeVo=new BaseAuthorizeVo();
        baseAuthorizeVo.setOrgId("000000001");//必填
        baseAuthorizeVo.setStartTime(new Date());//必填
        baseAuthorizeVo.setEndTime(DateUtil.getAfterDaysToday(baseAuthorizeVo.getStartTime(),20));//必填
        baseAuthorizeVo.setCreateUserAccount("13161337653");//必填

        ChannelAuthGroupVo channelAuthGroupVo =new ChannelAuthGroupVo();
//        channelAuthGroupVo.setUserGroupName();
//        channelAuthGroupVo.setUserGroupId();
//        channelAuthGroupVo.setDeviceGroupName();
//        channelAuthGroupVo.setDeviceGroupId();
        ChannelAuthLockVo channelAuthLockVo =new ChannelAuthLockVo();
        channelAuthLockVo.setLockId("005448489A47497AA59941EC6D82");//必填
        ChannelAuthUserVo channelAuthUserVo =new ChannelAuthUserVo();
        channelAuthUserVo.setUserAccount("13161337653");//必填

        ChannelAuthChannelInfoVo channelAuthChannelInfoVo =new ChannelAuthChannelInfoVo();
        channelAuthChannelInfoVo.setAuthChannelModuleType(ChannelTypeEnums.MODULETYPE_01);//必填
        channelAuthChannelInfoVo.setAuthChannelType(ChannelTypeEnums.CHANNELTYPE_101);//必填
        channelAuthChannelInfoVo.setAuthChannelValue("135425");//必填

        List<ChannelAuthChannelInfoVo> channelAuthChannelInfoVoList =new ArrayList<>();
        channelAuthChannelInfoVoList.add(channelAuthChannelInfoVo);
        channelAuthUserVo.setChannelAuthChannelInfoVoList(channelAuthChannelInfoVoList);
        ChannelAuthorizeEntity model=new ChannelAuthorizeEntity();
        List<ChannelAuthLockVo> channelAuthLockVoList = new ArrayList<>();
        channelAuthLockVoList.add(channelAuthLockVo);
        List<ChannelAuthUserVo> channelAuthUserVoList = new ArrayList<>();
        channelAuthUserVoList.add(channelAuthUserVo);
        model.setBaseAuthorizeVo(baseAuthorizeVo);
        model.setChannelAuthGroupVo(channelAuthGroupVo);
        model.setChannelAuthLockVoList(channelAuthLockVoList);
        model.setChannelAuthUserVoList(channelAuthUserVoList);
        System.out.println(JSONObject.toJSONString(model));

        MqttConsumerEntity param=new MqttConsumerEntity();
        param.setMsgBody(JSONObject.toJSONString(channelAuthLockVo));
        System.out.println(JSONObject.toJSONString(param));

    }
}
