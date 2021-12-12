package com.company;
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
        //Création d'une matrice
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

    /**
     * Mise à jour de l'interface graphique
     */
    public void setAllColor(){
        for (int i =0; i<getTailleY();i++)
        {
            for(int j=0;j<getTailleY();j++){
                matrice[i][j].setColor();
            }
        }
    }

    /**
     * Retourne la position d'une case aléatoire, sans bloc de l'environnement
     * @return Position d'une case vide de l'environnment
     */
    public Position getRandomEmptyPosition(){
        int posX,posY;
        do {
            posX = ThreadLocalRandom.current().nextInt(0, tailleX);
            posY = ThreadLocalRandom.current().nextInt(0, tailleY);
        } while (matrice[posX][posY].getBloc().getType() != 'O');
        return new Position(posX,posY);
    }

    /**
     * Place l'agent sur une case vide aléatoire de l'environnement
     * @param agent l'agent que l'on veut positioner sur l'environnement
     * @return retourne la position atribué à l'agent
     */
    public Position setPosition(Agent agent){
        Position pos = getRandomEmptyPosition();
        matrice[pos.x][pos.y].setAgent(agent);
        return pos;

    }

    /**
     * Fonction représentant la perception des agents
     * @param agent L'agent souhaitant obtenir sa perception de l'environement
     * @return la Case sur laquelle l'agent se trouve
     */
    public Case perception(Agent agent){
        Position pos = agent.getPosition();
        return matrice[pos.x][pos.y];
    }

    /**
     * Fonction permettant à un agent de ramasser le bloc sur lequel il se trouve
     * @param agent l'agent qui souhaite ramasser un bloc
     * @return le bloc qui est ramassé
     */
    public Bloc pickBLoc(Agent agent){
        Position pos = agent.getPosition();
        Bloc bloc =matrice[pos.x][pos.y].getBloc();

        matrice[pos.x][pos.y].bloc = new Bloc();

        return bloc;
    }

    /**
     * Permet de récuper la case à la position souhaité
     * @param pos la position de la case que l'on souhaite récupérer
     * @return la case à la position souhaité
     */
    public Case getCaseAtPosition(Position pos){
        if ((pos.x>= 0 && pos.x < this.getTailleX()) && (pos.y>=0 && pos.y < this.getTailleY() ))
            return matrice[pos.x][pos.y];
        else
            return null;
    }

    /**
     * Permet à un agent de déposer un bloc là où il se trouve
     * @param agent l'agent qui souhaite poser le bloc
     */
    public void putBlock(Agent agent){
        Position pos = agent.getPosition();
        matrice[pos.x][pos.y].bloc= agent.getBlocPorte();

    }


    /**
     * Permet à un agent de se déplacer à la position qu'il souhaite
     * @param agent l'agent qui se déplace
     * @param newPos la nouvelle position de l'agent
     */
    public void moveToNewPosition(Agent agent, Position newPos){
        matrice[agent.getPosition().x][agent.getPosition().y].setAgent(null);

        matrice[newPos.x][newPos.y].setAgent(agent);

    }

    /**
     * Permet à un agent d'envoyer un signal d'appel à l'aide
     * @param agent l'agent qui envoi le signal
     */
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

    /**
     * Permet de faire décroitre les signaux de l'environnement d'un pourcentage défini par evaporationSignal
     */
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

    /**
     * Permet à un agent d'obtenir la position de la case qui l'entour directement avec le signal le plus fort
     * @param agent l'agent qui souhaite obtenir la case
     * @return la position de la case avec le plus fort signal autour de l'agent
     */
    public Position positionOfCaseWithMostSignalAroundAgent(Agent agent) {
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

    /**
     * Permet à un agent d'obtenir un agent qui aurait besoin d'aide dans son entourage direct, s'il n'y en a pas =
     * retourne null.
     * @param agent l'agent effectuant la recherche
     * @return un agent qui a besoin d'aide, ou null
     */
    public Agent agentNeedingHelpAround(Agent agent) {
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
    //Getter Setter
    public Case[][] getMatrice(){
        return matrice;
    }
    public int getTailleX() {
        return tailleX;
    }

    public int getTailleY() {
        return tailleY;
    }
    public void setAgentToNull(Agent agent){
        matrice[agent.getPosition().x][agent.getPosition().y].setAgent(null);

    }

}
