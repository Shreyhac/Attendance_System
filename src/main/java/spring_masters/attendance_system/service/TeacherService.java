package spring_masters.attendance_system.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import spring_masters.attendance_system.dto.CreateSubjectRequest;
import spring_masters.attendance_system.dto.MarkAttendanceRequest;
import spring_masters.attendance_system.exception.ResourceNotFoundException;
import spring_masters.attendance_system.model.entity.Attendance;
import spring_masters.attendance_system.model.entity.Subject;
import spring_masters.attendance_system.model.entity.User;
import spring_masters.attendance_system.repository.AttendanceRepository;
import spring_masters.attendance_system.repository.SubjectRepository;
import spring_masters.attendance_system.repository.UserRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    @CacheEvict(value = "subjects", allEntries = true)
    public Subject createSubject(CreateSubjectRequest request) {
        Subject subject = new Subject(
                request.getName(),
                request.getTeacherEmail());
        return subjectRepository.save(subject);
    }

    @CacheEvict(value = { "studentAnalytics", "subjectStats", "attendancePercentage" }, allEntries = true)
    public Attendance markAttendance(MarkAttendanceRequest request) {
        // Save attendance record
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
}
