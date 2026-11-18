
package org.springcrazy.modules.edu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.edu.entity.CoursePackage;
import org.springcrazy.modules.edu.entity.CoursePackageExam;
import org.springcrazy.modules.edu.vo.CoursePackageExamVO;
import org.springcrazy.modules.edu.vo.CoursePackageVO;

import java.util.List;

/**
 *  服务类
 *
 * @author TongZhou
 * @since 2020-05-05
 */
public interface ICoursePackageExamService extends IService<CoursePackageExam> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param coursePackage
	 * @return
	 */
	IPage<CoursePackageExamVO> selectCoursePackageExamPage(IPage<CoursePackageExamVO> page, CoursePackageExamVO coursePackageExam);


	List<CoursePackageExamVO> selectCoursePackageExamList(CoursePackageExamVO coursePackageExam);

	List<CoursePackageExamVO> selectClazzCoursePackageExamList(CoursePackageExamVO coursePackageExam);
}
