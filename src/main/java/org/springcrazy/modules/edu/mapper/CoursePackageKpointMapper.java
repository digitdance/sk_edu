
package org.springcrazy.modules.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.edu.entity.CoursePackage;
import org.springcrazy.modules.edu.entity.CoursePackageKpoint;
import org.springcrazy.modules.edu.vo.CoursePackageKpointVO;
import org.springcrazy.modules.edu.vo.CoursePackageVO;

import java.util.List;

/**
 *  Mapper 接口
 *
 * @author TongZhou
 * @since 2020-05-05
 */
public interface CoursePackageKpointMapper extends BaseMapper<CoursePackageKpoint> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param coursePackage
	 * @return
	 */
	List<CoursePackageKpointVO> selectCoursePackageKpointPage(IPage page,@Param("coursePackageKpoint") CoursePackageKpointVO coursePackage);

	List<CoursePackageKpointVO> selectCoursePackageKpointList(CoursePackageKpointVO coursePackage);
}
