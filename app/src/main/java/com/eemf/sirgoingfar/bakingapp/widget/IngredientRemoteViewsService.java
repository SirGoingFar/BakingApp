package com.eemf.sirgoingfar.bakingapp.widget;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.eemf.sirgoingfar.bakingapp.R;
import com.eemf.sirgoingfar.bakingapp.models.RecipeData;
import com.eemf.sirgoingfar.bakingapp.utils.DataUtil;
import com.eemf.sirgoingfar.bakingapp.utils.Prefs;

import java.util.List;

public class IngredientRemoteViewsService extends RemoteViewsService {

    public static final String EXTRA_MEAL_ID = "extra_meal_id";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {

        if (intent.hasExtra(EXTRA_MEAL_ID))
            return new IngredientRemoteViewsFactory(this.getApplicationContext(),
                    intent.getIntExtra(EXTRA_MEAL_ID, -1));
        else if (Prefs.getsInstance(this.getApplicationContext()).getLastClickedIngredient() >= 0)
            return new IngredientRemoteViewsFactory(this.getApplicationContext(),
                    Prefs.getsInstance(this.getApplicationContext()).getLastClickedIngredient());
        else
            return null;
    }

    class IngredientRemoteViewsFactory implements RemoteViewsFactory {

        private Context mContext;
        private int mMealId;
        private List<RecipeData.Ingredient> mIngredientList;
        private RecipeData.Ingredient currentIngredientObject;

        IngredientRemoteViewsFactory(@NonNull Context context, int mealId) {

            if (mealId < 0)
                return;

            this.mContext = context;
            this.mMealId = mealId;
            mIngredientList = DataUtil.getIngredientList(mContext, mMealId);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            mIngredientList = DataUtil.getIngredientList(mContext, mMealId);
        }

        @Override
        public void onDestroy() {
            mIngredientList.clear();
        }

        @Override
        public int getCount() {
            if (mIngredientList != null)
                return mIngredientList.size();
            else
                return 0;
        }

        @Override
        public RemoteViews getViewAt(int position) {

            RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.widget_content_view);

            currentIngredientObject = mIngredientList.get(position);

            remoteView.setTextViewText(R.id.tv_ingredient_name, currentIngredientObject.getIngredient());
            remoteView.setTextViewText(R.id.tv_ingredient_measure, mContext.getString(R.string.ingredient_measurement,
                    String.valueOf(currentIngredientObject.getQuantity()), currentIngredientObject.getMeasure()));

            return remoteView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }
    }
}
