package org.springcrazy.common.pojo;


import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import javax.validation.constraints.NotNull;

public class AuditParamDTO {
    @ApiModelProperty("选中项")
    private @NotNull(
            message = "缺少参数IDS"
    ) List<Long> ids;
    @ApiModelProperty("审核状态")
    private @NotNull(
            message = "请选择审核状态"
    ) Integer verifyState;
    @ApiModelProperty("审核说明")
    private String verifyRemark;
    private Long staffId;

    public AuditParamDTO() {
    }

    public List<Long> getIds() {
        return this.ids;
    }

    public Integer getVerifyState() {
        return this.verifyState;
    }

    public String getVerifyRemark() {
        return this.verifyRemark;
    }

    public Long getStaffId() {
        return this.staffId;
    }

    public void setIds(final List<Long> ids) {
        this.ids = ids;
    }

    public void setVerifyState(final Integer verifyState) {
        this.verifyState = verifyState;
    }

    public void setVerifyRemark(final String verifyRemark) {
        this.verifyRemark = verifyRemark;
    }

    public void setStaffId(final Long staffId) {
        this.staffId = staffId;
    }

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof AuditParamDTO)) {
            return false;
        } else {
            AuditParamDTO other = (AuditParamDTO)o;
            if (!other.canEqual(this)) {
                return false;
            } else {
                label59: {
                    Object this$verifyState = this.getVerifyState();
                    Object other$verifyState = other.getVerifyState();
                    if (this$verifyState == null) {
                        if (other$verifyState == null) {
                            break label59;
                        }
                    } else if (this$verifyState.equals(other$verifyState)) {
                        break label59;
                    }

                    return false;
                }

                Object this$staffId = this.getStaffId();
                Object other$staffId = other.getStaffId();
                if (this$staffId == null) {
                    if (other$staffId != null) {
                        return false;
                    }
                } else if (!this$staffId.equals(other$staffId)) {
                    return false;
                }

                Object this$ids = this.getIds();
                Object other$ids = other.getIds();
                if (this$ids == null) {
                    if (other$ids != null) {
                        return false;
                    }
                } else if (!this$ids.equals(other$ids)) {
                    return false;
                }

                Object this$verifyRemark = this.getVerifyRemark();
                Object other$verifyRemark = other.getVerifyRemark();
                if (this$verifyRemark == null) {
                    if (other$verifyRemark != null) {
                        return false;
                    }
                } else if (!this$verifyRemark.equals(other$verifyRemark)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof AuditParamDTO;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        Object $verifyState = this.getVerifyState();
        result = result * 59 + ($verifyState == null ? 43 : $verifyState.hashCode());
        Object $staffId = this.getStaffId();
        result = result * 59 + ($staffId == null ? 43 : $staffId.hashCode());
        Object $ids = this.getIds();
        result = result * 59 + ($ids == null ? 43 : $ids.hashCode());
        Object $verifyRemark = this.getVerifyRemark();
        result = result * 59 + ($verifyRemark == null ? 43 : $verifyRemark.hashCode());
        return result;
    }

    public String toString() {
        return "AuditParamDTO(ids=" + this.getIds() + ", verifyState=" + this.getVerifyState() + ", verifyRemark=" + this.getVerifyRemark() + ", staffId=" + this.getStaffId() + ")";
    }
}
