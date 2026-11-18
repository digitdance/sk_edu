package org.springcrazy.common.pojo;


import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import java.time.LocalDateTime;

public class AutoFillEntity implements Serializable {
    private static final long serialVersionUID = 1L;
    @ApiModelProperty("添加时间")
    @TableField(
            fill = FieldFill.INSERT
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm"
    )
    private LocalDateTime addTime;
    @ApiModelProperty("编辑时间")
    @TableField(
            fill = FieldFill.INSERT_UPDATE
    )
    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm"
    )
    private LocalDateTime editTime;
    @ApiModelProperty("创建者")
    @TableField(
            fill = FieldFill.INSERT
    )
    private Long creator;
    @ApiModelProperty("编辑者")
    @TableField(
            fill = FieldFill.INSERT_UPDATE
    )
    private Long editor;
    @TableLogic
    @ApiModelProperty("逻辑删除")
    private Boolean deleted;

    public AutoFillEntity() {
    }

    public LocalDateTime getAddTime() {
        return this.addTime;
    }

    public LocalDateTime getEditTime() {
        return this.editTime;
    }

    public Long getCreator() {
        return this.creator;
    }

    public Long getEditor() {
        return this.editor;
    }

    public Boolean getDeleted() {
        return this.deleted;
    }

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm"
    )
    public void setAddTime(final LocalDateTime addTime) {
        this.addTime = addTime;
    }

    @JsonFormat(
            pattern = "yyyy-MM-dd HH:mm"
    )
    public void setEditTime(final LocalDateTime editTime) {
        this.editTime = editTime;
    }

    public void setCreator(final Long creator) {
        this.creator = creator;
    }

    public void setEditor(final Long editor) {
        this.editor = editor;
    }

    public void setDeleted(final Boolean deleted) {
        this.deleted = deleted;
    }

    public String toString() {
        return "AutoFillEntity(addTime=" + this.getAddTime() + ", editTime=" + this.getEditTime() + ", creator=" + this.getCreator() + ", editor=" + this.getEditor() + ", deleted=" + this.getDeleted() + ")";
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AutoFillEntity)) {
            return false;
        } else {
            AutoFillEntity other = (AutoFillEntity)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label71: {
                    Object this$creator = this.getCreator();
                    Object other$creator = other.getCreator();
                    if (this$creator == null) {
                        if (other$creator == null) {
                            break label71;
                        }
                    } else if (this$creator.equals(other$creator)) {
                        break label71;
                    }

                    return false;
                }

                Object this$editor = this.getEditor();
                Object other$editor = other.getEditor();
                if (this$editor == null) {
                    if (other$editor != null) {
                        return false;
                    }
                } else if (!this$editor.equals(other$editor)) {
                    return false;
                }

                label57: {
                    Object this$deleted = this.getDeleted();
                    Object other$deleted = other.getDeleted();
                    if (this$deleted == null) {
                        if (other$deleted == null) {
                            break label57;
                        }
                    } else if (this$deleted.equals(other$deleted)) {
                        break label57;
                    }

                    return false;
                }

                Object this$addTime = this.getAddTime();
                Object other$addTime = other.getAddTime();
                if (this$addTime == null) {
                    if (other$addTime != null) {
                        return false;
                    }
                } else if (!this$addTime.equals(other$addTime)) {
                    return false;
                }

                Object this$editTime = this.getEditTime();
                Object other$editTime = other.getEditTime();
                if (this$editTime == null) {
                    if (other$editTime == null) {
                        return true;
                    }
                } else if (this$editTime.equals(other$editTime)) {
                    return true;
                }

                return false;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AutoFillEntity;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $creator = this.getCreator();
        result = result * 59 + ($creator == null ? 43 : $creator.hashCode());
        Object $editor = this.getEditor();
        result = result * 59 + ($editor == null ? 43 : $editor.hashCode());
        Object $deleted = this.getDeleted();
        result = result * 59 + ($deleted == null ? 43 : $deleted.hashCode());
        Object $addTime = this.getAddTime();
        result = result * 59 + ($addTime == null ? 43 : $addTime.hashCode());
        Object $editTime = this.getEditTime();
        result = result * 59 + ($editTime == null ? 43 : $editTime.hashCode());
        return result;
    }
}
