package spring_masters.attendance_system.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import spring_masters.attendance_system.dto.request.EmailNotificationRequest;
import spring_masters.attendance_system.service.EmailService;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    /**
     * Manually send low attendance alert
     * Requires TEACHER role
     */
    @PostMapping("/send-low-attendance-alert")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> sendLowAttendanceAlert(@Valid @RequestBody EmailNotificationRequest request) {
        try {
            emailService.sendLowAttendanceAlert(
                    request.getStudentEmail(),
                    request.getStudentName(),
                    request.getSubjectName(),
                    request.getAttendancePercentage());
            return ResponseEntity.ok("Low attendance alert sent successfully to " + request.getStudentEmail());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to send email: " + e.getMessage());
        }
    }

    /**
     * Manually send attendance marked notification
     * Requires TEACHER role
     */
    @PostMapping("/send-attendance-notification")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<String> sendAttendanceNotification(
            @RequestParam String studentEmail,
            @RequestParam String studentName,
            @RequestParam String subjectName,
            @RequestParam boolean present) {
        try {
            emailService.sendAttendanceMarkedNotification(
                    studentEmail,
                    studentName,
                    subjectName,
                    present,
                    LocalDate.now());
            return ResponseEntity.ok("Attendance notification sent successfully to " + studentEmail);
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Failed to send email: " + e.getMessage());
        }
    }
}
