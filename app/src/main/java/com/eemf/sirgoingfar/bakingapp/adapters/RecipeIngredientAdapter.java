package com.eemf.sirgoingfar.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eemf.sirgoingfar.bakingapp.R;
import com.eemf.sirgoingfar.bakingapp.activities.FragmentHostActivity;
import com.eemf.sirgoingfar.bakingapp.fragments.IngredientFragment;
import com.eemf.sirgoingfar.bakingapp.fragments.StepFragment;
import com.eemf.sirgoingfar.bakingapp.models.RecipeData;
import com.eemf.sirgoingfar.bakingapp.utils.DataUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class RecipeIngredientAdapter extends RecyclerView.Adapter<RecipeIngredientAdapter.Holder> {

    private int mMealIndex;
    private FragmentHostActivity mContext;
    private List<RecipeData.Ingredient> mIngredientList = new ArrayList<>();
    private List<RecipeData.Step> mStepList = new ArrayList<>();

    public RecipeIngredientAdapter(@NonNull Context context, int clickedMealIndex) {

        if (clickedMealIndex < 0)
            return;

        if (context instanceof FragmentHostActivity)
            mContext = (FragmentHostActivity) context;

        mMealIndex = clickedMealIndex;
        mIngredientList = DataUtil.getIngredientList(context, clickedMealIndex);
        mStepList = DataUtil.getStepList(context, clickedMealIndex);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_recipe_ingredient, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {

        RecipeData.Step currentStep = holder.getCurrentStep();

        if (position == 0)
            holder.desc.setText(mIngredientList.size() <= 1 ? mContext.getString(R.string.singular_ingredient) : mContext.getString(R.string.plural_ingredient));
        else
            holder.desc.setText(mContext.getString(R.string.step_label_text, String.valueOf(holder.getAdapterPosition()), currentStep.getShortDescription()));
    }

    @Override
    public int getItemCount() {
        return (mStepList.size() + 1);
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.tv_recipe_ingredient)
        TextView desc;

        @BindView(R.id.container)
        CardView container;

        Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            container.setOnClickListener(this);
        }

        RecipeData.Step getCurrentStep() {
            if ((getAdapterPosition() - 1) < 0)
                return null;

            return mStepList.get(getAdapterPosition() - 1);
        }

        @Override
        public void onClick(View clickedView) {
            switch (clickedView.getId()) {
                case R.id.tv_recipe_ingredient:
                case R.id.container:
                    openAppropriateFragment(getAdapterPosition());
                    break;
            }
        }

        private void openAppropriateFragment(int adapterPosition) {

            Fragment fragment;

            if (adapterPosition == 0) {
                //start Ingredient Fragment
                fragment = IngredientFragment.newInstance(mMealIndex);
                if(mContext.isDetailViewAvailable()) {
                    mContext.startFragmentOnDetailView(fragment, true);
                }
                else {
                    mContext.startFragmentOnMasterView(fragment, true);
                }
            }else {
                //start Step Fragment
                fragment = StepFragment.newInstance(mMealIndex, adapterPosition);
                if(mContext.isDetailViewAvailable()) {
                    mContext.startFragmentOnDetailView(fragment, true);
                }
                else {
                    mContext.startFragmentOnMasterView(fragment, true);
                }
            }
        }
    }
}
