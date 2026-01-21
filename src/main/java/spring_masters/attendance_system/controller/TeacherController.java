package spring_masters.attendance_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

    @PostMapping("/subject")
    @Operation(summary = "Create a new subject")
    public Subject createSubject(@Valid @RequestBody CreateSubjectRequest request) {
        return teacherService.createSubject(request);
    }

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
}
