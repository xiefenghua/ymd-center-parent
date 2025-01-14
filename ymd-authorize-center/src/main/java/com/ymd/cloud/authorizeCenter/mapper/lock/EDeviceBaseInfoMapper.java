package com.ymd.cloud.authorizeCenter.mapper.lock;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.lock.EDeviceBaseInfo;
import com.ymd.cloud.authorizeCenter.model.lock.ResourceConfig;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 设备基础信息表(EDeviceBaseInfo)表数据库访问层
 *
 * @author zgy
 * @since 2020-05-15 17:14:24
 */
@Component
public interface EDeviceBaseInfoMapper extends BaseMapper<EDeviceBaseInfo> {
    ResourceConfig selectResourceConfig(ResourceConfig resourceConfig);
    Integer selectMqttTypeByGateMac(@Param("deviceMac") String deviceMac);
}