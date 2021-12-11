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
    Agent esclave;
    Agent maitre;

    public Agent(Environnement environnement, float kP, float kM, int iDistanceParPas, int tailleMemoire, float erreurPerception, int distanceSignal, int timeToWaitForHelp) {
        this.environnement = environnement;
        this.position = environnement.attribuerPosition(this);
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
        freqMap.put('A',0.0);
        freqMap.put('B',0.0);
        freqMap.put('C',0.0);

    }


    public void memorize(Bloc bloc){
        // Regarde le bloc actuel pour l'ajouter à la mémoire et réduit la mémoire si on dépasse les 10
        memoire += String.valueOf(bloc.getType());
        if (memoire.length()>10) memoire = memoire.substring(1,11);
    }


    public void action(){
        if (!estEsclave() ) {
            //Perception du bloc.
            Case casePerception = environnement.perception(this);
            if(isWaiting()) waitForHelp(casePerception);
            else {
                Bloc blocPerception = casePerception.getBloc();
                memorize(blocPerception);


                // Si l'agent ne porte pas de bloc
                if (blocPorte.isNull()) {
                    //Si un agent attend de l'aide autour (1 case) on devient son esclave
                    if (environnement.agentNeedingHelp(this) != null) {
                        maitre = environnement.agentNeedingHelp(this);
                        maitre.esclave = this;
                        environnement.setAgentToNull(this);
                    }
                    // Si il y a un bloc là où se trouve l'agent.
                    else if (!blocPerception.isNull()) {
                        if (blocPerception.getType() == 'C')
                            handleTypeCBloc(casePerception);
                        else pickUp(blocPerception);

                    }
                }
                //Si l'agent porte un bloc
                else if (!blocPorte.isNull()) {
                    // et que le bloc sur lequel il se trouve est null
                    if (blocPerception.isNull()) {
                        putDown();
                    }
                }
                // Si l'agent n'est pas en attente, il peut se déplacer.
                if (!isWaiting() && !estEsclave()) move(casePerception);
            }
        }



    }

    private void waitForHelp(Case casePerception){
        if (!asUnEsclave()) {
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

    private void handleTypeCBloc(Case casePerception) {

        float fequenceBloc = getFrequency(casePerception.getBloc());
        float probabilitePrise = kP / (kP + fequenceBloc);
        probabilitePrise *= probabilitePrise;
        if (rand.nextFloat() < probabilitePrise) {
            waitingSince = 0;
            environnement.sendSignal(this);
        }

    }



    private void move(Case casePerceptioon){
        //Si l'agent percoit un signal, se dirige vers la où le signal est le plus fort,
        //Il ne percoit que dans un rayon d'action de 1 et se déplace donc que de un.
        if (blocPorte == null && casePerceptioon.getSignal() !=0){
            Position newPos = environnement.positionCaseWithMostSignalAround(this);
            if (!environnement.getCaseAtPosition(newPos).isAgent()) {
                environnement.moveToNewPosition(this, newPos);
                position = newPos;
            }
            else {
                Deplacement deplacement = new Deplacement(1);
                newPos = deplacement.calculerNewPosition(position,environnement);

                environnement.moveToNewPosition(this,newPos);
                position = newPos;
            }
        }else
        //Sinon l'agent se déplace dans une direction aléatoire, d'une distance aléatoire entre 1 et TailleDeplacementMax
        {

            Deplacement deplacement = new Deplacement(iDistanceParPas);
            Position newPos = deplacement.calculerNewPosition(position,environnement);

            environnement.moveToNewPosition(this,newPos);
            position = newPos;
        }
    }

    private boolean putDown(){
        float fequenceBloc = getFrequency(blocPorte);
        float prob = fequenceBloc / (kM + blocPorte.getType());
//        prob *= prob;
        if (rand.nextFloat() < prob) {
            environnement.putBlock(this);
            blocPorte = new Bloc();
            if (asUnEsclave()){
                esclave.setPosition(position);
                esclave.maitre = null;
                esclave = null;
            }
            return true;
        }
        return false;
    }

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

    public Bloc getBlocPorte() {
        return blocPorte;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position){
        this.position = position;
    }

    public boolean estEsclave() {
        return maitre != null;
    }

    public boolean asUnEsclave(){
        return esclave != null;
    }
    public boolean isWaiting() {
        return waitingSince!=-1;
    }


}
