package org.springcrazy.modules.readtask.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzb.erp.annotation.DataScoped;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.lesson.entity.Homework;
import org.springcrazy.modules.lesson.pojo.HomeworkParamDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkVO;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springcrazy.modules.readtask.pojo.ReadTaskParamDTO;
import org.springcrazy.modules.readtask.pojo.ReadTaskVO;

import java.util.List;

/**
 * <p>
 * 作业 Mapper 接口
 * </p>
 *
 * @author Ryan
 */
@Mapper
public interface ReadTaskMapper extends BaseMapper<ReadTask> {
    @DataScoped
    IPage<ReadTaskVO> getList(Page<?> page, ReadTaskParamDTO param);

    @DataScoped
    List<ReadTaskVO> getList(@Param("param") ReadTaskParamDTO param);

    ReadTaskVO getInfo(Long id, Long studentId);

    @DataScoped
    List<ReadTask> selectTaskByDay(ReadTaskParamDTO param);

}
