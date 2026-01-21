package spring_masters.attendance_system.dto.response;

import java.util.List;

public class StudentAnalyticsResponse {

    private String studentEmail;
    private String studentName;
    private double overallPercentage;
    private int totalClasses;
    private int attendedClasses;
    private List<SubjectWiseAttendance> subjectWiseAttendance;

    public StudentAnalyticsResponse() {
    }

    public StudentAnalyticsResponse(String studentEmail, String studentName, double overallPercentage,
            int totalClasses, int attendedClasses,
            List<SubjectWiseAttendance> subjectWiseAttendance) {
        this.studentEmail = studentEmail;
        this.studentName = studentName;
        this.overallPercentage = overallPercentage;
        this.totalClasses = totalClasses;
        this.attendedClasses = attendedClasses;
        this.subjectWiseAttendance = subjectWiseAttendance;
    }

    // Getters and Setters
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

    public double getOverallPercentage() {
        return overallPercentage;
    }

    public void setOverallPercentage(double overallPercentage) {
        this.overallPercentage = overallPercentage;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public void setAttendedClasses(int attendedClasses) {
        this.attendedClasses = attendedClasses;
    }

    public List<SubjectWiseAttendance> getSubjectWiseAttendance() {
        return subjectWiseAttendance;
    }

    public void setSubjectWiseAttendance(List<SubjectWiseAttendance> subjectWiseAttendance) {
        this.subjectWiseAttendance = subjectWiseAttendance;
    }
}
