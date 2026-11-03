package controllers.Exceptions;

// Excepción para lanzar errores de formato de archivo, si fuera un archivo que no sea .txt o .json, o si el archivo está vacío o corrupto.

public class FileFormatException extends RuntimeException {

	public FileFormatException(String message) {
		super(message);
	}


public FileFormatException(String message, Throwable cause) {
		super(message, cause);
	}
}