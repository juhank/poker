package com.juhan.poker.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * Created by Juhan Klementi on 28.09.2014.
 */
public class Player extends Observable {
    private String name;
    protected int chips;
    private List<Card> cards;
    private Context context;

    public Player(String name, Context context){
        this.name = name;
        this.chips = Game.STARTING_CHIPS;
        this.cards = new ArrayList<Card>();
        this.context = context;
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
        if (this.chips + chips < 0)
            throw new RuntimeException("ERROR: Bet was larger than Chip amount!");
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

    public List<Drawable> getDrawables(){

        List<Drawable> cardPics = new ArrayList<Drawable>();

        for (Card card : getCards()){
            cardPics.add(context.getResources().getDrawable(context
                    .getResources()
                    .getIdentifier(card.getSuit().name().substring(0, 1).toLowerCase() + card.getRank(),
                            "drawable", context.getPackageName())));
        }
        return cardPics;

    }
}
