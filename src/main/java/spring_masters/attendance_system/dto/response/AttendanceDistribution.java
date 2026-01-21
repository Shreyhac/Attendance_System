package spring_masters.attendance_system.dto.response;

public class AttendanceDistribution {

    private int excellent; // 75-100%
    private int good; // 50-75%
    private int poor; // 0-50%

    public AttendanceDistribution() {
    }

    public AttendanceDistribution(int excellent, int good, int poor) {
        this.excellent = excellent;
        this.good = good;
        this.poor = poor;
    }

    // Getters and Setters
    public int getExcellent() {
        return excellent;
    }

    public void setExcellent(int excellent) {
        this.excellent = excellent;
    }

    public int getGood() {
        return good;
    }

    public void setGood(int good) {
        this.good = good;
    }

    public int getPoor() {
        return poor;
    }

    public void setPoor(int poor) {
        this.poor = poor;
    }
}
