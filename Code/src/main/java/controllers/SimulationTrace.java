package controllers;

import controllers.Exceptions.AutomatonException;
import java.util.ArrayList;
import java.util.List;
import models.Automaton.DFA;
import models.Automaton.State;

// Recorrido de una cadena sobre un AFD. Guarda los estados visitados para que la UI pueda animarlos.
// stuckAt es la posición del símbolo que no tuvo transición, o NOT_STUCK si se consumió toda la cadena.

public record SimulationTrace(String input, List<State> path, boolean accepted, int stuckAt) {

    public static final int NOT_STUCK = -1;

    public SimulationTrace {
        path = List.copyOf(path);
    }

    public static SimulationTrace run(DFA dfa, String input) {
        State current = dfa.getInitial_state();
        if (current == null) throw new AutomatonException("El autómata no tiene estado inicial");

        List<State> path = new ArrayList<>();
        path.add(current);
        for (int i = 0; i < input.length(); i++) {
            current = dfa.transition(current, input.charAt(i));
            if (current == null) return new SimulationTrace(input, path, false, i);
            path.add(current);
        }
        return new SimulationTrace(input, path, dfa.isFinal(current), NOT_STUCK);
    }

    public boolean isStuck() {
        return stuckAt != NOT_STUCK;
    }

    public State lastState() {
        return path.get(path.size() - 1);
    }
}
