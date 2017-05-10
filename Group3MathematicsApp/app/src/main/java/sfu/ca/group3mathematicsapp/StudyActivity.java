package sfu.ca.group3mathematicsapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;
import android.view.View;

import static android.content.Intent.FLAG_ACTIVITY_REORDER_TO_FRONT;


// FILE: StudyActivity.java

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DESCRIPTION:
 *
 * PROVIDES USEFUL THIRD-PARTY LINKS AND RESOURCES FOR THE USER TO
 * LEARN MATH FROM.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class StudyActivity extends AppCompatActivity {

    // STRING VARS
    private String origin;

    public StudyActivity() {
        this.origin = "main";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study);

        origin = getIntent().getStringExtra("origin");

        //Textviews allowing links to be clicked without showing url
        TextView prop = (TextView) findViewById(R.id.introProp);
        prop.setMovementMethod(LinkMovementMethod.getInstance());

        TextView laws = (TextView) findViewById(R.id.logicLaws);
        laws.setMovementMethod(LinkMovementMethod.getInstance());

        TextView truth = (TextView) findViewById(R.id.truthtables);
        truth.setMovementMethod(LinkMovementMethod.getInstance());

        TextView proof = (TextView) findViewById(R.id.tableProofs);
        proof.setMovementMethod(LinkMovementMethod.getInstance());

        TextView cond = (TextView) findViewById(R.id.conditionals);
        cond.setMovementMethod(LinkMovementMethod.getInstance());

        TextView limits = (TextView) findViewById(R.id.linkLimits);
        limits.setMovementMethod(LinkMovementMethod.getInstance());

        TextView dervs = (TextView) findViewById(R.id.linkDerivatives);
        dervs.setMovementMethod(LinkMovementMethod.getInstance());

        TextView calc1 = (TextView) findViewById(R.id.link1Calc);
        calc1.setMovementMethod(LinkMovementMethod.getInstance());

        TextView calc2 = (TextView) findViewById(R.id.link2Calc);
        calc2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView calc3 = (TextView) findViewById(R.id.link3Calc);
        calc3.setMovementMethod(LinkMovementMethod.getInstance());

        TextView macm1 = (TextView) findViewById(R.id.link1Macm);
        macm1.setMovementMethod(LinkMovementMethod.getInstance());

        TextView macm2 = (TextView) findViewById(R.id.link2Macm);
        macm2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView trig1 = (TextView) findViewById(R.id.link1Trig);
        trig1.setMovementMethod(LinkMovementMethod.getInstance());

        TextView trig2 = (TextView) findViewById(R.id.link2Trig);
        trig2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView lalg1 = (TextView) findViewById(R.id.link1Lalg);
        lalg1.setMovementMethod(LinkMovementMethod.getInstance());

        TextView lalg2 = (TextView) findViewById(R.id.link2Lalg);
        lalg2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView alg1 = (TextView) findViewById(R.id.link1Alg);
        alg1.setMovementMethod(LinkMovementMethod.getInstance());

        TextView alg2 = (TextView) findViewById(R.id.link2Alg);
        alg2.setMovementMethod(LinkMovementMethod.getInstance());

        TextView conv = (TextView) findViewById(R.id.linkConvert);
        conv.setMovementMethod(LinkMovementMethod.getInstance());

        TextView prob = (TextView) findViewById(R.id.linkProb);
        prob.setMovementMethod(LinkMovementMethod.getInstance());


    }


    @Override
    public void onBackPressed() {
        SplashActivity.playSFX();
        if (origin.equals("main")) {
            // GO BACK TO THE MAIN MENU
            Intent intent = new Intent(StudyActivity.this, MainActivity.class);
            intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            StudyActivity.this.startActivity(intent);
        } else if (origin.equals("game")) {
            // GO BACK TO THE GAME
            Intent intent = new Intent(StudyActivity.this, GameActivity.class);
            intent.setFlags(FLAG_ACTIVITY_REORDER_TO_FRONT);
            StudyActivity.this.startActivity(intent);
        } else {
            // DO NOTHING
        }
    }
}
