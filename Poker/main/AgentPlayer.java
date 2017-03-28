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
        this.addBehaviour(new SendMessageEntrance() );
       
        
        }
    

        
   
   
    
private class SendMessageEntrance extends OneShotBehaviour{
		
		public sendMessageEntrance(){
		jogador = new Player(this.getName());
		}
		@Override 
		public void action(){
			AID receiver = new AID();
			receiver.setLocalName("Dealer");
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setOntology(Ontologias.ENTRAR);
			try {
				msg.setContentObject(this.jogador);
				msg.addReceiver(receiver);
				myAgent.send(msg);
				this.addBehaviour(new ReceiveBehaviourJogador());
			} catch (Exception e) {
				
			System.out.println(e.getMessage());
			}
			
		}
		
		
	}
    
 	/************************************************* INICIO DE UMA MAO *************************************************/ 
    private class ReceiveBehaviourJogador extends  CyclicBehaviour {
                
		
		@Override
		public void action(){
	
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			MessageTemplate mtJ = MessageTemplate.MatchOntology(Ontologias.NOVAMAO);
			MessageTemplate mtResp = MessageTemplate.and(mt, mtJ);
			ACLMessage msg = receive(mtResp);
			
			if(msg != null){
				
				try {
					table = new List<Card>();
					SequentialBehaviour seq = new SequentialBehaviour();
					 seq.addSubBehaviour(new NewCards());
					 seq.addSubBehaviour(new GetFlop());
					
                                       
                                        
                                      

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
					Card[] cards = new Card[2];
					//tira as cards do content
					jogador.setCards(cards);
                                      
                                        
                                      

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
			}else
			block();
	}

}
    
 private class GetFlop extends OneShotBehaviour{
	@Override
		public void action(){
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			MessageTemplate mtJ = MessageTemplate.MatchOntology(Ontologias.FLOP);
			MessageTemplate mtResp = MessageTemplate.and(mt, mtJ);
			ACLMessage msg = receive(mtResp);
			
			if(msg != null){
				
				try {
					
                          table =msg.getContentObject(); 
						           
                           this.addBehaviour(new PlayGame());             
                                      

				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
			}else
			block();
	}

}   
/************************************************* DECISAO *************************************************/
	private class PlayGame extends SimpleBehaviour{
		private boolean finished = false;
		int rank ,round,bet,pot;
		public PlayGame(){
			rank=0;
			round = 0;
			bet = 0;
			pot= 0;

		}
			public voi action(){
				//se o dealer falar decidir
			}

			@Override
			public boolean done() {
				
				return finished;
			}
	}    
    
    
    
	/************************************************* Metodos *************************************************/    
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