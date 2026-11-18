
package org.springcrazy.modules.edu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.entity.Medal;
import org.springcrazy.modules.edu.entity.Orders;
import org.springcrazy.modules.edu.vo.CourseVO;
import org.springcrazy.modules.system.excel.GiveCourseExcel;
import org.springcrazy.modules.user.entity.Student;

import java.util.Date;
import java.util.List;

/**
 * 课程表 服务类
 *
 * @author TongZhou
 * @since 2020-04-25
 */
public interface IMedalService extends IService<Medal> {


}
