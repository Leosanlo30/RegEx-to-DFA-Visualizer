package models.Automaton;

public class Fragment{
    //atributos
    private State start;
    private State end;

    public Fragment(State start, State end) {
        if(start==null || end==null){
            System.out.println("Illegal Arg"); // implementar cuando
        }
        this.start = start;
        this.end = end;
    }
    
    //getters ands setters

    public State getStart() {
        return start;
    }

    public State getEnd() {
        return end;
    }
    
    
    
    
}
