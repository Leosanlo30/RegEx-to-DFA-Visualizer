package views.automaton;

import java.awt.geom.Point2D;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import models.Automaton.DFA;
import models.Automaton.State;

// Acomoda los estados por capas, de izquierda a derecha, según su distancia (BFS) al estado inicial.
// Es determinista: la misma ER siempre produce el mismo dibujo, como en los libros.

public final class AutomatonLayout {

    static final double LAYER_GAP = 150;
    static final double ROW_GAP = 120;

    private AutomatonLayout() {
    }

    public static Map<State, Point2D> compute(DFA dfa) {
        Map<Integer, List<State>> statesByLayer = new TreeMap<>();
        assignLayers(dfa).forEach((state, layer) ->
                statesByLayer.computeIfAbsent(layer, k -> new ArrayList<>()).add(state));

        Map<State, Point2D> positions = new LinkedHashMap<>();
        statesByLayer.forEach((layer, states) -> {
            double centerOffset = (states.size() - 1) / 2.0;
            for (int row = 0; row < states.size(); row++) {
                positions.put(states.get(row), new Point2D.Double(layer * LAYER_GAP, (row - centerOffset) * ROW_GAP));
            }
        });
        return Collections.unmodifiableMap(positions);
    }

    private static Map<State, Integer> assignLayers(DFA dfa) {
        Map<State, Integer> layers = new LinkedHashMap<>();
        State initial = dfa.getInitial_state();
        if (initial != null) {
            Deque<State> queue = new ArrayDeque<>();
            layers.put(initial, 0);
            queue.add(initial);
            while (!queue.isEmpty()) {
                State current = queue.poll();
                for (State next : successors(dfa, current)) {
                    if (layers.containsKey(next)) continue;
                    layers.put(next, layers.get(current) + 1);
                    queue.add(next);
                }
            }
        }
        // Los estados inalcanzables (p. ej. de un .json editado a mano) van en una capa extra al final
        int extraLayer = layers.values().stream().max(Integer::compare).orElse(-1) + 1;
        dfa.getStates().stream()
                .sorted(Comparator.comparingInt(State::getId))
                .filter(state -> !layers.containsKey(state))
                .forEach(state -> layers.put(state, extraLayer));
        return layers;
    }

    private static List<State> successors(DFA dfa, State state) {
        return dfa.getDelta().getOrDefault(state, Map.of()).entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(Map.Entry::getValue)
                .toList();
    }
}
