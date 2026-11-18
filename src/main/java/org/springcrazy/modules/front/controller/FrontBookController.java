package org.springcrazy.modules.front.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import org.springcrazy.modules.cms.entity.Article;
import org.springcrazy.modules.cms.entity.Comment;
import org.springcrazy.modules.cms.service.ICommentService;
import org.springcrazy.modules.cms.vo.ArticleVO;
import org.springcrazy.modules.cms.wrapper.ArticleWrapper;
import org.springcrazy.modules.edu.entity.History;
import org.springcrazy.modules.edu.service.IHistoryService;
import org.springcrazy.modules.readtask.entity.ReadTask;
import org.springcrazy.modules.readtask.entity.ReadTaskRecord;
import org.springcrazy.modules.readtask.pojo.ReadTaskRecordSaveDTO;
import org.springcrazy.modules.readtask.service.ReadTaskRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/front/book")
@Api(value = "前端考试接口", tags = "前端考试接口")
public class FrontBookController extends CrazyController {
    private ICommentService commentService;

    private BookService bookService;
    private BookCollectionService bookCollectionService;

    private ReadTaskRecordService readTaskRecordService;

    private IHistoryService historyService;

    @ApiOperation("图书列表")
    @GetMapping("/list")
    public R list(BookParamDTO book, Query query) {
        QueryWrapper<Book> readTaskQueryWrapper = new QueryWrapper<>();
        readTaskQueryWrapper.lambda()
                .like(Book::getTitle,book.getKeyword())
                .or(e->{
                    e.like(Book::getDescription,book.getKeyword());
                })
                .orderByDesc(Book::getCreateTime);

       // return R.data(bookService.list(readTaskQueryWrapper));

        IPage<Book> pages = bookService.page(Condition.getPage(query), readTaskQueryWrapper);
        return R.data(BookWrapper.build().pageVO(pages));

    }


    @ApiOperation("图书列表")
    @GetMapping("/commentList")
    public R commentList(BookParamDTO book) {
        QueryWrapper<Comment> readTaskQueryWrapper = new QueryWrapper<>();
        readTaskQueryWrapper.lambda()
                .like(Comment::getOtherId,book.getBookId());

        // return R.data(bookService.list(readTaskQueryWrapper));

        List<Comment> commentList =  commentService.list(readTaskQueryWrapper);

        return R.data(commentList);

    }




    @GetMapping("/info")
    @ApiOperationSupport(order = 1)
    @ApiOperation(value = "详情", notes = "传入article")
    public R<BookVO> detail(BookParamDTO bookParamDTO) {
        Book detail = bookService.getById(bookParamDTO.getBookId());
        BookVO bookVO = BookWrapper.build().entityVO(detail);

        QueryWrapper<ReadTaskRecord> readTaskQueryWrapper = new QueryWrapper<>();
        readTaskQueryWrapper.lambda()
                .eq(ReadTaskRecord::getBookId,bookParamDTO.getBookId())
                .eq(ReadTaskRecord::getUserId,SecureUtil.getUserId());

        Integer readTaskRecordCount = readTaskRecordService.count(readTaskQueryWrapper);
        bookVO.setReadCount(readTaskRecordCount);

        //增加点击量
        detail.setClick(detail.getClick()+1);
        bookService.updateById(detail);

        return R.data(bookVO);
    }


    @ApiOperation("开始阅读")
    @PostMapping("/read")
    public R addComment(BookParamDTO bookParamDTO) {
        Book detail = bookService.getById(bookParamDTO.getBookId());
        //增加点击量
        detail.setClick(detail.getClick()+1);
        bookService.updateById(detail);
        bookService.saveOrUpdate(detail);

        ReadTaskRecordSaveDTO readTaskRecord = new ReadTaskRecordSaveDTO();
        readTaskRecord.setUserId(SecureUtil.getUserId());
        readTaskRecord.setBookId(bookParamDTO.getBookId());
        readTaskRecord.setTaskCompleteType(0);
        readTaskRecord.setTaskId(bookParamDTO.getTaskId());
        readTaskRecordService.saveRecord(readTaskRecord);


        History history = new History();
        history.setUserId(SecureUtil.getUserId());
        history.setStudentId(bookParamDTO.getStudentId());
        history.setObjId(bookParamDTO.getBookId());
        history.setType("book_read");
        historyService.save(history);
        //任务完成记录
        return R.success("ok");

    }


    @ApiOperation("收藏图书")
    @GetMapping("/collect")
    public R list(BookParamDTO bookParamDTO) {

        QueryWrapper<BookCollection> bookCollectionQueryWrapper = new QueryWrapper<>();
        bookCollectionQueryWrapper.lambda().eq(BookCollection::getUserId, SecureUtil.getUserId())
                .eq(BookCollection::getBookId,bookParamDTO.getBookId());

        bookCollectionService.remove(bookCollectionQueryWrapper);


        if(bookParamDTO.getCollectType()==1){
            BookCollection bookCollection = new BookCollection();
            bookCollection.setUserId(SecureUtil.getUserId());
            bookCollection.setBookId(bookParamDTO.getBookId());
            bookCollectionService.save(bookCollection);
        }
        return R.success("操作成功");
    }

    @ApiOperation("收藏图书")
    @GetMapping("/readHistory")
    public R readHistory(BookParamDTO bookParamDTO) {

        QueryWrapper<BookCollection> bookCollectionQueryWrapper = new QueryWrapper<>();
        bookCollectionQueryWrapper.lambda().eq(BookCollection::getUserId, SecureUtil.getUserId())
                .eq(BookCollection::getBookId,bookParamDTO.getBookId());

        bookCollectionService.remove(bookCollectionQueryWrapper);

        History history = new History();
        history.setUserId(SecureUtil.getUserId());
        history.setStudentId(bookParamDTO.getStudentId());
        history.setObjId(bookParamDTO.getBookId());
        history.setType("book_read");
        historyService.save(history);
        return R.success("操作成功");
    }


    @ApiOperation("收藏图书数量")
    @GetMapping("/collectionedCount")
    public R collectionedCount(BookParamDTO bookParamDTO) {

        QueryWrapper<BookCollection> bookCollectionQueryWrapper = new QueryWrapper<>();
        bookCollectionQueryWrapper.lambda().eq(BookCollection::getUserId, SecureUtil.getUserId())
                .eq(BookCollection::getBookId,bookParamDTO.getBookId());

       int count =  bookCollectionService.count(bookCollectionQueryWrapper);

        return R.data(count);
    }
}
