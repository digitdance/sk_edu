
package org.springcrazy.modules.user.wrapper;

import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.system.entity.User;
import org.springcrazy.modules.system.service.IDictService;
import org.springcrazy.modules.system.service.IUserService;
import org.springcrazy.modules.system.vo.UserVO;
import org.springcrazy.modules.user.entity.Teacher;
import org.springcrazy.modules.user.service.ITeacherService;
import org.springcrazy.modules.user.vo.TeacherVO;

import java.util.List;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author TongZhou
 */
public class TeacherWrapper extends BaseEntityWrapper<Teacher, TeacherVO> {

	private static ITeacherService teacherService;

	private static IDictService dictService;

	static {
		teacherService = SpringUtil.getBean(ITeacherService.class);
		dictService = SpringUtil.getBean(IDictService.class);
	}

	public static TeacherWrapper build() {
		return new TeacherWrapper();
	}

	@Override
	public TeacherVO entityVO(Teacher teacher) {
		TeacherVO teacherVO = BeanUtil.copy(teacher, TeacherVO.class);

		if(Func.isNotEmpty(teacher.getDeptId())){
			List<String> deptName = teacherService.getDeptName(teacher.getDeptId());
			teacherVO.setDeptName(Func.join(deptName));
		}
		return teacherVO;
	}

}
