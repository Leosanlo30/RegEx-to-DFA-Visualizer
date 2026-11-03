package models.Regex;

import models.Automaton.Fragment;
import models.Automaton.NFA;
import models.Automaton.State;

public class UnionNode extends RegexNode{
    //atributos
    private RegexNode left;
    private RegexNode right;

    public UnionNode(RegexNode left, RegexNode right) {
        if (left==null || right==null) System.out.println("Illegal error"); //implementar
        this.left = left;
        this.right = right;
    }

    @Override
    public Fragment buildNFA(NFA nfa) {
       // a | b
       Fragment a = left.buildNFA(nfa);
       Fragment b = right.buildNFA(nfa);
       State i = nfa.newState();
       State f = nfa.newState();
       nfa.addTransition(i, NFA.EPSILON, a.getStart()); 
       nfa.addTransition(i, NFA.EPSILON, b.getStart());
       nfa.addTransition(a.getEnd(), NFA.EPSILON, f);
       nfa.addTransition(b.getEnd(), NFA.EPSILON, f);
       return new Fragment(i, f); 
    }

    
}
