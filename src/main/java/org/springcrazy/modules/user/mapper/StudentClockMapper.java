
package org.springcrazy.modules.user.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.system.excel.ExportStudentExcel;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.entity.StudentClock;
import org.springcrazy.modules.user.vo.StudentVO;

import java.util.List;

/**
 * 学员表 Mapper 接口
 *
 * @author TongZhou
 * @since 2020-04-28
 */
public interface StudentClockMapper extends BaseMapper<StudentClock> {


}
