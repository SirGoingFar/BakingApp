package com.eemf.sirgoingfar.bakingapp.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import com.eemf.sirgoingfar.bakingapp.R;
import com.eemf.sirgoingfar.bakingapp.fragments.IngredientFragment;
import com.eemf.sirgoingfar.bakingapp.fragments.StepFragment;
import com.eemf.sirgoingfar.bakingapp.utils.Constants;

public class RecipeStepActivity extends BaseActivity {

    private int mMealIndex = -1;
    private int mItemPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);

        //initialize variables
        if (getIntent().getBundleExtra(Constants.RECIPE_STEP_ACTIVITY_DATA_BUNDLE) != null)
            setupView(getIntent().getBundleExtra(Constants.RECIPE_STEP_ACTIVITY_DATA_BUNDLE));
    }

    private void setupView(@NonNull Bundle dataBundle) {

        if (dataBundle.containsKey(Constants.KEY_CLICKED_ITEM_POSITION))
            mItemPosition = dataBundle.getInt(Constants.KEY_CLICKED_ITEM_POSITION);

        if (dataBundle.containsKey(Constants.KEY_MEAL_INDEX))
            mMealIndex = dataBundle.getInt(Constants.KEY_MEAL_INDEX);

        //invalid parameter check
        if (mMealIndex < 0 || mItemPosition < 0)
            finish();

        //pop multiple fragment launched, if any
        if (dataBundle.containsKey(Constants.KEY_CURRENT_FRAGMENT_NAME)) {
            String currentFragmentTag = dataBundle.getString(Constants.KEY_CURRENT_FRAGMENT_NAME);

            if (currentFragmentTag != null)
                popBackStackTo(currentFragmentTag);
        }

        ///open appropriate screen
        openAppropriateScreen(mMealIndex, mItemPosition);
    }

    private void openAppropriateScreen(int mMealIndex, int mItemPosition) {

        Fragment fragment;

        if (mItemPosition == 0) {
            //start Ingredient Fragment
            fragment = IngredientFragment.newInstance(mMealIndex);
            startFragmentOnMasterView(fragment, true);
        } else {
            //start Step Fragment
            fragment = StepFragment.newInstance(mMealIndex, mItemPosition);
            startFragmentOnMasterView(fragment, true);
        }
    }
}