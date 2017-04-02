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
import java.io.Serializable;
import java.util.Map.*;



public class Dealer  extends Agent{


    //baralho e jogadores 
    private Deck baralho;
    private List<Player> playersInTable;
    private List<Player> playersInHand;
    //rondas
    private List<Card> tableCards;
    private double pot,round,folded;
    private double toCall;
    private boolean raised,hand;
    private Map<String,Integer> dinheiroApostado;

protected void setup(){
    super.setup();
    hand=false;
    this.playersInTable=new ArrayList<>();
    this.addBehaviour(new ReceiveBehaviourJogadores());
    this.addBehaviour(new DealJob());
    this.addBehaviour(new ReceiveRequestDesistiram());
     this.addBehaviour(new ReceiveRequestPot());
     this.addBehaviour(new ReceiveMessageOfShame());
     
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
					playersInTable.add( (Player) msg.getContentObject());
					} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
			}else
			block();
			
		}
		
	} 
	/************************************************* Requests *************************************************/
	

	private class ReceiveRequestDesistiram  extends  CyclicBehaviour {
		
		@Override
		public void action() {
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			MessageTemplate mtE = MessageTemplate.MatchOntology(Ontologias.DESISTIRAM);
			MessageTemplate mtEntrada = MessageTemplate.and(mt, mtE);
			ACLMessage msg = receive(mtEntrada);
			
			if(msg != null){
				
				try {
					 addBehaviour(new sendMessageDesistiram(msg.getSender()));                
					
					} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
			}else
			block();
			
		}
		
	} 
	private class ReceiveRequestPot  extends  CyclicBehaviour {
		
		@Override
		public void action() {
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
			MessageTemplate mtE = MessageTemplate.MatchOntology(Ontologias.POT);
			MessageTemplate mtEntrada = MessageTemplate.and(mt, mtE);
			ACLMessage msg = receive(mtEntrada);
			
			if(msg != null){
				
				try {
					
                   addBehaviour(new sendMessagePot(msg.getSender()));                  
					
					} catch (Exception e) {
					System.out.println(e.getMessage());
				}
				
			}else
			block();
			
		}
		
	}
	
	private class ReceiveMessageOfShame  extends  CyclicBehaviour {
		
		@Override
		public void action() {
			
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CANCEL);
			MessageTemplate mtE = MessageTemplate.MatchOntology(Ontologias.NAODINHEIRO);
			MessageTemplate mtEntrada = MessageTemplate.and(mt, mtE);
			ACLMessage msg = receive(mtEntrada);
			
			if(msg != null){
				
				try { 
                                      /*
					String[] parts = msg.getSender().toString().split("@");
					String[] parts2 = parts[0].split("\"");
				   
                                    removeAgenteTable(parts2[1]);         
                                    */
                                      removeAgenteTable(msg.getSender().getLocalName());
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

                            
                        addBehaviour(new WorkWork());

                        
			
		}
                        else block();
		}

}


private class WorkWork extends SimpleBehaviour{


	private boolean finished = false;
        
        @Override
		public void action(){
                   
        	if(!hand){
                   
        	hand = true;
        	round = 0;
        	pot = 0;
            SequentialBehaviour seq = new SequentialBehaviour();
            seq.addSubBehaviour(new NewHand());
            seq.addSubBehaviour(new AsTableControl(0));
            seq.addSubBehaviour(new Flop());
            seq.addSubBehaviour(new AsTableControl(1));
            seq.addSubBehaviour(new Turn());
            seq.addSubBehaviour(new AsTableControl(2));
            seq.addSubBehaviour(new River());
            seq.addSubBehaviour(new AsTableControl(3));
            seq.addSubBehaviour(new Winner());
            seq.addSubBehaviour(new Clean());
            
            addBehaviour(seq);
            
      	}
        }

        @Override
        public boolean done(){
            if(playersInTable.size() == 1 && !hand) finished = true;
            return finished;
        }
        @Override
		public int onEnd(){
        	/************************************** */
			System.out.println("GRANDE VENCEDOR:: "+ playersInTable.get(0).getNome());
			/********************************************** */
			return 1;
		}
}


	/************************************************* DEAL SUB BEHAVIOURS *************************************************/
//to:do - win nao manda as loss -

private class NewHand extends OneShotBehaviour{

        @Override
		public void action(){
		    baralho = new Deck();
			tableCards = new ArrayList<>();
			playersInHand = new ArrayList<>();
			toCall = Ontologias.VALORAPOSTAR;
        for(Player p : playersInTable){
            
				playersInHand.add(p);
				addBehaviour(new sendMessageNewHand(p.getNome(),playersInTable.size()));
			}
        
			for (Player player : playersInHand) {
				List<Card> mao = new ArrayList<Card>();
				Card  c1 = baralho.pop();
				Card c2 =  baralho.pop();
				mao.add(c1); mao.add(c2);
				player.getCards()[0] = c1;
				player.getCards()[1] =c2;
				
				addBehaviour(new sendMessageCartas(mao,player.getNome()));
			}
        
			checkPlayersRanking();
}
}

private class Flop extends OneShotBehaviour{

        @Override
		public void action(){
		    baralho.pop();
			Card card1 = baralho.pop();
			tableCards.add(card1);
			Card card2 = baralho.pop();
			tableCards.add(card2);
			Card card3 = baralho.pop();
			tableCards.add(card3);
			checkPlayersRanking();
			/************************************** */
			System.out.println("FLOP:: "+tableCards.toString());
			/********************************************** */
			for (IPlayer player : playersInHand) {
				addBehaviour(new sendMessageFlop(tableCards,player.getNome()));
			}
        }

}


private class Turn extends OneShotBehaviour{

        @Override
		public void action(){
		    baralho.pop();
			Card c = baralho.pop();
			tableCards.add(c);
			checkPlayersRanking();
			/************************************** */
			System.out.println("TURN:: "+c.toString());
			/********************************************** */
			for (IPlayer player : playersInHand) {
			addBehaviour(new sendMessageTurn(c,player.getNome()));
			}
        }

}


private class River extends OneShotBehaviour{

        @Override
		public void action(){
		    baralho.pop();
			Card c = baralho.pop();
			tableCards.add(c);
			checkPlayersRanking();
			/************************************** */
			System.out.println("RIVER:: "+c.toString());
			/********************************************** */
			for (IPlayer player : playersInHand) {
			addBehaviour(new sendMessageRiver(c,player.getNome()));
			}
        }

}


private class Winner extends OneShotBehaviour{

        @Override
		public void action(){
        	List<IPlayer> win = new ArrayList<>();
                win.addAll(getWinner());
                int i;
                
                for(i=0;i<win.size();i++) {
                
                AID receiver = new AID();
		receiver.setLocalName(win.get(i).getNome());
		ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
		msg.setOntology(Ontologias.WIN); 
		msg.setContent((pot/win.size())+"");
		msg.addReceiver(receiver);
		myAgent.send(msg);
		 }
                List<IPlayer> loss = new ArrayList<>();
                
                loss.addAll(playersInTable);
                boolean d = loss.removeAll(win);
                if(d) {
                for(i=0;i<loss.size();i++) {
                 
		                  addBehaviour(new sendMessageDerrota(loss.get(i).getNome()));
		
                }
                
                }
                
                
        }

}


private class Clean extends OneShotBehaviour{

        @Override
		public void action(){
    		
		   hand = false; 
		   /************************************** */
			System.out.println("CLEAN Hand:: "+ hand);
			/********************************************** */
        }

}
	/************************************************* Perguntar *************************************************/
public class AsTableControl extends SimpleBehaviour{
	private boolean finished = false,worked = false;
	
	private int rounI ;
	 public AsTableControl(int i) {
		rounI  = i;
		folded = 0;
	}
	 @Override
		public void action(){
		 if(rounI == round & ! worked){
			 addBehaviour(new AskTable(0));
			 rounI++;
			 worked = true;
		 }else if(rounI == round && worked){
			 finished = true;
		 }
	 }
	 @Override
     public boolean done(){
		 
         return finished;
     }
}
public class AskTable extends OneShotBehaviour{
	
	
	private int indexActual;
	
	public AskTable( int index){
	
	indexActual = index;
	
	
	}
	
	 @Override
	public void action(){
	
		 if(indexActual < playersInHand.size()){
	
	Player p = (Player)playersInHand.get(indexActual);
	SequentialBehaviour seq = new SequentialBehaviour();
	seq.addSubBehaviour(new PerguntaAgenteJoga(p.getNome(),toCall));
	seq.addSubBehaviour(new RespostasPlayer(indexActual));
	addBehaviour(seq);
		 }else round ++;
	
				}
				
	
}


/************************************************* Resolver Respostas *************************************************/

private class RespostasPlayer extends SimpleBehaviour{

	
	private int indexActual;
	private boolean received = false;
	
	public RespostasPlayer( int index){
	
	indexActual = index;
	
	
	}
	 			@Override
				public void action(){
	 			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);	
				ACLMessage msg = receive(mt);
					if(msg != null){
							received  = true;
						try{
								
						if(msg.getOntology().equals(Ontologias.RAISE)){
							received = true;
					 		raised = true;
					 		double x =(double) msg.getContentObject();
					 		toCall=x;
					 		pot+=x;
							/************************************** */
							System.out.println("Raise "+x+" " + msg.getSender().getLocalName());
						/********************************************** */	
					 		
						}else if(msg.getOntology().equals(Ontologias.JOGA)){
						received = true;
						double y = (double)msg.getContentObject();
						pot+=y;
						/************************************** */
						System.out.println("Joga "+y+" " + msg.getSender().getLocalName());
					/********************************************** */	
				 						
						}else if(msg.getOntology().equals(Ontologias.FOLD)){
							received = true;
						folded++;
						/************************************** */
						System.out.println("Fold " + msg.getSender().getLocalName());
					/********************************************** */
						String[] parts = msg.getSender().toString().split("@");
						String[] parts2 = parts[0].split("\"");
                                                System.out.println("Vou remover o agente : " + msg.getSender().getLocalName());
						removerAgenteHand(msg.getSender().getLocalName());
						
						}
							} catch (Exception e) {
							System.out.println(e.getMessage());
									}
					
					
					}else block();

									}

	 			
	 			@Override
					public int onEnd(){
						if(raised && indexActual != 0 ){
							 
							Collections.rotate(playersInHand, playersInHand.size() - indexActual);
							raised = false;
							addBehaviour(new AskTable(1));
						}else {
							
							addBehaviour(new AskTable(indexActual +1));
						}
						return 1;
					}
	 			@Override
				public boolean done() {
					
					return received;
				}
}


/************************************************* Mensagens de Ronda *************************************************/		
private class sendMessageNewHand extends OneShotBehaviour{
private String receiverN;
private int quant;
 public sendMessageNewHand(String playername,int quantia)  {
	quant = quantia;
	 receiverN = playername;
}

	@Override 
	public void action(){
		AID receiver = new AID();
		receiver.setLocalName(receiverN);
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setOntology(Ontologias.NOVAMAO);
		try {
			msg.setContentObject(quant);
		} catch (IOException e) {
			
			e.printStackTrace();
		}
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
/************************************** */
System.out.println("Cartas:: "+mao.toString()+ " para "+ playername);
/********************************************** */
}
	@Override 
	public void action(){
		AID receiver = new AID();
		receiver.setLocalName(receiverN);
		ACLMessage msg = new ACLMessage(ACLMessage.AGREE);
		msg.setOntology(Ontologias.CARTAS);
		
    try {
        msg.setContentObject((Serializable) mao);
    } catch (IOException ex) {
        System.out.println(ex.getMessage());
    }
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
		ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
		msg.setOntology(Ontologias.FLOP);
		
    try {
        msg.setContentObject((Serializable) mesa);
    } catch (IOException ex) {
        System.out.println(ex.getMessage());
    }
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
		ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
		msg.setOntology(Ontologias.TURN);
		
		try {
			msg.setContentObject(mesa);
		} catch (IOException e) {
	
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
		ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
		msg.setOntology(Ontologias.RIVER);
		
		try {
			msg.setContentObject(mesa);
		} catch (IOException e) {
	
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
		ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
		msg.setOntology(Ontologias.WIN);
		
		try {
			msg.setContentObject(premio);
		} catch (IOException e) {
			
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
		ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
		msg.setOntology(Ontologias.LOSS);
		
		msg.addReceiver(receiver);
		myAgent.send(msg);
		
		
	}

	}	

/********************************************   POT E NR DE FOLDS************************************************/
private class sendMessageDesistiram extends OneShotBehaviour{
	AID receiverN;
 public sendMessageDesistiram(AID playername)  {

receiverN = playername;
}

	@Override 
	public void action(){
		AID receiver = new AID();
		receiver = receiverN;
		ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
		msg.setOntology(Ontologias.DESISTIRAM);
		try {
			msg.setContentObject(folded);
		} catch (IOException e) {
			e.printStackTrace();
		}
		msg.addReceiver(receiver);
		myAgent.send(msg);
		
		
	}

	}
private class sendMessagePot extends OneShotBehaviour{
	AID receiverN;
 public sendMessagePot(AID playername)  {

receiverN = playername;
}

	@Override 
	public void action(){
		AID receiver = new AID();
		receiver = receiverN;
		ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
		msg.setOntology(Ontologias.POT);
		try {
			msg.setContentObject(pot);
		} catch (IOException e) {
			e.printStackTrace();
		}
		msg.addReceiver(receiver);
		myAgent.send(msg);
		
		
	}

	}
private class PerguntaAgenteJoga extends OneShotBehaviour {
       private String nomeA;
       double dinheiroApostar;
    
      
        public PerguntaAgenteJoga(String nomeAgente,double dinheiroA) {
            nomeA=nomeAgente;
            dinheiroApostar=dinheiroA;
        }    
    
    
        @Override
        public void action() {
        
            
            AID receiver = new AID();
            receiver.setLocalName(nomeA);
            ACLMessage msg = new ACLMessage(ACLMessage.CONFIRM);
            msg.setOntology(Ontologias.PERGUNTAR);
            try {
				msg.setContentObject(dinheiroApostar);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
            msg.addReceiver(receiver);
        	/************************************** */
			System.out.println("A perguntar  " +nomeA);
		/********************************************** */	
            myAgent.send(msg);
         
        }

}

/************************************************* DECIDIR VENCEDOR *************************************************/
public List<IPlayer> getWinner() {
	checkPlayersRanking();
	List<IPlayer> winnerList = new ArrayList<IPlayer>();
	IPlayer winner = playersInHand.get(0);
	Integer winnerRank = RankingUtil.getRankingToInt(winner);
	winnerList.add(winner);
	for (int i = 1; i < playersInHand.size(); i++) {
		IPlayer player = playersInHand.get(i);
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
	for (IPlayer player : playersInHand) {
		RankingUtil.checkRanking(player, tableCards);
	}
}	
/************************************************* Remover Jogadores *************************************************/
public void removePlayerTable(IPlayer player) {
	//remover o player da mesa
	playersInTable.remove(player);
}

public void removePlayerHand(IPlayer player) {
	//remover o player da mesa
	playersInHand.remove(player);
}
public void removerAgenteHand(String nomeAgente) {
    
    for(IPlayer a :this.playersInHand) {
    
        if(a.getNome().equalsIgnoreCase(nomeAgente)) {
            this.playersInHand.remove(a);
            return;
        }
    }
}
    public void removeAgenteTable(String player) {
	//remover o player da mesa
                 for(IPlayer a :this.playersInTable) {
            
                if(a.getNome().equalsIgnoreCase(player)) {
                   
                    this.playersInTable.remove(a);
                    return;
                }
            }
                
	
}

  
    
    

}

