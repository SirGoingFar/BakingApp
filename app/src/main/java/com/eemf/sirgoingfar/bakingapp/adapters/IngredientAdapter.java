package com.eemf.sirgoingfar.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eemf.sirgoingfar.bakingapp.R;
import com.eemf.sirgoingfar.bakingapp.models.RecipeData;
import com.eemf.sirgoingfar.bakingapp.utils.DataUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class IngredientAdapter extends RecyclerView.Adapter<IngredientAdapter.Holder>{

    private Context mContext;
    private List<RecipeData.Ingredient> ingredientList = new ArrayList<>();

    public IngredientAdapter(Context mContext, int mealIndex) {

        if(mealIndex < 0)
            return;

        this.mContext = mContext;
        this.ingredientList = DataUtil.getIngredientList(mContext, mealIndex);
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(mContext).inflate(R.layout.item_ingredient, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        RecipeData.Ingredient currentIngredient = holder.getCurrentIngredient();
        holder.ingredientName.setText(currentIngredient.getIngredient());
        holder.ingredientMeasure.setText(mContext.getString(R.string.ingredient_measurement,
                String.valueOf(currentIngredient.getQuantity()), currentIngredient.getMeasure()));
    }

    @Override
    public int getItemCount() {
        return ingredientList.size();
    }

    class Holder extends RecyclerView.ViewHolder{

        @BindView(R.id.tv_ingredient_name)
        TextView ingredientName;

        @BindView(R.id.tv_ingredient_measure)
        TextView ingredientMeasure;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        RecipeData.Ingredient getCurrentIngredient(){
            return ingredientList.get(getAdapterPosition());
        }
    }
}
