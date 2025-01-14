package com.ymd.cloud.eventCenter.mq;

import java.io.IOException;

public interface BaseConsumer {
    String consume(String taskNo, String topic, String jobType, String msgBody) throws IOException;
}
