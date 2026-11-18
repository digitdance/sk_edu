
package org.springcrazy.modules.edu.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.entity.PointHomeworkhistory;
import org.springcrazy.modules.edu.entity.PointHomeworkhistory;
import org.springcrazy.modules.edu.mapper.PointHomeworkhistoryMapper;
import org.springcrazy.modules.edu.mapper.PointHomeworkhistoryMapper;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.edu.service.IPointHomeworkhistoryService;
import org.springcrazy.modules.edu.service.IPointHomeworkhistoryService;
import org.springcrazy.modules.edu.vo.PointHomeworkhistoryVO;
import org.springcrazy.modules.system.excel.PointHomeworkhistoryExcel;
import org.springcrazy.modules.system.excel.ExportStudentStudyExcel;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.service.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 课程播放记录(学习记录) 服务实现类
 *
 * @author TongZhou
 * @since 2020-05-06
 */
@Service
public class PointHomeworkhistoryServiceImpl extends ServiceImpl<PointHomeworkhistoryMapper, PointHomeworkhistory> implements IPointHomeworkhistoryService {
	@Autowired
	private ICourseService courseService;

	@Autowired
	private IStudentService studentService;

	@Override
	public IPage<PointHomeworkhistoryVO> selectPointHomeworkhistoryPage(IPage<PointHomeworkhistoryVO> page, PointHomeworkhistoryVO pointHomeworkhistory) {
		return page.setRecords(baseMapper.selectPointHomeworkPage(page, pointHomeworkhistory));
	}

	@Override
	public IPage<PointHomeworkhistory> selectCourseTrxorder(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory) {
		return page.setRecords(baseMapper.selectCourseTrxorder(page, PointHomeworkhistory));
	}

	@Override
	public PointHomeworkhistory getStudyTime(int userId, int courseId) {
		return baseMapper.getStudyTime(userId,courseId);
	}
	public PointHomeworkhistory getPackageStudyTime(int userId,int coursePackageId,int courseId){
		return baseMapper.getPackageStudyTime(userId,coursePackageId,courseId);
	}
	@Override
	public int getstudykpoint(int courseId) {
		return Integer.valueOf(baseMapper.getstudykpoint(courseId));
	}
	public int getPackageStudykpoint(int coursePackageId){
		return Integer.valueOf(baseMapper.getPackageStudykpoint(coursePackageId));
	}
	@Override
	public int getstudystudyisok(int userId, int courseId, int coursePackageId) {
		return Integer.valueOf(baseMapper.getstudystudyisok(userId,courseId,coursePackageId));
	}

//	@Override
//	public List<PointHomeworkhistory> getCourseList(PointHomeworkhistory pointHomeworkhistory) {
//		return null;
//	}

	@Override
	public List<PointHomeworkhistory> getCourseList(PointHomeworkhistory pointHomeworkhistory) {
		return baseMapper.getCourseList(pointHomeworkhistory);
	}

	@Override
	public List<PointHomeworkhistory> getCourseListSort(PointHomeworkhistory PointHomeworkhistory) {
		return baseMapper.getCourseListSort(PointHomeworkhistory);
	}


//	public void queryCourseKpointDateStudyHistoryInit(List<PointHomeworkhistory> PointHomeworkhistoryList, PointHomeworkhistory PointHomeworkhistory) {
//
//	}
	@Override
	public void queryCourseKpointDateStudyHistoryInit(List<PointHomeworkhistory>  PointHomeworkhistoryList,PointHomeworkhistory PointHomeworkhistory) {
		//判断为空,如果为空则返回出去
		if(ObjectUtil.isNull(PointHomeworkhistoryList)){
			return;
		}
		Map<String,Map> courseKpointStudyHistoryDateMap = baseMapper.getCourseKpointHomeworkHistoryDateToMap(PointHomeworkhistory);

		/*应学习人数*/
		int studyPope=0;
		//判断是套餐还是点播和直播
		if(ObjectUtil.isNotNull(PointHomeworkhistory.getCoursePackageId())&&PointHomeworkhistory.getCoursePackageId()>0){
			//套餐的应学习人数
			studyPope =getcoursepepo(PointHomeworkhistory.getCoursePackageId());
		}else{
			//点播和直播进来的应学习人数
			studyPope =getcoursepepo(PointHomeworkhistory.getCourseId());
		}

		for(PointHomeworkhistory kpoint:PointHomeworkhistoryList) {
			if (kpoint.getParentId() == 0) {
				//kpoint.setPlayercount(-1);
				kpoint.setStudyPope(-1);
				kpoint.setStudyPopeShould(-1);
				kpoint.setStudyPopeComplete(-1);
				kpoint.setStudyPopeNo(-1);
				continue;
			}
			//kpoint.setPlayercount(0);

			kpoint.setStudyPope(studyPope);
			/*已学习人数*/
			int studyPopeShould = 0;
			/*学习完成的人*/
			int studyPopeComplete = 0;

			/*已提交作业人数*/
			if (ObjectUtil.isNotEmpty(courseKpointStudyHistoryDateMap)) {
				if (ObjectUtil.isNotNull(courseKpointStudyHistoryDateMap.get(kpoint.getKpointId()))) {
					if (ObjectUtil.isNotNull(courseKpointStudyHistoryDateMap.get(kpoint.getKpointId()).get("studyNum"))) {
						studyPopeShould = Integer.valueOf(courseKpointStudyHistoryDateMap.get(kpoint.getKpointId()).get("studyNum") + "");
					}
				}
			}

			/*学习完成的人*/
			if (ObjectUtil.isNotEmpty(courseKpointStudyHistoryDateMap)) {
				if (ObjectUtil.isNotNull(courseKpointStudyHistoryDateMap.get(kpoint.getKpointId()))) {
					if (ObjectUtil.isNotNull(courseKpointStudyHistoryDateMap.get(kpoint.getKpointId()).get("studyFinishNum"))) {
						studyPopeComplete = Integer.valueOf(courseKpointStudyHistoryDateMap.get(kpoint.getKpointId()).get("studyFinishNum") + "");
					}
				}
			}
			/*已提交作业人数*/
			kpoint.setStudyPopeShould(studyPopeShould);
			/*作业完成的人*/
			kpoint.setStudyPopeComplete(studyPopeComplete);
			/*未提交作业人数*/
			kpoint.setStudyPopeNo(studyPope - studyPopeShould);
		}
	}

	@Override
	public IPage<PointHomeworkhistory> getCoursepackageList(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory) {
		return page.setRecords(baseMapper.getCoursepackageList(page, PointHomeworkhistory));
	}

	@Override
	public PointHomeworkhistory getHomeworkHistory(int userId, int kpointId,Integer coursePackageId,int studyType) {
		return baseMapper.getHomeworkHistory(userId,kpointId,coursePackageId,studyType);
	}

	@Override
	public PointHomeworkhistory getOnesStudyHistory(int userId, int kpointId, int studyType, int packAgee) {
		return baseMapper.getOnesStudyHistory(userId,kpointId,studyType,packAgee);
	}

	@Override
	public int getWatchNum(int courseId, int kpointId,int coursePackageId) {
		return Integer.valueOf(baseMapper.getWatchNum(courseId,kpointId,coursePackageId));
	}

	@Override
	public int getcoursepepo(int courseId) {
		return Integer.valueOf(baseMapper.getcoursepepo(courseId));
	}

	@Override
	public int getWatchShould(int courseId, int kpointId,int coursePackageId) {
		return Integer.valueOf(baseMapper.getWatchShould(courseId,kpointId,coursePackageId));
	}

	@Override
	public int getWatchComplete(int courseId, int kpointId,int coursePackageId) {
		return Integer.valueOf(baseMapper.getWatchComplete(courseId,kpointId,coursePackageId));
	}

	@Override
	public IPage<PointHomeworkhistory> selectCourseUser(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory) {
		return page.setRecords(baseMapper.selectCourseUser(page, PointHomeworkhistory));
	}

	@Override
	public IPage<PointHomeworkhistory> selectStudentShouldAndComplete(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory) {
		return page.setRecords(baseMapper.selectStudentShouldAndComplete(page, PointHomeworkhistory));
	}

	@Override
	public void exportUserStudyHistory(HttpServletResponse response, Map<String, Object> PointHomeworkhistory) {
		ExcelWriter excelWriter = null;
		try {
			Integer size = 3000;

			excelWriter = EasyExcel.write(response.getOutputStream(), PointHomeworkhistoryExcel.class).build();
			WriteSheet writeSheet = EasyExcel.writerSheet("用户数据表").build();
			//分页写入
			PointHomeworkhistoryVO PointHomeworkhistoryVO = new PointHomeworkhistoryVO();
			Page<PointHomeworkhistoryExcel> page = new Page<>();
			List<PointHomeworkhistoryExcel> list = baseMapper.exportUserStudyHistory(page , PointHomeworkhistoryVO);
			for (int i = 1; i <= (list.size()/size)+1; i++) {
				page.setCurrent(i);
				page.setSize(size);
				list = baseMapper.exportUserStudyHistory(page, PointHomeworkhistoryVO);
				excelWriter.write(list, writeSheet);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (excelWriter != null) {
				excelWriter.finish();
			}
		}
	}

	@Override
	public List<PointHomeworkhistory> getPackageList(PointHomeworkhistory PointHomeworkhistory) {
		return  baseMapper.getPackageList(PointHomeworkhistory);
	}

	@Override
	public int getCourseWatchNum(int courseId, String userList) {
		return Integer.valueOf(baseMapper.getCourseWatchNum(courseId,userList));
	}

	@Override
	public int getCourseWatchTime(int courseId) {
		return baseMapper.getCourseWatchTime(courseId);
	}

	@Override
	public IPage<PointHomeworkhistory> selectNoStudy(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory) {
		return page.setRecords(baseMapper.selectNoStudy(page, PointHomeworkhistory));
	}

	@Override
	public IPage<PointHomeworkhistory> selectPackageNoStudy(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory) {
		return page.setRecords(baseMapper.selectPackageNoStudy(page, PointHomeworkhistory));
	}

	@Override
	public IPage<PointHomeworkhistory> getPackageUserList(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory) {
		return page.setRecords(baseMapper.getPackageUserList(page, PointHomeworkhistory));
	}

	@Override
	public PointHomeworkhistory getKpointAndCourse(int courseId, int kpointId) {
		return baseMapper.getKpointAndCourse(courseId, kpointId);
	}

	@Override
	public IPage<PointHomeworkhistory> selectPackageStudentShouldAndComplete(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory) {
		return page.setRecords(baseMapper.selectPackageStudentShouldAndComplete(page, PointHomeworkhistory));
	}
	@Override
	public IPage<PointHomeworkhistory> getCourseUserList(IPage<PointHomeworkhistory> page, PointHomeworkhistory PointHomeworkhistory) {
		List<PointHomeworkhistory> list = baseMapper.getCourseUserList(page, PointHomeworkhistory);
		Course course = courseService.getById(PointHomeworkhistory.getCourseId());
		if("3".equals(course.getSellType())){
			for(PointHomeworkhistory PointHomework:list) {
				initCoursePackageListStudyHistory(PointHomework);
			}
		}else{
			initCourseUserListStudyHistory(list);
		}

		return page.setRecords(list);
	}
	public void initCoursePackageListStudyHistory(PointHomeworkhistory PointHomework){
		PointHomeworkhistory course =getPackageStudyTime(PointHomework.getUserId(),PointHomework.getCourseId(),0);
		if(course!=null){
			if(course.getWatchTime()>0){
				PointHomework.setWatchStingTime(gitTime(course.getWatchTime()));
			}else {
				PointHomework.setWatchStingTime("暂未学习时长");
			}
			PointHomework.setUpdateTime(course.getUpdateTime());
			int v = getstudystudyisok(PointHomework.getUserId(),0,PointHomework.getCourseId());
			int y= getPackageStudykpoint(PointHomework.getCourseId());
			if(v<=0){
				PointHomework.setStudyLearning("0.00%");
			}else {
				float num= (float)v*100/y;
				DecimalFormat df = new DecimalFormat("0.00");//格式化小数
				PointHomework.setStudyLearning(df.format(num)+"%");
			}
		}else {
			PointHomework.setWatchStingTime("暂未学习时长");
			PointHomework.setStudyLearning("0.00%");
		}
	}
	public void initCoursePackageCourseListStudyHistory(PointHomeworkhistory PointHomework){
		PointHomeworkhistory course =getPackageStudyTime(PointHomework.getUserId(),PointHomework.getCoursePackageId(),PointHomework.getCourseId());
		if(course!=null){
			if(course.getWatchTime()>0){
				PointHomework.setWatchStingTime(gitTime(course.getWatchTime()));
			}else {
				PointHomework.setWatchStingTime("暂未学习时长");
			}
			PointHomework.setUpdateTime(course.getUpdateTime());
			int v = getstudystudyisok(PointHomework.getUserId(),PointHomework.getCourseId(),PointHomework.getCoursePackageId());
			int y= getstudykpoint(PointHomework.getCourseId());
			if(v<=0){
				PointHomework.setStudyLearning("0.00%");
			}else {
				float num= (float)v*100/y;
				DecimalFormat df = new DecimalFormat("0.00");//格式化小数
				PointHomework.setStudyLearning(df.format(num)+"%");
			}
		}else {
			PointHomework.setWatchStingTime("暂未学习时长");
			PointHomework.setStudyLearning("0.00%");
		}
	}
	public void initCourseUserListStudyHistory(List<PointHomeworkhistory> PointHomeworkhistoryList){
		for(PointHomeworkhistory PointHomework:PointHomeworkhistoryList){
			initCourseUserListStudyHistoryDate(PointHomework);
		}
	}
	public void initCourseUserListStudyHistoryDate(PointHomeworkhistory PointHomework){
		PointHomeworkhistory course =getStudyTime(PointHomework.getUserId(),PointHomework.getCourseId());
		if(course!=null){
			if(course.getWatchTime()>0){
				PointHomework.setWatchStingTime(gitTime(course.getWatchTime()));
			}else {
				PointHomework.setWatchStingTime("暂未学习时长");
			}
			PointHomework.setUpdateTime(course.getUpdateTime());
			int v = getstudystudyisok(PointHomework.getUserId(),PointHomework.getCourseId(),0);
			int y=getstudykpoint(PointHomework.getCourseId());
			if(v<=0){
				PointHomework.setStudyLearning("0.00%");
			}else {
				float num= (float)v*100/y;
				DecimalFormat df = new DecimalFormat("0.00");//格式化小数
				PointHomework.setStudyLearning(df.format(num)+"%");
			}
		}else {
			PointHomework.setWatchStingTime("暂未学习时长");
			PointHomework.setStudyLearning("0.00%");
		}
	}

	@Override
	public int deleteHistory(Integer id) {
		return baseMapper.removeByKpointId(id);
	}

	@Override
	public void exportUser(HttpServletResponse response, PointHomeworkhistory PointHomeworkhistory) {
		ExcelWriter excelWriter = null;
		try {
			excelWriter = EasyExcel.write(response.getOutputStream(), ExportStudentStudyExcel.class).build();
			WriteSheet writeSheet = EasyExcel.writerSheet("用户学习记录表").build();
			List<ExportStudentStudyExcel> list  = new ArrayList<>();
			//查询对应课程的章节信息
			List<PointHomeworkhistory> pages = baseMapper.getCourseList(PointHomeworkhistory);
			//查询学生信息
			Student student = studentService.getStudent(PointHomeworkhistory.getUserId());
			//查询课程信息
			Course courseName = courseService.getCourseById(PointHomeworkhistory.getCourseId());
			//查询如果是套餐，则加入套餐名称。
			Course coursePake = new Course();
			if(Func.isNotEmpty(PointHomeworkhistory.getCoursePackageId())&&PointHomeworkhistory.getCoursePackageId()!=0){
				 coursePake = courseService.getCourseById(PointHomeworkhistory.getCoursePackageId());
			}
			//循环赋值
			for(PointHomeworkhistory history:pages){
				//只查章
				if(history.getParentId()==0){
					ExportStudentStudyExcel exportStudentStudyExcel =  new ExportStudentStudyExcel();
					exportStudentStudyExcel.setUserName(student.getUserName());
					exportStudentStudyExcel.setCourseName(courseName.getCourseName());
					exportStudentStudyExcel.setKpointName(history.getKpointName());
					if (Func.isNotEmpty(coursePake.getCourseName())){
						exportStudentStudyExcel.setPakeName(coursePake.getCourseName());
					}
					list.add(exportStudentStudyExcel);
					//添加完章查询对应的节
					for(PointHomeworkhistory historychi:pages){
						if((history.getKpointId()).equals(historychi.getParentId())){
							ExportStudentStudyExcel exportStudentStudyExcelchil =  new ExportStudentStudyExcel();
							exportStudentStudyExcelchil.setUserName(student.getUserName());
							exportStudentStudyExcelchil.setCourseName(courseName.getCourseName());
							exportStudentStudyExcelchil.setKpointName(historychi.getKpointName());
							exportStudentStudyExcelchil.setStudyTime(historychi.getVideoTime());
							if (Func.isNotEmpty(coursePake.getCourseName())){
								exportStudentStudyExcelchil.setPakeName(coursePake.getCourseName());
							}
							PointHomeworkhistory course=baseMapper.getHomeworkHistory(PointHomeworkhistory.getUserId(),historychi.getKpointId(),PointHomeworkhistory.getCoursePackageId(),0);
							if(course!=null){
								/*累计学习时长*/
								if(course.getWatchTime()>0){
									exportStudentStudyExcelchil.setStudyTimeNums(gitTime(course.getWatchTime()));
								}else {
									exportStudentStudyExcelchil.setStudyTimeNums("0");
								}
								/*学习进度*/
								exportStudentStudyExcelchil.setStudyLearning(course.getStudyLearning());
								/*是否完成*/
								if("1".equals(course.getComplete())){
									exportStudentStudyExcelchil.setComplete("未完成");
								}else {
									exportStudentStudyExcelchil.setComplete("完成");
								}
								/*最后学习时长*/
								exportStudentStudyExcelchil.setUpdateTime(course.getUpdateTime());
							}else {
								/*累计学习时长*/
								exportStudentStudyExcelchil.setStudyTimeNums("0");
								/*学习进度*/
								exportStudentStudyExcelchil.setStudyLearning("暂未学习进度");
								/*是否完成*/
								exportStudentStudyExcelchil.setComplete("未完成");

							}
							list.add(exportStudentStudyExcelchil);
						}
					}
				}
			}
			excelWriter.write(list, writeSheet);

		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if (excelWriter != null) {
				excelWriter.finish();
			}
		}
	}

	@Override
	public void exportKpointIdUser(HttpServletResponse response, PointHomeworkhistory PointHomeworkhistory) {
		ExcelWriter excelWriter = null;
		List<PointHomeworkhistory> pages = null;
		try {
			excelWriter = EasyExcel.write(response.getOutputStream(), ExportStudentStudyExcel.class).build();
			WriteSheet writeSheet = EasyExcel.writerSheet("用户学习记录表").build();
			List<ExportStudentStudyExcel> list  = new ArrayList<>();
			//查询课程信息
			Course courseName = courseService.getCourseById(PointHomeworkhistory.getCourseId());
			//查询如果是套餐，则加入套餐名称。
			Course coursePake = new Course();
			if(Func.isNotEmpty(PointHomeworkhistory.getKpointCourseId())&&PointHomeworkhistory.getKpointCourseId()!=0){
				coursePake = courseService.getCourseById(PointHomeworkhistory.getKpointCourseId());
			}
			int studyType=1;
			PointHomeworkhistory PointHomeworkhistory1 = baseMapper.getKpointAndCourse(PointHomeworkhistory.getCourseId(),PointHomeworkhistory.getKpointId());

			if("PACKAGE".equals(PointHomeworkhistory.getCourseType())){
				studyType=2;
				if("ALL".equals(PointHomeworkhistory.getPopeType())){
					//全部所有的人信息
					pages = baseMapper.getPackageUserListNoPage(PointHomeworkhistory);
				}else if("SHOULD".equals(PointHomeworkhistory.getPopeType())){
					//已学习
					pages=baseMapper.selectPackageStudentShouldAndCompleteNoPage(PointHomeworkhistory);
				}else if("COMPLETE".equals(PointHomeworkhistory.getPopeType())){
					//完成学员
					pages=baseMapper.selectPackageStudentShouldAndCompleteNoPage(PointHomeworkhistory);
				}else if("NO".equals(PointHomeworkhistory.getPopeType())){
					//未学习学员
					pages=baseMapper.selectPackageNoStudyNoPage(PointHomeworkhistory);
				}
			}else {
				if("ALL".equals(PointHomeworkhistory.getPopeType())){
					//全部所有的人信息
					pages=baseMapper.selectCourseUserNoPage(PointHomeworkhistory);
				}else if("SHOULD".equals(PointHomeworkhistory.getPopeType())){
					//已学习
					pages=baseMapper.selectStudentShouldAndCompleteNoPage(PointHomeworkhistory);
				}else if("COMPLETE".equals(PointHomeworkhistory.getPopeType())){
					//完成学员
					pages=baseMapper.selectStudentShouldAndCompleteNoPage(PointHomeworkhistory);
				}else if("NO".equals(PointHomeworkhistory.getPopeType())){
					//未学习学员
					pages=baseMapper.selectNoStudyNoPage(PointHomeworkhistory);
				}
			}

			for(PointHomeworkhistory prpo:pages){
				//查询学生信息
				Student student = studentService.getStudent(prpo.getUserId());
				//填写表信息
				ExportStudentStudyExcel exportStudentStudyExcel =  new ExportStudentStudyExcel();
				if(Func.isNotEmpty(student.getUserName())){
					exportStudentStudyExcel.setUserName(student.getUserName());
				}else {
					exportStudentStudyExcel.setUserName(student.getShowName());
				}
				exportStudentStudyExcel.setCourseName(courseName.getCourseName());
				exportStudentStudyExcel.setKpointName(PointHomeworkhistory1.getKpointName());
				exportStudentStudyExcel.setStudyTime(PointHomeworkhistory1.getVideoTime());
				if (Func.isNotEmpty(coursePake.getCourseName())){
					exportStudentStudyExcel.setPakeName(coursePake.getCourseName());
				}
				PointHomeworkhistory course = new PointHomeworkhistory();
				if(Func.isNotEmpty(PointHomeworkhistory.getCoursePackageId())&&PointHomeworkhistory.getCoursePackageId()!=0){
					course=baseMapper.getHomeworkHistory(prpo.getUserId(),PointHomeworkhistory.getKpointId(),PointHomeworkhistory.getCoursePackageId(),studyType);
				}else {
					course=baseMapper.getHomeworkHistory(prpo.getUserId(),PointHomeworkhistory.getKpointId(),0,studyType);
				}
				if(course!=null){
					/*累计学习时长*/
					if(course.getWatchTime()>0){
						exportStudentStudyExcel.setStudyTimeNums(Integer.toString(course.getWatchTime()));
					}else {
						exportStudentStudyExcel.setStudyTimeNums("0");
					}
					/*学习进度*/
					exportStudentStudyExcel.setStudyLearning(course.getStudyLearning());
					/*是否完成*/
					if("1".equals(course.getComplete())){
						exportStudentStudyExcel.setComplete("未完成");
					}else {
						exportStudentStudyExcel.setComplete("完成");
					}
					/*最后学习时长*/
					exportStudentStudyExcel.setUpdateTime(course.getUpdateTime());
				}else {
					/*累计学习时长*/
					exportStudentStudyExcel.setStudyTimeNums("0");
					/*学习进度*/
					exportStudentStudyExcel.setStudyLearning("暂未学习进度");
					/*是否完成*/
					exportStudentStudyExcel.setComplete("未完成");

				}
				list.add(exportStudentStudyExcel);
			}
			excelWriter.write(list, writeSheet);

		} catch (IOException e) {
				e.printStackTrace();
		}finally {
			if (excelWriter != null) {
				excelWriter.finish();
			}
		}
	}

	public String  gitTime(int time){
		int theTime = Integer.valueOf(time);// 秒
		String middles= "";// 分
		int middle= 0;// 分
		int hour= 0;// 小时
		if(theTime > 60) {
			middles = theTime/60 < 10 ? "0" + theTime/60 : theTime/60+"" ;//小于10分钟的补位
			middle = theTime/60;//分钟数
			theTime = theTime%60;
//			if(middle> 60) {
//				hour= middle/60;
//				middle= middle%60;
//			}
		}else{
			if(theTime>9){
				return "00:"+theTime;
			}else{
				return "00:0"+theTime;
			}

		}
		String timec="";
//		if(hour> 0) {
//			timec += hour+":";
//		}
		if(middle > 0) {
			timec += middles+":";
		}
		timec += theTime < 10 ? "0" + theTime : theTime;
		return timec;
	}
}
