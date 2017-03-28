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
import java.io.Serializable;
	import java.util.ArrayList;
	import java.util.List;


	public class AgentPlayer  extends Agent{
					private double dinheiro;
					private int nrAgentesFold;
					private IPlayer jogador;
				private List<Card> table;
			private int myRank;
					
					@Override
			protected void setup() {
					super.setup();
					this.nrAgentesFold=0;
					this.dinheiro=0;
					this.addBehaviour(new SendMessageEntrance() );
				
					
					}
			

					
		
		
			
	private class SendMessageEntrance extends OneShotBehaviour{
			
			public SendMessageEntrance (){
			jogador = new Player(this.getAgent().getName());
			}
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
					addBehaviour(new ReceiveBehaviourJogador());
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
			int rank ,round,bet,pot,toRaise;
			public PlayGame(){
				rank=0;
				round = 0;
				bet = 0;
				pot= 0;
				toRaise = 0;
			}	
				public void action(){
					ACLMessage msg = receive();
					if(msg != null){
						if(msg.getOntology().equals(Ontologias.PERGUNTAR)){
	RankingUtil.checkRanking(jogador,table);
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
							round++;
						}else if(msg.getOntology().equals(Ontologias.TURN)){

						}else if (msg.getOntology().equals(Ontologias.RIVER)){

						}else if (msg.getOntology().equals(Ontologias.RAISE)){
/*
*round 0 
*
*round 1
* 
*
*round 2 
*/
						}else if(msg.getOntology().equals(Ontologias.PERDEU)){
							if(dinheiro == 0){ 
								//add pedir para sair 
							}
							finished = true;
						}else if(msg.getOntology().equals(Ontologias.DINHEIRO)){
							//add dinheiro ao total 
							finished = true;
						}
					}else block();
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