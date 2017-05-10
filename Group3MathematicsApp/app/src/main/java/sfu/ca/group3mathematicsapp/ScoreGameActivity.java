package sfu.ca.group3mathematicsapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Random;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;
import static java.lang.Math.abs;

// FILE: GameActivity.java

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DESCRIPTION:
 *
 * MAIN GAME ACTIVITY THAT DISPLAYS BASIC UI TO RUN THE GAME.
 * TEXT IS DISPLAYED AND MODIFIED WITHIN fragment_question.xml
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class ScoreGameActivity extends AppCompatActivity {

    // JAVA AND ANDROID VARS
    private Button b, mb;
    private QuestionManager qm;
    private QuestionFragment fragment_current_question;
    private InputMethodManager im;
    private EditText answerBox;
    private String correctAnswer, userAnswer;
    private Random random;

    // PRIMITIVES
    private int qCount, questionNumber, correctCount, len;

    // STRING ARRAYS FOR QUESTION DATA
    private String[] content;
    private String[] answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_game);

        // HIDE KEYBOARD INPUT AT START + STATUS BAR
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        // THESE ARE EDITABLE SETTINGS
        setGameVars();

        // SETUP GAME FOR FIRST START - do not modify
        gameInitializers();

        // HANDLE NAVIGATION BUTTONS
        setupGameSettingsButton();
        setupEndButton();

        // HANDLE ANSWERS
        setupAnswerListener();
        setupSubmitButton();


        printConsole(getString(R.string.welcomeScoreMode));
        launchNext();
    }

    @Override
    public void onBackPressed() {
        SplashActivity.playSFX();
        MainActivity.toggleResumeScore(true); // CHANGE BUTTON TEXT IN MENU TO "RESUME"
        Intent intent = new Intent(ScoreGameActivity.this, MainActivity.class);
        intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT); // DO NOT START A NEW MAIN MENU; JUST BRING FORWARD THE CURRENTLY RUNNING ONE
        ScoreGameActivity.this.startActivity(intent);
    }


    private void setGameVars() {
        qCount = 100; // CHANGE BASED ON HOW MANY QUESTIONS HAVE BEEN DEVELOPED
    }

    private void gameInitializers() {
        /* * * * * * * * * * * * * * * * * * * * * * *
         - SETS INITIAL VALUES FOR THE GAME: DO NOT MODIFY
         - MODIFIABLE SETTINGS ARE WITHIN ONCREATE()
        * * * * * * * * * * * * * * * * * * * * * * */

        qm = new QuestionManager(qCount);
        answerBox = (EditText) findViewById(R.id.ScoreAnswerEditText);

        random = new Random();

        // INITIALIZING FIELDS
        userAnswer = "emptyUserAnswer";
        correctAnswer = "emptyCorrectAnswer";
        correctCount = 0;
        questionNumber = 0;
        updateScore();
        len = 0;

        fragment_current_question = (QuestionFragment) getFragmentManager().findFragmentById(R.id.score_content_fragment);
        fragment_current_question.updateTitle("");
        fragment_current_question.updateContext("");

        im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        // POPULATE QUESTION INFORMATION

        populateContents();
        populateAnswers();
        populateQuestions();

    }


    private void updateScore() {
        TextView scoreTextView = (TextView) findViewById(R.id.ScoreTextView);
        scoreTextView.setText(getString(R.string.scoreText, correctCount, questionNumber));
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
                    answerBox.setText(""); // CLEAR ANSWER BOX
                    authenticateAnswer();
                    launchNext();
                    return true; // ENTER KEY WAS PRESSED
                } else {
                    return false; // ENTER KEY WAS NOT PRESSED
                }
            }
        });
    }

    private void authenticateAnswer() {
        if (userAnswer.equals(correctAnswer)) {
            //Toast.makeText(this, "CORRECT!", Toast.LENGTH_SHORT).show();
            printConsole(getString(R.string.correctAnswer) + " " + (questionNumber + 1) + ".");
            correctCount++;
            questionNumber++;
            updateScore();
        } else {
            printConsole(getString(R.string.incorrectAnswer) + " " + (questionNumber + 1) + ".");
            printConsole(getString(R.string.provideCorrect) + " " + correctAnswer + ".");
            questionNumber++;
            updateScore();
        }
    }

    private void launchNext() {
        newQuestion();
    }

    private void displayScoreScreen() {
        Intent intent = new Intent(ScoreGameActivity.this, ScoreGameOverActivity.class);
        Bundle e = new Bundle();
        e.putString("correct", Integer.toString(correctCount));
        e.putString("total", Integer.toString(questionNumber));
        intent.putExtras(e);
        ScoreGameActivity.this.startActivity(intent);
        finish();
    }

    private void newQuestion() {
        // RETRIEVE QUESTION OBJECT FROM QUESTION MANAGER
        int rIndex = abs(random.nextInt() % qCount);
        // printConsole("RETRIEVED QUESTION " + rIndex); // DEBUG QUESTION NUMBERS PRINT STATEMENT
        Question qObj = qm.retrieve(rIndex);
        if (qObj != null) { // qObj must not be null.
            fragment_current_question.updateContent(qObj.getContent());
            correctAnswer = qObj.getAnswer();
        } else { // ERROR STATE
            fragment_current_question.updateContent("CONTENT.ERR: NULL QUESTION OBJECT RETURN ON QUESTION " + rIndex);
        }
    }

    private void setupGameSettingsButton() {
        mb = (Button) findViewById(R.id.ScoreGameSettingsBtn);
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

    private void setupEndButton() {
        b = (Button) findViewById(R.id.EndGameButton);
        b.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                SplashActivity.playSFX();
                displayScoreScreen();
            }
        });
    }


    private void setupSubmitButton() {
        b = (Button) findViewById(R.id.ScoreSubmitButton);
        answerBox = (EditText) findViewById(R.id.ScoreAnswerEditText);
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
                authenticateAnswer();
                launchNext();
            }
        });
    }

    private void printConsole(String message) {
        fragment_current_question.updateConsole(message);
    }

    /***** BEGIN QUESTION DATA *******/
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
        content[30] = getString(R.string.content30);
        content[31] = getString(R.string.content31);
        content[32] = getString(R.string.content32);
        content[33] = getString(R.string.content33);
        content[34] = getString(R.string.content34);
        content[35] = getString(R.string.content35);
        content[36] = getString(R.string.content36);
        content[37] = getString(R.string.content37);
        content[38] = getString(R.string.content38);
        content[39] = getString(R.string.content39);
        content[40] = getString(R.string.content40);
        content[41] = getString(R.string.content41);
        content[42] = getString(R.string.content42);
        content[43] = getString(R.string.content43);
        content[44] = getString(R.string.content44);
        content[45] = getString(R.string.content45);
        content[46] = getString(R.string.content46);
        content[47] = getString(R.string.content47);
        content[48] = getString(R.string.content48);
        content[49] = getString(R.string.content49);
        content[50] = getString(R.string.content50);
        content[51] = getString(R.string.content51);
        content[52] = getString(R.string.content52);
        content[53] = getString(R.string.content53);
        content[54] = getString(R.string.content54);
        content[55] = getString(R.string.content55);
        content[56] = getString(R.string.content56);
        content[57] = getString(R.string.content57);
        content[58] = getString(R.string.content58);
        content[59] = getString(R.string.content59);
        content[60] = getString(R.string.content60);
        content[61] = getString(R.string.content61);
        content[62] = getString(R.string.content62);
        content[63] = getString(R.string.content63);
        content[64] = getString(R.string.content64);
        content[65] = getString(R.string.content65);
        content[66] = getString(R.string.content66);
        content[67] = getString(R.string.content67);
        content[68] = getString(R.string.content68);
        content[69] = getString(R.string.content69);
        content[70] = getString(R.string.content70);
        content[71] = getString(R.string.content71);
        content[72] = getString(R.string.content72);
        content[73] = getString(R.string.content73);
        content[74] = getString(R.string.content74);
        content[75] = getString(R.string.content75);
        content[76] = getString(R.string.content76);
        content[77] = getString(R.string.content77);
        content[78] = getString(R.string.content78);
        content[79] = getString(R.string.content79);
        content[80] = getString(R.string.content80);
        content[81] = getString(R.string.content81);
        content[82] = getString(R.string.content82);
        content[83] = getString(R.string.content83);
        content[84] = getString(R.string.content84);
        content[85] = getString(R.string.content85);
        content[86] = getString(R.string.content86);
        content[87] = getString(R.string.content87);
        content[88] = getString(R.string.content88);
        content[89] = getString(R.string.content89);
        content[90] = getString(R.string.content90);
        content[91] = getString(R.string.content91);
        content[92] = getString(R.string.content92);
        content[93] = getString(R.string.content93);
        content[94] = getString(R.string.content94);
        content[95] = getString(R.string.content95);
        content[96] = getString(R.string.content96);
        content[97] = getString(R.string.content97);
        content[98] = getString(R.string.content98);
        content[99] = getString(R.string.content99);
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
        answer[30] = getString(R.string.answer30);
        answer[31] = getString(R.string.answer31);
        answer[32] = getString(R.string.answer32);
        answer[33] = getString(R.string.answer33);
        answer[34] = getString(R.string.answer34);
        answer[35] = getString(R.string.answer35);
        answer[36] = getString(R.string.answer36);
        answer[37] = getString(R.string.answer37);
        answer[38] = getString(R.string.answer38);
        answer[39] = getString(R.string.answer39);
        answer[40] = getString(R.string.answer40);
        answer[41] = getString(R.string.answer41);
        answer[42] = getString(R.string.answer42);
        answer[43] = getString(R.string.answer43);
        answer[44] = getString(R.string.answer44);
        answer[45] = getString(R.string.answer45);
        answer[46] = getString(R.string.answer46);
        answer[47] = getString(R.string.answer47);
        answer[48] = getString(R.string.answer48);
        answer[49] = getString(R.string.answer49);
        answer[50] = getString(R.string.answer50);
        answer[51] = getString(R.string.answer51);
        answer[52] = getString(R.string.answer52);
        answer[53] = getString(R.string.answer53);
        answer[54] = getString(R.string.answer54);
        answer[55] = getString(R.string.answer55);
        answer[56] = getString(R.string.answer56);
        answer[57] = getString(R.string.answer57);
        answer[58] = getString(R.string.answer58);
        answer[59] = getString(R.string.answer59);
        answer[60] = getString(R.string.answer60);
        answer[61] = getString(R.string.answer61);
        answer[62] = getString(R.string.answer62);
        answer[63] = getString(R.string.answer63);
        answer[64] = getString(R.string.answer64);
        answer[65] = getString(R.string.answer65);
        answer[66] = getString(R.string.answer66);
        answer[67] = getString(R.string.answer67);
        answer[68] = getString(R.string.answer68);
        answer[69] = getString(R.string.answer69);
        answer[70] = getString(R.string.answer70);
        answer[71] = getString(R.string.answer71);
        answer[72] = getString(R.string.answer72);
        answer[73] = getString(R.string.answer73);
        answer[74] = getString(R.string.answer74);
        answer[75] = getString(R.string.answer75);
        answer[76] = getString(R.string.answer76);
        answer[77] = getString(R.string.answer77);
        answer[78] = getString(R.string.answer78);
        answer[79] = getString(R.string.answer79);
        answer[80] = getString(R.string.answer80);
        answer[81] = getString(R.string.answer81);
        answer[82] = getString(R.string.answer82);
        answer[83] = getString(R.string.answer83);
        answer[84] = getString(R.string.answer84);
        answer[85] = getString(R.string.answer85);
        answer[86] = getString(R.string.answer86);
        answer[87] = getString(R.string.answer87);
        answer[88] = getString(R.string.answer88);
        answer[89] = getString(R.string.answer89);
        answer[90] = getString(R.string.answer90);
        answer[91] = getString(R.string.answer91);
        answer[92] = getString(R.string.answer92);
        answer[93] = getString(R.string.answer93);
        answer[94] = getString(R.string.answer94);
        answer[95] = getString(R.string.answer95);
        answer[96] = getString(R.string.answer96);
        answer[97] = getString(R.string.answer97);
        answer[98] = getString(R.string.answer98);
        answer[99] = getString(R.string.answer99);
    }

    private void populateQuestions() {
        // STORES VALUES IN THE QUESTION ARRAY.
        for (int i = 0; i < qCount; i++) {
            qm.list.get(i).setContent(content[i]);
            qm.list.get(i).setAnswer(answer[i]);
        }
    }
    /***** END QUESTION DATA *******/

}
