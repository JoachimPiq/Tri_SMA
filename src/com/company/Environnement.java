package com.company;
import java.util.concurrent.ThreadLocalRandom;
import java.util.ArrayList;

public class Environnement {
    private Case[][] matrice;
    private int tailleX;

    public Case[][] getMatrice(){
        return matrice;
    }
    public int getTailleX() {
        return tailleX;
    }

    public void setTailleX(int tailleX) {
        this.tailleX = tailleX;
    }

    public int getTailleY() {
        return tailleY;
    }

    public void setTailleY(int tailleY) {
        this.tailleY = tailleY;
    }

    private int tailleY;

    public Environnement(int tailleX,int tailleY,int nbA, int nbB) {
        this.tailleX = tailleX;
        this.tailleY = tailleY;
//        Création d'une matrice
        matrice = new Case[tailleX][tailleY];
        for (int x = 0; x < tailleX; x++) {
            for (int y = 0; y < tailleY; y++) {
                matrice[x][y] = new Case();
            }
        }
//Création des différentes blocs
        for (int i = 0; i < nbA; i++) {
            Position pos = getRandomPositionVide();
           matrice[pos.x][pos.y].setBlocType('A');
        }

        for (int i = 0; i < nbB; i++) {
            Position pos = getRandomPositionVide();
            matrice[pos.x][pos.y].setBlocType('B');

        }
    }

    public void print(){
        System.out.println("-------------------------------");
        for (int x = 0; x < this.tailleX; x++){
            for (int y = 0; y< this.tailleY; y++){
                System.out.print(matrice[x][y]);

            }
            System.out.println();
        }


    }

    public void setAllColor(){
        for (int i =0; i<getTailleY();i++)
        {
            for(int j=0;j<getTailleY();j++){
                matrice[i][j].setColor();
            }
        }
    }

    public Position getRandomPositionVide(){
        int posX = -1;
        int posY = -1;
        do {
            posX = ThreadLocalRandom.current().nextInt(0, tailleX);
            posY = ThreadLocalRandom.current().nextInt(0, tailleY);
        } while (matrice[posX][posY].getBloc().getType() != 'O');
        return new Position(posX,posY);
    }


    public Position attribuerPosition(Agent agent){
        Position pos = getRandomPositionVide();
        matrice[pos.x][pos.y].setAgent(agent);
        return pos;

    }

    public Bloc perception(Agent agent){
        Position pos = agent.getPosition();
        return matrice[pos.x][pos.y].getBloc();
    }
    public Bloc pickBlocAtPosition(Position pos){
        Bloc bloc =matrice[pos.x][pos.y].getBloc();

        matrice[pos.x][pos.y].bloc = new Bloc();

        return bloc;


    }

    public void putBlock(Agent agent){
        Position pos = agent.getPosition();
        Bloc bloc = agent.getBlocPorte();
        matrice[pos.x][pos.y].bloc= bloc;

    }


    public void moveAgent(Agent agent, Position newPos){
        matrice[agent.getPosition().x][agent.getPosition().y].setAgent(null);

        matrice[newPos.x][newPos.y].setAgent(agent);

    }
}
