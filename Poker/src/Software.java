/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package src;

import jade.core.AID;
import jade.gui.GuiAgent;
import jade.gui.GuiEvent;
import jade.lang.acl.ACLMessage;
import java.util.List;

/**
 *
 * @author gil
 */
public class Software extends GuiAgent{
    
    protected Inicio myGui;

    @Override
    protected void setup () {
        super.setup();
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
		msg.setOntology("DINHEIRO");
		msg.addReceiver(receiver);
                
		 String conteudo =String.valueOf(dinheiroUser.get(i));
		
                 msg.setContent(conteudo);
		
		send(msg);
        
        }
    
    
    
    
    
    }
    
    
    
    
}
