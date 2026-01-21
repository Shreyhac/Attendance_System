package spring_masters.attendance_system.controller;

import org.springframework.web.bind.annotation.*;
import spring_masters.attendance_system.dto.CreateSubjectRequest;
import spring_masters.attendance_system.dto.MarkAttendanceRequest;
import spring_masters.attendance_system.model.entity.Attendance;
import spring_masters.attendance_system.model.entity.Subject;
import spring_masters.attendance_system.service.TeacherService;

@RestController
@RequestMapping("/api/teacher")
public class TeacherController {

    private final TeacherService teacherService;

    public TeacherController(TeacherService teacherService) {
        this.teacherService = teacherService;
    }

    @PostMapping("/subject")
    public Subject createSubject(@RequestBody CreateSubjectRequest request) {
        return teacherService.createSubject(request);
    }

    @PostMapping("/attendance")
    public Attendance markAttendance(@RequestBody MarkAttendanceRequest request) {
        return teacherService.markAttendance(request);
    }
}
