package cartas;

import java.io.Serializable;
import java.util.List;


public class Player implements IPlayer, Serializable {

	
    private String nomeAgente;

	private Card[] cards = new Card[2];

	private RankingEnum rankingEnum = null;

	private List<Card> rankingList = null;

	private Card highCard = null;

    public Player(String nomeAgente) {
        this.nomeAgente = nomeAgente;
        this.cards = new Card[2];
    }

    public Player (IPlayer a) {
      this.nomeAgente=a.getNome();
      this.cards= a.getCards();
      this.rankingEnum=a.getRankingEnum();
      this.rankingList=a.getRankingList();
      this.highCard=a.getHighCard();
      
        
        
    }
    
 	public String getNome() {
 		return nomeAgente;
 	}

 
      
        
        
	public Card getHighCard() {
		return highCard;
	}

	public void setHighCard(Card highCard) {
		this.highCard = highCard;
	}

	public RankingEnum getRankingEnum() {
		return rankingEnum;
	}

	public void setRankingEnum(RankingEnum rankingEnum) {
		this.rankingEnum = rankingEnum;
	}

	public List<Card> getRankingList() {
		return rankingList;
	}

	public void setRankingList(List<Card> rankingList) {
		this.rankingList = rankingList;
	}

	public Card[] getCards() {
		return cards;
	}

	public void setCards(Card[] cards) {
		this.cards = cards;
	}
}
