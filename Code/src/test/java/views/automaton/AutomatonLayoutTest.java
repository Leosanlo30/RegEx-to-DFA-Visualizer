package views.automaton;

import static org.junit.jupiter.api.Assertions.assertEquals;

import controllers.SampleConverter;
import java.awt.geom.Point2D;
import java.util.Map;
import models.Automaton.DFA;
import models.Automaton.State;
import org.junit.jupiter.api.Test;

class AutomatonLayoutTest {

    @Test
    void compute_placesStatesInLayersByDistanceFromInitial() {
        DFA dfa = new SampleConverter().convert("(a|b)*abb").minimalDfa();

        Map<State, Point2D> positions = AutomatonLayout.compute(dfa);

        assertEquals(4, positions.size());
        for (int id = 0; id < 4; id++) {
            assertEquals(new Point2D.Double(id * AutomatonLayout.LAYER_GAP, 0), positions.get(new State(id)));
        }
    }

    @Test
    void compute_unreachableStatesGoToAnExtraLayer() {
        DFA dfa = new SampleConverter().convert("a*b").minimalDfa();
        dfa.newState(); // q2, sin transiciones que lleguen a él

        Map<State, Point2D> positions = AutomatonLayout.compute(dfa);

        assertEquals(2 * AutomatonLayout.LAYER_GAP, positions.get(new State(2)).getX());
    }

    @Test
    void compute_centersStatesSharingALayer() {
        DFA dfa = new DFA();
        State q0 = dfa.newState();
        State q1 = dfa.newState();
        State q2 = dfa.newState();
        dfa.setInitial_state(q0);
        dfa.addTransition(q0, 'a', q1);
        dfa.addTransition(q0, 'b', q2);

        Map<State, Point2D> positions = AutomatonLayout.compute(dfa);

        assertEquals(-AutomatonLayout.ROW_GAP / 2, positions.get(q1).getY());
        assertEquals(AutomatonLayout.ROW_GAP / 2, positions.get(q2).getY());
    }
}
