package org.springcrazy.modules.edu.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springcrazy.modules.book.entity.BookCollection;
import org.springcrazy.modules.edu.entity.Like;

/**
 * <p>
 * 作业 Mapper 接口
 * </p>
 *
 * @author Ryan
 */
@Mapper
public interface LikeMapper extends BaseMapper<Like> {


}
