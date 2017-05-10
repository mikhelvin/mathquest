package sfu.ca.group3mathematicsapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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

public class ScoreGameOverActivity extends AppCompatActivity {

    // ANDROID VARS
    private Button b;
    private TextView title, subtitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_game_over);

        changeText();

        setupRetryButton();
    }

    @Override
    public void onBackPressed() {
        SplashActivity.playSFX();
        MainActivity.toggleResumeScore(false);
        Intent intent = new Intent(ScoreGameOverActivity.this, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
        ScoreGameOverActivity.this.startActivity(intent);
    }

    private void changeText() {
        title = (TextView) findViewById(R.id.ScoreScreenTitle);
        subtitle = (TextView) findViewById(R.id.ScoreScreenSubtitle);

        b = (Button) findViewById(R.id.ScorePlayAgainButton);

        Bundle e = getIntent().getExtras();
        String correct = e.getString("correct");
        String total = e.getString("total");

        title.setText(getString(R.string.FinalScoreTitle, correct, total));
        subtitle.setText(getString(R.string.FinalScoreSubtitle, correct, total));
        b.setText(getString(R.string.playAgainNav));
    }


    private void setupRetryButton() {
        b = (Button) findViewById(R.id.ScorePlayAgainButton);

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SplashActivity.playSFX();
                Intent intent = new Intent(ScoreGameOverActivity.this, ScoreGameActivity.class);
                ScoreGameOverActivity.this.startActivity(intent);
            }
        });
    }


}
