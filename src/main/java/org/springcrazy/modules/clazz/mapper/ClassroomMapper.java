package org.springcrazy.modules.clazz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzb.erp.annotation.Log;
import com.hzb.erp.annotation.PreventMultiSubmit;
import com.hzb.erp.annotation.DataScoped;

import org.apache.ibatis.annotations.Mapper;
import org.springcrazy.modules.clazz.entity.Classroom;
import org.springcrazy.modules.clazz.pojo.ClassroomParamDTO;
import org.springcrazy.modules.clazz.pojo.ClassroomVO;

/**
 * <p>
 * 教室 Mapper 接口
 * </p>
 *
 * @author Ryan
 */
@Mapper
public interface ClassroomMapper extends BaseMapper<Classroom> {
    @DataScoped
    IPage<ClassroomVO> getList(Page<?> objectPage, ClassroomParamDTO param);
}
