
package org.springcrazy.modules.edu.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.modules.edu.entity.CourseHomework;
import org.springcrazy.modules.edu.entity.CourseMaterial;
import org.springcrazy.modules.edu.vo.CourseHomeworkVO;
import org.springcrazy.modules.edu.vo.CourseMaterialVO;

import java.util.List;

/**
 * 知识点的基本信息 服务类
 *
 * @author TongZhou
 * @since 2020-04-25
 */
public interface ICourseHomeworkService extends IService<CourseHomework> {
    /**
     * 自定义分页
     *
     * @param page
     * @param courseMaterial
     * @return
     */
    IPage<CourseHomeworkVO> selectCourseHomeworkPage(IPage<CourseHomeworkVO> page, CourseHomeworkVO courseHomeworkVO);

    /**
     * 根据章节id获取资料
     * @param id
     * @return
     */
    List<CourseHomework> getHomeworksBykpointId(Integer id);

    /**
     * 根据课程id获取资料
     * @param id
     * @return
     */
    List<CourseHomework> getHomeworksByCourseId(Integer id);
}
