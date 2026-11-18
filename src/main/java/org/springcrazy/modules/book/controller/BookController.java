package org.springcrazy.modules.book.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hzb.erp.annotation.Log;
import com.hzb.erp.annotation.PreventMultiSubmit;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springcrazy.common.pojo.PaginationVO;
import org.springcrazy.common.tool.CommonUtil;
import org.springcrazy.common.tool.JsonResponse;
import org.springcrazy.common.tool.JsonResponseUtil;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.pojo.BookParamDTO;
import org.springcrazy.modules.book.pojo.BookRecordCommentSaveDTO;
import org.springcrazy.modules.book.pojo.BookSaveDTO;
import org.springcrazy.modules.book.service.BookService;
import org.springcrazy.modules.book.wrapper.BookWrapper;
import org.springcrazy.modules.cms.entity.Article;
import org.springcrazy.modules.cms.wrapper.ArticleWrapper;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 图书 前端控制器
 * </p>
 *
 * @author Ryan
 */
@RestController
@AllArgsConstructor
@RequestMapping("/book")
@Api(value = "图书", tags = "图书")
public class BookController extends CrazyController {


    private BookService bookService;

//    private HomeworkRecordService homeworkRecordService;

    @ApiOperation("图书列表")
    @GetMapping("/list")
    public Object list(@ApiIgnore @RequestParam Map<String, Object> bookParamDTO, Query query) {

        IPage<Book> pages = bookService.page(Condition.getPage(query), Condition.getQueryWrapper(bookParamDTO, Book.class).lambda());
        return R.data(BookWrapper.build().pageVO(pages));
    }

    @ApiOperation("图书列表")
    @GetMapping("/search")
    public Object search(@ApiIgnore @RequestParam Map<String, Object> bookParamDTO) {
        Query query = new Query();
        query.setSize(10);
        query.setCurrent(1);
        IPage<Book> pages = bookService.page(Condition.getPage(query), Condition.getQueryWrapper(bookParamDTO, Book.class).lambda());
        return R.data(BookWrapper.build().pageVO(pages).getRecords());
    }


    @ApiOperation("创建和修改图书")
    @Log(description = "创建和修改图书", type = "图书管理")
    @PostMapping("/submit")
    @PreventMultiSubmit
    public JsonResponse save(@Valid @RequestBody BookSaveDTO dto, BindingResult result) {
        CommonUtil.handleValidMessage(result);
        bookService.saveOrUpdateByDTO(dto);
        return JsonResponseUtil.success();
    }

    @ApiOperation("删除图书")
    @Log(description = "删除图书", type = "图书管理")
    @PostMapping("/delete")
    public JsonResponse delete(@RequestBody List<Long> ids) {
        if (bookService.removeByIds(ids)) {
            return JsonResponseUtil.success();
        } else {
            return JsonResponseUtil.error("删除失败");
        }
    }

//    @ApiOperation("图书提交列表")
//    @GetMapping("/recordsList")
//    public PaginationVO recordsList(
//            @RequestParam(value = "page", defaultValue = "") Integer page,
//            @RequestParam(value = "pageSize", defaultValue = "30") Integer pageSize,
//            @RequestParam(value = "homeworkId", defaultValue = "") Long homeworkId) {
//
//        BookParamDTO param = new BookParamDTO();
//        param.setPage(page);
//        param.setPageSize(pageSize);
//        param.setHomeworkId(homeworkId);
//
//        return JsonResponseUtil.paginate(homeworkRecordService.getList(param));
//    }
//
//    @ApiOperation("图书评价")
//    @Log(description = "图书评价", type = "图书管理")
//    @PostMapping("/recordComment")
//    public JsonResponse recordComment(@Valid @RequestBody BookRecordCommentSaveDTO dto, BindingResult result) {
//        CommonUtil.handleValidMessage(result);
//        if (homeworkRecordService.comment(dto, Long.parseLong(SecureUtil.getUserId()+""))) {
//            return JsonResponseUtil.success();
//        } else {
//            return JsonResponseUtil.error("操作失败");
//        }
//    }


}
