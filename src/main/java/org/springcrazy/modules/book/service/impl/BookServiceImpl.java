package org.springcrazy.modules.book.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springcrazy.core.mp.base.BaseServiceImpl;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.mapper.BookMapper;
import org.springcrazy.modules.book.pojo.BookParamDTO;
import org.springcrazy.modules.book.pojo.BookSaveDTO;
import org.springcrazy.modules.book.pojo.BookVO;
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
public class BookServiceImpl extends BaseServiceImpl<BookMapper, Book> implements BookService {

//    @Override
//    public IPage<BookVO> getList(BookParamDTO param) {
//        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
//    }

    @Override
    public List<BookVO> getAll(BookParamDTO param) {
        return this.baseMapper.getList(param);
    }

    @Override
    public Boolean saveOrUpdateByDTO(BookSaveDTO dto) {
        Book item = new Book();
        BeanUtils.copyProperties(dto, item);
        return this.saveOrUpdate(item);
    }

    @Override
    public BookVO getInfo(Long id, Long studentId) {
        return baseMapper.getInfo(id, studentId);
    }

}
