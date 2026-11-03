package models.Regex;

import models.Automaton.Fragment;
import models.Automaton.NFA;
import models.Automaton.State;

public class SymbolNode extends RegexNode{
    private final char symbol;

    public SymbolNode(char symbol) {
        if(!Character.isLetterOrDigit(symbol)){
            System.out.println("Simbolo invalido: "+ symbol); // implementar cuando ya este
        }
        this.symbol = symbol;
    }

    public char getSymbol() {
        return symbol;
    }

    @Override
    public Fragment buildNFA(NFA nfa) {
        State i = nfa.newState();
        State f = nfa.newState();
        nfa.addTransition(i, symbol, f); // transition: i - 'A' - > f
        return new Fragment(i,f);
    }

        
    
}
