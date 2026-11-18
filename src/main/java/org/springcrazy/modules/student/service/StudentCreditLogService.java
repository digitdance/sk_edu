package org.springcrazy.modules.student.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.student.entity.StudentCreditLog;
import org.springcrazy.modules.student.pojo.StudentCreditLogParamDTO;
import org.springcrazy.modules.student.pojo.StudentCreditLogVO;

/**
 * 积分变动记录服务类
 */
public interface StudentCreditLogService extends IService<StudentCreditLog> {
    /**
     * 分页
     * */
    IPage<StudentCreditLogVO> getList(StudentCreditLogParamDTO param);

}
