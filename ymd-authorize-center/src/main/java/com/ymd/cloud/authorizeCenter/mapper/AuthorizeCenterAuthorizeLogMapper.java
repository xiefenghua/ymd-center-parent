package com.ymd.cloud.authorizeCenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterAuthorizeLog;

public interface AuthorizeCenterAuthorizeLogMapper extends BaseMapper<AuthorizeCenterAuthorizeLog> {
    void delBefore6Month();
}