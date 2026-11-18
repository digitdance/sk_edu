package org.springcrazy.modules.lesson.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.lesson.entity.HomeworkRecord;
import org.springcrazy.modules.lesson.pojo.HomeworkParamDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordCommentSaveDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordSaveDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordVO;

/**
 * <p>
 * 作业提交记录 服务类
 * </p>
 *
 * @author Ryan
 */
public interface HomeworkRecordService extends IService<HomeworkRecord> {

    boolean comment(HomeworkRecordCommentSaveDTO dto, Long teacherId);

    IPage<HomeworkRecordVO> getList(HomeworkParamDTO param);

    boolean deleteByStudentId(Long id, Long studentId);

    boolean saveRecord(HomeworkRecordSaveDTO dto);
}
