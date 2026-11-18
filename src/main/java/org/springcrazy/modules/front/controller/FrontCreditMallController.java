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
import org.springcrazy.core.tool.utils.StringUtil;
import org.springcrazy.modules.book.entity.Book;
import org.springcrazy.modules.book.entity.BookCollection;
import org.springcrazy.modules.book.pojo.BookParamDTO;
import org.springcrazy.modules.book.pojo.BookVO;
import org.springcrazy.modules.book.service.BookCollectionService;
import org.springcrazy.modules.book.service.BookService;
import org.springcrazy.modules.book.wrapper.BookWrapper;
import org.springcrazy.modules.cms.entity.Comment;
import org.springcrazy.modules.cms.service.ICommentService;
import org.springcrazy.modules.creditMall.entity.CreditExchange;
import org.springcrazy.modules.creditMall.entity.CreditMall;
import org.springcrazy.modules.creditMall.pojo.CreditExchangeDTO;
import org.springcrazy.modules.creditMall.pojo.CreditExchangeParamDTO;
import org.springcrazy.modules.creditMall.pojo.CreditMallParamDTO;
import org.springcrazy.modules.creditMall.service.CreditExchangeService;
import org.springcrazy.modules.creditMall.service.CreditMallService;
import org.springcrazy.modules.creditMall.wrapper.CreditMallWrapper;
import org.springcrazy.modules.readtask.entity.ReadTaskRecord;
import org.springcrazy.modules.readtask.pojo.ReadTaskRecordSaveDTO;
import org.springcrazy.modules.readtask.service.ReadTaskRecordService;
import org.springcrazy.modules.user.entity.Student;
import org.springcrazy.modules.user.service.IStudentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/front/creditMall")
@Api(value = "前端考试接口", tags = "前端考试接口")
public class FrontCreditMallController extends CrazyController {
    private ICommentService commentService;

    private CreditMallService creditMallService;
    private CreditExchangeService creditExchangeService;

    private IStudentService studentService;

    private ReadTaskRecordService readTaskRecordService;

    @ApiOperation("积分商城积分礼品列表")
    @GetMapping("/gifts")
    public R gifts(CreditMallParamDTO param, Query query) {

        QueryWrapper<CreditMall> queryWrapper   = new QueryWrapper<>();
        if(StringUtil.isNotBlank(param.getSearchKey())){
            queryWrapper.lambda().like(CreditMall::getName,param.getSearchKey()).or(
                    e->e.like(CreditMall::getContent,param.getSearchKey())
            ).orderByDesc(CreditMall::getSort);
        }
        IPage<CreditMall> pages = creditMallService.page(Condition.getPage(query), queryWrapper);
        return R.data(CreditMallWrapper.build().pageVO(pages));
    }


//    @GetMapping("/info")
//    @ApiOperationSupport(order = 1)
//    @ApiOperation(value = "详情", notes = "传入article")
//    public R<BookVO> detail(BookParamDTO bookParamDTO) {
//        Book detail = bookService.getById(bookParamDTO.getBookId());
//        BookVO bookVO = BookWrapper.build().entityVO(detail);
//
//        QueryWrapper<ReadTaskRecord> readTaskQueryWrapper = new QueryWrapper<>();
//        readTaskQueryWrapper.lambda()
//                .eq(ReadTaskRecord::getBookId,bookParamDTO.getBookId())
//                .eq(ReadTaskRecord::getUserId,SecureUtil.getUserId());
//
//        Integer readTaskRecordCount = readTaskRecordService.count(readTaskQueryWrapper);
//        bookVO.setReadCount(readTaskRecordCount);
//
//        //增加点击量
//        detail.setClick(detail.getClick()+1);
//        bookService.updateById(detail);
//
//        return R.data(bookVO);
//    }
//
//
//    @ApiOperation("开始阅读")
//    @PostMapping("/read")
//    public R addComment(BookParamDTO bookParamDTO) {
//        Book detail = bookService.getById(bookParamDTO.getBookId());
//        //增加点击量
//        detail.setClick(detail.getClick()+1);
//        bookService.updateById(detail);
//        bookService.saveOrUpdate(detail);
//
//        ReadTaskRecordSaveDTO readTaskRecord = new ReadTaskRecordSaveDTO();
//        readTaskRecord.setUserId(SecureUtil.getUserId());
//        readTaskRecord.setBookId(bookParamDTO.getBookId());
//        readTaskRecord.setTaskCompleteType(0);
//        readTaskRecord.setTaskId(bookParamDTO.getTaskId());
//        readTaskRecordService.saveRecord(readTaskRecord);
//
//        //任务完成记录
//        return R.success("ok");
//
//    }


    @ApiOperation("兑换积分")
    @GetMapping("/exchange")
    public R list(CreditExchangeParamDTO bookParamDTO) {
        CreditExchangeDTO creditExchangeDTO = new CreditExchangeDTO();
        creditExchangeDTO.setId(bookParamDTO.getCreditMallId());
        creditExchangeDTO.setUserId(SecureUtil.getUserId());
        creditExchangeDTO.setStudentId(bookParamDTO.getStudentId());

        creditMallService.exchange(creditExchangeDTO);

        return R.success("操作成功");
    }


//    @ApiOperation("收藏图书数量")
//    @GetMapping("/collectionedCount")
//    public R collectionedCount(BookParamDTO bookParamDTO) {
//
//        QueryWrapper<BookCollection> bookCollectionQueryWrapper = new QueryWrapper<>();
//        bookCollectionQueryWrapper.lambda().eq(BookCollection::getUserId, SecureUtil.getUserId())
//                .eq(BookCollection::getBookId,bookParamDTO.getBookId());
//
//       int count =  bookCollectionService.count(bookCollectionQueryWrapper);
//
//        return R.data(count);
//    }
}
