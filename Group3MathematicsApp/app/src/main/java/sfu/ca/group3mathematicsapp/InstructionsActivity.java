package sfu.ca.group3mathematicsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

// FILE: InstructionsActivity.java

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DESCRIPTION:
 *
 * INSTRUCTIONS SCREEN THAT PROVIDES USER WITH INFO ON HOW TO PLAY
 * MATH QUEST.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class InstructionsActivity extends AppCompatActivity {

    private Button b;
    private TextView t1, t2, t3, t4, t5, t6, t7, t8;

    private boolean about;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        initializeViews();
        setInstructionsText();
        setupAboutButton();
    }


    private void initializeViews() {
        t1 = (TextView) findViewById(R.id.t1);
        t2 = (TextView) findViewById(R.id.t2);
        t3 = (TextView) findViewById(R.id.t3);
        t4 = (TextView) findViewById(R.id.t4);
        t5 = (TextView) findViewById(R.id.t5);
        t6 = (TextView) findViewById(R.id.t6);
        t7 = (TextView) findViewById(R.id.t7);
        t8 = (TextView) findViewById(R.id.t8);

        b = (Button) findViewById(R.id.aboutButton);
    }

    private void setAboutText() {
        t1.setText(getString(R.string.about1));
        t2.setText(getString(R.string.about2));
        t3.setText(R.string.about3);
        t4.setText(R.string.about4);
        t5.setText("");
        t6.setText(getString(R.string.about5));
        t7.setText("");
        t8.setText("");
        b.setText(getString(R.string.InstructionsNav));
        about = true;
    }

    private void setInstructionsText() {
        t1.setText(getString(R.string.welcome));
        t2.setText(getString(R.string.instructions));
        t3.setText(getString(R.string.instructions2));
        t4.setText(getString(R.string.importantInst));
        t5.setText(getString(R.string.exHeading));
        t6.setText(getString(R.string.exQ));
        t7.setText(getString(R.string.exInput));
        t8.setText(getString(R.string.exAnsw));
        b.setText(getString(R.string.AboutNav));
        about = false;
    }

    private void setupAboutButton() {
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SplashActivity.playSFX();
                if (about) {
                    setInstructionsText();
                } else {
                    setAboutText();
                }
            }
        });
    }


    @Override
    public void onBackPressed() {
        SplashActivity.playSFX();
        Intent intent = new Intent(InstructionsActivity.this, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
        InstructionsActivity.this.startActivity(intent);
    }
}
