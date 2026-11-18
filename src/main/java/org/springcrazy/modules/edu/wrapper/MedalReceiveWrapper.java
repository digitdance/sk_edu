
package org.springcrazy.modules.edu.wrapper;

import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.edu.entity.Medal;
import org.springcrazy.modules.edu.entity.MedalReceive;
import org.springcrazy.modules.edu.service.IMedalReceiveService;
import org.springcrazy.modules.edu.service.IMedalService;
import org.springcrazy.modules.edu.vo.MedalReceiveVO;
import org.springcrazy.modules.edu.vo.MedalVO;

/**
 * 包装类,返回视图层所需的字段
 *
 * @author TongZhou
 */
public class MedalReceiveWrapper extends BaseEntityWrapper<MedalReceive, MedalReceiveVO> {

	private static IMedalReceiveService medalReceiveService;


	static {
		medalReceiveService = SpringUtil.getBean(IMedalReceiveService.class);

	}

	public static MedalReceiveWrapper build() {
		return new MedalReceiveWrapper();
	}

	@Override
	public MedalReceiveVO entityVO(MedalReceive course) {
		MedalReceiveVO courseVO = BeanUtil.copy(course, MedalReceiveVO.class);
		return courseVO;
	}

}
