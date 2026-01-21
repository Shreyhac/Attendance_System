package spring_masters.attendance_system.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import spring_masters.attendance_system.model.entity.User;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmail(String email);
}
