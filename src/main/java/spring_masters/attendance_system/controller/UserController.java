package spring_masters.attendance_system.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import spring_masters.attendance_system.model.entity.User;
import spring_masters.attendance_system.repository.UserRepository;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User Management", description = "Endpoints for user profile management")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @PutMapping("/profile/location")
    @Operation(summary = "Update user's weather location preference")
    public ResponseEntity<?> updateLocation(@RequestBody Map<String, String> payload) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String newLocation = payload.get("location");

        if (newLocation == null || newLocation.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Location is required");
        }

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setLocation(newLocation.trim());
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "message", "Location updated successfully",
                "location", user.getLocation()));
    }

    @GetMapping("/profile")
    @Operation(summary = "Get current user profile")
    public ResponseEntity<User> getProfile() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(user);
    }
}
