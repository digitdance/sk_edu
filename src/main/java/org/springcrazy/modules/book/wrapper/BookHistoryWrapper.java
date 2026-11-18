
package org.springcrazy.modules.book.wrapper;

import org.springcrazy.core.mp.support.BaseEntityWrapper;
import org.springcrazy.core.tool.utils.BeanUtil;
import org.springcrazy.core.tool.utils.SpringUtil;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.entity.BookCollection;
import org.springcrazy.modules.book.pojo.BookCollectionVO;
import org.springcrazy.modules.book.pojo.BookHistoryVO;
import org.springcrazy.modules.book.service.BookService;
import org.springcrazy.modules.edu.entity.History;
import org.springcrazy.modules.edu.service.ISubjectService;

/**
 * 文章信息表包装类,返回视图层所需的字段
 *
 * @author TongZhou
 * @since 2020-02-28
 */
public class BookHistoryWrapper extends BaseEntityWrapper<History, BookHistoryVO>  {
	private static BookService bookService;
    public static BookHistoryWrapper build() {
        return new BookHistoryWrapper();
    }


	static {
		bookService = SpringUtil.getBean(BookService.class);

	}
	@Override
	public BookHistoryVO entityVO(History book) {
		BookHistoryVO articleVO = BeanUtil.copy(book, BookHistoryVO.class);
		if(articleVO.getType().equals("read_book")&&articleVO.getObjId()!=null){
			Book book1 =  bookService.getById(articleVO.getObjId());
			articleVO.setBook(book1);
		}
		return articleVO;
	}

}
