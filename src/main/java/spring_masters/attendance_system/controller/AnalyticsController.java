package spring_masters.attendance_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring_masters.attendance_system.dto.response.*;
import spring_masters.attendance_system.service.AnalyticsService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@CrossOrigin(origins = "*")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    // ==================== STUDENT ANALYTICS ====================

    /**
     * Get comprehensive analytics overview for a student
     * Accessible by: STUDENT (own data only)
     */
    @GetMapping("/student/{email}/overview")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<StudentAnalyticsResponse> getStudentOverview(@PathVariable String email) {
        StudentAnalyticsResponse response = analyticsService.getStudentOverview(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Get analytics for a specific student in a specific subject
     * Accessible by: STUDENT (own data only)
     */
    @GetMapping("/student/{email}/subject/{subjectId}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<SubjectWiseAttendance> getStudentSubjectAnalytics(
            @PathVariable String email,
            @PathVariable String subjectId) {
        SubjectWiseAttendance response = analyticsService.getStudentSubjectAnalytics(email, subjectId);
        return ResponseEntity.ok(response);
    }

    /**
     * Get attendance trends for a student over the last N days
     * Accessible by: STUDENT (own data only)
     */
    @GetMapping("/student/{email}/trends")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<AttendanceTrendResponse>> getStudentTrends(
            @PathVariable String email,
            @RequestParam(defaultValue = "30") int days) {
        List<AttendanceTrendResponse> trends = analyticsService.getStudentTrends(email, days);
        return ResponseEntity.ok(trends);
    }

    // ==================== TEACHER ANALYTICS ====================

    /**
     * Get comprehensive subject analytics for teachers
     * Accessible by: TEACHER
     */
    @GetMapping("/teacher/subject/{subjectId}/summary")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<SubjectAnalyticsResponse> getSubjectSummary(
            @PathVariable String subjectId,
            @RequestParam(defaultValue = "75.0") double threshold) {
        SubjectAnalyticsResponse response = analyticsService.getSubjectSummary(subjectId, threshold);
        return ResponseEntity.ok(response);
    }

    /**
     * Get list of students with attendance below threshold
     * Accessible by: TEACHER
     */
    @GetMapping("/teacher/subject/{subjectId}/defaulters")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<List<DefaulterInfo>> getDefaultersList(
            @PathVariable String subjectId,
            @RequestParam(defaultValue = "75.0") double threshold) {
        List<DefaulterInfo> defaulters = analyticsService.getDefaultersList(subjectId, threshold);
        return ResponseEntity.ok(defaulters);
    }

    /**
     * Get attendance distribution (excellent/good/poor) for a subject
     * Accessible by: TEACHER
     */
    @GetMapping("/teacher/subject/{subjectId}/distribution")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<AttendanceDistribution> getAttendanceDistribution(@PathVariable String subjectId) {
        AttendanceDistribution distribution = analyticsService.getAttendanceDistribution(subjectId);
        return ResponseEntity.ok(distribution);
    }

    // ==================== SUBJECT ANALYTICS ====================

    /**
     * Get subject-level statistics
     * Accessible by: All authenticated users
     */
    @GetMapping("/subject/{subjectId}/stats")
    public ResponseEntity<Map<String, Object>> getSubjectStats(@PathVariable String subjectId) {
        Map<String, Object> stats = analyticsService.getSubjectStats(subjectId);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get subject attendance trends over time
     * Accessible by: All authenticated users
     */
    @GetMapping("/subject/{subjectId}/trends")
    public ResponseEntity<List<AttendanceTrendResponse>> getSubjectTrends(
            @PathVariable String subjectId,
            @RequestParam(defaultValue = "30") int days) {
        List<AttendanceTrendResponse> trends = analyticsService.getSubjectTrends(subjectId, days);
        return ResponseEntity.ok(trends);
    }
}
