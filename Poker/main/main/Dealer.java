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


public class Dealer  extends Agent{

    //baralho e jogadores 
    private Deck baralho;
    private List<Player> playersInTable;
    private List<Player> playersInHand;
    //rondas
    private List<Card> tableCards;
    private int toCall,pot,round;
    private boolean raised,hand;
    private Map<String,Integer> dinheiroApostado;

protected void setup(){
    super.setup();
    this.playersInTable=new ArrayList<>();
    this.addBehaviour(new ReceiveBehaviourJogadores());
    this.addBehaviour(new DealJob());
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
		}
}


private class WorkWork extends SimpleBehaviour{

	private boolean finished = false;
        
        @Override
		public void action(){
        	if(!hand){
        	hand = true;
        	round = 0;
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
            finished = true;
        	}
        }

        @Override
        public boolean done(){
            if(playersInTable.size() == 1 && !hand) finished = true;
            return finished;
        }
}


	/************************************************* DEAL SUB BEHAVIOURS *************************************************/
private class NewHand extends OneShotBehaviour{

        @Override
		public void action(){
		    baralho = new Deck();
			tableCards = new ArrayList<>();
			playersInHand = new ArrayList<>();
			toCall = 50;
        for(Player p : playersInTable){
				playersInHand.add(p);
				addBehaviour(new sendMessageNewHand(p.getNome()));
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
			for (IPlayer player : playersInHand) {
			addBehaviour(new sendMessageRiver(c,player.getNome()));
			}
        }

}


private class Winner extends OneShotBehaviour{

        @Override
		public void action(){
		    
        }

}


private class Clean extends OneShotBehaviour{

        @Override
		public void action(){
		   hand = false; 
        }

}
	/************************************************* Perguntar *************************************************/
public class AsTableControl extends SimpleBehaviour{
	private boolean finished = false,worked = false;
	
	private int rounI ;
	 public AsTableControl(int i) {
		rounI  = i;
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
	//Collections.rotate(numbers, numbers.size() -5);
	Player p = (Player)playersInHand.get(indexActual);
	SequentialBehaviour seq = new SequentialBehaviour();
	seq.addSubBehaviour(new PerguntaAgenteJoga(p.getNome(),toCall));
	seq.addSubBehaviour(new RespostasPlayer(indexActual));
	addBehaviour(seq);
		 }else round ++;
	
				}
				
	
}


/************************************************* Resolver Respostas *************************************************/

private class RespostasPlayer extends OneShotBehaviour{

	
	private int indexActual;
	private boolean received = false;
	
	public RespostasPlayer( int index){
	
	indexActual = index;
	
	
	}
	 			@Override
				public void action(){
				ACLMessage msg = receive();
					if(msg != null){
							received  = true;
						try{
								
						if(msg.getOntology().equals(Ontologias.RAISE)){
					 		raised = true;
						}else if(msg.getOntology().equals(Ontologias.JOGA)){
						System.out.println("Jogando");
						}else if(msg.getOntology().equals(Ontologias.FOLD)){
						
						}
							} catch (Exception e) {
							System.out.println(e.getMessage());
									}
					
					
					}else block();

									}
	 			
	 			@Override
					public int onEnd(){
						if(raised ){
							 
							Collections.rotate(playersInHand, playersInHand.size() -indexActual);
							raised = false;
							addBehaviour(new AskTable(1));
						}else {
							
							addBehaviour(new AskTable(indexActual +1));
						}
						return 1;
					}
}
/************************************************* Updates *************************************************/		

private void checkPlayersRanking() {
			for (IPlayer player : playersInHand) {
				RankingUtil.checkRanking(player, tableCards);
			}
		}

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
		msg.setOntology(Ontologias.DINHEIRO);
		
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
		msg.setOntology(Ontologias.PERDEU);
		
		msg.addReceiver(receiver);
		myAgent.send(msg);
		
		
	}

	}	

private class PerguntaAgenteJoga extends OneShotBehaviour {
       String nomeA;
       int dinheiroApostar;
    
      
        public PerguntaAgenteJoga(String nomeAgente,int dinheiroA) {
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

}
/**        
			
			List<IPlayer> win = getWinner();
			//dar o pot ao vencedor 
			//ver se alguem sai da mesa
			
            
            
             */