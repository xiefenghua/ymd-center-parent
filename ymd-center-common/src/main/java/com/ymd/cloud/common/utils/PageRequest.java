package com.ymd.cloud.common.utils;

import lombok.Data;

import java.io.Serializable;

/**
 * Page Request
 *  
 * @author  黄华东
 * @version  [V1.00, 2020年04月20日]
 * @since V1.00
 */
@Data
public class PageRequest implements Serializable
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 6962757156163947300L;

    private Integer pageNum;
    
    private Integer pageSize;
    
    private String orderBy;
    
    public PageRequest()
    {
        this.pageNum = 1;
        this.pageSize = 10;
    }
    public PageRequest(Integer pageNum, Integer pageSize, String orderBy)
    {
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.orderBy = orderBy;
    }
}
