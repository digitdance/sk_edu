
package org.springcrazy.modules.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.edu.entity.CoursePackage;
import org.springcrazy.modules.edu.entity.CoursePackageExam;
import org.springcrazy.modules.edu.vo.CoursePackageExamVO;
import org.springcrazy.modules.edu.vo.CoursePackageVO;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author TongZhou
 * @since 2020-05-05
 */
public interface CoursePackageExamMapper extends BaseMapper<CoursePackageExam> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param coursePackage
	 * @return
	 */
	List<CoursePackageExamVO> selectCoursePackageExamPage(IPage page, @Param("coursePackageExam") CoursePackageExamVO coursePackage);

	List<CoursePackageExamVO> selectCoursePackageExamList(@Param("coursePackageExam") CoursePackageExamVO coursePackage);


	List<CoursePackageExamVO> selectClazzExamPackageList(@Param("coursePackageExam") CoursePackageExamVO coursePackage);

}
