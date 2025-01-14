package com.ymd.cloud.authorizeCenter.job.authSync;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.enums.ChannelTypeEnums;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeTemplateData;
import com.ymd.cloud.authorizeCenter.model.entity.AuthorizeDataVo;
import com.ymd.cloud.common.utils.EmptyUtil;

import java.util.List;

public class FaceSyncDataJson {
    //每包发送数据
    public final static Integer PACKAGE_NUM=100;
    public static final Integer CMD_FOR_PUSH_FACE_AUTHORIZE=0;
    public static AuthorizeDataVo createAuthorizeDataVo(AuthorizeCenterAuthorizeRecord faceAuth, AuthorizeCenterAuthorizeTemplateData authorizeCenterAuthorizeTemplateData) {
        AuthorizeDataVo vo = new AuthorizeDataVo();
        vo.setFaceFeature(authorizeCenterAuthorizeTemplateData.getFeature());
        vo.setFaceImgUrl(faceAuth.getAuthChannelValue());
        vo=AuthDataCompare.convertAuthorizeDataVoCommon(faceAuth,vo);
        return vo;
    }
    public static JSONObject createHeaderJson(String lockMac, int packageIndex, int hasChange, int p1_size, int b1_crc, int p2_size, int b2_crc, int count){
        JSONObject faceData = new JSONObject();
        faceData.put("mac",lockMac);
        faceData.put("package",packageIndex);
        faceData.put("authVersion",hasChange);
        faceData.put("authCount",p1_size);
        faceData.put("authCrc",b1_crc);
        faceData.put("listCount",p2_size);
        faceData.put("listCrc",b2_crc);
        faceData.put("count",count);
        return faceData;
    }
    public static JSONArray createDataJsonList(List<AuthorizeDataVo> authList, Integer cmd){
        JSONArray array = new JSONArray();
        if(EmptyUtil.isNotEmpty(authList)&&authList.size()!=0) {
            for (int i = 0; i < authList.size(); i++) {
                AuthorizeDataVo vo = authList.get(i);
                array.add(createFaceDataJson(vo, cmd));
            }
        }
        return array;
    }
    private static JSONObject createFaceDataJson(AuthorizeDataVo vo,Integer cmd){
        JSONObject temp=new JSONObject();
        temp.put("authId",vo.getId());
        temp.put("code", vo.getCode());
        temp.put("start",vo.getStart());
        temp.put("end",vo.getEnd());
        return temp;
    }
    public static StringBuilder createCrcBuff(AuthorizeDataVo vo){
        StringBuilder faceAuthCrcBuff = new StringBuilder();
        faceAuthCrcBuff.append(vo.getStartTime());
        faceAuthCrcBuff.append(vo.getEndTime());
        return faceAuthCrcBuff;
    }
}
