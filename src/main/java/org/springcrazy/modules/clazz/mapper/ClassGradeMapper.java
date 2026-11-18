package org.springcrazy.modules.clazz.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.clazz.entity.ClassGrade;
import org.springcrazy.modules.clazz.pojo.ClassGradeParamDTO;

import java.util.List;

/**
* @author c
* @description 针对表【class_grade(年级表)】的数据库操作Mapper
* @createDate 2023-02-07 17:15:48
* @Entity org.springcrazy.modules.clazz.entity.ClassGrade
*/
public interface ClassGradeMapper extends BaseMapper<ClassGrade> {

    IPage<ClassGrade> getList(Page<?> page, ClassGradeParamDTO param);

    List<ClassGrade> getList(@Param("param") ClassGradeParamDTO param);
}




