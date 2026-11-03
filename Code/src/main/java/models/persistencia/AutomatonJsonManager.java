package models.persistencia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import controllers.Exceptions.FileFormatException;
import models.Automaton.DFA;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

// Para analizar y manejar los archivos .json se utiliza la dependencia Gson, de Google.


// Esta clase serializa y deserializa los autómatas (DFA) en archivos .json.


public class AutomatonJsonManager {

	private static final Gson gson = new GsonBuilder().enableComplexMapKeySerialization().setPrettyPrinting().create();

	// Guarda un DFA en un archivo .json

	public static void saveDFA(DFA dfa, String filepath) { //DFA es el autómata que se guardará, y filepath es la ruta del archivo en donde se desea guardarlo en el dispositivo.
		try (FileWriter writer = new FileWriter(filepath)) {
			// Convierte el DFA a formato JSON y lo escribe en el archivo
			gson.toJson(dfa, writer);
		} 
		
		catch (IOException e) {
			
		throw new FileFormatException("Error al guardar el archivo .json: " + e.getMessage(), e);
		}
	}

// Carga un DFA desde un archivo .json

	public static DFA loadDFA(String filepath) {  // Este filepath es la ruta desde donde se cargará el .json que se desea leer.	
		try (FileReader reader = new FileReader(filepath)) {
		DFA dfa = gson.fromJson(reader, DFA.class);

		if (dfa == null) {
				throw new FileFormatException("El archivo .json cargado está vacío o corrupto");
			}
			return dfa; // Devuelve el DFA cargado desde el archivo .json
		} 
		
		catch (IOException e) { // En caso de que no se pueda leer el archivo.
			
		throw new FileFormatException("Error al leer el archivo .json: " + e.getMessage(), e);
		}

		catch (Exception e) { // En caso de que el contenido del .json no sea válido.
			
			throw new FileFormatException("El archivo .json no corresponde a un autómata válido", e);
        }
    }
    }