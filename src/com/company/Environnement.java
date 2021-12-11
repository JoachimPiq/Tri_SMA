package com.company;
import javax.swing.plaf.synth.SynthTextAreaUI;
import java.util.concurrent.ThreadLocalRandom;

public class Environnement {
    private final Case[][] matrice;
    private final int tailleX;
    private final int tailleY;
    private final float evaportaionSignal;

    public Environnement(int tailleX,int tailleY,int nbA, int nbB, int nbC, float evaportaionSignal) {
        this.tailleX = tailleX;
        this.tailleY = tailleY;
        this.evaportaionSignal = evaportaionSignal;
//        Création d'une matrice
        matrice = new Case[tailleX][tailleY];
        for (int x = 0; x < tailleX; x++) {
            for (int y = 0; y < tailleY; y++) {
                matrice[x][y] = new Case();
            }
        }
//Création des différentes blocs
        for (int i = 0; i < nbA; i++) {
            Position pos = getRandomEmptyPosition();
           matrice[pos.x][pos.y].setBlocType('A');
        }

        for (int i = 0; i < nbB; i++) {
            Position pos = getRandomEmptyPosition();
            matrice[pos.x][pos.y].setBlocType('B');

        }
        for (int i = 0; i < nbC; i++) {
            Position pos = getRandomEmptyPosition();
            matrice[pos.x][pos.y].setBlocType('C');

        }
    }


    public Case[][] getMatrice(){
        return matrice;
    }
    public int getTailleX() {
        return tailleX;
    }


    public int getTailleY() {
        return tailleY;
    }


    public void setAllColor(){
        for (int i =0; i<getTailleY();i++)
        {
            for(int j=0;j<getTailleY();j++){
                matrice[i][j].setColor();
            }
        }
    }

    public Position getRandomEmptyPosition(){
        int posX,posY;
        do {
            posX = ThreadLocalRandom.current().nextInt(0, tailleX);
            posY = ThreadLocalRandom.current().nextInt(0, tailleY);
        } while (matrice[posX][posY].getBloc().getType() != 'O');
        return new Position(posX,posY);
    }


    public Position attribuerPosition(Agent agent){
        Position pos = getRandomEmptyPosition();
        matrice[pos.x][pos.y].setAgent(agent);
        return pos;

    }

    public Case perception(Agent agent){
        Position pos = agent.getPosition();
        return matrice[pos.x][pos.y];
    }

    public Bloc pickBlocAtPosition(Position pos){
        Bloc bloc =matrice[pos.x][pos.y].getBloc();

        matrice[pos.x][pos.y].bloc = new Bloc();

        return bloc;
    }

    public Case getCaseAtPosition(Position pos){
        if ((pos.x>= 0 && pos.x < this.getTailleX()) && (pos.y>=0 && pos.y < this.getTailleY() ))
            return matrice[pos.x][pos.y];
        else
            return null;
    }

    public void putBlock(Agent agent){
        Position pos = agent.getPosition();
        matrice[pos.x][pos.y].bloc= agent.getBlocPorte();

    }

    public void setAgentToNull(Agent agent){
        matrice[agent.getPosition().x][agent.getPosition().y].setAgent(null);

    }


    public void moveAgent(Agent agent, Position newPos){
        matrice[agent.getPosition().x][agent.getPosition().y].setAgent(null);

        matrice[newPos.x][newPos.y].setAgent(agent);

    }


    public void sendSignal(Agent agent){
        float signalOriginal = 1f;
        float signal = signalOriginal;

        Position agentPos = agent.getPosition();
        getCaseAtPosition(agentPos).setSignal(signal);
        for (int distance = 0; distance < agent.distanceSignal; distance ++){
            signal = signalOriginal - (signalOriginal/agent.distanceSignal)*distance;
            for (int i = -1; i<= 1; i++){
                for (int j = -1 ; j <=1; j++){
                    if (!(i==0 && j ==0)) {
                        Position pos = new Position(i * distance + agentPos.x, j * distance + agentPos.y);
                        if (getCaseAtPosition(pos) != null)
                            getCaseAtPosition(pos).setSignal(signal);
                    }
                }
            }
        }
    }

    public void updateSignal(){
        for(int i = 0 ;i<getTailleX();i++){
            for(int j = 0; j< getTailleY(); j++){
                if (matrice[i][j].getSignal()!=0) {
                    matrice[i][j].setSignal(matrice[i][j].getSignal() - matrice[i][j].getSignal() * evaportaionSignal);

                    if (matrice[i][j].getSignal() < .1)
                        matrice[i][j].setSignal(0f);
                }
            }
        }
    }


    public Position positionCaseWithMostSignalAround(Agent agent) {
        float maxSignal = -1;
        Position res = new Position(-1,-1);

        for (int x = -1; x <= 1;x++) {
            for (int y = -1; y <= 1; y++) {
                if (!(x==0 && y ==0)) {
                    Position potPos = new Position(agent.getPosition().x + x, agent.getPosition().y + y);
                    if (getCaseAtPosition(potPos) != null && getCaseAtPosition(potPos).getSignal() > maxSignal) {
                        maxSignal = getCaseAtPosition(potPos).getSignal();
                        res = potPos;
                    }
                }
            }
        }

        return res;
    }

    public Agent agentNeedingHelp(Agent agent) {
        for (int x = -1; x <= 1;x++) {
            for (int y = -1; y <= 1; y++) {
                Position potPos = new Position(agent.getPosition().x + x, agent.getPosition().y + y);
                if (getCaseAtPosition(potPos) != null && getCaseAtPosition(potPos).isAgent() && getCaseAtPosition(potPos).getAgent().isWaiting()) {
                    return getCaseAtPosition(potPos).getAgent();
                }
            }
        }
        return null;
    }
}
