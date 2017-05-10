package sfu.ca.group3mathematicsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;


// FILE: MainActivity.java

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DESCRIPTION:
 *
 * MAIN MENU FOR THE APP. HANDLES MUSIC, SFX, AND NAVIGATION FOR THE USER.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class MainActivity extends AppCompatActivity {

    // ANDROID VARS
    private Button b, sb, ib, settingsButton;
    private static Button startResumeButton, scoreResumeBtn;
    private Fragment f;

    private SharedPreferences SP;
    private SharedPreferences.Editor editor;

    // PRIMITIVES
    private boolean settingsActive;
    private static boolean resumeStory, resumeScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializePrimitives();

        loadPreferences();

        setupStartButton();
        setupScoreGameButton();
        setupStudyButton();
        setupInstructionsButton();
        setupSettingsButton();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        updateButtons();
    }

    private void initializePrimitives() {
        resumeStory = false;
        resumeScore = false;
        settingsActive = false;
    }

    private void loadPreferences() {
        SP = getSharedPreferences("storyModeData", Context.MODE_PRIVATE);
        editor = SP.edit();

        if (SP.contains("questionNumberPref")) {
            if (SP.getInt("questionNumberPref", -1) >= 0) {
                resumeStory = true;
                updateButtons();
                resumeStory = false;
            }
        } else {
            resumeStory = false;
        }
    }

    public void setupStartButton() {
        startResumeButton = (Button) findViewById(R.id.startBtn);

        startResumeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                closeSettingsFragment();
                SplashActivity.playSFX();
                if (resumeStory) {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                    MainActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, GameActivity.class);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK); // TODO: DELETE DEBUG CODE
                    toggleResumeStory(true);
                    MainActivity.this.startActivity(intent);
                }

            }
        });
    }

    private void setupScoreGameButton() {
        scoreResumeBtn = (Button) findViewById(R.id.ScoreModeBtn);

        scoreResumeBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                closeSettingsFragment();
                SplashActivity.playSFX();
                if (resumeScore) {
                    Intent intent = new Intent(MainActivity.this, ScoreGameActivity.class);
                    intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
                    MainActivity.this.startActivity(intent);
                } else {
                    Intent intent = new Intent(MainActivity.this, ScoreGameActivity.class);
                    toggleResumeScore(true);
                    MainActivity.this.startActivity(intent);
                }
            }
        });
    }

    private void updateButtons() {
        startResumeButton = (Button) findViewById(R.id.startBtn);
        if (resumeStory) {
            startResumeButton.setText(R.string.resumeNav);
        } else {
            startResumeButton.setText(R.string.StartNav);
        }

        scoreResumeBtn = (Button) findViewById(R.id.ScoreModeBtn);
        if (resumeScore) {
            scoreResumeBtn.setText(R.string.scoreResumeNav);
        } else {
            scoreResumeBtn.setText(R.string.ScoreModeNav);
        }

    }

    private void setupStudyButton() {
        sb = (Button) findViewById(R.id.studyBtn);

        sb.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                closeSettingsFragment();
                SplashActivity.playSFX();
                Intent intent = new Intent(MainActivity.this, StudyActivity.class);
                intent.putExtra("origin", "main");
                MainActivity.this.startActivity(intent);
            }
        });
    }

    private void setupInstructionsButton() {
        ib = (Button) findViewById(R.id.instructionsBtn);

        ib.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                closeSettingsFragment();
                SplashActivity.playSFX();
                Intent intent = new Intent(MainActivity.this, InstructionsActivity.class);
                MainActivity.this.startActivity(intent);
            }
        });
    }


    private void setupSettingsButton() {
        settingsButton = (Button) findViewById(R.id.settingsBtn);

        settingsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SplashActivity.playSFX();
                if (!settingsActive) {
                    settingsActive = true;
                    settingsButton.setText(getString(R.string.closeSettingsNav));
                    // SETTINGS FRAGMENT
                    f = new SettingsFragment();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    ft.replace(R.id.menu_fragment, f);
                    ft.commit();
                } else {
                    settingsActive = false;
                    settingsButton.setText(getString(R.string.SettingsNav));
                    // SETTINGS FRAGMENT
                    f = new LogoFragment();
                    FragmentManager fm = getFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();

                    ft.replace(R.id.menu_fragment, f);
                    ft.commit();
                }

            }
        });
    }

    private void closeSettingsFragment() {
        if (settingsActive) {
            settingsActive = false;
            settingsButton.setText(getString(R.string.SettingsNav));
            // SETTINGS FRAGMENT
            f = new LogoFragment();
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();

            ft.replace(R.id.menu_fragment, f);
            ft.commit();
        }
    }

    public static void resetStartButtonUI() {
        // TODO: FIX STATIC ISSUES
        // startResumeButton = (Button) findViewById(R.id.startBtn);
        // startResumeButton.setText(R.string.StartNav);
    }

    public static void toggleResumeStory(boolean bool) {
        resumeStory = bool;
    }

    public static void toggleResumeScore(boolean bool) {
        resumeScore = bool;
    }
}
