package com.ymd.cloud.api;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 〈一句话功能简述〉<br> 
 *
 * @author hhd668@163.com
 * @create 2019/3/8
 * @since 1.0
 */
public interface RedisService {
    String get(String key);
    void set(String key, String value);
    void set(String key, String value, long timeout);
    void remove(String key);
    long incr(String key, long delta);
    long decr(String key, long delta);
    void clear();
    boolean expire(String key, long time);
    long getExpire(String key);
    boolean exists(String key);
    //================================Map=================================
    <T> T hget(String key, String item, Class<T> clazz);
    Object hget(String key, String item);
    Map<String, String> hgetAll(String key);
    boolean hgetAll(String key, Map<String, Object> map);
    boolean hgetAll(String key, Map<String, Object> map, long time);
    boolean hset(String key, String item, Object value);
    boolean hset(String key, String item, Object value, long time);
    void hdel(String key, Object... item);
    boolean hHasKey(String key, String item);
    double hincr(String key, String item, double by);
    double hdecr(String key, String item, double by);
    boolean hasKey(String key);
    //============================set=============================
    Set<Object> sGet(String key);
    boolean sHasKey(String key, Object value);
    long sSet(String key, Object... values);
    long sSetAndTime(String key, long time, Object... values);
    long setRemove(String key, Object... values);
    long sGetSetSize(String key);
    //===============================list=================================
    List<Object> lGet(String key, long start, long end);
    long lGetListSize(String key);
    Object lGetIndex(String key, long index);
    boolean lSet(String key, Object value);
    boolean lSet(String key, Object value, long time);
    boolean lSet(String key, List<Object> value);
    boolean lSet(String key, List<Object> value, long time);
    boolean lUpdateIndex(String key, long index, Object value);
    long lRemove(String key, long count, Object value);
    //==============zSet=============================
    Long zcount(String key);
    Long zScoreCount(String key, long start, long end);
    Boolean zadd(String key, double score, String value);
    Set<String> zrange(String key, long start, long end);
    Long zremRangeByScore(String key, double scoreMin, double scoreMax);
    //===============================分布式工具=================================
    String toJson(Object object);
    <T> T fromJson(String json, Class<T> clazz);
    boolean lock(String key,String value, long expire);
    void unLock(String key);
    boolean pipelinedString(final List<Map<String, Object>> list);
    boolean pipelinedHash(final List<Map<String, Object>> list);

    //==============模糊搜索key值是否存在,spring-redis 版本号在1.8之后的不需要关闭游标，之前的需要关闭游标。
    boolean scan(String key, long count) throws Exception;
}
