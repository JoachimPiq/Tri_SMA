package com.company;


import javax.swing.*;
import java.awt.*;

public class Case extends JPanel {
    Bloc bloc;
    Agent agent;


    void setColor(){
        switch (bloc.getType()){
            case 'A':
                this.setBackground(Color.red);
                break;
            case 'B':
                this.setBackground(Color.blue);
                break;
            default:
                this.setBackground(Color.white);
        }
        if (isAgent()) {
            this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            switch (agent.getBlocPorte().getType()){
                case 'A' :
                    this.setBackground(Color.pink);
                    break;
                case 'B':
                    this.setBackground(Color.CYAN);
                    break;
                default:
                    this.setBackground(Color.gray);
            }
        }

        else this.setBorder(null);
    }
    public Case() {
        this.bloc = new Bloc();
        this.agent = null;
    }



    public void setBlocType(char type) {
        this.bloc.setType(type);
    }

    public void setAgent(Agent agent) {
        this.agent = agent;

    }

    public Bloc getBloc() {
        return bloc;
    }

    public boolean isAgent() {
        return !(agent==null);
    }

    @Override
    public String toString() {
        if (isAgent()) return "["+agent.blocPorte.getType()+"]";
        else return " " + bloc.getType()+ " ";
    }
}
