package com.ymd.cloud.eventCenter;

import com.ymd.cloud.common.utils.EmptyUtil;

public class MainTest {
    public static void main(String[] args) {
        Long score=99l;
        String id=Long.MAX_VALUE+"";
        System.out.println( Double.valueOf(score + id).doubleValue());
        score=141l;
        System.out.println( Double.valueOf(score + id).doubleValue());
        score=999l;
        System.out.println( Double.valueOf(score + id).doubleValue());
        score=199l;
        System.out.println( Double.valueOf(score + id).doubleValue());
        score=1199l;
        System.out.println( Double.valueOf(score + id).doubleValue());

        System.out.println( Double.valueOf(System.currentTimeMillis()).doubleValue());
        System.out.println( Double.valueOf(System.currentTimeMillis()+1).doubleValue());
        System.out.println( Double.valueOf(System.currentTimeMillis()+99).doubleValue());
        System.out.println( Double.valueOf(System.currentTimeMillis()+199).doubleValue());
        System.out.println( Double.valueOf(System.currentTimeMillis()).doubleValue());
    }
}
