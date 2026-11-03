package persistencia;

import controllers.Exceptions.FileFormatException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

// Esta clase exclusivamente lee expresiones regulares desde archivos .txt

public class RegexFileReader {


// Lee la primera línea de un archivo .txt con la ER.

// filePath es la ruta del archivo .txt que se desea leer.
public static String readRegex(String filePath) { 
	
try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
	String line = br.readLine();

	if (line == null || line.trim().isEmpty()) {
		throw new FileFormatException("El archivo .txt cargado está vacío");

	}

	return line.trim(); // Devuelve la primera línea del archivo .txt, que es la expresión regular en formato String.
}

catch (IOException e) {
	throw new FileFormatException("Error al leer el archivo .txt: " + e.getMessage(), e); // Error de lectura (excepción personalizada, se debe especificar) 
}
}
}