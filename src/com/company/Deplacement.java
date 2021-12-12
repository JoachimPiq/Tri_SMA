package com.company;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Représente les déplacements des agents.
 */
public class Deplacement {
    int direction;
    int distance;

    /**
     * Génère un déplacement dans une direction aléatoire et d'une distance entre 1 et distance.
     * @param distance la distance max
     */
    public Deplacement(int distance) {
        this.direction = ThreadLocalRandom.current().nextInt(0, 8);
        Random rand = new Random();
        this.distance = 1 +rand.nextInt(distance);
    }

    /**
     * Retourne la nouvelle position d'un agent en fonciton de son déplacement
     * @param oldPos l'ancienne position d'un agent
     * @param environnement l'environnement dans lequel il evolu
     * @return la nouvelle position
     */
    public Position calculerNewPosition(Position oldPos, Environnement environnement) {
        Position pos = new Position(oldPos.x,oldPos.y);
        switch (direction) {
            case 0:
                pos.x += distance;
                break;
            case 1:
                pos.x += distance;
                pos.y += distance;
                break;
            case 2:
                pos.y += distance;
                break;
            case 3:
                pos.x -= distance;
                pos.y += distance;
                break;
            case 4:
                pos.x -= distance;
                break;
            case 5:
                pos.x -= distance;
                pos.y -= distance;
                break;
            case 6:
                pos.y -= distance;
                break;
            case 7:
                pos.x += distance;
                pos.y -= distance;
        }
        if (pos.x< 0) pos.x=0;
        else if (pos.x>environnement.getTailleX()-1) pos.x = environnement.getTailleX()-1;

        if(pos.y<0) pos.y = 0;
        else if (pos.y>environnement.getTailleY()-1) pos.y = environnement.getTailleY()-1;

        if (environnement.getMatrice()[pos.x][pos.y].isAgent())
            return oldPos;
        return pos;
    }
}
