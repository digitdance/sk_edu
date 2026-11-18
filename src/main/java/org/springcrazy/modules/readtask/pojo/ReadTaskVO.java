package org.springcrazy.modules.readtask.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.lesson.pojo.HomeworkRecordVO;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springcrazy.modules.readtask.entity.ReadTaskRecord;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 * 作业
 * </p>
 *
 * @author Ryan
 */
@Data
@ApiModel(value = "Homework对象", description = "作业")
public class ReadTaskVO  extends ReadTask {

    @ApiModelProperty(value = "主键")
    private Long id;

    @ApiModelProperty(value = "班级ID")
    private Long classId;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "班级")
    private String className;
    @ApiModelProperty(value = "是否需要家长反馈")
    private Integer needFk;

    @ApiModelProperty(value = "创建人")
    private Long creator;
    @ApiModelProperty(value = "创建人")
    private String teacherName;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date taskDateStart;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date taskDateEnd;

    private Integer completeCount;

    @ApiModelProperty(value = "单个学生查询时用于判断是否完成")
    private Boolean completed;

    private List<ReadTaskRecord> recordList;
    private List<Book> books;

    private Course clz;

    private Integer taskCount;
}
