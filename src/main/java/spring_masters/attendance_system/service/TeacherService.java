package spring_masters.attendance_system.service;

import org.springframework.stereotype.Service;
import spring_masters.attendance_system.dto.CreateSubjectRequest;
import spring_masters.attendance_system.dto.MarkAttendanceRequest;
import spring_masters.attendance_system.model.entity.Attendance;
import spring_masters.attendance_system.model.entity.Subject;
import spring_masters.attendance_system.repository.AttendanceRepository;
import spring_masters.attendance_system.repository.SubjectRepository;

import java.time.LocalDate;

@Service
public class TeacherService {

    private final SubjectRepository subjectRepository;
    private final AttendanceRepository attendanceRepository;

    public TeacherService(SubjectRepository subjectRepository,
                          AttendanceRepository attendanceRepository) {
        this.subjectRepository = subjectRepository;
        this.attendanceRepository = attendanceRepository;
    }

    public Subject createSubject(CreateSubjectRequest request) {
        Subject subject = new Subject(
                request.getName(),
                request.getTeacherEmail()
        );
        return subjectRepository.save(subject);
    }

    public Attendance markAttendance(MarkAttendanceRequest request) {
        Attendance attendance = new Attendance(
                request.getStudentEmail(),
                request.getSubjectId(),
                LocalDate.now(),
                request.isPresent()
        );
        return attendanceRepository.save(attendance);
    }
}
