package org.springcrazy.modules.clazz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzb.erp.annotation.DataScoped;
import com.hzb.erp.annotation.Log;
import com.hzb.erp.annotation.PreventMultiSubmit;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.clazz.entity.Grade;
import org.springcrazy.modules.clazz.pojo.GradeParamDTO;
import org.springcrazy.modules.clazz.pojo.GradeVO;

import java.util.List;

/**
 * <p>
 * 成绩单 Mapper 接口
 * </p>
 *
 * @author Ryan
 */
@Mapper
public interface GradeMapper extends BaseMapper<Grade> {
    @DataScoped
    IPage<GradeVO> getList(Page<?> page, GradeParamDTO param);

    @DataScoped
    List<GradeVO> getList(@Param("param") GradeParamDTO param);
}
