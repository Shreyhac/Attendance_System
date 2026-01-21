package spring_masters.attendance_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CreateSubjectRequest {

    @NotBlank(message = "Subject name is required")
    private String name;

    @NotBlank(message = "Teacher email is required")
    @Email(message = "Teacher email must be a valid email address")
    private String teacherEmail;

    public String getName() {
        return name;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }
}
