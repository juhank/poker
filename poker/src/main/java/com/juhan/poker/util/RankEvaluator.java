package com.juhan.poker.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import android.util.Log;
import com.juhan.poker.model.Card;
import com.juhan.poker.model.Card.Suits;
import com.juhan.poker.model.Dealer;
import com.juhan.poker.model.Player;

/**
 * Created by Juhan Klementi on 29.09.2014.
 */
public class RankEvaluator {

    private int rank = 9;
    private int rank2 = 0;
    private int rank3 = 0;
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
        Log.d("RankEvaluator:", "rank: " + rank + "(" + ranks[rank] + "), rank2:" + rank2 + ", rank3:" + rank3);
    }

    private void evaluate(){
        rank = 9;
        Log.d("RankEvaluator:", "evaluating " + player.getName() + "(" + player.getCardsString() + ")");
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
        rank2 = cards.get(4).getRank();
        rank3 = cards.get(3).getRank();
    }

    public RankEvaluator compareRanks(RankEvaluator another){
        if (this.rank < another.rank) return this;
        if (this.rank > another.rank) return another;
        else {
            return compareDraw(another);
        }
    }

    // simplified, when both players have the same rank & highest card, dealer wins.
    private RankEvaluator compareDraw(RankEvaluator another){
        if (rank != another.rank)
            throw new RuntimeException("This should have not happened!");

        if (Arrays.asList(0,1,4,5).contains(rank)){
            int pMax = Collections.max(cards).getRank();
            Log.d("RankEvaluator:", "comparing player max");
            int dMax = Collections.max(another.cards).getRank();

            return pMax > dMax ? this : another;
        } else {
            if (rank2 != another.rank2) {
                return rank2 > another.rank2 ? this : another;
            }
            if (rank3 != another.rank3) {
                return rank3 > another.rank3 ? this : another;
            }

            return another;
        }
    }

    private boolean isRoyalFlush(){
        return cards.get(4).getRank() == 14 && cards.get(0).getRank() == 10
                && isFlush() && isStraight();
    }

    private boolean isStraightFlush(){
        return isFlush() && isStraight();
    }

    private boolean isFourOfAKind() {
        if (cards.get(0).getRank() == cards.get(3).getRank() ||
            cards.get(1).getRank() == cards.get(4).getRank()){

            rank2 = cards.get(0).getRank() == cards.get(1).getRank() ?
                    cards.get(4).getRank() : cards.get(0).getRank();
            return true;
        } else return false;
    }

    private boolean isFullHouse(){
        boolean fullHouse = false;
        if (cards.get(0).getRank() == cards.get(2).getRank() &&
            cards.get(3).getRank() == cards.get(4).getRank()){
            rank2 = cards.get(0).getRank();
            rank3 = cards.get(4).getRank();
            fullHouse = true;
        }
        if (cards.get(0).getRank() == cards.get(1).getRank() &&
            cards.get(2).getRank() == cards.get(4).getRank()) {
            rank2 = cards.get(4).getRank();
            rank3 = cards.get(0).getRank();
            fullHouse = true;
        }
        return fullHouse;
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
        return
                cards.get(0).getRank() == cards.get(1).getRank() -1 &&
                cards.get(0).getRank() == cards.get(2).getRank() -2 &&
                cards.get(0).getRank() == cards.get(3).getRank() -3 &&
                cards.get(0).getRank() == cards.get(4).getRank() -4;
    }

    private boolean isThreeOfAKind() {
        int three = isNumberOfAKind(cards, 3);
        if (three != 0){
            rank2 = three;
            return true;
        }
        else return false;
    }

    private int numberOfPairs(){
        int pairs = 0, temp = 0;
        for (int i = 0; i<4; i++){
            temp = isNumberOfAKind(cards.subList(i, i+2), 2);
            if (temp != 0) {
                pairs++;
                rank2 = temp;
            }
        }
        return pairs;
    }

    private int isNumberOfAKind(List<Card> cards, int number) {

        for(int rank=2; rank<=14; rank++) {
            int count = 0, temp=0;
            for (int i = 0; i < cards.size(); i++){
                if (cards.get(i).getRank()==rank) {
                    temp=cards.get(i).getRank();
                    count++;
                }
            }
            if (count==number) {
                return temp;
            }
        }
        return 0;
    }

    public int getRank(){
        return rank;
    }

    public Player getPlayer(){
        return player;
    }
}
