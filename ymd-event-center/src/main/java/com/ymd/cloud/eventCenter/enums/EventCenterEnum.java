package com.ymd.cloud.eventCenter.enums;

public enum EventCenterEnum {
    event_center("event_center","ymd.delay.queue","ymd.delay.exchange","ymd.delay.routeKey");
    public String job;
    public String queue;
    public String exchange;
    public String routeKey;
    EventCenterEnum(String job, String queue, String exchange, String routeKey) {
        this.job = job;
        this.queue = queue;
        this.exchange = exchange;
        this.routeKey=routeKey;
    }
}
