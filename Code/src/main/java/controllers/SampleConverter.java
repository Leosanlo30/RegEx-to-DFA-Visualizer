package controllers;

import controllers.Exceptions.RegexSyntaxException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import models.Automaton.DFA;
import models.Automaton.State;

// Convertidor TEMPORAL para poder desarrollar la interfaz mientras se terminan Lexer, Parser,
// Thompson, subconjuntos y minimización. Valida la sintaxis básica y regresa AFD mínimos
// armados a mano para los ejemplos del menú Sintaxis.
// Cuando exista el convertidor real, reemplazarlo en views.Main y borrar esta clase.

public class SampleConverter implements Converter {

    static final String NOT_AVAILABLE_MESSAGE =
            "La expresión es válida, pero el convertidor real todavía no está integrado. "
            + "Prueba con un ejemplo del menú Sintaxis.";

    private static final char EPSILON = 'ε';

    private static final Map<String, Supplier<DFA>> SAMPLES = Map.of(
            "(a|b)*abb", () -> build(4, List.of(3),
                    "0a1", "0b0", "1a1", "1b2", "2a1", "2b3", "3a1", "3b0"),
            "a*b", () -> build(2, List.of(1),
                    "0a0", "0b1"),
            "(ab)*", () -> build(2, List.of(0),
                    "0a1", "1b0"),
            "(0|1)*1", () -> build(2, List.of(1),
                    "000", "011", "100", "111"));

    @Override
    public ConversionResult convert(String regex) {
        validate(regex);
        Supplier<DFA> sample = SAMPLES.get(regex);
        if (sample == null) throw new UnsupportedOperationException(NOT_AVAILABLE_MESSAGE);
        return ConversionResult.ofMinimal(regex, sample.get());
    }

    // Validación mínima de sintaxis; la versión completa vivirá en el Parser
    static void validate(String regex) {
        if (regex == null || regex.isEmpty()) {
            throw new RegexSyntaxException("Escribe una expresión regular.", "Expresión vacía", 0);
        }
        Deque<Integer> openGroups = new ArrayDeque<>();
        for (int i = 0; i < regex.length(); i++) {
            char c = regex.charAt(i);
            char previous = i == 0 ? '(' : regex.charAt(i - 1);
            switch (c) {
                case '(' -> openGroups.push(i);
                case ')' -> {
                    if (openGroups.isEmpty()) throw error("Este ')' no tiene un '(' que lo abra.", "Paréntesis sin abrir", i);
                    if (previous == '(') throw error("El grupo '()' está vacío.", "Grupo vacío", i);
                    if (previous == '|') throw error("Falta un operando después de '|'.", "Operando faltante", i);
                    openGroups.pop();
                }
                case '*' -> {
                    if (previous == '(' || previous == '|') throw error("'*' no tiene a qué aplicarse.", "Operando faltante", i);
                }
                case '|' -> {
                    if (previous == '(' || previous == '|') throw error("Falta un operando antes de '|'.", "Operando faltante", i);
                }
                default -> {
                    if (!isAlphabetSymbol(c) && c != EPSILON) {
                        throw error("'" + c + "' no es un símbolo válido. Usa letras, dígitos u operadores.", "Símbolo no válido", i);
                    }
                }
            }
        }
        int last = regex.length() - 1;
        if (regex.charAt(last) == '|') throw error("Falta un operando después de '|'.", "Operando faltante", last);
        if (!openGroups.isEmpty()) throw error("Falta cerrar este '('.", "Paréntesis sin cerrar", openGroups.peek());
    }

    private static boolean isAlphabetSymbol(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }

    private static RegexSyntaxException error(String message, String type, int position) {
        return new RegexSyntaxException(message, type, position);
    }

    // Cada transición se escribe como "origen símbolo destino", p. ej. "0a1"
    private static DFA build(int stateCount, List<Integer> finals, String... transitions) {
        DFA dfa = new DFA();
        for (int i = 0; i < stateCount; i++) dfa.newState();
        dfa.setInitial_state(new State(0));
        for (int id : finals) dfa.markFinals(new State(id));
        for (String t : transitions) {
            dfa.addTransition(new State(t.charAt(0) - '0'), t.charAt(1), new State(t.charAt(2) - '0'));
        }
        return dfa;
    }
}
