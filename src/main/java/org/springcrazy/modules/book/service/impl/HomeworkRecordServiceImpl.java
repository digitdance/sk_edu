//package org.springcrazy.modules.book.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
//import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
//import org.springcrazy.modules.book.pojo.BookParamDTO;
//import org.springcrazy.modules.book.pojo.BookRecordCommentSaveDTO;
//import org.springcrazy.modules.book.pojo.BookRecordSaveDTO;
//import org.springcrazy.modules.book.pojo.BookRecordVO;
//import org.springcrazy.modules.book.service.HomeworkRecordService;
//import org.springframework.stereotype.Service;
//
//import java.time.LocalDateTime;
//
///**
// * <p>
// * 作业提交记录 服务实现类
// * </p>
// *
// * @author Ryan
// */
//@Service
//public class HomeworkRecordServiceImpl extends ServiceImpl<HomeworkRecordMapper, HomeworkRecord> implements HomeworkRecordService {
//
//    @Override
//    public boolean comment(BookRecordCommentSaveDTO dto, Long teacherId) {
//        HomeworkRecord item = getById(dto.getId());
//        item.setScore(dto.getScore());
//        item.setComment(dto.getComment());
//        item.setCommentTime(LocalDateTime.now());
//        item.setCommentTeacher(teacherId);
//        return this.updateById(item);
//    }
//
//    @Override
//    public boolean saveRecord(BookRecordSaveDTO dto) {
//        String content = "";
//        content += "<p>" + dto.getContent() + "</p>";
//        if (dto.getImgList() != null && dto.getImgList().size() > 0) {
//            for (String url : dto.getImgList()) {
//                content += "<p><img src='" + url + "' style='width:100%'></p>";
//            }
//        }
//        HomeworkRecord item = new HomeworkRecord();
//        item.setHomeworkId(dto.getHomeworkId());
//        item.setStudentId(dto.getStudentId());
//        item.setContent(content);
//        item.setAddTime(LocalDateTime.now());
//        return this.save(item);
//    }
//
//    @Override
//    public IPage<BookRecordVO> getList(BookParamDTO param) {
//        return this.baseMapper.getList(new Page<>(param.getPage(), param.getPageSize()), param);
//    }
//
//    @Override
//    public boolean deleteByStudentId(Long id, Long studentId) {
//        QueryWrapper<HomeworkRecord> qw = new QueryWrapper<>();
//        qw.eq("id", id).eq("student_id", studentId);
//        return remove(qw);
//    }
//
//
//}
