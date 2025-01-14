package com.ymd.cloud.authorizeCenter;

import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.enums.AuthConstants;
import com.ymd.cloud.authorizeCenter.model.entity.*;
import com.ymd.cloud.authorizeCenter.util.AuthorizeUtil;
import com.ymd.cloud.common.utils.DateUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

class YmdAuthorizeCenterApplicationTests {

    public static void main(String[] args) {
        System.out.println("hello,world");

        System.out.println(DateUtil.format(new Date()));
        System.out.println(DateUtil.format(new Date(DateUtil.delayTime(AuthConstants.authTimeDiff))));


        long startTimel = new Date().getTime();
        long endTimel = new Date().getTime();
        //为了避免服务器不同步 开始时间-10分钟
        int diff=10*60;//10分钟
        if(String.valueOf(startTimel).length()==13){
            diff=diff*1000;
        }
        startTimel=startTimel-diff;

        String startTime = AuthorizeUtil.bytesToHexString(AuthorizeUtil.intToByteArray_LITTLE_ENDIAN(startTimel));
        String endTime = AuthorizeUtil.bytesToHexString(AuthorizeUtil.intToByteArray_LITTLE_ENDIAN(endTimel));
        System.out.println(startTime);
        System.out.println(endTime);

        String startStr= convertDateToHex(DateUtil.formatIntDate(startTimel));
        System.out.println(startStr);
    }
    private static String convertDateToHex(Date date){
        long l=date.getTime()/1000;
        return AuthorizeUtil.bytesToHexString(AuthorizeUtil.intToByteArray_LITTLE_ENDIAN(l));
    }
}
