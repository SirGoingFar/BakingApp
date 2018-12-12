package com.eemf.sirgoingfar.bakingapp.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.eemf.sirgoingfar.bakingapp.activities.BaseActivity;
import com.eemf.sirgoingfar.bakingapp.utils.Prefs;


public class BaseFragment extends Fragment {

    protected BaseActivity fragmentActivity;
    protected Prefs prefs;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof BaseActivity)
            fragmentActivity = (BaseActivity) context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = Prefs.getsInstance(fragmentActivity);
    }
}
