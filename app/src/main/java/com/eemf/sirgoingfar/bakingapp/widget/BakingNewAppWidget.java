package com.eemf.sirgoingfar.bakingapp.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.RemoteViews;

import com.eemf.sirgoingfar.bakingapp.R;
import com.eemf.sirgoingfar.bakingapp.activities.MealListActivity;
import com.eemf.sirgoingfar.bakingapp.utils.DataUtil;
import com.eemf.sirgoingfar.bakingapp.utils.Prefs;

/**
 * Implementation of App Widget functionality.
 */
public class BakingNewAppWidget extends AppWidgetProvider {

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.baking_new_app_widget);

        int lastClickedMealIndex = Prefs.getsInstance(context).getLastClickedIngredient();

        if (lastClickedMealIndex < 0) {
            views.setViewVisibility(R.id.rl_empty_state, View.VISIBLE);
            views.setViewVisibility(R.id.ll_filled_state, View.GONE);

            //set pending intent on the empty state prompt
            views.setOnClickPendingIntent(R.id.tv_empty_state_prompt, getOpenMainActivityPendingIntent(context));
        } else {
            views.setViewVisibility(R.id.rl_empty_state, View.GONE);
            views.setViewVisibility(R.id.ll_filled_state, View.VISIBLE);

            //set header text
            views.setTextViewText(R.id.tv_widget_header, context.getString(R.string.widget_header_text, DataUtil.getMealNameAt(context, lastClickedMealIndex)));

            //set pending intent on the widget header
            views.setOnClickPendingIntent(R.id.tv_widget_header, getOpenMainActivityPendingIntent(context));

            //set up the ListView
            setupGridListView(views, context);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    private static void setupGridListView(@NonNull RemoteViews views, @NonNull Context context) {

        Intent gridviewDataServiceIntent = new Intent(context, IngredientRemoteViewsService.class);
        gridviewDataServiceIntent.putExtra(IngredientRemoteViewsService.EXTRA_MEAL_ID, Prefs.getsInstance(context).getLastClickedIngredient());
        views.setRemoteAdapter(R.id.lv_widget_ingredient, gridviewDataServiceIntent);
        views.setEmptyView(R.id.lv_widget_ingredient, R.id.rl_empty_state);
    }

    public static PendingIntent getOpenMainActivityPendingIntent(@NonNull Context context) {

        return PendingIntent.getActivity(context, 0, new Intent(context, MealListActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

