package org.springcrazy.modules.student.pojo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 转移学员DTO
 * @author Ryan 541720500@qq.com
 * description
 */
@Data
public class StaffExchangeCounselorDTO {

    @NotNull(message = "缺少被转移人员参数")
    private List<Long> fromIds;

    @NotNull(message = "缺少转移后人员参数")
    private Long toId;

}
