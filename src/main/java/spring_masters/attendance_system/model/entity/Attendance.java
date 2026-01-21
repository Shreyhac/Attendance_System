package spring_masters.attendance_system.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Document(collection = "attendance")
public class Attendance {

    @Id
    private String id;

    private String studentEmail;
    private String subjectId;
    private LocalDate date;
    private boolean present;

    public Attendance() {}

    public Attendance(String studentEmail, String subjectId,
                      LocalDate date, boolean present) {
        this.studentEmail = studentEmail;
        this.subjectId = subjectId;
        this.date = date;
        this.present = present;
    }

    public String getId() {
        return id;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public LocalDate getDate() {
        return date;
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

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }
}
