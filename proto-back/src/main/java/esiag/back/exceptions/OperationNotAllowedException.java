package esiag.back.exceptions;

/**
 * Exception levee quand une operation n'est pas permise dans l'etat actuel.
 * Exemple: annuler une reservation deja terminee, supprimer une place occupee.
 * Resulte en une reponse HTTP 400 Bad Request.
 */
public class OperationNotAllowedException extends BusinessException {

    public OperationNotAllowedException(String message) {
        super("OPERATION_NOT_ALLOWED", message);
    }
}
