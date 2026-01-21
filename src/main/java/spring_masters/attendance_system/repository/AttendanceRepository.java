package spring_masters.attendance_system.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import spring_masters.attendance_system.model.entity.Attendance;

import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepository extends MongoRepository<Attendance, String> {

    List<Attendance> findBySubjectId(String subjectId);

    List<Attendance> findByStudentEmail(String studentEmail);

    List<Attendance> findByStudentEmailAndSubjectId(String studentEmail, String subjectId);

    // Count methods for analytics
    long countByStudentEmailAndSubjectId(String studentEmail, String subjectId);

    long countByStudentEmailAndSubjectIdAndPresentTrue(String studentEmail, String subjectId);

    long countBySubjectId(String subjectId);

    long countBySubjectIdAndPresentTrue(String subjectId);

    // Date range queries
    List<Attendance> findByStudentEmailAndDateBetween(String studentEmail, LocalDate startDate, LocalDate endDate);

    List<Attendance> findBySubjectIdAndDateBetween(String subjectId, LocalDate startDate, LocalDate endDate);

    // Find distinct students for a subject
    @Query(value = "{ 'subjectId': ?0 }", fields = "{ 'studentEmail': 1 }")
    List<Attendance> findDistinctStudentsBySubjectId(String subjectId);
}
