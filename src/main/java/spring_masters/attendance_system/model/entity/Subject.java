package spring_masters.attendance_system.model.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "subjects")
public class Subject {

    @Id
    private String id;

    private String name;
    private String teacherEmail;

    public Subject() {}

    public Subject(String name, String teacherEmail) {
        this.name = name;
        this.teacherEmail = teacherEmail;
    }

    public String getId() {
        return id;
    }

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
