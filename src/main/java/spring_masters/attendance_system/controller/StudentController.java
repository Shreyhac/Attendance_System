package spring_masters.attendance_system.controller;

import org.springframework.web.bind.annotation.*;
import spring_masters.attendance_system.model.entity.Attendance;
import spring_masters.attendance_system.service.StudentService;

import java.util.List;

@RestController
@RequestMapping("/api/student")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @GetMapping("/attendance")
    public List<Attendance> getAttendance(@RequestParam String email) {
        return studentService.getAttendance(email);
    }

    @GetMapping("/attendance/percentage")
    public double getAttendancePercentage(@RequestParam String email) {
        return studentService.getAttendancePercentage(email);
    }
}
