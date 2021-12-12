package com.company;

public class Bloc {
    char type;


    public Bloc(){
        type = 'O';
    }

    public char getType(){
        return type;
    }

    public void setType(char type) {
        this.type = type;
    }

    public boolean isNull(){
        if (type=='O') return true;
        else return false;
    }

    @Override
    public String toString() {
        return String.valueOf(type);
    }
}
