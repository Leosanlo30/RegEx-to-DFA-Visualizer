package models.Regex;

import models.Automaton.Fragment;
import models.Automaton.NFA;
import models.Automaton.State;

public class EpsilonNode extends RegexNode{

    @Override
    public Fragment buildNFA(NFA nfa) {
        State i = nfa.newState();
        State f = nfa.newState();
        nfa.addTransition(i, NFA.EPSILON, f);
        return new Fragment(i, f);
    }
    
}
