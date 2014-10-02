package com.juhan.poker;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.widget.Button;
import android.widget.TextView;
import com.juhan.poker.model.Dealer;
import com.juhan.poker.model.Game;
import com.juhan.poker.model.Player;
import com.juhan.poker.util.RankEvaluator;
import com.juhan.poker.view.CardView;

import java.util.Observable;
import java.util.Observer;


/**
 * Main entry point for the 5-card draw poker application
 *
 * Created by Juhan Klementi on 28.09.2014.
 */
public class MainActivity extends ActionBarActivity implements Observer {

    private Handler mHandler;
    private Game game;
    private Dealer dealer;
    private Player player;

    //TODO: Fragments for different screen sizes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new GameFragment())
                    .commit();
        }

        dealer = new Dealer("Peedu", this);
        dealer.addObserver(this);
        player = new Player("Juhan", this);
        dealer.addObserver(this);

        mHandler = new Handler(Looper.getMainLooper()) {

            // handles messages from background tasks and updates the UI.
            @Override
            public void handleMessage(Message msg) {

                if (msg.what == game.WAITING_PLAYER) {
                    ((TextView)findViewById(R.id.pot)).setText("POT: " + game.getPot());
                    Log.d("UI", "Waiting player input");
                    CardView view = (CardView)findViewById(R.id.playerView);
                    view.setImages(player.getDrawables());
                    togglePlayerButtons(true);
                }
                if (msg.what == game.EVALUATION) {
                    Bundle bundle = msg.getData();
                    int dealerRank = bundle.getInt("dealerRank");
                    Log.d("UI", "Dealer has:" + RankEvaluator.ranks[dealerRank]);
                    int playerRank = bundle.getInt("playerRank");
                    Log.d("UI", "Player has:" + RankEvaluator.ranks[playerRank]);
                    String winText = bundle.getString("winText");

                    CardView view = (CardView)findViewById(R.id.dealerView);
                    view.setImages(dealer.getDrawables());

                    if (!bundle.containsKey("winner")) {
                        ((TextView)findViewById(R.id.pot)).setText(winText);
                        findViewById(R.id.newHandButton).setVisibility(View.VISIBLE);
                    }
                    else {
                        ((TextView)findViewById(R.id.pot)).setText("GAME OVER: " + bundle.getString("winner") + " wins");
                        game.setState(Game.GAME_OVER);
                        notifyGameThread();
                    }
                }
            }
        };

        game = new Game(player, dealer, mHandler);
        game.start();
    }

    // data changes are observed to display chip count
    @Override
    public void update(Observable observable, Object data) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView dealerChips = (TextView) findViewById(R.id.dealerChips);
                TextView playerChips = (TextView) findViewById(R.id.playerChips);

                if (dealerChips != null)
                    dealerChips.setText(String.valueOf(dealer.getChips()));
                if (playerChips != null)
                    playerChips.setText(String.valueOf(player.getChips()));

            }
        });
    }

    // hides buttons when not needed
    private void togglePlayerButtons(boolean visible) {
        findViewById(R.id.checkButton).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.betButton).setVisibility(visible && player.getChips() > 0 ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.betAmountButton).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    // player made a bet choice
    public void playerAction(View view) {
        if (view.getId() == R.id.betButton) {
            int b = Integer.valueOf(((Button)findViewById(R.id.betAmountButton)).getText().toString());
            player.addChips(-1 * b);
            game.setPot(game.getPot() + b);
        }
        togglePlayerButtons(false);
        game.setState(Game.WAITING_DEALER);
        notifyGameThread();
    }

    // TODO: if dealer has only 1 chip left, the bet amount button should show 1
    // increase the bet value by increment of 2
    public void changeBet(View view) {
        if (view.getId() == R.id.betAmountButton) {
            int betAmount = Integer.valueOf (((Button)view).getText().toString());
            if (player.getChips() >= betAmount + 2 && dealer.getChips() >= betAmount + 2){
                ((Button) view).setText(String.valueOf(betAmount + 2));
            } else ((Button) view).setText(String.valueOf(2));
        }
    }

    // starts a new hand
    public void newHand(View view) {
        view.setVisibility(View.INVISIBLE);
        ((CardView) findViewById(R.id.playerView)).resetImages();
        ((CardView) findViewById(R.id.dealerView)).resetImages();
        ((Button) findViewById(R.id.betAmountButton)).setText(String.valueOf(2));
        game.setState(Game.GAME_STARTING);
        notifyGameThread();
    }

    // notifies the GameThread after game state has changed
    private void notifyGameThread() {
        synchronized (game) {
            game.notify();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A fragment containing the poker game.
     */
    public static class GameFragment extends Fragment {

        public GameFragment() {}

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ((TextView)rootView.findViewById(R.id.dealerChips)).setText(String.valueOf(Game.STARTING_CHIPS));
            ((TextView)rootView.findViewById(R.id.playerChips)).setText(String.valueOf(Game.STARTING_CHIPS));
            return rootView;
        }
    }
}
