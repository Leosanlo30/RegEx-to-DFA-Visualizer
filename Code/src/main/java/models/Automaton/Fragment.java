package models.Automaton;

public class Fragment{
    //atributos
    private State start;
    private State end;

    public Fragment(State start, State end) {
        //manejo de errores
        if(start==null || end==null) throw new controllers.Exceptions.AutomatonException("start y end del Fragment no pueden ser null");
        
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
