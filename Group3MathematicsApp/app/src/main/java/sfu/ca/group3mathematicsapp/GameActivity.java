package sfu.ca.group3mathematicsapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;

// FILE: GameActivity.java
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DESCRIPTION:
 *
 * MAIN GAME ACTIVITY THAT DISPLAYS BASIC UI TO RUN THE GAME.
 * TEXT IS DISPLAYED AND MODIFIED WITHIN fragment_question.xml
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
public class GameActivity extends AppCompatActivity {
    // ANDROID VARS
    private Button b, mb;
    private EditText answerBox;
    private InputMethodManager im;
    private QuestionManager qm;
    private QuestionFragment fragment_current_question;
    private SharedPreferences SP;
    private SharedPreferences.Editor editor;
    // STRING VARS
    private String correctAnswer, userAnswer;
    // PRIMITIVES
    public static int lives, questionNumber;
    private int[] studyID;
    private int qCount, storyCount, storyInstance, len, correctCount;
    private boolean gameActive;
    // STRING ARRAYS FOR QUESTIONS
    private String[] title;
    private String[] context;
    private String[] contextF;
    private String[] content;
    private String[] answer;
    private String[] storySuccess;
    private String[] storyFail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        // HIDE KEYBOARD INPUT AT START + STATUS BAR
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        // LOAD SHARED PREFS
        SP = getSharedPreferences("storyModeData", Context.MODE_PRIVATE);
        editor = SP.edit();
        // THESE ARE EDITABLE SETTINGS
        setGameVars();
        // LOAD QUESTION MANAGER
        qm = new QuestionManager(qCount);
        // SETUP GAME FOR FIRST START - do not modify
        gameInitializers();
        // HANDLE NAVIGATION BUTTONS
        setupGameSettingsButton();
        setupHelpButton();
        // HANDLE ANSWERS
        setupAnswerListener();
        setupSubmitButton();

        if (SP.getInt("relaunchPref", -1) == 1 || questionNumber < 0) {
            initialSettings();
            launchNext(true);
        } else {
            skipTo(SP.getInt("questionNumberPref", -2), true);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        //Toast.makeText(this, "WELCOME BACK", Toast.LENGTH_SHORT).show(); // TODO: DEBUG DELETE
        resumeUpdate();
        progressCheck();
    }

    private void skipTo(int i, boolean passed) {
        Question qObj = qm.retrieve(i);
        if (qObj != null) { // qObj must not be null.
            fragment_current_question.updateTitle(qObj.getTitle());
            if (passed) {
                fragment_current_question.updateContext(qObj.getContext());
            } else {
                fragment_current_question.updateContext(qObj.getContextF());
            }
            fragment_current_question.updateContent(qObj.getContent());
            correctAnswer = qObj.getAnswer();
        } else { // ERROR STATE
            fragment_current_question.updateTitle("TITLE.ERR: NULL QUESTION OBJECT RETURN");
            fragment_current_question.updateContext("CONTEXT.ERR: NULL QUESTION OBJECT RETURN");
            fragment_current_question.updateContent("CONTENT.ERR: NULL QUESTION OBJECT RETURN");
            varDiagnostic();
        }
    }


    @Override
    public void onBackPressed() {
        SplashActivity.playSFX();
        MainActivity.toggleResumeStory(true);
        exitUpdate();
        Intent intent = new Intent(GameActivity.this, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT); // DO NOT START A NEW MAIN MENU; JUST BRING FORWARD THE CURRENTLY RUNNING ONE
        GameActivity.this.startActivity(intent);
    }

    public void initialSettings() {
        // USER'S FIRST LAUNCH
        storyInstance = -1;
        correctCount = 1;
        questionNumber = -2;
        lives = 3;
        editor.putInt("livesPref", lives); // default 3
        editor.putInt("storyInstancePref", storyInstance); // default -1
        editor.putInt("questionNumberPref", questionNumber); // default -2
        editor.putInt("correctCountPref", correctCount); // default 1
        editor.putInt("relaunchPref", 1); // default 1
        editor.commit();
    }

    private void setGameVars() {
        qCount = 30; // CHANGE BASED ON HOW MANY QUESTIONS HAVE BEEN DEVELOPED
        storyCount = 3; // CHANGE BASED ON HOW MANY STORY ITERATIONS HAVE BEEN DEVELOPED
    }

    private void gameInitializers() {
        /* * * * * * * * * * * * * * * * * * * * * * *
         - SETS INITIAL VALUES FOR THE GAME: DO NOT MODIFY
         - MODIFIABLE SETTINGS ARE WITHIN ONCREATE()
        * * * * * * * * * * * * * * * * * * * * * * */
        answerBox = (EditText) findViewById(R.id.answerEditText);
        // INITIALIZING FIELDS
        userAnswer = "emptyUserAnswer";
        correctAnswer = "emptyCorrectAnswer";
        len = 0;
        fragment_current_question = (QuestionFragment) getFragmentManager().findFragmentById(R.id.content_fragment);
        im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        // POPULATE QUESTION INFORMATION
        populateStudyID();
        populateTitles();
        populateContexts();
        populateContents();
        populateAnswers();
        populateQuestions();
        populateStories();
        // SET GAME ACTIVE
        resumeUpdate();
        gameActive = true;
    }

    private void progressCheck() {
        if (questionNumber < 0) {
            // HANDLE STORY FRAGMENT
            fragment_current_question.updateTitle(storySuccess[0]);
            fragment_current_question.updateContext("");
            fragment_current_question.updateContent("");
            // ENSURE ANSWER VARIABLES ARE CLEAR
            userAnswer = "emptyUserAnswer";
            correctAnswer = "emptyCorrectAnswer";
        } else if (SP.getInt("relaunchPref", -1) == 1) {
            launchNext(true);
        } else {
            newQuestion(true);
        }
    }

    private void exitUpdate() {
        SP = getSharedPreferences("storyModeData", Context.MODE_PRIVATE);
        editor = SP.edit();
        editor.putInt("livesPref", lives);
        editor.putInt("storyInstancePref", storyInstance);
        editor.putInt("questionNumberPref", questionNumber);
        editor.putInt("correctCountPref", correctCount);
        editor.putInt("relaunchPref", 0);
        editor.putString("lastConsoleMessagePref", lastConsole());
        editor.commit();
    }

    private void resumeUpdate() {
        SP = getSharedPreferences("storyModeData", Context.MODE_PRIVATE);
        if (SP.contains("relaunchPref")) {
            // PREFERENCES EXIST
            if (SP.getInt("relaunchPref", -1) == 0) {
                // Toast.makeText(getApplicationContext(), "RETRIEVING OLD Q'S", Toast.LENGTH_SHORT).show(); // DEBUG TOAST
                storyInstance = SP.getInt("storyInstancePref", -10);
                correctCount = SP.getInt("correctCountPref", -10);
                questionNumber = SP.getInt("questionNumberPref", -10);
                lives = SP.getInt("livesPref", -10);
                if (questionNumber >= 0) {
                    printConsole(getString(R.string.WelcomeBackConsole));
                }
            } else {
                initialSettings();
            }
        } else {
            initialSettings();
        }
        updateLives();
    }

    // TESTING FUNCTION TO PRINT VARIABLES IN PREFS
    private void varDiagnostic() {
        printConsole("storyInstance = " + storyInstance);
        printConsole("correctCount = " + correctCount);
        printConsole("questionNumber = " + questionNumber);
        printConsole("lives = " + lives);
        printConsole("relaunch pref = " + SP.getInt("relaunchPref", -90));
    }

    private void updateLives() {
        TextView livesTextView = (TextView) findViewById(R.id.livesTextView);
        editor.putInt("livesPref", lives);
        editor.commit();
        // UPDATE LIVES BASED ON CURRENT VALUE OF LIVES VARIABLE
        livesTextView.setText(getString(R.string.livesText));
        livesTextView.append(" " + Integer.toString(lives));
    }

    private void setupAnswerListener() {
        //answerBox.setFocusableInTouchMode(true);
        answerBox.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    im.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    // AUTHENTICATE ANSWER
                    userAnswer = answerBox.getText().toString();
                    launchNext(authenticateAnswer());
                    return true; // ENTER KEY WAS PRESSED
                } else {
                    return false; // ENTER KEY WAS NOT PRESSED
                }
            }
        });
    }

    private boolean authenticateAnswer() {
        if (userAnswer.equals(correctAnswer)) {
            //Toast.makeText(this, "CORRECT!", Toast.LENGTH_SHORT).show();
            printConsole(getString(R.string.correctAnswer) + " " + (questionNumber + 1) + ".");
            correctCount++;
            editor.putInt("correctCountPref", correctCount);
            editor.commit();
            return true;
        } else if (userAnswer.length() == 0) {
            printConsole(getString(R.string.suggestion));
            questionNumber--;
            editor.putInt("questionNumberPref", questionNumber);
            editor.commit();
            return true;
        } else if (userAnswer.equals("0451")) { // CHEAT CODE 0451 SKIPS TO THE END OF THE GAME
            // CORRECT ANSWER
            printConsole(getString(R.string.cheatActivated));
            printConsole(getString(R.string.gameCheated));
            questionNumber = qCount - 1;
            editor.putInt("questionNumberPref", questionNumber);
            editor.commit();
            return true;
        } else if (userAnswer.equals("0000")) { // CHEAT CODE 0000 SKIPS THE CURRENT QUESTION
            printConsole(getString(R.string.cheatActivated));
            printConsole(getString(R.string.questionSkipped));
            return true;
        } else if (userAnswer.equals("0099")) { // CHEAT CODE 0099 GIVES THE USER 99 LIVES
            printConsole(getString(R.string.cheatActivated));
            printConsole(getString(R.string.godCheat));
            lives = 99;
            updateLives();
            questionNumber--;
            editor.putInt("questionNumberPref", questionNumber);
            editor.commit();
            return true;
        } else if (userAnswer.equals("1337")) { // CHEAT CODE 1337 PRINT THE CORRECT ANSWER
            printConsole(getString(R.string.cheatActivated));
            printConsole(getString(R.string.leetCheat) + " \"" + correctAnswer + "\".");
            questionNumber--;
            editor.putInt("questionNumberPref", questionNumber);
            editor.commit();
            return true;
        } else {
            //Toast.makeText(this, "INCORRECT!", Toast.LENGTH_SHORT).show();
            printConsole(getString(R.string.incorrectAnswer) + " " + (questionNumber + 1) + ".");
            printConsole(getString(R.string.provideCorrect) + " " + correctAnswer + ".");
            return false;
        }
    }

    private void launchNext(boolean passed) {
        if (!passed) {
            lives--;
            updateLives();
        }
        if (correctCount % 4 == 0) {
            // EVERY THREE CORRECT QUESTIONS AWARDS AN EXTRA LIFE
            correctCount = 1;
            editor.putInt("correctCountPref", correctCount);
            editor.commit();
            lives++;
            updateLives();
            printConsole(getString(R.string.extraLife));
        }
        if (lives <= 0) {
            // GAME OVER: USER LOSES
            Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
            editor.putInt("relaunchPref", 1);
            editor.commit();
            intent.putExtra("gameResult", "LOSE");
            GameActivity.this.startActivity(intent);
            finish();
        } else if (questionNumber == (qCount - 1)) {
            // GAME OVER: USER WINS
            finalStory();
        } else {
            answerBox.setText(""); // CLEAR ANSWER BOX
            if (questionNumber == -2) {
                // LAUNCH A STORY INSTANCE
                questionNumber++;
                editor.putInt("questionNumberPref", questionNumber);
                editor.commit();
                setupOkButton();
                newStory(passed);
            } else {
                // LAUNCH A QUESTION INSTANCE
                if (questionNumber == -1) {
                    setupSubmitButton();
                }
                questionNumber++;
                editor.putInt("questionNumberPref", questionNumber);
                editor.commit();
                newQuestion(passed);
            }
        }
    }

    private void finalStory() {
        // HANDLE FINAL STORY FRAGMENT
        fragment_current_question.updateTitle(getString(R.string.finalStoryString));
        fragment_current_question.updateContext("");
        fragment_current_question.updateContent("");
        b = (Button) findViewById(R.id.submitButton);
        answerBox = (EditText) findViewById(R.id.answerEditText);
        b.setText(R.string.OkString);
        answerBox.setVisibility(View.GONE);
        // CHANGE BUTTON TEXT TO "PLAY AGAIN" INSTEAD OF "RETRY"
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.playSFX();
                Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
                editor.putInt("relaunchPref", 1);
                editor.commit();
                intent.putExtra("gameResult", "WIN");
                GameActivity.this.startActivity(intent);
                finish();
            }
        });
    }

    private void newQuestion(boolean passed) {
        // RETRIEVE QUESTION OBJECT FROM QUESTION MANAGER
        Question qObj = qm.retrieve(questionNumber);
        if (qObj != null) { // qObj must not be null.
            fragment_current_question.updateTitle(qObj.getTitle());
            if (passed) {
                fragment_current_question.updateContext(qObj.getContext());
            } else {
                fragment_current_question.updateContext(qObj.getContextF());
            }
            fragment_current_question.updateContent(qObj.getContent());
            correctAnswer = qObj.getAnswer();
        } else { // ERROR STATE
            fragment_current_question.updateTitle("TITLE.ERR: NULL QUESTION OBJECT RETURN");
            fragment_current_question.updateContext("CONTEXT.ERR: NULL QUESTION OBJECT RETURN");
            fragment_current_question.updateContent("CONTENT.ERR: NULL QUESTION OBJECT RETURN");
            varDiagnostic();
        }
    }

    private void newStory(boolean passed) {
        if (storyInstance <= -1) {
            printConsole(getString(R.string.welcomeMQ));
        }
        storyInstance++;
        editor.putInt("storyInstancePref", storyInstance);
        editor.commit();
        String retrievedStory;
        if (passed) {
            retrievedStory = storySuccess[storyInstance];
        } else {
            retrievedStory = storyFail[storyInstance];
        }
        // HANDLE STORY FRAGMENT
        fragment_current_question.updateTitle(retrievedStory);
        fragment_current_question.updateContext("");
        fragment_current_question.updateContent("");
        // ENSURE ANSWER VARIABLES ARE CLEAR
        userAnswer = "emptyUserAnswer";
        correctAnswer = "emptyCorrectAnswer";
    }

    private void setupGameSettingsButton() {
        mb = (Button) findViewById(R.id.gameSettingsBtn);
        // CHECK MUSIC PLAYER STATUS ON ENTRY
        if (SplashActivity.mp.isPlaying()) {
            mb.setText(getString(R.string.musicOn));
        } else {
            mb.setText(getString(R.string.musicOff));
        }
        mb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.playSFX();
                if (SplashActivity.mp != null) {
                    // TOGGLE MUSIC
                    if (SplashActivity.mp.isPlaying()) { // CASE 1: MUSIC IS PLAYING, THUS WE PAUSE IT
                        len = SplashActivity.mp.getCurrentPosition();
                        SplashActivity.mp.pause();
                        mb.setText(getString(R.string.musicOff));
                    } else { // CASE 2: MUSIC IS NOT PLAYING, THUS WE RESUME IT
                        SplashActivity.mp.seekTo(len);
                        SplashActivity.mp.start();
                        mb.setText(getString(R.string.musicOn));
                    }
                }
            }
        });
    }



    private void setupHelpButton() {
        b = (Button) findViewById(R.id.helpButton);
        exitUpdate();
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.playSFX();
                Intent intent = new Intent(GameActivity.this, StudyActivity.class);
                intent.putExtra("origin", "game");
                GameActivity.this.startActivity(intent);
            }
        });
    }

    private void setupOkButton() {
        b = (Button) findViewById(R.id.submitButton);
        answerBox = (EditText) findViewById(R.id.answerEditText);
        b.setText(R.string.OkString);
        answerBox.setVisibility(View.GONE);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.playSFX();
                launchNext(true); // OK BUTTON ADVANCES THE GAME
            }
        });
    }

    private void setupSubmitButton() {
        b = (Button) findViewById(R.id.submitButton);
        answerBox = (EditText) findViewById(R.id.answerEditText);
        b.setText(R.string.SubmitString);
        answerBox.setVisibility(View.VISIBLE);
        userAnswer = "emptyUserAnswer";
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SplashActivity.playSFX();
                im.hideSoftInputFromWindow(v.getWindowToken(), 0);
                userAnswer = answerBox.getText().toString();
                answerBox.setText(""); // CLEAR ANSWER BOX
                launchNext(authenticateAnswer());
            }
        });
    }

    private void printConsole(String message) {
        fragment_current_question.updateConsole(message);
    }

    private String lastConsole() {
        return fragment_current_question.getLastConsoleMessage();
    }

    /***** BEGIN QUESTION DATA *******/
    private void populateStudyID() {
        studyID = new int[qCount];
        studyID[0] = 0;
        studyID[1] = 1;
        studyID[2] = 2;
        studyID[3] = 3;
        studyID[4] = 4;
        studyID[5] = 5;
    }

    private void populateTitles() {
        title = new String[qCount];
        title[0] = getString(R.string.title0);
        title[1] = getString(R.string.title1);
        title[2] = getString(R.string.title2);
        title[3] = getString(R.string.title3);
        title[4] = getString(R.string.title4);
        title[5] = getString(R.string.title5);
        title[6] = getString(R.string.title6);
        title[7] = getString(R.string.title7);
        title[8] = getString(R.string.title8);
        title[9] = getString(R.string.title9);
        title[10] = getString(R.string.title10);
        title[11] = getString(R.string.title11);
        title[12] = getString(R.string.title12);
        title[13] = getString(R.string.title13);
        title[14] = getString(R.string.title14);
        title[15] = getString(R.string.title15);
        title[16] = getString(R.string.title16);
        title[17] = getString(R.string.title17);
        title[18] = getString(R.string.title18);
        title[19] = getString(R.string.title19);
        title[20] = getString(R.string.title20);
        title[21] = getString(R.string.title21);
        title[22] = getString(R.string.title22);
        title[23] = getString(R.string.title23);
        title[24] = getString(R.string.title24);
        title[25] = getString(R.string.title25);
        title[26] = getString(R.string.title26);
        title[27] = getString(R.string.title27);
        title[28] = getString(R.string.title28);
        title[29] = getString(R.string.title29);
    }

    private void populateContexts() {
        context = new String[qCount];
        context[0] = getString(R.string.context0);
        context[1] = getString(R.string.context1);
        context[2] = getString(R.string.context2);
        context[3] = getString(R.string.context3);
        context[4] = getString(R.string.context4);
        context[5] = getString(R.string.context5);
        context[6] = getString(R.string.context6);
        context[7] = getString(R.string.context7);
        context[8] = getString(R.string.context8);
        context[9] = getString(R.string.context9);
        context[10] = getString(R.string.context10);
        context[11] = getString(R.string.context11);
        context[12] = getString(R.string.context12);
        context[13] = getString(R.string.context13);
        context[14] = getString(R.string.context14);
        context[15] = getString(R.string.context15);
        context[16] = getString(R.string.context16);
        context[17] = getString(R.string.context17);
        context[18] = getString(R.string.context18);
        context[19] = getString(R.string.context19);
        context[20] = getString(R.string.context20);
        context[21] = getString(R.string.context21);
        context[22] = getString(R.string.context22);
        context[23] = getString(R.string.context23);
        context[24] = getString(R.string.context24);
        context[25] = getString(R.string.context25);
        context[26] = getString(R.string.context26);
        context[27] = getString(R.string.context27);
        context[28] = getString(R.string.context28);
        context[29] = getString(R.string.context29);
        contextF = new String[qCount];
        contextF[0] = getString(R.string.contextF0);
        contextF[1] = getString(R.string.contextF1);
        contextF[2] = getString(R.string.contextF2);
        contextF[3] = getString(R.string.contextF3);
        contextF[4] = getString(R.string.contextF4);
        contextF[5] = getString(R.string.contextF5);
        contextF[6] = getString(R.string.contextF6);
        contextF[7] = getString(R.string.contextF7);
        contextF[8] = getString(R.string.contextF8);
        contextF[9] = getString(R.string.contextF9);
        contextF[10] = getString(R.string.contextF10);
        contextF[11] = getString(R.string.contextF11);
        contextF[12] = getString(R.string.contextF12);
        contextF[13] = getString(R.string.contextF13);
        contextF[14] = getString(R.string.contextF14);
        contextF[15] = getString(R.string.contextF15);
        contextF[16] = getString(R.string.contextF16);
        contextF[17] = getString(R.string.contextF17);
        contextF[18] = getString(R.string.contextF18);
        contextF[19] = getString(R.string.contextF19);
        contextF[20] = getString(R.string.contextF20);
        contextF[21] = getString(R.string.contextF21);
        contextF[22] = getString(R.string.contextF22);
        contextF[23] = getString(R.string.contextF23);
        contextF[24] = getString(R.string.contextF24);
        contextF[25] = getString(R.string.contextF25);
        contextF[26] = getString(R.string.contextF26);
        contextF[27] = getString(R.string.contextF27);
        contextF[28] = getString(R.string.contextF28);
        contextF[29] = getString(R.string.contextF29);
    }

    private void populateContents() {
        content = new String[qCount];
        content[0] = getString(R.string.content0);
        content[1] = getString(R.string.content1);
        content[2] = getString(R.string.content2);
        content[3] = getString(R.string.content3);
        content[4] = getString(R.string.content4);
        content[5] = getString(R.string.content5);
        content[6] = getString(R.string.content6);
        content[7] = getString(R.string.content7);
        content[8] = getString(R.string.content8);
        content[9] = getString(R.string.content9);
        content[10] = getString(R.string.content10);
        content[11] = getString(R.string.content11);
        content[12] = getString(R.string.content12);
        content[13] = getString(R.string.content13);
        content[14] = getString(R.string.content14);
        content[15] = getString(R.string.content15);
        content[16] = getString(R.string.content16);
        content[17] = getString(R.string.content17);
        content[18] = getString(R.string.content18);
        content[19] = getString(R.string.content19);
        content[20] = getString(R.string.content20);
        content[21] = getString(R.string.content21);
        content[22] = getString(R.string.content22);
        content[23] = getString(R.string.content23);
        content[24] = getString(R.string.content24);
        content[25] = getString(R.string.content25);
        content[26] = getString(R.string.content26);
        content[27] = getString(R.string.content27);
        content[28] = getString(R.string.content28);
        content[29] = getString(R.string.content29);
    }

    private void populateAnswers() {
        answer = new String[qCount];
        answer[0] = getString(R.string.answer0);
        answer[1] = getString(R.string.answer1);
        answer[2] = getString(R.string.answer2);
        answer[3] = getString(R.string.answer3);
        answer[4] = getString(R.string.answer4);
        answer[5] = getString(R.string.answer5);
        answer[6] = getString(R.string.answer6);
        answer[7] = getString(R.string.answer7);
        answer[8] = getString(R.string.answer8);
        answer[9] = getString(R.string.answer9);
        answer[10] = getString(R.string.answer10);
        answer[11] = getString(R.string.answer11);
        answer[12] = getString(R.string.answer12);
        answer[13] = getString(R.string.answer13);
        answer[14] = getString(R.string.answer14);
        answer[15] = getString(R.string.answer15);
        answer[16] = getString(R.string.answer16);
        answer[17] = getString(R.string.answer17);
        answer[18] = getString(R.string.answer18);
        answer[19] = getString(R.string.answer19);
        answer[20] = getString(R.string.answer20);
        answer[21] = getString(R.string.answer21);
        answer[22] = getString(R.string.answer22);
        answer[23] = getString(R.string.answer23);
        answer[24] = getString(R.string.answer24);
        answer[25] = getString(R.string.answer25);
        answer[26] = getString(R.string.answer26);
        answer[27] = getString(R.string.answer27);
        answer[28] = getString(R.string.answer28);
        answer[29] = getString(R.string.answer29);
    }

    private void populateQuestions() {
        // STORES VALUES IN THE QUESTION ARRAY.
        for (int i = 0; i < qCount; i++) {
            qm.list.get(i).setTitle(title[i]);
            qm.list.get(i).setContext(context[i]);
            qm.list.get(i).setContextF(contextF[i]);
            qm.list.get(i).setContent(content[i]);
            qm.list.get(i).setAnswer(answer[i]);
        }
    }

    /***** END QUESTION DATA *******/
    private void populateStories() {
        storySuccess = new String[storyCount];
        storySuccess[0] = getString(R.string.storyS0);
        storyFail = new String[storyCount];
        storyFail[0] = getString(R.string.storyF0);
    }
}