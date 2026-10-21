package models.Automaton;


import java.util.Set;

public abstract class Automaton {
    //atributos
    private State initial_state;
    private Set<State> states;
    private Set<Character> alphabet;
    private Set<State> final_states;


    //constructor
    public Automaton(State initial_state, Set<State> final_states, Set<Character> alphabet, Set<State> states) {
        this.initial_state = initial_state;
        this.final_states = final_states;
        this.alphabet = alphabet;
        this.states = states;
    }

    //getter and setters

    public State getInitial_state() {
        return initial_state;
    }

    public void setInitial_state(State initial_state) {
        this.initial_state = initial_state;
    }

    public Set<State> getStates() {
        return states;
    }

    public void setStates(Set<State> states) {
        this.states = states;
    }

    public Set<Character> getAlphabet() {
        return alphabet;
    }

    public void setAlphabet(Set<Character> alphabet) {
        this.alphabet = alphabet;
    }

    public Set<State> getFinal_states() {
        return final_states;
    }

    public void setFinal_states(Set<State> final_states) {
        this.final_states = final_states;
    }


}
