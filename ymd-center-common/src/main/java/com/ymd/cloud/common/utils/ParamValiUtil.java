/**
 * Copyright (C), 杭州云门道科技有限公司
 * FileName:
 * Author:   hhd668@163.com
 * Date:     2019/4/15 16:16
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.ymd.cloud.common.utils;


import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.exception.ValidateException;

/**
 * 〈参数验证工具类〉<br>
 *
 * @author hhd668@163.com
 * @create 2019/4/15
 * @since 1.0
 */
public class ParamValiUtil {
    private ParamValiUtil() {
    }
    public static void checkArgumentNotEmpty(Object value,String paramName) {
        if (EmptyUtil.isEmpty(value)) {
            throw new ValidateException(paramName + ","+ ErrorCodeEnum.MESSAGE_PARAM_ERROR);
        }
    }
    public static void checkRegEx(String paramName,String value,String regEx,String msg) {
        if (value.matches(regEx)) {
            throw new ValidateException(paramName + ","+msg);
        }
    }
    public static void returnStrParamNotEmpty(StringBuffer stringBuffer,Object value,String paramName) {
        if (EmptyUtil.isEmpty(value)) {
            stringBuffer.append(paramName +ErrorCodeEnum.MESSAGE_PARAM_ERROR+ ",");
        }
    }
    public static void checkParamLength(String paramName,String value,int length) {
        if(EmptyUtil.isNotEmpty(value)&&(value.length()>length)){
            throw new ValidateException(paramName+ErrorCodeEnum.MESSAGE_PARAM_LENGTH_ERROR+length+"个字符");
        }
    }

}
