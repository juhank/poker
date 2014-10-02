package com.juhan.poker.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.juhan.poker.R;
import com.juhan.poker.model.Card;

import java.util.List;

/**
 * A View consisting of 5 cards side-by-side contained in ImageViews.
 *
 * Created by Juhan Klementi on 1.10.2014.
 */
public class CardView extends LinearLayout {
    public CardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.view_cardview, this, true);
    }

    // sets the ImageViews' drawables
    public void setImages(List<Drawable> cards) {
        LinearLayout view = (LinearLayout)findViewById(R.id.linearLayout_cards);
        for(int i = 0 ; i < view.getChildCount(); i++){
            ImageView v = (ImageView)view.getChildAt(i);
            v.setImageDrawable(cards.get(i));
        }
    }

    // resets the images before new round
    public void resetImages(){
        LinearLayout view = (LinearLayout)findViewById(R.id.linearLayout_cards);
        for(int i = 0 ; i < view.getChildCount(); i++){
            ImageView v = (ImageView)view.getChildAt(i);
            v.setImageDrawable(getResources().getDrawable(R.drawable.b2fv));
        }
    }
}
