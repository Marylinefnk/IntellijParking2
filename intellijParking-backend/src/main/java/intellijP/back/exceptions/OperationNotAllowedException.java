package intellijP.back.exceptions;

 
 
public class OperationNotAllowedException extends BusinessException {

    public OperationNotAllowedException(String message) {
        super("OPERATION_NOT_ALLOWED", message);
    }
}
