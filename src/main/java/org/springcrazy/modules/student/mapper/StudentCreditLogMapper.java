package org.springcrazy.modules.student.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import com.hzb.erp.annotation.DataScoped;
import org.apache.ibatis.annotations.Mapper;
import org.springcrazy.modules.student.entity.StudentCreditLog;
import org.springcrazy.modules.student.pojo.StudentCreditLogParamDTO;
import org.springcrazy.modules.student.pojo.StudentCreditLogVO;

/**
 * 积分变动记录
 * @Entity com.hzb.erp.api.adminCenter.student.entity.StudentCreditLog
 */
@Mapper
public interface StudentCreditLogMapper extends BaseMapper<StudentCreditLog> {

    @DataScoped
    IPage<StudentCreditLogVO> getList(Page<Object> objectPage, StudentCreditLogParamDTO param);
}




