
package org.springcrazy.modules.exam.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import lombok.AllArgsConstructor;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import lombok.SneakyThrows;
import org.apache.commons.codec.Charsets;
import org.springcrazy.common.tool.StringUtils;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.modules.edu.entity.ScoreRecord;
import org.springcrazy.modules.edu.entity.TrxorderDetail;
import org.springcrazy.modules.edu.mapper.ScoreRecordMapper;
import org.springcrazy.modules.edu.service.ITrxorderDetailService;
import org.springcrazy.modules.exam.entity.Exampaper;
import org.springcrazy.modules.exam.entity.QuestionRecord;
import org.springcrazy.modules.exam.service.IExampaperService;
import org.springcrazy.modules.exam.service.IQuestionService;
import org.springframework.web.bind.annotation.*;
import org.springcrazy.modules.exam.entity.ExampaperRecord;
import org.springcrazy.modules.exam.vo.ExampaperRecordVO;
import org.springcrazy.modules.exam.wrapper.ExampaperRecordWrapper;
import org.springcrazy.modules.exam.service.IExampaperRecordService;
import org.springcrazy.core.boot.ctrl.CrazyController;
import springfox.documentation.annotations.ApiIgnore;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考试记录表 控制器
 *
 * @author TongZhou
 * @since 2020-12-02
 */
@RestController
@AllArgsConstructor
@RequestMapping("/exam/exampaperrecord")
@Api(value = "考试记录表", tags = "考试记录表接口")
public class ExampaperRecordController extends CrazyController {

	private IExampaperRecordService exampaperRecordService;
	private ITrxorderDetailService trxorderDetailService;
	private ScoreRecordMapper scoreRecordMapper;
	private IExampaperService exampaperService;
	private IQuestionService questionService;
	/**
	 * 详情
	 */
	@GetMapping("/detail")
	@ApiOperationSupport(order = 1)
	@ApiOperation(value = "详情", notes = "传入exampaperRecord")
	public R<ExampaperRecordVO> detail(ExampaperRecord exampaperRecord) {
		ExampaperRecord detail = exampaperRecordService.getOne(Condition.getQueryWrapper(exampaperRecord));
		//exampaperRecordService.getById(detail.getqs());
		return R.data(ExampaperRecordWrapper.build().entityVO(detail));
	}

	/**
	 * 分页 考试记录表
	 */
	@GetMapping("/list")
	@ApiOperationSupport(order = 2)
	@ApiOperation(value = "分页", notes = "传入exampaperRecord")
	public R<IPage<ExampaperRecord>> list(ExampaperRecord exampaperRecord, Query query) {
		IPage<ExampaperRecord> pages = exampaperRecordService.page(Condition.getPage(query), Condition.getQueryWrapper(exampaperRecord).lambda().orderByDesc(ExampaperRecord::getAddTime));
		return R.data(pages);
	}

	/**
	 * 自定义分页 考试记录表
	 */
	@GetMapping("/page")
	@ApiOperationSupport(order = 3)
	@ApiOperation(value = "分页", notes = "传入exampaperRecord")
	public R<IPage<ExampaperRecordVO>> page(ExampaperRecordVO exampaperRecord, Query query) {

		if(exampaperRecord.getClazzId()!=null){
			List<TrxorderDetail> trxorderDetailList = 	trxorderDetailService.list(
						new LambdaQueryWrapper<TrxorderDetail>()
						.eq(TrxorderDetail::getClazzId,exampaperRecord.getClazzId())
				);
			List<Integer> list =  trxorderDetailList.stream().map(item->item.getUserId()).collect(Collectors.toList());
			if(list.size()>0){
				exampaperRecord.setUserIds(StringUtils.join(",",list));
			}
		}

		IPage<ExampaperRecordVO> pages = exampaperRecordService.selectExampaperRecordPage(Condition.getPage(query), exampaperRecord);
		return R.data(pages);
	}

	/**
	 * 新增 考试记录表
	 */
	@PostMapping("/save")
	@ApiOperationSupport(order = 4)
	@ApiOperation(value = "新增", notes = "传入exampaperRecord")
	public R save(@Valid @RequestBody ExampaperRecord exampaperRecord) {
		return R.status(exampaperRecordService.save(exampaperRecord));
	}

	/**
	 * 修改 考试记录表
	 */
	@PostMapping("/update")
	@ApiOperationSupport(order = 5)
	@ApiOperation(value = "修改", notes = "传入exampaperRecord")
	public R update(@Valid @RequestBody ExampaperRecord exampaperRecord) {
		return R.status(exampaperRecordService.updateById(exampaperRecord));
	}

	/**
	 * 新增或修改 考试记录表
	 */
	@PostMapping("/submit")
	@ApiOperationSupport(order = 6)
	@ApiOperation(value = "新增或修改", notes = "传入exampaperRecord")
	public R submit(@Valid @RequestBody ExampaperRecordVO exampaperRecord) {

		List<ScoreRecord> scoreRecords =  scoreRecordMapper.selectList(new LambdaQueryWrapper<ScoreRecord>()
				.eq(ScoreRecord::getUserId,SecureUtil.getUserId())
				.eq(ScoreRecord::getType,"exam")
				.eq(ScoreRecord::getBizId,exampaperRecord.getId()));
		if(scoreRecords.size()==0){
			List<Exampaper> records =  exampaperService.list(new LambdaQueryWrapper<Exampaper>()
					.eq(Exampaper::getId,exampaperRecord.getEpId()));

			ScoreRecord scoreRecord = new ScoreRecord();
			scoreRecord.setType("exam");
			scoreRecord.setCourseId(records.get(0).getCourseId());
			scoreRecord.setClazzId(records.get(0).getClazzId());
			scoreRecord.setBizId(exampaperRecord.getId());
			scoreRecord.setUserId(SecureUtil.getUserId());
			scoreRecord.setTitle(records.get(0).getExamName()+"成绩");
			scoreRecordMapper.insert(scoreRecord);
		}

		return R.status(exampaperRecordService.saveOrUpdate(exampaperRecord));
	}


	/**
	 * 删除 考试记录表
	 */
	@PostMapping("/remove")
	@ApiOperationSupport(order = 8)
	@ApiOperation(value = "删除", notes = "传入ids")
	public R remove(@ApiParam(value = "主键集合", required = true) @RequestParam String ids) {
		return R.status(exampaperRecordService.removeByIds(Func.toIntList(ids)));
	}

	/**
	 * 导出考试记录
	 */
	@SneakyThrows
	@GetMapping("export-exampaperRecord")
	@ApiOperationSupport(order = 13)
	@ApiOperation(value = "导出考试记录", notes = "传入user")
	public void exportExampaperRecord(ExampaperRecordVO exampaperRecord, HttpServletResponse response) {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String date=simpleDateFormat.format(new Date());
//		exampaperRecord.remove("crazy-auth");

		response.setContentType("application/vnd.ms-excel");
		response.setCharacterEncoding(Charsets.UTF_8.name());
		String fileName = URLEncoder.encode("考试记录数据导出"+date, Charsets.UTF_8.name());
		response.setHeader("Content-disposition", "attachment;filename=" + fileName + ".xlsx");
		exampaperRecordService.exportExampaperRecord(response , exampaperRecord);

	}

}
