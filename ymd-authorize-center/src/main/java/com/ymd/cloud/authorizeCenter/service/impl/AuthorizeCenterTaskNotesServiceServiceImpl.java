package com.ymd.cloud.authorizeCenter.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.ymd.cloud.authorizeCenter.mapper.AuthorizeCenterTaskNotesMapper;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTask;
import com.ymd.cloud.authorizeCenter.model.AuthorizeCenterTaskNotes;
import com.ymd.cloud.authorizeCenter.model.entity.TaskParam;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskNotesService;
import com.ymd.cloud.authorizeCenter.service.AuthorizeCenterTaskResultService;
import com.ymd.cloud.common.utils.EmptyUtil;
import com.ymd.cloud.common.utils.PageRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AuthorizeCenterTaskNotesServiceServiceImpl extends ServiceImpl<AuthorizeCenterTaskNotesMapper, AuthorizeCenterTaskNotes> implements AuthorizeCenterTaskNotesService {
    @Autowired
    AuthorizeCenterTaskResultService authorizeCenterTaskResultService;

    @Override
    public PageInfo<AuthorizeCenterTaskNotes> listPage(PageRequest request, TaskParam queryParam) {
        PageHelper.startPage(request.getPageNum(), request.getPageSize(), request.getOrderBy());
        List<AuthorizeCenterTaskNotes> list=this.baseMapper.selectListPage(queryParam);
        PageInfo<AuthorizeCenterTaskNotes> pageInfo = new PageInfo<>(list);//进行分页查询
        return pageInfo;
    }

    @Override
    public int addTaskNotes(AuthorizeCenterTaskNotes authorizeCenterTaskNotes) {
        authorizeCenterTaskNotes.setAuthNo(authorizeCenterTaskResultService.generateAuthNo());
        AuthorizeCenterTaskNotes taskNotes = this.baseMapper.selectTaskNotesByAuthNo(authorizeCenterTaskNotes.getAuthNo());
        if (EmptyUtil.isEmpty(taskNotes)) {
            authorizeCenterTaskNotes.setCreateTime(new Date());
            return this.baseMapper.insert(authorizeCenterTaskNotes);
        }else{
            return 0;
        }
    }

    @Override
    public List<AuthorizeCenterTaskNotes> selectTaskNotesPageByTaskNo(AuthorizeCenterTaskNotes authorizeCenterTaskNotes) {
        List<AuthorizeCenterTaskNotes> list=this.baseMapper.selectTaskNotesPageByTaskNo(authorizeCenterTaskNotes);
        return list;
    }

    @Override
    public Long selectTaskNotesCountByTaskNo(String taskNo) {
        return this.baseMapper.selectTaskNotesCountByTaskNo(taskNo);
    }
    @Override
    public int delTaskNotesBefore1Month() {
        return this.baseMapper.delTaskNotesBefore1Month();
    }

    @Override
    public int deleteTaskNotesById(Long id) {
        return this.baseMapper.deleteTaskNotesById(id);
    }
}
