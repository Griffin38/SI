package main;


import cartas.*;
import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AgentPlayer extends Agent {

        // propriedades AgentPlayer
        private int dinheiro, myRank,round,bet,pot,toRaise;
        private Player jogador;
        private List<Card> tableCards;
    
    @Override
    protected void setup() {
    super.setup();
    
    dinheiro=5000;
    jogador = new Player(getLocalName());
    this.addBehaviour(new SendMessageEntrance() );

    }

private class SendMessageEntrance extends OneShotBehaviour{
			
			@Override 
			public void action(){
				AID receiver = new AID();
				receiver.setLocalName("Dealer");
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setOntology(Ontologias.ENTRAR);
				try {
					msg.setContentObject((Player) jogador);
					msg.addReceiver(receiver);
					myAgent.send(msg);
					
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
						tableCards = new ArrayList<>();
						round = bet= pot = toRaise = 0;
						
						SequentialBehaviour seq = new SequentialBehaviour();
						seq.addSubBehaviour(new NewCards());
						seq.addSubBehaviour(new GetFlop());
						addBehaviour(seq);
																				
																					
																				

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
						List<Card> mao =(List<Card>) msg.getContentObject();
						int i = 0;
						for(Card a : mao){
							cards[i] = a ;
							i++;
						}
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

						tableCards =(List<Card>) msg.getContentObject(); 
							
								       
								addBehaviour(new PlayGame());             
								

					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					
				}else
				block();
		}

	}   
	/************************************************* Comunicar Mao *************************************************/
		private class PlayGame extends SimpleBehaviour{
			private boolean finished = false;
			
			@Override
				public void action(){
					ACLMessage msg = receive();
					if(msg != null){

						try{

						if(msg.getOntology().equals(Ontologias.PERGUNTAR)){
					 		int quantia = (int)msg.getContentObject();
							addBehaviour(new RespondeDealer(quantia));
							round++;
						}else if(msg.getOntology().equals(Ontologias.TURN)){
							Card cturn = (Card)msg.getContentObject();
							tableCards.add(cturn);
						}else if (msg.getOntology().equals(Ontologias.RIVER)){
							Card criver = (Card)msg.getContentObject();
							tableCards.add(criver);
						}else if (msg.getOntology().equals(Ontologias.RAISE)){
							//
							//Raise
						}else if(msg.getOntology().equals(Ontologias.PERDEU)){
							if(dinheiro == 0){ 
								//add pedir para sair 
							}
							finished = true;
						}else if(msg.getOntology().equals(Ontologias.DINHEIRO)){
							int quantia = (int)msg.getContentObject();
							dinheiro += quantia;
							finished = true;
						}
					
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					
					
					}else block();
				}

				@Override
				public boolean done() {
					
					return finished;
				}

		}
		
		/*************************************************Decidir  Resposta *************************************************/
		private class RespondeDealer extends OneShotBehaviour{
			private int quantia;
			 public RespondeDealer(int q) {
					quantia = q;
					}
			
			@Override
			public void action(){
				addBehaviour(new sendMessageCall(quantia));
			}
		}
		
		/************************************************* Respostas *************************************************/			

		private class sendMessageCall extends OneShotBehaviour{
			private int quantia;
			 public sendMessageCall(int q) {
			quantia = q;
			}
				@Override 
				public void action(){
					AID receiver = new AID();
					receiver.setLocalName("Dealer");
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setOntology(Ontologias.JOGA);
					
					try {
						msg.setContentObject(quantia);
						dinheiro-=quantia;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					msg.addReceiver(receiver);
					myAgent.send(msg);
					
					
				}
					
				}


		private class sendMessageRaise extends OneShotBehaviour{
			int quantia;
			 public sendMessageRaise(int q) {
			quantia = q;
			}
				@Override 
				public void action(){
					AID receiver = new AID();
					receiver.setLocalName("Dealer");
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setOntology(Ontologias.RAISE);
					
					try {
						msg.setContentObject(quantia);
						dinheiro-=quantia;
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					msg.addReceiver(receiver);
					myAgent.send(msg);
					
					
				}
					
				}


			private class sendMessageFold extends OneShotBehaviour{
			
			
				@Override 
				public void action(){
					AID receiver = new AID();
					receiver.setLocalName("Dealer");
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setOntology(Ontologias.FOLD);
					
					msg.addReceiver(receiver);
					myAgent.send(msg);
					
					
				}
			
				}	


}