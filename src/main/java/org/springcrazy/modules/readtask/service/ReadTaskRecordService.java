package org.springcrazy.modules.readtask.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.readtask.entity.ReadTaskRecord;
import org.springcrazy.modules.readtask.pojo.ReadTaskParamDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskRecordCommentSaveDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskRecordSaveDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskRecordVO;

/**
 * <p>
 * 作业提交记录 服务类
 * </p>
 *
 * @author Ryan
 */
public interface ReadTaskRecordService extends IService<ReadTaskRecord> {

    boolean comment(ReadTaskRecordCommentSaveDTO dto, Long teacherId);

    IPage<ReadTaskRecordVO> getList(ReadTaskParamDTO param);

    boolean deleteByStudentId(Long id, Long studentId);

    boolean saveRecord(ReadTaskRecordSaveDTO dto);
}
