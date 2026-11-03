package controllers.Exceptions;

// Excepción para cuando al comparar un string contra un autómata, el string propone recorridos no válidos.
// 

public class InvalidPathException extends RuntimeException {

public InvalidPathException(String message) {
		super(message);
	}
	public InvalidPathException(String message, Throwable cause) {
		super(message, cause);
	}
}