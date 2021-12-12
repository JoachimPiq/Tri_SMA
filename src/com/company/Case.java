package com.company;


import javax.swing.*;
import java.awt.*;
import java.text.DecimalFormat;

public class Case extends JPanel {
    Bloc bloc;
    Agent agent;
    Float signal;


    public Case() {
        this.bloc = new Bloc();
        this.agent = null;
        this.signal = 0.0f;

    }

    /**
     * Interface graphique
     */
    void setColor(){
        switch (bloc.getType()) {
            case 'A' -> this.setBackground(Color.red);
            case 'B' -> this.setBackground(Color.blue);
            case 'C' -> this.setBackground(Color.orange);
            default -> this.setBackground(Color.white);
        }
        if (isAgent()) {

            if (agent.isWaiting()) this.setBorder(BorderFactory.createDashedBorder(null,5,5));
            else this.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
            switch (agent.getBlocPorte().getType()) {
                case 'A' -> this.setBackground(Color.pink);
                case 'B' -> this.setBackground(Color.CYAN);
                case 'C' -> this.setBackground(Color.yellow);

            }
        }

        else this.setBorder(null);
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

    public Float getSignal() {
        return signal;
    }

    public void setSignal(Float signal) {
        this.signal = signal;

    }

    public boolean isAgent() {
        return !(agent==null);
    }

    public Agent getAgent(){
        return agent;
    }
    @Override
    public String toString() {
        if (isAgent()) return "["+agent.blocPorte.getType()+"]";
        else return " " + bloc.getType()+ " ";
    }
}
