package com.ymd.cloud.authorizeCenter.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterPersonAuthorize;

public interface AuthorizeCenterPersonAuthorizeMapper extends BaseMapper<AuthorizeCenterPersonAuthorize> {
    AuthorizeCenterPersonAuthorize selectPersonAuthorizeListByDeviceUser(AuthorizeCenterPersonAuthorize authorizeCenterPersonAuthorize);
}