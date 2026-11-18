
package org.springcrazy.modules.user.service.impl;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import eu.bitwalker.useragentutils.UserAgent;
import org.apache.commons.lang.math.NumberUtils;
import org.springcrazy.common.enums.StudentStageEnum;
import org.springcrazy.common.exception.BizException;
import org.springcrazy.core.log.exception.ServiceException;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.secure.CrazyUser;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.utils.*;
import org.springcrazy.modules.clazz.pojo.ClassStudentVO;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.student.entity.StudentCreditLog;
import org.springcrazy.modules.student.mapper.StudentCreditLogMapper;
import org.springcrazy.modules.student.pojo.StudentParamDTO;
import org.springcrazy.modules.student.service.StudentCourseService;
import org.springcrazy.modules.system.entity.User;
import org.springcrazy.modules.system.entity.UserInfo;
import org.springcrazy.modules.system.excel.ExportStudentExcel;
import org.springcrazy.modules.system.excel.StudentExcel;
import org.springcrazy.modules.system.service.IUserService;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.entity.StudentClock;
import org.springcrazy.modules.user.entity.UserAccount;
import org.springcrazy.modules.user.entity.UserLoginLog;
import org.springcrazy.modules.user.mapper.StudentClockMapper;
import org.springcrazy.modules.user.mapper.StudentMapper;
import org.springcrazy.modules.user.service.IStudentClockService;
import org.springcrazy.modules.user.service.IStudentService;
import org.springcrazy.modules.user.service.IUserAccountService;
import org.springcrazy.modules.user.service.IUserLoginLogService;
import org.springcrazy.modules.user.vo.StudentVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * 学员表 服务实现类
 *
 * @author TongZhou
 * @since 2020-04-28
 */
@Service
public class StudentClockServiceImpl extends ServiceImpl<StudentClockMapper, StudentClock> implements IStudentClockService {




}
