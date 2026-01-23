package esiag.back.exceptions;

/**
 * Exception pour les erreurs d'authentification (401 Unauthorized).
 * Utilisee pour les identifiants invalides, tokens expires, etc.
 */
public class AuthenticationException extends RuntimeException {

    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
