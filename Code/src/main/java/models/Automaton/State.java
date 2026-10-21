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
        return true; //por ahora
    }

    public int hashCode(){
        return 0; //por ahora
    }



}
