package sfu.ca.group3mathematicsapp;


import android.os.Bundle;
import android.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

// FILE: QuestionFragment.java

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DESCRIPTION:
 *
 * COMPANION CLASS TO GameActivity.java
 * HOSTS THE MAIN CONTENT OF THE GAME
 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class QuestionFragment extends Fragment {

    // ANDROID VARS
    public TextView title, context, content, console;
    private ScrollView consoleScroller;
    private List<String> consoleMessages;

    // STRING VARS
    private String lastConsoleMessage;

    // PRIMITIVES
    private int lineCount, consoleMaxLines;



    public QuestionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_question, container, false);

        consoleMessages = new ArrayList<String>();
        consoleMaxLines = 12;

        // RETRIEVE TEXT VIEWS
        title = (TextView) v.findViewById(R.id.QuestionTitle);
        context = (TextView) v.findViewById(R.id.QuestionContext);
        content = (TextView) v.findViewById(R.id.QuestionContent);
        console = (TextView) v.findViewById(R.id.QuestionConsole);

        title.setText("This is a title");
        context.setText("This is a context");
        content.setText("This is a description");
        console.setText("This is a console");

        lineCount = 1;

        // ALLOW CONSOLE TO BE SCROLLABLE
        //consoleScroller = (ScrollView) v.findViewById(R.id.console_sv);
        console.setMovementMethod(new ScrollingMovementMethod());
        console.setFocusable(true);

        return v; // need to access view through variable 'v' in order to edit text.
    }

    public void updateTitle(String newTitle) {
        title.setText(newTitle);
    }

    public void updateContext(String newContext) {
        context.setText(newContext);
    }

    public void updateContent(String newContent) {
        content.setText(newContent);
    }

    public void updateConsole(String message) {
        consoleMessages.add(" >> " + message);

        lineCount++;

        if (lineCount >= consoleMaxLines) {
            consoleMessages.remove(0);
            lineCount--;
        }

        String allMessages = "";
        for (String string : consoleMessages) {
            allMessages += string + "\n";
        }

        lastConsoleMessage = allMessages;

        // TODO: IMPLEMENT PROPER SCROLLING
        console.setText(allMessages);
        console.setGravity(Gravity.BOTTOM);
    }

    public String getLastConsoleMessage() {
        return lastConsoleMessage;
    }


}
