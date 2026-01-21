package spring_masters.attendance_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class MarkAttendanceRequest {

    @NotBlank(message = "Student email is required")
    @Email(message = "Student email must be a valid email address")
    private String studentEmail;

    @NotBlank(message = "Subject ID is required")
    private String subjectId;

    private boolean present;

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public boolean isPresent() {
        return present;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
