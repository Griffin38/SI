package main;


import cartas.Card;
import cartas.IPlayer;
import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.ArrayList;
import java.util.List;


public class AgentPlayer  extends Agent{
        private double dinheiro;
        private int nrAgentesFold;
        private IPlayer jogador;
       private List<Card> table;
    
        
        @Override
    protected void setup() {
        super.setup();
        this.nrAgentesFold=0;
        this.dinheiro=0;
        this.addBehaviour(new ReceiveBehaviourJogador() );
       
        
        }
    

        
   
    /*
    Recebe mensagens dos agentes que vão desistindo da partida
    */
    
private class sendMessageEntrance extends OneShotBehaviour{
		IPlayer player;
		public sendMessageEntrance(){
		player = new Player(this.getName());
		}
		@Override 
		public void action(){
			AID receiver = new AID();
			receiver.setLocalName("Dealer");
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setOntology(Ontologias.ENTRAR);
			try {
				msg.setContentObject(this.player);
				msg.addReceiver(receiver);
				myAgent.send(msg);
			} catch (Exception e) {
				// Nao deu
			System.out.println(e.getMessage());
			}
			
		}
		
		
	}
    
  
    private class ReceiveBehaviourJogador extends  CyclicBehaviour {
                
		
		@Override
		public void action(){
	
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			MessageTemplate mtJ = MessageTemplate.MatchOntology(Ontologias.NOVAMAO);
			MessageTemplate mtResp = MessageTemplate.and(mt, mtJ);
			ACLMessage msg = receive(mtResp);
			
			if(msg != null){
				
				try {
					//reset table e cards
					//add NewCards getFlop
                                       
                                        
                                      

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
			}else
			block();
				
			}

        
		}
    
    
private class NewCards extends OneShotBehaviour{
	@Override
		public void action(){
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			MessageTemplate mtJ = MessageTemplate.MatchOntology(Ontologias.CARTAS);
			MessageTemplate mtResp = MessageTemplate.and(mt, mtJ);
			ACLMessage msg = receive(mtResp);
			
			if(msg != null){
				
				try {
					
                       //add Cards                
                                        
                                      

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
			}else
			block();
	}

}
    
 private class getFlop extends OneShotBehaviour{
	@Override
		public void action(){
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			MessageTemplate mtJ = MessageTemplate.MatchOntology(Ontologias.FLOP);
			MessageTemplate mtResp = MessageTemplate.and(mt, mtJ);
			ACLMessage msg = receive(mtResp);
			
			if(msg != null){
				
				try {
					
                          //add table   
						  //add Answer          
                                        
                                      

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
			}else
			block();
	}

}   
    
    
    
    
    
    
    public double getDinheiro() {
        return dinheiro;
    }

    public void setDinheiro(double dinheiro) {
        this.dinheiro = dinheiro;
    }
	
    
    public void retirarDinheiro(double quantidade) {
    
        this.dinheiro=this.dinheiro-quantidade;
    }
    
    
    public void aumentaDinheiro(double quantidade) {
    
        this.dinheiro=this.dinheiro+quantidade;
        
    }
    
    
    
    
   
    
    
    
    

}