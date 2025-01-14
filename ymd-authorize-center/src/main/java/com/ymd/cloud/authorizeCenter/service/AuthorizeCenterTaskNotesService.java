package com.ymd.cloud.authorizeCenter.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTaskNotes;
import com.ymd.cloud.authorizeCenter.model.entity.TaskParam;
import com.ymd.cloud.common.utils.PageRequest;

import java.util.List;

public interface AuthorizeCenterTaskNotesService extends IService<AuthorizeCenterTaskNotes> {
    PageInfo<AuthorizeCenterTaskNotes> listPage(PageRequest request, TaskParam queryParam);
    int addTaskNotes(AuthorizeCenterTaskNotes authorizeCenterTaskNotes);
    List<AuthorizeCenterTaskNotes> selectTaskNotesPageByTaskNo(AuthorizeCenterTaskNotes authorizeCenterTaskNotes);
    Long selectTaskNotesCountByTaskNo(String taskNo);
    int delTaskNotesBefore1Month();
    int deleteTaskNotesById(Long id);
}
