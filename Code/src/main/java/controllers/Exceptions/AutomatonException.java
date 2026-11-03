package controllers.Exceptions;

// Excepción para cuando hayan errores de OPERACIONES IMPOSIBLES NO VÁLIDAS en el autómata.
// Es personalizada, depende de lo que se escriba en los modelos.

public class AutomatonException extends RuntimeException {
    
    public AutomatonException(String message) {
        super(message);
    }
}