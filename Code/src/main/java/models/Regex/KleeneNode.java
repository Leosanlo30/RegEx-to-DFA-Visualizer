package models.Regex;

import models.Automaton.Fragment;
import models.Automaton.NFA;
import models.Automaton.State;

public class KleeneNode extends RegexNode{
    //atributos
    private final RegexNode child;

    //construcor **implementar

    public KleeneNode(RegexNode child) {
        if(child==null) throw new controllers.Exceptions.AutomatonException("El hijo de KleeneNode no puede ser null");
        this.child = child;
    }

    public RegexNode getChild() {
        return child;
    }

    @Override
    public Fragment buildNFA(NFA nfa) {
        Fragment a = child.buildNFA(nfa);
        State i = nfa.newState();
        State f = nfa.newState();
        nfa.addTransition(i, NFA.EPSILON, a.getStart());
        nfa.addTransition(i, NFA.EPSILON, f);
        nfa.addTransition(a.getEnd(), NFA.EPSILON, a.getStart());
        nfa.addTransition(a.getEnd(), NFA.EPSILON, f);
        return new Fragment(i, f);
    }
    
    
    
    
}
