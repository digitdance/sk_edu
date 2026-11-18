package org.springcrazy.modules.book.service.impl;

import org.springcrazy.core.mp.base.BaseServiceImpl;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.entity.BookCollection;
import org.springcrazy.modules.book.mapper.BookCollectionMapper;
import org.springcrazy.modules.book.mapper.BookMapper;
import org.springcrazy.modules.book.pojo.BookParamDTO;
import org.springcrazy.modules.book.pojo.BookSaveDTO;
import org.springcrazy.modules.book.pojo.BookVO;
import org.springcrazy.modules.book.service.BookCollectionService;
import org.springcrazy.modules.book.service.BookService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 作业 服务实现类
 * </p>
 *
 * @author Ryan
 */
@Service
public class BookCollectionServiceImpl extends BaseServiceImpl<BookCollectionMapper, BookCollection> implements BookCollectionService {


}
