package com.juhan.poker.model;

import android.content.Context;
import android.graphics.drawable.Drawable;
import com.juhan.poker.R;

import java.util.List;

/**
 * Created by juhan_000 on 28.09.2014.
 */
public class Card implements Comparable {

    private Context context;

    public enum Suits {
        SPADES,
        HEARTS,
        DIAMONDS,
        CLUBS;
    }
    public static String [] faces = new String[]{"2", "3", "4", "5", "6",
            "7", "8", "9", "10", "Jack", "Queen", "King", "Ace"};
    public static int [] ranks = new int[]{2,3,4,5,6,7,8,9,10,11,12,13,14};
    private Suits suit;
    private int rank;

    public Card (Suits suit, int rank){
        this.suit = suit;
        this.rank = rank;
    }

    @Override
    public int compareTo(Object another) {
        Card card = (Card)another;
        return this.rank < card.rank ? -1 : (this.rank == card.rank ? 0 : 1);
    }

    public int getRank() {
        return rank;
    }

    public String getFace() {
        return faces[rank-2];
    }

    public Suits getSuit(){
        return suit;
    }
}
