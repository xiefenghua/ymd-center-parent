package com.ymd.cloud.authorizeCenter.job.queue;

import java.util.Queue;

public interface MyQueue extends Queue {
	String queueName();
	long belongStart();
	long belongEnd();
}
