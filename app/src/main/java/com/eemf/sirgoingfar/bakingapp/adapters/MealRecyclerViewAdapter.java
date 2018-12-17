package com.eemf.sirgoingfar.bakingapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.eemf.sirgoingfar.bakingapp.R;
import com.eemf.sirgoingfar.bakingapp.models.RecipeData;
import com.eemf.sirgoingfar.bakingapp.utils.Prefs;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;

public class MealRecyclerViewAdapter extends RecyclerView.Adapter<MealRecyclerViewAdapter.Holder>{

    private Context context;
    private List<RecipeData> mealList;
    private OnMealItemClicked listener;

    public MealRecyclerViewAdapter(Context context, List<RecipeData> mealList, OnMealItemClicked listener) {
        this.context = context;
        this.mealList = mealList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_meal, parent, false);
        return new Holder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Holder holder, int position) {
        RecipeData currentMeal = holder.getCurrentMealName();

        //set appropriate meal icon
        if (TextUtils.isEmpty(currentMeal.getImage())) {
            holder.mealIcon.setVisibility(View.GONE);
            holder.altMealIcon.setText(currentMeal.getName().substring(0,1));
        }else {
            Glide.with(context).load(currentMeal.getImage()).into(holder.mealIcon);
        }

        //set meal name
        holder.mealName.setText(currentMeal.getName());
    }

    @Override
    public int getItemCount() {
        return mealList.size();
    }

    class Holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.ic_meal)
        CircleImageView mealIcon;

        @BindView(R.id.tv_meal_icon)
        TextView altMealIcon;

        @BindView(R.id.tv_meal_name)
        TextView mealName;

        @BindView(R.id.container)
        CardView container;

        public Holder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            container.setOnClickListener(this);
        }

        RecipeData getCurrentMealName(){
            return mealList.get(getAdapterPosition());
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.container:
                    listener.onMealItemClicked(getAdapterPosition());
                    //update last clicked meal index
                    Prefs.getsInstance(context).setLastClickedIngredient(getAdapterPosition());
                    break;
            }
        }
    }

    public interface OnMealItemClicked{
        void onMealItemClicked(int clickedMealPosition);
    }
}
