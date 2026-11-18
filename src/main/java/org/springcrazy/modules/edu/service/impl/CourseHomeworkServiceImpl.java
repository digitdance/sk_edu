
package org.springcrazy.modules.edu.service.impl;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.modules.edu.entity.CourseHomework;
import org.springcrazy.modules.edu.entity.CourseMaterial;
import org.springcrazy.modules.edu.mapper.CourseHomeworkMapper;
import org.springcrazy.modules.edu.mapper.CourseMaterialMapper;
import org.springcrazy.modules.edu.service.ICourseHomeworkService;
import org.springcrazy.modules.edu.service.ICourseMaterialService;
import org.springcrazy.modules.edu.vo.CourseHomeworkVO;
import org.springcrazy.modules.edu.vo.CourseMaterialVO;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 知识点的基本信息 服务实现类
 *
 * @author TongZhou
 * @since 2020-04-25
 */
@Service
public class CourseHomeworkServiceImpl extends ServiceImpl<CourseHomeworkMapper, CourseHomework> implements ICourseHomeworkService {

    @Override
    public IPage<CourseHomeworkVO> selectCourseHomeworkPage(IPage<CourseHomeworkVO> page, CourseHomeworkVO courseHomeworkVO) {
        return page.setRecords(baseMapper.selectCourseHomeworkPage(page, courseHomeworkVO));
    }

    @Override
    public List<CourseHomework> getHomeworksBykpointId(Integer id) {
        return baseMapper.getHomeworksBykpointId(id);
    }

    @Override
    public List<CourseHomework> getHomeworksByCourseId(Integer id) {
        return baseMapper.getHomeworksByCourseId(id);
    }
}
