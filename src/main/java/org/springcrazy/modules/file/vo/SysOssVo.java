package org.springcrazy.modules.file.vo;

import com.hzb.erp.common.entity.SysLog;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springcrazy.modules.file.entity.SysOss;

@Data
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ErrorQuestionVO对象", description = "错题记录表")
public class SysOssVo extends SysOss {
}
