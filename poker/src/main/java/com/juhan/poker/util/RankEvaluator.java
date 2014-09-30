package com.juhan.poker.util;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.juhan.poker.model.Card;
import com.juhan.poker.model.Card.Suits;
import com.juhan.poker.model.Dealer;
import com.juhan.poker.model.Player;

/**
 * Created by Juhan Klementi on 29.09.2014.
 */
public class RankEvaluator {

    private int rank = 9;
    private Player player;
    private List<Card> cards;
    public static final String[] ranks = new String[]{
        "Royal Flush",
        "Straight Flush",
        "Four of a kind",
        "Full house",
        "Flush",
        "Straight",
        "Three of a kind",
        "Two pairs",
        "One pair",
        "High card"
    };

    public RankEvaluator(Player player) {
        this.player = player;
        cards = player.getCards();
        if (cards == null || cards.size() != 5)
            throw new RuntimeException("Not supported");
        this.cards = cards;
        Collections.sort(cards);
        evaluate();
    }

    private void evaluate(){
        if (isRoyalFlush()) {rank = 0;return;}
        if (isStraightFlush()) {rank = 1;return;}
        if (isFourOfAKind()) {rank = 2;return;}
        if (isFullHouse()) {rank = 3;return;}
        if (isFlush()) {rank = 4;return;}
        if (isStraight()) {rank = 5;return;}
        if (isThreeOfAKind()) {rank = 6;return;}
        int num = numberOfPairs();
        if (num == 2) {rank = 7;return;}
        if (num == 1) {rank = 8;return;}
        rank = 9;
    }

    public RankEvaluator compareRanks(RankEvaluator another){
        if (this.rank < another.rank) return this;
        if (this.rank > another.rank) return another;
        else {
            return compareDraw(another);
        }
    }

    private RankEvaluator compareDraw(RankEvaluator another){
        if (rank != another.rank)
            throw new RuntimeException("This should have not happened!");

        int pMax = Collections.max(cards).getRank();
        int dMax = Collections.max(another.cards).getRank();

        // simplified, when both players have the same rank & highest card, dealer wins.
        return pMax > dMax ? this : another;
    }

    private boolean isRoyalFlush(){
        return cards.get(4).getRank() == 14 && cards.get(0).getRank() == 10
                && isFlush() && isStraight();
    }

    private boolean isStraightFlush(){
        return isFlush() && isStraight();
    }

    private boolean isFourOfAKind() { return isNumberOfAKind(cards, 4); }

    private boolean isFullHouse(){
        return isNumberOfAKind(cards, 3) && numberOfPairs() == 3;
    }

    private boolean isFlush(){
        Suits suit = cards.get(0).getSuit();
        for (int i = 1; i<cards.size(); i++){
            if (suit != cards.get(i).getSuit()) return false;
        }
        return true;
    }

    // this method is simplified. Ace - 2 - 3 - 4 - 5 doesn't count.
    private boolean isStraight(){
        return Collections.max(cards).getRank() - Collections.min(cards).getRank() == 5;
    }

    private boolean isThreeOfAKind() { return isNumberOfAKind(cards, 3); }

    private int numberOfPairs(){
        int pairs = 0;
        for (int i = 0; i<4; i++){
            if (isNumberOfAKind(cards.subList(i, i+2), 2))
                pairs++;
        }
        return pairs;
    }

    private boolean isNumberOfAKind(List<Card> cards, int number) {

        for(int rank=2; rank<=14; rank++) {
            int count = 0;
            for (int i = 0; i < cards.size(); i++){
                if (cards.get(i).getRank()==rank) { count++; }
            }
            if (count==number) { return true; }
        }
        return false;
    }

    public int getRank(){
        return rank;
    }

    public Player getPlayer(){
        return player;
    }
}
