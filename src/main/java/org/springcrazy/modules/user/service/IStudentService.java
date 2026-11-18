
package org.springcrazy.modules.user.service;

import org.springcrazy.common.enums.StudentStageEnum;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.clazz.pojo.ClassStudentVO;
import org.springcrazy.modules.student.entity.StudentCreditLog;
import org.springcrazy.modules.student.pojo.StudentParamDTO;
import org.springcrazy.modules.system.entity.UserInfo;
import org.springcrazy.modules.system.excel.StudentExcel;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.vo.StudentVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 学员表 服务类
 *
 * @author TongZhou
 * @since 2020-04-28
 */
public interface IStudentService extends IService<Student> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param student
	 * @return
	 */
	IPage<StudentVO> selectStudentPage(IPage<StudentVO> page, StudentVO student);

    IPage<StudentVO> selectStudentInClazzPage(IPage<StudentVO> page, StudentVO student);

    /**
	 * 用户信息
	 *
	 * @return
	 */
	UserInfo userInfo(Integer userId);

	/**
	 * 用户信息
	 *
	 * @return
	 */
	UserInfo userInfos(Integer userId);

	/**
	 * 用户信息
	 *
	 * @param account
	 * @return
	 */
	UserInfo userInfo(String account);

	/**
	 * 用户信息
	 *
	 * @param account
	 * @param password
	 * @return
	 */
	UserInfo userInfo(String account, String password);

    IPage<StudentVO> selectStudentNotInClazzPage(IPage<StudentVO> page, StudentVO student);

    IPage<StudentVO> selectClazzStudentPage(IPage<StudentVO> page, StudentVO student);

	List<StudentVO> selectCourseStudent(StudentVO student);
	/**
	 * 导入用户数据
	 */
	void importUser(List<StudentExcel> data);
	/**
	 * excel导入代理商用户数据
	 */
	void importUserAgent(List<StudentExcel> data);
	/**
	 * 获取导出用户数据
	 * @param response
	 * @return
	 */
	void exportUser(HttpServletResponse response, Map<String, Object> user);

	void register(Student student);

	Student checkStudent(Student student);

	Student checkStudentNew(Student student);

    Student checkStudentUserName(Student student);

	Student checkStudentMobile(Student student);

	Student checkStudentEmail(Student student);

	/**
	 * 用户信息
	 *
	 * @return
	 */
	Student getStudent(Integer userId);

	/**
	 * 冻结用户
	 * @param key
	 */
	void updateAvalible(String key);



	public List<String> getDeptName(String deptIds);

	 Boolean changeStage(List<Long> ids, StudentStageEnum stage, Boolean force);


    IPage<ClassStudentVO> getListByClassId(StudentParamDTO param);

	boolean incCredit(StudentCreditLog creditLog);

	boolean decCredit(StudentCreditLog creditLog);
}
