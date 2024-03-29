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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AgentPlayer extends Agent {

        // propriedades AgentPlayer
        private int  myRank,round,npTable,hasRaised;
        private Player jogador;
        private List<Card> tableCards;
        private double dinheiro,pot,toRaise,bet,folded;
        private boolean estaEmJogo;
        private double dinheiroJogado;
    
    @Override
    protected void setup() {
    super.setup();
    
    //dinheiro=220;
    jogador = new Player(getLocalName());
    this.estaEmJogo=false;
    this.addBehaviour(new SendMessageEntrance() );
    this.addBehaviour(new  ReceiveBehaviourJogador());
    this.addBehaviour(new ReceiveBehaviour());
    

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
                                                estaEmJogo=false;
						tableCards = new ArrayList<>();
                                                round=0;
						  bet= pot = toRaise = 0;
                                                  dinheiroJogado=0;
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
						hasRaised = 0;
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
					 		double quantia = (double)msg.getContentObject();
      
					 		bet = quantia;
							addBehaviour(new RespondeDecide(quantia));
							
						}else if(msg.getOntology().equals(Ontologias.FLOP)){
							hasRaised = 0;
							tableCards =(List<Card>) msg.getContentObject();
							round++;
						}else if(msg.getOntology().equals(Ontologias.TURN)){
							round++;
							hasRaised = 0;
							Card cturn = (Card)msg.getContentObject();
							tableCards.add(cturn);
							
						}else if (msg.getOntology().equals(Ontologias.RIVER)){
							hasRaised = 0;
							round++;
							Card criver = (Card)msg.getContentObject();
							tableCards.add(criver);
							toRaise =1 ;
						}else if (msg.getOntology().equals(Ontologias.POT)){
							pot = (double)msg.getContentObject();
						}else if (msg.getOntology().equals(Ontologias.DESISTIRAM)){
							folded = (double)msg.getContentObject();
						}else if(msg.getOntology().equals(Ontologias.LOSS)){
							if(dinheiro <= 0){ 
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
		
                    private double quantia;
                
                    public RespondeDecide(double q) {
					quantia = q;
					}	
                    
                    @Override
			public void action(){
             
            addBehaviour(new sendMessageDesistiram());
            addBehaviour(new sendMessagePot());
                    	
            int i = 0;  
      /*
     HIGH_CARD,
	ONE_PAIR,
	TWO_PAIR,
	THREE_OF_A_KIND,
	STRAIGHT,
	FLUSH,
	FULL_HOUSE,
	FOUR_OF_A_KIND,
	STRAIGHT_FLUSH,
	ROYAL_FLUSH
	
	  private int  myRank,folded,round,npTable;
        private Player jogador;
        private List<Card> tableCards;
        private double dinheiro,pot,toRaise,bet;
        private boolean estaEmJogo;
        private double dinheiroJogado;
	**/   
			switch (round) {
            case 0:  
                 RankingUtil.checkRanking(jogador, tableCards);
                  myRank = RankingUtil.getRankingToInt(jogador);
                  
                  switch(myRank){
              	case 0: 
              			Card highCard = RankingUtil.getHighCard(jogador,
              			Collections.EMPTY_LIST);
              			int cRank = highCard.getRank().ordinal();
              			if(cRank >= 8) addBehaviour(new sendMessageCall(quantia));
              			else if(folded > (npTable -1))addBehaviour(new sendMessageFold());
              			else addBehaviour(new sendMessageCall(quantia));
              			break;
              	case 1: 
              			addBehaviour(new sendMessageCall(quantia));
              			Card highCardPair = RankingUtil.getHighCard(jogador,
              			Collections.EMPTY_LIST);
              			int cRankPair = highCardPair.getRank().ordinal();
              			if(cRankPair >= 7) toRaise = 1;	
              			break;			

              			}
                     break;
                     
            case 1:
                 RankingUtil.checkRanking(jogador, tableCards);
                 myRank = RankingUtil.getRankingToInt(jogador);
                 switch(myRank){
               	case 0: 
               		Card highCard = RankingUtil.getHighCard(jogador,
              			Collections.EMPTY_LIST);
              			int cRank = highCard.getRank().ordinal();
              			if(cRank >= 10) addBehaviour(new sendMessageCall(quantia));
              			else if(folded > (npTable -1))addBehaviour(new sendMessageFold());
              			else addBehaviour(new sendMessageCall(quantia));
              			break;
               		
               		
               	case 1: if(toRaise == 1) {addBehaviour(new sendMessageRaise((quantia/2)));
               		toRaise--;	
               				}
               		else{
               			List<Card> highP = RankingUtil.getOnePair(jogador,
                          		tableCards);
               			int c = highP.get(0).getRankToInt();
               			if(c >= 8 && hasRaised == 0) {addBehaviour(new sendMessageRaise(quantia/2));hasRaised++;}
               			else addBehaviour(new sendMessageCall(quantia));
               				
               			}
               			break;			
               	case 2:  if(hasRaised == 0){addBehaviour(new sendMessageRaise(quantia/2)); hasRaised++; toRaise = 1;}
               	else addBehaviour(new sendMessageCall(quantia));
               				
               			break;
               	case 3: 
               	 if(hasRaised == 0){addBehaviour(new sendMessageRaise(quantia/2)); hasRaised++; toRaise = 1;}
               	else addBehaviour(new sendMessageCall(quantia));
               			break;
               	case 4:   if(hasRaised == 0){addBehaviour(new sendMessageRaise(quantia/2)); hasRaised++; toRaise = 2;}
               	else addBehaviour(new sendMessageCall(quantia));
               			break;
               			
               	default:   if(hasRaised == 0){addBehaviour(new sendMessageRaise(quantia/2)); hasRaised++; toRaise = 3;}
               	else addBehaviour(new sendMessageCall(quantia));
               			break;
              

               			}
                     break;
            case 2:
                RankingUtil.checkRanking(jogador, tableCards);
                myRank = RankingUtil.getRankingToInt(jogador);
                switch(myRank){
              	case 0:  if(folded > (npTable -1))addBehaviour(new sendMessageFold());
      			else addBehaviour(new sendMessageCall(quantia));
              			break;
              	case 1: addBehaviour(new sendMessageCall(quantia));
              			break;			
              	case 2:if(toRaise == 1){
              		addBehaviour(new sendMessageRaise(quantia/2));
              		toRaise--;
              	}else addBehaviour(new sendMessageCall(quantia));
              			break;
              	case 3: if(toRaise == 1){
              		addBehaviour(new sendMessageRaise(quantia/2));
              		toRaise--;
              	}
              	else addBehaviour(new sendMessageCall(quantia));

              	default:  
              		 if(toRaise == 3){
              			addBehaviour(new sendMessageRaise(quantia));
              			toRaise=1;
              		 }else if(toRaise == 2){
              			addBehaviour(new sendMessageRaise(quantia - (quantia/3)));
              			toRaise = 0;
              		 }else if(toRaise == 1){
              			addBehaviour(new sendMessageRaise(quantia/2));
              			toRaise--;
              		 }else addBehaviour(new sendMessageCall(quantia));
              			break;
             

              			}
                    break;
            case 3:
                RankingUtil.checkRanking(jogador, tableCards);
                myRank = RankingUtil.getRankingToInt(jogador);
                
                switch(myRank){

              	case 1: Card highCardPair = RankingUtil.getHighCard(jogador,
              			Collections.EMPTY_LIST);
              			int cRankPair = highCardPair.getRank().ordinal();
              			if(cRankPair >= 7 && hasRaised == 0) {addBehaviour(new sendMessageRaise(quantia)); hasRaised++;}
              			else addBehaviour(new sendMessageCall(quantia));
              			break;			
              	case 2: if(toRaise == 1){ addBehaviour(new sendMessageRaise(quantia-(quantia/4))); toRaise--;}
              	else addBehaviour(new sendMessageCall(quantia));
              			break;
              	case 3: if(toRaise == 1){ addBehaviour(new sendMessageRaise(quantia-(quantia/3))); toRaise--;}
             	else addBehaviour(new sendMessageCall(quantia));
              			break;
              	case 4:if(toRaise == 1){ addBehaviour(new sendMessageRaise(quantia)); toRaise--;}
		 	else addBehaviour(new sendMessageCall(quantia));
              			break;
              	case 5: if(toRaise == 1){ addBehaviour(new sendMessageRaise(quantia + (quantia/4))); toRaise--;}
                 	else addBehaviour(new sendMessageCall(quantia));
              			break;
              	default: if(toRaise == 1){  addBehaviour(new sendMessageRaise(2*quantia)); toRaise--;}
	 	else addBehaviour(new sendMessageCall(quantia));
              			break;
             

              			}
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
							msg.setContentObject((Serializable)quantia);
						} catch (IOException e) {
							e.printStackTrace();
						}
						dinheiro-=quantia;
					
					msg.addReceiver(receiver);
					myAgent.send(msg);
					
					
				}
					
				}


		private class sendMessageRaise extends OneShotBehaviour{
			double quantia;
			 public sendMessageRaise(double q) {
			quantia = q;
			}
				@Override 
				public void action(){
					AID receiver = new AID();
					receiver.setLocalName("Dealer");
					ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
					msg.setOntology(Ontologias.RAISE);
					
					
						try {
							msg.setContentObject((Serializable)quantia);
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
				   	/************************************** */
	    			System.out.println(" ->>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> EU vou para casa "+ getLocalName());
	    		/********************************************** */    
					AID receiver = new AID();
					receiver.setLocalName("Dealer");
					ACLMessage msg = new ACLMessage(ACLMessage.CANCEL);
					msg.setOntology(Ontologias.NAODINHEIRO);
					
					msg.addReceiver(receiver);
					myAgent.send(msg);
					
					
				}
			
				}
 
 
			private class sendMessagePot extends OneShotBehaviour{
				
				
				@Override 
				public void action(){
					AID receiver = new AID();
					receiver.setLocalName("Dealer");
					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					msg.setOntology(Ontologias.POT);
					
					msg.addReceiver(receiver);
					myAgent.send(msg);
					
					
				}
			
				}	
		private class sendMessageDesistiram extends OneShotBehaviour{
				
				
				@Override 
				public void action(){
					AID receiver = new AID();
					receiver.setLocalName("Dealer");
					ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
					msg.setOntology(Ontologias.DESISTIRAM);
					
					msg.addReceiver(receiver);
					myAgent.send(msg);
					
					
				}
			
				}	       
                        

 private class ReceiveBehaviour extends CyclicBehaviour {
   		
  	
  	@Override
 		public void action(){
  			
  			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.DISCONFIRM);
 		MessageTemplate mtJ = MessageTemplate.MatchOntology(Ontologias.RECEVEDINHEIRO);
  			MessageTemplate mtRespJogo = MessageTemplate.and(mt, mtJ);
  			ACLMessage msg = receive(mtRespJogo);
  			
  			if(msg != null){
  				
  				try {
 					dinheiro = Double.parseDouble( msg.getContent());
  					
                                          System.out.println ("Dinherio para jogar : "+ dinheiro);
                                                  
  
 				} catch (Exception e) {
  				System.out.println(e.getMessage());
  				}
 				
  			}else
  		block();
 				
  			}
 		} 
                
}