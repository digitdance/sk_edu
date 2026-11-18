
package org.springcrazy.modules.user.service;

import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.user.entity.Teacher;
import org.springcrazy.modules.user.vo.TeacherVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 讲师 服务类
 *
 * @author TongZhou
 * @since 2020-04-28
 */
public interface ITeacherService extends IService<Teacher> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param teacher
	 * @return
	 */
	IPage<Teacher> selectTeacherPage(IPage<Teacher> page, TeacherVO teacher);


	public List<String> getDeptName(String deptIds);

}
