package sfu.ca.group3mathematicsapp;

// FILE: Question.java

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DESCRIPTION:
 *
 * JAVA CLASS FOR A QUESTION OBJECT THAT SERVES AS THE BASIS FOR QUESTION
 * FRAGMENTS WITHIN THE GAME. USE GETTERS/SETTERS TO CHANGE VALUES.
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class Question {

    // STRING VARS
    private String title; // STRING THAT CONTAINS THE TEXT OF THE TITLE
    private String context; // STRING THAT CONTAINS CONTEXT OF THE QUESTION
    private String contextF; // STRING THAT CONTAINS CONTEXT OF THE QUESTION IF YOU FAILED THE PREVIOUS QUESTIONS
    private String content; // STRING THAT CONTAINS CONTENT OF THE QUESTION
    private String answer; // STRING THAT CONTAINS THE RIGHT ANSWER

    // PRIMITIVES
    private boolean solved;
    private int qID;
    private int studyID; // -1 == uninitialized
    private int penalty;

    // CONSTRUCTOR FOR GENERIC QUESTION, WITH QID
    public Question(int qID) {
        this.qID = qID;
        this.studyID = -1;
        this.penalty = -1;
        this.title = "NO TITLE ASSIGNED";
        this.context = "NO CONTEXT ASSIGNED";
        this.context = "NO CONTEXTF ASSIGNED";
        this.content = "NO CONTENT ASSIGNED";
        this.answer = "NO ANSWER ASSIGNED";
        this.solved = false;
    }

    // CONSTRUCTOR FOR SETTING ALL QUESTION PARAMS
    public Question(int qID, int studyID, int penalty, String title, String context, String contextF, String content, String answer, boolean solved) {
        this.qID = qID;
        this.studyID = studyID;
        this.title = title;
        this.context = context;
        this.contextF = contextF;
        this.penalty = penalty;
        this.content = content;
        this.answer = answer;
        this.solved = solved;
    }


    /***
     * START OF GETTERS AND SETTERS
     ***/
    public int getqID() {
        return qID;
    }

    public void setqID(int qID) {
        this.qID = qID;
    }

    public int getStudyID() {
        return studyID;
    }

    public void setStudyID(int studyID) {
        this.studyID = studyID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPenalty() {
        return penalty;
    }

    public void setPenalty(int penalty) {
        this.penalty = penalty;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getContextF() {
        return contextF;
    }

    public void setContextF(String contextF) {
        this.contextF = contextF;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }
    /*** END OF GETTERS AND SETTERS ***/
}
