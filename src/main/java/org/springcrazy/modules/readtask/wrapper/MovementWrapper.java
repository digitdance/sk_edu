
package org.springcrazy.modules.readtask.wrapper;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.service.BookService;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.readtask.entity.Movement;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springcrazy.modules.readtask.entity.ReadTaskRecord;
import org.springcrazy.modules.readtask.pojo.MovementVO;
import org.springcrazy.modules.readtask.pojo.ReadTaskVO;
import org.springcrazy.modules.readtask.service.ReadTaskRecordService;
import org.springcrazy.modules.user.entity.Teacher;
import org.springcrazy.modules.user.service.ITeacherService;

import java.util.Arrays;
import java.util.List;

/**
 * 文章信息表包装类,返回视图层所需的字段
 *
 * @author TongZhou
 * @since 2020-02-28
 */
public class MovementWrapper extends BaseEntityWrapper<Movement, MovementVO>  {

    public static MovementWrapper build() {
        return new MovementWrapper();
    }

	@Override
	public MovementVO entityVO(Movement movement) {
		MovementVO movementVO = BeanUtil.copy(movement, MovementVO.class);
//		ReadTaskRecordService readTaskRecordService = SpringUtil.getBean(ReadTaskRecordService.class);
//		ICourseService courseService = SpringUtil.getBean(ICourseService.class);
//		ITeacherService teacherService = SpringUtil.getBean(ITeacherService.class);
//		BookService bookService = SpringUtil.getBean(BookService.class);
//		Integer userId = SecureUtil.getUserId();
//		if(userId!=null){
//			int count = readTaskRecordService.count(new QueryWrapper<ReadTaskRecord>().lambda().eq(ReadTaskRecord::getUserId,userId).eq(ReadTaskRecord::getTaskId,readTask.getId()));
//			readTaskVO.setCompleted(count>0);
//		}
//		if(Func.isNotEmpty(readTask.getClassId())){
//			Course clazz = courseService.getById((readTask.getClassId()));
//			readTaskVO.setClz(clazz);
//			readTaskVO.setClassName(clazz.getCourseName());
//		}
//		if(Func.isNotEmpty(readTask.getTeachId())){
//			Teacher teacher = teacherService.getById((readTask.getTeachId()));
//			readTaskVO.setTeacherName(teacher.getName());
//		}
//		if(Func.isNotEmpty(readTask.getBookIds())){
//			List<Book> books = bookService.list(new QueryWrapper<Book>().lambda().in(Book::getId, Arrays.asList(readTask.getBookIds())));
//			readTaskVO.setBooks(books);
//		}

//		ICmsSubjectService cmsSubjectService = SpringUtil.getBean(ICmsSubjectService.class);
//		CmsSubject cmsSubject = cmsSubjectService.getSubject(articleVO.getSubjectId());
//		if(Func.isNotEmpty(cmsSubject)){
//			articleVO.setSubjectName(cmsSubject.getSubjectName());
//		}
		return movementVO;
	}

}
