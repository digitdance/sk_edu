package org.springcrazy.modules.book.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springcrazy.core.mp.base.BaseService;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.pojo.BookParamDTO;
import org.springcrazy.modules.book.pojo.BookSaveDTO;
import org.springcrazy.modules.book.pojo.BookVO;

import java.util.List;

/**
 * <p>
 * 作业 服务类
 * </p>
 *
 * @author Ryan
 */
public interface BookService extends BaseService<Book> {

   // IPage<BookVO> getList(BookParamDTO param);

    List<BookVO> getAll(BookParamDTO param);

    Boolean saveOrUpdateByDTO(BookSaveDTO dto);

    BookVO getInfo(Long id, Long studentId);
}
