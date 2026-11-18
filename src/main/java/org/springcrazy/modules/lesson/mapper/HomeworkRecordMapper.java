package org.springcrazy.modules.lesson.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springcrazy.modules.lesson.entity.HomeworkRecord;
import org.springcrazy.modules.lesson.pojo.HomeworkParamDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordVO;

/**
 * <p>
 * 作业提交记录 Mapper 接口
 * </p>
 *
 * @author Ryan
 */
@Mapper
public interface HomeworkRecordMapper extends BaseMapper<HomeworkRecord> {

    IPage<HomeworkRecordVO> getList(Page<Object> page, HomeworkParamDTO param);
}
