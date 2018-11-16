package com.eemf.sirgoingfar.bakingapp.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.eemf.sirgoingfar.bakingapp.activities.FragmentHostActivity;


public class BaseFragment extends Fragment {

    protected FragmentHostActivity fragmentActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if(context instanceof FragmentHostActivity)
            fragmentActivity = (FragmentHostActivity) context;
    }
}
