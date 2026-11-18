
package org.springcrazy.modules.edu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.edu.entity.CoursePackage;
import org.springcrazy.modules.edu.entity.CoursePackageKpoint;
import org.springcrazy.modules.edu.vo.CoursePackageKpointVO;
import org.springcrazy.modules.edu.vo.CoursePackageVO;

import java.util.List;

/**
 *  服务类
 *
 * @author TongZhou
 * @since 2020-05-05
 */
public interface ICoursePackageKpointService extends IService<CoursePackageKpoint> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param coursePackage
	 * @return
	 */
	IPage<CoursePackageKpointVO> selectCoursePackageKpointPage(IPage<CoursePackageKpointVO> page, CoursePackageKpointVO coursePackage);


	List<CoursePackageKpointVO> selectCoursePackageKpointList(CoursePackageKpointVO coursePackage);
}
