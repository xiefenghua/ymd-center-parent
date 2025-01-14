package com.ymd.cloud.authorizeCenter.model.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

//授权同步数据
@Data
@EqualsAndHashCode(callSuper = false)
public class AuthorizeDataVo implements Serializable {
    private Long id;
    //操作码
    private int code;
    //开始时间
    private String startTime;
    //结束时间
    private String endTime;
	//授权伪删除标识 1正常 0删除
	private Integer status;
	//授权是否有效 1=有效 0=无效
	private Integer flag;
	//同步状态 1同步 0未同步
	private Integer sync;
	//冻结状态 1正常 0冻结
	private Integer freezeStatus;
	//开始时间
	private long start;
	//结束时间
	private long end;


	//卡片类型
	private int cardType;
	//UID
	private String nfcId;
	//DN码
	private String dnCode;

	//密码类型
	private int pwdType;
	private String pwdType16;
	//密码
	private String pwd;


	private int  fingerId;
	private int fingerType;

	private String faceImgUrl;
	private String faceFeature;



}
