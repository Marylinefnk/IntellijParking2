package intellijP.back.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Gere les erreurs d'authentification.
     * @return 401 Unauthorized
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> handleAuthentication(
            AuthenticationException ex, WebRequest request) {

        logger.warn("Erreur d'authentification: {}", ex.getMessage());

        Map<String, Object> body = buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            "UNAUTHORIZED",
            ex.getMessage(),
            request
        );

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    /**
     * Gere les ressources non trouvees.
     * @return 404 Not Found
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleResourceNotFound(
            ResourceNotFoundException ex, WebRequest request) {

        logger.warn("Ressource non trouvee: {} - {}", ex.getResourceType(), ex.getMessage());

        Map<String, Object> body = buildErrorResponse(
            HttpStatus.NOT_FOUND,
            "NOT_FOUND",
            ex.getMessage(),
            request
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    /**
     * Gere les erreurs de validation.
     * @return 400 Bad Request
     */
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(
            ValidationException ex, WebRequest request) {

        logger.warn("Erreur de validation: {}", ex.getMessage());

        Map<String, Object> body = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getCode(),
            ex.getMessage(),
            request
        );

        if (ex.getField() != null) {
            body.put("field", ex.getField());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Gere les conflits (doublons, creneaux deja reserves).
     * @return 409 Conflict
     */
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(
            ConflictException ex, WebRequest request) {

        logger.warn("Conflit detecte: {}", ex.getMessage());

        Map<String, Object> body = buildErrorResponse(
            HttpStatus.CONFLICT,
            ex.getCode(),
            ex.getMessage(),
            request
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    /**
     * Gere les operations non autorisees.
     * @return 400 Bad Request
     */
    @ExceptionHandler(OperationNotAllowedException.class)
    public ResponseEntity<Map<String, Object>> handleOperationNotAllowed(
            OperationNotAllowedException ex, WebRequest request) {

        logger.warn("Operation non autorisee: {}", ex.getMessage());

        Map<String, Object> body = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getCode(),
            ex.getMessage(),
            request
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Gere toutes les autres erreurs metier.
     * @return 400 Bad Request
     */
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Map<String, Object>> handleBusiness(
            BusinessException ex, WebRequest request) {

        logger.warn("Erreur metier: {} - {}", ex.getCode(), ex.getMessage());

        Map<String, Object> body = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getCode(),
            ex.getMessage(),
            request
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Gere les RuntimeException non attrapees (anciennes exceptions).
     * Pour compatibilite avec le code existant.
     * @return 400 Bad Request
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, Object>> handleRuntime(
            RuntimeException ex, WebRequest request) {

        logger.error("Erreur runtime: {}", ex.getMessage());

        Map<String, Object> body = buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            "ERROR",
            ex.getMessage(),
            request
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    /**
     * Gere toutes les autres exceptions non prevues.
     * @return 500 Internal Server Error
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(
            Exception ex, WebRequest request) {

        logger.error("Erreur interne inattendue: {}", ex.getMessage(), ex);

        Map<String, Object> body = buildErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_ERROR",
            "Une erreur interne s'est produite",
            request
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    /**
     * Construit une reponse d'erreur
     */
    private Map<String, Object> buildErrorResponse(
            HttpStatus status, String code, String message, WebRequest request) {

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now().toString());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("code", code);
        body.put("message", message);
        body.put("path", request.getDescription(false).replace("uri=", ""));

        return body;
    }
}
