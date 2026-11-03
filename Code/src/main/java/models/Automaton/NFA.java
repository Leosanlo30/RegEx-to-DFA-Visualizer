package models.Automaton;
import java.util.*;

public class NFA extends Automaton{
    //Automata Finito No determinista
    public static final char EPSILON ='ε';
    private Map<State, Map<Character, Set<State>>> delta;

    //constructor 1 vacio
    public NFA() {
        super();
        this.delta = new HashMap<>();
    }
    
    //constructor 2 
    public NFA(State initial_state, Set<State> final_states, Set<Character> alphabet, Set<State> states, Map<State, Map<Character, Set<State>>> delta) {
        super(initial_state, final_states, alphabet, states);
        if(delta!=null) this.delta = delta;
        else this.delta = new HashMap<>();
    }
    

    // getters and setters

    public Map<State, Map<Character, Set<State>>> getDelta() {
        return delta;
    }

    public void setDelta(Map<State, Map<Character, Set<State>>> delta) {
        this.delta = delta;
    }


    //metodos

    public void addTransition(State s , char c , State d){
        if (!states.contains(s) || !states.contains(d)) throw new controllers.Exceptions.AutomatonException("El estado no pertenece al NFA");// manejo de errores
        else {
            delta.computeIfAbsent(s, k -> new HashMap<>())
                    .computeIfAbsent(c, k -> new HashSet<>())
                    .add(d);
            if (c != EPSILON) alphabet.add(c);
        }
    }
    
    //destinos de s con c (o vacío si no hay)
    private Set<State> targets(State s, char c) {
        Map<Character, Set<State>> fila = delta.get(s);
        if (fila == null) return Collections.emptySet();
        return fila.getOrDefault(c, Collections.emptySet());
    }

    //Estados alcanzables con Epsilon
    public Set<State> epsilonClosure(Set<State> set) {
        Set<State> cierre = new HashSet<>(set);
        Deque<State> pila = new ArrayDeque<>(set);
        
        while (!pila.isEmpty()) {
            State q = pila.pop();
            for (State t : targets(q, EPSILON)) {
                if (cierre.add(t)) pila.push(t);
            }
        }
        return cierre;
    }

    public Set<State> move(Set<State> set, char c) {
        Set<State> res = new HashSet<>();
        for (State q : set) {
            res.addAll(targets(q, c));
        }
        return res;
    }

    @Override
    public boolean accepts(String input) {
        if (initial_state == null) return false;
        Set<State> actuales = epsilonClosure(Set.of(initial_state));
        for (char c : input.toCharArray()) {
            actuales = epsilonClosure(move(actuales, c));
            if (actuales.isEmpty()) return false;
        }
        for (State q : actuales) {
            if (isFinal(q)) return true;
        }
        return false;
}


}
