package com.company;

import java.util.*;

public class Agent {
    private static final Random rand = new Random();
    Environnement environnement;
    float kP;
    float kM;
    int iDistanceParPas;
    int tailleMemoire;
    String memoire;
    float erreurPerception;
    Bloc blocPorte;
    Position position;
    Map<Character, Double> freqMap;

    int distanceSignal;
    int timeToWaitForHelp;
    int waitingSince;
    Agent slave;
    Agent master;

    boolean hasMove;
    public Agent(Environnement environnement, float kP, float kM, int iDistanceParPas, int tailleMemoire, float erreurPerception, int distanceSignal, int timeToWaitForHelp) {
        this.environnement = environnement;
        this.position = environnement.setPosition(this);
        this.kP = kP;
        this.kM = kM;
        this.iDistanceParPas = iDistanceParPas;
        this.tailleMemoire = tailleMemoire;
        this.memoire = "";
        this.erreurPerception = erreurPerception;
        this.blocPorte = new Bloc();
        this.freqMap = new HashMap<>();
        this.distanceSignal = distanceSignal;
        this.waitingSince = -1;
        this.timeToWaitForHelp = timeToWaitForHelp;
        this.hasMove = false;
        freqMap.put('A',0.0);
        freqMap.put('B',0.0);
        freqMap.put('C',0.0);

    }

    /**
     * permet à l'agent de mémoriser le bloc de la case sur laquelle il se trouve
     * @param bloc le bloc qu'il mémorise
     */
    public void memorize(Bloc bloc){

        if(hasMove) {
        // Ajoute le bloc à la mémoire, et suprimme le plus vieux bloc pour arriver à une mémoire
            memoire += String.valueOf(bloc.getType());
            if (memoire.length() > tailleMemoire) memoire = memoire.substring(1, 11);
        }
    }

    /**
     * Effectue un tour d'action de l'agent.
     */
    public void action(){
        if (!isSlave() ) {
            //Perception du bloc.
            Case casePerception = environnement.perception(this);

            if(isWaiting()) waitForHelp(casePerception);
            else {
                Bloc blocPerception = casePerception.getBloc();
                memorize(blocPerception);


                // Si l'agent ne porte pas de bloc
                if (blocPorte.isNull()) {
                    //Si un agent attend de l'aide autour (1 case) on devient son esclave
                    if (environnement.agentNeedingHelpAround(this) != null) {
                        master = environnement.agentNeedingHelpAround(this);
                        master.slave = this;
                        environnement.setAgentToNull(this);
                    }
                    // S'il y a un bloc là où se trouve l'agent il tente de le ramasser.
                    else if (!blocPerception.isNull()) {
                        if (blocPerception.getType() == 'C')
                            pickUpTypeC(casePerception);
                        else pickUp(blocPerception);

                    }
                }
                //Si l'agent porte un bloc
                else if (!blocPorte.isNull()) {
                    // et que le bloc sur lequel il se trouve est null il tente de poser son bloc
                    if (blocPerception.isNull()) {
                        putDown();
                    }
                }
                // Si l'agent n'est pas en attente, il peut se déplacer.
                if (!isWaiting() && !isSlave()) move(casePerception);
            }
        }



    }

    /**
     * Comportement des agents en attente d'aide pour un bloc de type C
     * @param casePerception la case sur laquelle l'agent se trouve
     */
    private void waitForHelp(Case casePerception){
        //Si l'agent n'as pas encore reçu d'aide, il continue d'attendre.
        if (!asASlave()) {
            waitingSince++;
            if (casePerception.getSignal() == 0) {
                if (waitingSince > timeToWaitForHelp) {
                    // Si le temps d'attente est dépassé et qu'il n'y a plus de signal sur la case, l'agent bouge.
                    waitingSince = -1;
                } else {
                    //Si le temps d'attente n'est pas écoulé l'agent renvoi un signal
                    environnement.sendSignal(this);
                }
            }
        } else {
            //Si on a un esclave, on ramasse le bloc.
            blocPorte = environnement.pickBLoc(this);
            waitingSince =-1;
        }
    }

    /**
     * Comportement des agents qui tente de ramasser un bloc de type c
     * @param casePerception la case sur laque il se trouve
     */
    private void pickUpTypeC(Case casePerception) {
        //On teste la probabilité
        float fequenceBloc = getFrequency(casePerception.getBloc());
        float probabilitePrise = kP / (kP + fequenceBloc);
        probabilitePrise *= probabilitePrise;
        if (rand.nextFloat() < probabilitePrise) {
            //Si la probabilité est validée, l'agent se met en attente.
            waitingSince = 0;
            environnement.sendSignal(this);
        }

    }


    /**
     * Permet à l'agent de se déplacer
     * @param casePerception la case sur laque il se trouve
     */
    private void move(Case casePerception){
        hasMove = false;
        //Si l'agent perçoit un signal, se dirige vers là où le signal est le plus fort
        //Il ne percoit que dans un rayon de 1 et se déplace donc que de 1 case
        if (blocPorte == null && casePerception.getSignal() !=0){
            Position newPos = environnement.positionOfCaseWithMostSignalAroundAgent(this);
            if (!environnement.getCaseAtPosition(newPos).isAgent()) {
                environnement.moveToNewPosition(this, newPos);
                position = newPos;
                hasMove = true;
            }
            else {
                //Si il y a un agent sur la case avec le signal le plus fort, l'agent se déplace d'une case de manière
                //aléatoire, afin d'éviter les blocages.
                Deplacement deplacement = new Deplacement(1);
                newPos = deplacement.calculerNewPosition(position,environnement);

                if (newPos!=position) hasMove = true;

                environnement.moveToNewPosition(this,newPos);
                position = newPos;
            }
        }else
        //Sinon l'agent se déplace dans une direction aléatoire, d'une distance aléatoire entre 1 et TailleDeplacementMax
        {

            Deplacement deplacement = new Deplacement(iDistanceParPas);
            Position newPos = deplacement.calculerNewPosition(position,environnement);
            if (newPos != position) hasMove = true;
            environnement.moveToNewPosition(this,newPos);
            position = newPos;
        }
    }

    /**
     * Determine si il doit poser le bloc en fonction de la probabilité
     * @return true si il pose le bloc, false sinon.
     */
    private boolean putDown(){
        float fequenceBloc = getFrequency(blocPorte);
        float prob = fequenceBloc / (kM + blocPorte.getType());
//        prob *= prob; Rend la simulation beaucoup plus rapide et pas moins performante.
        if (rand.nextFloat() < prob) {
            environnement.putBlock(this);
            blocPorte = new Bloc();
            // Si l'agent qui pose avait un esclave, ce dernier est relaché de ses obligations et reprend un comportement
            // normal depuis cette position.
            if (asASlave()){
                slave.setPosition(position);
                slave.master = null;
                slave = null;
            }
            return true;
        }
        return false;
    }

    /**
     * Détermine si l'agent doit prendre le bloc en fonction de la probabilité
     * @param blocPerception le bloc sur lequel il se trouve
     * @return true si il prend le bloc, false sinon
     */
    private boolean pickUp(Bloc blocPerception){
        float fequenceBloc = getFrequency(blocPerception);
        float probabilitePrise = kP / (kP + fequenceBloc);
        probabilitePrise *= probabilitePrise;
        if (rand.nextFloat() < probabilitePrise) {
            //Prendre le bloc à la position souhaité
            blocPorte = environnement.pickBLoc(this);
            return true;
        }
        return false;

    }

    /**
     * Retourne la fréquence du bloc dans la mémoire
     * @param bloc le bloc dont on veut la fréquence
     * @return la fréquence du bloc
     */
    public float getFrequency(Bloc bloc)
    {
        float count = 0;
        if(!bloc.isNull()){
            for(char c : memoire.toCharArray()) {
                if (c != 'O' && bloc.getType() == c)
                    count++;
            }
        }
        return count/tailleMemoire;
    }

    /**
     *
     * @return true si l'agent à un maitre
     */
    public boolean isSlave() {
        return master != null;
    }

    /**
     *
     * @return true si l'agent à un esclave
     */
    public boolean asASlave(){
        return slave != null;
    }

    /**
     *
     * @return true si l'agent attend de l'aide.
     */
    public boolean isWaiting() {
        return waitingSince!=-1;
    }

    //getter setter.
    public Bloc getBlocPorte() {
        return blocPorte;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position){
        this.position = position;
    }



}
