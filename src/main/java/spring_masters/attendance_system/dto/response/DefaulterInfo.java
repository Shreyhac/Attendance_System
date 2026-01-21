package spring_masters.attendance_system.dto.response;

public class DefaulterInfo {

    private String studentEmail;
    private String studentName;
    private double attendancePercentage;
    private int totalClasses;
    private int attendedClasses;

    public DefaulterInfo() {
    }

    public DefaulterInfo(String studentEmail, String studentName, double attendancePercentage,
            int totalClasses, int attendedClasses) {
        this.studentEmail = studentEmail;
        this.studentName = studentName;
        this.attendancePercentage = attendancePercentage;
        this.totalClasses = totalClasses;
        this.attendedClasses = attendedClasses;
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

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
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
}
