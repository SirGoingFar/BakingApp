package com.eemf.sirgoingfar.bakingapp.activities;

import android.os.Bundle;

import com.eemf.sirgoingfar.bakingapp.R;
import com.eemf.sirgoingfar.bakingapp.fragments.MealListFragment;

public class MealListActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        //pop backstack
        popBackStackTo(MealListFragment.class.getName());

        //Start Default Landing Screen
        startFragmentOnMasterView(MealListFragment.newInstance(), true);
    }
}
