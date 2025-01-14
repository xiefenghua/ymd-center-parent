package com.ymd.cloud.authorizeCenter.thirdServer.sever;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ymd.cloud.api.RedisService;
import com.ymd.cloud.authorizeCenter.model.third.ThirdServerAuthReturnLog;
import com.ymd.cloud.authorizeCenter.thirdServer.service.ThirdServerService;
import com.ymd.cloud.common.utils.EmptyUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * socket推送消息(服务端)
 */
@ServerEndpoint(value = "/api/ws",encoders = { ServerEncoder.class })
@Service
@Slf4j
public class WebSocketServerService {
    /**concurrent包的线程安全Set，用来存放每个客户端对应的WebSocket对象。*/
    public static ConcurrentHashMap<String, WebSocketServerService> webSocketMap = new ConcurrentHashMap<>();
    public Session getSession() {
        return session;
    }
    private Session session;
    public static String PING="ping";
    public static String PONG="pong";
    public static String heartKey="HEART_PONG";
    public static String connectCountKey="HEART_CONNECT_COUNT";
    @Autowired
    RedisService redisService;
    @Autowired
    ThirdServerService thirdServerService;

    /**
     * 连接建立成
     * 功调用的方法
     */
    @OnOpen
    public void onOpen(Session session) {
        String serialNo = getNo(session);
        String devName = getName(session);
        try {
            this.session = session;
            if (webSocketMap.containsKey(serialNo)) {
                webSocketMap.remove(serialNo);
                //加入set中
                webSocketMap.put(serialNo, this);
            } else {
                //加入set中
                webSocketMap.put(serialNo, this);
            }
            redisService.hdel(heartKey,serialNo);
            redisService.hdel(connectCountKey,serialNo);
            log.info("【锐颖webSocket】连接建立成功>>>>设备序列号连接:" + serialNo + ",当前在线数为:" + getOnlineCount());
        }catch (Exception e){
            log.error("【锐颖webSocket--onOpen】连接建立>>>>设备序列号:" + serialNo + " 入库失败:{}",e);
        }
    }

    /**
     * 连接关闭
     * 调用的方法
     */
    @OnClose
    public void onClose(Session session) {
        try{
            String serialNo = getNo(session);
            session.close();
            offLine(serialNo);
            log.info("【锐颖webSocket】连接关闭>>>>>设备序列号退出:"+serialNo+"session是否关闭:"+(!(session.isOpen()))+",当前在线数为:" + getOnlineCount());
        }catch (Exception e){
            log.info("【锐颖webSocket】连接关闭>>>>>异常{}",e);
        }
    }

    public void offLine(String serialNo) {
        try {
            thirdServerService.heartOnline(serialNo,false);
        }catch (Exception e){  }
        if(webSocketMap.containsKey(serialNo)){
            webSocketMap.remove(serialNo);
            redisService.hdel(heartKey,serialNo);
            redisService.hdel(connectCountKey,serialNo);
        }
    }


    /**
     * 收到客户端消
     * 息后调用的方法
     * @param message
     * 客户端发送过来的消息
     **/
    @OnMessage
    public void onMessage(String message, Session session) {
        String serialNo = getNo(session);
//        log.info("【锐颖webSocket】收到客户端消>>>>>>设备---服务器(设备应答)序列号:"+serialNo+",报文:"+message);
        //可以群发消息
        //消息保存到数据库、redis
        if(StringUtils.isNotBlank(message)&&isJSON(message)){
            try {
                //解析发送的报文
                JSONObject jsonObject = JSON.parseObject(message);
                String to=jsonObject.getString("serialNumber");
                //为了防止串改 判断请求session中的序列号和报文中的序列号是否一致
                if(serialNo.equals(to)) {
                    long id=jsonObject.getLong("id");
                    boolean result=jsonObject.getBoolean("result");
                    String userAccount=null;
                    String phone=null;
                    try {
                        if(jsonObject.containsKey("params")) {
                            JSONArray params = jsonObject.getJSONArray("params");
                            if (EmptyUtil.isNotEmpty(params) && params.size() > 0) {
                                userAccount = params.getJSONObject(0).getString("Code");
                                //TODO 根据账号查询手机号


                            }
                        }
                    }catch (Exception e){}
                    if (StringUtils.isNotBlank(to) && webSocketMap.containsKey(to)) {
                        if(result){
                            String type=null;
                            String opType=null;
                            if("cardManager.updateCard".equals(jsonObject.getString("method"))
                            ||"cardManager.removeCard".equals(jsonObject.getString("method"))){
                                type="3";
                                if("cardManager.updateCard".equals(jsonObject.getString("method"))){
                                    opType="add";
                                }else if("cardManager.removeCard".equals(jsonObject.getString("method"))){
                                    opType="del";
                                }
                            } else if("personManager.updatePerson".equals(jsonObject.getString("method"))
                                    ||"personManager.insertPerson".equals(jsonObject.getString("method"))
                                ){
                                type="4";
                            }else if("personnelData.removePersons".equals(jsonObject.getString("method"))
                                    ||"personnelData.savePersons".equals(jsonObject.getString("method"))){
                                type="2";
                                if("personnelData.savePersons".equals(jsonObject.getString("method"))){
                                    opType="add";
                                }else if("personnelData.removePersons".equals(jsonObject.getString("method"))){
                                    opType="del";
                                }
                            }
                            thirdServerService.pushThirdAuthReturn(serialNo,id+"",type,userAccount,opType);
                        }
                    }
                    try {
                        boolean online=webSocketMap.containsKey(to);
                        ThirdServerAuthReturnLog model=new ThirdServerAuthReturnLog();
                        model.setMsgId(id);
                        model.setSerialNo(to);
                        model.setMethod(jsonObject.getString("method"));
                        if(userAccount!=null) {
                            model.setUserAccount(userAccount);
                        }
                        if(EmptyUtil.isNotEmpty(phone)){
                            model.setPhone(phone);
                        }
                        model.setResultContent(jsonObject.toJSONString());
                        model.setResultCode(result);
                        model.setCreateTime(new Date());
                        model.setDeviceOnline(online);
                        thirdServerService.saveThirdPartyReturnLog(model);
                    }catch (Exception e){ log.error("tblThirdPartyReturnLogService.save报错:{}",e); }
                }else{
                    log.error("【锐颖webSocket--onMessage】收到客户端消>>>>>>系统检测到非法序列号请求，当前session序列号：{} 报文序列号：{}",serialNo,to);
                }
            }catch (Exception e){
                log.error("【锐颖webSocket--onMessage】报错:{}",e);
            }
        }else if(PING.equals(message)){
            redisService.hdel(heartKey,serialNo);
            redisService.hdel(connectCountKey,serialNo);
            webSocketMap.put(serialNo, this);
            sendMessage(PONG,serialNo);
            thirdServerService.heartOnline(serialNo,true);
        }
    }

    /**
     * @param session
     * @param error
     */
    @OnError
    public void onError(Session session, Throwable error) {
        String serialNo = getNo(session);
        log.error("【锐颖webSocket】连接错误>>>>>>错误:"+serialNo+",原因:{}",error);
    }

    /**
     * 实现服务
     * 器主动推送
     */
    public void sendMessage(String message) {
        try {
            if(this.session!=null&&this.session.isOpen()) {
                this.session.getBasicRemote().sendText(message);
            }else{
                log.error("【锐颖webSocket--sendMessage】session 为null或者已关闭");
            }
        } catch (Exception e) {
            log.error("【锐颖webSocket--sendMessage】报错:{}",e);
        }
    }
    public void sendObject(Object data) {
        try {
            if(this.session!=null&&this.session.isOpen()) {
                this.session.getBasicRemote().sendObject(data);
            }else{
                log.error("【锐颖webSocket--sendObject】session 为null或者已关闭");
            }
        } catch (Exception e) {
            log.error("【锐颖webSocket--sendObject】报错:{}",e);
        }
    }

    public void sendAll(String message) {
        try {
            for (WebSocketServerService webSocket : this.webSocketMap.values()) {
                if(webSocket.session!=null&&webSocket.session.isOpen()) {
                    webSocket.sendMessage(message);
                }else{
                    log.error("【锐颖webSocket--sendAll】session 为null或者已关闭");
                }
            }
        } catch (Exception e) {
            log.error("【锐颖webSocket--sendAll】报错:{}",e);
        }
    }
    /**
     *发送自定
     *义消息
     **/
    public void sendObject(Object data, String serialNo) {
        if(StringUtils.isNotBlank(serialNo) && webSocketMap.containsKey(serialNo)){
            webSocketMap.get(serialNo).sendObject(data);
        }else{
            log.error("【锐颖webSocket】发送消息>>>>设备"+serialNo+",不在线！");
        }
    }
    public void sendMessage(String message, String serialNo) {
        if(StringUtils.isNotBlank(serialNo) && webSocketMap.containsKey(serialNo)){
            webSocketMap.get(serialNo).sendMessage(message);
        }else{
            log.error("【锐颖webSocket】发送消息>>>>设备"+serialNo+",不在线！");
        }
    }
    /**
     * 获得此时的
     * 在线人数
     * @return
     */
    public static synchronized int getOnlineCount() {
        return webSocketMap.size();
    }

    public boolean isJSON(String str) {
        boolean result = false;
        try {
            Object obj=JSON.parse(str);
            result = true;
        } catch (Exception e) {
            result=false;
        }
        return result;
    }
    private String getNo(Session session) {
        String serialNo=null;
        List<String> serialNos = session.getRequestParameterMap().get("SerialNo");
        if (EmptyUtil.isNotEmpty(serialNos)) {
            serialNo = serialNos.get(0);
        }
        return serialNo;
    }
    private String getName(Session session) {
        String name=null;
        List<String> names = session.getRequestParameterMap().get("DevName");
        if (EmptyUtil.isNotEmpty(names)) {
            name = names.get(0);
        }
        return name;
    }
}





