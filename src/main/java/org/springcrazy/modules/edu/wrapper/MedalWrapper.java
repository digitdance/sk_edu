
package org.springcrazy.modules.edu.wrapper;

import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.entity.Medal;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.edu.service.IMedalService;
import org.springcrazy.modules.edu.vo.CourseVO;
import org.springcrazy.modules.edu.vo.MedalVO;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author TongZhou
 */
public class MedalWrapper extends BaseEntityWrapper<Medal, MedalVO> {

	private static IMedalService iMedalService;


	static {
		iMedalService = SpringUtil.getBean(IMedalService.class);

	}

	public static MedalWrapper build() {
		return new MedalWrapper();
	}

	@Override
	public MedalVO entityVO(Medal course) {
		MedalVO courseVO = BeanUtil.copy(course, MedalVO.class);
		return courseVO;
	}

}
