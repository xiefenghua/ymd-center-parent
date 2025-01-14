package com.ymd.cloud.authorizeCenter.model.third;

import com.ymd.cloud.common.enumsSupport.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = false)
public class PersonInfo implements Serializable {
    private String userAccount;

    private String createUser;//创建人 默认账号
    private String realName;//姓名  默认账号
    private String phone= Constants.commonPhone;//手机号
    private String sex = "1";//0 女 1 男
    private String idCardNo="";//身份证
    private String birthday="1970-01-01";//出生日期

}
