package spring_masters.attendance_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import spring_masters.attendance_system.dto.CreateSubjectRequest;
import spring_masters.attendance_system.dto.CsvAttendanceRecord;
import spring_masters.attendance_system.dto.MarkAttendanceRequest;
import spring_masters.attendance_system.dto.response.BulkAttendanceUploadResponse;
import spring_masters.attendance_system.exception.InvalidFileTypeException;
import spring_masters.attendance_system.model.entity.Attendance;
import spring_masters.attendance_system.model.entity.Subject;
import spring_masters.attendance_system.service.CsvParserService;
import spring_masters.attendance_system.service.TeacherService;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/teacher")
@Tag(name = "Teacher", description = "Teacher management APIs")
public class TeacherController {

    private final TeacherService teacherService;
    private final CsvParserService csvParserService;

    public TeacherController(TeacherService teacherService, CsvParserService csvParserService) {
        this.teacherService = teacherService;
        this.csvParserService = csvParserService;
    }

    // ========== Basic Endpoints ==========

    /**
     * Create a new subject
     * POST /api/teacher/subject
     */
    @PostMapping("/subject")
    @Operation(summary = "Create a new subject")
    public Subject createSubject(@Valid @RequestBody CreateSubjectRequest request) {
        return teacherService.createSubject(request);
    }

    /**
     * Mark attendance for a student
     * POST /api/teacher/attendance
     * Enhanced: Prevents duplicate attendance for the same day
     */
    @PostMapping("/attendance")
    @Operation(summary = "Mark attendance for a single student")
    public Attendance markAttendance(@Valid @RequestBody MarkAttendanceRequest request) {
        return teacherService.markAttendance(request);
    }

    @PostMapping("/attendance/upload")
    @Operation(summary = "Upload CSV file for bulk attendance marking", description = "Upload a CSV file with columns: studentEmail, subjectName, present")
    public ResponseEntity<BulkAttendanceUploadResponse> uploadAttendanceCsv(
            @RequestParam("file") MultipartFile file) {

        // Validate file is not empty
        if (file.isEmpty()) {
            throw new InvalidFileTypeException("File is empty");
        }

        // Validate file type
        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".csv")) {
            throw new InvalidFileTypeException("Only CSV files are allowed");
        }

        // Validate content type
        String contentType = file.getContentType();
        if (contentType != null &&
                !contentType.equals("text/csv") &&
                !contentType.equals("application/csv") &&
                !contentType.equals("text/plain")) {
            throw new InvalidFileTypeException("Invalid file type. Expected CSV file.");
        }

        // Parse CSV file
        List<CsvAttendanceRecord> records = csvParserService.parseCsvFile(file);

        // Process bulk attendance
        BulkAttendanceUploadResponse response = teacherService.processBulkAttendance(records);

        return ResponseEntity.ok(response);
    }

    // ========== Complex Query Endpoints ==========

    /**
     * Get all attendance records for a subject (Optionally Paginated)
     * GET /api/teacher/attendance/subject?subjectId=123&page=0&size=10&sortBy=date
     */
    @GetMapping("/attendance/subject")
    public Object getAttendanceBySubject(
            @RequestParam String subjectId,
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false, defaultValue = "date") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String sortDir) {

        if (page != null && size != null) {
            Sort sort = sortDir.equalsIgnoreCase("desc") ? Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
            return teacherService.getAttendanceBySubject(subjectId, PageRequest.of(page, size, sort));
        }

        return teacherService.getAttendanceBySubject(subjectId);
    }

    /**
     * Get attendance for a subject on a specific date
     * GET /api/teacher/attendance/subject/date?subjectId=123&date=2026-01-21
     */
    @GetMapping("/attendance/subject/date")
    public List<Attendance> getAttendanceBySubjectAndDate(
            @RequestParam String subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return teacherService.getAttendanceBySubjectAndDate(subjectId, date);
    }

    /**
     * Get attendance for a subject between dates
     * GET /api/teacher/attendance/subject/daterange?subjectId=123&startDate=2026-01-01&endDate=2026-01-31
     */
    @GetMapping("/attendance/subject/daterange")
    public List<Attendance> getAttendanceBySubjectAndDateRange(
            @RequestParam String subjectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return teacherService.getAttendanceBySubjectAndDateRange(
                subjectId, startDate, endDate);
    }

    /**
     * Get all students who were present in a subject
     * GET /api/teacher/attendance/subject/present?subjectId=123
     */
    @GetMapping("/attendance/subject/present")
    public List<Attendance> getPresentStudentsBySubject(@RequestParam String subjectId) {
        return teacherService.getPresentStudentsBySubject(subjectId);
    }

    /**
     * Get all students who were absent in a subject
     * GET /api/teacher/attendance/subject/absent?subjectId=123
     */
    @GetMapping("/attendance/subject/absent")
    public List<Attendance> getAbsentStudentsBySubject(@RequestParam String subjectId) {
        return teacherService.getAbsentStudentsBySubject(subjectId);
    }

    /**
     * Get attendance for a specific student in a subject
     * GET /api/teacher/attendance/student?studentEmail=student@example.com&subjectId=123
     */
    @GetMapping("/attendance/student")
    public List<Attendance> getStudentAttendanceInSubject(
            @RequestParam String studentEmail,
            @RequestParam String subjectId) {
        return teacherService.getStudentAttendanceInSubject(studentEmail, subjectId);
    }

    /**
     * Get attendance percentage for a student in a subject
     * GET /api/teacher/attendance/student/percentage?studentEmail=student@example.com&subjectId=123
     */
    @GetMapping("/attendance/student/percentage")
    public double getStudentAttendancePercentageInSubject(
            @RequestParam String studentEmail,
            @RequestParam String subjectId) {
        return teacherService.getStudentAttendancePercentageInSubject(
                studentEmail, subjectId);
    }

    /**
     * Get today's attendance for a subject
     * GET /api/teacher/attendance/today?subjectId=123
     */
    @GetMapping("/attendance/today")
    public List<Attendance> getTodayAttendance(@RequestParam String subjectId) {
        return teacherService.getTodayAttendance(subjectId);
    }

    /**
     * Check if attendance is already marked for a student today
     * GET /api/teacher/attendance/today/check?studentEmail=student@example.com&subjectId=123
     */
    @GetMapping("/attendance/today/check")
    public boolean isAttendanceMarkedToday(
            @RequestParam String studentEmail,
            @RequestParam String subjectId) {
        return teacherService.isAttendanceMarkedToday(studentEmail, subjectId);
    }

    /**
     * Get total attendance count for a subject
     * GET /api/teacher/attendance/count?subjectId=123
     */
    @GetMapping("/attendance/count")
    public long getTotalAttendanceCount(@RequestParam String subjectId) {
        return teacherService.getTotalAttendanceCount(subjectId);
    }

    /**
     * Get all subjects taught by a teacher
     * GET /api/teacher/subjects?teacherEmail=teacher@example.com
     */
    @GetMapping("/subjects")
    public List<Subject> getSubjectsByTeacher(@RequestParam String teacherEmail) {
        return teacherService.getSubjectsByTeacher(teacherEmail);
    }
    /**
     * Search attendance with any combination of filters
     * GET /api/teacher/attendance/search?subjectId=...&present=...&startDate=...
     */
    @GetMapping("/attendance/search")
    public List<Attendance> searchAttendance(
            @RequestParam(required = false) String studentEmail,
            @RequestParam(required = false) String subjectId,
            @RequestParam(required = false) Boolean present,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        return teacherService.searchAttendance(studentEmail, subjectId, present, startDate, endDate);
    }
}
