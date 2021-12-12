package com.company;

/**
 * Représente les positions prises par les agents
 */
public class Position {
    int x;
    int y;

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "(" + x +
                ", " + y +")";
    }
}
