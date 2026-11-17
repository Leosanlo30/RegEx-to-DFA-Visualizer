package views.table;

import static org.junit.jupiter.api.Assertions.assertEquals;

import controllers.SampleConverter;
import models.Automaton.State;
import org.junit.jupiter.api.Test;

class TransitionTableModelTest {

    private final TransitionTableModel model = new TransitionTableModel();

    @Test
    void emptyModel_hasOnlyStateColumn() {
        assertEquals(0, model.getRowCount());
        assertEquals(1, model.getColumnCount());
        assertEquals(TransitionTableModel.STATE_COLUMN, model.getColumnName(0));
    }

    @Test
    void setDfa_buildsRowsPerStateAndColumnsPerSymbol() {
        model.setDfa(new SampleConverter().convert("(a|b)*abb").minimalDfa());

        assertEquals(4, model.getRowCount());
        assertEquals(3, model.getColumnCount());
        assertEquals("a", model.getColumnName(1));
        assertEquals("b", model.getColumnName(2));
        assertEquals("q2", model.getValueAt(1, 2));
    }

    @Test
    void stateLabels_markInitialAndAcceptingStates() {
        model.setDfa(new SampleConverter().convert("(ab)*").minimalDfa());

        assertEquals("→* q0", model.getValueAt(0, 0));
        assertEquals("   q1", model.getValueAt(1, 0));
    }

    @Test
    void missingTransition_showsDash() {
        model.setDfa(new SampleConverter().convert("(ab)*").minimalDfa());

        assertEquals(TransitionTableModel.NO_TRANSITION, model.getValueAt(0, 2));
    }

    @Test
    void rowOf_returnsRowOrMinusOne() {
        model.setDfa(new SampleConverter().convert("a*b").minimalDfa());

        assertEquals(1, model.rowOf(new State(1)));
        assertEquals(-1, model.rowOf(new State(9)));
    }
}
