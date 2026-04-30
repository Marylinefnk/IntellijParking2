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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private PersonneRepository personneRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;

    @Test
    void testLogin() {
        LoginRequest request = new LoginRequest();
        request.setEmail("alice@mail.com");
        request.setPassword("motdepasse");

        Personne personne = new Personne();
        personne.setId(4L);
        personne.setNom("Durand");
        personne.setPrenom("Alice");
        personne.setMail("alice@mail.com");
        personne.setPassword("hash");
        personne.setTypePersonne(TypePersonne.VISITEUR);

        when(personneRepository.findByMail("alice@mail.com")).thenReturn(Optional.of(personne));
        when(passwordEncoder.matches("motdepasse", "hash")).thenReturn(true);
        when(jwtUtil.generateToken(4L, "alice@mail.com", "VISITEUR")).thenReturn("token-abc");

        AuthResponse resultat = authService.login(request);
        assertEquals("token-abc", resultat.getToken());
        assertEquals("alice@mail.com", resultat.getEmail());
    }
      @Test
    void testLoginEmailInexistant() {
        LoginRequest request = new LoginRequest();
        request.setEmail("inconnu@mail.com");
        request.setPassword("xxxxxx");
        when(personneRepository.findByMail("inconnu@mail.com")).thenReturn(Optional.empty());

        var resultat = assertThrows(AuthenticationException.class, () -> authService.login(request));
        assertEquals("Email ou mot de passe incorrect", resultat.getMessage());
    }
    @Test
    void testLoginMauvaisMotDePasse() {
        LoginRequest request = new LoginRequest();
        request.setEmail("bob@mail.com");
        request.setPassword("mauvais");
        Personne personne = new Personne();
        personne.setId(6L);
        personne.setMail("bob@mail.com");
        personne.setPassword("hash");

        when(personneRepository.findByMail("bob@mail.com")).thenReturn(Optional.of(personne));
        when(passwordEncoder.matches("mauvais", "hash")).thenReturn(false);
        assertThrows(AuthenticationException.class, () -> authService.login(request));
    }
       @Test
    void testCreer() {
        RegisterRequest request = new RegisterRequest();
        request.setNom("Martin");
        request.setPrenom("Paul");
        request.setEmail("paul@mail.com");
        request.setPassword("azerty123");

        Personne saved = new Personne();
        saved.setId(11L);
        saved.setNom("Martin");
        saved.setPrenom("Paul");
        saved.setMail("paul@mail.com");
        saved.setTypePersonne(TypePersonne.VISITEUR);

        when(personneRepository.existsByMail("paul@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("azerty123")).thenReturn("hashe");
        when(personneRepository.save(any(Personne.class))).thenReturn(saved);
        when(jwtUtil.generateToken(11L, "paul@mail.com", "VISITEUR")).thenReturn("nouveau-token");

        AuthResponse resultat = authService.register(request);
        assertEquals("nouveau-token", resultat.getToken());
        assertEquals("Martin", resultat.getNom());
    }
    @Test
    void testRegisterEmailDejaUtilise() {
        RegisterRequest request = new RegisterRequest();
        request.setNom("Martin");
        request.setPrenom("Paul");
        request.setEmail("dejapris@mail.com");
        request.setPassword("azerty123");
        when(personneRepository.existsByMail("dejapris@mail.com")).thenReturn(true);

        var resultat = assertThrows(ConflictException.class, () -> authService.register(request));
        assertEquals("Un compte avec cet email existe deja", resultat.getMessage());
    }

      @Test
    void testRegisterEmailInvalide() {
        RegisterRequest request = new RegisterRequest();
        request.setNom("Martin");
        request.setPrenom("Paul");
        request.setEmail("pas-un-email");
        request.setPassword("azerty123");
        var resultat = assertThrows(ValidationException.class, () -> authService.register(request));
        assertEquals("Format d'email invalide", resultat.getMessage());
    }
    @Test
    void testValidatePasswordTropCourt(){
        var resultat = assertThrows(ValidationException.class, () -> authService.validatePassword("abc"));
        assertEquals("Le mot de passe doit contenir au moins 6 caracteres", resultat.getMessage());
    }
}
