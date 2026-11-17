package controllers;

import controllers.Exceptions.RegexSyntaxException;

// Contrato entre la interfaz y la lógica de conversión.
// La UI solo depende de esta interfaz; cuando exista el convertidor real (Thompson + subconjuntos + minimización)
// basta con cambiar la implementación que se pasa en views.Main.

public interface Converter {

    // Lanza RegexSyntaxException si la expresión no es válida.
    ConversionResult convert(String regex) throws RegexSyntaxException;
}
