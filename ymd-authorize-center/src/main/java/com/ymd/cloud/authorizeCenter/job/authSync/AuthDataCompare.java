package com.ymd.cloud.authorizeCenter.job.authSync;

import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeRecord;
import com.ymd.cloud.authorizeCenter.model.entity.AuthorizeDataVo;
import com.ymd.cloud.authorizeCenter.util.AuthorizeUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.common.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;

@Slf4j
@Component
public class AuthDataCompare {
    /**
     * 如果某个授权有多条则，获取与当前时间有交叉的最新交集授权数据
     * @param list
     * @return
     */
    public List<AuthorizeCenterAuthorizeRecord> compareAuthList(List<AuthorizeCenterAuthorizeRecord> list) {
        List<AuthorizeCenterAuthorizeRecord> resultList=new ArrayList<>();
        if(EmptyUtil.isNotEmpty(list)) {
            Map<String,List<AuthorizeCenterAuthorizeRecord>> addAuthGroupMap=new HashMap();
            List<AuthorizeCenterAuthorizeRecord> addList=new ArrayList<>();
            List<AuthorizeCenterAuthorizeRecord> addAuthGroupList;

            Map<String,List<AuthorizeCenterAuthorizeRecord>> delAuthGroupMap=new HashMap();
            List<AuthorizeCenterAuthorizeRecord> delList=new ArrayList<>();
            List<AuthorizeCenterAuthorizeRecord> delAuthGroupList;

            for (AuthorizeCenterAuthorizeRecord authorizeCenterAuthorizeRecord : list) {
                Integer state = authorizeCenterAuthorizeRecord.getStatus();
                Integer flag = authorizeCenterAuthorizeRecord.getFlag();
                Integer freezeStatus = authorizeCenterAuthorizeRecord.getFreezeStatus();
                if(state==0||flag==0||freezeStatus==0){//code=1 表示删除  此状态下一般是指增量 未同步，下数据
                    //删除数据放入集合中
                    delList.add(authorizeCenterAuthorizeRecord);
                }else{
                    //新增数据放入集合中
                    addList.add(authorizeCenterAuthorizeRecord);
                }
            }
            //处理新增授权数据 以lockId+user_account+auth_channel_type+auth_channel_value 是否相同或数据放到map<list>中
            if(EmptyUtil.isNotEmpty(addList)&&addList.size()!=0) {
                for (AuthorizeCenterAuthorizeRecord addModel : addList) {
                    String key = addModel.getLockId()+addModel.getUserAccount()+addModel.getAuthChannelType()+ UUIDUtil.getUid(addModel.getAuthChannelValue())+addModel.getAuthEqualUuid();
                    if (addAuthGroupMap.containsKey(key)) {
                        addAuthGroupList = addAuthGroupMap.get(key);
                    } else {
                        addAuthGroupList = new ArrayList<>();
                    }
                    addAuthGroupList.add(addModel);//同锁同卡集合
                    addAuthGroupMap.put(key, addAuthGroupList);//判断同锁同卡 keymap集合
                }
            }
            if(EmptyUtil.isNotEmpty(delList)&&delList.size()!=0) {
                //处理删除授权数据 以lockId+dnCode+nfcId 是否相同或数据放到map<list>中
                for (AuthorizeCenterAuthorizeRecord delModel : delList) {
                    String key = delModel.getLockId()+delModel.getUserAccount()+delModel.getAuthChannelType()+UUIDUtil.getUid(delModel.getAuthChannelValue())+delModel.getAuthEqualUuid();
                    if (addAuthGroupMap.containsKey(key)) {
                        addAuthGroupList = addAuthGroupMap.get(key);
                        //判断放入删除集合前 是否有效数据大于1 如果大于一 则不下发
                        if (addAuthGroupList != null && addAuthGroupList.size() > 1) {
                            continue;
                        } else {
                            //对于一新增数据和一删除数据 取删除数据
                            if (addAuthGroupList != null && addAuthGroupList.size() == 1) {
                                addAuthGroupMap.remove(key);
                            }
                        }
                    }
                    //处理删除授权数据 以lockId+dnCode+nfcId 是否相同或数据放到map<list>中
                    if (delAuthGroupMap.containsKey(key)) {
                        delAuthGroupList = delAuthGroupMap.get(key);
                    } else {
                        delAuthGroupList = new ArrayList<>();
                    }
                    delAuthGroupList.add(delModel);//同锁同卡集合
                    delAuthGroupMap.put(key, delAuthGroupList);//判断同锁同卡 keymap集合

                }
            }
            addAuthGroupMap.putAll(delAuthGroupMap);
            //处理同锁同卡集合取有效期最长的一部分
            for (Map.Entry<String, List<AuthorizeCenterAuthorizeRecord>> m : addAuthGroupMap.entrySet()) {
                List<AuthorizeCenterAuthorizeRecord> groupList=  m.getValue();
                resultList.add(compareGroupList(groupList));
            }
        }
        return resultList;
    }

    /**
     * 处理同锁同卡集合取有效期最长的一部分
     * @param groupList
     * @return
     */
    private AuthorizeCenterAuthorizeRecord compareGroupList(List<AuthorizeCenterAuthorizeRecord> groupList) {
        AuthorizeCenterAuthorizeRecord resultMap=null;
        if(EmptyUtil.isNotEmpty(groupList)&&groupList.size()>0) {
            long compareStart=0;
            long compareEnd=0;
            for (AuthorizeCenterAuthorizeRecord model : groupList) {
                long start=model.getStartTime().getTime();
                long end=model.getEndTime().getTime();

                if(compareStart==0&&compareEnd==0) {
                    compareStart = start;
                    compareEnd = end;
                }else {
                    if (start < compareStart) {
                        compareStart = start;
                    } else if (end > compareEnd) {
                        compareEnd = end;
                    }
                }
            }
            resultMap=groupList.get(0);
            resultMap.setStartTime(new Date(compareStart));
            resultMap.setEndTime(new Date(compareEnd));
        }
        return resultMap;
    }
    public static AuthorizeDataVo convertAuthorizeDataVoCommon(AuthorizeCenterAuthorizeRecord record, AuthorizeDataVo vo){
        long startTimel = record.getStartTime().getTime();
        long endTimel = record.getEndTime().getTime();
        //为了避免服务器不同步 开始时间-10分钟
        int diff=10*60;//10分钟
        if(String.valueOf(startTimel).length()==13){
            diff=diff*1000;
        }
        startTimel=startTimel-diff;

        String startTime = AuthorizeUtil.bytesToHexString(AuthorizeUtil.intToByteArray_LITTLE_ENDIAN(startTimel));
        String endTime = AuthorizeUtil.bytesToHexString(AuthorizeUtil.intToByteArray_LITTLE_ENDIAN(endTimel));

        Integer state = record.getStatus();
        Integer flag = record.getFlag();
        Integer freezeStatus = record.getFreezeStatus();
        int code;
        if(state==0||flag==0||freezeStatus==0){//code=1 表示删除  此状态下一般是指增量 未同步，下数据
            code=1;//删除状态
        }else{//全量数据 基本都是正常 有效 非冻结数据
            code=2;//新增状态
        }
        vo.setCode(code);

        vo.setStart(startTimel);
        vo.setEnd(endTimel);
        vo.setStartTime(startTime);
        vo.setEndTime(endTime);
        return vo;
    }
}
