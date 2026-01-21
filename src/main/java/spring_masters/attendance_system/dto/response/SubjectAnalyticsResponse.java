package spring_masters.attendance_system.dto.response;

import java.util.List;

public class SubjectAnalyticsResponse {

    private String subjectId;
    private String subjectName;
    private int totalClasses;
    private int totalStudents;
    private double averageAttendance;
    private AttendanceDistribution distribution;
    private List<DefaulterInfo> defaulters;

    public SubjectAnalyticsResponse() {
    }

    public SubjectAnalyticsResponse(String subjectId, String subjectName, int totalClasses,
            int totalStudents, double averageAttendance,
            AttendanceDistribution distribution, List<DefaulterInfo> defaulters) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.totalClasses = totalClasses;
        this.totalStudents = totalStudents;
        this.averageAttendance = averageAttendance;
        this.distribution = distribution;
        this.defaulters = defaulters;
    }

    // Getters and Setters
    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public int getTotalClasses() {
        return totalClasses;
    }

    public void setTotalClasses(int totalClasses) {
        this.totalClasses = totalClasses;
    }

    public int getTotalStudents() {
        return totalStudents;
    }

    public void setTotalStudents(int totalStudents) {
        this.totalStudents = totalStudents;
    }

    public double getAverageAttendance() {
        return averageAttendance;
    }

    public void setAverageAttendance(double averageAttendance) {
        this.averageAttendance = averageAttendance;
    }

    public AttendanceDistribution getDistribution() {
        return distribution;
    }

    public void setDistribution(AttendanceDistribution distribution) {
        this.distribution = distribution;
    }

    public List<DefaulterInfo> getDefaulters() {
        return defaulters;
    }

    public void setDefaulters(List<DefaulterInfo> defaulters) {
        this.defaulters = defaulters;
    }
}
