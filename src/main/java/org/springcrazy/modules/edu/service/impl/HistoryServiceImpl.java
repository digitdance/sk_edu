
package org.springcrazy.modules.edu.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.modules.edu.entity.History;
import org.springcrazy.modules.edu.entity.Like;
import org.springcrazy.modules.edu.mapper.HistoryMapper;
import org.springcrazy.modules.edu.mapper.LikeMapper;
import org.springcrazy.modules.edu.service.IHistoryService;
import org.springcrazy.modules.edu.service.ILikeService;
import org.springframework.stereotype.Service;

/**
 * 课程表 服务实现类
 *
 * @author TongZhou
 * @since 2020-04-25
 */
@Service
public class HistoryServiceImpl extends ServiceImpl<HistoryMapper, History> implements IHistoryService {


}
