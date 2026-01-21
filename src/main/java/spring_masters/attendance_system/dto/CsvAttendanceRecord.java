package spring_masters.attendance_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class CsvAttendanceRecord {

    @NotBlank(message = "Student email is required")
    @Email(message = "Student email must be a valid email address")
    private String studentEmail;

    @NotBlank(message = "Subject name is required")
    private String subjectName;

    private boolean present;

    private int rowNumber; // For error reporting

    public CsvAttendanceRecord() {
    }

    public CsvAttendanceRecord(String studentEmail, String subjectName, boolean present, int rowNumber) {
        this.studentEmail = studentEmail;
        this.subjectName = subjectName;
        this.present = present;
        this.rowNumber = rowNumber;
    }

    public String getStudentEmail() {
        return studentEmail;
    }

    public void setStudentEmail(String studentEmail) {
        this.studentEmail = studentEmail;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public boolean isPresent() {
        return present;
    }

    public void setPresent(boolean present) {
        this.present = present;
    }

    public int getRowNumber() {
        return rowNumber;
    }

    public void setRowNumber(int rowNumber) {
        this.rowNumber = rowNumber;
    }
}
