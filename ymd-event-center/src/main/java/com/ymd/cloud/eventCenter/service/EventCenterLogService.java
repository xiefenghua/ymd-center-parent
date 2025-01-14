package com.ymd.cloud.eventCenter.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.common.utils.PageRequest;
import com.ymd.cloud.eventCenter.model.vo.EventCenterLog;

import java.util.List;

public interface EventCenterLogService extends IService<EventCenterLog> {
    PageInfo<EventCenterLog> pageEventCenterLogList(PageRequest request, EventCenterLog model);
    JSONObject saveLog(EventCenterLog model);
    void saveEventLog(String taskNo,String postUrl, String param, String resultCode, String result, long start, String account,Integer retryCount);
    void saveEventLog(String taskNo,String postUrl, String param, String resultCode, String result, long start, String account);
    int selectExistByTaskNo(String taskNo);
    List<EventCenterLog> listenResult(String taskNo, Long timeStemp);

}
