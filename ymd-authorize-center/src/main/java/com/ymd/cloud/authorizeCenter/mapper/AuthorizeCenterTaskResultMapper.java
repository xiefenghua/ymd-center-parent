package com.ymd.cloud.authorizeCenter.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTask;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTaskResult;
import com.ymd.cloud.authorizeCenter.model.entity.TaskParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuthorizeCenterTaskResultMapper extends BaseMapper<AuthorizeCenterTaskResult> {
    List<AuthorizeCenterTaskResult> selectListPage(TaskParam queryParam);

    List<AuthorizeCenterTaskResult> selectByTaskNo(@Param("taskNo") String taskNo);
    AuthorizeCenterTaskResult selectByAuthNo(@Param("authNo") String authNo);
    List<AuthorizeCenterTaskResult> selectCardCountByTaskNo(@Param("taskNo") String taskNo,@Param("cardList") List<String> cardList);
    List<AuthorizeCenterTaskResult> selectLockCountByTaskNo(@Param("taskNo") String taskNo);
    void delTaskResultBefore1Month();
}