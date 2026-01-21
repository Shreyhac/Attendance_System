package spring_masters.attendance_system.controller;

import org.springframework.web.bind.annotation.*;
import spring_masters.attendance_system.model.entity.Subject;
import spring_masters.attendance_system.repository.SubjectRepository;

import java.util.Optional;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectRepository subjectRepository;

    public SubjectController(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    @GetMapping("/{id}")
    public Optional<Subject> getSubjectById(@PathVariable String id) {
        return subjectRepository.findById(id);
    }
}
