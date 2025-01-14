/**
 * Copyright (C), 杭州云门道科技有限公司
 * FileName:
 * Author:   hhd668@163.com
 * Date:     2019/3/8 18:36
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.ymd.cloud.common.redis.impl;

import com.google.common.base.Preconditions;
import com.google.gson.Gson;
import com.ymd.cloud.api.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.*;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Redis 操作服务类<br>
 */
@Slf4j
@Component
public class RedisServiceImpl  implements RedisService {
    private final static Gson GSON = new Gson();
    @Resource
    private RedisTemplate redisTemplate;
    public String get(String key) {
        String value = null;
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        if (redisTemplate.hasKey(key)) {
            value = ops.get(key);
        }
        log.info("getKey. [OK] key={}, value={}", key, value);
        return value;
    }

    @Override
    public void remove(String key) {
        redisTemplate.delete(key);
        log.info("deleteKey. [OK] key={}", key);

    }

    @Override
    public void set(String key, String value) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(key), "Redis key is not null");
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, value);
        log.info("setKey. [OK] key={}, value={}, expire=默认超时时间", key, value);
    }
    @Override
    public void set(String key, String value, long timeout) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(key), "Redis key is not null");
        ValueOperations<String, String> ops = redisTemplate.opsForValue();
        ops.set(key, value);
        redisTemplate.expire(key, timeout,TimeUnit.MINUTES);
        log.info("setKey. [OK] key={}, value={}, timeout={}, unit={}", key, value, timeout, TimeUnit.MINUTES);
    }
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, delta);
    }
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        return redisTemplate.opsForValue().increment(key, -delta);
    }
    /**
     * Clears this cache instance
     */
    public void clear() {
        redisTemplate.execute((RedisCallback) connection -> {
            connection.flushDb();
            return null;
        });
    }
    /**
     * 指定缓存失效时间
     *
     * @param key 键
     * @param time 时间(秒)
     */
    public boolean expire( String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    public long getExpire( String key) {
        return redisTemplate.getExpire(key, TimeUnit.SECONDS);
    }

    @Override
    public boolean exists(String key) {
        return redisTemplate.hasKey(key);
    }
    //================================Map=================================

    /**
     * HashGet
     *
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public Object hget( String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }


    /**
     * HashGet
     *
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    public <T> T hget( String key, String item, Class<T> clazz) {
        String value = (String) redisTemplate.opsForHash().get(key, item);
        return value == null ? null : fromJson(value, clazz);

    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<String, String> hgetAll( String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hgetAll( String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key 键
     * @param map 对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hgetAll( String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire( key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key 键
     * @param item 项
     * @param value 值
     * @return true 成功 false失败
     */
    public boolean hset( String key, String item, Object value) {
        try {
            int cs = 1; // 向Redis中入数据的次数
            while(true){
                redisTemplate.opsForHash().put(key, item, value);
                if(cs > 3 || hasKey( key)){
                    break;
                }else{
                    cs ++;
                }
            }
            if(cs > 3){
                throw new Exception("向Redis插入数据失败，请稍后再试");
            }
            return true;
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     * 并指定缓存失效时间
     *
     * @param key 键
     * @param item 项
     * @param value 值
     * @param time 时间(秒)  注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    public boolean hset( String key, String item, Object value,
                         long time) {
        try {
            int cs = 1; // 向Redis中入数据的次数
            while(true){
                redisTemplate.opsForHash().put(key, item, value);
                // 如果缓存失效时间大于0，则指定缓存失效时间
                if (time > 0) {
                    expire( key, time);
                }
                if(cs > 3 || hasKey( key)){
                    break;
                }else{
                    cs ++;
                }
            }
            if(cs > 3){
                throw new Exception("向Redis插入数据失败，请稍后再试");
            }
            return true;
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 删除hash表中的值
     *
     * @param key 键 不能为null
     * @param item 项 可以使多个 不能为null
     */
    public void hdel( String key, Object... item) {
        redisTemplate.opsForHash().delete(key, item);
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key 键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    public boolean hHasKey( String key, String item) {
        return redisTemplate.opsForHash().hasKey(key, item);
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key 键
     * @param item 项
     * @param by 要增加几(大于0)
     */
    public double hincr( String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, by);
    }

    /**
     * hash递减
     *
     * @param key 键
     * @param item 项
     * @param by 要减少记(小于0)
     */
    public double hdecr( String key, String item, double by) {
        return redisTemplate.opsForHash().increment(key, item, -by);
    }
    /**
     *判断redis中是否已存在key
     * @param key
     * @return true/false
     *
     */
    public boolean hasKey(String key){
        if (StringUtils.isBlank(key)){
            return false;
        }
        return redisTemplate.hasKey(key);
    }
    //============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     */
    public Set<Object> sGet( String key) {
        try {
            return redisTemplate.opsForSet().members(key);
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key 键
     * @param value 值
     * @return true 存在 false不存在
     */
    public boolean sHasKey( String key, Object value) {
        try {
            return redisTemplate.opsForSet().isMember(key, value);
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将数据放入set缓存
     *
     * @param key 键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSet( String key, Object... values) {
        try {
            return redisTemplate.opsForSet().add(key, values);
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 将set数据放入缓存
     *
     * @param key 键
     * @param time 时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    public long sSetAndTime( String key, long time, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().add(key, values);
            if (time > 0) {
                expire( key, time);
            }
            return count;
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     */
    public long sGetSetSize( String key) {
        try {
            return redisTemplate.opsForSet().size(key);
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 移除值为value的
     *
     * @param key 键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    public long setRemove( String key, Object... values) {
        try {
            Long count = redisTemplate.opsForSet().remove(key, values);
            return count;
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return 0;
        }
    }
    //===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key 键
     * @param start 开始
     * @param end 结束  0 到 -1代表所有值
     */
    public List<Object> lGet( String key, long start, long end) {
        try {
            return redisTemplate.opsForList().range(key, start, end);
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     */
    public long lGetListSize( String key) {
        try {
            return redisTemplate.opsForList().size(key);
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key 键
     * @param index 索引  index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     */
    public Object lGetIndex( String key, long index) {
        try {
            return redisTemplate.opsForList().index(key, index);
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key 键
     * @param value 值
     */
    public boolean lSet( String key, Object value) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            return true;
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     */
    public boolean lSet( String key, Object value, long time) {
        try {
            redisTemplate.opsForList().rightPush(key, value);
            if (time > 0) {
                expire( key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key 键
     * @param value 值
     */
    public boolean lSet( String key, List<Object> value) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            return true;
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);

            e.printStackTrace();
            return false;
        }
    }

    /**
     * 将list放入缓存
     *
     * @param key 键
     * @param value 值
     * @param time 时间(秒)
     */
    public boolean lSet( String key, List<Object> value, long time) {
        try {
            redisTemplate.opsForList().rightPushAll(key, value);
            if (time > 0) {
                expire( key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key 键
     * @param index 索引
     * @param value 值
     */
    public boolean lUpdateIndex( String key, long index, Object value) {
        try {
            redisTemplate.opsForList().set(key, index, value);
            return true;
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 移除N个值为value
     *
     * @param key 键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    public long lRemove( String key, long count, Object value) {
        try {
            Long remove = redisTemplate.opsForList()
                    .remove(key, count, value);
            return remove;
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Object转成JSON数据
     */
    public String toJson(Object object) {
        if (object instanceof Integer || object instanceof Long || object instanceof Float ||
                object instanceof Double || object instanceof Boolean || object instanceof String) {
            return String.valueOf(object);
        }
        return GSON.toJson(object);
    }

    /**
     * JSON数据，转成Object
     */
    public <T> T fromJson(String json, Class<T> clazz) {
        return GSON.fromJson(json, clazz);
    }

    /**
     * 加锁 setIfAbsent 方法将键 key 的值设置为 value，并且仅在键不存在时才设置成功。
     * 如果键已存在，则不进行任何操作，并返回 false；如果设置成功，则返回 true。
     * @param key
     * @param value
     * @param expire 秒
     * @return
     */
    public boolean lock(String key,String value, long expire) {
        try {
            return redisTemplate.opsForValue().setIfAbsent(key, value,expire,TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("【redis分布式锁】解锁异常, {}", e);
        }
        return false;
    }

    /**
     * 解锁
     */
    public void unLock(String key) {
        try {
            redisTemplate.delete(key);
        } catch (Exception e) {
            log.error("【redis分布式锁】解锁异常, {}", e);
        }
    }


    /**
     * 批量向redis中插入:key  value
     * 如果键已存在则返回false,不更新,防止覆盖。使用pipeline批处理方式(不关注返回值)
     *    @param list  一个map代表一行记录,2个key:key & value。
     *    @return
     */
    public boolean pipelinedString(final List<Map<String, Object>> list) {
        boolean result = (boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                for (Map<String, Object> map : list) {
                    byte[] key = serializer
                            .serialize( map.get("key").toString());
                    byte[] values = serializer.serialize(map.get("value").toString());
                    connection.set(key, values);
                }
                return true;
            }
        }, false, true);
        return result;
    }
    /**
     * 批量向redis中插入:key  value
     * 如果键已存在则返回false,不更新,防止覆盖。使用pipeline批处理方式(不关注返回值)
     *    @param list  一个map代表一行记录,2个key:key & value。
     *    @return
     */
    public boolean pipelinedHash(final List<Map<String, Object>> list) {
        boolean result = (boolean) redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection connection)
                    throws DataAccessException {
                RedisSerializer<String> serializer = redisTemplate.getStringSerializer();
                for (Map<String, Object> map : list) {
                    byte[] key = serializer
                            .serialize(map.get("key").toString());
                    byte[] hkey = serializer.serialize(map.get("hkey").toString());
                    byte[] values = serializer.serialize(map.get("value").toString());
                    connection.hSet(key, hkey, values);
                }
                return true;
            }
        }, false, true);
        return result;
    }

    //==============zSet=======================
    @Override
    public Long zcount(String key) {
        try {
            return redisTemplate.opsForZSet().size(key);
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public Long zScoreCount(String key, long start, long end){
        try {
            return redisTemplate.opsForZSet().count(key,start,end);
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return null;
        }
    }
    @Override
    public Boolean zadd(String key, double score, String value) {
        try {
            return redisTemplate.opsForZSet().add(key,value,score);
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Set<String> zrange(String key, long start, long end) {
        try {
            return redisTemplate.opsForZSet().range(key,start,end);
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public Long zremRangeByScore(String key, double scoreMin, double scoreMax) {
        try {
            return redisTemplate.opsForZSet().removeRangeByScore(key,scoreMin,scoreMax);
        } catch (Exception e) {
            log.error("redis 操作失败，失败原因：", e);
            e.printStackTrace();
            return null;
        }
    }

    /***
     * 模糊搜索key值是否存在,spring-redis 版本号在1.8之后的不需要关闭游标，之前的需要关闭游标。
     * @param key
     * @param count
     * @return
     */
    public boolean scan( String key, long count) throws Exception {
        RedisConnection redisConnection = redisTemplate.getConnectionFactory().getConnection();
        Cursor cursor = redisConnection
                .scan(ScanOptions.scanOptions().match(key).count(count).build());
        try {
            Boolean isHas = cursor.hasNext();
            return isHas;
        } catch (Exception e) {
            log.error("redis 查询key是否存在 异常：" + key, e);
            return false;
        } finally {
            if (null != cursor) {
                cursor.close();
            }
        }
    }
}
