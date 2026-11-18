package org.springcrazy.modules.book.pojo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springcrazy.common.pojo.PaginateDTO;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class BookSaveDTO extends PaginateDTO {
    private Long id;

    @ApiModelProperty(value = "班级ID")
    private Long classId;

    @ApiModelProperty(value = "分类")
    private Integer subjectId;

    @ApiModelProperty(value = "作者")
    private String author;

    @ApiModelProperty(value = "标题")
    private String title;

    @ApiModelProperty(value = "描述")
    private String description;

    @ApiModelProperty(value = "描述")
    private String fileUrl;

    @ApiModelProperty(value = "学校")
    private Long schoolId;
    @ApiModelProperty(value = "点击")
    private Integer click;
    @ApiModelProperty(value = "评论字数")
    private Integer word;
    @ApiModelProperty(value = "内容")
    private String content;

    @ApiModelProperty(value = "创建者所属机构")
    @TableField(fill = FieldFill.INSERT)
    private Long orgId;
}
