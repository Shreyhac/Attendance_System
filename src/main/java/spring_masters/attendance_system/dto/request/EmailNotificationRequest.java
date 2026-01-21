package spring_masters.attendance_system.dto.request;

public class EmailNotificationRequest {

    private String studentEmail;
    private String studentName;
    private String subjectName;
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
