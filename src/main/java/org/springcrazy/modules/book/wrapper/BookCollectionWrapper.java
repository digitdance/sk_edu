
package org.springcrazy.modules.book.wrapper;

import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.entity.BookCollection;
import org.springcrazy.modules.book.pojo.BookCollectionVO;
import org.springcrazy.modules.book.pojo.BookVO;
import org.springcrazy.modules.edu.entity.Subject;
import org.springcrazy.modules.edu.service.ISubjectService;

/**
 * 文章信息表包装类,返回视图层所需的字段
 *
 * @author TongZhou
 * @since 2020-02-28
 */
public class BookCollectionWrapper extends BaseEntityWrapper<BookCollection, BookCollectionVO>  {
	private static ISubjectService subjectService;
    public static BookCollectionWrapper build() {
        return new BookCollectionWrapper();
    }


	static {
		subjectService = SpringUtil.getBean(ISubjectService.class);

	}
	@Override
	public BookCollectionVO entityVO(BookCollection book) {
		BookCollectionVO articleVO = BeanUtil.copy(book, BookCollectionVO.class);
//		if(subjectService!=null&&articleVO.getSubjectId()!=null){
//			Subject cmsSubject = subjectService.getSubjectBysubjectId(articleVO.getSubjectId());
//			if(Func.isNotEmpty(cmsSubject)){
//				articleVO.setSubjectName(cmsSubject.getSubjectName());
//			}
//		}
//
//
//		if(subjectService!=null&&book.getSubjectId()!=null){
//			Subject cmsSubject = subjectService.getSubjectBysubjectId(articleVO.getSubjectId());
//			if(Func.isNotEmpty(cmsSubject)){
//				articleVO.setSubjectName(cmsSubject.getSubjectName());
//			}
//		}

		return articleVO;
	}

}
