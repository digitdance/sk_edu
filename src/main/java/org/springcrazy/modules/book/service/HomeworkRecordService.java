//package org.springcrazy.modules.book.service;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.baomidou.mybatisplus.extension.service.IService;
//import org.springcrazy.modules.book.pojo.BookParamDTO;
//import org.springcrazy.modules.book.pojo.BookRecordCommentSaveDTO;
//import org.springcrazy.modules.book.pojo.BookRecordSaveDTO;
//import org.springcrazy.modules.book.pojo.BookRecordVO;
//
///**
// * <p>
// * 作业提交记录 服务类
// * </p>
// *
// * @author Ryan
// */
//public interface HomeworkRecordService extends IService<HomeworkRecord> {
//
//    boolean comment(BookRecordCommentSaveDTO dto, Long teacherId);
//
//    IPage<BookRecordVO> getList(BookParamDTO param);
//
//    boolean deleteByStudentId(Long id, Long studentId);
//
//    boolean saveRecord(BookRecordSaveDTO dto);
//}
