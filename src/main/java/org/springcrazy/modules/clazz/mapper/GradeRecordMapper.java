package org.springcrazy.modules.clazz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.springcrazy.modules.clazz.entity.GradeRecord;
import org.springcrazy.modules.clazz.pojo.GradeParamDTO;
import org.springcrazy.modules.clazz.pojo.GradeRecordVO;

/**
 * <p>
 * 成绩单学生分数 Mapper 接口
 * </p>
 *
 * @author Ryan
 */
@Mapper
public interface GradeRecordMapper extends BaseMapper<GradeRecord> {
    IPage<GradeRecordVO> getList(Page<?> page, GradeParamDTO param);
}
