/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import cartas.IPlayer;
import cartas.Player;
import jade.core.AID;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gil
 */
public class Software extends GuiAgent{
    
    protected Inicio myGui;
    private List<IPlayer> jogadoresCriados;

    @Override
    protected void setup () {
        super.setup();
        this.jogadoresCriados=new ArrayList<>();
        this.myGui = new Inicio(this);
        myGui.setVisible(true);
        
    }
    
    
    @Override
    protected void onGuiEvent(GuiEvent ge) {
        List<Double> dinheiroUser = (List<Double>) ge.getSource();
        int i;
       
        
        for(i=0;i<dinheiroUser.size();i++) {
        
            AID receiver = new AID();
		receiver.setLocalName(Inicio.NOMEAGENTE+i);
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setOntology(Ontologias.JOGADOR);
		msg.addReceiver(receiver);
            try {
                
                Player a = new Player(Inicio.NOMEAGENTE+i);
                Argumentos arg = new Argumentos(dinheiroUser.get(i), a);
                this.jogadoresCriados.add(a);
                msg.setContentObject(arg);
            } catch (IOException ex) {
                System.err.println(ex.getMessage());
            }
		
		send(msg);
        }
        
         AID receiver = new AID();
		receiver.setLocalName("dealer");
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setOntology(Ontologias.LISTAJOGADORES);
		msg.addReceiver(receiver);
        try {
            msg.setContentObject((Serializable) this.jogadoresCriados);
        } catch (IOException ex) {
           System.err.println(ex.getMessage());
        }
        
        send(msg);
                
        
        
        
        
        
    
    
    
    
    
    }
    
    
    
    
}
