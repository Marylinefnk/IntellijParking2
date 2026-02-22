package intellijP.back.controllers;

import intellijP.back.dto.AuthResponse;
import intellijP.back.dto.LoginRequest;
import intellijP.back.dto.RegisterRequest;
import intellijP.back.models.Personne;
import intellijP.back.models.TypePersonne;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.security.JwtUtil;
import intellijP.back.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthResponse register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @GetMapping("/me")
    public AuthResponse getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        String token = authService.extractTokenFromHeader(authHeader);
        return authService.getCurrentUser(token);
    }
}
