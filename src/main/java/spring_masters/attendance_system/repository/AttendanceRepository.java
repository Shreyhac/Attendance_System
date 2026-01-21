package spring_masters.attendance_system.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import spring_masters.attendance_system.model.entity.Attendance;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends MongoRepository<Attendance, String>, AttendanceRepositoryCustom {

    // ========== Basic Queries ==========
    
    /**
     * Find all attendance records for a specific subject
     */
    /**
     * Find all attendance records for a specific subject (Paginated)
     */
    Page<Attendance> findBySubjectId(String subjectId, Pageable pageable);

    /**
     * Find all attendance records for a specific subject (All)
     */
    List<Attendance> findBySubjectId(String subjectId);

    /**
     * Find all attendance records for a specific student (Paginated)
     */
    Page<Attendance> findByStudentEmail(String studentEmail, Pageable pageable);

    /**
     * Find all attendance records for a specific student (All)
     */
    List<Attendance> findByStudentEmail(String studentEmail);

    // ========== Analytics and Complex Queries ==========

    List<Attendance> findByStudentEmailAndSubjectId(String studentEmail, String subjectId);

    // Count methods for analytics
    long countByStudentEmailAndSubjectId(String studentEmail, String subjectId);

    long countByStudentEmailAndSubjectIdAndPresent(String studentEmail, String subjectId, boolean present);

    long countByStudentEmailAndSubjectIdAndPresentTrue(String studentEmail, String subjectId);

    long countBySubjectId(String subjectId);

    long countBySubjectIdAndPresentTrue(String subjectId);

    // Date range queries
    List<Attendance> findByStudentEmailAndDateBetween(String studentEmail, LocalDate startDate, LocalDate endDate);

    List<Attendance> findBySubjectIdAndDateBetween(String subjectId, LocalDate startDate, LocalDate endDate);

    List<Attendance> findByStudentEmailAndSubjectIdAndDateBetween(
            String studentEmail,
            String subjectId,
            LocalDate startDate,
            LocalDate endDate
    );

    // Filtered queries
    List<Attendance> findByStudentEmailAndPresent(String studentEmail, boolean present);

    List<Attendance> findBySubjectIdAndPresent(String subjectId, boolean present);

    List<Attendance> findBySubjectIdAndDate(String subjectId, LocalDate date);

    Optional<Attendance> findByStudentEmailAndSubjectIdAndDate(
            String studentEmail,
            String subjectId,
            LocalDate date
    );

    // Find distinct students for a subject
    @Query(value = "{ 'subjectId': ?0 }", fields = "{ 'studentEmail': 1 }")
    List<Attendance> findDistinctStudentsBySubjectId(String subjectId);

    /**
     * Custom query: Find attendance with multiple filters
     */
    @Query("{ 'studentEmail': ?0, 'subjectId': ?1, 'date': { $gte: ?2, $lte: ?3 }, 'present': ?4 }")
    List<Attendance> findByCustomFilters(
            String studentEmail,
            String subjectId,
            LocalDate startDate,
            LocalDate endDate,
            boolean present
    );

    /**
     * Delete attendance records older than a specific date
     */
    void deleteByDateBefore(LocalDate date);
}
