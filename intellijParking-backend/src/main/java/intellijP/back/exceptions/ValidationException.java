package intellijP.back.exceptions;

/**
 * Exception levee quand les donnees fournies sont invalides.
 * Resulte en une reponse HTTP 400 Bad Request.
 */
public class ValidationException extends BusinessException {

    private final String field;

    public ValidationException(String message) {
        super("VALIDATION_ERROR", message);
        this.field = null;
    }

    public ValidationException(String field, String message) {
        super("VALIDATION_ERROR", message);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
