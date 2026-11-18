
package org.springcrazy.modules.user.service.impl;

import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.user.entity.Teacher;
import org.springcrazy.modules.user.vo.TeacherVO;
import org.springcrazy.modules.user.mapper.TeacherMapper;
import org.springcrazy.modules.user.service.ITeacherService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 讲师 服务实现类
 *
 * @author TongZhou
 * @since 2020-04-28
 */
@Service
public class TeacherServiceImpl extends ServiceImpl<TeacherMapper, Teacher> implements ITeacherService {

	@Override
	public IPage<Teacher> selectTeacherPage(IPage<Teacher> page, TeacherVO teacher) {
		return page.setRecords(baseMapper.selectTeacherPage(page, teacher));
	}

	@Override
	public List<String> getDeptName(String deptIds) {
		return baseMapper.getDeptName(Func.toStrArray(deptIds));
	}


}
