
package org.springcrazy.modules.edu.wrapper;

import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.entity.CoursePackage;
import org.springcrazy.modules.edu.entity.CoursePackageExam;
import org.springcrazy.modules.edu.service.ICoursePackageExamService;
import org.springcrazy.modules.edu.service.ICoursePackageService;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.edu.vo.CoursePackageExamVO;
import org.springcrazy.modules.edu.vo.CoursePackageVO;
import org.springcrazy.modules.exam.entity.Exampaper;
import org.springcrazy.modules.exam.service.IExampaperService;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author TongZhou
 */
public class CoursePackageExamWrapper extends BaseEntityWrapper<CoursePackageExam, CoursePackageExamVO> {

	private static ICoursePackageExamService coursePackageService;
	private static IExampaperService exampaperService;

	static {
		coursePackageService = SpringUtil.getBean(ICoursePackageExamService.class);
		exampaperService = SpringUtil.getBean(IExampaperService.class);
	}

	public static CoursePackageExamWrapper build() {
		return new CoursePackageExamWrapper();
	}

	@Override
	public CoursePackageExamVO entityVO(CoursePackageExam course) {
		CoursePackageExamVO courseVO = BeanUtil.copy(course, CoursePackageExamVO.class);

		if(course.getExampaperId()!=null){
		 	Exampaper course1 = 	exampaperService.getById(course.getExampaperId());
			courseVO.setExamName(course1.getName());
		}
		return courseVO;
	}



}
