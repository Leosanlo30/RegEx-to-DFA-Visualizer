package controllers.Exceptions;

// Excepción para errores de sintaxis en las expresiones regulares introducidas, requiere especificar tipo de error y posición del error en la cadena.
// Errores del análisis léxico y sintáctico de una ER.

public class RegexSyntaxException extends RuntimeException {

private String errortype; // Tipo de error de sintaxis
private int position; // Posición del error en la cadena de entrada


//Constructor
public RegexSyntaxException(String message, String errortype, int position) {
	super(message);
	this.errortype = errortype;
	this.position = position;
}


public String getErrortype() {
	return errortype;
}

public int getPosition() {
		return position;
}

// toString específicamente para errores de sintaxis
@Override
public String toString() {
	return "Error [" + errortype + "] en la posición " + position + ": " + super.getMessage();
}

}

