package models.Automaton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class NFA extends Automaton{
    //Automata Finito No determinista
    private char EPSILON ='ε';
    private Map<State, Map<Character, Set<State>>> delta;

    //constructor
    public NFA(State initial_state, Set<State> final_states, Set<Character> alphabet, Set<State> states, char EPSILON, Map<State, Map<Character, Set<State>>> delta) {
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

    public Map<State, Map<Character, Set<State>>> getDelta() {
        return delta;
    }

    public void setDelta(Map<State, Map<Character, Set<State>>> delta) {
        this.delta = delta;
    }


    //metodos

    public void addTransition(State s , char c , State d){
        if (!states.contains(s) || !states.contains(d)) System.out.println("Lanzar alerta de no existen");
        else {
            delta.computeIfAbsent(s, k -> new HashMap<>())
                    .computeIfAbsent(c, k -> new HashSet<>())
                    .add(d);
            if (c != EPSILON) alphabet.add(c);
        }
    }


}
