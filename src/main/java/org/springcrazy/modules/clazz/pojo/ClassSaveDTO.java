package org.springcrazy.modules.clazz.pojo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
public class ClassSaveDTO {
    private Long id;

    @NotBlank(message = "未输入名称")
    private String name;


    private Long courseId;
    private Long gradeId;
    private String gradeName;
    private Long schoolId;

    private Long classroomId;

    @NotNull(message = "未选择班级负责人")
    private Long teacherId;

    private String teacherName;

    private String scoreSetting;

//  private Long assistantId1;
//  private Long assistantId2;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

   // @NotNull(message = "未输入预招人数")
    //@Min(value = 0, message = "数量不能小于1")
    private Integer plannedStudentCount;

   // @NotNull(message = "未输入总价")
    //@Min(value = 0, message = "数量不能小于1")
    private Integer plannedLessonCount;

    private String remark;

}
