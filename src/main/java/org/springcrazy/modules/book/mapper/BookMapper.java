package org.springcrazy.modules.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hzb.erp.annotation.DataScoped;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.pojo.BookParamDTO;
import org.springcrazy.modules.book.pojo.BookVO;

import java.util.List;

/**
 * <p>
 * 作业 Mapper 接口
 * </p>
 *
 * @author Ryan
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {


    @DataScoped
    IPage<BookVO> getList(Page<?> page, BookParamDTO param);

    @DataScoped
    List<BookVO> getList(@Param("param") BookParamDTO param);

    BookVO getInfo(Long id, Long studentId);
}
