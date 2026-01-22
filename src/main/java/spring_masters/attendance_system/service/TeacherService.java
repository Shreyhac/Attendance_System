package spring_masters.attendance_system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import spring_masters.attendance_system.dto.CreateSubjectRequest;
import spring_masters.attendance_system.dto.MarkAttendanceRequest;
import spring_masters.attendance_system.exception.ResourceNotFoundException;
import spring_masters.attendance_system.model.entity.Attendance;
import spring_masters.attendance_system.model.entity.Subject;
import spring_masters.attendance_system.model.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import spring_masters.attendance_system.repository.AttendanceRepository;
import spring_masters.attendance_system.repository.SubjectRepository;
import spring_masters.attendance_system.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    @Value("${app.email.attendance-threshold}")
    private double attendanceThreshold;

    public TeacherService(SubjectRepository subjectRepository,
            AttendanceRepository attendanceRepository,
            UserRepository userRepository,
            EmailService emailService) {
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    /**
     * Create a new subject
     */
    @CacheEvict(value = "subjects", allEntries = true)
    public Subject createSubject(CreateSubjectRequest request) {
        Subject subject = new Subject(
                request.getName(),
                request.getTeacherEmail());
        return subjectRepository.save(subject);
    }

    /**
     * Mark attendance for a student
     * Enhanced: Prevents duplicate attendance for the same day
     */
    @CacheEvict(value = { "studentAnalytics", "subjectStats", "attendancePercentage" }, allEntries = true)
    public Attendance markAttendance(MarkAttendanceRequest request) {
        // Check if attendance already exists for today
        Optional<Attendance> existing = attendanceRepository
                .findByStudentEmailAndSubjectIdAndDate(
                        request.getStudentEmail(),
                        request.getSubjectId(),
                        LocalDate.now()
                );

        if (existing.isPresent()) {
            // Update existing attendance record
            Attendance attendance = existing.get();
            attendance.setPresent(request.isPresent());
            return attendanceRepository.save(attendance);
        }

        // Create new attendance record
        Attendance attendance = new Attendance(
                request.getStudentEmail(),
                request.getSubjectId(),
                LocalDate.now(),
                request.isPresent());
        Attendance savedAttendance = attendanceRepository.save(attendance);

        // Calculate current attendance percentage for this subject
        double currentPercentage = calculateStudentAttendanceForSubject(
                request.getStudentEmail(),
                request.getSubjectId());

        // Send low attendance alert if below threshold
        if (currentPercentage < attendanceThreshold) {
            try {
                User student = userRepository.findByEmail(request.getStudentEmail())
                        .orElseThrow(
                                () -> new ResourceNotFoundException("Student", "email", request.getStudentEmail()));
                Subject subject = subjectRepository.findById(request.getSubjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.getSubjectId()));

                emailService.sendLowAttendanceAlert(
                        student.getEmail(),
                        student.getName(),
                        subject.getName(),
                        currentPercentage);
            } catch (Exception e) {
                // Log error but don't fail the attendance marking
                System.err.println("Failed to send email notification: " + e.getMessage());
            }
        }

        return savedAttendance;
    }

    /**
     * Calculate attendance percentage for a specific student in a specific subject
     */
    private double calculateStudentAttendanceForSubject(String studentEmail, String subjectId) {
        List<Attendance> records = attendanceRepository.findByStudentEmailAndSubjectId(
                studentEmail, subjectId);

        if (records.isEmpty()) {
            return 100.0; // No records yet, return 100%
        }

        long presentCount = records.stream()
                .filter(Attendance::isPresent)
                .count();

        return (presentCount * 100.0) / records.size();
    }

    /**
     * Process bulk attendance records from CSV upload
     */
    @CacheEvict(value = { "studentAnalytics", "subjectStats", "attendancePercentage" }, allEntries = true)
    public spring_masters.attendance_system.dto.response.BulkAttendanceUploadResponse processBulkAttendance(
            List<spring_masters.attendance_system.dto.CsvAttendanceRecord> csvRecords) {

        long startTime = System.currentTimeMillis();

        List<Attendance> successfulRecords = new ArrayList<>();
        List<spring_masters.attendance_system.dto.response.AttendanceUploadError> failedRecords = new ArrayList<>();

        for (spring_masters.attendance_system.dto.CsvAttendanceRecord csvRecord : csvRecords) {
            try {
                // Find subject by name
                Subject subject = subjectRepository.findByName(csvRecord.getSubjectName());
                if (subject == null) {
                    failedRecords.add(new spring_masters.attendance_system.dto.response.AttendanceUploadError(
                            csvRecord.getRowNumber(),
                            csvRecord.getStudentEmail(),
                            csvRecord.getSubjectName(),
                            "Subject not found: " + csvRecord.getSubjectName(),
                            "SUBJECT_NOT_FOUND"));
                    continue;
                }

                // Verify student exists
                User student = userRepository.findByEmail(csvRecord.getStudentEmail()).orElse(null);
                if (student == null) {
                    failedRecords.add(new spring_masters.attendance_system.dto.response.AttendanceUploadError(
                            csvRecord.getRowNumber(),
                            csvRecord.getStudentEmail(),
                            csvRecord.getSubjectName(),
                            "Student not found: " + csvRecord.getStudentEmail(),
                            "STUDENT_NOT_FOUND"));
                    continue;
                }

                // Create attendance record
                Attendance attendance = new Attendance(
                        csvRecord.getStudentEmail(),
                        subject.getId(),
                        LocalDate.now(),
                        csvRecord.isPresent());
                Attendance savedAttendance = attendanceRepository.save(attendance);
                successfulRecords.add(savedAttendance);

                // Calculate current attendance percentage and send alert if needed
                double currentPercentage = calculateStudentAttendanceForSubject(
                        csvRecord.getStudentEmail(),
                        subject.getId());

                if (currentPercentage < attendanceThreshold) {
                    try {
                        emailService.sendLowAttendanceAlert(
                                student.getEmail(),
                                student.getName(),
                                subject.getName(),
                                currentPercentage);
                    } catch (Exception e) {
                        System.err.println("Failed to send email notification: " + e.getMessage());
                    }
                }

            } catch (Exception e) {
                failedRecords.add(new spring_masters.attendance_system.dto.response.AttendanceUploadError(
                        csvRecord.getRowNumber(),
                        csvRecord.getStudentEmail(),
                        csvRecord.getSubjectName(),
                        "Error processing record: " + e.getMessage(),
                        "PROCESSING_ERROR"));
            }
        }

        long processingTime = System.currentTimeMillis() - startTime;

        spring_masters.attendance_system.dto.response.BulkAttendanceUploadResponse response = new spring_masters.attendance_system.dto.response.BulkAttendanceUploadResponse();
        response.setTotalRecords(csvRecords.size());
        response.setSuccessCount(successfulRecords.size());
        response.setFailureCount(failedRecords.size());
        response.setSuccessfulRecords(successfulRecords);
        response.setFailedRecords(failedRecords);
        response.setProcessingTimeMs(processingTime);

        return response;
    }

    // ========== Complex Query Methods ==========

    /**
     * Get attendance records for a subject (Paginated)
     */
    public Page<Attendance> getAttendanceBySubject(String subjectId, Pageable pageable) {
        return attendanceRepository.findBySubjectId(subjectId, pageable);
    }

    /**
     * Get all attendance records for a subject
     * Use case: Teacher views all attendance for "Mathematics"
     */
    public List<Attendance> getAttendanceBySubject(String subjectId) {
        return attendanceRepository.findBySubjectId(subjectId, Pageable.unpaged()).getContent();
    }

    /**
     * Get attendance for a subject on a specific date
     * Use case: "Who was present in Mathematics on Jan 15, 2026?"
     */
    public List<Attendance> getAttendanceBySubjectAndDate(String subjectId, LocalDate date) {
        return attendanceRepository.findBySubjectIdAndDate(subjectId, date);
    }

    /**
     * Get attendance for a subject between dates
     * Use case: "Show Mathematics attendance for January 2026"
     */
    public List<Attendance> getAttendanceBySubjectAndDateRange(
            String subjectId,
            LocalDate startDate,
            LocalDate endDate) {
        return attendanceRepository.findBySubjectIdAndDateBetween(
                subjectId, startDate, endDate);
    }

    /**
     * Get all students who were present in a subject
     * Use case: "List of students present in Mathematics"
     */
    public List<Attendance> getPresentStudentsBySubject(String subjectId) {
        return attendanceRepository.findBySubjectIdAndPresent(subjectId, true);
    }

    /**
     * Get all students who were absent in a subject
     * Use case: "List of students absent in Mathematics"
     */
    public List<Attendance> getAbsentStudentsBySubject(String subjectId) {
        return attendanceRepository.findBySubjectIdAndPresent(subjectId, false);
    }

    /**
     * Get attendance for a specific student in teacher's subject
     * Use case: Teacher checks a particular student's attendance
     */
    public List<Attendance> getStudentAttendanceInSubject(
            String studentEmail,
            String subjectId) {
        return attendanceRepository.findByStudentEmailAndSubjectId(
                studentEmail, subjectId);
    }

    /**
     * Calculate attendance percentage for a student in a subject
     * Use case: "What's John's attendance in Mathematics?"
     */
    public double getStudentAttendancePercentageInSubject(
            String studentEmail,
            String subjectId) {
        List<Attendance> records = attendanceRepository
                .findByStudentEmailAndSubjectId(studentEmail, subjectId);

        if (records.isEmpty()) {
            return 0.0;
        }

        long presentCount = attendanceRepository
                .countByStudentEmailAndSubjectIdAndPresent(
                        studentEmail, subjectId, true);

        return (presentCount * 100.0) / records.size();
    }

    /**
     * Get today's attendance for a subject
     * Use case: Quick view of today's attendance
     */
    public List<Attendance> getTodayAttendance(String subjectId) {
        return attendanceRepository.findBySubjectIdAndDate(subjectId, LocalDate.now());
    }

    /**
     * Check if attendance is already marked for a student today
     */
    public boolean isAttendanceMarkedToday(String studentEmail, String subjectId) {
        return attendanceRepository
                .findByStudentEmailAndSubjectIdAndDate(
                        studentEmail, subjectId, LocalDate.now())
                .isPresent();
    }

    /**
     * Count total attendance records for a subject
     */
    public long getTotalAttendanceCount(String subjectId) {
        return attendanceRepository.findBySubjectId(subjectId).size();
    }

    /**
     * Get subjects taught by a teacher
     */
    @Cacheable(value = "subjects", key = "#teacherEmail")
    public List<Subject> getSubjectsByTeacher(String teacherEmail) {
        return subjectRepository.findByTeacherEmail(teacherEmail);
    }
    /**
     * Search attendance with generic filters
     */
    public List<Attendance> searchAttendance(
            String studentEmail,
            String subjectId,
            Boolean present,
            LocalDate startDate,
            LocalDate endDate) {
        return attendanceRepository.searchAttendance(studentEmail, subjectId, present, startDate, endDate);
    }
}
