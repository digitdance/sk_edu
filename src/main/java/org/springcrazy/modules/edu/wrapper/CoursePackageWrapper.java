
package org.springcrazy.modules.edu.wrapper;

import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.entity.CoursePackage;
import org.springcrazy.modules.edu.service.ICoursePackageService;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.edu.vo.CoursePackageVO;
import org.springcrazy.modules.edu.vo.CourseVO;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author TongZhou
 */
public class CoursePackageWrapper extends BaseEntityWrapper<CoursePackage, CoursePackageVO> {

	private static ICoursePackageService coursePackageService;
	private static ICourseService courseService;

	static {
		coursePackageService = SpringUtil.getBean(ICoursePackageService.class);
		courseService = SpringUtil.getBean(ICourseService.class);
	}

	public static CoursePackageWrapper build() {
		return new CoursePackageWrapper();
	}

	@Override
	public CoursePackageVO entityVO(CoursePackage course) {
		CoursePackageVO courseVO = BeanUtil.copy(course, CoursePackageVO.class);

		if(course.getCourseId()!=null){
		 	Course course1 = 	courseService.getById(course.getCourseId());
			courseVO.setLogo(course1.getLogo());
			courseVO.setCourseName(course1.getCourseName());
		}
		return courseVO;
	}



}
