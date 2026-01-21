package spring_masters.attendance_system.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import spring_masters.attendance_system.dto.request.LoginRequest;
import spring_masters.attendance_system.dto.request.RegisterRequest;
import spring_masters.attendance_system.service.auth.AuthService;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "Endpoints for user registration and login")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details.")
    public String register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user", description = "Authenticates a user and returns a JWT token.")
    public String login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
