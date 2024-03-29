package cartas;

import java.util.List;


public interface IPlayer {
	public Card[] getCards();
       
	public List<Card> getRankingList();

	public void setRankingList(List<Card> rankingList);

	public RankingEnum getRankingEnum();

	public void setRankingEnum(RankingEnum rankingEnum);

	public Card getHighCard();

	public void setHighCard(Card highCard);
        public String getNome();
        public void setCards(Card[] cards);
        
}
