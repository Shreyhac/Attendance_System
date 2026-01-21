package spring_masters.attendance_system.controller.auth;

import org.springframework.web.bind.annotation.*;
import spring_masters.attendance_system.dto.request.LoginRequest;
import spring_masters.attendance_system.dto.request.RegisterRequest;
import spring_masters.attendance_system.service.auth.AuthService;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
