package com.ymd.cloud.common.base;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.common.enumsSupport.ErrorCodeEnum;
import com.ymd.cloud.common.exception.ReturnMessageKey;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ModelMap;

import java.nio.charset.Charset;
import java.util.List;

/**
 * 抽象控制器
 * 
 */
@Slf4j
public abstract class AbstractController{
    protected ResponseEntity<ModelMap> setSuccessModelMap()
    {
        return setModelMap(ErrorCodeEnum.SUCCESS.code(),ErrorCodeEnum.SUCCESS.msg(), null);
    }
    protected ResponseEntity<ModelMap> setSuccessModelMap(Object data)
    {
        return setModelMap(ErrorCodeEnum.SUCCESS.code(),ErrorCodeEnum.SUCCESS.msg(), data);
    }

    protected ResponseEntity<ModelMap> setSuccessModelMap(JSONObject obj)
    {
        return setModelMap(obj);
    }
    protected ResponseEntity<ModelMap> setModelMap(String code, String msg)
    {
        return setModelMap(code, msg, null);
    }

    protected ResponseEntity<ModelMap> setModelMap(JSONObject obj)
    {
        return setModelMap(String.valueOf(obj.get("status")), String.valueOf(obj.get("message")),obj.get("data"));
    }

    /**
     * <一句话功能简述>
     * @param code
     * @param msg
     * @param data
     * @return
     * @see [类、类#方法、类#成员]
     */
    @SuppressWarnings("rawtypes")
    protected ResponseEntity<ModelMap> setModelMap(String code, String msg, Object data) {
        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
        headers.setContentType(mediaType);
        ModelMap modelMap=new ModelMap();
        if (data != null)
        {
            ModelMap dataMap=new ModelMap();
            if ((data instanceof PageInfo<?>))
            {
                PageInfo<?> page = (PageInfo)data;
                dataMap.put("rows", page.getList()); // 返回数据列表
                dataMap.put("pageNum", page.getPageNum());// 当前页
                dataMap.put("pageSize", page.getPageSize());// 页码大小
                dataMap.put("pages", page.getPages()); // 总页数
                dataMap.put("total", page.getTotal()); // 总条数
                dataMap.put("startRow", page.getStartRow()); // 开始行
                dataMap.put("endRow", page.getEndRow()); // 结束行
                dataMap.put("prePage", page.getPrePage()); // 前一页
                dataMap.put("nextPage", page.getNextPage()); // 后一页
                dataMap.put("isFirstPage", page.getNextPage()); // 后一页
                dataMap.put("isLastPage", page.getNextPage()); // 后一页
                dataMap.put("hasPreviousPage", page.isHasPreviousPage()); // 是否有前一页
                dataMap.put("hasNextPage", page.isHasNextPage()); // 是否有后一页
                dataMap.put("navigatePages", page.getNavigatePages()); // 总共几个导航页
                dataMap.put("navigatepageNums", page.getNavigatepageNums()); // 导航页码集合
                dataMap.put("navigateFirstPage", page.getNavigateFirstPage()); // 第一个导航页
                dataMap.put("navigateLastPage", page.getNavigateLastPage()); // 最后一个导航页
                modelMap.put(ReturnMessageKey.DATA, dataMap);
            }
            else if ((data instanceof List))
            {
                dataMap.put("rows", data);
                dataMap.put("total", ((List)data).size());
                modelMap.put(ReturnMessageKey.DATA, dataMap);
            }
            else
            {
                modelMap.put(ReturnMessageKey.DATA, data);
            }

        }
        modelMap.put(ReturnMessageKey.CODE, code);
        modelMap.put(ReturnMessageKey.MESSAGE, msg);
        modelMap.put(ReturnMessageKey.TIMESTAMP, Long.valueOf(System.currentTimeMillis()));
        return new ResponseEntity<>(modelMap, headers, HttpStatus.OK);
    }
}
