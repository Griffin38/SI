package src;


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
	private List<String> mesa;
	
	
public Deck baralho;
	
	
	protected void setup(){
		super.setup();
		baralho = new Deck();
		//add receive entrance
		//mao behaviour
		
	}
	
	

private class DealJob extends SimpleBehaviour{
	boolean finished = false;
	String[] table ;
	int current;
		@Override
		public void action(){
			if(mesa.size()>1){
				int i =0;
				for(String a:mesa){
					table[i]= a; i++;
				}
				hand =true;
			
			Flop();	
			//pergunta a todos os da table o que fazem. a comea�r pelo small blind e acabar no ultimo
			TurnRiver();
			//pergunta a todos
			TurnRiver();
			//pergunta a todos
			//decide quem ganah pelas cartas--
			//ve se alguem foi de pi�a dinheiro == 0
			hand = false;
			baralho.Repack();
			}
			
			
		}
		
		private void novaMao(){
			baralho.shuffle();
			for(int i = 0 ; i < 2 ; i++){
			for(String s: table){
				String carta = baralho.drawCard();
				//avisar player da carta que recebeu
			}}
		}
		
		
		private void Flop(){
			String[] flop = new String[3];
			baralho.drawCard();
			for(int i = 0; i < 3 ; i++){
				flop[i]= baralho.drawCard();
			}
			for(String s: table){
				//avisar os players das cartas que sairam
			}
		}
		private void TurnRiver(){
			baralho.drawCard();
			String carta = baralho.drawCard();
			for(String s: table){
				//avisar os players das cartas que sairam
			}
		}
		@Override
		public boolean done() {
		if(mesa.size()==1) finished = true;
		return finished;
		}
}

	
	
	

}



class Deck {
   
    private static final String[] SUITS = {
        "H", "D", "C", "S"
    };
    private static final String[] RANKS = {
        "A", "K", "Q", "J", "10", "9", "8", "7", "6", "5", "4", "3", "2"
    };

    private List<String> cards;

    public Deck() {
        this.cards = new LinkedList<>();
        for (int i = 0; i < SUITS.length; i++) {
            for (int j = 0; j < RANKS.length; j++) {
                this.cards.add(RANKS[j] + SUITS[i]);
            }
        }
    }
    
    public void Repack(){
    	this.cards = new LinkedList<>();
        for (int i = 0; i < SUITS.length; i++) {
            for (int j = 0; j < RANKS.length; j++) {
                this.cards.add(RANKS[j] + SUITS[i]);
            }
        }
    }
    
    public void shuffle() {
        Collections.shuffle(this.cards);
    }

    public String drawCard() throws NoSuchElementException {
        if (this.cards.isEmpty()) {
            throw new NoSuchElementException();
        }
        return this.cards.remove(0);
    }

    public void returnCard(String card) {
        this.cards.add(card);
    }

   
}