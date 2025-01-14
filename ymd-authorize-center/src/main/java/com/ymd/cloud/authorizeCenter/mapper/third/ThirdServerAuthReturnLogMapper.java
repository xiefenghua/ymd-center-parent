package com.ymd.cloud.authorizeCenter.mapper.third;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.third.ThirdServerAuthReturnLog;
import org.springframework.stereotype.Component;

@Component
public interface ThirdServerAuthReturnLogMapper extends BaseMapper<ThirdServerAuthReturnLog> {
    ThirdServerAuthReturnLog selectReturnLogBySerialNo(ThirdServerAuthReturnLog model);
    void delBefore1Month();
}