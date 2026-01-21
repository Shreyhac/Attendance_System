package spring_masters.attendance_system.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import spring_masters.attendance_system.model.entity.Attendance;

import java.util.List;

public interface AttendanceRepository extends MongoRepository<Attendance, String> {

    List<Attendance> findBySubjectId(String subjectId);

    List<Attendance> findByStudentEmail(String studentEmail);
}
