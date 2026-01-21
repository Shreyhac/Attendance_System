package spring_masters.attendance_system.dto;

public class CreateSubjectRequest {

    private String name;
    private String teacherEmail;

    public String getName() {
        return name;
    }

    public String getTeacherEmail() {
        return teacherEmail;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTeacherEmail(String teacherEmail) {
        this.teacherEmail = teacherEmail;
    }
}
