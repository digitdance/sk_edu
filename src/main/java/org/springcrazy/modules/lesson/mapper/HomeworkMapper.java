package org.springcrazy.modules.lesson.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.hzb.erp.annotation.DataScoped;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.lesson.entity.Homework;
import org.springcrazy.modules.lesson.pojo.HomeworkParamDTO;
import org.springcrazy.modules.lesson.pojo.HomeworkVO;

import java.util.List;

/**
 * <p>
 * 作业 Mapper 接口
 * </p>
 *
 * @author Ryan
 */
@Mapper
public interface HomeworkMapper extends BaseMapper<Homework> {
    @DataScoped
    IPage<HomeworkVO> getList(Page<?> page, HomeworkParamDTO param);

    @DataScoped
    List<HomeworkVO> getList(@Param("param") HomeworkParamDTO param);

    HomeworkVO getInfo(Long id, Long studentId);
}
