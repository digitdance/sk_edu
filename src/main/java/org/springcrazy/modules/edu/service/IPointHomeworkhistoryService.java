
package org.springcrazy.modules.edu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.edu.entity.PointHomeworkhistory;
import org.springcrazy.modules.edu.entity.PointHomeworkhistory;
import org.springcrazy.modules.edu.vo.PointHomeworkhistoryVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 课程播放记录(学习记录) 服务类
 *
 * @author TongZhou
 * @since 2020-05-06
 */
public interface IPointHomeworkhistoryService extends IService<PointHomeworkhistory> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param PointHomeworkhistory
	 * @return
	 */
	IPage<PointHomeworkhistoryVO> selectPointHomeworkhistoryPage(IPage<PointHomeworkhistoryVO> page, PointHomeworkhistoryVO PointHomeworkhistory);

	/**
	 * 按照用户数据查询所有订单课程信息
	 *
	 * @param page
	 * @param PointHomeworkhistory
	 * @return
	 */
	IPage<PointHomeworkhistory> selectCourseTrxorder(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 学习总时长和最后学习时长
	 *
	 * @param userId
	 * @param courseId
	 * @return
	 */
	PointHomeworkhistory getStudyTime(int userId,int courseId);
	/**
	 * 获取套餐的学习总时长和最后学习时长
	 *
	 * @param userId
	 * @param courseId
	 * @return
	 */
	PointHomeworkhistory getPackageStudyTime(int userId,int coursePackageId,int courseId);
	/**
	 * 某个课程总章节数
	 * @param courseId
	 * @return
	 */
	int getstudykpoint(int courseId);
	/**
	 * 查询某个套餐下的所有章节数
	 * @param coursePackageId
	 */
	int getPackageStudykpoint(int coursePackageId);
	/**
	 * 学习进度学习完成度
	 *
	 * @param userId
	 * @param courseId
	 * @return
	 */
	int getstudystudyisok(int userId,int courseId,int coursePackageId);

	/**
	 * 课程章节信息查询学习记录
	 *
	 * @param
	 * @param
	 * @return
	 */
	List<PointHomeworkhistory> getCourseList(PointHomeworkhistory pointHomeworkhistory);


	/**
	 * 课程章节信息查询学习记录导出用
	 *
	 * @param
	 * @param
	 * @return
	 */
	List<PointHomeworkhistory> getCourseListSort(PointHomeworkhistory pointHomeworkhistory);
	/**
	 * 查询课程章节的学习记录数据，整理查询并放入list对象中
	 */
	void queryCourseKpointDateStudyHistoryInit(List<PointHomeworkhistory>  pointHomeworkhistoryList,PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 套餐课程查询学习记录
	 *
	 * @param
	 * @param PointHomeworkhistory
	 * @return
	 */
	IPage<PointHomeworkhistory> getCoursepackageList(IPage<PointHomeworkhistory> page,PointHomeworkhistory PointHomeworkhistory);


	/**
	 * 根据学员和章节查询当前学习记录详情
	 *
	 * @param userId
	 * @param kpointId
	 * @return
	 */
	PointHomeworkhistory getHomeworkHistory(int userId,int kpointId,Integer coursePackageId,int studyType);

	/**
	 * 根据学员和章节查询当前学习记录详情
	 *
	 * @param userId
	 * @param kpointId
	 * @return
	 */
	PointHomeworkhistory getOnesStudyHistory(int userId,int kpointId,int studyTyp,int packAgee);

	/**
	 * 章节观看总数
	 *
	 * @param courseId
	 * @param kpointId
	 * @return
	 */
	int getWatchNum(int courseId,int kpointId,int coursePackageId);

	/**
	 * 购买单录播课程总人数
	 * @param courseId
	 * 	@param
	 */
	int getcoursepepo(int courseId);

	/**
	 * 已学习人数
	 *
	 * @param courseId
	 * @param kpointId
	 * @return
	 */
	int getWatchShould(int courseId,int kpointId,int coursePackageId);

	/**
	 *
	 *学习完成人数
	 * @param courseId
	 * @param kpointId
	 * @return
	 */
	int getWatchComplete(int courseId,int kpointId,int coursePackageId);

	/**
	 * 按照课程查询所有用户信息包括
	 *
	 * @param page
	 * @param PointHomeworkhistory
	 * @return
	 */
	IPage<PointHomeworkhistory> selectCourseUser(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 按照课程查询所有用户信息包括
	 *
	 * @param page
	 * @param PointHomeworkhistory
	 * @return
	 */
	IPage<PointHomeworkhistory> selectStudentShouldAndComplete(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 导出交易记录
	 * @param response
	 */
	void exportUserStudyHistory(HttpServletResponse response , Map<String, Object> PointHomeworkhistory);

	/**
	 * 课程章节信息查询学习记录
	 *
	 * @param
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> getPackageList(PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 课程观看总数
	 *
	 * @param courseId
	 * @param userList
	 * @return
	 */
	int getCourseWatchNum(int courseId,String userList);


	/**
	 * 课程观看总时长
	 *
	 * @param courseId
	 * @param
	 * @return
	 */
	int getCourseWatchTime(int courseId);

	/**
	 * 查询购买但没有学习的用户
	 *
	 * @param page
	 * @param PointHomeworkhistory
	 * @return
	 */
	IPage<PointHomeworkhistory> selectNoStudy(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 查询套餐购买但没有学习的用户
	 *
	 * @param page
	 * @param PointHomeworkhistory
	 * @return
	 */
	IPage<PointHomeworkhistory> selectPackageNoStudy(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory);


	/**
	 * 查询所有的套餐购买学员
	 *
	 * @param
	 * @param PointHomeworkhistory
	 * @return
	 */
	IPage<PointHomeworkhistory> getPackageUserList(IPage<PointHomeworkhistory> page,PointHomeworkhistory PointHomeworkhistory);


	/**
	 * 查询课程及章节信息
	 *
	 * @param kpointId
	 * @param courseId
	 * @return
	 */
	PointHomeworkhistory getKpointAndCourse(int courseId,int kpointId);


	/**
	 * 查询套餐已学习的课程
	 *
	 * @param page
	 * @param PointHomeworkhistory
	 * @return
	 */
	IPage<PointHomeworkhistory> selectPackageStudentShouldAndComplete(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory);
	/**
	 * 查询所有的课程购买学员
	 */
	IPage<PointHomeworkhistory> getCourseUserList(IPage<PointHomeworkhistory> page,PointHomeworkhistory PointHomeworkhistory);
	/**
	 * 套餐课程的子课程的学习记录查询
	 */
	void initCoursePackageCourseListStudyHistory(PointHomeworkhistory PointHomework);
	/**
	 * 整理PointHomeworkhistory对象添加学习记录
	 */
	void initCourseUserListStudyHistory(List<PointHomeworkhistory> PointHomeworkhistoryList);
	/**
	 * 秒转化时间单位
	 */
	String gitTime(int time);
	/**
	 * 删除章下节 学习记录
	 * @param id
	 */
	int deleteHistory(Integer id);


	/**
	 * 获取导出用户学习记录数据
	 * @param response
	 * @return
	 */
	void exportUser(HttpServletResponse response, PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 获取导出章节用户学习记录数据
	 * @param response
	 * @return
	 */
	void exportKpointIdUser(HttpServletResponse response, PointHomeworkhistory PointHomeworkhistory);



}
