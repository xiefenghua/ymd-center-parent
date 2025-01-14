package com.ymd.cloud.authorizeCenter.thirdServer.factory;

import com.ymd.cloud.authorizeCenter.enums.VendorTypeEnum;
import com.ymd.cloud.authorizeCenter.thirdServer.handler.BtHandlerService;
import com.ymd.cloud.authorizeCenter.thirdServer.handler.HandlerService;
import com.ymd.cloud.authorizeCenter.thirdServer.handler.JlHandlerService;
import com.ymd.cloud.authorizeCenter.thirdServer.handler.RyHandlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HandlerFactory {
    @Autowired
    RyHandlerService ryHandlerService;
    @Autowired
    BtHandlerService btHandlerService;
    @Autowired
    JlHandlerService jlHandlerService;
    public HandlerService getInstall(String vendor){
        HandlerService handlerService=null;
        if (VendorTypeEnum.RY.getVentor().equals(vendor)) {
            handlerService= ryHandlerService;
        }else if (VendorTypeEnum.BT.getVentor().equals(vendor)) {
            handlerService= btHandlerService;
        }else if (VendorTypeEnum.JL.getVentor().equals(vendor)) {
            handlerService=jlHandlerService;
        }
        return handlerService;
    }
}
