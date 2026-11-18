package org.springcrazy.modules.clazz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.modules.clazz.entity.ClassGrade;
import org.springcrazy.modules.clazz.mapper.ClassGradeMapper;
import org.springcrazy.modules.clazz.pojo.ClassGradeParamDTO;
import org.springcrazy.modules.clazz.service.ClassGradeService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author c
* @description 针对表【class_grade(年级表)】的数据库操作Service实现
* @createDate 2023-02-07 17:15:48
*/
@Service
public class ClassGradeServiceImpl extends ServiceImpl<ClassGradeMapper, ClassGrade> implements ClassGradeService {

    @Override
    public IPage<ClassGrade> getList(ClassGradeParamDTO param) {
        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
    }

    @Override
    public List<ClassGrade> getAll(ClassGradeParamDTO param) {
        return this.baseMapper.getList(param);
    }

    @Override
    public Boolean saveOrUpdateByDTO(ClassGrade dto) {
        ClassGrade item = new ClassGrade();
        BeanUtils.copyProperties(dto, item);
        return this.saveOrUpdate(item);
    }

    @Override
    public boolean upgrade(List<Long> ids, Long currentUserId) {
        List<ClassGrade> classGrades = this.listByIds(ids);
        for (ClassGrade grade: classGrades) {
            grade.setName(upgradeGradeNum(grade.getName()));
        }
        this.updateBatchById(classGrades);
        return true;
    }

    private String upgradeGradeNum(String gradeName)
    {
        gradeName = gradeName.replace("9","10");
        gradeName = gradeName.replace("8","9");
        gradeName = gradeName.replace("7","8");
        gradeName = gradeName.replace("6","7");
        gradeName = gradeName.replace("5","6");
        gradeName = gradeName.replace("4","5");
        gradeName = gradeName.replace("3","4");
        gradeName = gradeName.replace("2","3");
        gradeName = gradeName.replace("1","2");
        gradeName = gradeName.replace("九","十");
        gradeName = gradeName.replace("八","九");
        gradeName = gradeName.replace("七","八");
        gradeName = gradeName.replace("六","七");
        gradeName = gradeName.replace("五","六");
        gradeName = gradeName.replace("四","五");
        gradeName = gradeName.replace("三","四");
        gradeName = gradeName.replace("二","三");
        gradeName = gradeName.replace("一","二");
        return gradeName;
    }
}




