package org.springcrazy.modules.front.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springcrazy.core.boot.ctrl.CrazyController;
import org.springcrazy.core.mp.support.Condition;
import org.springcrazy.core.mp.support.Query;
import org.springcrazy.core.secure.utils.SecureUtil;
import org.springcrazy.core.tool.api.R;
import org.springcrazy.core.tool.api.ResultCode;
import org.springcrazy.core.tool.utils.Func;
import org.springcrazy.core.tool.utils.StringUtil;
import org.springcrazy.modules.cms.entity.Article;
import org.springcrazy.modules.cms.entity.Comment;
import org.springcrazy.modules.cms.service.IArticleService;
import org.springcrazy.modules.cms.service.ICommentService;
import org.springcrazy.modules.cms.vo.ArticleVO;
import org.springcrazy.modules.cms.vo.CommentVO;
import org.springcrazy.modules.creditMall.entity.CreditMall;
import org.springcrazy.modules.creditMall.pojo.CreditMallParamDTO;
import org.springcrazy.modules.creditMall.service.CreditMallService;
import org.springcrazy.modules.creditMall.wrapper.CreditMallWrapper;
import org.springcrazy.modules.edu.entity.Course;
import org.springcrazy.modules.edu.entity.CourseKpoint;
import org.springcrazy.modules.edu.service.ICourseKpointService;
import org.springcrazy.modules.edu.service.ICourseService;
import org.springcrazy.modules.edu.service.ISubjectService;
import org.springcrazy.modules.edu.vo.SubjectVO;
import org.springcrazy.modules.exam.entity.ExampaperRecord;
import org.springcrazy.modules.exam.wrapper.ExampaperRecordWrapper;
import org.springcrazy.modules.readtask.entity.ReadTaskRecord;
import org.springcrazy.modules.readtask.service.ReadTaskRecordService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/front/cms")
@Api(value = "前端用户相关接口", tags = "前端接口")
public class FrontCmsController extends CrazyController {

    private ISubjectService subjectService;

    private ReadTaskRecordService readTaskRecordService;
    private IArticleService articleService;
    CreditMallService creditMallService;
    private ICommentService commentService;
    private ICourseKpointService courseKpointService;
    private ICourseService courseService;
    @ApiOperation("我的留言评论")
    @GetMapping("/commentPage")
    public R myComment(Comment param, Query query) {

        int userId=SecureUtil.getUserId();
        //判断如果没有用户信息则进行登录
        if(Func.equals(userId,-1)){
            return R.fail(ResultCode.UN_AUTHORIZED);
        }
        param.setUserId(userId);
        IPage<Comment> pages = commentService.page(Condition.getPage(query), Condition.getQueryWrapper(param).lambda().orderByDesc(Comment::getAddtime));

        pages.getRecords().stream().map(item->{

           List<Comment> replys =   commentService.list(new LambdaQueryWrapper<Comment>()
                    .eq(Comment::getParentId,item.getId()));

            Course course =  courseService.getById(item.getOtherId());
            item.setCourse(course);
            item.setReplys(replys);
            return item;
        }).collect(Collectors.toList());

        return R.data(pages);

//
//       List commentService.list(new LambdaQueryWrapper<Comment>()
//                .eq(Comment::getUserId, SecureUtil.getUserId()));
//
//        if(param.getTaskRecordId()!=null){
//            ReadTaskRecord readTaskRecord =  readTaskRecordService.getById(param.getTaskRecordId());
//            readTaskRecord.setArticleId(param.getId());
//            readTaskRecord.setCompleteTime(new Date());
//            readTaskRecordService.saveOrUpdate(readTaskRecord);
//        }
//        return R.success("保存成功");
    }


    @ApiOperation("发布读后感")
    @PostMapping("/post")
    public R postArticle(ArticleVO param) {
        articleService.saveOrUpdate(param);

        if(param.getTaskRecordId()!=null){
            ReadTaskRecord readTaskRecord =  readTaskRecordService.getById(param.getTaskRecordId());
            readTaskRecord.setArticleId(param.getId());
            readTaskRecord.setCompleteTime(new Date());
            readTaskRecordService.saveOrUpdate(readTaskRecord);
        }
        return R.success("保存成功");
    }


    @ApiOperation("积分商品列表")
    @GetMapping("/contentsList")
    public R giftInfo(CreditMallParamDTO param) {

        CreditMall creditMall =  creditMallService.getById(param.getId());
        return R.data(CreditMallWrapper.build().entityVO(creditMall));

    }

}
