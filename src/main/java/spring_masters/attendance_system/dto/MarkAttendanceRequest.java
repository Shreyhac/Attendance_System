package spring_masters.attendance_system.dto;

public class MarkAttendanceRequest {

    private String studentEmail;
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
