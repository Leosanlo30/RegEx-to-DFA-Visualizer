package models.Automaton;


import java.util.Set;

public abstract class Automaton {
    //atributos
    protected State initial_state;
    protected Set<State> states;
    protected Set<Character> alphabet;
    protected Set<State> final_states;
    protected int nextId;

    //constructor
    public Automaton(State initial_state, Set<State> final_states, Set<Character> alphabet, Set<State> states) {
        this.initial_state = initial_state;
        this.final_states = final_states;
        this.alphabet = alphabet;
        this.states = states;
    }

    //getter and setters

    public State getInitial_state() {
        return this.initial_state;
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

    //metodos

    //
    public State newState(){
        State s = new State(nextId++);
        addState(s);
        return s;
    }

    public void addState(State s){
        if (s==null) System.out.println("Alerta es null (por ahora)"); //agregar error cuando se implementen lso errores
        else states.add(s);
    }

    public void markFinals(State s){
        if (states.contains(s)) final_states.add(s);
        else System.out.println("Lanza error"); //implementar cuando ya etsen los errores
    }

    public boolean isFinal(State s){
        return final_states.contains(s);
    }

    public boolean hasState(State s) {
        return states.contains(s);
    }


}
