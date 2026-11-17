package controllers;

import java.util.Objects;
import models.Automaton.DFA;
import models.Automaton.NFA;

// Resultado de convertir una ER. nfa y dfa son opcionales (null) cuando solo se conoce el AFD mínimo,
// por ejemplo al cargar un .json o con el convertidor de ejemplo.

public record ConversionResult(String regex, NFA nfa, DFA dfa, DFA minimalDfa) {

    public ConversionResult {
        Objects.requireNonNull(regex, "regex");
        Objects.requireNonNull(minimalDfa, "minimalDfa");
    }

    public static ConversionResult ofMinimal(String regex, DFA minimalDfa) {
        return new ConversionResult(regex, null, null, minimalDfa);
    }

    public boolean hasPipeline() {
        return nfa != null && dfa != null;
    }
}
