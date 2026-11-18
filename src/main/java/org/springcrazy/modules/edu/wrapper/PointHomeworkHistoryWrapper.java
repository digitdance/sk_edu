
package org.springcrazy.modules.edu.wrapper;

import org.springcrazy.common.constant.CommonConstant;
import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.node.ForestNodeMerger;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.entity.CourseStudyhistory;
import org.springcrazy.modules.edu.entity.PointHomeworkhistory;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.edu.service.ICourseStudyhistoryService;
import org.springcrazy.modules.edu.service.IPointHomeworkhistoryService;
import org.springcrazy.modules.edu.vo.CourseStudyhistoryVO;
import org.springcrazy.modules.edu.vo.PointHomeworkhistoryVO;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.service.IStudentService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author TongZhou
 */
public class PointHomeworkHistoryWrapper extends BaseEntityWrapper<PointHomeworkhistory, PointHomeworkhistoryVO> {

	private static IPointHomeworkhistoryService pointHomeworkhistoryService;
	private static ICourseService courseService;

	private static IStudentService iStudentService;
	static {
		pointHomeworkhistoryService = SpringUtil.getBean(IPointHomeworkhistoryService.class);
		iStudentService = SpringUtil.getBean(IStudentService.class);
		courseService = SpringUtil.getBean(ICourseService.class);
	}

	public static PointHomeworkHistoryWrapper build() {
		return new PointHomeworkHistoryWrapper();
	}

	@Override
	public PointHomeworkhistoryVO entityVO(PointHomeworkhistory courseStudyhistory) {
		PointHomeworkhistoryVO courseStudyhistoryVO = BeanUtil.copy(courseStudyhistory, PointHomeworkhistoryVO.class);
//		if (Func.equals(courseStudyhistory.getParentId(), CommonConstant.TOP_PARENT_ID)) {
//			courseStudyhistoryVO.setParentName(CommonConstant.TOP_PARENT_NAME);
//		} else {
//			PointHomeworkhistory parent = pointHomeworkhistoryService.getById(courseStudyhistory.getParentId());
//			courseStudyhistoryVO.setParentName(parent.getKpointName());
//		}

		if (courseStudyhistory.getUserId()!=null) {
		  	Student student =  iStudentService.getById(courseStudyhistory.getUserId());
			courseStudyhistoryVO.setUserName(student.getRealName());
			courseStudyhistoryVO.setUserAvatar(student.getHeadImg());
		  //courseStudyhistoryVO.setParentName(CommonConstant.TOP_PARENT_NAME);
		}

		if (courseStudyhistory.getCourseId()!=null) {
			Course course =  courseService.getById(courseStudyhistory.getCourseId());
			courseStudyhistoryVO.setCourseName(course.getCourseName());
			courseStudyhistoryVO.setLogo(course.getLogo());
			//courseStudyhistoryVO.setParentName(CommonConstant.TOP_PARENT_NAME);
		}

		return courseStudyhistoryVO;
	}


	public List<PointHomeworkhistoryVO> listNodeVO(List<PointHomeworkhistory> list) {
		List<PointHomeworkhistoryVO> collect = list.stream().map(courseStudyhistory -> BeanUtil.copy(courseStudyhistory, PointHomeworkhistoryVO.class)).collect(Collectors.toList());
		return ForestNodeMerger.merge(collect);
	}

}
