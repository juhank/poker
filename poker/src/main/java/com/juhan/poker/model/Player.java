package com.juhan.poker.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by juhan_000 on 28.09.2014.
 */
public class Player extends Observable {
    private String name;
    private int chips;
    private List<Card> cards;

    public Player(String name){
        this.name = name;
        this.chips = Game.STARTING_CHIPS;
        this.cards = new ArrayList<Card>();
    }

    public void giveCard(Card card) {
        cards.add(card);
        if (this.getClass().isInstance(Player.class)){
            Log.d("Player got card:", " " + card.getRank() + card.getSuit());
        }
    }

    public int getChips(){
        return chips;
    }

    public void addChips(int chips) {
        setChanged();
        this.chips += chips;
        notifyObservers();
        Log.d("Player", "Chipcount changed to " + this.chips);
    }
    public String getName(){
        return name;
    }

    public List<Card> getCards(){
        return cards;
    }

    public void resetCards() {
        cards.removeAll(cards);
    }

    public String getCardsString(){
        StringBuilder stringBuilder = new StringBuilder();
        for (Card card : cards) {
            stringBuilder.append(card.getFace());
            stringBuilder.append(" of ");
            stringBuilder.append(card.getSuit());
            stringBuilder.append(", ");
        }
        return stringBuilder.substring(0, stringBuilder.length()-2);
    }
}
