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
        int tailleMemoire = 15; // taille de la mémoire des agents
        float erreurPerception = 0f; // Erreur de perception des agents 0.1 : 10% de chance de confondre les blocs.
        float evaporationSignal = 0.02f; // Evaporation du signal par tour
        int distanceSignal = 10; //Distance de propagation des signaux d'aide
        int timeToWaitForHelp =200; //Temps d'attente des agents pour de l'aide quand il trouve un bloc C

        //Génération de l'environnemnt
        Environnement monEnvironnment = new Environnement(tailleX, tailleY, nbBlocA, nbBlocB, nbBlocC,evaporationSignal);

        //Interface graphique
        Fenetre fenetre = new Fenetre(monEnvironnment);
        monEnvironnment.setAllColor();

        //Création des agents
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



            if (j%1000==0){
                monEnvironnment.setAllColor();
                System.out.println("Nombre d'itérations : "+j);

            }
        }


    }

}
