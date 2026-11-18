package org.springcrazy.modules.lesson.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.modules.lesson.entity.HomeworkRecord;
import org.springcrazy.modules.lesson.mapper.HomeworkRecordMapper;
import org.springcrazy.modules.lesson.pojo.HomeworkParamDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordCommentSaveDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordSaveDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordVO;
import org.springcrazy.modules.lesson.service.HomeworkRecordService;
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
public class HomeworkRecordServiceImpl extends ServiceImpl<HomeworkRecordMapper, HomeworkRecord> implements HomeworkRecordService {

    @Override
    public boolean comment(HomeworkRecordCommentSaveDTO dto, Long teacherId) {
        HomeworkRecord item = getById(dto.getId());
        if (item == null || !item.getHomeworkId().equals(dto.getHomeworkId())) {
            return false;
        }
        item.setScore(dto.getScore());
        item.setComment(dto.getComment());
        item.setCommentTime(LocalDateTime.now());
        item.setCommentTeacher(teacherId);
        return this.updateById(item);
    }

    @Override
    public boolean saveRecord(HomeworkRecordSaveDTO dto) {
        String content = dto.getContent();
        if (dto.getImgList() != null && !dto.getImgList().isEmpty()) {
            content += "\n" + String.join("\n", dto.getImgList());
        }
        QueryWrapper<HomeworkRecord> existingQuery = new QueryWrapper<>();
        existingQuery.eq("homework_id", dto.getHomeworkId()).eq("student_id", dto.getStudentId());
        HomeworkRecord item = getOne(existingQuery);
        if (item == null) item = new HomeworkRecord();
        item.setHomeworkId(dto.getHomeworkId());
        item.setStudentId(dto.getStudentId());
        item.setContent(content);
        item.setAddTime(LocalDateTime.now());
        item.setScore(null);
        item.setComment(null);
        item.setCommentTime(null);
        item.setCommentTeacher(null);
        return this.saveOrUpdate(item);
    }

    @Override
    public IPage<HomeworkRecordVO> getList(HomeworkParamDTO param) {
        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
    }

    @Override
    public boolean deleteByStudentId(Long id, Long studentId) {
        QueryWrapper<HomeworkRecord> qw = new QueryWrapper<>();
        qw.eq("id", id).eq("student_id", studentId);
        return remove(qw);
    }


}
