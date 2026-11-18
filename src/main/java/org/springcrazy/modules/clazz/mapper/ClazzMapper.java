package org.springcrazy.modules.clazz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzb.erp.annotation.Log;
import com.hzb.erp.annotation.PreventMultiSubmit;
import com.hzb.erp.annotation.DataScoped;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.clazz.entity.Clazz;
import org.springcrazy.modules.clazz.pojo.ClassParamDTO;
import org.springcrazy.modules.clazz.pojo.ClassVO;

import java.util.List;

/**
 * <p>
 * 班级 Mapper 接口
 * </p>
 *
 * @author 541720500@qq.com
 */
@Mapper
public interface ClazzMapper extends BaseMapper<Clazz> {
    @DataScoped
    IPage<ClassVO> getList(Page<?> page, ClassParamDTO param);

    @DataScoped
    List<ClassVO> getList(@Param("param") ClassParamDTO param);

    ClassVO getInfo(Long id);

    Clazz getClassByStudentIdAndCourseId(Long studentId, Long courseId);
}
