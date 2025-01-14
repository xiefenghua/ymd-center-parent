package com.ymd.cloud.authorizeCenter.job.authSync;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.enums.ChannelTypeEnums;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeTemplateData;
import com.ymd.cloud.authorizeCenter.model.entity.AuthorizeDataVo;
import com.ymd.cloud.common.utils.EmptyUtil;
import java.util.List;

public class CardSyncDataJson {
    //每包发送数据
    public final static Integer PACKAGE_NUM=100;
    //卡片授权增量同步
    public final static Integer CMD_FOR_PUSH_AUTHORIZE_INCREMENT=92;
    //卡片授权全量同步
    public final static Integer CMD_FOR_PUSH_AUTHORIZE_ALL=83;

    public static AuthorizeDataVo createAuthorizeDataVo(AuthorizeCenterAuthorizeRecord cardAuth, AuthorizeCenterAuthorizeTemplateData authorizeCenterAuthorizeTemplateData) {
        AuthorizeDataVo vo = new AuthorizeDataVo();
        int cardType= ChannelTypeEnums.CHANNELTYPE_201.equals(cardAuth.getAuthChannelType())?1:2;
        vo.setCardType(cardType);
        vo.setDnCode(authorizeCenterAuthorizeTemplateData.getCardDnCode());
        vo.setNfcId(cardAuth.getAuthChannelValue());
        vo.setId(cardAuth.getId());
        vo=AuthDataCompare.convertAuthorizeDataVoCommon(cardAuth,vo);
        return vo;
    }

    public static JSONObject createHeaderJson(String lockMac, int packageIndex, int hasChange, int p1_size, int b1_crc, int p2_size, int b2_crc, int count){
        JSONObject cardData = new JSONObject();
        cardData.put("mac",lockMac);
        cardData.put("package",packageIndex);
        cardData.put("authVersion",hasChange);
        cardData.put("authCount",p1_size);
        cardData.put("authCrc",b1_crc);
        cardData.put("listCount",p2_size);
        cardData.put("listCrc",b2_crc);
        cardData.put("count",count);
        return cardData;
    }
    public static JSONArray createDataJsonList(List<AuthorizeDataVo> authList, Integer cmd){
        JSONArray array = new JSONArray();
        if(EmptyUtil.isNotEmpty(authList)&&authList.size()!=0) {
            for (int i = 0; i < authList.size(); i++) {
                AuthorizeDataVo vo = authList.get(i);
                array.add(createCardDataJson(vo, cmd));
            }
        }
        return array;
    }
    private static JSONObject createCardDataJson(AuthorizeDataVo vo,Integer cmd){
        JSONObject temp=new JSONObject();
        temp.put("authId",vo.getId());
        if(CMD_FOR_PUSH_AUTHORIZE_INCREMENT.equals(cmd)) {
            temp.put("code", vo.getCode());
        }
        temp.put("cardType",vo.getCardType());
        temp.put("nfcId",vo.getNfcId());
        temp.put("dnCode",vo.getDnCode());
        temp.put("start",vo.getStart());
        temp.put("end",vo.getEnd());
        return temp;
    }
    public static StringBuilder createCrcBuff(AuthorizeDataVo vo){
        StringBuilder cardAuthCrcBuff = new StringBuilder();
        cardAuthCrcBuff.append(vo.getNfcId());
        cardAuthCrcBuff.append(vo.getDnCode());
        cardAuthCrcBuff.append(vo.getStartTime());
        cardAuthCrcBuff.append(vo.getEndTime());
        return cardAuthCrcBuff;
    }

}
