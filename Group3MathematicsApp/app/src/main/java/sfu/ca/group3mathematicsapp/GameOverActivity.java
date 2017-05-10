package sfu.ca.group3mathematicsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

// FILE: GameOverActivity.java

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DESCRIPTION:
 *
 * GAME OVER SCREEN THAT USES INTENT EXTRA TO DETERMINE WHETHER THIS
 * IS A WIN OR A LOSE SCREEN.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class GameOverActivity extends AppCompatActivity {

    // ANDROID VARS
    private Button b;
    private TextView title, subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        changeText();

        setupRetryButton();
    }

    @Override
    public void onBackPressed() {
        SplashActivity.playSFX();
        MainActivity.toggleResumeStory(false);
        Intent intent = new Intent(GameOverActivity.this, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
        GameOverActivity.this.startActivity(intent);
    }

    private void changeText() {
        title = (TextView) findViewById(R.id.GameOverTextView);
        subtitle = (TextView) findViewById(R.id.GameOverInfoTextView);

        b = (Button) findViewById(R.id.retryBtn);

        String result = getIntent().getStringExtra("gameResult");

        // CHANGE TEXT TO MATCH USER'S COMPLETION OF THE GAME
        if (result.equals("WIN")) {
            title.setText( getString(R.string.YouWinLarge));
            subtitle.setText( getString(R.string.YouWinInfo));
            b.setText( getString(R.string.playAgainNav));
        } else {
            title.setText( getString(R.string.GameOverLarge));
            subtitle.setText( getString(R.string.GameOverInfo));
            b.setText( getString(R.string.retryNav));
        }

    }

    private void setupRetryButton() {
        b = (Button) findViewById(R.id.retryBtn);

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SplashActivity.playSFX();
                Intent intent = new Intent(GameOverActivity.this, GameActivity.class);
                GameOverActivity.this.startActivity(intent);
            }
        });
    }



}
