package spring_masters.attendance_system.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import spring_masters.attendance_system.model.entity.Attendance;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class AttendanceRepositoryImpl implements AttendanceRepositoryCustom {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public AttendanceRepositoryImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public List<Attendance> searchAttendance(
            String studentEmail,
            String subjectId,
            Boolean present,
            LocalDate startDate,
            LocalDate endDate) {

        Query query = new Query();
        List<Criteria> criteria = new ArrayList<>();

        if (studentEmail != null && !studentEmail.isEmpty()) {
            criteria.add(Criteria.where("studentEmail").is(studentEmail));
        }

        if (subjectId != null && !subjectId.isEmpty()) {
            criteria.add(Criteria.where("subjectId").is(subjectId));
        }

        if (present != null) {
            criteria.add(Criteria.where("present").is(present));
        }

        if (startDate != null && endDate != null) {
            criteria.add(Criteria.where("date").gte(startDate).lte(endDate));
        } else if (startDate != null) {
            criteria.add(Criteria.where("date").gte(startDate));
        } else if (endDate != null) {
            criteria.add(Criteria.where("date").lte(endDate));
        }

        if (!criteria.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteria.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Attendance.class);
    }
}
