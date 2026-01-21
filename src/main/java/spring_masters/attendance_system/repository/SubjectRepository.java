package spring_masters.attendance_system.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import spring_masters.attendance_system.model.entity.Subject;

import java.util.List;

public interface SubjectRepository extends MongoRepository<Subject, String> {
    List<Subject> findByTeacherEmail(String teacherEmail);

    Subject findByName(String name);
}
