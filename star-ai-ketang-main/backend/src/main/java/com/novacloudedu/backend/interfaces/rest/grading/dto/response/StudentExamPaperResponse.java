package com.novacloudedu.backend.interfaces.rest.grading.dto.response;

import com.alibaba.fastjson.JSONArray;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * 学生视角的试卷详情（不含标准答案）
 */
@Data
@Schema(description = "学生视角试卷详情（不含答案）")
public class StudentExamPaperResponse {

    @Schema(description = "试卷ID")
    private String id;

    @Schema(description = "标题")
    private String title;

    @Schema(description = "副标题")
    private String subtitle;

    @Schema(description = "学科代码")
    private String subject;

    @Schema(description = "学科名称")
    private String subjectName;

    @Schema(description = "年级")
    private String grade;

    @Schema(description = "总分")
    private Integer totalScore;

    @Schema(description = "考试时长(分钟)")
    private Integer durationMin;

    @Schema(description = "大题列表")
    private List<SectionView> sections;

    @Data
    @Schema(description = "大题（学生视角）")
    public static class SectionView {
        @Schema(description = "大题ID")
        private String id;
        @Schema(description = "标题")
        private String title;
        @Schema(description = "描述")
        private String description;
        @Schema(description = "题型")
        private String questionType;
        @Schema(description = "题型描述")
        private String questionTypeDesc;
        @Schema(description = "排序")
        private Integer sortOrder;
        @Schema(description = "题目列表")
        private List<QuestionView> questions;
    }

    @Data
    @Schema(description = "题目（学生视角，不含答案）")
    public static class QuestionView {
        @Schema(description = "题目序号（从1开始）")
        private Integer index;
        @Schema(description = "题目ID")
        private String questionId;
        @Schema(description = "题型")
        private String questionType;
        @Schema(description = "题干内容")
        private String content;
        @Schema(description = "选项列表（选择题用）")
        private List<Object> options;
        @Schema(description = "选项列表（选择题用）")
        private JSONArray optionArray;
        @Schema(description = "分值")
        private Integer score;
        @Schema(description = "图片URL")
        private String imageUrl;
    }
}
