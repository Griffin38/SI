package main;



import cartas.*;
import java.util.*;
import jade.core.*;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;


public class Dealer  extends Agent{
	public boolean hand = false;
	//baralho e jogadores 
	private IDeck baralho;
	private List<IPlayer> playersTable;
	private List<IPlayer> playersHand;
	private List<Card> tableCards;
	private double valorApostar;
        private Map<String,Double> dinheiroApostado;
	
	
	
	
	protected void setup(){
		super.setup();
                this.dinheiroApostado=new HashMap<>();
		this.addBehaviour(new ReceiveBehaviourJogadores());
		
		//mao behaviour
		
	}
	
	/************************************************* ACEITAR JOGADORES *************************************************/
	
	private class ReceiveBehaviourJogadores  extends  CyclicBehaviour {
		
		@Override
		public void action() {
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
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
	
	private class DealJob extends SimpleBehaviour{
		boolean finished = false;
		int pot = 0;
		@Override
		public void action(){
			
			
			if(playersTable.size() > 1 ){
			newHand();
			hand =true;
			
			Flop();	
			//pergunta a todos os da table o que fazem. a comea�r pelo small blind e acabar no ultimo
			TurnRiver();
			//pergunta a todos 
			TurnRiver();
			//pergunta a todos
			List<IPlayer> win = getWinner();
			//dar o pot ao vencedor 
			//ver se alguem sai da mesa
			hand = false;
			
		}	
		}
		
		
		
		
			@Override
			public boolean done() {
				if(playersTable.size()==1) finished = true;
				return finished;
			}
			
			
			
		}
	/************************************************* ASK BEHAVIOUR *************************************************/
public class AskTable extends OneShotBehaviour{
public void action(){
for(IPlayer p : playersHand){
//mandar mensagem




}
}
}

public class PerguntaAgenteJoga extends OneShotBehaviour {
       String nomeA;
       double dinheiroApostar;
    
      
        public PerguntaAgenteJoga(String nomeAgente,double dinheiroA) {
        
            super();
            this.nomeA=nomeAgente;
            this.dinheiroApostar=dinheiroA;
        }    
    
    
        @Override
        public void action() {
        
            
            AID receiver = new AID();
            receiver.setLocalName(this.nomeA);
            ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
            msg.setOntology(Ontologias.ENTRARJOGAR);
            msg.setContent(dinheiroApostar+"");
            msg.addReceiver(receiver);
            myAgent.send(msg);
         
        }

}


public class RecebeAgenteDesistiu extends SimpleBehaviour {

        private boolean finished = false;
        @Override
        
        public void action() {
        
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            MessageTemplate mtC = MessageTemplate.MatchOntology(Ontologias.DESISTIU);
            MessageTemplate mtRespClass= MessageTemplate.and(mt, mtC);
            ACLMessage msg = receive(mtRespClass);
           
            if(msg != null){    
                String g = msg.getSender().getName();
                removerAgenteHand(g);
                 dinheiroApostado.remove(g);
             finished = true;
			
		}else {
		      block();
	    }
		
	}

  public boolean done() {
    return finished;
  }   
}


public class RecebeAgenteRaise extends SimpleBehaviour {

        private boolean finished = false;
        @Override
        
        public void action() {
        
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
            MessageTemplate mtC = MessageTemplate.MatchOntology(Ontologias.RAISE);
            MessageTemplate mtRespClass= MessageTemplate.and(mt, mtC);
            ACLMessage msg = receive(mtRespClass);
           
            if(msg != null){    
 
               valorApostar=Double.valueOf(msg.getContent());
               dinheiroApostado.put(msg.getSender().getName(),valorApostar);
            

             finished = true;
			
		}else {
		      block();
	    }
		
	}

  public boolean done() {
    return finished;
  }   

    
    

}




	/************************************************* DEALAR CARTAS *************************************************/
		public void newHand() {
			baralho = new Deck();
			tableCards = new ArrayList<>();
			playersHand = new ArrayList<>();
			for(IPlayer p : this.playersTable){
				playersHand.add(p);
			}
		}	
		
		
		private void deal()	{
			for (IPlayer player : playersHand) {
				player.getCards()[0] = baralho.pop();
				player.getCards()[1] = baralho.pop();
				//mandar mensager ao player.getNome()
				
			}
			checkPlayersRanking();
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
				//mandar as cartas ao player.getNome()
			}
		}
		
		public void TurnRiver() {
			baralho.pop();
			Card c = baralho.pop();
			tableCards.add(c);
			checkPlayersRanking();
			for (IPlayer player : playersHand) {
				//mandar as cartas ao player.getNome()
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
                        
	}
	
	
	