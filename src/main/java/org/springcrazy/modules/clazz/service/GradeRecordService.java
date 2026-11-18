package org.springcrazy.modules.clazz.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.clazz.entity.GradeRecord;
import org.springcrazy.modules.clazz.pojo.GradeParamDTO;
import org.springcrazy.modules.clazz.pojo.GradeRecordSaveDTO;
import org.springcrazy.modules.clazz.pojo.GradeRecordVO;

/**
 * <p>
 * 成绩单学生分数 服务类
 * </p>
 *
 * @author Ryan
 */
public interface GradeRecordService extends IService<GradeRecord> {
    IPage<GradeRecordVO> getList(GradeParamDTO param);

    Boolean saveOrUpdateByDTO(GradeRecordSaveDTO dto);
}
