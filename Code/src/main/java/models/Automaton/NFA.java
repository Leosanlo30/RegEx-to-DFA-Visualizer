package models.Automaton;

import java.util.Map;
import java.util.Set;

public class NFA extends Automaton{
    //Automata Finito No determinista
    private char EPSILON ='ε';
    private Map<State, Map<State, Character>> delta;

    //constructor
    public NFA(State initial_state, Set<State> final_states, Set<Character> alphabet, Set<State> states, char EPSILON, Map<State, Map<State, Character>> delta) {
        super(initial_state, final_states, alphabet, states);
        this.EPSILON = EPSILON;
        this.delta = delta;
    }

    // getters and setters


    public char getEPSILON() {
        return EPSILON;
    }

    public void setEPSILON(char EPSILON) {
        this.EPSILON = EPSILON;
    }

    public Map<State, Map<State, Character>> getDelta() {
        return delta;
    }

    public void setDelta(Map<State, Map<State, Character>> delta) {
        this.delta = delta;
    }


    //metodos
}
