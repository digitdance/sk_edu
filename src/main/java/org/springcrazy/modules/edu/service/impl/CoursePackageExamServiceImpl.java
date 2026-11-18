
package org.springcrazy.modules.edu.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.modules.edu.entity.CoursePackage;
import org.springcrazy.modules.edu.entity.CoursePackageExam;
import org.springcrazy.modules.edu.mapper.CoursePackageExamMapper;
import org.springcrazy.modules.edu.mapper.CoursePackageMapper;
import org.springcrazy.modules.edu.service.ICoursePackageExamService;
import org.springcrazy.modules.edu.service.ICoursePackageService;
import org.springcrazy.modules.edu.vo.CoursePackageExamVO;
import org.springcrazy.modules.edu.vo.CoursePackageVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *  服务实现类
 *
 * @author TongZhou
 * @since 2020-05-05
 */
@Service
public class CoursePackageExamServiceImpl extends ServiceImpl<CoursePackageExamMapper, CoursePackageExam> implements ICoursePackageExamService {

	@Override
	public IPage<CoursePackageExamVO> selectCoursePackageExamPage(IPage<CoursePackageExamVO> page, CoursePackageExamVO coursePackageExam) {
		return page.setRecords(baseMapper.selectCoursePackageExamPage(page, coursePackageExam));
	}

//	@Override
//	public IPage<CoursePackageExamVO> selectCoursePackageExamPage(IPage<CoursePackageExamVO> page, CoursePackageExamVO coursePackageExam) {
//		return page.setRecords(baseMapper.selectCoursePackageExamPage(page, coursePackageExam));
//	}

	@Override
	public List<CoursePackageExamVO> selectCoursePackageExamList(CoursePackageExamVO coursePackageExam) {
		return baseMapper.selectCoursePackageExamList(coursePackageExam);
	}

	@Override
	public List<CoursePackageExamVO> selectClazzCoursePackageExamList(CoursePackageExamVO coursePackageExam) {
		return baseMapper.selectClazzExamPackageList(coursePackageExam);
	}

}
