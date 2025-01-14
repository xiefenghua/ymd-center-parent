/**
 * Copyright (C), 杭州云门道科技有限公司
 * FileName:
 * Author:   hhd668@163.com
 * Date:     2019/3/8 17:39
 * Description:
 * History:
 * <author>          <time>          <version>          <desc>
 * 作者姓名           修改时间           版本号              描述
 */
package com.ymd.cloud.common.redis;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * RedisKeyUtil Redis KEY生成 工具类 <br>
 *
 * @author hhd668@163.com
 * @create 2019/3/8
 * @since 1.0
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisKeyUtil {
    /**
     * The constant RESET_PWD_TOKEN_KEY.
     */
    private static final String ACCESS_TOKEN = "ymdcloud:token:accessToken";

    public static String getAccessTokenKey(String key) {
        Preconditions.checkArgument(StringUtils.isNotEmpty(key), "非法请求token参数不存在");
        return key + ":" + ACCESS_TOKEN;
    }

}
