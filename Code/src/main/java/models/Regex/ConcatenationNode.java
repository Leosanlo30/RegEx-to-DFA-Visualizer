package models.Regex;

import models.Automaton.Fragment;
import models.Automaton.NFA;

public class ConcatenationNode extends RegexNode{
    //atributos
    private final RegexNode left;
    private final RegexNode right;

    public ConcatenationNode(RegexNode left, RegexNode right) {
        if (left==null || right==null) throw new controllers.Exceptions.AutomatonException("Los hijos de ConcatenationNode no pueden ser null");
        this.left = left;
        this.right = right;
    }

    public RegexNode getLeft() {
        return left;
    }

    public RegexNode getRight() {
        return right;
    }

    
    @Override
    public Fragment buildNFA(NFA nfa) {
        Fragment a = left.buildNFA(nfa);
        Fragment b = right.buildNFA(nfa);
        nfa.addTransition(a.getEnd(), NFA.EPSILON, b.getStart()); // a -> epsilon -> b
        return new Fragment(a.getStart(), b.getEnd());
    }
    
    

    

}
