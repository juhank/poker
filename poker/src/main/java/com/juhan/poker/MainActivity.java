package com.juhan.poker;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.*;
import android.widget.TextView;
import com.juhan.poker.model.Dealer;
import com.juhan.poker.model.Game;
import com.juhan.poker.model.Player;
import com.juhan.poker.util.RankEvaluator;
import org.w3c.dom.Text;

import java.util.Observable;
import java.util.Observer;


public class MainActivity extends ActionBarActivity implements Observer {

    private Handler mHandler;
    private Game game;
    private Dealer dealer;
    private Player player;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        dealer = new Dealer("Peedu");
        dealer.addObserver(this);
        player = new Player("Juhan");
        dealer.addObserver(this);

        mHandler = new Handler(Looper.getMainLooper()) {

            @Override
            public void handleMessage(Message msg) {
                ((TextView)findViewById(R.id.pot)).setText("POT: " + game.getPot());
                if (msg.what == game.GAME_STARTING) {
                    Log.d("UI", "Game starting");
                }
                if (msg.what == game.WAITING_PLAYER) {
                    Log.d("UI", "Waiting player input");
                    TextView tv = (TextView)findViewById(R.id.playerView);
                    tv.setText(player.getCardsString());
                    togglePlayerButtons(true);
                }
                if (msg.what == game.GAME_OVER) {
                    Log.d("UI:", "Game Over");
                }
                if (msg.what == game.EVALUATION) {

                    Bundle bundle = msg.getData();
                    int dealerRank = bundle.getInt("dealerRank");
                    Log.d("UI", "Dealer has:" + RankEvaluator.ranks[dealerRank]);
                    int playerRank = bundle.getInt("playerRank");
                    Log.d("UI", "Player has:" + RankEvaluator.ranks[playerRank]);
                    String winText = bundle.getString("winText");
                    ((TextView)findViewById(R.id.pot)).setText(winText);

                    TextView tv = (TextView) findViewById(R.id.dealerView);
                    tv.setText(dealer.getCardsString());

                    findViewById(R.id.newHandButton).setVisibility(View.VISIBLE);

                }
            }
        };
        game = new Game(player, dealer, mHandler);
        game.start();
    }


    @Override
    public void update(Observable observable, Object data) {

        TextView dealerChips = (TextView) findViewById(R.id.dealerChips);
        TextView playerChips = (TextView) findViewById(R.id.playerChips);

        if (dealerChips != null)
            dealerChips.setText(String.valueOf(dealer.getChips()));
        if (playerChips != null)
            playerChips.setText(String.valueOf(player.getChips()));

    }

    private void togglePlayerButtons(boolean visible) {
        findViewById(R.id.checkButton).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
        findViewById(R.id.betButton).setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
    }

    public void playerAction(View view) {
        if (view.getId() == R.id.checkButton) {

        }
        if (view.getId() == R.id.betButton) {
            player.addChips(-1);
            game.setPot(game.getPot() + 1);
        }
        togglePlayerButtons(false);
        game.setState(Game.WAITING_DEALER);
        notifyGameThread();
    }

    public void newHand(View view) {
        view.setVisibility(View.INVISIBLE);
        ((TextView) findViewById(R.id.playerView)).setText("");
        ((TextView) findViewById(R.id.dealerView)).setText("");
        game.setState(Game.GAME_STARTING);
        notifyGameThread();
    }

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
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {


        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            ((TextView)rootView.findViewById(R.id.dealerChips)).setText(String.valueOf(Game.STARTING_CHIPS));
            ((TextView)rootView.findViewById(R.id.playerChips)).setText(String.valueOf(Game.STARTING_CHIPS));
            return rootView;
        }

    }
}
