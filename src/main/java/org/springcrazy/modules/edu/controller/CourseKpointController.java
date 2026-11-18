
package org.springcrazy.modules.edu.controller;

import cn.hutool.core.util.ObjectUtil;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.vod.model.v20170321.GetPlayInfoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoInfoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.google.common.collect.Maps;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springcrazy.common.tool.*;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.node.INode;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.core.tool.utils.StringUtil;
import org.springcrazy.core.tool.vod.AliVodUtil;
import org.springcrazy.modules.cms.entity.WebsiteProfile;
import org.springcrazy.modules.edu.entity.*;
import org.springcrazy.modules.edu.service.*;
import org.springcrazy.modules.edu.vo.CourseKpointPlayVO;
import org.springcrazy.modules.edu.vo.SubjectVO;
import org.springcrazy.modules.edu.wrapper.CourseKpointWrapper;
import org.springcrazy.modules.edu.wrapper.SubjectWrapper;
import org.springcrazy.modules.exam.entity.Exampaper;
import org.springcrazy.modules.exam.entity.Qstmiddle;
import org.springcrazy.modules.exam.entity.Question;
import org.springcrazy.modules.exam.service.IExampaperService;
import org.springcrazy.modules.exam.service.IQstmiddleService;
import org.springcrazy.modules.exam.service.IQuestionService;
import org.springcrazy.modules.exam.vo.QstmiddleVO;
import org.springcrazy.modules.exam.vo.QuestionVO;
import org.springframework.web.bind.annotation.*;
import org.springcrazy.modules.edu.vo.CourseKpointVO;
import org.springcrazy.core.boot.ctrl.CrazyController;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 知识点的基本信息 控制器
 *
 * @author TongZhou
 * @since 2020-04-25
 */
@RestController
@AllArgsConstructor
@RequestMapping("/edu/coursekpoint")
@Api(value = "知识点的基本信息", tags = "知识点的基本信息接口")
public class CourseKpointController extends CrazyController {

	private ICourseKpointService courseKpointService;
	private ICourseService courseService;

	private ICoursePackageExamService coursePackageExamService;
	private ICourseExamService courseExamService;
	private IExampaperService exampaperService;
	private ICourseStudyhistoryService courseStudyhistoryService;
	private ICourseMaterialService courseMaterialService;
	private IQstmiddleService qstmiddleService;
	private IQuestionService questionService;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入courseKpoint")
	public R<CourseKpointPlayVO> detail(CourseKpoint courseKpoint,@RequestParam(defaultValue = "teacher")String userType,String courseIds) {
		CourseKpoint detail = courseKpointService.getOne(Condition.getQueryWrapper(courseKpoint));
		CourseKpointPlayVO courseKpointPlayVO = BeanUtil.copy(detail, CourseKpointPlayVO.class);
		String playProgress = "2";//进度条
		String rateComponent = "2";//倍速
		//阿里云点播
//		if(Func.equals(detail.getVideoType(),"aliyunvod")||Func.equals(detail.getVideoType(),"aliyunReplay")){
//			DefaultAcsClient client = MsgVodFactory.getMsgSender(WebsiteProfile.ALIYUN);
//			Map<String, String> config = ProfileConfig.getConfig(WebsiteProfile.ALIYUN_VOD);
//			playProgress=config.get("play_progress");
//			rateComponent=config.get("rate_component");
//			GetVideoPlayAuthResponse response = AliVodUtil.getVideoPlayAuth(client, detail.getVideoUrl());
//			GetPlayInfoResponse playInfoResponse = AliVodUtil.getPlayInfo(client, detail.getVideoUrl());
//			if (Func.notNull(response)) {
//				Map<String,Object> params = Maps.newHashMap();
//				params.put("playAuth",response.getPlayAuth());
//				if(Func.isNotEmpty(playInfoResponse)){
//					params.put("playURL",playInfoResponse.getPlayInfoList());
//				}
//				courseKpointPlayVO.setParams(params);
//			}
//		}
		//百家云点播
		if(Func.equals(detail.getVideoType(),"baijiayunvod")){
			Map<String, String> config = ProfileConfig.getConfig(WebsiteProfile.BAIJIA_VOD);
			playProgress=config.get("play_progress");
			rateComponent=config.get("rate_component");
			String token = BajiayunUtil.getUrl(detail.getVideoUrl());
			String token1 = BajiayunUtil.getToken(detail.getVideoUrl());
			Map<String,Object> params = Maps.newHashMap();
			params.put("playURL",token);
			params.put("token",token1);
			courseKpointPlayVO.setParams(params);
		}
		//百家云直播
		if(Func.equals(detail.getVideoType(),"baijiayunlive")){
			//当直播时间未结束
			if(detail.getLiveEndTime().compareTo(LocalDateTime.now()) >= 0){
				Map<String,String> userMap = Maps.newHashMap();
				userMap.put("userId", SecureUtil.getUser().getUserId()+"");
				userMap.put("userName",SecureUtil.getUser().getUserName());
				String token = BajiayunUtil.roomUrl(detail.getVideoUrl(),"WEB",userType,userMap);
				String clienturl = BajiayunUtil.roomUrl(detail.getVideoUrl(),"APP",userType,userMap);
				Map<String,Object> params = Maps.newHashMap();
				params.put("url",token);
				params.put("clienturl",clienturl);
				courseKpointPlayVO.setParams(params);
			}else{//当直播时间已结束
				if(Func.equals(detail.getOpenReplay(),2)){
					Map<String,String> userMap = Maps.newHashMap();
					userMap.put("userId", SecureUtil.getUser().getUserId()+"");
					userMap.put("userName",SecureUtil.getUser().getUserName());
					String token = BajiayunUtil.roomUrl(detail.getVideoUrl(),"WEB",userType,userMap);
					String clienturl = BajiayunUtil.roomUrl(detail.getVideoUrl(),"APP",userType,userMap);
					Map<String,Object> params = Maps.newHashMap();
					params.put("url",token);
					params.put("clienturl",clienturl);
					courseKpointPlayVO.setParams(params);
				}
				//百家云回放
				if(Func.equals(detail.getOpenReplay(),1)){
					//房间号
					Map<String,Object> params = Maps.newHashMap();
					params.put("url",BajiayunUtil.getBackToken(detail.getVideoUrl()));
					courseKpointPlayVO.setParams(params);
				}
			}
		}

		if(("1").equals(playProgress)){
			int userId=SecureUtil.getUserId();
			CourseStudyhistory courseStudyhistory = new CourseStudyhistory();
			if(ObjectUtil.isNotNull(courseIds) && ("").equals(courseIds)){
				courseStudyhistory=courseStudyhistoryService.getOnesStudyHistory(userId,courseKpoint.getId(),2,Integer.valueOf(courseIds));
			}else {
				courseStudyhistory=courseStudyhistoryService.getOnesStudyHistory(userId,courseKpoint.getId(),1,0);
			}
			if(ObjectUtil.isNotNull(courseStudyhistory)){
				if(("2").equals(courseStudyhistory.getComplete())){
					playProgress="2";
				}
			}
		}
		courseKpointPlayVO.setPlayProgress(playProgress);
		if(Func.equals(courseKpointPlayVO.getVideoType(),"aliyunvod")||Func.equals(courseKpointPlayVO.getVideoType(),"baijiayunvod")||Func.equals(courseKpointPlayVO.getVideoType(),"polyvvod")||Func.equals(courseKpointPlayVO.getVideoType(),"othervod")){
			if(courseKpointPlayVO.getVideoTime()!=null&&!"".equals(courseKpointPlayVO.getVideoTime())){
				int value= Integer.valueOf(courseKpointPlayVO.getVideoTime());
				String timeStr = null;
				int hour;
				int minute ;
				int second = 0;
				if (value > 0) {
					minute = value / 60;
					second = value % 60;
					if(minute<10){
						if(second<10){
							timeStr = "0"+minute + ":0" + second;
						}else {
							timeStr = "0"+minute + ":" + second;
						}
					}else {
						timeStr = minute + ":" + second;
					}
					courseKpointPlayVO.setVideoTime(timeStr);
				}
			}
		}
		courseKpointPlayVO.setRateComponent(rateComponent);
		return R.data(courseKpointPlayVO);
	}





	/**
	 * 分页 知识点的基本信息
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入courseKpoint")
	public R<List<CourseKpointVO>> list(CourseKpoint courseKpoint, Query query) {
		List<CourseKpoint> list = courseKpointService.list(Condition.getQueryWrapper(courseKpoint).lambda().orderByAsc(CourseKpoint::getSort));
			for(CourseKpoint courseKpoint1:list){
				Integer kMaterialCount = 0;
				List<CourseMaterial> courseMaterials = new ArrayList<CourseMaterial>();
				if(Func.isNotBlank(courseKpoint1.getMaterialIds())){
					String[] ids = StringUtils.split(courseKpoint1.getMaterialIds(),",");
					courseMaterials = courseMaterialService.list(new LambdaQueryWrapper<CourseMaterial>()
							.in(CourseMaterial::getId,ids));
					kMaterialCount = ids.length;
					courseKpoint1.setCourseMaterials(courseMaterials);
				}

				QstmiddleVO Qstmiddle = new QstmiddleVO();
				Qstmiddle.setKpointId(courseKpoint1.getId());
				//Integer qstCount=  qstmiddleService.selectQstmiddleCount(Qstmiddle);

//				List<Exampaper> exampapers1 = exampaperService.list(new LambdaQueryWrapper<Exampaper>().eq(Exampaper::getKpointId,courseKpoint1.getId())
//						.eq(Exampaper::getType,4));

				List<CoursePackageExam> exampapers1 = coursePackageExamService.list(new LambdaQueryWrapper<CoursePackageExam>().eq(CoursePackageExam::getKpointId,courseKpoint1.getId())
						.eq(CoursePackageExam::getExamType,4));
				List<Integer> ids1= new ArrayList<Integer>();
				if(exampapers1.size()>0){
					ids1 =  exampapers1.stream().map(item->item.getExampaperId()).collect(Collectors.toList());
				}
				Integer qstCount = 0;
				if(ids1.size()>0){
					qstCount =  qstmiddleService.count(
							new QueryWrapper<Qstmiddle>().in("paperId",ids1)
					);
				}
				courseKpoint1.setKqstCount(qstCount);
				courseKpoint1.setKmaterialCount(kMaterialCount);

				List<CoursePackageExam> scExampapers1 = coursePackageExamService.list(new LambdaQueryWrapper<CoursePackageExam>().eq(CoursePackageExam::getKpointId,courseKpoint1.getId())
						.eq(CoursePackageExam::getExamType,3));
				List<Integer> scIds1= new ArrayList<Integer>();
				if(scExampapers1.size()>0){
					scIds1 =  scExampapers1.stream().map(item->item.getExampaperId()).collect(Collectors.toList());
				}


//				List<Exampaper> exampapers = exampaperService.list(new LambdaQueryWrapper<Exampaper>().eq(Exampaper::getKpointId,courseKpoint1.getId())
//						.eq(Exampaper::getType,3));
//				List<Integer> ids =  exampapers.stream().map(item->item.getId()).collect(Collectors.toList());
//
				Integer shicaoCOUNT = 0;
				if(scIds1.size()>0){
					shicaoCOUNT =  qstmiddleService.count(
							new QueryWrapper<Qstmiddle>().in("paperId",scIds1)
					);
				}
				courseKpoint1.setShicaoCount(shicaoCOUNT);
				if(Func.equals(courseKpoint1.getVideoType(),"aliyunvod")||Func.equals(courseKpoint1.getVideoType(),"baijiayunvod")||Func.equals(courseKpoint1.getVideoType(),"polyvvod")||Func.equals(courseKpoint1.getVideoType(),"othervod")){
					if(courseKpoint1.getVideoTime()!=null&&!"".equals(courseKpoint1.getVideoTime())){
						int value= Integer.valueOf(courseKpoint1.getVideoTime());
						String timeStr = null;
						int hour;
						int minute ;
						int second = 0;
						if (value > 0) {
							minute = value / 60;
							second = value % 60;
							if(minute<10){
								if(second<10){
									timeStr = "0"+minute + ":0" + second;
								}else {
									timeStr = "0"+minute + ":" + second;
								}
							}else {
								timeStr = minute + ":" + second;
							}
							courseKpoint1.setVideoTime(timeStr);
						}
					}
				}
			}
		return R.data(CourseKpointWrapper.build().listNodeVO(list));
	}

	/**
	 * 自定义分页 知识点的基本信息
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入courseKpoint")
	public R<IPage<CourseKpointVO>> page(CourseKpointVO courseKpoint, Query query) {
		IPage<CourseKpointVO> pages = courseKpointService.selectCourseKpointPage(Condition.getPage(query), courseKpoint);
		return R.data(pages);
	}

	/**
	 * 新增 知识点的基本信息
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入courseKpoint")
	public R save(@Valid @RequestBody CourseKpoint courseKpoint) {
		return R.status(courseKpointService.save(courseKpoint));
	}

	@PostMapping("/updateSort")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "修改排序", notes = "传入courseKpoint")
	public R udpateSort(@Valid @RequestBody List<CourseKpoint> courseKpoints) {

//		CourseKpoint ck = courseKpointService.getById(courseKpoint.getId());
//		ck.setSort(courseKpoint.getSort());
		courseKpoints.stream().map(item->{
			boolean res = courseKpointService.update(new LambdaUpdateWrapper<CourseKpoint>()
					.eq(CourseKpoint::getId,item.getId())
					.set(CourseKpoint::getSort,item.getSort()));
			return item;
		}).collect(Collectors.toSet());

		return R.status(true);
	}

	@PostMapping("/unLock")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入courseKpoint")
	public R unLock(@Valid @RequestBody CourseKpoint courseKpoint) {

	    CourseKpoint courseKpoint1 = 	courseKpointService.getById(courseKpoint.getId());
		courseKpoint1.setIsLock(courseKpoint.getIsLock());
		return R.status(courseKpointService.updateById(courseKpoint1));
	}


	@PostMapping("/updateMaterials")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入courseKpoint")
	public R updateMaterials(@RequestBody CourseKpoint courseKpoint) {

		CourseKpoint courseKpoint1 = 	courseKpointService.getById(courseKpoint.getId());
		courseKpoint1.setMaterialIds(courseKpoint.getMaterialIds());
		return R.status(courseKpointService.updateById(courseKpoint1));
	}
	/**
	 * 修改 知识点的基本信息
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入courseKpoint")
	public R update(@Valid @RequestBody CourseKpoint courseKpoint) {
		//判断如果为百家直播则创建房间
		long liveBegin =0L;
		long liveEnd=0L;
		if(Func.equals(courseKpoint.getVideoType(),"aliyunlive")||Func.equals(courseKpoint.getVideoType(),"aliyunReplay")||Func.equals(courseKpoint.getVideoType(),"baijiayunlive")||Func.equals(courseKpoint.getVideoType(),"baijiayunReplay")||Func.equals(courseKpoint.getVideoType(),"polyvlive")||Func.equals(courseKpoint.getVideoType(),"polyvReplay")||Func.equals(courseKpoint.getVideoType(),"otherlive")){
			liveBegin=courseKpoint.getLiveBeginTime().toEpochSecond(ZoneOffset.of("+8"));
			liveEnd=courseKpoint.getLiveEndTime().toEpochSecond(ZoneOffset.of("+8"));
			courseKpoint.setVideoTime((liveEnd-liveBegin)+"");
		}else {
			if(courseKpoint.getParentId()!=0){
				if(Func.isNotEmpty(courseKpoint.getVideoTime())&&!("").equals(courseKpoint.getVideoTime())&&!("0").equals(courseKpoint.getVideoTime())){
					int mintime =Integer.valueOf(courseKpoint.getVideoTime().substring(0,courseKpoint.getVideoTime().indexOf(":")));
					int sintime =Integer.valueOf(courseKpoint.getVideoTime().substring(courseKpoint.getVideoTime().lastIndexOf(":")+1));
					String times=Integer.toString(mintime*60+sintime);
					courseKpoint.setVideoTime(times);
				}
			}
		}
/*		if(Func.equals(courseKpoint.getVideoType(),"baijiayunlive")){
			if((liveEnd-liveBegin)>= 86400){
				return R.fail("直播时长不能大于24小时");
			}
			Map room = BajiayunUtil.updateRoom(courseKpoint.getVideoUrl(),courseKpoint.getName(), liveBegin, liveEnd);
		}*/
		return R.status(courseKpointService.updateById(courseKpoint));
	}

	/**
	 * 新增或修改 知识点的基本信息
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入courseKpoint")
	public R submit(@Valid @RequestBody CourseKpoint courseKpoint) {
		if(Func.isNull(courseKpoint.getParentId())){
			courseKpoint.setParentId(0);
		}
		long liveBegin =0L;
		long liveEnd=0L;
		if(Func.equals(courseKpoint.getVideoType(),"aliyunlive")||Func.equals(courseKpoint.getVideoType(),"aliyunReplay")||Func.equals(courseKpoint.getVideoType(),"baijiayunlive")||Func.equals(courseKpoint.getVideoType(),"baijiayunReplay")||Func.equals(courseKpoint.getVideoType(),"polyvlive")||Func.equals(courseKpoint.getVideoType(),"polyvReplay")||Func.equals(courseKpoint.getVideoType(),"otherlive")){
			liveBegin=courseKpoint.getLiveBeginTime().toEpochSecond(ZoneOffset.of("+8"));
			liveEnd=courseKpoint.getLiveEndTime().toEpochSecond(ZoneOffset.of("+8"));
			courseKpoint.setVideoTime((liveEnd-liveBegin)+"");
		}else {
			if(Func.isNotEmpty(courseKpoint.getVideoTime())){
				int mintime =Integer.valueOf(courseKpoint.getVideoTime().substring(0,courseKpoint.getVideoTime().indexOf(":")));
				int sintime =Integer.valueOf(courseKpoint.getVideoTime().substring(courseKpoint.getVideoTime().lastIndexOf(":")+1));
				String times=Integer.toString(mintime*60+sintime);
				courseKpoint.setVideoTime(times);
			}
		}
		//判断如果为百家直播则创建房间
/*		if(Func.equals(courseKpoint.getVideoType(),"baijiayunlive")){
			if((liveEnd-liveBegin)>= 86400){
				return R.fail("直播时长不能大于24小时");
			}
			Map room = BajiayunUtil.createRoom(courseKpoint.getName(), liveBegin, liveEnd);
			courseKpoint.setVideoUrl(Func.toStr(room.get("room_id")));

		}*/
		return R.status(courseKpointService.saveOrUpdate(courseKpoint));
	}


	/**
	 * 新增或修改 教学模块
	 */
	@PostMapping("/submitMk")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "模块")
	public R submitMk(@Valid @RequestBody CourseKpoint courseKpoint) {
		if(Func.isNull(courseKpoint.getParentId())){
			courseKpoint.setParentId(0);
		}
		long liveBegin =0L;
		long liveEnd=0L;
		if(Func.equals(courseKpoint.getVideoType(),"aliyunlive")||Func.equals(courseKpoint.getVideoType(),"aliyunReplay")||Func.equals(courseKpoint.getVideoType(),"baijiayunlive")||Func.equals(courseKpoint.getVideoType(),"baijiayunReplay")||Func.equals(courseKpoint.getVideoType(),"polyvlive")||Func.equals(courseKpoint.getVideoType(),"polyvReplay")||Func.equals(courseKpoint.getVideoType(),"otherlive")){
			liveBegin=courseKpoint.getLiveBeginTime().toEpochSecond(ZoneOffset.of("+8"));
			liveEnd=courseKpoint.getLiveEndTime().toEpochSecond(ZoneOffset.of("+8"));
			courseKpoint.setVideoTime((liveEnd-liveBegin)+"");
		}else {
			if(Func.isNotEmpty(courseKpoint.getVideoTime())){
				int mintime =Integer.valueOf(courseKpoint.getVideoTime().substring(0,courseKpoint.getVideoTime().indexOf(":")));
				int sintime =Integer.valueOf(courseKpoint.getVideoTime().substring(courseKpoint.getVideoTime().lastIndexOf(":")+1));
				String times=Integer.toString(mintime*60+sintime);
				courseKpoint.setVideoTime(times);
			}
		}
		//判断如果为百家直播则创建房间
/*		if(Func.equals(courseKpoint.getVideoType(),"baijiayunlive")){
			if((liveEnd-liveBegin)>= 86400){
				return R.fail("直播时长不能大于24小时");
			}
			Map room = BajiayunUtil.createRoom(courseKpoint.getName(), liveBegin, liveEnd);
			courseKpoint.setVideoUrl(Func.toStr(room.get("room_id")));

		}*/
		return R.status(courseKpointService.saveOrUpdate(courseKpoint));
	}
	/**
	 * 删除 知识点的基本信息
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(courseKpointService.deleteIds(ids));
	}

	/**
	 * 获取菜单树形结构
	 */
	@GetMapping("/tree")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "树形结构", notes = "树形结构")
	public R<List<CourseKpointVO>> tree() {
		List<CourseKpointVO> tree = courseKpointService.tree();
		return R.data(tree);
	}


	/**
	 * 获取菜单树形结构
	 */
	@GetMapping("/treeByCourse")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "树形结构", notes = "树形结构")
	public R<Object> treeByCourse() {

		//
		List<Course> courses = courseService.list(new LambdaQueryWrapper<Course>()
				.eq(Course::getIsTemplateCourse,1));

		List<Map<String,Object>> trees = new ArrayList<Map<String,Object>>();
		courses.stream().map(item->{
			Map<String,Object> courseKpointVO = new HashMap<>();
			courseKpointVO.put("id",item.getId());
			courseKpointVO.put("key",item.getId());
			courseKpointVO.put("title",item.getCourseName());
			CourseKpoint p = new CourseKpoint();
			p.setCourseId(item.getId());
			List<CourseKpointVO> tree = courseKpointService.treeOfCourse(p);
			courseKpointVO.put("children",tree);
			trees.add(courseKpointVO);
			return item;
		}).collect(Collectors.toSet());

		return R.data(trees);
	}

	/**
	 * 获取直播地址
	 * @param streamName
	 * @return
	 */
	@RequestMapping("createUrl")
	@ResponseBody
	public R createUrl(String streamName){
		String livePushUrl = AliyunLiveUtils.createLivePushUrl(streamName);
		Map<String, String> livePullUrl = AliyunLiveUtils.createLivePullUrl(streamName);
		Map resultMap = Maps.newHashMap();
		resultMap.put("livePushUrl",livePushUrl);
		resultMap.put("livePullUrl",livePullUrl.get("m3u8Url"));
		return R.data(resultMap);
	}


	@PostMapping("/setExams")
	@ApiOperation(value = "新增或修改", notes = "传入courseKpoint")
	public R setExams(CourseKpoint courseKpoint) {

		if(StringUtil.isNotBlank(courseKpoint.getExamIds())){
			courseExamService.remove(new QueryWrapper<CourseExam>().lambda().eq(CourseExam::getKpointId,courseKpoint.getKpointId()));
			String[] ids = courseKpoint.getExamIds().split(",");

			Arrays.stream(ids).map(i->{

				CourseKpoint courseKpoint1 =  courseKpointService.getById(courseKpoint.getKpointId());
				Exampaper exampaper =  exampaperService.getById(i);
				CourseExam courseExam =new CourseExam();
				courseExam.setKpointId(courseKpoint.getKpointId());
				courseExam.setCourseId(courseKpoint1.getCourseId());
				courseExam.setExamId(exampaper.getId());
				courseExam.setExamType("test");
				courseExam.setExamName(exampaper.getName());
				courseExamService.save(courseExam);
				return i;
			}).collect(Collectors.toList());

			return R.success("ok");
		}

		return R.success("ok");

	}

	@GetMapping("/getExampaters")
	@ApiOperationSupport(order = 7)
	@ApiOperation(value = "随堂测验", notes = "随堂测验")
	public R<List<CourseExam>> getExampaters(Integer kpointId) {
		List<CourseExam> list = courseExamService.list(new QueryWrapper<CourseExam>().lambda().eq(CourseExam::getKpointId,kpointId));
		return R.data(list);
	}
}
