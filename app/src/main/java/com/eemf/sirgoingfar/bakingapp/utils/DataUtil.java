package com.eemf.sirgoingfar.bakingapp.utils;

import android.content.Context;
import android.support.annotation.NonNull;

import com.eemf.sirgoingfar.bakingapp.R;
import com.eemf.sirgoingfar.bakingapp.models.RecipeData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.List;

public class DataUtil {

    private final String DATA_URL = "https://d17h27t6h515a5.cloudfront.net/topher/2017/May/59121517_baking/baking.json";

    private static String parseRecipeData(@NonNull Context context) {
        InputStream is = context.getResources().openRawResource(R.raw.data_json_file);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
                is = null;
            }
        }

        return writer.toString();
    }

    public static List<RecipeData> getMealList(@NonNull Context context) {

        String jsonString = parseRecipeData(context);

        return new Gson().fromJson(jsonString, new TypeToken<List<RecipeData>>(){}.getType());
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
