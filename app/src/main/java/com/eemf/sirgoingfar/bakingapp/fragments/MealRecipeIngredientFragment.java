package com.eemf.sirgoingfar.bakingapp.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eemf.sirgoingfar.bakingapp.R;
import com.eemf.sirgoingfar.bakingapp.adapters.RecipeIngredientAdapter;
import com.eemf.sirgoingfar.bakingapp.utils.DataUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MealRecipeIngredientFragment extends BaseFragment {

    @BindView(R.id.rv_meal_list)
    RecyclerView ingredientRecipeRecyclerView;

    private int mMealIndex;

    public static MealRecipeIngredientFragment newInstance(int mealIndex) {
        Bundle args = new Bundle();
        MealRecipeIngredientFragment fragment = new MealRecipeIngredientFragment();
        fragment.mMealIndex = mealIndex;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        fragmentActivity.showOrHideDetailView(true);

        //Set ActionBar Title to the name of the meal
        fragmentActivity.setActionBarTitle(DataUtil.getMealNameAt(fragmentActivity, mMealIndex));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_recipe, container, false);
        ButterKnife.bind(this, view);
        setupView();
        return view;
    }

    private void setupView() {
        RecipeIngredientAdapter adapter = new RecipeIngredientAdapter(fragmentActivity, mMealIndex);
        ingredientRecipeRecyclerView.setHasFixedSize(true);
        ingredientRecipeRecyclerView.setFocusable(true);
        ingredientRecipeRecyclerView.setLayoutManager(new LinearLayoutManager(fragmentActivity, LinearLayoutManager.VERTICAL, false));
        ingredientRecipeRecyclerView.setAdapter(adapter);
    }
}
