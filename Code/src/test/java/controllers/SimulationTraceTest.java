package controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import controllers.Exceptions.AutomatonException;
import java.util.List;
import models.Automaton.DFA;
import models.Automaton.State;
import org.junit.jupiter.api.Test;

class SimulationTraceTest {

    private final DFA dfa = new SampleConverter().convert("a*b").minimalDfa();

    @Test
    void run_acceptedInput_recordsEveryVisitedState() {
        SimulationTrace trace = SimulationTrace.run(dfa, "aab");

        assertTrue(trace.accepted());
        assertFalse(trace.isStuck());
        assertEquals(List.of(new State(0), new State(0), new State(0), new State(1)), trace.path());
    }

    @Test
    void run_missingTransition_stopsAndReportsPosition() {
        SimulationTrace trace = SimulationTrace.run(dfa, "abx");

        assertFalse(trace.accepted());
        assertEquals(2, trace.stuckAt());
        assertEquals(new State(1), trace.lastState());
    }

    @Test
    void run_endsInNonFinalState_isRejectedWithoutBeingStuck() {
        SimulationTrace trace = SimulationTrace.run(dfa, "aa");

        assertFalse(trace.accepted());
        assertFalse(trace.isStuck());
    }

    @Test
    void run_emptyInput_staysInInitialState() {
        SimulationTrace trace = SimulationTrace.run(dfa, "");

        assertEquals(List.of(new State(0)), trace.path());
        assertFalse(trace.accepted());
    }

    @Test
    void run_withoutInitialState_throws() {
        assertThrows(AutomatonException.class, () -> SimulationTrace.run(new DFA(), "a"));
    }
}
