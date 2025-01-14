package com.ymd.cloud.authorizeCenter.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTaskNotes;
import com.ymd.cloud.authorizeCenter.model.entity.TaskParam;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AuthorizeCenterTaskNotesMapper extends BaseMapper<AuthorizeCenterTaskNotes> {
    List<AuthorizeCenterTaskNotes> selectListPage(TaskParam queryParam);

    List<AuthorizeCenterTaskNotes> selectTaskNotesPageByTaskNo(AuthorizeCenterTaskNotes authorizeCenterTaskNotes);
    Long selectTaskNotesCountByTaskNo(@Param("taskNo") String taskNo);
    AuthorizeCenterTaskNotes selectTaskNotesByAuthNo(@Param("authNo") String authNo);
    int deleteTaskNotesByTaskNo(@Param("taskNo") String taskNo);
    int delTaskNotesBefore1Month();
    int deleteTaskNotesById(@Param("id") Long id);
}