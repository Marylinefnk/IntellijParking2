package intellijP.back.services;
import intellijP.back.dto.AuthResponse;
import intellijP.back.dto.LoginRequest;
import intellijP.back.dto.RegisterRequest;
import intellijP.back.exceptions.AuthenticationException;
import intellijP.back.exceptions.ConflictException;
import intellijP.back.exceptions.ValidationException;
import intellijP.back.models.Personne;
import intellijP.back.models.TypePersonne;
import intellijP.back.repositories.PersonneRepository;
import intellijP.back.security.JwtUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Service
@Transactional
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";

    private final PersonneRepository personneRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(PersonneRepository personneRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.personneRepository = personneRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        logger.info("AuthService initialise");
    }

    public AuthResponse login(LoginRequest request) {
        logger.debug("Tentative de connexion pour l'email: {}", request.getEmail());

        Optional<Personne> personneOpt = personneRepository.findByMail(request.getEmail());

        if (personneOpt.isEmpty()) {
            logger.warn("Echec connexion: email non trouve - {}", request.getEmail());
            throw new AuthenticationException("Email ou mot de passe incorrect");
        }

        Personne personne = personneOpt.get();

        if (personne.getPassword() == null || !passwordEncoder.matches(request.getPassword(), personne.getPassword())) {
            logger.warn("Echec connexion: mot de passe incorrect pour l'email - {}", request.getEmail());
            throw new AuthenticationException("Email ou mot de passe incorrect");
        }

        String token = generateToken(personne);
        logger.info("Connexion reussie pour l'utilisateur: {} {} (id={})",
            personne.getPrenom(), personne.getNom(), personne.getId());

        return buildAuthResponse(personne, token);
    }

    public AuthResponse register(RegisterRequest request) {
        logger.debug("Tentative d'inscription pour l'email: {}", request.getEmail());

        validateRegistrationData(request);

        if (personneRepository.existsByMail(request.getEmail())) {
            logger.warn("Echec inscription: email deja utilise - {}", request.getEmail());
            throw new ConflictException("Un compte avec cet email existe deja");
        }

        Personne personne = Personne.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .mail(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .typePersonne(TypePersonne.VISITEUR)
                .build();

        personne = personneRepository.save(personne);
        String token = generateToken(personne);

        logger.info("Inscription reussie pour l'utilisateur: {} {} (id={})",
            personne.getPrenom(), personne.getNom(), personne.getId());

        return buildAuthResponse(personne, token);
    }

    public AuthResponse getCurrentUser(String token) {
        logger.debug("Verification du token pour recuperer l'utilisateur courant");

        if (!jwtUtil.validateToken(token)) {
            logger.warn("Token invalide ou expire");
            throw new AuthenticationException("Token invalide ou expire");
        }

        String email = jwtUtil.extractEmail(token);
        Optional<Personne> personneOpt = personneRepository.findByMail(email);

        if (personneOpt.isEmpty()) {
            logger.warn("Utilisateur non trouve pour l'email du token: {}", email);
            throw new AuthenticationException("Utilisateur non trouve");
        }

        Personne personne = personneOpt.get();
        logger.debug("Utilisateur courant recupere: {} (id={})", personne.getMail(), personne.getId());

        return buildAuthResponse(personne, token);
    }
    public String extractTokenFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.warn("Header Authorization manquant ou invalide");
            throw new AuthenticationException("Token manquant");
        }
        return authHeader.substring(7);
    }
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    public void validatePassword(String password) {
        if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
            throw new ValidationException("Le mot de passe doit contenir au moins " + MIN_PASSWORD_LENGTH + " caracteres");
        }
    }

    public boolean isValidEmail(String email) {
        return email != null && email.matches(EMAIL_REGEX);
    }

    private void validateRegistrationData(RegisterRequest request) {
        if (request.getNom() == null || request.getNom().trim().isEmpty()) {
            logger.debug("Validation echouee: nom manquant");
            throw new ValidationException("Le nom est obligatoire");
        }
        if (request.getPrenom() == null || request.getPrenom().trim().isEmpty()) {
            logger.debug("Validation echouee: prenom manquant");
            throw new ValidationException("Le prenom est obligatoire");
        }
        if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
            logger.debug("Validation echouee: email manquant");
            throw new ValidationException("L'email est obligatoire");
        }
        if (!isValidEmail(request.getEmail())) {
            logger.debug("Validation echouee: format email invalide - {}", request.getEmail());
            throw new ValidationException("Format d'email invalide");
        }
        if (request.getPassword() == null || request.getPassword().length() < MIN_PASSWORD_LENGTH) {
            logger.debug("Validation echouee: mot de passe trop court");
            throw new ValidationException("Le mot de passe doit contenir au moins " + MIN_PASSWORD_LENGTH + " caracteres");
        }
    }

    private String generateToken(Personne personne) {
        return jwtUtil.generateToken(
                personne.getId(),
                personne.getMail(),
                personne.getTypePersonne().name()
        );
    }

    private AuthResponse buildAuthResponse(Personne personne, String token) {
        return AuthResponse.builder()
                .token(token)
                .id(personne.getId())
                .nom(personne.getNom())
                .prenom(personne.getPrenom())
                .email(personne.getMail())
                .typePersonne(personne.getTypePersonne())
                .build();
    }
}
