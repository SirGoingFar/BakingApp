package com.eemf.sirgoingfar.bakingapp.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.eemf.sirgoingfar.bakingapp.R;
import com.eemf.sirgoingfar.bakingapp.activities.MealListActivity;
import com.eemf.sirgoingfar.bakingapp.adapters.MealRecyclerViewAdapter;
import com.eemf.sirgoingfar.bakingapp.customs.GridSpacingItemDecoration;
import com.eemf.sirgoingfar.bakingapp.utils.ArchitectureUtil;
import com.eemf.sirgoingfar.bakingapp.utils.DataUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MealListFragment extends BaseFragment implements MealRecyclerViewAdapter.OnMealItemClicked {

    @BindView(R.id.rv_meal_list)
    RecyclerView mealRecyclerView;

    @BindView(R.id.rl_data_loading)
    RelativeLayout dataLoadingView;

    @BindView(R.id.ll_network_connectivity)
    LinearLayout poorConnectivityView;

    MealListActivity fragmentActivity;

    public static MealListFragment newInstance() {
        Bundle args = new Bundle();
        MealListFragment fragment = new MealListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof MealListActivity)
            fragmentActivity = (MealListActivity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        fragmentActivity.showOrHideDetailView(false);
        fragmentActivity.setActionBarTitle(fragmentActivity.getString(R.string.app_name));

        //fetch data and setup view
        if (prefs.isRecipeDataAvailable())
            setupView();
        else
            loadApiData();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_meal_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    private void loadApiData() {

        new AsyncTask<Void, Void, Void>() {

            @Override
            protected void onPreExecute() {
                dataLoadingView.setVisibility(View.VISIBLE);
                poorConnectivityView.setVisibility(View.GONE);
            }

            @Override
            protected Void doInBackground(Void... voids) {
                DataUtil.fetchRecipeData(fragmentActivity);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                if (prefs.isRecipeDataAvailable())
                    setupView();
                else {
                    poorConnectivityView.setVisibility(View.VISIBLE);
                    dataLoadingView.setVisibility(View.GONE);
                }
            }
        }.execute();
    }

    private void setupView() {

        //hide other views
        dataLoadingView.setVisibility(View.GONE);
        poorConnectivityView.setVisibility(View.GONE);

        //setup recyclerview
        mealRecyclerView.setHasFixedSize(true);
        mealRecyclerView.setFocusable(true);

        GridLayoutManager gridLayoutManager = new GridLayoutManager(fragmentActivity, getMaxSpanCount());

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(fragmentActivity, LinearLayoutManager.VERTICAL,
                false);

        //set appropriate Layout Manager
        if (!ArchitectureUtil.isTablet(fragmentActivity)) {
            if (!ArchitectureUtil.isPhoneRotated(fragmentActivity)) {
                mealRecyclerView.setLayoutManager(linearLayoutManager);

            } else {
                mealRecyclerView.setLayoutManager(gridLayoutManager);
                mealRecyclerView.addItemDecoration(new GridSpacingItemDecoration(getMaxSpanCount(), 16, false));
            }
        } else {
            mealRecyclerView.setLayoutManager(gridLayoutManager);
            mealRecyclerView.addItemDecoration(new GridSpacingItemDecoration(getMaxSpanCount(), 16, false));
        }

        MealRecyclerViewAdapter adapter = new MealRecyclerViewAdapter(fragmentActivity, DataUtil.getMealList(fragmentActivity), this);

        mealRecyclerView.setAdapter(adapter);
    }

    private int getMaxSpanCount() {

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();

        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        int SCALING_FACTOR = 250;

        int noOfColumns = (int) (dpWidth / SCALING_FACTOR);

        if (noOfColumns < 3)
            noOfColumns = 3;

        return noOfColumns;
    }

    @OnClick(R.id.btn_try_again)
    void retryDataFetch() {
        loadApiData();
    }

    @Override
    public void onMealItemClicked(int clickedMealPosition) {
        fragmentActivity.startFragmentOnMasterView(MealRecipeIngredientFragment.newInstance(clickedMealPosition), true);
    }

}