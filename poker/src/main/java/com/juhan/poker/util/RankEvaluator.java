package com.juhan.poker.util;

import android.util.Log;
import com.juhan.poker.model.Card;
import com.juhan.poker.model.Card.Suits;
import com.juhan.poker.model.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class can be used to evaluate the strength of a 5-card draw poker hand.
 *
 * Created by Juhan Klementi on 29.09.2014.
 */

//TODO: algorithm testing & improvement
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
        Collections.sort(cards);
        evaluate();
        Log.d("RankEvaluator:", "rank: " + rank + "(" + ranks[rank] + "), rank2:" + rank2 + ", rank3:" + rank3);
    }

    /**
     * Evaluates the hand by checking for different poker combinations.
     *
     * @return int of the hand rank from 0 - 9, 0 being the best and 9 being the worst.
     */
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

    /**
     * Compares hand strength to another hand. If ranks are equal, further comparing needs to be done.
     * @param another
     * @return RankEvaluator of winning hand
     */
    public RankEvaluator compareRanks(RankEvaluator another){
        if (this.rank < another.rank) return this;
        if (this.rank > another.rank) return another;
        return compareDraw(another);
    }

    /**
     * Compares to hands when ranks are equal. For example, high cards are checked for a kicker
     * and straights are checked, which is the highest.
     * @param another Rank to be compared against.
     * @return RankEvaluator of the winning hand, null if hands are equal (no winner)
     */
    private RankEvaluator compareDraw(RankEvaluator another){
        if (rank != another.rank)
            throw new RuntimeException("This should have not happened!");

        if (Arrays.asList(0,1,4,5).contains(rank)){
            return
                    Collections.max(cards).getRank() >
                    Collections.max(another.cards).getRank() ? this : another;
        } else {
            if (rank2 != another.rank2)
                return rank2 > another.rank2 ? this : another;
            if (rank3 != another.rank3)
                return rank3 > another.rank3 ? this : another;

            ArrayList<Card> temp = new ArrayList<>(cards);
            ArrayList<Card> anotherTemp = new ArrayList<>(another.cards);
            temp.removeAll(Arrays.asList(rank2, rank3));
            anotherTemp.removeAll(Arrays.asList(rank2, rank3));

            for (int i = temp.size()-1; i>=0; i--){
                if (temp.get(i).getRank() != anotherTemp.get(i).getRank())
                    return temp.get(i).getRank() > anotherTemp.get(i).getRank() ? this : another;
            }
            // card ranks are identical
            return null;

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

    private boolean isStraight(){
        return
                (cards.get(0).getRank() == cards.get(1).getRank() -1 &&
                cards.get(0).getRank() == cards.get(2).getRank() -2 &&
                cards.get(0).getRank() == cards.get(3).getRank() -3 &&
                cards.get(0).getRank() == cards.get(4).getRank() -4) ||
                (cards.get(0).getRank() == 2 && cards.get(1).getRank() == 3 &&
                cards.get(2).getRank() == 4 && cards.get(3).getRank() == 5 &&
                cards.get(4).getRank() == 14);
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
