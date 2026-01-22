package spring_masters.attendance_system.repository;

import spring_masters.attendance_system.model.entity.Attendance;
import java.time.LocalDate;
import java.util.List;

public interface AttendanceRepositoryCustom {
    List<Attendance> searchAttendance(
            String studentEmail,
            String subjectId,
            Boolean present,
            LocalDate startDate,
            LocalDate endDate
    );
}
