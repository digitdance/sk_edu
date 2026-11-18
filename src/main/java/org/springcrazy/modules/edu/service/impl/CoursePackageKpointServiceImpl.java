
package org.springcrazy.modules.edu.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.modules.edu.entity.CoursePackageKpoint;
import org.springcrazy.modules.edu.mapper.CoursePackageKpointMapper;
import org.springcrazy.modules.edu.service.ICoursePackageKpointService;
import org.springcrazy.modules.edu.vo.CoursePackageKpointVO;
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
public class CoursePackageKpointServiceImpl extends ServiceImpl<CoursePackageKpointMapper, CoursePackageKpoint> implements ICoursePackageKpointService {

	@Override
	public IPage<CoursePackageKpointVO> selectCoursePackageKpointPage(IPage<CoursePackageKpointVO> page, CoursePackageKpointVO coursePackageKpoint) {
		return page.setRecords(baseMapper.selectCoursePackageKpointPage(page, coursePackageKpoint));
	}

	@Override
	public List<CoursePackageKpointVO> selectCoursePackageKpointList(CoursePackageKpointVO coursePackageKpoint) {
		return baseMapper.selectCoursePackageKpointList(coursePackageKpoint);
	}
//	@Override
//	public IPage<CoursePackageVO> selectCoursePackageKpointPage(IPage<CoursePackageKpointVO> page, CoursePackageKpointVO coursePackage) {
//		return null;
//	}
//
//	@Override
//	public List<CoursePackageVO> selectCoursePackageKpointList(CoursePackageKpointVO coursePackage) {
//		return null;
//	}
}
