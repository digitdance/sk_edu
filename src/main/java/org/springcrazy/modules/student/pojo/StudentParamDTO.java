package org.springcrazy.modules.student.pojo;

import org.springcrazy.common.pojo.PaginateDTO;
import lombok.Data;

/**
 * 课时列表查询DTO
 */
@Data
public class StudentParamDTO extends PaginateDTO {

    private String keyword;
    private Integer stage;
    // 排除阶段
    private Integer excludeStage;
    private Long classId;
    // 排除班级
    private Long excludeClassId;
    private Long creator;
    private Long userId;
    private Long courseId;
    private Long lessonId;
    private Integer grade;
    // 微信昵称
    private String nickname;
    private Long gradeId;
}
