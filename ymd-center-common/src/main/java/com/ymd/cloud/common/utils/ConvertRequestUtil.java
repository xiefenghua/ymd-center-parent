package com.ymd.cloud.common.utils;


import com.ymd.cloud.api.common.RequestParamKey;

public class ConvertRequestUtil
{
    /**
     * 分页请求参数
     *
     * @return
     * @see [类、类#方法、类#成员]
     */
    public static PageRequest pageRequest()
    {
        if (ServletUtil.getParameter(RequestParamKey.ORDER_BY_COLUMN) == null){
            return new PageRequest(ServletUtil.getParameterToInt(RequestParamKey.PAGE_NUM), ServletUtil.getParameterToInt(RequestParamKey.PAGE_SIZE), null);
        }
        return new PageRequest(ServletUtil.getParameterToInt(RequestParamKey.PAGE_NUM), ServletUtil.getParameterToInt(RequestParamKey.PAGE_SIZE), ConvertUtil.camel2Underline(ServletUtil.getParameter(RequestParamKey.ORDER_BY_COLUMN)) + " " + ServletUtil.getParameter(RequestParamKey.IS_ASC).toLowerCase());
    }
}
