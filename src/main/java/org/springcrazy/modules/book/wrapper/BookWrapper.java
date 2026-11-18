
package org.springcrazy.modules.book.wrapper;

import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.pojo.BookVO;
import org.springcrazy.modules.edu.entity.Subject;
import org.springcrazy.modules.edu.service.ICoursePackageService;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.edu.service.ISubjectService;

/**
 * 文章信息表包装类,返回视图层所需的字段
 *
 * @author TongZhou
 * @since 2020-02-28
 */
public class BookWrapper extends BaseEntityWrapper<Book, BookVO>  {
	private static ISubjectService subjectService;
    public static BookWrapper build() {
        return new BookWrapper();
    }


	static {
		subjectService = SpringUtil.getBean(ISubjectService.class);

	}
	@Override
	public BookVO entityVO(Book book) {
		BookVO articleVO = BeanUtil.copy(book, BookVO.class);
		if(subjectService!=null&&articleVO.getSubjectId()!=null){
			Subject cmsSubject = subjectService.getSubjectBysubjectId(articleVO.getSubjectId());
			if(Func.isNotEmpty(cmsSubject)){
				articleVO.setSubjectName(cmsSubject.getSubjectName());
			}
		}


		if(subjectService!=null&&articleVO.getSubjectId()!=null){
			Subject cmsSubject = subjectService.getSubjectBysubjectId(articleVO.getSubjectId());
			if(Func.isNotEmpty(cmsSubject)){
				articleVO.setSubjectName(cmsSubject.getSubjectName());
			}
		}

		return articleVO;
	}

}
