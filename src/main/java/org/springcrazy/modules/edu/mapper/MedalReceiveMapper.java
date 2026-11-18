
package org.springcrazy.modules.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.entity.Medal;
import org.springcrazy.modules.edu.entity.MedalReceive;
import org.springcrazy.modules.edu.vo.MedalVO;

import java.util.List;

/**
 * 勋章表 Mapper 接口
 *
 * @author TongZhou
 * @since 2020-04-25
 */
public interface MedalReceiveMapper extends BaseMapper<MedalReceive> {


}
