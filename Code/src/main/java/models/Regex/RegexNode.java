package models.Regex;
import models.Automaton.NFA;
import models.Automaton.Fragment;

public abstract class RegexNode {
    //Nodo de Expresion regular
    public abstract Fragment buildNFA(NFA nfa);
    
}
