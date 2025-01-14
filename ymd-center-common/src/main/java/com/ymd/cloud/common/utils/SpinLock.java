package com.ymd.cloud.common.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicStampedReference;

@Slf4j
public class SpinLock {
    public static AtomicStampedReference<Thread> atomicStampedReference = new AtomicStampedReference(null, 0);

    /**
     * 自旋锁的简单实现
     */
    public static void lock(){
        while (!atomicStampedReference.compareAndSet(null, Thread.currentThread(), 0, 1)){

        }
        log.info(Thread.currentThread().getName() + "加锁成功");
    }
    public static void unLock(){
        log.info(Thread.currentThread().getName() + "解锁成功");
        atomicStampedReference.compareAndSet( Thread.currentThread(), null, 1, 0);
    }
}
