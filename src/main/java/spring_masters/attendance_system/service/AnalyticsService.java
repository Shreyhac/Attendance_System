package spring_masters.attendance_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import spring_masters.attendance_system.dto.response.*;
import spring_masters.attendance_system.exception.ResourceNotFoundException;
import spring_masters.attendance_system.model.entity.Attendance;
import spring_masters.attendance_system.model.entity.Subject;
import spring_masters.attendance_system.model.entity.User;
import spring_masters.attendance_system.repository.AttendanceRepository;
import spring_masters.attendance_system.repository.SubjectRepository;
import spring_masters.attendance_system.repository.UserRepository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnalyticsService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Get comprehensive analytics overview for a student
     */
    public StudentAnalyticsResponse getStudentOverview(String studentEmail) {
        User student = userRepository.findByEmail(studentEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "email", studentEmail));

        List<Attendance> allAttendance = attendanceRepository.findByStudentEmail(studentEmail);

        if (allAttendance.isEmpty()) {
            return new StudentAnalyticsResponse(studentEmail, student.getName(), 0.0, 0, 0, new ArrayList<>());
        }

        // Calculate overall stats
        int totalClasses = allAttendance.size();
        long attendedClasses = allAttendance.stream().filter(Attendance::isPresent).count();
        double overallPercentage = (attendedClasses * 100.0) / totalClasses;

        // Group by subject and calculate subject-wise attendance
        Map<String, List<Attendance>> bySubject = allAttendance.stream()
                .collect(Collectors.groupingBy(Attendance::getSubjectId));

        List<SubjectWiseAttendance> subjectWiseList = new ArrayList<>();
        for (Map.Entry<String, List<Attendance>> entry : bySubject.entrySet()) {
            String subjectId = entry.getKey();
            List<Attendance> subjectAttendance = entry.getValue();

            Subject subject = subjectRepository.findById(subjectId).orElse(null);
            String subjectName = subject != null ? subject.getName() : "Unknown";

            int subjectTotal = subjectAttendance.size();
            long subjectAttended = subjectAttendance.stream().filter(Attendance::isPresent).count();
            double subjectPercentage = (subjectAttended * 100.0) / subjectTotal;

            subjectWiseList.add(new SubjectWiseAttendance(
                    subjectId, subjectName, subjectTotal, (int) subjectAttended, subjectPercentage));
        }

        return new StudentAnalyticsResponse(
                studentEmail, student.getName(), overallPercentage,
                totalClasses, (int) attendedClasses, subjectWiseList);
    }

    /**
     * Get analytics for a specific student in a specific subject
     */
    public SubjectWiseAttendance getStudentSubjectAnalytics(String studentEmail, String subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", subjectId));

        List<Attendance> attendance = attendanceRepository.findByStudentEmailAndSubjectId(studentEmail, subjectId);

        if (attendance.isEmpty()) {
            return new SubjectWiseAttendance(subjectId, subject.getName(), 0, 0, 0.0);
        }

        int total = attendance.size();
        long attended = attendance.stream().filter(Attendance::isPresent).count();
        double percentage = (attended * 100.0) / total;

        return new SubjectWiseAttendance(subjectId, subject.getName(), total, (int) attended, percentage);
    }

    /**
     * Get attendance trends for a student over the last N days
     */
    public List<AttendanceTrendResponse> getStudentTrends(String studentEmail, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        List<Attendance> attendance = attendanceRepository.findByStudentEmailAndDateBetween(
                studentEmail, startDate, endDate);

        // Group by date
        Map<LocalDate, List<Attendance>> byDate = attendance.stream()
                .collect(Collectors.groupingBy(Attendance::getDate));

        List<AttendanceTrendResponse> trends = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Attendance>> entry : byDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Attendance> dayAttendance = entry.getValue();

            int total = dayAttendance.size();
            long present = dayAttendance.stream().filter(Attendance::isPresent).count();
            int absent = total - (int) present;
            double percentage = (present * 100.0) / total;

            trends.add(new AttendanceTrendResponse(date, percentage, (int) present, absent, total));
        }

        // Sort by date
        trends.sort(Comparator.comparing(AttendanceTrendResponse::getDate));

        return trends;
    }

    /**
     * Get comprehensive subject analytics for teachers
     */
    public SubjectAnalyticsResponse getSubjectSummary(String subjectId, double threshold) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        List<Attendance> allAttendance = attendanceRepository.findBySubjectId(subjectId);

        if (allAttendance.isEmpty()) {
            return new SubjectAnalyticsResponse(
                    subjectId, subject.getName(), 0, 0, 0.0,
                    new AttendanceDistribution(0, 0, 0), new ArrayList<>());
        }

        // Get unique students
        Set<String> uniqueStudents = allAttendance.stream()
                .map(Attendance::getStudentEmail)
                .collect(Collectors.toSet());

        int totalStudents = uniqueStudents.size();

        // Count total classes (unique dates)
        long totalClasses = allAttendance.stream()
                .map(Attendance::getDate)
                .distinct()
                .count();

        // Calculate average attendance
        long totalPresent = allAttendance.stream().filter(Attendance::isPresent).count();
        double averageAttendance = (totalPresent * 100.0) / allAttendance.size();

        // Calculate distribution and defaulters
        AttendanceDistribution distribution = calculateDistribution(subjectId, uniqueStudents);
        List<DefaulterInfo> defaulters = getDefaultersList(subjectId, uniqueStudents, threshold);

        return new SubjectAnalyticsResponse(
                subjectId, subject.getName(), (int) totalClasses, totalStudents,
                averageAttendance, distribution, defaulters);
    }

    /**
     * Get list of students with attendance below threshold
     */
    public List<DefaulterInfo> getDefaultersList(String subjectId, double threshold) {
        List<Attendance> allAttendance = attendanceRepository.findBySubjectId(subjectId);

        Set<String> uniqueStudents = allAttendance.stream()
                .map(Attendance::getStudentEmail)
                .collect(Collectors.toSet());

        return getDefaultersList(subjectId, uniqueStudents, threshold);
    }

    private List<DefaulterInfo> getDefaultersList(String subjectId, Set<String> students, double threshold) {
        List<DefaulterInfo> defaulters = new ArrayList<>();

        for (String studentEmail : students) {
            List<Attendance> studentAttendance = attendanceRepository
                    .findByStudentEmailAndSubjectId(studentEmail, subjectId);

            if (studentAttendance.isEmpty())
                continue;

            int total = studentAttendance.size();
            long attended = studentAttendance.stream().filter(Attendance::isPresent).count();
            double percentage = (attended * 100.0) / total;

            if (percentage < threshold) {
                User student = userRepository.findByEmail(studentEmail).orElse(null);
                String studentName = student != null ? student.getName() : "Unknown";

                defaulters.add(new DefaulterInfo(
                        studentEmail, studentName, percentage, total, (int) attended));
            }
        }

        // Sort by percentage (lowest first)
        defaulters.sort(Comparator.comparingDouble(DefaulterInfo::getAttendancePercentage));

        return defaulters;
    }

    /**
     * Calculate attendance distribution (excellent/good/poor)
     */
    public AttendanceDistribution getAttendanceDistribution(String subjectId) {
        List<Attendance> allAttendance = attendanceRepository.findBySubjectId(subjectId);

        Set<String> uniqueStudents = allAttendance.stream()
                .map(Attendance::getStudentEmail)
                .collect(Collectors.toSet());

        return calculateDistribution(subjectId, uniqueStudents);
    }

    private AttendanceDistribution calculateDistribution(String subjectId, Set<String> students) {
        int excellent = 0; // 75-100%
        int good = 0; // 50-75%
        int poor = 0; // 0-50%

        for (String studentEmail : students) {
            List<Attendance> studentAttendance = attendanceRepository
                    .findByStudentEmailAndSubjectId(studentEmail, subjectId);

            if (studentAttendance.isEmpty())
                continue;

            int total = studentAttendance.size();
            long attended = studentAttendance.stream().filter(Attendance::isPresent).count();
            double percentage = (attended * 100.0) / total;

            if (percentage >= 75) {
                excellent++;
            } else if (percentage >= 50) {
                good++;
            } else {
                poor++;
            }
        }

        return new AttendanceDistribution(excellent, good, poor);
    }

    /**
     * Get subject-level statistics
     */
    public Map<String, Object> getSubjectStats(String subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        List<Attendance> allAttendance = attendanceRepository.findBySubjectId(subjectId);

        Map<String, Object> stats = new HashMap<>();
        stats.put("subjectId", subjectId);
        stats.put("subjectName", subject.getName());
        stats.put("teacherEmail", subject.getTeacherEmail());

        if (allAttendance.isEmpty()) {
            stats.put("totalClasses", 0);
            stats.put("totalRecords", 0);
            stats.put("averageAttendance", 0.0);
            return stats;
        }

        long totalClasses = allAttendance.stream()
                .map(Attendance::getDate)
                .distinct()
                .count();

        long totalPresent = allAttendance.stream().filter(Attendance::isPresent).count();
        double averageAttendance = (totalPresent * 100.0) / allAttendance.size();

        stats.put("totalClasses", totalClasses);
        stats.put("totalRecords", allAttendance.size());
        stats.put("averageAttendance", averageAttendance);

        return stats;
    }

    /**
     * Get subject attendance trends over time
     */
    public List<AttendanceTrendResponse> getSubjectTrends(String subjectId, int days) {
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days);

        List<Attendance> attendance = attendanceRepository.findBySubjectIdAndDateBetween(
                subjectId, startDate, endDate);

        // Group by date
        Map<LocalDate, List<Attendance>> byDate = attendance.stream()
                .collect(Collectors.groupingBy(Attendance::getDate));

        List<AttendanceTrendResponse> trends = new ArrayList<>();
        for (Map.Entry<LocalDate, List<Attendance>> entry : byDate.entrySet()) {
            LocalDate date = entry.getKey();
            List<Attendance> dayAttendance = entry.getValue();

            int total = dayAttendance.size();
            long present = dayAttendance.stream().filter(Attendance::isPresent).count();
            int absent = total - (int) present;
            double percentage = (present * 100.0) / total;

            trends.add(new AttendanceTrendResponse(date, percentage, (int) present, absent, total));
        }

        // Sort by date
        trends.sort(Comparator.comparing(AttendanceTrendResponse::getDate));

        return trends;
    }
}
