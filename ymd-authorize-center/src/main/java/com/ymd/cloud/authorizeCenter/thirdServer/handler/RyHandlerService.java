package com.ymd.cloud.authorizeCenter.thirdServer.handler;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.authorizeCenter.thirdServer.job.pushAuth.RyPushMsgUtil;
import com.ymd.cloud.authorizeCenter.thirdServer.service.ThirdServerService;
import com.ymd.cloud.authorizeCenter.util.CardNo;
import com.ymd.cloud.common.enumsSupport.Constants;
import com.ymd.cloud.common.utils.DateUtil;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.common.utils.IdGen;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class RyHandlerService implements HandlerService{
    @Autowired
    RyPushMsgUtil ryPushMsgUtil;
    @Autowired
    ThirdServerService thirdServerService;
    public void savePersonsAndFaceImg(JSONObject body,String serialNo,Long id){
        JSONArray userList=body.getJSONArray("data");
        id=thirdServerService.getId(id);
        if(EmptyUtil.isNotEmpty(userList)&&userList.size()!=0) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id",id);
            notifyJson.put("method", "personnelData.savePersons");
            notifyJson.put("userAuthFeatureId",id);
            String userAccount=null;
            JSONArray personList = new JSONArray();
            for (int i = 0; i < userList.size(); i++) {
                JSONObject user = userList.getJSONObject(i);
                userAccount=user.getString("userAccount");
                String realName=EmptyUtil.isNotEmpty(user.getString("realName"))?user.getString("realName"):userAccount;
                String sex=EmptyUtil.isNotEmpty(user.getString("sex"))&&user.getString("sex") .equals("0")?"female":"male";
                String birthday=EmptyUtil.isEmpty(user.getString("birthday"))?"1970-01-01"
                        :user.getString("birthday").replace("年","-") .replace("月","-")
                        .replace("日","");

                JSONObject person = new JSONObject();
                    JSONObject model = new JSONObject();
                    model.put("Type", 1);
                    model.put("Code", userAccount);
                    model.put("CredentialNo", user.getString("idCardNo"));
                    model.put("Name",realName);
                    model.put("Sex",sex);
                    model.put("Birthday", birthday);
                    List<String> faceImg = new ArrayList<>();
                    faceImg.add(user.getString("faceImgUrl"));
                    model.put("URL", faceImg);
                person.put("Person",model);
                personList.add(person);
            }
            notifyJson.put("params", personList);
            thirdServerService.saveThird(notifyJson,serialNo,userAccount);
            ryPushMsgUtil.push(serialNo,notifyJson);
        }
    }
    public void delPersonsAndFaceImg(JSONObject body,String serialNo,Long id){
        JSONArray userList=body.getJSONArray("data");
        if(EmptyUtil.isNotEmpty(userList)&&userList.size()!=0) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id", id);
            notifyJson.put("method", "personnelData.removePersons");
            notifyJson.put("userAuthFeatureId",id);
            String userAccount=null;
            JSONArray personList = new JSONArray();
            for (int i = 0; i < userList.size(); i++) {
                JSONObject user = userList.getJSONObject(i);
                JSONObject model = new JSONObject();
                model.put("Code", user.getString("userAccount"));
                userAccount=user.getString("userAccount");
                personList.add(model);
            }
            notifyJson.put("params", personList);
            thirdServerService.saveThird(notifyJson,serialNo,userAccount);
            ryPushMsgUtil.push(serialNo,notifyJson);
        }
    }
    public void updateCard(JSONObject body,String serialNo,Long id) {
        JSONArray cardList=body.getJSONArray("data");
        id=thirdServerService.getId(id);
        if(EmptyUtil.isNotEmpty(cardList)&&cardList.size()!=0) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id", id);
            notifyJson.put("method", "cardManager.updateCard");
            String userAccount=null;
            JSONObject params = new JSONObject();
            for (int i = 0; i < cardList.size(); i++) {
                JSONObject cardJson = cardList.getJSONObject(i);
                userAccount=cardJson.getString("userAccount");
                //把16进制转10进制
                params.put("ID", cardJson.containsKey("cardNo10")&&EmptyUtil.isNotEmpty(cardJson.getString("cardNo10"))
                        ?cardJson.getString("cardNo10")
                        : CardNo.has16hxTo10(CardNo.reverseCardNo(cardJson.getString("cardNo")))+"");
                JSONObject card = new JSONObject();
                card.put("PersonCode", userAccount);
                card.put("Type", EmptyUtil.isNotEmpty(cardJson.getString("type")) ?
                        cardJson.getString("type") : 1);
                List<String> validity = new ArrayList<>();
                String startTime=cardJson.getString("startTime");
                String endTime=cardJson.getString("endTime");
                validity.add(DateUtil.convertTimeLongToStr(startTime));
                validity.add(DateUtil.convertTimeLongToStr(endTime));
                card.put("Validity", validity);
                JSONObject Memo = new JSONObject();
                Memo.put("Entrance",cardJson.getString("desc"));
                card.put("Memo",Memo );
                params.put("Card", card);
            }

            notifyJson.put("params", params);
            thirdServerService.saveThird(notifyJson,serialNo,userAccount);
            ryPushMsgUtil.push(serialNo,notifyJson);
        }
    }
    public void removeCard(JSONObject body,String serialNo,Long id) {
        JSONArray cardList=body.getJSONArray("data");
        id=thirdServerService.getId(id);
        if(EmptyUtil.isNotEmpty(cardList)&&cardList.size()!=0) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id", id);
            notifyJson.put("method", "cardManager.removeCard");
            String userAccount=null;
            JSONObject params = new JSONObject();
            for (int i = 0; i < cardList.size(); i++) {
                JSONObject cardJson = cardList.getJSONObject(i);
                params.put("ID", CardNo.has16hxTo10(CardNo.reverseCardNo(cardJson.getString("cardNo")))+"");
            }
            notifyJson.put("params", params);
            thirdServerService.saveThird(notifyJson,serialNo,userAccount);
            ryPushMsgUtil.push(serialNo,notifyJson);
        }
    }
    public void syncPwd(JSONObject body,int enable,String serialNo,Long id) {
        JSONArray pwdList=body.getJSONArray("data");
        id=thirdServerService.getId(id);
        if(EmptyUtil.isNotEmpty(pwdList)&&pwdList.size()!=0) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id", id);
            notifyJson.put("method", "personManager.updatePerson");
            String userAccount=null;
            JSONObject params = new JSONObject();
            for (int i = 0; i < pwdList.size(); i++) {
                JSONObject bean = pwdList.getJSONObject(i);
                params.put("Type", 1);
                params.put("Code", bean.getString("userAccount"));
                userAccount=bean.getString("userAccount");
                JSONObject person = new JSONObject();
                person.put("Status", 1);
                person.put("PasswordEnable", enable);
                person.put("Password",  EmptyUtil.isNotEmpty(bean.getString("password"))
                        ?bean.getString("password"):"0");
                person.put("PasswordPhone", EmptyUtil.isNotEmpty(bean.getString("phone"))
                        ?bean.getString("phone"): Constants.commonPhone);
                params.put("Person",person);
            }
            notifyJson.put("params", params);
            thirdServerService.saveThird(notifyJson,serialNo,userAccount);
            ryPushMsgUtil.push(serialNo,notifyJson);
        }
    }
    public void setVisitorCode(JSONObject body,String serialNo,Long id) {
        JSONArray dataList=body.getJSONArray("data");
        id=thirdServerService.getId(id);
        if(EmptyUtil.isNotEmpty(dataList)&&dataList.size()!=0) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id", id);
            notifyJson.put("method", "personManager.insertPerson");
            JSONArray cardList= new JSONArray();
            JSONObject params = new JSONObject();
            String userAccount=null;
            for (int i = 0; i < dataList.size(); i++) {
                JSONObject user = dataList.getJSONObject(i);
                String phone=EmptyUtil.isNotEmpty(user.getString("phone"))?user.getString("phone"):Constants.commonPhone;
                String password=user.getString("password");
                String startTime=user.getString("startTime");
                String endTime=user.getString("endTime");
                userAccount=user.getString("userAccount");
                if(EmptyUtil.isEmpty(userAccount)) {
                    if (phone.matches("[\u4e00-\u9fa5]")) {
                        userAccount = "temp_person_" + id;
                    } else {
                        userAccount = "temp_person_" + phone;
                    }
                }
                String realName=user.getString("realName");
                if(EmptyUtil.isEmpty(realName)) {
                    if(EmptyUtil.isNotEmpty(userAccount)) {
                        realName = userAccount.replace("temp_person_", "临时人员密码");
                    }else{
                        realName=phone;
                    }
                }

                JSONObject person = new JSONObject();
                person.put("Type", 2);
                person.put("Name", realName);
                JSONObject guestInfo = new JSONObject();
                Long startTimeStamp= DateUtil.getTimeStampFormat(startTime,DateUtil.yyyy_MM_ddHH_mm_ss);
                Long endTimeStamp=DateUtil.getTimeStampFormat(endTime,DateUtil.yyyy_MM_ddHH_mm_ss);

                List<Long> accessTimeList = new ArrayList<>();
                accessTimeList.add(startTimeStamp);
                accessTimeList.add(endTimeStamp);
                guestInfo.put("AccessTime",accessTimeList);
                person.put("GuestInfo", guestInfo);
                person.put("Code", userAccount);

                person.put("GroupName","");
                person.put("PasswordEnable", 1);
                person.put("Password",password);
                person.put("PasswordPhone",phone);
                person.put("AccessTimes",new ArrayList<>());
                params.put("Person",person);


                JSONObject cardJson = new JSONObject();
                cardJson.put("cardNo",password);
                cardJson.put("cardNo10",password);
                cardJson.put("userAccount",userAccount);
                cardJson.put("type",1);
                cardJson.put("startTime",startTimeStamp);
                cardJson.put("endTime",endTimeStamp);
                cardList.add(cardJson);
            }
            notifyJson.put("params", params);
            thirdServerService.saveThird(notifyJson,serialNo,userAccount);
            ryPushMsgUtil.push(serialNo,notifyJson);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            JSONObject cardBody = new JSONObject();
            cardBody.put("data",cardList);
            updateCard(cardBody,serialNo,0l);
        }
    }

    public void remoteOpenDoor(JSONObject body,String serialNo) {
        JSONArray dataList=body.getJSONArray("data");
        if(EmptyUtil.isNotEmpty(serialNo)) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id", IdGen.randomInteger());
            notifyJson.put("method", "devDoor.openOnce");
            JSONObject params = new JSONObject();
            params.put("channel",0);
            notifyJson.put("params", params);
            thirdServerService.saveThird(notifyJson,serialNo,null);
            ryPushMsgUtil.push(serialNo,notifyJson);
        }
    }
    public void clearAllGroup(JSONObject body,String serialNo) {
        JSONArray configList=body.getJSONArray("data");
        if(EmptyUtil.isNotEmpty(serialNo)) {
            JSONObject notifyJson = new JSONObject();
            notifyJson.put("id", IdGen.randomInteger());
            notifyJson.put("method", "faceGroupManager.clearAllGroup");
            thirdServerService.saveThird(notifyJson,serialNo,null);
            ryPushMsgUtil.push(serialNo,notifyJson);
        }
    }
    public void update(JSONObject body,String serialNo) {
        JSONArray configList=body.getJSONArray("data");
        if(EmptyUtil.isNotEmpty(configList)&&configList.size()>0) {
            for (int i = 0; i < configList.size(); i++) {
                JSONObject params = configList.getJSONObject(i);
                if(EmptyUtil.isNotEmpty(params)) {

                }
            }
        }

    }
    public void updateTime(JSONObject body,String serialNo) {
        JSONArray configList=body.getJSONArray("data");
        for (int i = 0; i < configList.size(); i++) {
            JSONObject params = configList.getJSONObject(i);
        }
    }

}
