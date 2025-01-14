package com.ymd.cloud.authorizeCenter.job.authSync;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.enums.ChannelTypeEnums;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeTemplateData;
import com.ymd.cloud.authorizeCenter.model.entity.AuthorizeDataVo;
import com.ymd.cloud.authorizeCenter.util.AuthorizeUtil;
import com.ymd.cloud.authorizeCenter.util.StringUtilBle;
import com.ymd.cloud.common.utils.EmptyUtil;

import java.text.DecimalFormat;
import java.util.List;

public class PwdSyncDataJson {
    public static final Integer PACKAGE_NUM=100;
    public static final Integer CMD_FOR_PUSH_PWD_INCREMENT=93;
    public static final Integer CMD_FOR_PUSH_PWD_ALL=88;
    private static final String PADDING_DATA="0000000000000000";
    private static final String PADDING_PREFIX="00";


    public static AuthorizeDataVo createAuthorizeDataVo(AuthorizeCenterAuthorizeRecord pwdAuth,String activedays, AuthorizeCenterAuthorizeTemplateData authorizeCenterAuthorizeTemplateData) {
        //0.管理员1.普通密码 2.随身码/期限码(强安全对应期限码)
        String pwdTypeCode= ChannelTypeEnums.CHANNELTYPE_101.equals(pwdAuth.getAuthChannelType())?"0":ChannelTypeEnums.CHANNELTYPE_102.equals(pwdAuth.getAuthChannelType())?"1": "2";
        String pwdType= convertConfig(activedays,pwdTypeCode);
        int pt=Integer.parseInt(pwdType,16);
        AuthorizeDataVo vo = new AuthorizeDataVo();
        vo.setNfcId(pwdAuth.getUid());
        vo.setPwd(AuthorizeUtil.strTo16(pwdAuth.getAuthChannelValue()));
        vo.setPwdType(pt);
        vo.setPwdType16(pwdType);
        vo.setId(pwdAuth.getId());
        vo=AuthDataCompare.convertAuthorizeDataVoCommon(pwdAuth,vo);
        return vo;
    }

    public static JSONObject createHeaderJson(String lockMac, int packageIndex, int hasChange, int p1_size, int b1_crc, int p2_size, int b2_crc, int count){
        JSONObject pwdData = new JSONObject();
        pwdData.put("mac",lockMac);
        pwdData.put("package",packageIndex);
        pwdData.put("authVersion",hasChange);
        pwdData.put("authCount",p1_size);
        pwdData.put("authCrc",b1_crc);
        pwdData.put("listCount",p2_size);
        pwdData.put("listCrc",b2_crc);
        pwdData.put("count",count);
        return pwdData;
    }
    public static JSONArray createDataJsonList(List<AuthorizeDataVo> authList, Integer cmd){
        JSONArray array = new JSONArray();
        if(EmptyUtil.isNotEmpty(authList)&&authList.size()!=0) {
            for (int i = 0; i < authList.size(); i++) {
                AuthorizeDataVo vo = authList.get(i);
                array.add(createPwdDataJson(vo, cmd));
            }
        }
        return array;
    }
    private static JSONObject createPwdDataJson(AuthorizeDataVo vo,Integer cmd){
        JSONObject temp=new JSONObject();
        temp.put("authId",vo.getId());
        if(CMD_FOR_PUSH_PWD_INCREMENT.equals(cmd)) {
            temp.put("code", vo.getCode());
        }
        temp.put("pwdType",vo.getPwdType());
        temp.put("nfcId",vo.getNfcId());
        temp.put("pwd",vo.getPwd());
        temp.put("start",vo.getStart());
        temp.put("end",vo.getEnd());
        return temp;
    }
    public static StringBuilder createCrcBuff(AuthorizeDataVo vo){
        StringBuilder pwdAuthCrcBuff = new StringBuilder();
        pwdAuthCrcBuff.append(vo.getNfcId());
        pwdAuthCrcBuff.append(vo.getPwd());
        pwdAuthCrcBuff.append(PADDING_PREFIX);
        pwdAuthCrcBuff.append(vo.getPwdType16());
        pwdAuthCrcBuff.append(vo.getStartTime());
        pwdAuthCrcBuff.append(vo.getEndTime());
        pwdAuthCrcBuff.append(PADDING_DATA);
        return pwdAuthCrcBuff;
    }
    //配置字格式转换
    public static String convertConfig(String activeDate,String pwdType){
        DecimalFormat df=new DecimalFormat("00000000");
        String activeStr=Integer.toBinaryString(Integer.parseInt(activeDate));
        activeStr=df.format(Integer.parseInt(activeStr));
        String pwdTypeStr=Integer.toBinaryString(Integer.parseInt(pwdType));
        pwdTypeStr=df.format(Integer.parseInt(pwdTypeStr));
        String res=activeStr.substring(3)+pwdTypeStr.substring(5);
        byte bt= StringUtilBle.BitToByte(res);
        res=StringUtilBle.Bytes2HexString(new byte[]{bt});
        return res;
    }




















}
