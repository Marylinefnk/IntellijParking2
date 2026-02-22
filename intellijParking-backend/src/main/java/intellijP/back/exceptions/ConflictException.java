package intellijP.back.exceptions;

/**
 * Exception levee en cas de conflit (doublon, creneau deja reserve, etc.).
 * Resulte en une reponse HTTP 409 Conflict.
 */
public class ConflictException extends BusinessException {

    public ConflictException(String message) {
        super("CONFLICT", message);
    }
}
