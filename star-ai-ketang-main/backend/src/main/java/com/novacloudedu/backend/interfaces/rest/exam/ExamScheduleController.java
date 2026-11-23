package com.novacloudedu.backend.interfaces.rest.exam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novacloudedu.backend.annotation.AuthCheck;
import com.novacloudedu.backend.common.BaseResponse;
import com.novacloudedu.backend.common.ResultUtils;
import com.novacloudedu.backend.exception.BusinessException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamScheduleController {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;

    @GetMapping
    @AuthCheck(mustRole = "teacher")
    public BaseResponse<List<Map<String, Object>>> listTeacherExams(Authentication authentication) {
        long teacherId = userId(authentication);
        return ResultUtils.success(jdbc.queryForList("""
            SELECT e.id, e.title AS name, e.course_id AS "courseId", c.title AS course,
              e.paper_id AS "paperId", e.starts_at AS "start", e.ends_at AS "end",
              e.duration_minutes AS duration, e.class_ids_json::text AS "classIds",
              e.settings_json::text AS settings, e.status,
              COUNT(DISTINCT s.id) AS attend, AVG(s.score) AS avg
            FROM exam_schedule e JOIN course c ON c.id=e.course_id
            LEFT JOIN exam_submission s ON s.exam_id=e.id
            WHERE e.teacher_id=? GROUP BY e.id,c.title ORDER BY e.starts_at DESC
            """, teacherId));
    }

    @PostMapping
    @AuthCheck(mustRole = "teacher")
    @Transactional
    public BaseResponse<Long> create(@Valid @RequestBody ScheduleRequest request, Authentication authentication) {
        long teacherId = userId(authentication);
        checkOwnership(teacherId, request.courseId(), request.classIds());
        Long id = jdbc.queryForObject("""
            INSERT INTO exam_schedule(teacher_id,course_id,paper_id,title,starts_at,ends_at,duration_minutes,class_ids_json,settings_json,status)
            VALUES(?,?,?,?,?,?,?,CAST(? AS jsonb),CAST(? AS jsonb),'SCHEDULED') RETURNING id
            """, Long.class, teacherId, request.courseId(), request.paperId(), request.title().trim(), request.startsAt(), request.endsAt(),
            request.durationMinutes(), json(request.classIds()), json(request.settings()));
        return ResultUtils.success(id);
    }

    @PutMapping("/{id}")
    @AuthCheck(mustRole = "teacher")
    @Transactional
    public BaseResponse<Boolean> update(@PathVariable long id, @Valid @RequestBody ScheduleRequest request, Authentication authentication) {
        long teacherId = userId(authentication);
        requireOwner(id, teacherId);
        if (jdbc.queryForObject("SELECT COUNT(*) FROM exam_submission WHERE exam_id=?", Long.class, id) > 0)
            throw new BusinessException(40000, "已有学生提交的考试不能修改安排");
        checkOwnership(teacherId, request.courseId(), request.classIds());
        jdbc.update("""
            UPDATE exam_schedule SET course_id=?,paper_id=?,title=?,starts_at=?,ends_at=?,duration_minutes=?,
              class_ids_json=CAST(? AS jsonb),settings_json=CAST(? AS jsonb),updated_at=CURRENT_TIMESTAMP
            WHERE id=? AND teacher_id=?
            """, request.courseId(), request.paperId(), request.title().trim(), request.startsAt(), request.endsAt(),
            request.durationMinutes(), json(request.classIds()), json(request.settings()), id, teacherId);
        return ResultUtils.success(true);
    }

    @DeleteMapping("/{id}")
    @AuthCheck(mustRole = "teacher")
    public BaseResponse<Boolean> delete(@PathVariable long id, Authentication authentication) {
        requireOwner(id, userId(authentication));
        jdbc.update("DELETE FROM exam_schedule WHERE id=?", id);
        return ResultUtils.success(true);
    }

    @GetMapping("/my")
    public BaseResponse<List<Map<String, Object>>> listStudentExams(Authentication authentication) {
        long studentId = userId(authentication);
        return ResultUtils.success(jdbc.queryForList("""
            SELECT e.id,e.title AS name,c.title AS course,e.paper_id AS "paperId",e.starts_at AS "start",e.ends_at AS "end",
              e.duration_minutes AS duration,p.total_score AS score,
              CASE WHEN s.id IS NOT NULL THEN 'DONE' WHEN CURRENT_TIMESTAMP < e.starts_at THEN 'TODO'
                   WHEN CURRENT_TIMESTAMP <= e.ends_at THEN 'ONGOING' ELSE 'CLOSED' END AS "examState",
              s.score AS "myScore"
            FROM exam_schedule e JOIN course c ON c.id=e.course_id
            LEFT JOIN exam_paper p ON p.id=e.paper_id
            JOIN LATERAL jsonb_array_elements_text(e.class_ids_json) class_id(value) ON TRUE
            JOIN class_member cm ON cm.class_id=CAST(class_id.value AS BIGINT) AND cm.user_id=? AND UPPER(cm.role)='STUDENT' AND cm.is_delete=0
            LEFT JOIN exam_submission s ON s.exam_id=e.id AND s.student_id=?
            GROUP BY e.id,c.title,p.total_score,s.id ORDER BY e.starts_at DESC
            """, studentId, studentId));
    }

    @GetMapping("/{id}/paper")
    public BaseResponse<Map<String, Object>> getStudentPaper(@PathVariable long id, Authentication authentication) {
        long studentId = userId(authentication);
        Map<String, Object> exam = jdbc.queryForMap("""
            SELECT e.paper_id, e.starts_at, e.ends_at, EXISTS(
              SELECT 1 FROM jsonb_array_elements_text(e.class_ids_json) x(value)
              JOIN class_member cm ON cm.class_id=CAST(x.value AS BIGINT) AND cm.user_id=? AND UPPER(cm.role)='STUDENT' AND cm.is_delete=0
            ) AS member FROM exam_schedule e WHERE e.id=?
            """, studentId, id);
        if (!Boolean.TRUE.equals(exam.get("member"))) throw new BusinessException(40300, "你不在本场考试的参考班级中");
        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore((LocalDateTime) exam.get("starts_at")) || now.isAfter((LocalDateTime) exam.get("ends_at")))
            throw new BusinessException(40000, "当前不在考试时间窗口内");
        if (!(exam.get("paper_id") instanceof Number)) throw new BusinessException(40000, "本场考试尚未配置试卷");
        Long paperId = ((Number) exam.get("paper_id")).longValue();
        List<Map<String, Object>> rows = jdbc.queryForList("""
            SELECT ps.id AS "sectionId", ps.title AS "sectionTitle", ps.description AS "sectionDescription",
                   ps.question_type AS "questionType", pq.score, pq.sort_order AS "sortOrder",
                   q.id AS "questionId", q.content AS stem, q.type AS kind, q.options::text AS options
            FROM paper_section ps JOIN paper_question pq ON pq.section_id=ps.id
            JOIN question q ON q.id=pq.question_id
            WHERE ps.paper_id=? ORDER BY ps.sort_order,pq.sort_order
            """, paperId);
        Map<Long, Map<String, Object>> sectionMap = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            Long sectionId = ((Number) row.get("sectionId")).longValue();
            Map<String, Object> section = sectionMap.computeIfAbsent(sectionId, key -> {
                Map<String, Object> item = new LinkedHashMap<>();
                item.put("id", key); item.put("title", row.get("sectionTitle"));
                item.put("description", row.get("sectionDescription")); item.put("questionType", row.get("questionType"));
                item.put("questions", new ArrayList<Map<String, Object>>()); return item;
            });
            @SuppressWarnings("unchecked") List<Map<String, Object>> questions = (List<Map<String, Object>>) section.get("questions");
            Map<String, Object> question = new LinkedHashMap<>();
            question.put("id", row.get("questionId")); question.put("kind", row.get("kind"));
            question.put("stem", row.get("stem")); question.put("score", row.get("score"));
            question.put("options", readJson((String) row.get("options")));
            questions.add(question);
        }
        Map<String, Object> result = new LinkedHashMap<>(); result.put("sections", new ArrayList<>(sectionMap.values()));
        return ResultUtils.success(result);
    }

    @PostMapping("/{id}/submissions")
    @Transactional
    public BaseResponse<Boolean> submit(@PathVariable long id, @RequestBody SubmissionRequest request, Authentication authentication) {
        long studentId = userId(authentication);
        Map<String, Object> exam = jdbc.queryForMap("""
            SELECT e.ends_at, e.class_ids_json, EXISTS(
              SELECT 1 FROM jsonb_array_elements_text(e.class_ids_json) x(value)
              JOIN class_member cm ON cm.class_id=CAST(x.value AS BIGINT) AND cm.user_id=? AND UPPER(cm.role)='STUDENT' AND cm.is_delete=0
            ) AS member FROM exam_schedule e WHERE e.id=?
            """, studentId, id);
        if (!Boolean.TRUE.equals(exam.get("member"))) throw new BusinessException(40300, "你不在本场考试的参考班级中");
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter((LocalDateTime) exam.get("ends_at"))) throw new BusinessException(40000, "考试已结束，不能交卷");
        if (now.isBefore((LocalDateTime) exam.get("starts_at"))) throw new BusinessException(40000, "考试尚未开始");
        jdbc.update("""
            INSERT INTO exam_submission(exam_id,student_id,answers_json,submitted_at)
            VALUES(?,?,CAST(? AS jsonb),CURRENT_TIMESTAMP)
            ON CONFLICT(exam_id,student_id) DO UPDATE SET answers_json=EXCLUDED.answers_json,submitted_at=EXCLUDED.submitted_at
            """, id, studentId, json(request.answers()));
        return ResultUtils.success(true);
    }

    private void checkOwnership(long teacherId, long courseId, List<Long> classIds) {
        if (jdbc.queryForObject("SELECT COUNT(*) FROM course WHERE id=? AND teacher_id=?", Long.class, courseId, teacherId) == 0)
            throw new BusinessException(40300, "无权为该课程安排考试");
        if (classIds == null || classIds.isEmpty()) throw new BusinessException(40000, "至少选择一个参考班级");
        for (Long classId : classIds) if (jdbc.queryForObject("SELECT COUNT(*) FROM class_info WHERE id=? AND creator_id=?", Long.class, classId, teacherId) == 0)
            throw new BusinessException(40300, "参考班级不存在或无权管理");
    }
    private void requireOwner(long id, long teacherId) {
        if (jdbc.queryForObject("SELECT COUNT(*) FROM exam_schedule WHERE id=? AND teacher_id=?", Long.class, id, teacherId) == 0)
            throw new BusinessException(40400, "考试不存在");
    }
    private long userId(Authentication auth) { return Long.parseLong(auth.getName()); }
    private String json(Object value) {
        try { return mapper.writeValueAsString(value == null ? Map.of() : value); }
        catch (JsonProcessingException e) { throw new IllegalArgumentException("JSON序列化失败", e); }
    }
    private Object readJson(String value) {
        if (value == null || value.isBlank()) return List.of();
        try { return mapper.readValue(value, Object.class); }
        catch (JsonProcessingException e) { return List.of(); }
    }
    public record ScheduleRequest(@NotNull Long courseId, Long paperId, @NotBlank String title,
                                  @NotNull LocalDateTime startsAt, @NotNull LocalDateTime endsAt,
                                  @NotNull Integer durationMinutes, @NotEmpty List<Long> classIds,
                                  Map<String, Object> settings) {}
    public record SubmissionRequest(Map<String, Object> answers) {}
}
