package spring_masters.attendance_system.service;

import org.springframework.stereotype.Service;
import spring_masters.attendance_system.model.entity.Attendance;
import spring_masters.attendance_system.repository.AttendanceRepository;

import java.util.List;

@Service
public class StudentService {

    private final AttendanceRepository attendanceRepository;

    public StudentService(AttendanceRepository attendanceRepository) {
        this.attendanceRepository = attendanceRepository;
    }

    public List<Attendance> getAttendance(String studentEmail) {
        return attendanceRepository.findByStudentEmail(studentEmail);
    }

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
}
