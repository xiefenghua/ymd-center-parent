package com.ymd.cloud.authorizeCenter.thirdServer.model;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhanggy
 * @version 1.0
 * @date 2020/8/21 10:21
 */
@Data
public class WebSocketDTO implements Serializable {
    private static final long serialVersionUID = -6451812593150428369L;
    private String json;
    private String data;
    private String clientId;
    public WebSocketDTO() {
        super();
    }
    @Override
    public String toString() {
        return "WebSocketDTO [clientId=" + clientId + ", data=" + data
                + ", data=" + data +"]";
    }
}



