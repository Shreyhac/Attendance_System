package spring_masters.attendance_system.dto.response;

public class SubjectWiseAttendance {

    private String subjectId;
    private String subjectName;
    private int totalClasses;
    private int attendedClasses;
    private double percentage;

    public SubjectWiseAttendance() {
    }

    public SubjectWiseAttendance(String subjectId, String subjectName, int totalClasses,
            int attendedClasses, double percentage) {
        this.subjectId = subjectId;
        this.subjectName = subjectName;
        this.totalClasses = totalClasses;
        this.attendedClasses = attendedClasses;
        this.percentage = percentage;
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

    public int getAttendedClasses() {
        return attendedClasses;
    }

    public void setAttendedClasses(int attendedClasses) {
        this.attendedClasses = attendedClasses;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }
}
