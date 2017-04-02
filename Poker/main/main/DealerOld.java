package main;



import cartas.*;

import java.io.IOException;
import java.util.*;
import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import java.util.Map.Entry;


public class DealerOld  extends Agent{
	public boolean hand = false;
	//baralho e jogadores 
	private IDeck baralho;
	private List<IPlayer> playersTable;
	private List<IPlayer> playersHand;
	private List<Card> tableCards;
	private double valorApostar;
        private Map<String,Double> dinheiroApostado;
	private int pot,lastRaiseID;
	private boolean raised;
	
	
	
	protected void setup(){
		super.setup();
                this.playersTable=new ArrayList<>();
                this.playersHand= new ArrayList<>();
                this.dinheiroApostado=new HashMap<>();
                this.valorApostar= Ontologias.VALORAPOSTAR;
		this.addBehaviour(new ReceiveBehaviourJogadores());
                this.addBehaviour(new DealJob());
                
		
		//mao behaviour
		
	}
	
	/************************************************* ACEITAR JOGADORES *************************************************/
	
	private class ReceiveBehaviourJogadores  extends  CyclicBehaviour {
		
		@Override
		public void action() {
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			MessageTemplate mtE = MessageTemplate.MatchOntology(Ontologias.ENTRAR);
			MessageTemplate mtEntrada = MessageTemplate.and(mt, mtE);
			ACLMessage msg = receive(mtEntrada);
			
			if(msg != null){
				
				try {
                                       
					//adicionar o jogador a mesa
					playersTable.add( (IPlayer) msg.getContentObject());
					
					
					
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
			}else
			block();
			
		}
		
	} 
	
	
	/************************************************* DEAL BEHAVIOUR *************************************************/
	
	private class DealJob extends CyclicBehaviour{
		
		
		@Override
		public void action(){
			
                    MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
			MessageTemplate mtE = MessageTemplate.MatchOntology(Ontologias.COMECAR);
			MessageTemplate mtEntrada = MessageTemplate.and(mt, mtE);
			ACLMessage msg = receive(mtEntrada);
			
                        if(msg!=null) {
                        
                           
			
                            
			newHand();
			hand =true;
			
			
			//pergunta a todos os da table o que fazem. a comea�r pelo small blind e acabar no ultimo
			TurnRiver(true);
			addBehaviour(new AskTable());
			 
			TurnRiver(false);
			
			addBehaviour(new AskTable());
			List<IPlayer> win = getWinner();
			//dar o pot ao vencedor 
			//ver se alguem sai da mesa
			hand = false;
                        
                        
			
		}	
		}
		
		
		
		
			
			
			
		}
	/************************************************* Perguntar *************************************************/
public class AskTable extends SimpleBehaviour{
	private boolean finished = false;
	
	private int indexActual;
	
	public AskTable( ){
	raised = false;
	indexActual = 0;
	
	lastRaiseID = playersTable.size();
	}
	public void action(){
	int last = playersTable.size();
	if(indexActual <= last  && indexActual != lastRaiseID){
	
	Player p = (Player)playersTable.get(indexActual);
	SequentialBehaviour seq = new SequentialBehaviour();
	seq.addSubBehaviour(new PerguntaAgenteJoga(p.getNome(),valorApostar));
	seq.addSubBehaviour(new RespostasPlayer());
	addBehaviour(seq);

	//se deu raise -> raised == true ; update lastRaiseID
	//se deu reraise updateLastRaiseID
	//se saiu remover da playersHand e nao se incremente o indexactual
	if(indexActual < last){
	indexActual++;
	}else if(indexActual == last ){
		if(raised){
			indexActual =0;
		}else{
			finished = true;
		}

	}
   }
   else  if(indexActual == lastRaiseID && raised ){
	finished = true;
   }
	
				}
				@Override
				public boolean done() {
					
					return finished;
				}
	








}

public class PerguntaAgenteJoga extends OneShotBehaviour {
       String nomeA;
      double dinheiroApostar;
    
      
        public PerguntaAgenteJoga(String nomeAgente,double dinheiroA) {
            nomeA=nomeAgente;
            dinheiroApostar=dinheiroA;
        }    
    
    
        @Override
        public void action() {
        
            
            AID receiver = new AID();
            receiver.setLocalName(nomeA);
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setOntology(Ontologias.PERGUNTAR);
            try {
				msg.setContentObject(dinheiroApostar);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            msg.addReceiver(receiver);
            myAgent.send(msg);
         
        }

}
/************************************************* Resolver Respostas *************************************************/

private class RespostasPlayer extends OneShotBehaviour{


				public void action(){
				ACLMessage msg = receive();
					if(msg != null){

						try{
								
						if(msg.getOntology().equals(Ontologias.RAISE)){
					 		
						}else if(msg.getOntology().equals(Ontologias.JOGA)){
						
						}else if(msg.getOntology().equals(Ontologias.FOLD)){
						
						}else if(msg.getOntology().equals(Ontologias.RERAISE)){
						
						}

							} catch (Exception e) {
							System.out.println(e.getMessage());
									}
					
					
					}else block();

									}

}


/*
public class RecebeAgenteAposta extends SimpleBehaviour {

        private boolean finished = false;
        @Override
        
        public void action() {
        
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            MessageTemplate mtC = MessageTemplate.MatchOntology(Ontologias.JOGA);
            MessageTemplate mtRespClass= MessageTemplate.and(mt, mtC);
            ACLMessage msg = receive(mtRespClass);
           
            if(msg != null){    
                String g = msg.getSender().getName();
                double d = Double.valueOf(msg.getContent());
                
                if(d==-1) removerAgenteHand(g);
                
                if(dinheiroApostado.containsKey(g) && d==-1) { 
                    dinheiroApostado.remove(g);
                }
                
                if(d==valorApostar) {
                    dinheiroApostado.put(g, d);
                }
                
                if(d>valorApostar) {
                
                    dinheiroApostado.put(g, d);
                    valorApostar=d;
                    
                }
                
                
             finished = true;
			
		}else {
		      block();
	    }
		
	}

  public boolean done() {
    return finished;
  }   
}



public class EnviaRaise extends OneShotBehaviour { 
     
      private Map<String,Double> nomesUser;
     
      public EnviaRaise(Map<String,Double> nomes) {
       this.nomesUser=nomes;
           
      }    
    
        @Override
        public void action() {
           
           for(Entry<String,Double> entry : nomesUser.entrySet()) {
            
            AID receiver = new AID();
            receiver.setLocalName(entry.getKey());
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setOntology(Ontologias.RAISE);
            double enviar = valorApostar-entry.getValue();
            msg.setContent(enviar+"");
            msg.addReceiver(receiver);
            myAgent.send(msg);
           
           
           }

        }
}

public class RecebeRaise extends OneShotBehaviour { 
     
    
        @Override
        public void action() {
           
           MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            MessageTemplate mtC = MessageTemplate.MatchOntology(Ontologias.CALLRAISE);
            MessageTemplate mtRespClass= MessageTemplate.and(mt, mtC);
            ACLMessage msg = receive(mtRespClass);
            
            if(msg!=null) {
                double dinheiro =Double.valueOf(msg.getContent());
                String nome = msg.getSender().getName();
                if(dinheiro==-1) {
                    removerAgenteHand(nome);
                    if(dinheiroApostado.containsKey(nome))
                        dinheiroApostado.remove(nome);
                    
                }
                else {
                         double dinheiroA = dinheiroApostado.get(nome);
                         dinheiroA=dinheiroA + dinheiro;
                         dinheiroApostado.put(nome, dinheiroA);
                }
               
            }
           
           }

        }
*/
/************************************************* Mensagens de Ronda *************************************************/
	private class sendMessageNewHand extends OneShotBehaviour{
	String receiverN;
	 public sendMessageNewHand(String playername)  {

		 receiverN = playername;
	}
	
		@Override 
		public void action(){
			AID receiver = new AID();
			receiver.setLocalName(receiverN);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setOntology(Ontologias.NOVAMAO);
			
			msg.addReceiver(receiver);
			myAgent.send(msg);
			
			
		}
	
		}


	private class sendMessageCartas extends OneShotBehaviour{
	List<Card> mao;
	String receiverN;
	 public sendMessageCartas(List<Card> car,String playername)  {
	mao = car;
	receiverN = playername;
	}
		@Override 
		public void action(){
			AID receiver = new AID();
			receiver.setLocalName(receiverN);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setOntology(Ontologias.CARTAS);
			
			//msg.setContentObject(mao);
			msg.addReceiver(receiver);
			myAgent.send(msg);
			
			
		}
			
		}


	private class sendMessageFlop extends OneShotBehaviour{
	List<Card> mesa;
	String receiverN;
	 public sendMessageFlop(List<Card> car,String playername)  {
	mesa = car;
	receiverN = playername;
	}
		@Override 
		public void action(){
			AID receiver = new AID();
			receiver.setLocalName(receiverN);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setOntology(Ontologias.FLOP);
			
			//msg.setContentObject(mesa);
			msg.addReceiver(receiver);
			myAgent.send(msg);
			
			
		}
			
		}


	private class sendMessageTurn extends OneShotBehaviour{
	Card mesa;
	String receiverN;
	 public sendMessageTurn(Card car,String playername)  {
	mesa = car;
	receiverN = playername;
	}
		@Override 
		public void action(){
			AID receiver = new AID();
			receiver.setLocalName(receiverN);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setOntology(Ontologias.TURN);
			
			try {
				msg.setContentObject(mesa);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msg.addReceiver(receiver);
			myAgent.send(msg);
			
			
		}
			
		}


	private class sendMessageRiver extends OneShotBehaviour{
	Card mesa;
	String receiverN;
	 public sendMessageRiver(Card car,String playername)  {
	mesa = car;
	receiverN = playername;
	}
		@Override 
		public void action(){
			AID receiver = new AID();
			receiver.setLocalName(receiverN);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setOntology(Ontologias.RIVER);
			
			try {
				msg.setContentObject(mesa);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msg.addReceiver(receiver);
			myAgent.send(msg);
			
			
		}
			
		}
		
/************************************************* Mensagens de Fim de Ronda *************************************************/
	private class sendMessageVitoria extends OneShotBehaviour{
	int premio;
	String receiverN;
	 public sendMessageVitoria(int premio,String playername)  {
	premio = premio;
	receiverN = playername;
	}
		@Override 
		public void action(){
			AID receiver = new AID();
			receiver.setLocalName(receiverN);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setOntology(Ontologias.WIN);
			
			try {
				msg.setContentObject(premio);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msg.addReceiver(receiver);
			myAgent.send(msg);
			
			
		}
			
		}

private class sendMessageDerrota extends OneShotBehaviour{
String receiverN;
	 public sendMessageDerrota(String playername)  {

	receiverN = playername;
	}
	
		@Override 
		public void action(){
			AID receiver = new AID();
			receiver.setLocalName(receiverN);
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			msg.setOntology(Ontologias.LOSS);
			
			msg.addReceiver(receiver);
			myAgent.send(msg);
			
			
		}
	
		}		
/************************************************* DEALAR CARTAS *************************************************/
		public void newHand() {
			baralho = new Deck();
			tableCards = new ArrayList<>();
			playersHand = new ArrayList<>();
			for(IPlayer p : this.playersTable){
				playersHand.add(p);
				this.addBehaviour(new sendMessageNewHand(p.getNome()));
			}
			deal();
			
		}	
		
		
		private void deal()	{
			for (IPlayer player : playersHand) {
				List<Card> mao = new ArrayList<Card>();
				Card  c1 = baralho.pop();
				Card c2 =  baralho.pop();
				mao.add(c1); mao.add(c2);
				player.getCards()[0] = c1;
				player.getCards()[1] =c2;
				
				this.addBehaviour(new sendMessageCartas(mao,player.getNome()));
			}
			checkPlayersRanking();
			Flop();	
		}
		
		public void Flop() {
			
			baralho.pop();
			Card card1 = baralho.pop();
			tableCards.add(card1);
			Card card2 = baralho.pop();
			tableCards.add(card2);
			Card card3 = baralho.pop();
			tableCards.add(card3);
			checkPlayersRanking();
			for (IPlayer player : playersHand) {
				this.addBehaviour(new sendMessageFlop(tableCards,player.getNome()));
			}
			
			addBehaviour(new AskTable());
		}
		
		public void TurnRiver(boolean ct) {
			baralho.pop();
			Card c = baralho.pop();
			tableCards.add(c);
			checkPlayersRanking();
			for (IPlayer player : playersHand) {
				if(ct){
					this.addBehaviour(new sendMessageTurn(c,player.getNome()));
				}else{
						this.addBehaviour(new sendMessageRiver(c,player.getNome()));
				}
				
			}
		}
		
		
		/************************************************* DECIDIR VENCEDOR *************************************************/
		public List<IPlayer> getWinner() {
			checkPlayersRanking();
			List<IPlayer> winnerList = new ArrayList<IPlayer>();
			IPlayer winner = playersHand.get(0);
			Integer winnerRank = RankingUtil.getRankingToInt(winner);
			winnerList.add(winner);
			for (int i = 1; i < playersHand.size(); i++) {
				IPlayer player = playersHand.get(i);
				Integer playerRank = RankingUtil.getRankingToInt(player);
				//Draw game
				if (winnerRank == playerRank) {
					IPlayer highHandPlayer = checkHighSequence(winner, player);
					//Draw checkHighSequence
					if (highHandPlayer == null) {
						highHandPlayer = checkHighCardWinner(winner, player);
					}
					//Not draw in checkHighSequence or checkHighCardWinner
					if (highHandPlayer != null && !winner.equals(highHandPlayer)) {
						winner = highHandPlayer;
						winnerList.clear();
						winnerList.add(winner);
					} else if (highHandPlayer == null) {
						//Draw in checkHighSequence and checkHighCardWinner
						winnerList.add(winner);
					}
				} else if (winnerRank < playerRank) {
					winner = player;
					winnerList.clear();
					winnerList.add(winner);
				}
				winnerRank = RankingUtil.getRankingToInt(winner);
			}
			
			return winnerList;
		}
		
		private IPlayer checkHighSequence(IPlayer player1, IPlayer player2) {
			Integer player1Rank = sumRankingList(player1);
			Integer player2Rank = sumRankingList(player2);
			if (player1Rank > player2Rank) {
				return player1;
			} else if (player1Rank < player2Rank) {
				return player2;
			}
			return null;
		}
		
		@SuppressWarnings("unchecked")
		private IPlayer checkHighCardWinner(IPlayer player1, IPlayer player2) {
			IPlayer winner = compareHighCard(player1, player1.getHighCard(),
			player2, player2.getHighCard());
			if (winner == null) {
				Card player1Card = RankingUtil.getHighCard(player1,
				Collections.EMPTY_LIST);
				Card player2Card = RankingUtil.getHighCard(player2,
				Collections.EMPTY_LIST);
				winner = compareHighCard(player1, player1Card, player2, player2Card);
				if (winner != null) {
					player1.setHighCard(player1Card);
					player2.setHighCard(player2Card);
				} else if (winner == null) {
					player1Card = getSecondHighCard(player1, player1Card);
					player2Card = getSecondHighCard(player2, player2Card);
					winner = compareHighCard(player1, player1Card, player2,
					player2Card);
					if (winner != null) {
						player1.setHighCard(player1Card);
						player2.setHighCard(player2Card);
					}
				}
			}
			return winner;
		}
		
		private IPlayer compareHighCard(IPlayer player1, Card player1HighCard,
		IPlayer player2, Card player2HighCard) {
			if (player1HighCard.getRankToInt() > player2HighCard.getRankToInt()) {
				return player1;
			} else if (player1HighCard.getRankToInt() < player2HighCard
			.getRankToInt()) {
				return player2;
			}
			return null;
		}
		
		
		private Card getSecondHighCard(IPlayer player, Card card) {
			if (player.getCards()[0].equals(card)) {
				return player.getCards()[1];
			}
			return player.getCards()[0];
		}
		
	
		private Integer sumRankingList(IPlayer player) {
			Integer sum = 0;
			for (Card card : player.getRankingList()) {
				sum += card.getRankToInt();
			}
			return sum;
		}
		
		private void checkPlayersRanking() {
			for (IPlayer player : playersHand) {
				RankingUtil.checkRanking(player, tableCards);
			}
		}	
/************************************************* Remover Jogadores *************************************************/
		public void removePlayerTable(IPlayer player) {
			//remover o player da mesa
			playersTable.remove(player);
		}
                
                public void removeAgenteTable(String player) {
			//remover o player da mesa
                         for(IPlayer a :this.playersTable) {
                    
                        if(a.getNome().equalsIgnoreCase(player)) { 
                            this.playersHand.remove(a);
                            return;
                        }
                    }
                        
			
		}

			public void removePlayerHand(IPlayer player) {
			//remover o player da mesa
			playersHand.remove(player);
		}
                      
                public void removerAgenteHand(String nomeAgente) {
                
                    for(IPlayer a :this.playersHand) {
                    
                        if(a.getNome().equalsIgnoreCase(nomeAgente)) { 
                            this.playersHand.remove(a);
                            return;
                        }
                    }
                    
                
                }
                //calcula as apostas que nao sejam normais
                // se retornar vazio significa que todos apostaram o mesmo
        public Map<String,Double> getApostasNormais() {
           Map<String,Double> lista = new HashMap<>();
              
           this.dinheiroApostado.entrySet().stream().filter((entry) -> (Objects.equals(this.valorApostar, entry.getValue()) ==false)).forEachOrdered((entry) -> {
               lista.put(entry.getKey(),entry.getValue());
            }); 
        
            return lista;
            
        }        
                
                        
	}
	
	
	