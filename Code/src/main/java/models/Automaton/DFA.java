package models.Automaton;

import java.util.Map;
import java.util.Set;

public class DFA extends Automaton{
    //Automata Finito Determinista
    //atributos
    private Map<State,Map<State, Character>> delta; //delta es el conjunto de los estados y a quienes van ((q0,a)q1)

    //constructor
    public DFA(State initial_state, Set<State> final_states, Set<Character> alphabet, Set<State> states, Map<State, Map<State, Character>> delta) {
        super(initial_state, final_states, alphabet, states);
        this.delta = delta;
    }

    public Map<State, Map<State, Character>> getDelta() {
        return delta;
    }

    public void setDelta(Map<State, Map<State, Character>> delta) {
        this.delta = delta;
    }

    //metodos

}
