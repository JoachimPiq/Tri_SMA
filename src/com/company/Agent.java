package com.company;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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

    public Agent(Environnement environnement, float kP, float kM, int iDistanceParPas, int tailleMemoire, float erreurPerception) {
        this.environnement = environnement;
        this.position = environnement.attribuerPosition(this);
        this.kP = kP;
        this.kM = kM;
        this.iDistanceParPas = iDistanceParPas;
        this.tailleMemoire = tailleMemoire;
        this.memoire = "";
        this.erreurPerception = erreurPerception;
        this.blocPorte = new Bloc();
        this.freqMap = new HashMap<Character, Double>();
        freqMap.put('A',0.0);
        freqMap.put('B',0.0);

    }


    public void memorize(Bloc bloc){
        // Regarde le bloc actuel pour l'ajouter à la mémoire et réduit la mémoire si on dépasse les 10
        memoire += String.valueOf(bloc.getType());
        if (memoire.length()>10) memoire = memoire.substring(1,11);
    }


    public void action(){
        //Perception du bloc.
        Bloc blocPerception = environnement.perception(this);
        memorize(blocPerception);

        // Prendre un bloc par terre
        // Si l'agent ne porte pas de bloc
        if (blocPorte.isNull()){
            // Si il y a un bloc là où se trouve l'agent.
            if (!blocPerception.isNull()) {
                pickUp(blocPerception);

            }
        }
        //Si l'agent porte un bloc
        else if (!blocPorte.isNull()) {
            // et que le bloc sur lequel il se trouve est null
            if (blocPerception.isNull()) {
                putDown();
            }
        }

        Deplacement deplacement = new Deplacement(iDistanceParPas);
        Position newPos = deplacement.calculerNewPosition(position,environnement);

        environnement.moveAgent(this,newPos);
        position = newPos;

    }

    private boolean putDown(){
        float fequenceBloc = getFrequency(blocPorte);
        float prob = fequenceBloc / (kM + blocPorte.getType());
        prob *= prob;
        if (rand.nextFloat() < prob) {
            environnement.putBlock(this);
            blocPorte = new Bloc();
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
            blocPorte = environnement.pickBlocAtPosition(position);
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

}
