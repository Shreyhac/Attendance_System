package spring_masters.attendance_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import spring_masters.attendance_system.exception.ResourceNotFoundException;
import spring_masters.attendance_system.model.entity.Subject;
import spring_masters.attendance_system.repository.SubjectRepository;

import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    @Cacheable(value = "subjects", key = "#id")
    public Subject findById(String id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));
    }

    @Cacheable(value = "subjects", key = "'all'")
    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    @Cacheable(value = "subjects", key = "'teacher_' + #teacherEmail")
    public List<Subject> findByTeacherEmail(String teacherEmail) {
        return subjectRepository.findByTeacherEmail(teacherEmail);
    }

    @CacheEvict(value = "subjects", allEntries = true)
    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    @CacheEvict(value = "subjects", allEntries = true)
    public void deleteById(String id) {
        subjectRepository.deleteById(id);
    }
}
