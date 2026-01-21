package spring_masters.attendance_system.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class EmailNotificationRequest {

    @NotBlank(message = "Student email is required")
    @Email(message = "Invalid email format")
    private String studentEmail;

    @NotBlank(message = "Student name is required")
    private String studentName;

    @NotBlank(message = "Subject name is required")
    private String subjectName;

    @Min(value = 0, message = "Attendance percentage cannot be negative")
    @Max(value = 100, message = "Attendance percentage cannot exceed 100")
    private double attendancePercentage;

    public EmailNotificationRequest() {
    }

    public EmailNotificationRequest(String studentEmail, String studentName,
            String subjectName, double attendancePercentage) {
        this.studentEmail = studentEmail;
        this.studentName = studentName;
        this.subjectName = subjectName;
        this.attendancePercentage = attendancePercentage;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }
}
