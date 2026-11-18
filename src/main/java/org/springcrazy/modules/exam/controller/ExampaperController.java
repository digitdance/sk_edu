
package org.springcrazy.modules.exam.controller;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springcrazy.common.tool.StringUtils;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.edu.entity.CoursePackageExam;
import org.springcrazy.modules.edu.service.ICoursePackageExamService;
import org.springcrazy.modules.edu.vo.CoursePackageExamVO;
import org.springcrazy.modules.exam.entity.ExampaperRecord;
import org.springcrazy.modules.exam.entity.PaperMiddle;
import org.springcrazy.modules.exam.entity.Question;
import org.springcrazy.modules.exam.service.IExampaperRecordService;
import org.springcrazy.modules.exam.service.IQuestionService;
import org.springcrazy.modules.exam.vo.QstmiddleVO;
import org.springcrazy.modules.exam.wrapper.QuestionWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springcrazy.modules.exam.entity.Exampaper;
import org.springcrazy.modules.exam.vo.ExampaperVO;
import org.springcrazy.modules.exam.wrapper.ExampaperWrapper;
import org.springcrazy.modules.exam.service.IExampaperService;
import org.springcrazy.core.boot.ctrl.CrazyController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考试试卷表 控制器
 *
 * @author TongZhou
 * @since 2020-12-01
 */
@RestController
@AllArgsConstructor
@RequestMapping("/exam/exampaper")
@Api(value = "考试试卷表", tags = "考试试卷表接口")
public class ExampaperController extends CrazyController {


	private ICoursePackageExamService coursePackageExamService;
	private IExampaperService exampaperService;
	private IExampaperRecordService exampaperRecordService;
	private IQuestionService questionService;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入exampaper")
	public R<ExampaperVO> detail(Exampaper exampaper) {
		Exampaper detail = exampaperService.getOne(Condition.getQueryWrapper(exampaper));
		QueryWrapper<ExampaperRecord> exampaperRecordQueryWrapper = new QueryWrapper<ExampaperRecord>();
		exampaperRecordQueryWrapper.lambda().eq(ExampaperRecord::getEpId,detail.getId());
		exampaperRecordQueryWrapper.select("max(user_score) as max","min(user_score) as min","count(DISTINCT user_id) as num","avg(user_score) as avg");
		Map<String, Object> map = exampaperRecordService.getMap(exampaperRecordQueryWrapper);
		ExampaperVO exampaperVO = ExampaperWrapper.build().entityVO(detail);
		exampaperVO.setMaxScore(Func.toStr(map.get("max"),"0"));
		exampaperVO.setMinScore(Func.toStr(map.get("min"),"0"));
		exampaperVO.setActualJoinNum(Func.toInt(map.get("num"),0));
		exampaperVO.setAvgScore(new BigDecimal(Func.toStr(map.get("avg"),"0")));
		return R.data(exampaperVO);
	}

	/**
	 * 分页 考试试卷表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入exampaper")
	public R<IPage<ExampaperVO>> list(Exampaper exampaper, Query query,String sort) {
		String name = exampaper.getName();
		exampaper.setName(null);
		QueryWrapper<Exampaper> queryWrapper = Condition.getQueryWrapper(exampaper);
		/*
			if(Func.equals(sort,"desc")){
				queryWrapper.lambda().orderByDesc(Exampaper::getSortNum);
			}else{
				queryWrapper.lambda().orderByAsc(Exampaper::getSortNum);
			}
		*/
		queryWrapper.lambda().like(Func.isNotBlank(name),Exampaper::getName,name).ne(Exampaper::getStatus,"delete");

		if(StringUtils.isNotBlank(exampaper.getKpointIdNull())){
			queryWrapper.lambda().isNull(Exampaper::getKpointId);
		}

//		if(StringUtils.isNotBlank(exampaper.getKpointIdNull())){
//			queryWrapper.lambda().isNull(Exampaper::getKpointId);
//		}

		if(StringUtils.isNotBlank(exampaper.getSubjectIds())){
			queryWrapper.lambda().in(Exampaper::getSubjectId,exampaper.getSubjectIds().split(","));
		}
		IPage<Exampaper> pages = exampaperService.page(Condition.getPage(query),queryWrapper );
		return R.data(ExampaperWrapper.build().pageVO(pages));
	}


	/**
	 * 自定义分页 考试试卷表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入exampaper")
	public R<IPage<ExampaperVO>> page(ExampaperVO exampaper, Query query) {
		IPage<ExampaperVO> pages = exampaperService.selectExampaperPage(Condition.getPage(query), exampaper);
		return R.data(pages);
	}


	@GetMapping("/selectClazzExamList")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入exampaper")
	public R<IPage<ExampaperVO>> selectClazzExamList(ExampaperVO exampaper, Query query) {


		IPage<ExampaperVO> pages = exampaperService.selectClazzExamList(Condition.getPage(query), exampaper);
		return R.data(pages);
	}



	/**
	 * 新增 考试试卷表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入exampaper")
	public R save(@Valid @RequestBody Exampaper exampaper) {
		exampaper.setAddTime(LocalDateTime.now());
		exampaper.setUpdateTime(LocalDateTime.now());
		exampaper.setCreaterId(SecureUtil.getUserId());
		exampaper.setCreaterName(SecureUtil.getUserName());
		return R.status(exampaperService.save(exampaper));
	}


	/**
	 * 详情
	 */
	@GetMapping("/detailByCourseKpoint")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入exampaper")
	public R<ExampaperVO> detailByCourseKpoint(Exampaper exampaper) {

		Exampaper detail = 	exampaperService.getOne(new LambdaQueryWrapper<Exampaper>()
				.eq(Exampaper::getCourseId,exampaper.getCourseId())
				.eq(Exampaper::getKpointId,exampaper.getKpointId()));
		if(detail!=null){
			QueryWrapper<ExampaperRecord> exampaperRecordQueryWrapper = new QueryWrapper<ExampaperRecord>();
			exampaperRecordQueryWrapper.lambda().eq(ExampaperRecord::getEpId,detail.getId());
			exampaperRecordQueryWrapper.select("max(user_score) as max","min(user_score) as min","count(DISTINCT user_id) as num","avg(user_score) as avg");
			Map<String, Object> map = exampaperRecordService.getMap(exampaperRecordQueryWrapper);
			ExampaperVO exampaperVO = ExampaperWrapper.build().entityVO(detail);
			exampaperVO.setMaxScore(Func.toStr(map.get("max"),"0"));
			exampaperVO.setMinScore(Func.toStr(map.get("min"),"0"));
			exampaperVO.setActualJoinNum(Func.toInt(map.get("num"),0));
			exampaperVO.setAvgScore(new BigDecimal(Func.toStr(map.get("avg"),"0")));
			return R.data(exampaperVO);
		}else{
			return R.data(null);
		}

	}

	/**
	 * 新增 课堂测试
	 */
	@PostMapping("/save2")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入exampaper")
	public R save2(@Valid @RequestBody Exampaper exampaper) {

		CoursePackageExamVO coursePackageExamVO = new CoursePackageExamVO();
		coursePackageExamVO.setKpointId(exampaper.getKpointId());
		coursePackageExamVO.setCourseId(exampaper.getCourseId());
		coursePackageExamVO.setExamType(exampaper.getType());
		List<CoursePackageExamVO> exams =  coursePackageExamService.selectCoursePackageExamList(coursePackageExamVO);
		List<PaperMiddle> middles= new ArrayList<PaperMiddle>();
		if(exams.size()==0){
			exampaper.setAddTime(LocalDateTime.now());
			exampaper.setUpdateTime(LocalDateTime.now());
			exampaperService.save(exampaper);
			exampaper.setMiddles(middles);

			CoursePackageExam coursePackageExam = new CoursePackageExam();
			coursePackageExam.setCourseId(exampaper.getCourseId());
			coursePackageExam.setKpointId(exampaper.getKpointId());
			coursePackageExam.setExampaperId(exampaper.getId());
			coursePackageExam.setCustomExampaperName(exampaper.getName());
			coursePackageExam.setExamType(exampaper.getType());
			coursePackageExamService.save(coursePackageExam);

			return R.data(exampaper);
		}else{
			Exampaper exampaper1 = 	exampaperService.getById(exams.get(0).getExampaperId());
			exampaper1.setMiddles(exampaperService.queryQuestion(exampaper1.getId()+""));
			return R.data(exampaper1);
		}

//		Exampaper exampaper1 = 	exampaperService.getOne(new LambdaQueryWrapper<Exampaper>()
//				.eq(Exampaper::getCourseId,exampaper.getCourseId())
//				.eq(Exampaper::getKpointId,exampaper.getKpointId()));
//
//		if(exampaper1==null){
//			exampaper.setAddTime(LocalDateTime.now());
//			exampaper.setUpdateTime(LocalDateTime.now());
//			exampaperService.save(exampaper);
//			exampaper.setMiddles(middles);
//			return R.data(exampaper1);
//		}else{
//			exampaper1.setMiddles(exampaperService.queryQuestion(exampaper1.getId()+""));
//			return R.data(exampaper1);
//		}

	}

//
//	@PostMapping("/getDetails")
//	@ApiOperationSupport(order = 4)
//	@ApiOperation(value = "新增", notes = "传入exampaper")
//	public R getDetails(@Valid @RequestBody Exampaper exampaper) {
//
//		Exampaper exampaper1 = 	exampaperService.getById(exampaper.getExampaperId());
//		exampaper1.setMiddles(exampaperService.queryQuestion(exampaper1.getId()+""));
//		return R.data(exampaper1);
//	}


	/**
	 * 修改 考试试卷表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入exampaper")
	public R update(@Valid @RequestBody Exampaper exampaper) {
		exampaper.setUpdateTime(LocalDateTime.now());
		return R.status(exampaperService.updateById(exampaper));
	}

	/**
	 * 新增或修改 考试试卷表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入exampaper")
	public R submit(@Valid @RequestBody Exampaper exampaper) {
		return R.status(exampaperService.saveOrUpdate(exampaper));
	}


	/**
	 * 删除 考试试卷表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		List<Exampaper> exampapers = exampaperService.listByIds(Func.toIntList(ids));
		for (Exampaper exampaper : exampapers) {
			exampaper.setStatus("delete");
		}
		return R.status(exampaperService.updateBatchById(exampapers));
	}

	/**
	 * 新增保存试题
	 */
	@PostMapping("/saveQuestion")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "保存试题", notes = "保存试题")
	public R saveQuestion(@Valid @RequestBody List<PaperMiddle> exampaperList) {
		exampaperService.insertQuestion(exampaperList);
		return R.status(true);
	}

	/**
	 * 新增保存试题
	 */
	@PostMapping("/saveQuestion/{paperId}")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "保存试题", notes = "保存试题")
	public R saveQuestionWithPaperId(@Valid @RequestBody List<PaperMiddle> exampaperList,@PathVariable Integer paperId) {
		exampaperService.insertQuestionWithPaperId(exampaperList,paperId);
		return R.status(true);
	}

	@PostMapping("/genQuestion")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "保存试题", notes = "智能组卷")
	public R genQuestion(@Valid @RequestBody PaperMiddle paperMiddle) {


		//queryWrapper.lambda().like(Func.isNotBlank(qstContent),Question::getQstContent,qstContent);
		//hard
		final int[] index = {0};
		List<QstmiddleVO> actQuestionArr = new ArrayList<QstmiddleVO>();
		List<Question> hards = null;
		if(paperMiddle.getHardNum()!=null&&paperMiddle.getHardNum()>0){
			QueryWrapper<Question> hardQueryWrapper = new QueryWrapper<Question>();
			if(Func.isNotBlank(paperMiddle.getSubjectIds())){
				hardQueryWrapper.lambda().in(Question::getSubjectId,(paperMiddle.getSubjectIds().split(",")));
			}
			hardQueryWrapper.lambda().eq(Question::getLevel,3);
			hardQueryWrapper.lambda().last("ORDER BY RAND() LIMIT " + paperMiddle.getHardNum());
			Query hardQuery = new Query();
			hardQuery.setSize(paperMiddle.getHardNum());
			hards = questionService.list(hardQueryWrapper);

			hards.stream().map(item->{
				QstmiddleVO qstmiddleVO = new QstmiddleVO();
				BeanUtils.copyProperties(item,qstmiddleVO);
				qstmiddleVO.setPaperId(paperMiddle.getPaperId());
				qstmiddleVO.setId(null);
				qstmiddleVO.setQstId(item.getId());
				qstmiddleVO.setQstLevel(item.getLevel());
				qstmiddleVO.setQstType(item.getQstType());
				qstmiddleVO.setPapermiddleId(paperMiddle.getId());
				actQuestionArr.add(qstmiddleVO);
				index[0]++;
				return item;
			}).collect(Collectors.toList());
		}

		List<Question> normals = null;
		if(paperMiddle.getNormalNum()!=null&&paperMiddle.getNormalNum()>0){
			QueryWrapper<Question> normalQueryWrapper = new QueryWrapper<Question>();
			if(Func.isNotBlank(paperMiddle.getSubjectIds())){
				normalQueryWrapper.lambda().in(Question::getSubjectId,(paperMiddle.getSubjectIds().split(",")));
			}
			normalQueryWrapper.lambda().eq(Question::getLevel,2);
			normalQueryWrapper.lambda().last("ORDER BY RAND() LIMIT " + paperMiddle.getNormalNum());
			Query normalQuery = new Query();
			normalQuery.setSize(paperMiddle.getNormalNum());
			normals = questionService.list(normalQueryWrapper);
			normals.stream().map(item->{
				QstmiddleVO qstmiddleVO = new QstmiddleVO();
				BeanUtils.copyProperties(item,qstmiddleVO);
				qstmiddleVO.setPaperId(paperMiddle.getPaperId());
				qstmiddleVO.setId(null);
				qstmiddleVO.setQstId(item.getId());
				qstmiddleVO.setQstLevel(item.getLevel());
				qstmiddleVO.setQstType(item.getQstType());
				qstmiddleVO.setPapermiddleId(paperMiddle.getId());
				actQuestionArr.add(qstmiddleVO);
				index[0]++;
				return item;
			}).collect(Collectors.toList());
		}

		List<Question> simples = null;
		if(paperMiddle.getSimpleNum()!=null&&paperMiddle.getSimpleNum()>0){
			QueryWrapper<Question> simpleQueryWrapper = new QueryWrapper<Question>();
			if(Func.isNotBlank(paperMiddle.getSubjectIds())){
				simpleQueryWrapper.lambda().in(Question::getSubjectId,(paperMiddle.getSubjectIds().split(",")));
			}
			simpleQueryWrapper.lambda().eq(Question::getLevel,1);
			simpleQueryWrapper.lambda().last("ORDER BY RAND() LIMIT " + paperMiddle.getSimpleNum());
			Query simpleQuery = new Query();
			simpleQuery.setSize(paperMiddle.getSimpleNum());
			simples = questionService.list(simpleQueryWrapper);
			simples.stream().map(item->{
				QstmiddleVO qstmiddleVO = new QstmiddleVO();
				BeanUtils.copyProperties(item,qstmiddleVO);
				qstmiddleVO.setPaperId(paperMiddle.getPaperId());
				qstmiddleVO.setId(null);
				qstmiddleVO.setQstId(item.getId());
				qstmiddleVO.setQstLevel(item.getLevel());
				qstmiddleVO.setQstType(item.getQstType());
				qstmiddleVO.setPapermiddleId(paperMiddle.getId());
				actQuestionArr.add(qstmiddleVO);
				index[0]++;
				return item;
			}).collect(Collectors.toList());
		}
		paperMiddle.setQuestionArr(actQuestionArr);

		return R.data(actQuestionArr);
	}

	@PostMapping("/genOneQuestion")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "保存试题", notes = "智能组卷")
	public R genOneQuestion(@Valid @RequestBody PaperMiddle paperMiddle) {
		//queryWrapper.lambda().like(Func.isNotBlank(qstContent),Question::getQstContent,qstContent);
		//hard
		final int[] index = {0};
		List<QstmiddleVO> actQuestionArr = new ArrayList<QstmiddleVO>();
		List<Question> hards = null;
		if(paperMiddle.getHardNum()!=null&&paperMiddle.getHardNum()>0){
			QueryWrapper<Question> hardQueryWrapper = new QueryWrapper<Question>();
			if(Func.isNotBlank(paperMiddle.getSubjectIds())){
				hardQueryWrapper.lambda().in(Question::getSubjectId,(paperMiddle.getSubjectIds().split(",")));
			}
			hardQueryWrapper.lambda().eq(Question::getLevel,3);
			hardQueryWrapper.lambda().last("ORDER BY RAND() LIMIT " + paperMiddle.getHardNum());
			hardQueryWrapper.lambda().notIn(Question::getId,paperMiddle.getIds().split(","));
			Query hardQuery = new Query();
			hardQuery.setSize(paperMiddle.getHardNum());
			hards = questionService.list(hardQueryWrapper);

			hards.stream().map(item->{
				QstmiddleVO qstmiddleVO = new QstmiddleVO();
				BeanUtils.copyProperties(item,qstmiddleVO);
				qstmiddleVO.setPaperId(paperMiddle.getPaperId());
				qstmiddleVO.setId(null);
				qstmiddleVO.setQstId(item.getId());
				qstmiddleVO.setQstLevel(item.getLevel());
				qstmiddleVO.setQstType(item.getQstType());
				qstmiddleVO.setPapermiddleId(paperMiddle.getId());
				actQuestionArr.add(qstmiddleVO);
				index[0]++;
				return item;
			}).collect(Collectors.toList());
		}

		List<Question> normals = null;
		if(paperMiddle.getNormalNum()!=null&&paperMiddle.getNormalNum()>0){
			QueryWrapper<Question> normalQueryWrapper = new QueryWrapper<Question>();
			if(Func.isNotBlank(paperMiddle.getSubjectIds())){
				normalQueryWrapper.lambda().in(Question::getSubjectId,(paperMiddle.getSubjectIds().split(",")));
			}
			normalQueryWrapper.lambda().eq(Question::getLevel,2);
			normalQueryWrapper.lambda().last("ORDER BY RAND() LIMIT " + paperMiddle.getNormalNum());
			normalQueryWrapper.lambda().notIn(Question::getId,paperMiddle.getIds().split(","));
			Query normalQuery = new Query();
			normalQuery.setSize(paperMiddle.getNormalNum());
			normals = questionService.list(normalQueryWrapper);

			normals.stream().map(item->{
				QstmiddleVO qstmiddleVO = new QstmiddleVO();
				BeanUtils.copyProperties(item,qstmiddleVO);
				qstmiddleVO.setPaperId(paperMiddle.getPaperId());
				qstmiddleVO.setId(null);
				qstmiddleVO.setQstId(item.getId());
				qstmiddleVO.setQstLevel(item.getLevel());
				qstmiddleVO.setQstType(item.getQstType());
				qstmiddleVO.setPapermiddleId(paperMiddle.getId());
				actQuestionArr.add(qstmiddleVO);
				index[0]++;
				return item;
			}).collect(Collectors.toList());
		}

		List<Question> simples = null;
		if(paperMiddle.getSimpleNum()!=null&&paperMiddle.getSimpleNum()>0){
			QueryWrapper<Question> simpleQueryWrapper = new QueryWrapper<Question>();
			if(Func.isNotBlank(paperMiddle.getSubjectIds())){
				simpleQueryWrapper.lambda().in(Question::getSubjectId,(paperMiddle.getSubjectIds().split(",")));
			}
			simpleQueryWrapper.lambda().eq(Question::getLevel,1);
			simpleQueryWrapper.lambda().last("ORDER BY RAND() LIMIT " + paperMiddle.getSimpleNum());
			simpleQueryWrapper.lambda().notIn(Question::getId,paperMiddle.getIds().split(","));
			Query simpleQuery = new Query();
			simpleQuery.setSize(paperMiddle.getSimpleNum());

			simples = questionService.list(simpleQueryWrapper);
			simples.stream().map(item->{
				QstmiddleVO qstmiddleVO = new QstmiddleVO();
				BeanUtils.copyProperties(item,qstmiddleVO);
				qstmiddleVO.setPaperId(paperMiddle.getPaperId());
				qstmiddleVO.setId(null);
				qstmiddleVO.setQstId(item.getId());
				qstmiddleVO.setQstLevel(item.getLevel());
				qstmiddleVO.setQstType(item.getQstType());
				qstmiddleVO.setPapermiddleId(paperMiddle.getId());
				actQuestionArr.add(qstmiddleVO);
				index[0]++;
				return item;
			}).collect(Collectors.toList());
		}
		paperMiddle.setQuestionArr(actQuestionArr);

		return R.data(actQuestionArr);
	}
//
//	@PostMapping("/changeQuestion")
//	@ApiOperationSupport(order = 6)
//	@ApiOperation(value = "保存试题", notes = "智能组卷")
//	public R genQuestion(@Valid @RequestBody PaperMiddle paperMiddle) {
//
//
//		//queryWrapper.lambda().like(Func.isNotBlank(qstContent),Question::getQstContent,qstContent);
//		//hard
//		final int[] index = {0};
//		List<QstmiddleVO> actQuestionArr = new ArrayList<QstmiddleVO>();
//		List<Question> hards = null;
//		if(paperMiddle.getHardNum()>0){
//			QueryWrapper<Question> hardQueryWrapper = new QueryWrapper<Question>();
//			if(Func.isNotBlank(paperMiddle.getSubjectIds())){
//				hardQueryWrapper.lambda().in(Question::getSubjectId,(paperMiddle.getSubjectIds().split(",")));
//			}
//			hardQueryWrapper.lambda().eq(Question::getLevel,3);
//			hardQueryWrapper.lambda().last("ORDER BY RAND() LIMIT " + paperMiddle.getNormalNum());
//			Query hardQuery = new Query();
//			hardQuery.setSize(paperMiddle.getHardNum());
//			hards = questionService.list(hardQueryWrapper);
//
//			hards.stream().map(item->{
//				QstmiddleVO qstmiddleVO = new QstmiddleVO();
//				BeanUtils.copyProperties(item,qstmiddleVO);
//				qstmiddleVO.setPaperId(paperMiddle.getPaperId());
//				qstmiddleVO.setId(null);
//				qstmiddleVO.setQstId(item.getId());
//				qstmiddleVO.setQstLevel(item.getLevel());
//				qstmiddleVO.setQstType(item.getQstType());
//				qstmiddleVO.setPapermiddleId(paperMiddle.getId());
//				actQuestionArr.add(qstmiddleVO);
//				index[0]++;
//				return item;
//			}).collect(Collectors.toList());
//		}
//
//		List<Question> normals = null;
//		if(paperMiddle.getNormalNum()>0){
//			QueryWrapper<Question> normalQueryWrapper = new QueryWrapper<Question>();
//			if(Func.isNotBlank(paperMiddle.getSubjectIds())){
//				normalQueryWrapper.lambda().in(Question::getSubjectId,(paperMiddle.getSubjectIds().split(",")));
//			}
//			normalQueryWrapper.lambda().eq(Question::getLevel,2);
//			normalQueryWrapper.lambda().last("ORDER BY RAND() LIMIT " + paperMiddle.getNormalNum());
//			Query normalQuery = new Query();
//			normalQuery.setSize(paperMiddle.getNormalNum());
//			normals = questionService.list(normalQueryWrapper);
//			normals.stream().map(item->{
//				QstmiddleVO qstmiddleVO = new QstmiddleVO();
//				BeanUtils.copyProperties(item,qstmiddleVO);
//				qstmiddleVO.setPaperId(paperMiddle.getPaperId());
//				qstmiddleVO.setId(null);
//				qstmiddleVO.setQstId(item.getId());
//				qstmiddleVO.setQstLevel(item.getLevel());
//				qstmiddleVO.setQstType(item.getQstType());
//				qstmiddleVO.setPapermiddleId(paperMiddle.getId());
//				actQuestionArr.add(qstmiddleVO);
//				index[0]++;
//				return item;
//			}).collect(Collectors.toList());
//		}
//
//		List<Question> simples = null;
//		if(paperMiddle.getNormalNum()>0){
//			QueryWrapper<Question> simpleQueryWrapper = new QueryWrapper<Question>();
//			if(Func.isNotBlank(paperMiddle.getSubjectIds())){
//				simpleQueryWrapper.lambda().in(Question::getSubjectId,(paperMiddle.getSubjectIds().split(",")));
//			}
//			simpleQueryWrapper.lambda().eq(Question::getLevel,1);
//			simpleQueryWrapper.lambda().last("ORDER BY RAND() LIMIT " + paperMiddle.getNormalNum());
//			Query simpleQuery = new Query();
//			simpleQuery.setSize(paperMiddle.getSimpleNum());
//			simples = questionService.list(simpleQueryWrapper);
//			simples.stream().map(item->{
//				QstmiddleVO qstmiddleVO = new QstmiddleVO();
//				BeanUtils.copyProperties(item,qstmiddleVO);
//				qstmiddleVO.setPaperId(paperMiddle.getPaperId());
//				qstmiddleVO.setId(null);
//				qstmiddleVO.setQstId(item.getId());
//				qstmiddleVO.setQstLevel(item.getLevel());
//				qstmiddleVO.setQstType(item.getQstType());
//				qstmiddleVO.setPapermiddleId(paperMiddle.getId());
//				actQuestionArr.add(qstmiddleVO);
//				index[0]++;
//				return item;
//			}).collect(Collectors.toList());
//		}
//		paperMiddle.setQuestionArr(actQuestionArr);
//
//		return R.data(actQuestionArr);
//	}

	/**
	 * 根据试卷id查询试题
	 */
	@GetMapping("/queryQuestion")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "根据试卷id查询试题", notes = "根据试卷id查询试题")
	public R queryQuestion(String id) {
		return R.data(exampaperService.queryQuestion(id));
	}
}
