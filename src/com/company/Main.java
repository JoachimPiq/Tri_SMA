package com.company;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int tailleX = 50; //Taille de l'environnement
        int tailleY = 50;
        int nbBlocA = 200; //nb de bloc A
        int nbBlocB = 200; //nb de bloc B
        int nbAgent = 50;//nbagent
        float kP = 0.1f; // K plus
        float kM = 0.3f; //K moins
        int npPas = 1 ; // nombre de pas des agents
        int tailleMemoire = 10;
        float erreurPerception = 0.0f;

        Environnement monEnvironnment = new Environnement(tailleX, tailleY, nbBlocA, nbBlocB);
        Fenetre fenetre = new Fenetre(monEnvironnment);
        monEnvironnment.setAllColor();
        ArrayList<Agent> agents = new ArrayList<Agent>();
        for (int i = 0; i < nbAgent; i++) {
            agents.add(new Agent(monEnvironnment, kP, kM, npPas, tailleMemoire, erreurPerception));

        }
        monEnvironnment.print();


        int j = 0;
        while (true)
        {
            j++;
            for (Agent agent:agents
                 ) {
                agent.action();
            }
//            monEnvironnment.setAllColor();
//            Thread.sleep(1000);



            if (j%10000==0){
                monEnvironnment.setAllColor();

            }
        }


    }

}
