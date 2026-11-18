package org.springcrazy.modules.readtask.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springcrazy.modules.lesson.entity.HomeworkRecord;
import org.springcrazy.modules.lesson.pojo.HomeworkParamDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordVO;
import org.springcrazy.modules.readtask.entity.ReadTaskRecord;
import org.springcrazy.modules.readtask.pojo.ReadTaskParamDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskRecordVO;

/**
 * <p>
 * 作业提交记录 Mapper 接口
 * </p>
 *
 * @author Ryan
 */
@Mapper
public interface ReadTaskRecordMapper extends BaseMapper<ReadTaskRecord> {

    IPage<ReadTaskRecordVO> getList(Page<Object> page, ReadTaskParamDTO param);
}
