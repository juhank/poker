package com.juhan.poker.model;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import com.juhan.poker.util.RankEvaluator;

/**
 * Created by Juhan Klementi on 28.09.2014.
 */
public class Game {

    private final Player player;
    private final Dealer dealer;

    public static final int STARTING_CHIPS = 20;
    public static final int GAME_STARTING = 0;
    public static final int WAITING_DEALER = 1;
    public static final int WAITING_PLAYER = 2;
    public static final int GAME_OVER = 3;
    public static final int EVALUATION = 4;
    public static final int PAUSE = 5;

    private int state;
    private Thread game;
    private int pot;
    private Handler handler;

    public Game(Player player, Dealer dealer, Handler handler) {
        this.player = player;
        this.dealer = dealer;
        this.handler = handler;
        state = GAME_STARTING;
        pot = 0;
        game = new Thread(new GameThread(this));
    }

    public void start() {
        if (game != null)
            game.start();
    }

    public synchronized void setState(int state){
        setState(state, null);
    }

    public synchronized void setState(int state, Message message) {
        if(message == null)
            handler.sendEmptyMessage(state);
        else{
            message.what = state;
            handler.sendMessage(message);
        }
        this.state = state;
    }

    public synchronized int getPot() {
        return pot;
    }

    public synchronized void setPot(int pot) {

        this.pot = pot;
    }

    private class GameThread implements Runnable {

        Game game;

        GameThread(Game game) {
            this.game = game;

        }

        @Override
        public void run() {
            while (game.state != PAUSE) {
                synchronized (game) {
                    try {
                        if (game.state == GAME_STARTING) {
                            dealer.shuffle();
                            dealer.resetCards();
                            player.resetCards();
                            dealer.dealCards(player);
                            dealer.dealCards(dealer);
                            dealer.addChips(-1);
                            player.addChips(-1);
                            setPot(2);
                            game.setState(WAITING_PLAYER);
                            game.wait();
                        }
                        if (game.state == WAITING_DEALER) {
                            if (pot != 2) {
                                dealer.addChips(-1);
                                setPot(getPot() + 1);
                            }

                            RankEvaluator dealerRank = new RankEvaluator(dealer);
                            RankEvaluator playerRank = new RankEvaluator(player);

                            RankEvaluator winner = dealerRank.compareRanks(playerRank);
                            dealer.distributePot(pot, new Player[]{winner.getPlayer()});

                            Message message = new Message();
                            Bundle bundle = new Bundle();
                            bundle.putInt("dealerRank", dealerRank.getRank());
                            bundle.putInt("playerRank", playerRank.getRank());
                            bundle.putString("winText",
                                    winner.getPlayer().getName() +
                                    " wins pot of " + game.getPot() + " with " +
                                    RankEvaluator.ranks[winner.getRank()]);
                            message.setData(bundle);

                            game.setState(EVALUATION, message);
                            game.wait();
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
