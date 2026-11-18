
package org.springcrazy.modules.edu.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.modules.edu.entity.CourseExam;
import org.springcrazy.modules.edu.entity.CourseFavorites;
import org.springcrazy.modules.edu.mapper.CourseExamMapper;
import org.springcrazy.modules.edu.mapper.CourseFavoritesMapper;
import org.springcrazy.modules.edu.service.ICourseExamService;
import org.springcrazy.modules.edu.service.ICourseFavoritesService;
import org.springcrazy.modules.edu.vo.CourseFavoritesVO;
import org.springframework.stereotype.Service;

/**
 * 收藏 服务实现类
 *
 * @author TongZhou
 * @since 2020-04-25
 */
@Service
public class CourseExamServiceImpl extends ServiceImpl<CourseExamMapper, CourseExam> implements ICourseExamService {



}
