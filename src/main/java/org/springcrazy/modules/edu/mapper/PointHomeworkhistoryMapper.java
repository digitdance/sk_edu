
package org.springcrazy.modules.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.edu.entity.PointHomeworkhistory;
import org.springcrazy.modules.edu.vo.PointHomeworkhistoryVO;
import org.springcrazy.modules.system.excel.PointHomeworkhistoryExcel;

import java.util.List;
import java.util.Map;

/**
 * 课程播放记录(学习记录) Mapper 接口
 *
 * @author TongZhou
 * @since 2020-05-06
 */
public interface PointHomeworkhistoryMapper extends BaseMapper<PointHomeworkhistory> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param
	 * @return
	 */
	List<PointHomeworkhistoryVO> selectPointHomeworkPage(IPage page,  @Param("e")PointHomeworkhistoryVO pointHomeworkhistory);

	/**
	 * 按照用户数据查询所有订单课程信息
	 *
	 * @param page
	 * @param
	 * @return
	 */
	List<PointHomeworkhistory> selectCourseTrxorder(IPage page, @Param("e")PointHomeworkhistory pointHomeworkhistory);

	/**
	 * 学习总时长和最后学习时长
	 *
	 * @param userId
	 * @param courseId
	 * @return
	 */
	PointHomeworkhistory getStudyTime(@Param("userId")int userId,@Param("courseId")int courseId);
	/**
	 * 学习总时长和最后学习时长
	 * @return
	 */
	PointHomeworkhistory getPackageStudyTime(@Param("userId")int userId,@Param("coursePackageId")int coursePackageId,@Param("courseId")int courseId);

	/**
	 * 查询出某个
	 * @param courseId
	 */
	String getstudykpoint(@Param("courseId")int courseId);
	/**
	 * 查询某个套餐下的所有章节数
	 * @param coursePackageId
	 */
	String getPackageStudykpoint(@Param("coursePackageId")int coursePackageId);
	/**
	 * 学习进度学习完成度
	 *
	 * @param userId
	 * @param courseId
	 * @return
	 */
	String getstudystudyisok(@Param("userId")int userId,@Param("courseId")int courseId,@Param("coursePackageId")int coursePackageId);

	/**
	 * 课程章节信息查询学习记录
	 *
	 * @param
	 * @param
	 * @return
	 */
	List<PointHomeworkhistory> getCourseList(@Param("e")PointHomeworkhistory pointHomeworkhistory);

	/**
	 * 课程章节信息查询学习记录导出用
	 *
	 * @param
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> getCourseListSort(@Param("e")PointHomeworkhistory PointHomeworkhistory);


	/**
	 * 套餐课程查询学习记录
	 *
	 * @param
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> getCoursepackageList(IPage page, @Param("e")PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 根据学员和章节查询当前学习记录详情
	 *
	 * @param userId
	 * @param kpointId
	 * @return
	 */
	PointHomeworkhistory getHomeworkHistory(@Param("userId")int userId,@Param("kpointId")int kpointId,@Param("coursePackageId")Integer coursePackageId,@Param("studyType")int studyType);

	/**
	 * 根据学员和章节查询当前学习记录详情
	 *
	 * @param userId
	 * @param kpointId
	 * @return
	 */
	PointHomeworkhistory getOnesStudyHistory(@Param("userId")int userId,@Param("kpointId")int kpointId,@Param("studyType")int studyType,@Param("packAgee")int packAgee);

	/**
	 * 章节观看总数
	 *
	 * @param courseId
	 * @param kpointId
	 * @return
	 */
	String getWatchNum(@Param("courseId")int courseId,@Param("kpointId")int kpointId,@Param("coursePackageId")int coursePackageId);

	/**
	 * 购买单录播课程总人数
	 * @param courseId
	 * 	@param
	 */
	String getcoursepepo(@Param("courseId")int courseId);

	/**
	 * 已学习人数
	 *
	 * @param courseId
	 * @param kpointId
	 * @return
	 */
	String getWatchShould(@Param("courseId")int courseId,@Param("kpointId")int kpointId,@Param("coursePackageId")int coursePackageId);

	/**
	 *
	 *学习完成人数
	 * @param courseId
	 * @param kpointId
	 * @return
	 */
	String getWatchComplete(@Param("courseId")int courseId,@Param("kpointId")int kpointId,@Param("coursePackageId")int coursePackageId);

	/**
	 * 获取章节作业总数数据，返回map
	 */
	@MapKey("kpointId")
	Map<String,Map> getCourseKpointHomeworkHistoryDateToMap(PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 按照用户数据查询所有订单课程信息
	 *
	 * @param page
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> selectCourseUser(IPage page, @Param("e")PointHomeworkhistory PointHomeworkhistory);


	/**
	 * 按照用户数据查询所有订单课程信息全部信息
	 *
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> selectCourseUserNoPage( @Param("e")PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 按照用户数据查询所有订单课程信息
	 *
	 * @param page
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> selectStudentShouldAndComplete(IPage page, @Param("e")PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 按照用户数据查询所有订单课程信息全部信息
	 *
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> selectStudentShouldAndCompleteNoPage(@Param("e")PointHomeworkhistory PointHomeworkhistory);


	/**
	 * 学员学习记录导出
	 * @param page
	 * @return
	 */
	List<PointHomeworkhistoryExcel> exportUserStudyHistory(Page<PointHomeworkhistoryExcel> page, @Param("ew") PointHomeworkhistoryVO PointHomeworkhistoryVO);


	/**
	 * 课程章节信息查询学习记录
	 *
	 * @param
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> getPackageList(@Param("e")PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 课程观看总数
	 *
	 * @param courseId
	 * @param userList
	 * @return
	 */
	String getCourseWatchNum(@Param("courseId")int courseId, @Param("userList") String userList);

	/**
	 * 课程观看总时长
	 *
	 * @param courseId
	 * @param
	 * @return
	 */
	int getCourseWatchTime(@Param("courseId")int courseId);


	/**
	 * 查询购买但没有学习的用户
	 *
	 * @param page
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> selectNoStudy(IPage page, @Param("e") PointHomeworkhistory PointHomeworkhistory);


	/**
	 * 查询购买但没有学习的用户全部信息
	 *
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> selectNoStudyNoPage(@Param("e") PointHomeworkhistory PointHomeworkhistory);


	/**
	 * 查询购买但没有学习的用户
	 *
	 * @param page
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> selectPackageNoStudy(IPage page, @Param("e") PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 查询购买但没有学习的用户全部信息
	 *
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> selectPackageNoStudyNoPage(@Param("e") PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 查询所有的套餐购买学员
	 *
	 * @param
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> getPackageUserList(IPage page, @Param("e")PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 查询所有的套餐购买学员全部信息
	 *
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> getPackageUserListNoPage( @Param("e")PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 查询所有购买课程的学员
	 */
	List<PointHomeworkhistory> getCourseUserList(IPage page, @Param("e")PointHomeworkhistory PointHomeworkhistory);


	/**
	 * 查询课程及章节信息
	 *
	 * @param kpointId
	 * @param courseId
	 * @return
	 */
	PointHomeworkhistory getKpointAndCourse(@Param("courseId")int courseId,@Param("kpointId")int kpointId);

	/**
	 * 查询套餐已学习的课程
	 *
	 * @param page
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> selectPackageStudentShouldAndComplete(IPage page, @Param("e")PointHomeworkhistory PointHomeworkhistory);


	/**
	 * 查询套餐已学习的课程全部信息
	 *
	 * @param PointHomeworkhistory
	 * @return
	 */
	List<PointHomeworkhistory> selectPackageStudentShouldAndCompleteNoPage( @Param("e")PointHomeworkhistory PointHomeworkhistory);

	/**
	 * 根据节删章节id删除学习记录
	 * @param id
	 */
    int removeByKpointId(Integer id);
}
