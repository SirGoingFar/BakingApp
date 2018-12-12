package com.eemf.sirgoingfar.bakingapp.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.eemf.sirgoingfar.bakingapp.data.RecipeDataClient;
import com.eemf.sirgoingfar.bakingapp.models.RecipeData;

import java.io.IOException;
import java.util.List;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataUtil {

    private static final String BASE_URL = "https://d17h27t6h515a5.cloudfront.net/";

    public static void fetchRecipeData(@NonNull Context context) {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        try {

            Response<List<RecipeData>> recipeDataResponse = retrofit.create(RecipeDataClient.class)
                    .fetchRecipeData().execute();

            if (recipeDataResponse.isSuccessful()) {
                Prefs.getsInstance(context).saveRecipeData(recipeDataResponse.body());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<RecipeData> getMealList(@NonNull Context context) {
        return Prefs.getsInstance(context).getRecipeData();
    }

    public static String getMealNameAt(@NonNull Context context, int mealIndex){

        if(mealIndex < 0)
            return "";

        RecipeData recipe = getMealAt(context, mealIndex);

        if(recipe == null)
            return "";
        else
            return recipe.getName();
    }

    public static RecipeData getMealAt(@NonNull Context context, int index) {

        if (index < 0)
            return null;

        return getMealList(context).get(index);
    }

    public static List<RecipeData.Ingredient> getIngredientList(@NonNull Context context, int mealIndex){

        if(mealIndex < 0)
            return null;

        return getMealList(context).get(mealIndex).getIngredients();
    }

    public static List<RecipeData.Step> getStepList(@NonNull Context context, int mealIndex){

        if(mealIndex < 0)
            return null;

        return getMealList(context).get(mealIndex).getSteps();
    }

    public static RecipeData.Step getStepAt(@NonNull Context context, int mealIndex, int stepIndex){

        if(mealIndex < 0 || stepIndex < 0)
            return null;

        return getStepList(context, mealIndex).get(stepIndex);
    }
}
