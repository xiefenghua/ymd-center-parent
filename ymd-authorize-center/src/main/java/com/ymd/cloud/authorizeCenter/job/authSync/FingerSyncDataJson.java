package com.ymd.cloud.authorizeCenter.job.authSync;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeTemplateData;
import com.ymd.cloud.authorizeCenter.model.entity.AuthorizeDataVo;
import com.ymd.cloud.authorizeCenter.util.AuthorizeUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import java.util.List;

public class FingerSyncDataJson {
    //预留字
    private static final String PRE_STR="0";
    //自定义数据区
    private static final String CUSTOMIZE_DATA_AREA="0000000000000000";
    //预留
    private static final String PRE_DATA_AREA="0000000000";
    public static final Integer PACKAGE_NUM=10;
    //指纹授权列表推送
    public static final Integer CMD_FOR_PUSH_FINGER_AUTHORIZE=95;

    public static AuthorizeDataVo createAuthorizeDataVo(AuthorizeCenterAuthorizeRecord fingerAuth, AuthorizeCenterAuthorizeTemplateData authorizeCenterAuthorizeTemplateData) {
        AuthorizeDataVo vo = new AuthorizeDataVo();
        vo.setNfcId(fingerAuth.getUid());
        int fingerNum=authorizeCenterAuthorizeTemplateData.getFingerId();
        vo.setFingerId(fingerNum);
        vo.setFingerType(authorizeCenterAuthorizeTemplateData.getFingerType());
        vo=AuthDataCompare.convertAuthorizeDataVoCommon(fingerAuth,vo);
        return vo;
    }
    public static JSONObject createHeaderJson(String lockMac, int packageIndex, int hasChange, int p1_size, int b1_crc, int p2_size, int b2_crc, int count){
        JSONObject data =new JSONObject();
        data.put("count",count);
        data.put("mac",lockMac);
        data.put("package",packageIndex);
        data.put("authCount",p1_size);
        data.put("authCrc",b1_crc);
        data.put("authVersion",hasChange);
        return data;
    }
    public static JSONArray createDataJsonList(List<AuthorizeDataVo> authList, Integer cmd){
        JSONArray array = new JSONArray();
        if(EmptyUtil.isNotEmpty(authList)&&authList.size()!=0) {
            for (int i = 0; i < authList.size(); i++) {
                AuthorizeDataVo vo = authList.get(i);
                array.add(createFingerDataJson(vo, cmd));
            }
        }
        return array;
    }
    private static JSONObject createFingerDataJson(AuthorizeDataVo vo,Integer cmd){
        JSONObject temp=new JSONObject();
        temp.put("nfcId",vo.getNfcId());
        temp.put("fingerId",vo.getFingerId());
        temp.put("fingerType",vo.getFingerType());
        temp.put("start",vo.getStart());
        temp.put("end",vo.getEnd());
        return temp;
    }
    public static StringBuilder createCrcBuff(AuthorizeDataVo vo){
        StringBuilder authCrcBuff = new StringBuilder();
        String fingerNumStr=convertIdToHex(vo.getFingerId());
        //配置字
        String fingerStr=convertConfig(PRE_STR,String.valueOf(vo.getFingerType()));
        //uid(8)+指纹编号(2)+预留(5)+指纹配置字(1)+开始时间(4)+结束时间(4)+自定义数据区(8)
        authCrcBuff.append(vo.getNfcId()).append(fingerNumStr).append(PRE_DATA_AREA)
                .append(fingerStr).append(vo.getStartTime()).append(vo.getEndTime()).append(CUSTOMIZE_DATA_AREA);
        return authCrcBuff;
    }
    //配置字格式转换
    public  static  String convertConfig(String pre,String fingerType){
        return PwdSyncDataJson.convertConfig(pre,fingerType);
    }
    private static String convertIdToHex(int  fingerNum){
        return AuthorizeUtil.bytesToHexString(AuthorizeUtil.intToByteArray_LITTLE_ENDIAN2(fingerNum));
    }

}
