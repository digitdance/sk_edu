package org.springcrazy.modules.front.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.entity.BookCollection;
import org.springcrazy.modules.book.pojo.BookParamDTO;
import org.springcrazy.modules.book.pojo.BookVO;
import org.springcrazy.modules.book.service.BookCollectionService;
import org.springcrazy.modules.book.service.BookService;
import org.springcrazy.modules.book.wrapper.BookWrapper;
import org.springcrazy.modules.cms.entity.Comment;
import org.springcrazy.modules.cms.service.ICommentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/front/comment")
@Api(value = "前端考试接口", tags = "前端考试接口")
public class FrontCommentController extends CrazyController {
    private ICommentService commentService;

    private BookService bookService;
    private BookCollectionService bookCollectionService;



    @ApiOperation("图书列表")
    @PostMapping("/addComment")
    public R addComment(Comment comment) {
        commentService.saveOrUpdate(comment);

        //任务完成记录
        return R.success("评论成功");

    }

    @GetMapping("/info")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入article")
    public R<BookVO> detail(BookParamDTO bookParamDTO) {
        Book detail = bookService.getById(bookParamDTO.getBookId());
        return R.data(BookWrapper.build().entityVO(detail));
    }



}
