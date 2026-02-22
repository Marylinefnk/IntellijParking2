package intellijP.back.controllers;

import intellijP.back.dto.AuthResponse;
import intellijP.back.dto.LoginRequest;
import intellijP.back.dto.RegisterRequest;
import intellijP.back.models.Personne;
import intellijP.back.models.TypePersonne;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.security.JwtUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://127.0.0.1:3000"})
public class AuthController {

    private final PersonneRepository personneRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthController(PersonneRepository personneRepository,
                         PasswordEncoder passwordEncoder,
                         JwtUtil jwtUtil) {
        this.personneRepository = personneRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        Optional<Personne> personneOpt = personneRepository.findByMail(request.getEmail());

        if (personneOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email ou mot de passe incorrect"));
        }

        Personne personne = personneOpt.get();

        // Check password
        if (personne.getPassword() == null || !passwordEncoder.matches(request.getPassword(), personne.getPassword())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Email ou mot de passe incorrect"));
        }

        // Generate JWT token
        String token = jwtUtil.generateToken(
                personne.getId(),
                personne.getMail(),
                personne.getTypePersonne().name()
        );

        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .id(personne.getId())
                .nom(personne.getNom())
                .prenom(personne.getPrenom())
                .email(personne.getMail())
                .typePersonne(personne.getTypePersonne())
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Validate required fields
        if (request.getNom() == null || request.getNom().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le nom est obligatoire"));
        }
        if (request.getPrenom() == null || request.getPrenom().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le prenom est obligatoire"));
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "L'email est obligatoire"));
        }
        if (request.getPassword() == null || request.getPassword().length() < 6) {
            return ResponseEntity.badRequest().body(Map.of("error", "Le mot de passe doit contenir au moins 6 caracteres"));
        }

        // Validate email format
        if (!isValidEmail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Format d'email invalide"));
        }

        // Check if email already exists
        if (personneRepository.existsByMail(request.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Un compte avec cet email existe deja"));
        }

        // Create new user with VISITEUR role (default for new registrations)
        Personne personne = Personne.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .mail(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .typePersonne(TypePersonne.VISITEUR)
                .build();

        personne = personneRepository.save(personne);

        // Generate JWT token
        String token = jwtUtil.generateToken(
                personne.getId(),
                personne.getMail(),
                personne.getTypePersonne().name()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.builder()
                .token(token)
                .id(personne.getId())
                .nom(personne.getNom())
                .prenom(personne.getPrenom())
                .email(personne.getMail())
                .typePersonne(personne.getTypePersonne())
                .build());
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token manquant"));
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Token invalide ou expire"));
        }

        String email = jwtUtil.extractEmail(token);
        Optional<Personne> personneOpt = personneRepository.findByMail(email);

        if (personneOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Utilisateur non trouve"));
        }

        Personne personne = personneOpt.get();
        return ResponseEntity.ok(AuthResponse.builder()
                .token(token)
                .id(personne.getId())
                .nom(personne.getNom())
                .prenom(personne.getPrenom())
                .email(personne.getMail())
                .typePersonne(personne.getTypePersonne())
                .build());
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
}
