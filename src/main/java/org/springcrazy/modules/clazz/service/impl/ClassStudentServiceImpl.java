//package org.springcrazy.modules.clazz.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import com.hzb.erp.exception.BizException;
//import org.apache.commons.lang3.BooleanUtils;
//import org.springcrazy.modules.clazz.entity.ClassStudent;
//import org.springcrazy.modules.clazz.entity.Clazz;
//import org.springcrazy.modules.clazz.mapper.ClassStudentMapper;
//import org.springcrazy.modules.clazz.mapper.ClazzMapper;
//import org.springcrazy.modules.clazz.service.ClassStudentService;
//import org.springcrazy.modules.edu.mapper.CourseMapper;
//import org.springcrazy.modules.user.mapper.StudentMapper;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import javax.annotation.Resource;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Objects;
//import java.util.stream.Collectors;
//
///**
// * <p>
// * 班级学员表 服务实现类
// * </p>
// *
// * @author 541720500@qq.com
// */
//@Service
//public class ClassStudentServiceImpl extends ServiceImpl<ClassStudentMapper, ClassStudent> implements ClassStudentService {
//
//    @Autowired
//    private StudentService studentService;
//    @Resource
//    private ClazzMapper clazzMapper;
//    @Autowired
//    private LessonStudentService lessonStudentService;
//    @Autowired
//    private LessonStudentMapper lessonStudentMapper;
//    @Resource
//    private StudentMapper studentMapper;
//    @Resource
//    private CourseMapper courseMapper;
//
//    /**
//     * 添加学员
//     */
//    @Override
//    public Boolean addClassStudent(Long classId, Long studentId, Long staffId) {
//        Clazz clazz = clazzMapper.selectById(classId);
//        QueryWrapper<ClassStudent> queryWrapper = buildQW(classId, studentId);
//        ClassStudent item = this.getOne(queryWrapper);
//        Boolean res;
//        if (item != null) {
//            if (!item.getDeleted()) {
//                throw new BizException("请勿重复添加");
//            }
//            item.setDeleted(false);
//            item.setAddTime(LocalDateTime.now());
//            item.setCreator(staffId);
//            item.setConsumeCourseId(clazz.getCourseId());
//            res = this.updateById(item);
//        } else {
//            res = this.save(buildOne(clazz, studentId));
//        }
//        if (res) {
//
//            // 班级的课程如果是预约模式，那么就不自动加入学生到课表签到记录里。
//            this.addStudentToLesson(clazz, studentMapper.selectById(studentId));
//
//        }
//        return res;
//    }
//
//    private void addStudentToLesson(Clazz clazz, Student student) {
//        // 班级的课程如果是预约模式，那么就不自动加入学生到课表签到记录里。
//        // 否则自动添加学生和课时记录的关联数据
//        boolean addStudentLesson = true;
//        if(!Objects.isNull(clazz.getCourseId())) {
//            Course course = courseMapper.selectById(clazz.getCourseId());
//            if(BooleanUtils.isTrue(course.getBookable())) {
//                addStudentLesson = false;
//            }
//        }
//        if(addStudentLesson) {
//            lessonStudentService.addRecord(clazz, student);
//        }
//    }
//
//    @Override
//    public void addClassStudents(ClassStudentAddDTO dto, Long staffId) {
//        List<ClassStudent> list = new ArrayList<>();
//        Clazz clazz = clazzMapper.selectById(dto.getClassId());
//        for (Long studentId : dto.getStudentIds()) {
//            QueryWrapper<ClassStudent> queryWrapper = buildQW(dto.getClassId(), studentId);
//            ClassStudent item = this.getOne(queryWrapper);
//            if (item == null) {
//                list.add(buildOne(clazz, studentId));
//            } else {
//                item.setDeleted(false);
//                item.setAddTime(LocalDateTime.now());
//                item.setCreator(staffId);
//                item.setConsumeCourseId(clazz.getCourseId());
//                this.updateById(item);
//            }
//            this.addStudentToLesson(clazz, studentMapper.selectById(studentId));
//        }
//        if (list.size() > 0) {
//            this.saveBatch(list);
//        }
//    }
//
//    @Override
//    public List<Student> getStudentsByClassId(Long classId) {
//
//        QueryWrapper<ClassStudent> qw = new QueryWrapper<>();
//        qw.eq("class_id", classId);
//        qw.eq("deleted", false);
//        List<ClassStudent> csList = list(qw);
//        if (csList == null || csList.size() == 0) {
//            return null;
//        }
//
//        QueryWrapper<Student> studentQw = new QueryWrapper<>();
//        studentQw.in("id", csList.stream().map(ClassStudent::getStudentId).collect(Collectors.toList()));
//        return studentService.list(studentQw);
//    }
//
//    @Override
//    public boolean changeConsumeCourse(Long classStudentId, Long consumeCourseId) {
//        ClassStudent item = getById(classStudentId);
//        item.setConsumeCourseId(consumeCourseId);
//        boolean res = updateById(item);
//
//        // 对未开始的课程的上课记录里的消课是否使用该消课项
//        lessonStudentMapper.updateCourseIdByClassStudentForFuture(item);
//
//        return res;
//    }
//
//    private ClassStudent buildOne(Clazz clazz, Long studentId) {
//        ClassStudent cs = new ClassStudent();
//        cs.setClassId(clazz.getId());
//        cs.setStudentId(studentId);
//        cs.setConsumeCourseId(clazz.getCourseId());
//        return cs;
//    }
//
////    @Override
////    public IPage<ClassStudentVO> getList(ClassStudentParamDTO param) {
////        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
////    }
////
////    @Override
////    public List<ClassStudentVO> getAll(ClassStudentParamDTO param) {
////        return this.baseMapper.getList(param);
////    }
//
//    @Override
//    public List<ClassStudentSignVO> signRecord(ClassStudentParamDTO param) {
//        return this.baseMapper.signRecord(param);
//    }
//
//    /**
//     * 移除学员
//     */
//    @Override
//    public Boolean deleteClassStudent(Long classId, Long studentId) {
//        QueryWrapper<ClassStudent> queryWrapper = buildQW(classId, studentId);
//        ClassStudent cs = this.getOne(queryWrapper);
//        cs.setDeleted(true);
//        Boolean res = this.updateById(cs);
//        if (res) {
//            lessonStudentMapper.removeByStudentIdAndClassId(cs.getStudentId(), cs.getClassId());
//        }
//        return res;
//    }
//
//    @Override
//    public int deleteClassStudent(List<Long> studentIds) {
//        QueryWrapper<ClassStudent> queryWrapper = new QueryWrapper<>();
//        queryWrapper.in("student_id", studentIds);
//        return this.baseMapper.delete(queryWrapper);
//    }
//
//    /**
//     * 批量移除学员
//     */
//    @Override
//    public Boolean deleteClassStudentByIds(List<Long> ids) {
//        List<ClassStudent> list = this.listByIds(ids);
//        for (ClassStudent cs : list) {
//            cs.setDeleted(true);
//        }
//        Boolean res = this.updateBatchById(list);
//        if (res) {
//            for (ClassStudent cs : list) {
//                lessonStudentMapper.removeByStudentIdAndClassId(cs.getStudentId(), cs.getClassId());
//            }
//        }
//        return res;
//    }
//
//    /**
//     * 批量调班
//     * 需要清除一下学生课时关联数据，并重新关联新班级的学生课时关联数据。
//     */
//    @Override
//    public Boolean changeClassByIds(List<Long> ids, Long newClassId, Long staffId) {
//        Clazz clazz = clazzMapper.selectById(newClassId);
//        List<ClassStudent> list = this.listByIds(ids);
//        for (ClassStudent cs : list) {
//            cs.setClassId(newClassId);
//            lessonStudentMapper.removeByStudentIdAndClassId(cs.getStudentId(), cs.getClassId());
//        }
//        this.updateBatchById(list);
//
//        for (ClassStudent cs : list) {
//            this.addStudentToLesson(clazz, studentMapper.selectById(cs.getStudentId()));
//        }
//
//        return true;
//    }
//
//    private QueryWrapper<ClassStudent> buildQW(Long classId, Long studentId) {
//        QueryWrapper<ClassStudent> queryWrapper = new QueryWrapper<>();
//        queryWrapper.eq("class_id", classId).eq("student_id", studentId);;
//        return queryWrapper;
//    }
//
//    @Override
//    public boolean addStudentsOneKey(Long classId, Long currentUserId) {
//        Clazz clazz = clazzMapper.selectById(classId);
//        if(clazz == null) {
//            throw new BizException("未知班级");
//        }else if(clazz.getCourseId() == null) {
//            throw new BizException("本班级未设置默认课程，无法继续添加");
//        }
//        StudentParamDTO param = new StudentParamDTO();
//        param.setCourseId(clazz.getCourseId());
//        List<StudentVO> studentList = studentMapper.getList(param);
//        if(CollectionUtils.isEmpty(studentList)) {
//            throw new BizException("没有复核条件的学员");
//        }
//        List<Long> studentIds = studentList.stream().map(StudentVO::getId).collect(Collectors.toList());
//        ClassStudentAddDTO dto = new ClassStudentAddDTO();
//        dto.setClassId(classId);
//        dto.setStudentIds(studentIds);
//        addClassStudents(dto, currentUserId);
//        return true;
//    }
//}
