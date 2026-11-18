
package org.springcrazy.modules.exam.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;
import javax.validation.Valid;

import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.edu.entity.ScoreRecord;
import org.springcrazy.modules.edu.mapper.ScoreRecordMapper;
import org.springcrazy.modules.exam.entity.Exampaper;
import org.springcrazy.modules.exam.entity.ExampaperRecord;
import org.springcrazy.modules.exam.entity.Question;
import org.springcrazy.modules.exam.service.IExampaperRecordService;
import org.springcrazy.modules.exam.service.IExampaperService;
import org.springcrazy.modules.exam.service.IQuestionService;
import org.springcrazy.modules.exam.vo.ExampaperRecordVO;
import org.springcrazy.modules.exam.wrapper.ExampaperRecordWrapper;
import org.springframework.web.bind.annotation.*;
import org.springcrazy.modules.exam.entity.QuestionRecord;
import org.springcrazy.modules.exam.vo.QuestionRecordVO;
import org.springcrazy.modules.exam.wrapper.QuestionRecordWrapper;
import org.springcrazy.modules.exam.service.IQuestionRecordService;
import org.springcrazy.core.boot.ctrl.CrazyController;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考试试题记录表 控制器
 *
 * @author TongZhou
 * @since 2021-01-05
 */
@RestController
@AllArgsConstructor
@RequestMapping("/exam/questionrecord")
@Api(value = "考试试题记录表", tags = "考试试题记录表接口")
public class QuestionRecordController extends CrazyController {
	private IExampaperRecordService exampaperRecordService;
	private IQuestionRecordService questionRecordService;
	private IQuestionService questionService;
	private ScoreRecordMapper scoreRecordMapper;
	private IExampaperService exampaperService;

	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入questionRecord")
	public R<QuestionRecord> detail(QuestionRecord questionRecord) {
		QuestionRecord detail = questionRecordService.getById(questionRecord.getId());
	    Question question =  questionService.getById(detail.getQstId());

		QuestionRecordVO questionRecordVO = QuestionRecordWrapper.build().entityVO(detail);
		if(question!=null){
			questionRecordVO.setStandard(question.getEvaluationStandards());
		}
		return R.data(questionRecordVO);
	}


	@PostMapping("/pigai")
	public R<ExampaperRecordVO> pigai(@RequestBody  ExampaperRecordVO exampaperRecord) {

		QuestionRecord detail = questionRecordService.getById(exampaperRecord.getId());
		ExampaperRecord exampaperRecord1 =  exampaperRecordService.getById(detail.getPaperRecordId());
		exampaperRecord1.setUserScore(exampaperRecord.getUserScore());
		exampaperRecordService.updateById(exampaperRecord1);

		detail.setPigaiUser(SecureUtil.getUserName());
		detail.setPigaiTime(LocalDateTime.now());
		questionRecordService.updateById(detail);

		Question question =  questionService.getById(detail.getQstId());
		String type = "exam";
		if(question.getQstType().equals(6)){
			type = "shixun";
		}
		List<ScoreRecord> scoreRecords =  scoreRecordMapper.selectList(new LambdaQueryWrapper<ScoreRecord>()
				.eq(ScoreRecord::getUserId, SecureUtil.getUserId())
				.eq(ScoreRecord::getType,type)
				.eq(ScoreRecord::getBizId,exampaperRecord1.getEpId()));
		if(scoreRecords.size()==0){
			List<Exampaper> records =  exampaperService.list(new LambdaQueryWrapper<Exampaper>()
					.eq(Exampaper::getId,detail.getPaperId()));
			ScoreRecord scoreRecord = new ScoreRecord();
			scoreRecord.setType(type);
			scoreRecord.setCourseId(records.get(0).getCourseId());
			scoreRecord.setClazzId(records.get(0).getClazzId());
			scoreRecord.setTitle(records.get(0).getExamName()+"成绩");
			scoreRecord.setBizId(exampaperRecord1.getEpId());
			scoreRecord.setUserId(SecureUtil.getUserId());
			scoreRecord.setScore(exampaperRecord1.getUserScore());
			scoreRecordMapper.insert(scoreRecord);
		}else{
			if(scoreRecords.get(0).getScore().compareTo(new BigDecimal(exampaperRecord.getUserScore()+""))==-1){
				scoreRecords.get(0).setScore(exampaperRecord1.getUserScore());
				scoreRecordMapper.updateById(scoreRecords.get(0));
			}
		}
		//ExampaperRecord detail = exampaperRecordService.getOne(Condition.getQueryWrapper(exampaperRecord));
		return R.data(ExampaperRecordWrapper.build().entityVO(exampaperRecord1));
	}

	/**
	 * 分页 考试试题记录表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入questionRecord")
	public R<IPage<QuestionRecordVO>> list(QuestionRecord questionRecord, Query query) {
		IPage<QuestionRecord> pages = questionRecordService.page(Condition.getPage(query), Condition.getQueryWrapper(questionRecord));
		return R.data(QuestionRecordWrapper.build().pageVO(pages));
	}



//	/**
//	 * 分页 考试试题记录表
//	 */
//	@GetMapping("/list")
//	@ApiOperationSupport(order = 2)
//	@ApiOperation(value = "分页", notes = "传入questionRecord")
//	public R<IPage<QuestionRecordVO>> list(QuestionRecord questionRecord, Query query) {
//		IPage<QuestionRecord> pages = questionRecordService.page(Condition.getPage(query), Condition.getQueryWrapper(questionRecord));
//		return R.data(QuestionRecordWrapper.build().pageVO(pages));
//	}


	/**
	 * 自定义分页 考试试题记录表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入questionRecord")
	public R<IPage<Map<String,Object>>> page(QuestionRecordVO questionRecord, Query query) {
		IPage<Map<String,Object>> pages = questionRecordService.selectQuestionRecordsPage(Condition.getPage(query), questionRecord);
		return R.data(pages);
	}

	@GetMapping("/uniqPage")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入questionRecord")
	public R<IPage<Map<String,Object>>> uniqPage(QuestionRecordVO questionRecord, Query query) {
		IPage<Map<String,Object>> pages = questionRecordService.selectQuestionRecordsPageUniq(Condition.getPage(query), questionRecord);
		return R.data(pages);
	}

	/**
	 * 新增 考试试题记录表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入questionRecord")
	public R save(@Valid @RequestBody QuestionRecord questionRecord) {
		return R.status(questionRecordService.save(questionRecord));
	}

	/**
	 * 修改 考试试题记录表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入questionRecord")
	public R update(@Valid @RequestBody QuestionRecord questionRecord) {
		questionRecord.setPigaiTime(LocalDateTime.now());
		questionRecord.setPigaiUser(SecureUtil.getUserName());

		return R.status(questionRecordService.updateById(questionRecord));
	}

	/**
	 * 新增或修改 考试试题记录表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入questionRecord")
	public R submit(@Valid @RequestBody QuestionRecord questionRecord) {

		if(questionRecord.getId()!=null){
			questionRecord.setPigaiTime(LocalDateTime.now());
			questionRecord.setPigaiUser(SecureUtil.getUserName());
		}
		questionRecordService.saveOrUpdate(questionRecord);
		ExampaperRecord exampaperRecord =  exampaperRecordService.getById(questionRecord.getPaperRecordId());
		exampaperRecord.setPigaiStatus(1);


		List<QuestionRecord> questionRecordList =  questionRecordService.list(new LambdaQueryWrapper<QuestionRecord>()
				.eq(QuestionRecord::getPaperRecordId,questionRecord.getPaperRecordId()));
		final BigDecimal[] score = {new BigDecimal(0)};
		questionRecordList.stream().map(item->{
			if(item.getGiveScore()!=null){
				score[0] = score[0].add(item.getGiveScore());
			}
			return item;
		}).collect(Collectors.toSet());
		exampaperRecord.setUserScore(score[0]);
		exampaperRecordService.updateById(exampaperRecord);
		return R.status(true);
	}


	/**
	 * 删除 考试试题记录表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(questionRecordService.removeByIds(Func.toIntList(ids)));
	}


}
