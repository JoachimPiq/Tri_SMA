package com.company;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        int tailleX = 50; //Taille de l'environnement
        int tailleY = 50;
        int nbBlocA = 200; //nb de bloc A
        int nbBlocB = 200; //nb de bloc B
        int nbBlocC = 200; // nb de bloc C
        int nbAgent = 150;//nbagent
        float kP = 0.1f; // K plus
        float kM = 0.3f; //K moins
        int npPas = 1 ; // nombre de pas des agents
        int tailleMemoire = 15;
        float erreurPerception = 0f;
        float evaporationSignal = 0.02f;
        int distanceSignal = 10;
        int timeToWaitForHelp =200;
        Environnement monEnvironnment = new Environnement(tailleX, tailleY, nbBlocA, nbBlocB, nbBlocC,evaporationSignal);
        Fenetre fenetre = new Fenetre(monEnvironnment);
        monEnvironnment.setAllColor();
        ArrayList<Agent> agents = new ArrayList<>();
        for (int i = 0; i < nbAgent; i++) {
            agents.add(new Agent(monEnvironnment, kP, kM, npPas, tailleMemoire, erreurPerception,distanceSignal,timeToWaitForHelp));
        }



        int j = 0;
        while (true)
        {
            j++;
            for (Agent agent:agents
                 ) {
                agent.action();
            }
            monEnvironnment.updateSignal();

//            monEnvironnment.setAllColor();




            if (j%1000==0){
                monEnvironnment.setAllColor();
                System.out.println("Nombre de pas : "+j);

            }
        }


    }

}
