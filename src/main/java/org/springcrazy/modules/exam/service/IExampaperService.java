
package org.springcrazy.modules.exam.service;

import org.springcrazy.modules.exam.entity.Exampaper;
import org.springcrazy.modules.exam.entity.PaperMiddle;
import org.springcrazy.modules.exam.vo.ExampaperVO;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.springcrazy.modules.exam.vo.QstmiddleVO;

import java.util.List;
import java.util.Map;

/**
 * 考试试卷表 服务类
 *
 * @author TongZhou
 * @since 2020-12-01
 */
public interface IExampaperService extends IService<Exampaper> {

	/**
	 * 自定义分页
	 *
	 * @param page
	 * @param exampaper
	 * @return
	 */
	IPage<ExampaperVO> selectExampaperPage(IPage<ExampaperVO> page, ExampaperVO exampaper);

    //保存大题
    void insertQuestionOne(PaperMiddle paperMiddle);

    void insertQuestion(List<PaperMiddle> examQuestionDTOList);

    void insertQuestionWithPaperId(List<PaperMiddle> examQuestionDTOList, Integer paperId);

    //IPage page, @Param(value = "coursePackageExam") ExampaperVO exampaper
    IPage<ExampaperVO> selectClazzExamList(IPage page, ExampaperVO exampaper);

    IPage<ExampaperVO> selectCourseExamList(IPage page, ExampaperVO exampaper);

    List<PaperMiddle> queryQuestion(String paperId);

	Map<Integer,QstmiddleVO> queryQuestionReturnMap(Integer paperId);

}
