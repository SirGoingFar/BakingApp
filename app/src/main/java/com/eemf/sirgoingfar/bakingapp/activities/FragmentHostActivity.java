package com.eemf.sirgoingfar.bakingapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.view.View;

import com.eemf.sirgoingfar.bakingapp.R;
import com.eemf.sirgoingfar.bakingapp.fragments.MealListFragment;

public class FragmentHostActivity extends BaseActivity {

    private ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        //Start Default Landing Screen
        startFragmentOnMasterView(MealListFragment.newInstance(), true);
    }

    public void setActionBarTitle(@NonNull String title){

        if(actionBar == null)
            actionBar = getSupportActionBar();

        actionBar.setTitle(title);
    }

    public boolean isDetailViewAvailable(){
        return findViewById(R.id.container_detail) != null;
    }

    public void showOrHideDetailView(boolean show){

        View detailView = findViewById(R.id.container_detail);

        if(!isDetailViewAvailable())
            return;

        if(show)
            detailView.setVisibility(View.VISIBLE);
        else
            detailView.setVisibility(View.GONE);
    }
}
