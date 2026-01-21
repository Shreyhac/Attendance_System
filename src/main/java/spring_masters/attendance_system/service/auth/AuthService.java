package spring_masters.attendance_system.service.auth;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import spring_masters.attendance_system.dto.request.LoginRequest;
import spring_masters.attendance_system.dto.request.RegisterRequest;
import spring_masters.attendance_system.exception.DuplicateResourceException;
import spring_masters.attendance_system.exception.InvalidCredentialsException;
import spring_masters.attendance_system.model.entity.User;
import spring_masters.attendance_system.repository.UserRepository;
import spring_masters.attendance_system.util.JwtUtil;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository,
            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @CacheEvict(value = "users", allEntries = true)
    public String register(RegisterRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException("User", "email", request.getEmail());
        }

        User user = new User(
                request.getName(),
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getRole(),
                true,
                request.getLocation());

        userRepository.save(user);

        return "User registered successfully";
    }

    public String login(LoginRequest request) {

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid email or password");
        }

        // ✅ JWT contains email + role
        return JwtUtil.generateToken(
                user.getEmail(),
                user.getRole().name());
    }
}
