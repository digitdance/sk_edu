package com.novacloudedu.backend.application.grading.command;

import java.util.List;

/**
 * 在线提交答卷命令（学生直接在网页作答，非图片上传）
 */
public record SubmitOnlineAnswersCommand(
        Long examPaperId,
        Long classId,
        List<StudentAnswerItem> answers
) {
    public record StudentAnswerItem(
            Integer index,
            Long questionId,
            String answer
    ) {}
}
