package main;



import cartas.*;

import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AgentPlayer extends Agent {

        // propriedades AgentPlayer
        private int  myRank,folded,round,npTable;
        private Player jogador;
        private List<Card> tableCards;
        private double dinheiro,pot,toRaise,bet;
    
    @Override
    protected void setup() {
    super.setup();
    
    dinheiro=5000;
    jogador = new Player(getLocalName());

    this.addBehaviour(new SendMessageEntrance() );
    this.addBehaviour(new  ReceiveBehaviourJogador());
    

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
                                                round=0;
						  bet= pot = toRaise = 0;
						npTable = (int) msg.getContentObject();
/************************************** */
        System.out.println("nova mao: "+ npTable + " Nome: "+getLocalName());
/********************************************** */
						
						addBehaviour(new NewCards());
																				
						 											
																				

					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					
				}else
				block();
					
				}

					
			}
			
			
	private class NewCards extends SimpleBehaviour {
		private boolean finished = false;
		@Override
			public void action(){
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.AGREE);
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
						addBehaviour(new PlayGame());
						finished = true;														
																					
																				

					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					
				}else
				block();
		}
		@Override
		public boolean done() {
			
			return finished;
		}
	}
			
  
	/************************************************* Comunicar Mao *************************************************/
//to:do -matar agente?

		private class PlayGame extends SimpleBehaviour{
			private boolean finished = false;
			
			@SuppressWarnings("unchecked")
			@Override
				public void action(){
				MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
					ACLMessage msg = receive(mt);
					if(msg != null){
                                            
						try{

						if(msg.getOntology().equals(Ontologias.PERGUNTAR)){
					 		int quantia = (int)msg.getContentObject();
					 		bet = quantia;
							addBehaviour(new RespondeDealer(quantia));
							
						}else if(msg.getOntology().equals(Ontologias.FLOP)){
							tableCards =(List<Card>) msg.getContentObject();
							round++;
						}else if(msg.getOntology().equals(Ontologias.TURN)){
							Card cturn = (Card)msg.getContentObject();
							tableCards.add(cturn);
							round++;
						}else if (msg.getOntology().equals(Ontologias.RIVER)){
							Card criver = (Card)msg.getContentObject();
							tableCards.add(criver);
							round++;
						}else if (msg.getOntology().equals(Ontologias.POT)){
							pot = (int)msg.getContentObject();
						}else if (msg.getOntology().equals(Ontologias.DESISTIRAM)){
							folded = (int)msg.getContentObject();
						}else if(msg.getOntology().equals(Ontologias.LOSS)){
							if(dinheiro == 0){ 
								addBehaviour(new sendMessageOfShame());
								//matar agente?
							}
							finished = true;
						}else if(msg.getOntology().equals(Ontologias.WIN)){
							double quantia = Double.valueOf(msg.getContent());
							dinheiro += quantia;
/************************************** */
System.out.println("Ganhei:: "+quantia+" "+getLocalName());
/********************************************** */
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
		//to:do - responder confrme a ronda , o nr de agentes fold e o rank das cartas
		
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
		
		private class RespondeDecide extends OneShotBehaviour{
			@Override
			public void action(){
			switch (round) {
            case 0:  //responder Incio
                     break;
			case 1:  //responder FLop
                     break;
			case 2: //responder turn
                     break;
			case 3: //respnder river
                     break;
				}
			}


		}
		/************************************************* Respostas *************************************************/			

		private class sendMessageCall extends OneShotBehaviour{
			private double quantia;
			 public sendMessageCall(double q) {
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
						} catch (IOException e) {
							e.printStackTrace();
						}
						dinheiro-=quantia;
					
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
						} catch (IOException e) {
							e.printStackTrace();
						}
						dinheiro-=(quantia+bet);
					
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
			

			private class sendMessageOfShame extends OneShotBehaviour{
			
			
				@Override 
				public void action(){
					AID receiver = new AID();
					receiver.setLocalName("Dealer");
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setOntology(Ontologias.NAODINHEIRO);
					
					msg.addReceiver(receiver);
					myAgent.send(msg);
					
					
				}
			
				}	

}