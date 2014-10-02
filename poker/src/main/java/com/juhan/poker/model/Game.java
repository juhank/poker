package com.juhan.poker.model;

import android.os.Handler;
import android.os.Message;
import com.juhan.poker.util.GameThread;

/**
 * Created by Juhan Klementi on 28.09.2014.
 */
public class Game {

    public static final int STARTING_CHIPS = 20;
    public static final int GAME_STARTING = 0;
    public static final int WAITING_DEALER = 1;
    public static final int WAITING_PLAYER = 2;
    public static final int GAME_OVER = 3;
    public static final int EVALUATION = 4;

    private int state;
    private int pot;
    private Thread game;
    private Handler handler;

    public Game(Player player, Dealer dealer, Handler handler) {
        this.handler = handler;
        this.state = GAME_STARTING;
        this.pot = 0;
        game = new Thread(new GameThread(this, player, dealer));
    }

    public void start() {
        game.start();
    }

    public int getState(){
        return state;
    }

    public void setState(int state) {
        setState(state, null);
    }

    public void setState(int state, Message message) {
        if (message == null)
            handler.sendEmptyMessage(state);
        else {
            message.what = state;
            handler.sendMessage(message);
        }
        this.state = state;
    }

    public int getPot() {
        return pot;
    }

    public void setPot(int pot) {
        this.pot = pot;
    }


}
