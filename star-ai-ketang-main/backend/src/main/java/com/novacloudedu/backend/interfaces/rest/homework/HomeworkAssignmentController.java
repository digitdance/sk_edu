package com.novacloudedu.backend.interfaces.rest.homework;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novacloudedu.backend.annotation.AuthCheck;
import com.novacloudedu.backend.application.service.UserApplicationService;
import com.novacloudedu.backend.common.BaseResponse;
import com.novacloudedu.backend.common.ResultUtils;
import com.novacloudedu.backend.exception.BusinessException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/homework")
@RequiredArgsConstructor
public class HomeworkAssignmentController {
    private final JdbcTemplate jdbc;
    private final ObjectMapper mapper;
    private final UserApplicationService users;

    @GetMapping("/assignments")
    @AuthCheck(mustRole = "teacher")
    public BaseResponse<List<Map<String, Object>>> teacherAssignments() {
        long teacherId = currentUserId();
        return ResultUtils.success(jdbc.queryForList("""
            SELECT a.id, a.title, a.description, a.course_id AS \"courseId\", c.title AS \"courseName\",
                   a.chapter_id AS \"chapterId\", a.section_id AS \"sectionId\", a.total_score AS \"totalScore\",
                   a.pass_score AS \"passScore\", a.deadline, a.status, a.settings_json::text AS settings,
                   COUNT(DISTINCT ac.class_id) AS \"classCount\", COUNT(DISTINCT s.id) AS \"submissionCount\",
                   COALESCE(to_json(array_agg(DISTINCT ac.class_id) FILTER (WHERE ac.class_id IS NOT NULL))::text, '[]') AS \"classIds\",
                   a.create_time AS \"createTime\"
            FROM homework_assignment a
            LEFT JOIN course c ON c.id = a.course_id
            LEFT JOIN homework_assignment_class ac ON ac.assignment_id = a.id
            LEFT JOIN homework_assignment_submission s ON s.assignment_id = a.id
            WHERE a.teacher_id = ?
            GROUP BY a.id, c.title
            ORDER BY a.update_time DESC
            """, teacherId));
    }

    @PostMapping("/assignments")
    @AuthCheck(mustRole = "teacher")
    @Transactional
    public BaseResponse<Long> create(@Valid @RequestBody AssignmentRequest request) {
        long teacherId = currentUserId();
        checkCourseAndClasses(teacherId, request.courseId(), request.classIds());
        Long id = jdbc.queryForObject("""
            INSERT INTO homework_assignment
              (teacher_id, course_id, chapter_id, section_id, title, description, total_score, pass_score, deadline, status, settings_json)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, 'DRAFT', CAST(? AS jsonb)) RETURNING id
            """, Long.class, teacherId, request.courseId(), request.chapterId(), request.sectionId(), request.title().trim(),
            request.description(), request.totalScore(), request.passScore(), request.deadline(), json(request.settings()));
        replaceClasses(id, request.classIds());
        return ResultUtils.success(id);
    }

    @PutMapping("/assignments/{id}")
    @AuthCheck(mustRole = "teacher")
    @Transactional
    public BaseResponse<Boolean> update(@PathVariable long id, @Valid @RequestBody AssignmentRequest request) {
        long teacherId = currentUserId();
        requireOwner(id, teacherId);
        checkCourseAndClasses(teacherId, request.courseId(), request.classIds());
        jdbc.update("""
            UPDATE homework_assignment SET course_id=?, chapter_id=?, section_id=?, title=?, description=?,
              total_score=?, pass_score=?, deadline=?, settings_json=CAST(? AS jsonb), update_time=CURRENT_TIMESTAMP
            WHERE id=? AND teacher_id=?
            """, request.courseId(), request.chapterId(), request.sectionId(), request.title().trim(), request.description(),
            request.totalScore(), request.passScore(), request.deadline(), json(request.settings()), id, teacherId);
        replaceClasses(id, request.classIds());
        return ResultUtils.success(true);
    }

    @PostMapping("/assignments/{id}/publish")
    @AuthCheck(mustRole = "teacher")
    public BaseResponse<Boolean> publish(@PathVariable long id) {
        long teacherId = currentUserId();
        requireOwner(id, teacherId);
        if (jdbc.queryForObject("SELECT COUNT(*) FROM homework_assignment_class WHERE assignment_id=?", Long.class, id) == 0)
            throw new BusinessException(40000, "至少选择一个班级");
        jdbc.update("UPDATE homework_assignment SET status='PUBLISHED', update_time=CURRENT_TIMESTAMP WHERE id=? AND teacher_id=?", id, teacherId);
        return ResultUtils.success(true);
    }

    @DeleteMapping("/assignments/{id}")
    @AuthCheck(mustRole = "teacher")
    @Transactional
    public BaseResponse<Boolean> delete(@PathVariable long id) {
        long teacherId = currentUserId();
        requireOwner(id, teacherId);
        jdbc.update("DELETE FROM homework_assignment WHERE id=? AND teacher_id=?", id, teacherId);
        return ResultUtils.success(true);
    }

    @GetMapping("/my-assignments")
    @AuthCheck(mustRole = "student")
    public BaseResponse<List<Map<String, Object>>> studentAssignments() {
        long studentId = currentUserId();
        return ResultUtils.success(jdbc.queryForList("""
            SELECT DISTINCT ON (a.id) a.id, a.title, a.description, a.course_id AS \"courseId\", c.title AS \"courseName\",
                   a.chapter_id AS \"chapterId\", a.section_id AS \"sectionId\", a.total_score AS \"totalScore\",
                   a.pass_score AS \"passScore\", a.deadline, a.settings_json::text AS settings,
                   latest.id AS \"submissionId\", latest.status AS \"submissionStatus\", latest.score,
                   latest.teacher_comment AS \"teacherComment\", latest.submitted_at AS \"submittedAt\",
                   latest.answers_json::text AS answers, latest.attachment_urls_json::text AS attachments
            FROM homework_assignment a
            JOIN homework_assignment_class ac ON ac.assignment_id=a.id
            JOIN class_member cm ON cm.class_id=ac.class_id AND cm.user_id=? AND UPPER(cm.role)='STUDENT' AND cm.is_delete=0
            JOIN course c ON c.id=a.course_id
            LEFT JOIN LATERAL (
              SELECT id, status, score, teacher_comment, submitted_at, answers_json, attachment_urls_json FROM homework_assignment_submission
              WHERE assignment_id=a.id AND student_id=? ORDER BY attempt_no DESC LIMIT 1
            ) latest ON TRUE
            WHERE a.status='PUBLISHED' AND (a.deadline IS NULL OR a.deadline >= CURRENT_TIMESTAMP
              OR COALESCE((a.settings_json->>'allowLate')::boolean, false) OR latest.id IS NOT NULL)
            ORDER BY a.id, a.deadline NULLS LAST, a.create_time DESC
            """, studentId, studentId));
    }

    @PostMapping("/assignments/{id}/submissions")
    @AuthCheck(mustRole = "student")
    @Transactional
    public BaseResponse<Long> submit(@PathVariable long id, @RequestBody @Valid SubmissionRequest request) {
        long studentId = currentUserId();
        Map<String, Object> assignment = jdbc.queryForMap("""
            SELECT a.id, a.status, a.settings_json::text AS settings,
                   (a.deadline IS NOT NULL AND a.deadline < CURRENT_TIMESTAMP) AS expired
            FROM homework_assignment a JOIN homework_assignment_class ac ON ac.assignment_id=a.id
            JOIN class_member cm ON cm.class_id=ac.class_id AND cm.user_id=? AND UPPER(cm.role)='STUDENT' AND cm.is_delete=0
            WHERE a.id=?
            """, studentId, id);
        if (!"PUBLISHED".equals(assignment.get("status"))) throw new BusinessException(40300, "作业尚未发布");
        Map<?, ?> settings = parseSettings((String) assignment.get("settings"));
        if (Boolean.TRUE.equals(assignment.get("expired")) && !Boolean.TRUE.equals(settings.get("allowLate")))
            throw new BusinessException(40300, "作业已截止");
        Integer attempts = jdbc.queryForObject("SELECT COALESCE(MAX(attempt_no),0) FROM homework_assignment_submission WHERE assignment_id=? AND student_id=?", Integer.class, id, studentId);
        int nextAttempt = (attempts == null ? 0 : attempts) + 1;
        int attemptLimit = settings.get("allowRetake") instanceof Boolean allowed && allowed
                ? number(settings.get("retakeCount"), 2) + 1 : 1;
        if (nextAttempt > attemptLimit) throw new BusinessException(40300, "已达到作业提交次数限制");
        Long submissionId = jdbc.queryForObject("""
            INSERT INTO homework_assignment_submission (assignment_id, student_id, attempt_no, answers_json, attachment_urls_json, status)
            VALUES (?, ?, ?, CAST(? AS jsonb), CAST(? AS jsonb), 'SUBMITTED') RETURNING id
            """, Long.class, id, studentId, nextAttempt, json(request.answers()), json(request.attachmentUrls() == null ? List.of() : request.attachmentUrls()));
        return ResultUtils.success(submissionId);
    }

    @GetMapping("/assignments/{id}/submissions")
    @AuthCheck(mustRole = "teacher")
    public BaseResponse<List<Map<String, Object>>> submissions(@PathVariable long id) {
        requireOwner(id, currentUserId());
        return ResultUtils.success(jdbc.queryForList("""
            SELECT s.id, s.student_id AS \"studentId\", u.user_name AS \"studentName\", u.user_account AS \"studentAccount\",
                   s.attempt_no AS \"attemptNo\", s.answers_json::text AS answers, s.attachment_urls_json::text AS attachments,
                   s.status, s.score, s.teacher_comment AS \"teacherComment\", s.submitted_at AS \"submittedAt\", s.graded_at AS \"gradedAt\"
            FROM homework_assignment_submission s JOIN \"user\" u ON u.id=s.student_id
            WHERE s.assignment_id=? ORDER BY s.submitted_at
            """, id));
    }

    @PostMapping("/submissions/{submissionId}/grade")
    @AuthCheck(mustRole = "teacher")
    public BaseResponse<Boolean> grade(@PathVariable long submissionId, @RequestBody @Valid GradeRequest request) {
        long teacherId = currentUserId();
        int changed = jdbc.update("""
            UPDATE homework_assignment_submission s SET score=?, teacher_comment=?, status='GRADED', graded_at=CURRENT_TIMESTAMP
            FROM homework_assignment a WHERE s.assignment_id=a.id AND s.id=? AND a.teacher_id=?
              AND ? >= 0 AND ? <= a.total_score
            """, request.score(), request.comment(), submissionId, teacherId, request.score(), request.score());
        if (changed == 0) throw new BusinessException(40400, "提交不存在或分数超出范围");
        return ResultUtils.success(true);
    }

    private long currentUserId() { return users.getCurrentUser().getId().value(); }
    private void requireOwner(long id, long teacherId) {
        if (jdbc.queryForObject("SELECT COUNT(*) FROM homework_assignment WHERE id=? AND teacher_id=?", Long.class, id, teacherId) == 0)
            throw new BusinessException(40400, "作业不存在");
    }
    private void checkCourseAndClasses(long teacherId, long courseId, List<Long> classIds) {
        if (jdbc.queryForObject("SELECT COUNT(*) FROM course WHERE id=? AND teacher_id=?", Long.class, courseId, teacherId) == 0)
            throw new BusinessException(40300, "无权为此课程布置作业");
        if (classIds == null || classIds.isEmpty()) throw new BusinessException(40000, "至少选择一个班级");
        for (Long classId : classIds) {
            if (jdbc.queryForObject("SELECT COUNT(*) FROM class_info WHERE id=? AND creator_id=?", Long.class, classId, teacherId) == 0)
                throw new BusinessException(40300, "班级不存在或不属于当前教师");
        }
    }
    private void replaceClasses(long id, List<Long> classIds) {
        jdbc.update("DELETE FROM homework_assignment_class WHERE assignment_id=?", id);
        for (Long classId : classIds) jdbc.update("INSERT INTO homework_assignment_class (assignment_id, class_id) VALUES (?, ?)", id, classId);
    }
    private String json(Object value) {
        try { return mapper.writeValueAsString(value == null ? Map.of() : value); }
        catch (JsonProcessingException e) { throw new IllegalArgumentException("Invalid JSON payload", e); }
    }
    private Map<?, ?> parseSettings(String value) {
        try { return value == null ? Map.of() : mapper.readValue(value, Map.class); }
        catch (JsonProcessingException e) { throw new IllegalStateException("Stored homework settings are invalid", e); }
    }
    private int number(Object value, int fallback) { return value instanceof Number n ? n.intValue() : fallback; }

    public record AssignmentRequest(@NotNull Long courseId, Long chapterId, Long sectionId,
                                    @NotBlank String title, String description,
                                    @NotNull Integer totalScore, Integer passScore,
                                    LocalDateTime deadline, @NotEmpty List<Long> classIds,
                                    Map<String, Object> settings) {}
    public record SubmissionRequest(Map<String, Object> answers, List<String> attachmentUrls) {}
    public record GradeRequest(@NotNull BigDecimal score, String comment) {}
}
