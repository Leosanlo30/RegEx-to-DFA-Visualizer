package controllers.Exceptions;

// Excepcion para cuando se lea un archivo de texto que no cumpla con el formato de alfabeto esperado (solo letras y números, sin símbolos especiales).

public class InvalidAlphabetException extends RuntimeException {

private char invalidSymbol;

public InvalidAlphabetException(String message, char invalidSymbol) {
	super(message);
	this.invalidSymbol = invalidSymbol;
}

public char getInvalidSymbol() {
	return invalidSymbol;
}
}