
package org.springcrazy.modules.user.wrapper;

import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.system.service.IDictService;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.entity.Teacher;
import org.springcrazy.modules.user.service.IStudentService;
import org.springcrazy.modules.user.service.ITeacherService;
import org.springcrazy.modules.user.vo.StudentVO;
import org.springcrazy.modules.user.vo.TeacherVO;

import java.util.List;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author TongZhou
 */
public class StudentWrapper extends BaseEntityWrapper<Student, StudentVO> {

	private static IStudentService studentService;

	private static IDictService dictService;

	private static ICourseService courseService;

	static {
		studentService = SpringUtil.getBean(IStudentService.class);
		courseService = SpringUtil.getBean(ICourseService.class);
		dictService = SpringUtil.getBean(IDictService.class);
	}

	public static StudentWrapper build() {
		return new StudentWrapper();
	}

	@Override
	public StudentVO entityVO(Student student) {
		StudentVO studentVO = BeanUtil.copy(student, StudentVO.class);

		if(Func.isNotEmpty(student.getDeptId())){
			List<String> deptName = studentService.getDeptName(student.getDeptId());
			studentVO.setDeptName(Func.join(deptName));
		}
//		if(Func.isNotEmpty(student.getClassCourseId())){
//			Course cls = courseService.getCourseById(student.getClassCourseId());
//			studentVO.setCls(cls);
//		}
		if(Func.isNotBlank(student.getClassId())){
			Course cls = courseService.getById(student.getClassId());
			studentVO.setCls(cls);
			studentVO.setClassName(cls.getCourseName());
		}

		return studentVO;
	}

}
