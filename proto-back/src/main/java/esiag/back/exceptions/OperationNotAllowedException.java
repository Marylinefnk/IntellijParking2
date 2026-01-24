package esiag.back.exceptions;

//Exception levee quand une operation n'est pas permise dans l'etat actuel.
 
 
public class OperationNotAllowedException extends BusinessException {

    public OperationNotAllowedException(String message) {
        super("OPERATION_NOT_ALLOWED", message);
    }
}
