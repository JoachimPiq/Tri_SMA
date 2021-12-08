package com.company;

public class Position {
    int x;
    int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }
    public Position(Environnement environnement){

    }

    @Override
    public String toString() {
        return "(" + x +
                ", " + y +")";
    }
}
