package controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import controllers.Exceptions.RegexSyntaxException;
import models.Automaton.DFA;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class SampleConverterTest {

    private final SampleConverter converter = new SampleConverter();

    @Test
    void convert_knownExample_returnsMinimalDfaThatAcceptsTheLanguage() {
        ConversionResult result = converter.convert("(a|b)*abb");

        DFA dfa = result.minimalDfa();
        assertEquals(4, dfa.getStates().size());
        assertTrue(dfa.accepts("abb"));
        assertTrue(dfa.accepts("babaabb"));
        assertFalse(dfa.accepts("ab"));
        assertFalse(result.hasPipeline());
    }

    @Test
    void convert_returnsFreshDfaEachTime() {
        DFA first = converter.convert("a*b").minimalDfa();
        DFA second = converter.convert("a*b").minimalDfa();

        assertFalse(first == second);
    }

    @Test
    void convert_validButUnknownRegex_throwsUnsupported() {
        assertThrows(UnsupportedOperationException.class, () -> converter.convert("abc"));
    }

    @ParameterizedTest
    @CsvSource({
            "'(a|b*abb', 0, Paréntesis sin cerrar",
            "'a)', 1, Paréntesis sin abrir",
            "'()', 1, Grupo vacío",
            "'*a', 0, Operando faltante",
            "'a||b', 2, Operando faltante",
            "'a|', 1, Operando faltante",
            "'(|a)', 1, Operando faltante",
            "'a+b', 1, Símbolo no válido",
            "'', 0, Expresión vacía"
    })
    void convert_invalidRegex_reportsTypeAndPosition(String regex, int position, String type) {
        RegexSyntaxException ex = assertThrows(RegexSyntaxException.class, () -> converter.convert(regex));

        assertEquals(position, ex.getPosition());
        assertEquals(type, ex.getErrortype());
    }

    @Test
    void validate_acceptsEpsilonAndNestedGroups() {
        SampleConverter.validate("((a|ε)b)*");
    }
}
