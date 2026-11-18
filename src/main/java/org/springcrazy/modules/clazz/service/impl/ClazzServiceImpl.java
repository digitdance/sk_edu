package org.springcrazy.modules.clazz.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hzb.erp.common.service.SettingService;
import org.springcrazy.common.enums.SettingNameEnum;
import org.springcrazy.common.enums.StudentStageEnum;
import org.springcrazy.modules.clazz.entity.Clazz;
import org.springcrazy.modules.clazz.mapper.ClazzMapper;
import org.springcrazy.modules.clazz.pojo.ClassParamDTO;
import org.springcrazy.modules.clazz.pojo.ClassSaveDTO;
import org.springcrazy.modules.clazz.pojo.ClassVO;
import org.springcrazy.modules.clazz.service.ClassStudentService;
import org.springcrazy.modules.clazz.service.ClazzService;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.service.IStudentService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 班级 服务实现类
 * </p>
 *
 * @author 541720500@qq.com
 */
@Service
public class ClazzServiceImpl extends ServiceImpl<ClazzMapper, Clazz> implements ClazzService {


    private IStudentService studentService;

    private ClassStudentService classStudentService;
//    @Autowired
//    private SettingService settingService;

    @Override
    public IPage<ClassVO> getList(ClassParamDTO param) {
        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
    }

    @Override
    public List<ClassVO> getAll(ClassParamDTO param) {
        return this.baseMapper.getList(param);
    }

    @Override
    public ClassVO getInfo(Long id) {
        return this.baseMapper.getInfo(id);
    }

    @Override
    public Boolean saveOrUpdateByDTO(ClassSaveDTO classSaveDTO) {
        Clazz item = new Clazz();
        BeanUtils.copyProperties(classSaveDTO, item);
        //item.setSchoolId(StaffAuthService.getLoginStaffOrgInfo().getComId());
        return this.saveOrUpdate(item);
    }

    @Override
    public Boolean over(List<Long> ids, Long operatorId) {
        List<Clazz> list = this.listByIds(ids);
        for (Clazz item : list) {
            item.setBeOver(true);
            item.setOverOperator(operatorId);
            item.setOverTime(LocalDateTime.now());
            Boolean bl = true;
            if (bl != null && bl) {
                // 学生结业
                List<Student> studentList = classStudentService.getStudentsByClassId(item.getId());
                if (studentList != null && studentList.size() > 0) {
                    studentService.changeStage(studentList.stream().map(i->Long.parseLong(i.getId()+"")).collect(Collectors.toList()), StudentStageEnum.GRADUATION, false);
                }
            }
        }
        return this.updateBatchById(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Clazz autoCreateOne2One(Student student, Course course, Long teacherId) {

        // 课程里已存在的班级里，包含本学生的话，就不要再创建了
        Clazz findClazz = this.baseMapper.getClassByStudentIdAndCourseId(Long.parseLong(student.getId()+""), Long.parseLong(course.getId()+""));
        if(findClazz!=null) {
            return findClazz;
        }

        // 判断是否1v1并自动生成班级
        Clazz clazz = new Clazz();
        clazz.setName(student.getRealName() + "1V1");
        clazz.setCourseId(Long.parseLong(course.getId()+""));
        clazz.setTeacherId(teacherId);
        clazz.setStartDate(LocalDate.now());
        //clazz.setEndDate(LocalDate.now().plusMonths(course.getExpireMonths()));
        //clazz.setPlannedLessonCount(course.getLessonCount());
        clazz.setPlannedStudentCount(1);
        this.save(clazz);

        classStudentService.addClassStudent(clazz.getId(), Long.parseLong(student.getId()+""), teacherId);

        return clazz;
    }
}
