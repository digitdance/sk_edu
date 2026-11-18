
package org.springcrazy.modules.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.common.enums.StudentStageEnum;
import org.springcrazy.modules.clazz.pojo.ClassStudentVO;
import org.springcrazy.modules.student.entity.StudentCreditLog;
import org.springcrazy.modules.student.pojo.StudentParamDTO;
import org.springcrazy.modules.system.entity.UserInfo;
import org.springcrazy.modules.system.excel.StudentExcel;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.entity.StudentClock;
import org.springcrazy.modules.user.vo.StudentVO;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * 学员表 服务类
 *
 * @author TongZhou
 * @since 2020-04-28
 */
public interface IStudentClockService extends IService<StudentClock> {


}
