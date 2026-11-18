//package org.springcrazy.common.service;
//
//import com.hzb.erp.sysservice.SmsManager;
//import com.hzb.erp.sysservice.dto.SmsSendDTO;
//import com.hzb.erp.sysservice.notification.NoticeCodeEnum;
//import com.hzb.erp.sysservice.notification.SmsNoticeSender;
//import com.hzb.erp.sysservice.notification.bo.*;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.HashMap;
//import java.util.Map;
//
///**
// * @author Ryan 541720500@qq.com
// * description 短信发送器
// */
//@Service
//@Slf4j
//public class SmsNoticeSenderImpl implements SmsNoticeSender {
//
//    @Autowired
//    private SmsManager smsManager;
//
//    /**
//     * 发送消息
//     */
//    @Override
//    public Boolean send(String mobile, String smsId, NoticeBO param, NoticeCodeEnum code) {
//
//        Map<String, Object> dataMap = new HashMap<>();
//        String content;
//        switch (code) {
//            case STUDENT_LESSON_START:
//            case TEACHER_LESSON_START:
//                LessonStartBO bo1 = (LessonStartBO) param;
//                log.info("=====bo1");
//                log.info(bo1.toString());
//                content = bo1.getContent();
//                break;
//            case STUDENT_SIGN:
//                StudentSignBO bo2 = (StudentSignBO) param;
//                log.info("=====bo2");
//                log.info(bo2.toString());
//                content = bo2.getContent();
//                break;
//            case STUDENT_LESSON_ONCHANGE:
//                LessonChangeBO bo3 = (LessonChangeBO) param;
//                log.info("=====bo3");
//                log.info(bo3.toString());
//                content = bo3.getContent();
//                break;
//            case STUDENT_NEW_CONTRACT:
//                NewContractBO bo4 = (NewContractBO) param;
//                log.info("=====bo4");
//                log.info(bo4.toString());
//                content = bo4.getContent();
//                break;
//            case STUDENT_LESSON_COUNT_LESS:
//            case TEACHER_STUDENT_LESSONLESS:
//                LessonNotEnoughBO bo5 = (LessonNotEnoughBO) param;
//                log.info("=====bo5");
//                log.info(bo5.toString());
//                content = bo5.getContent();
//                break;
//
//            case TEACHER_STUDENT_LEAVE:
//                StudentLeaveBO bo6 = (StudentLeaveBO) param;
//                log.info("=====bo6");
//                log.info(bo6.toString());
//                content = bo6.getContent();
//                break;
//
//            case TEACHER_LESSON_ONCHANGE:
//                LessonChangeBO bo7 = (LessonChangeBO) param;
//                log.info("=====bo7");
//                log.info(bo7.toString());
//                content = bo7.getContent();
//                break;
//            default:
//                throw new RuntimeException("未知NoticeBO类型");
//        }
//
//        log.info("=======短信发送消息===========");
//
//        if (StringUtils.isBlank(mobile)) {
//            return null;
//        }
//        SmsSendDTO dto = new SmsSendDTO();
//        dto.setMobile(mobile);
//        dto.setScene(code.getCode());
//        dto.setContent(content);
//        dto.setDataMap(dataMap);
//        dto.setTempId(smsId);
//
//        log.info(dto.toString());
//        smsManager.send(dto);
//
//        return null;
//    }
//
//}
