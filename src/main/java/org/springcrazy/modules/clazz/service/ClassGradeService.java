package org.springcrazy.modules.clazz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.clazz.entity.ClassGrade;
import org.springcrazy.modules.clazz.pojo.ClassGradeParamDTO;

import java.util.List;

/**
* @author c
* @description 针对表【class_grade(年级表)】的数据库操作Service
* @createDate 2023-02-07 17:15:48
*/
public interface ClassGradeService extends IService<ClassGrade> {

    IPage<ClassGrade> getList(ClassGradeParamDTO param);

    List<ClassGrade> getAll(ClassGradeParamDTO param);

    Boolean saveOrUpdateByDTO(ClassGrade dto);

    /**
    * 升级逻辑，一年级变二年级
    * */
    boolean upgrade(List<Long> ids, Long currentUserId);
}
