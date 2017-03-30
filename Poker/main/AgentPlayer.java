package main;


import cartas.Card;
import cartas.IPlayer;
import cartas.Player;
import cartas.RankingUtil;
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
import java.util.List;


public class AgentPlayer  extends Agent {


    private double dinheiro;
    private int nrAgentesFold;
    private IPlayer jogador;
    private List<Card> table;
    private int myRank,round,bet,pot,toRaise;


    @Override
    protected void setup() {
    super.setup();
    this.nrAgentesFold=0;
    this.dinheiro=0;
    this.jogador = new Player(getName());
    this.addBehaviour(new SendMessageEntrance() );

    }


    
  private class ReceiveBehaviour extends OneShotBehaviour {
  		
 	
 	@Override
 		public void action(){
 			
 			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
 		MessageTemplate mtJ = MessageTemplate.MatchOntology(Ontologias.RECEVEDINHEIRO);
 			MessageTemplate mtRespJogo = MessageTemplate.and(mt, mtJ);
 			ACLMessage msg = receive(mtRespJogo);
 			
 			if(msg != null){
 				
 				try {
 					dinheiro = Double.parseDouble( msg.getContent());
 					
                                         
                                                 
 
 				} catch (Exception e) {
 				System.out.println(e.getMessage());
 				}
 				
 			}else
 		block();
 				
 			}
 		}
    
					
		
		
		
	private class SendMessageEntrance extends OneShotBehaviour{
			
			@Override 
			public void action(){
				AID receiver = new AID();
				receiver.setLocalName("Dealer");
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.setOntology(Ontologias.ENTRAR);
				try {
					msg.setContentObject((Serializable) jogador);
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
						round = bet= pot = toRaise = 0;
						table = new ArrayList<Card>();
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

								table =(List<Card>) msg.getContentObject(); 
							
								       
								addBehaviour(new PlayGame());             
								

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
			
			
				public void action(){
					ACLMessage msg = receive();
					if(msg != null){

						try{

						if(msg.getOntology().equals(Ontologias.PERGUNTAR)){
					 		int quantia = (int)msg.getContentObject();
							responder(quantia);
							round++;
						}else if(msg.getOntology().equals(Ontologias.TURN)){
							Card cturn = (Card)msg.getContentObject();
							table.add(cturn);
						}else if (msg.getOntology().equals(Ontologias.RIVER)){
							Card criver = (Card)msg.getContentObject();
							table.add(criver);
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
	
	
	
	private void responder(int quantia){
	RankingUtil.checkRanking(jogador,table);
	myRank = RankingUtil.getRankingToInt(jogador);
	switch (round) {
            case 0:  responderFlop(quantia);
                     break;
			case 1:  responderTurn(quantia);
                     break;
			case 2:  responderRiver(quantia);
                     break;
				}	
	}
/*	
* check rank && round preço para entrar 
*  round 0
* 0-1(cartas fracas) FOLD
* 1(boas cartas)-3 JOGAR 
* 3(bom)-5 JOGAR SUBIR PROX RONDA
*6-9 JOGAR SUBIR SEMPRE 
* round 1 
* 1 - se for JACK > jogar 
*2-3 jogar 
*4 raise 
* 5-9 SUBIR 
* round 2 
* 1 - se for JACK > jogar  
* 2-3 - call se n for mutio alto
* 4-5 apostar 
* 6-9 subir e dar call a todos os raises   
*/

private void responderFlop(int quantia ){

switch(myRank){
	case 0: //fold 
			break;
	case 1:
			break;			
	case 2: //JOGAR
			break;
	case 3://JOGAR / se bom subir prox ronda
			break;
	case 4: // JOGAR subir prox ronda
			break;
	case 5: // JOGAR subir prox ronda
			break;
	case 6: // JOGAR SUBIR SEMPRE 
			break;
	case 7: // JOGAR SUBIR SEMPRE 
			break;
	case 8:// JOGAR SUBIR SEMPRE 
			break;
	case 9:// JOGAR SUBIR SEMPRE 
			break;

			}
}	


private void responderTurn(int quantia ){
	
	
switch(myRank){
	case 0: //FOLD
			break;
	case 1:
			break;			
	case 2:
			break;
	case 3:
			break;
	case 4:
			break;
	case 5:
			break;
	case 6:// JOGAR SUBIR SEMPRE 
			break;
	case 7:// JOGAR SUBIR SEMPRE 
			break;
	case 8:// JOGAR SUBIR SEMPRE 
			break;
	case 9:// JOGAR SUBIR SEMPRE 
			break;

			}
}


private void responderRiver(int quantia ){
	
	
switch(myRank){
	case 0: //FOLD
			break;
	case 1:
			break;			
	case 2:
			break;
	case 3:
			break;
	case 4:
			break;
	case 5:
			break;
	case 6: // JOGAR SUBIR SEMPRE 
			break;
	case 7: // JOGAR SUBIR SEMPRE 
			break;
	case 8: // JOGAR SUBIR SEMPRE 
			break;
	case 9: // JOGAR SUBIR SEMPRE 
			break;

			}
}		

//raise
/*
*round 0 
*
*round 1
* 
*
*round 2 
*/
		}    

	/************************************************* Respostas *************************************************/			

private class sendMessageCall extends OneShotBehaviour{
	int quantia;
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