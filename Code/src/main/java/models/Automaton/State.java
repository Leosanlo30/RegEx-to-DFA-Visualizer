package models.Automaton;

public class State {
    private int id;

    //constructor
    public State(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    //equals
    public boolean equals(Object o){
       if (this == o) return true;
       else if (o == null || getClass() != o.getClass()) return false;
       State otro = (State)o;
       return id == otro.id;
    }

    public int hashCode(){
        return Integer.hashCode(id);
    }



}
