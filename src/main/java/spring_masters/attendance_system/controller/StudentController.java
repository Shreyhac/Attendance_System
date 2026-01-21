package spring_masters.attendance_system.controller;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import spring_masters.attendance_system.model.entity.Attendance;
import spring_masters.attendance_system.service.StudentService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    // ========== Basic Endpoints ==========

    /**
     * Get all attendance records for a student (Optionally Paginated)
     * GET /api/student/attendance?email=student@example.com&page=0&size=10
     */
    @GetMapping("/attendance")
    public Object getAttendance(
            @RequestParam String email,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {

        if (page != null && size != null) {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            return studentService.getAttendance(email, PageRequest.of(page, size, sort));
        }

        return studentService.getAttendance(email);
    }

    /**
     * Get overall attendance percentage
     * GET /api/student/attendance/percentage?email=student@example.com
     */
    @GetMapping("/attendance/percentage")
    public double getAttendancePercentage(@RequestParam String email) {
        return studentService.getAttendancePercentage(email);
    }

    // ========== Complex Query Endpoints ==========

    /**
     * Get attendance for a specific subject
     * GET /api/student/attendance/subject?email=student@example.com&subjectId=123
     */
    @GetMapping("/attendance/subject")
    public List<Attendance> getAttendanceBySubject(
            @RequestParam String email,
            @RequestParam String subjectId) {
        return studentService.getAttendanceBySubject(email, subjectId);
    }

    /**
     * Get attendance percentage for a specific subject
     * GET /api/student/attendance/subject/percentage?email=student@example.com&subjectId=123
     */
    @GetMapping("/attendance/subject/percentage")
    public double getAttendancePercentageBySubject(
            @RequestParam String email,
            @RequestParam String subjectId) {
        return studentService.getAttendancePercentageBySubject(email, subjectId);
    }

    /**
     * Get attendance between two dates
     * GET /api/student/attendance/daterange?email=student@example.com&startDate=2026-01-01&endDate=2026-01-31
     */
    @GetMapping("/attendance/daterange")
    public List<Attendance> getAttendanceBetweenDates(
            @RequestParam String email,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return studentService.getAttendanceBetweenDates(email, startDate, endDate);
    }

    /**
     * Get attendance for a subject between dates
     * GET /api/student/attendance/subject/daterange?email=student@example.com&subjectId=123&startDate=2026-01-01&endDate=2026-01-31
     */
    @GetMapping("/attendance/subject/daterange")
    public List<Attendance> getAttendanceBySubjectAndDateRange(
            @RequestParam String email,
            @RequestParam String subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return studentService.getAttendanceBySubjectAndDateRange(
                email, subjectId, startDate, endDate);
    }

    /**
     * Get attendance percentage between dates
     * GET /api/student/attendance/percentage/daterange?email=student@example.com&startDate=2026-01-01&endDate=2026-01-31
     */
    @GetMapping("/attendance/percentage/daterange")
    public double getAttendancePercentageBetweenDates(
            @RequestParam String email,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return studentService.getAttendancePercentageBetweenDates(
                email, startDate, endDate);
    }

    /**
     * Get total present days
     * GET /api/student/attendance/present/count?email=student@example.com
     */
    @GetMapping("/attendance/present/count")
    public long getTotalPresentDays(@RequestParam String email) {
        return studentService.getTotalPresentDays(email);
    }

    /**
     * Get total absent days
     * GET /api/student/attendance/absent/count?email=student@example.com
     */
    @GetMapping("/attendance/absent/count")
    public long getTotalAbsentDays(@RequestParam String email) {
        return studentService.getTotalAbsentDays(email);
    }

    /**
     * Check if attendance is marked today for a subject
     * GET /api/student/attendance/today/check?email=student@example.com&subjectId=123
     */
    @GetMapping("/attendance/today/check")
    public boolean isAttendanceMarkedToday(
            @RequestParam String email,
            @RequestParam String subjectId) {
        return studentService.isAttendanceMarkedToday(email, subjectId);
    }
}
