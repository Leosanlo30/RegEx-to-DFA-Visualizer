package models.Automaton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class DFA extends Automaton{
    //Automata Finito Determinista
    //atributos
    private Map<State, Map<Character, State>> delta; //delta es el conjunto de los estados y a quienes van ((q0,a)q1)

    //constructor
    public DFA(State initial_state, Set<State> final_states, Set<Character> alphabet, Set<State> states, Map<State, Map<Character, State>> delta) {
        super(initial_state, final_states, alphabet, states);
        this.delta = delta;
    }

    public Map<State, Map<Character, State>> getDelta() {
        return delta;
    }

    public void setDelta(Map<State, Map<Character, State>> delta) {
        this.delta = delta;
    }

    //metodos

    public void addTransition(State s, char c, State d) {
        if (!hasState(s) || !hasState(d)) System.out.println("El estado no pertenece al DFA");

        if (c == 'ε') System.out.println("Un DFA no admite transiciones ε");

        Map<Character, State> froms = delta.computeIfAbsent(s, k -> new HashMap<>());
        State actual = froms.get(c);
        if (actual != null && !actual.equals(d)) System.out.println("Ya existe una transición de " + s.getId() + " con '" + c + "'");
        froms.put(c, d);
        alphabet.add(c);
    }

    public State transition(State s, char c){
        Map<Character, State> fila = delta.get(s);
        if (fila == null) return null; // si es null retorna null
        return fila.get(c);
    }

    public void complete(){
        boolean faltan = false;
        for (State s : states) {
            for (char c : alphabet) {
                if (transition(s, c) == null) faltan = true;
            }
        }
        if (!faltan) return; //completo sin estado sumidero

      //estado sumidero
        State sumidero = newState();

        for (State s : states) {
            for (char c : alphabet) {
                if (transition(s, c) == null) addTransition(s, c, sumidero); //estado sumidero

            }
        }
    }



}
