
package org.springcrazy.modules.edu.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springcrazy.core.tool.node.ForestNodeMerger;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.edu.entity.CourseKpoint;
import org.springcrazy.modules.edu.mapper.ScoreRecordMapper;
import org.springcrazy.modules.edu.service.ICourseStudyhistoryService;
import org.springcrazy.modules.edu.vo.CourseKpointVO;
import org.springcrazy.modules.edu.mapper.CourseKpointMapper;
import org.springcrazy.modules.edu.service.ICourseKpointService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.modules.user.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

/**
 * 知识点的基本信息 服务实现类
 *
 * @author TongZhou
 * @since 2020-04-25
 */
@Service
public class CourseKpointServiceImpl extends ServiceImpl<CourseKpointMapper, CourseKpoint> implements ICourseKpointService {

	@Autowired
	ICourseStudyhistoryService courseStudyhistoryService;
	@Autowired
	IStudentService studentService;
	@Autowired
    ScoreRecordMapper scoreRecordMapper;

	@Override
	public IPage<CourseKpointVO> selectCourseKpointPage(IPage<CourseKpointVO> page, CourseKpointVO courseKpoint) {
		return page.setRecords(baseMapper.selectCourseKpointPage(page, courseKpoint));
	}
	/**
	 * 查询距离当前时间最近的直播章节
	 */
	public CourseKpoint selectLiveCourseKpointOrderByBeginTime(CourseKpoint courseKpoint){
		return baseMapper.selectLiveCourseKpointOrderByBeginTime(courseKpoint);
	}
	/**
	 * 查询带有学习记录的章节数据
	 */
	public List<CourseKpointVO> selectCourseKpointListForStudyRecord(CourseKpointVO courseKpoint){
		return baseMapper.selectCourseKpointListForStudyRecord(courseKpoint);
	}
	@Override
	public List<CourseKpointVO> tree() {
		return ForestNodeMerger.merge(baseMapper.tree());
	}

	@Override
	public List<CourseKpointVO> treeOfCourse(CourseKpoint p) {
		return ForestNodeMerger.merge(baseMapper.treeOfCourse(p));
	}

	@Override
	public boolean deleteIds(String ids) {
		Func.toIntList(ids).forEach(id ->deleteId(id));
		return true;

	}


//	@PostMapping("/submit")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "新增或修改", notes = "传入courseStudyhistory")
//	public R updateStudyScore(CourseStudyhistory courseStudyhistory,List<Student> studentList) {
//
//		IPage<StudentVO> page
//		IPage<StudentVO> pages = studentService.selectStudentInClazzPage(Condition.getPage(query), student);
//
//		courseStudyhistory.setUserId(SecureUtil.getUserId());
//		QueryWrapper<CourseStudyhistory> courseStudyhistoryQueryWrapper = new QueryWrapper<>();
//		courseStudyhistoryQueryWrapper.lambda().eq(CourseStudyhistory::getCourseId,courseStudyhistory.getCourseId())
//				.eq(CourseStudyhistory::getUserId,courseStudyhistory.getUserId())
//				.eq(CourseStudyhistory::getKpointId,courseStudyhistory.getKpointId())
//				.eq(CourseStudyhistory::getCourseType,courseStudyhistory.getCourseType())
//				.eq(CourseStudyhistory::getKpointCourseId,courseStudyhistory.getKpointCourseId());
//		CourseStudyhistory tcourse = courseStudyhistoryService.getOne(courseStudyhistoryQueryWrapper);
//
//		courseStudyhistory.setPlayercount(1);
//		if(Func.isNotEmpty(tcourse)){
//			courseStudyhistory = tcourse;
//			courseStudyhistory.setPlayercount(courseStudyhistory.getPlayercount()+1);
//		}
//		courseStudyhistory.setUpdateTime(new Date());
//
//		List<ScoreRecord> scoreRecords =  scoreRecordMapper.selectList(new LambdaQueryWrapper<ScoreRecord>()
//				.eq(ScoreRecord::getUserId,SecureUtil.getUserId())
//				.eq(ScoreRecord::getClazzId,courseStudyhistory.getClazzId())
//				.eq(ScoreRecord::getType,"kpoint_study")
//				.eq(ScoreRecord::getBizId,courseStudyhistory.getKpointId()));
//
//		if(scoreRecords.size()==0){
//			ScoreRecord scoreRecord = new ScoreRecord();
//			scoreRecord.setType("kpoint_study");
//			scoreRecord.setCourseId(courseStudyhistory.getCourseId());
//			scoreRecord.setClazzId(courseStudyhistory.getClazzId());
//			scoreRecord.setBizId(courseStudyhistory.getKpointId());
//			scoreRecord.setUserId(SecureUtil.getUserId());
//			scoreRecord.setScore(new BigDecimal(5));
//			CourseKpoint courseKpoint =  courseKpointService.getById(courseStudyhistory.getKpointId());
//			scoreRecord.setTitle(courseKpoint.getName()+"课堂成绩");
//			scoreRecordMapper.insert(scoreRecord);
//		}
//
//		return R.status(courseStudyhistoryService.saveOrUpdate(courseStudyhistory));
//	}


	/**
	 * 递归删除文件
	 * @param id
	 */
	public void deleteId(Integer id){
		this.removeById(id);
		List<CourseKpoint> list = this.list(new QueryWrapper<CourseKpoint>().eq("parent_id", id));
		//直接删除节时删除该节学习记录
		if (list.size() == 0){
			courseStudyhistoryService.deleteHistory(id);
		}
		//删除章时 删除章下节  学习记录
		list.forEach(resourceFile -> {
			courseStudyhistoryService.deleteHistory(resourceFile.getId());
			deleteId(resourceFile.getId());
		});
	}
}
