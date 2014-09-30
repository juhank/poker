package com.juhan.poker;

import com.juhan.poker.model.Card;
import com.juhan.poker.util.RankEvaluator;
import com.juhan.poker.model.Player;

public class TestRankEvaluator2 {
    public static void main(String[] args) {

        Player player = new Player("Juku");
        player.giveCard(new Card(Card.Suits.CLUBS, 7));
        player.giveCard(new Card(Card.Suits.DIAMONDS, 7));
        player.giveCard(new Card(Card.Suits.HEARTS, 10));
        player.giveCard(new Card(Card.Suits.HEARTS, 13));

        RankEvaluator eval = new RankEvaluator(player);
        System.out.println(RankEvaluator.ranks[eval.evaluate()]);
    }
}