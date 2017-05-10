package sfu.ca.group3mathematicsapp;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

// FILE: LogoFragment.java

/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 * DESCRIPTION:
 *
 * SIMPLE FRAGMENT THAT JUST DISPLAYS USER LOGO. ENCAPSULATED WITHIN A
 * FRAGMENT TO ASSIST IN HIDING/SHOWING THE GAME LOGO.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

public class LogoFragment extends Fragment {

    public LogoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logo, container, false);
    }


}
