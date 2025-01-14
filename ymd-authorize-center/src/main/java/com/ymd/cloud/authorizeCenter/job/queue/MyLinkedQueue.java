package com.ymd.cloud.authorizeCenter.job.queue;

import java.io.Serializable;
import java.util.concurrent.LinkedBlockingQueue;

public class MyLinkedQueue<T> extends LinkedBlockingQueue  implements MyQueue,Serializable{
    private static final long serialVersionUID = 1406881264853111039L;
	private long belongStart;
	private long belongEnd;
	private String queueName;
	public MyLinkedQueue(int capacity){
		super(capacity);
		this.belongStart=-1;
		this.belongEnd=Long.MAX_VALUE;
		this.queueName=Thread.currentThread().getName();
	}
	public MyLinkedQueue(String queueName, int capacity,long belongStart, long belongEnd){
		super(capacity);
		this.belongStart=belongStart;
		this.belongEnd=belongEnd;
		this.queueName=queueName;
	}
	public String queueName() {
		return queueName;
	}
	public long belongStart() {
		return belongStart;
	}
	public long belongEnd() {
		return belongEnd;
	}
}
