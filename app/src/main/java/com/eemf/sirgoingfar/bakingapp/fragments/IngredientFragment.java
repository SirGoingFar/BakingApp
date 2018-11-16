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
import com.eemf.sirgoingfar.bakingapp.adapters.IngredientAdapter;
import com.eemf.sirgoingfar.bakingapp.adapters.RecipeIngredientAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientFragment extends BaseFragment {

    @BindView(R.id.rv_meal_ingredient)
    RecyclerView ingredientRecycler;

    private int mMealIndex;

    public static IngredientFragment newInstance(int mealIndex) {
        Bundle args = new Bundle();
        IngredientFragment fragment = new IngredientFragment();
        fragment.mMealIndex = mealIndex;
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ingredient, container, false);
        ButterKnife.bind(this, view);
        setupView();
        return view;
    }

    private void setupView() {
        IngredientAdapter adapter = new IngredientAdapter(fragmentActivity, mMealIndex);
        ingredientRecycler.setHasFixedSize(true);
        ingredientRecycler.setFocusable(true);
        ingredientRecycler.setLayoutManager(new LinearLayoutManager(fragmentActivity, LinearLayoutManager.VERTICAL, false));
        ingredientRecycler.setAdapter(adapter);
    }
}
