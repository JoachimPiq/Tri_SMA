package com.company;

import javax.swing.*;
import java.awt.*;

public class Fenetre extends JFrame {
    public Fenetre(Environnement env){
        this.setTitle("SMA TP2");
        this.setSize(1000,1000);
        this.setLocationRelativeTo(null);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        this.setLayout(new GridLayout(env.getTailleX(), env.getTailleY()));
        for (int i =0; i<env.getTailleY();i++)
        {
            for(int j=0;j<env.getTailleY();j++){
                this.getContentPane().add(env.getMatrice()[i][j]);
            }


        }
        this.setVisible(true);
    }
}
