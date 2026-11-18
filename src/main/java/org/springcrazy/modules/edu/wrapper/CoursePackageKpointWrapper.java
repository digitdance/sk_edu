
package org.springcrazy.modules.edu.wrapper;

import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.entity.CoursePackage;
import org.springcrazy.modules.edu.entity.CoursePackageKpoint;
import org.springcrazy.modules.edu.service.ICoursePackageKpointService;
import org.springcrazy.modules.edu.service.ICoursePackageService;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.edu.vo.CoursePackageKpointVO;
import org.springcrazy.modules.edu.vo.CoursePackageVO;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author TongZhou
 */
public class CoursePackageKpointWrapper extends BaseEntityWrapper<CoursePackageKpoint, CoursePackageKpointVO> {

	private static ICoursePackageKpointService coursePackageService;
	private static ICourseService courseService;

	static {
		coursePackageService = SpringUtil.getBean(ICoursePackageKpointService.class);
		courseService = SpringUtil.getBean(ICourseService.class);
	}

	public static CoursePackageKpointWrapper build() {
		return new CoursePackageKpointWrapper();
	}

	@Override
	public CoursePackageKpointVO entityVO(CoursePackageKpoint course) {
		CoursePackageKpointVO courseVO = BeanUtil.copy(course, CoursePackageKpointVO.class);

		if(course.getCourseId()!=null){
		 	Course course1 = 	courseService.getById(course.getCourseId());
			courseVO.setLogo(course1.getLogo());
			courseVO.setCourseName(course1.getCourseName());
		}
		return courseVO;
	}



}
