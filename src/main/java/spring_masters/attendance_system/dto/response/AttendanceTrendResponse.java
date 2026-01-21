package spring_masters.attendance_system.dto.response;

import java.time.LocalDate;

public class AttendanceTrendResponse {

    private LocalDate date;
    private double attendancePercentage;
    private int present;
    private int absent;
    private int total;

    public AttendanceTrendResponse() {
    }

    public AttendanceTrendResponse(LocalDate date, double attendancePercentage,
            int present, int absent, int total) {
        this.date = date;
        this.attendancePercentage = attendancePercentage;
        this.present = present;
        this.absent = absent;
        this.total = total;
    }

    // Getters and Setters
    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAttendancePercentage() {
        return attendancePercentage;
    }

    public void setAttendancePercentage(double attendancePercentage) {
        this.attendancePercentage = attendancePercentage;
    }

    public int getPresent() {
        return present;
    }

    public void setPresent(int present) {
        this.present = present;
    }

    public int getAbsent() {
        return absent;
    }

    public void setAbsent(int absent) {
        this.absent = absent;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
