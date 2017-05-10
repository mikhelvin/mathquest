package sfu.ca.group3mathematicsapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

// FILE: SplashActivity.java

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DESCRIPTION:
 *
 * SPLASH SCREEN FOR MATH QUEST. USED TO LOAD AUDIO AND INITIALIZE SHARED PREFS.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class SplashActivity extends Activity {

    // ANDROID VARS
    public static Context splashCTX;
    public static MediaPlayer mp; // MEDIA PLAYERS MUST BE PUBLIC IN ORDER TO ACCESS THEM IN OTHER ACTIVITIIES
    public static MediaPlayer sfx;

    private Intent mainMenuIntent;

    private SharedPreferences SP, SMP;
    private SharedPreferences.Editor editor, musiceditor;
    private TextView tv;

    // STRING VARS
    private static String playing;

    // PRIMITIVES
    private static boolean showSplash;
    private static boolean sfxDisabled;
    private static float vol;
    private static int splashTime = 750; // in milliseconds
    private int dots;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        preferenceSetup();
        initializePrimitives();
        initializeAudio();

        // SET UP INTENT TO MAIN MENU
        mainMenuIntent = new Intent(getApplicationContext(), MainActivity.class);
        mainMenuIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);

        if (showSplash) {
            splashTransition(mainMenuIntent);
        } else {
            startActivity(mainMenuIntent);
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            finish();
        }
    }

    private void preferenceSetup() {
        SP = getSharedPreferences("storyModeData", Context.MODE_PRIVATE);
        editor = SP.edit();

        if (SP.contains("relaunchPref")) {
            if (SP.getInt("relaunchPref", -1) == 0) {
                // DO NOT TOUCH PREFS
            }
        } else {
            // we initialize new prefs
            int storyInstance = -1;
            int correctCount = 1;
            int questionNumber = -2;
            int lives = 3;

            editor.putInt("livesPref", lives); // default 3
            editor.putInt("storyInstancePref", storyInstance); // default -1
            editor.putInt("questionNumberPref", questionNumber); // default -2
            editor.putInt("correctCountPref", correctCount); // default 1
            editor.putInt("relaunchPref", 1); // default 1
            editor.commit();
        }
        // MUSIC PREFERENCES
        SMP = getSharedPreferences("bgmData", Context.MODE_PRIVATE);
        musiceditor = SMP.edit();

        if (SP.contains("bgm")) {

            String title = SP.getString("bgm", "nothinghere");

            if (title.equals("theme") || title.equals("stranded")) {

                // DO NOTHING
            } else {
                // SET TO DEFAULT, THEME
                musiceditor.putString("bgm", "theme");
                musiceditor.commit();
            }
        }


        return;
    }

    private void initializePrimitives() {
        dots = 0;
        showSplash = true; // IF SET TO FALSE, SOUNDS AND PREFS SHOULD STILL BE LOADED

        splashCTX = getBaseContext();

        vol = 1.0f; // DEFAULT VOLUME OF MUSIC
        playing = null;
    }

    private void initializeAudio() {
        if (setupMusicPlayer() != 0) {
            Toast.makeText(this, "Music disabled!", Toast.LENGTH_SHORT).show();
        } else { // ASSUMING MUSIC PLAYER FUNCTIONS, PLAY THE MATH QUEST THEME
            changeTheme(SMP.getString("bgm", "stranded"));
        }

        if (setupSFXPlayer() != 0) {
            Toast.makeText(this, "SFX disabled!", Toast.LENGTH_LONG).show();
            sfxDisabled = true;
        } else {
            sfxDisabled = false;
        }
    }

    private void splashTransition(final Intent intent) {
        tv = (TextView) findViewById(R.id.LoadText);

        Handler updateHandler = new Handler();

        int i = 0;
        int loadTimeStart = 250; // CHANGE THIS TO HAVE DOTS APPEAR FASTER
        int loadTimeIncrement = loadTimeStart;

        int toLoad = 50;

        while (i < toLoad) {
            updateHandler.postDelayed(updateText, loadTimeStart);
            loadTimeStart += loadTimeIncrement;
            i++;
        }
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                startActivity(intent);
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                finish();
            }

        }, splashTime);
    }

    private final Runnable updateText = new Runnable() {
        public void run() {
            // loopable function to display dots.
            if (dots >= 3) {
                tv.setText(getString(R.string.Loading));
                dots = 0;
            } else {
                tv.append(".");
                dots++;
            }
        }
    };

    private int setupMusicPlayer() {
        mp = MediaPlayer.create(SplashActivity.this, R.raw.mathquesttheme); // new mediaplayer to handle background music
        mp.setVolume(vol, vol);
        mp.setLooping(true); // SET MUSIC TO LOOP

        if (mp == null) {
            return -1;
        }

        if (!mp.isPlaying()) {
            mp.start();
        }

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release();
            }
        });
        return 0;
    }

    private int setupSFXPlayer() {
        sfx = MediaPlayer.create(SplashActivity.this, R.raw.buttonsfx2); // new mediaplayer that plays button click sounds
        sfx.setVolume(1.0f, 1.0f); // 1.0f is full volume
        if (sfx == null) {
            return -1; // if error creating sfxplayer, return -1
        }
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                mp.release(); // RELEASE EACH SFX IN ORDER TO PREVENT RESOURCE LEAKS
            }
        });
        return 0; // return 0 if allocation of sfxplayer was successful
    }

    public static void playSFX() {
        if (!sfxDisabled) { // only play SFX if sfxplayer is working
            if (sfx.isPlaying()) {
                sfx.stop();
                sfx.release();
                sfx = MediaPlayer.create(splashCTX, R.raw.buttonsfx2);
            }
            sfx.start();
        }
    }

    public static int changeTheme(String title) {
        if (mp == null) {
            return -1;
        } else {
            mp.release();
            if (title.equals("stranded")) {
                mp = MediaPlayer.create(splashCTX, R.raw.bgmstranded);
                playing = "stranded";
            } else if (title.equals("journey")) {
                mp = MediaPlayer.create(splashCTX, R.raw.journey);
                playing = "journey";
            } else {
                mp = MediaPlayer.create(splashCTX, R.raw.mathquesttheme);
                playing = "theme";
            }
            mp.setVolume(vol, vol);
            mp.setLooping(true);
            if (!mp.isPlaying()) {
                mp.start();
            }
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.release();
                }
            });
            return 0;
        }
    }

    public static String checkPlaying() {
        return playing;
    }

}
