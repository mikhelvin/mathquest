package sfu.ca.group3mathematicsapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

// FILE: SettingsFragment.java

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DESCRIPTION:
 *
 * GIVES USER ACCESS TO SOME SETTINGS FOR THE GAME, LIKE
 * TOGGLING THE MUSIC ON OR OFF.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class SettingsFragment extends Fragment {

    // ANDROID VARS
    private Button b, changeButton;
    private SharedPreferences SP, SMP;
    private SharedPreferences.Editor editor, musiceditor;
    private View fragView;

    // DECLARE ALERTS
    AlertDialog alert;
    AlertDialog.Builder builder;
    AlertDialog dialog;


    // PRIMITIVES
    private int len;

    public SettingsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragView = inflater.inflate(R.layout.fragment_settings, container, false);

        SP = this.getActivity().getSharedPreferences("storyModeData", Context.MODE_PRIVATE);
        editor = SP.edit();
        SMP = this.getActivity().getSharedPreferences("bgmData", Context.MODE_PRIVATE);
        musiceditor = SMP.edit();


        alertSetup();

        setupResetStoryButton();
        setupChangeMusicButton();
        setupMusicButton();

        updateSongText();

        return fragView;
    }

    private void alertSetup() {
        builder = new AlertDialog.Builder(getActivity());
        dialog = builder.create();

        alert = builder.create();
    }

    private void resetAlert() {
        builder.setMessage(R.string.AlertClearStory)
                .setTitle(R.string.ConfirmReset)
                .setPositiveButton(R.string.AlertPositive, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SplashActivity.playSFX();
                        Toast.makeText(getActivity(), getString(R.string.progressCleared), Toast.LENGTH_SHORT).show();
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

                        MainActivity.toggleResumeStory(false);
                        MainActivity.resetStartButtonUI();
                    }
                })
                .setNegativeButton(R.string.AlertNegative, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        SplashActivity.playSFX();
                        // DO NOTHING
                    }
                }).show();
    }

    private void setupMusicButton() {

        b = (Button) fragView.findViewById(R.id.musicButton);

        if (SplashActivity.mp.isPlaying()) {
            b.setText(getString(R.string.musicOn));
        } else {
            b.setText(getString(R.string.musicOff));
        }

        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SplashActivity.playSFX();
                if (SplashActivity.mp != null) {
                    if (SplashActivity.mp.isPlaying()) {
                        len = SplashActivity.mp.getCurrentPosition();
                        b.setText(getString(R.string.musicOff));
                        SplashActivity.mp.pause();
                    } else {
                        SplashActivity.mp.seekTo(len);
                        b.setText(getString(R.string.musicOn));
                        SplashActivity.mp.start();
                    }
                    updateSongText();
                }
            }
        });
    }


    private void setupChangeMusicButton() {


        changeButton = (Button) fragView.findViewById(R.id.changeMusicButton);

        updateSongText();

        changeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SplashActivity.playSFX();
                if (SplashActivity.mp != null) {
                    if (!SplashActivity.mp.isPlaying()) {
                        // DO NOTHING
                    } else {
                        if (SplashActivity.checkPlaying().equals("theme")) {
                            musiceditor.putString("bgm", "stranded"); // STRANDED
                            musiceditor.commit();
                            SplashActivity.changeTheme("stranded");
                        } else if (SplashActivity.checkPlaying().equals("stranded")) {
                            musiceditor.putString("bgm", "journey"); // THEME
                            musiceditor.commit();
                            SplashActivity.changeTheme("journey");
                        } else if (SplashActivity.checkPlaying().equals("journey")) {
                            musiceditor.putString("bgm", "theme"); // THEME
                            musiceditor.commit();
                            SplashActivity.changeTheme("theme");
                        } else {

                        }
                        updateSongText();
                    }
                }
            }
        });
    }

    private void updateSongText() {
        changeButton = (Button) fragView.findViewById(R.id.changeMusicButton);

        if (!SplashActivity.mp.isPlaying()) {
            changeButton.setText(getString(R.string.NoMusicText));
        } else {
            if (SplashActivity.checkPlaying() == "theme") {
                changeButton.setText(getString(R.string.themePlaying));
            } else if (SplashActivity.checkPlaying() == "stranded") {
                changeButton.setText(getString(R.string.strandedPlaying));
            } else if (SplashActivity.checkPlaying() == "journey") {
                changeButton.setText(R.string.journeyPlaying);
            }
        }
    }


    private void setupResetStoryButton() {
        b = (Button) fragView.findViewById(R.id.resetStoryButton);


        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SplashActivity.playSFX();
                resetAlert();
            }
        });
    }


}
