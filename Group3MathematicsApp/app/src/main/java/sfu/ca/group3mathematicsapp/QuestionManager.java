package sfu.ca.group3mathematicsapp;

import android.content.res.Resources;

import java.util.ArrayList;

// FILE: QuestionManager.java

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DESCRIPTION:
 *
 * CONTAINS A LIST OF QUESTION OBJECTS FOR USE WITH THE QUESTION FRAGMENTS
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

@SuppressWarnings("unused")
public class QuestionManager {

    // JAVA VARS
    private int defaultPenalty;
    public ArrayList<Question> list;
    public static int questionCount; // VARIABLE FOR NUMBER OF QUESTIONS IN TOTAL

    public QuestionManager(int size) {
        questionCount = size; // MAX NUMBER OF QUESTIONS THAT THE GAME CAN HANDLE
        defaultPenalty = -1; // LIVES LOST WHEN A QUESTION IS FAILED

        InitializeQuestionsArray();
    }

    private void InitializeQuestionsArray() {
        list = new ArrayList<>();

        // GENERATE EMPTY QUESTIONS FOR ADDITION LATER
        for (int i = 0; i < questionCount; i++) {
            list.add(new Question(i, i, defaultPenalty, "", "", "", "", "", false));
        }
    }

    public Question retrieve(int id) {
        if (id >= 0 && id < questionCount) {
            Question qObj = list.get(id);
            return qObj;
        } else {
            return null;
        }
    }
}
