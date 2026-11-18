package org.springcrazy.modules.student.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springcrazy.common.enums.StudentCreditChangeTypeEnum;
import org.springcrazy.modules.student.entity.StudentCreditLog;
import org.springcrazy.modules.student.mapper.StudentCreditLogMapper;
import org.springcrazy.modules.student.pojo.StudentCreditLogParamDTO;
import org.springcrazy.modules.student.pojo.StudentCreditLogVO;
import org.springcrazy.modules.student.service.StudentCreditLogService;
import org.springframework.stereotype.Service;

/**
 * 积分变动记录服务类
 */
@Service
public class StudentCreditLogServiceImpl extends ServiceImpl<StudentCreditLogMapper, StudentCreditLog> implements StudentCreditLogService {

    @Override
    public IPage<StudentCreditLogVO> getList(StudentCreditLogParamDTO param) {
      //  param.setChangeType(EnumTools.getCodeByDist(param.getChangeTypeText(), StudentCreditChangeTypeEnum.class));
        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
    }
}




