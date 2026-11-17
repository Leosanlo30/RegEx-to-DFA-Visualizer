package views.table;

import java.util.Comparator;
import java.util.List;
import javax.swing.table.AbstractTableModel;
import models.Automaton.DFA;
import models.Automaton.State;

// Tabla de transiciones del AFD: filas = estados, columnas = símbolos del alfabeto.

public class TransitionTableModel extends AbstractTableModel {

    public static final String NO_TRANSITION = "—";
    static final String STATE_COLUMN = "Estado";
    private static final String INITIAL_MARK = "→";
    private static final String FINAL_MARK = "*";

    private DFA dfa;
    private List<State> states = List.of();
    private List<Character> symbols = List.of();

    public void setDfa(DFA dfa) {
        this.dfa = dfa;
        this.states = dfa == null ? List.of()
                : dfa.getStates().stream().sorted(Comparator.comparingInt(State::getId)).toList();
        this.symbols = dfa == null ? List.of()
                : dfa.getAlphabet().stream().sorted().toList();
        fireTableStructureChanged();
    }

    public int rowOf(State state) {
        return states.indexOf(state);
    }

    @Override
    public int getRowCount() {
        return states.size();
    }

    @Override
    public int getColumnCount() {
        return symbols.size() + 1;
    }

    @Override
    public String getColumnName(int column) {
        return column == 0 ? STATE_COLUMN : String.valueOf(symbols.get(column - 1));
    }

    @Override
    public Object getValueAt(int row, int column) {
        State state = states.get(row);
        if (column == 0) return stateLabel(state);
        State target = dfa.transition(state, symbols.get(column - 1));
        return target == null ? NO_TRANSITION : target.toString();
    }

    // "→ q0", "* q3" o "→* q0"; los espacios mantienen alineados los nombres
    private String stateLabel(State state) {
        String initial = state.equals(dfa.getInitial_state()) ? INITIAL_MARK : " ";
        String accepting = dfa.isFinal(state) ? FINAL_MARK : " ";
        return initial + accepting + " " + state;
    }
}
