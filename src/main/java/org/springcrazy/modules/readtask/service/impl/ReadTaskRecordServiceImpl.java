package org.springcrazy.modules.readtask.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.core.mp.base.BaseServiceImpl;
import org.springcrazy.modules.lesson.entity.HomeworkRecord;
import org.springcrazy.modules.lesson.mapper.HomeworkRecordMapper;
import org.springcrazy.modules.lesson.pojo.HomeworkParamDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordCommentSaveDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordSaveDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordVO;
import org.springcrazy.modules.readtask.entity.ReadTaskRecord;
import org.springcrazy.modules.readtask.mapper.ReadTaskRecordMapper;
import org.springcrazy.modules.readtask.pojo.ReadTaskParamDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskRecordCommentSaveDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskRecordSaveDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskRecordVO;
import org.springcrazy.modules.readtask.service.ReadTaskRecordService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * <p>
 * 作业提交记录 服务实现类
 * </p>
 *
 * @author Ryan
 */
@Service
public class ReadTaskRecordServiceImpl extends BaseServiceImpl<ReadTaskRecordMapper, ReadTaskRecord> implements ReadTaskRecordService {

    @Override
    public boolean comment(ReadTaskRecordCommentSaveDTO dto, Long teacherId) {
        ReadTaskRecord item = getById(dto.getId());
        item.setScore(dto.getScore());
        item.setComment(dto.getComment());
        item.setCommentTime(LocalDateTime.now());
        item.setCommentTeacher(teacherId);
        return this.updateById(item);
    }

    @Override
    public boolean saveRecord(ReadTaskRecordSaveDTO dto) {
        String content = "";
        content += "<p>" + dto.getContent() + "</p>";
        if (dto.getImgList() != null && dto.getImgList().size() > 0) {
            for (String url : dto.getImgList()) {
                content += "<p><img src='" + url + "' style='width:100%'></p>";
            }
        }
        ReadTaskRecord item = new ReadTaskRecord();
        item.setTaskId(dto.getTaskId());
       // item.setStudentId(dto.getStudentId());
        item.setContent(content);
        item.setAddTime(LocalDateTime.now());
        return this.save(item);
    }

    @Override
    public IPage<ReadTaskRecordVO> getList(ReadTaskParamDTO param) {
        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
    }

    @Override
    public boolean deleteByStudentId(Long id, Long studentId) {
        QueryWrapper<ReadTaskRecord> qw = new QueryWrapper<>();
        qw.eq("id", id).eq("student_id", studentId);
        return remove(qw);
    }


}
