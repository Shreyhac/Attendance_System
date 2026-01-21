package spring_masters.attendance_system.service;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import spring_masters.attendance_system.model.entity.Attendance;
import spring_masters.attendance_system.repository.AttendanceRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentService {

    private final AttendanceRepository attendanceRepository;

    public StudentService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    // ========== Basic Methods ==========

    /**
     * Get all attendance records for a student (Paginated)
     */
    public Page<Attendance> getAttendance(String studentEmail, Pageable pageable) {
        return attendanceRepository.findByStudentEmail(studentEmail, pageable);
    }

    /**
     * Get all attendance records for a student
     */
    public List<Attendance> getAttendance(String studentEmail) {
        return attendanceRepository.findByStudentEmail(studentEmail, Pageable.unpaged()).getContent();
    }

    @Cacheable(value = "attendancePercentage", key = "#studentEmail")
    public double getAttendancePercentage(String studentEmail) {
        List<Attendance> records = attendanceRepository.findByStudentEmail(studentEmail);

        if (records.isEmpty()) {
            return 0.0;
        }

        int total = records.size();
        int presentCount = 0;

        for (Attendance a : records) {
            if (a.isPresent()) {
                presentCount++;
            }
        }

        return (presentCount * 100.0) / total;
    }

    // ========== Complex Query Methods ==========

    /**
     * Get attendance records for a student in a specific subject
     * Use case: Student views attendance in "Mathematics" only
     */
    public List<Attendance> getAttendanceBySubject(String studentEmail, String subjectId) {
        return attendanceRepository.findByStudentEmailAndSubjectId(studentEmail, subjectId);
    }

    /**
     * Get attendance percentage for a student in a specific subject
     * Use case: "You have 85% attendance in Mathematics"
     */
    public double getAttendancePercentageBySubject(String studentEmail, String subjectId) {
        List<Attendance> records = attendanceRepository
                .findByStudentEmailAndSubjectId(studentEmail, subjectId);

        if (records.isEmpty()) {
            return 0.0;
        }

        long presentCount = attendanceRepository
                .countByStudentEmailAndSubjectIdAndPresent(studentEmail, subjectId, true);

        return (presentCount * 100.0) / records.size();
    }

    /**
     * Get attendance records between two dates
     * Use case: "Show my attendance for January 2026"
     */
    public List<Attendance> getAttendanceBetweenDates(
            String studentEmail,
            LocalDate startDate,
            LocalDate endDate) {
        return attendanceRepository.findByStudentEmailAndDateBetween(
                studentEmail, startDate, endDate);
    }

    /**
     * Get attendance for a subject between dates
     * Use case: "Show my Mathematics attendance for last month"
     */
    public List<Attendance> getAttendanceBySubjectAndDateRange(
            String studentEmail,
            String subjectId,
            LocalDate startDate,
            LocalDate endDate) {
        return attendanceRepository.findByStudentEmailAndSubjectIdAndDateBetween(
                studentEmail, subjectId, startDate, endDate);
    }

    /**
     * Get attendance percentage between dates
     * Use case: "What's my attendance percentage for January?"
     */
    public double getAttendancePercentageBetweenDates(
            String studentEmail,
            LocalDate startDate,
            LocalDate endDate) {
        List<Attendance> records = attendanceRepository
                .findByStudentEmailAndDateBetween(studentEmail, startDate, endDate);

        if (records.isEmpty()) {
            return 0.0;
        }

        long presentCount = records.stream()
                .filter(Attendance::isPresent)
                .count();

        return (presentCount * 100.0) / records.size();
    }

    /**
     * Get total present days for a student
     */
    @Cacheable(value = "studentAnalytics", key = "#studentEmail + '-present'")
    public long getTotalPresentDays(String studentEmail) {
        return attendanceRepository
                .findByStudentEmailAndPresent(studentEmail, true)
                .size();
    }

    /**
     * Get total absent days for a student
     */
    @Cacheable(value = "studentAnalytics", key = "#studentEmail + '-absent'")
    public long getTotalAbsentDays(String studentEmail) {
        return attendanceRepository
                .findByStudentEmailAndPresent(studentEmail, false)
                .size();
    }

    /**
     * Check if attendance is marked for today in a subject
     */
    public boolean isAttendanceMarkedToday(String studentEmail, String subjectId) {
        return attendanceRepository
                .findByStudentEmailAndSubjectIdAndDate(studentEmail, subjectId, LocalDate.now())
                .isPresent();
    }
}
