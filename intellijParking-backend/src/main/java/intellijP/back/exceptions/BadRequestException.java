package intellijP.back.exceptions;

public class BadRequestException extends BusinessException {

    public BadRequestException(String message) {
        super("Bad Request", message);
    }
}
