package com.novacloudedu.backend.interfaces.rest.grading.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 在线提交答卷请求（学生直接在网页作答）
 */
@Data
@Schema(description = "在线提交答卷请求")
public class SubmitOnlineAnswersRequest {

    @NotNull(message = "试卷ID不能为空")
    @Schema(description = "试卷ID")
    private Long examPaperId;

    @Schema(description = "班级ID（可选）")
    private Long classId;

    @NotEmpty(message = "答题列表不能为空")
    @Valid
    @Schema(description = "学生答案列表")
    private List<StudentAnswerItem> answers;

    @Data
    @Schema(description = "单题作答")
    public static class StudentAnswerItem {
        @NotNull(message = "题号不能为空")
        @Schema(description = "题号（从1开始，与试卷题目顺序一致）")
        private Integer index;

        @Schema(description = "题目ID")
        private Long questionId;

        @Schema(description = "学生作答内容")
        private String answer;
    }
}
