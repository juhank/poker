package com.juhan.poker.util;

import android.os.Bundle;
import android.os.Message;
import com.juhan.poker.model.Dealer;
import com.juhan.poker.model.Game;
import com.juhan.poker.model.Player;
import static com.juhan.poker.model.Game.*;

public class GameThread implements Runnable {

    private Game game;
    private Dealer dealer;
    private Player player;

    public GameThread(Game game, Player player, Dealer dealer) {
        this.game = game;
        this.player = player;
        this.dealer = dealer;
    }

    @Override
    public void run() {
        while (game.getState() != GAME_OVER) {
            synchronized (game) {
                try {
                    if (game.getState() == GAME_STARTING) {
                        dealer.shuffle();
                        dealer.resetCards();
                        player.resetCards();
                        dealer.dealCards(player);
                        dealer.dealCards(dealer);
                        dealer.addChips(-1);
                        player.addChips(-1);
                        game.setPot(2);
                        game.setState(WAITING_PLAYER);
                        game.wait();
                    }
                    if (game.getState() == WAITING_DEALER) {
                        if (game.getPot() != 2) {
                            int bet = game.getPot()-2;
                            if (bet > dealer.getChips()){
                                player.addChips(bet - dealer.getChips());
                                dealer.addChips(-1 * dealer.getChips());
                                game.setPot(game.getPot() - bet + 2*dealer.getChips());
                            } else {
                                dealer.addChips(-1 * bet);
                                game.setPot(game.getPot() + bet);
                            }
                        }

                        RankEvaluator dealerRank = new RankEvaluator(dealer);
                        RankEvaluator playerRank = new RankEvaluator(player);

                        RankEvaluator winner = dealerRank.compareRanks(playerRank);
                        Player[] winners = winner == null ? new Player[]{player, dealer} : new Player[]{winner.getPlayer()};
                        dealer.distributePot(game.getPot(), winners);

                        Message message = new Message();
                        Bundle bundle = new Bundle();
                        bundle.putInt("dealerRank", dealerRank.getRank());
                        bundle.putInt("playerRank", playerRank.getRank());
                        String winText = winner == null ?
                                "Split pot, both have " + RankEvaluator.ranks[playerRank.getRank()] :
                                winner.getPlayer().getName() + " wins pot of " + game.getPot() + " with " +
                                RankEvaluator.ranks[winner.getRank()];

                        bundle.putString("winText", winText);

                        if (dealer.getChips() == 0 || player.getChips() == 0) {
                            // TODO: distinguish by something else than name
                            bundle.putString("winner", winner.getPlayer().getName());
                        }
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