package com.eemf.sirgoingfar.bakingapp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.eemf.sirgoingfar.bakingapp.models.RecipeData;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

public class Prefs {

    private final String APP_PREFS = "app_prefs";
    private final String PREF_RECIPE_DATA = "pref_recipe_data";

    private SharedPreferences mPrefs;
    @SuppressLint("StaticFieldLeak")
    private static Prefs sInstance;
    @SuppressLint("StaticFieldLeak")
    private static Context mContext;

    public static Prefs getsInstance(@NonNull Context context) {

        mContext = context;

        if (sInstance == null)
            sInstance = new Prefs();

        return sInstance;
    }

    private Prefs() {
        sInstance = this;
        mPrefs = mContext.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return mPrefs.edit();
    }

    public void saveRecipeData(@NonNull List<RecipeData> recipeData) {

        String jsonString = new Gson().toJson(recipeData, new TypeToken<List<RecipeData>>() {
        }.getType());

        getEditor().putString(PREF_RECIPE_DATA, jsonString).apply();
    }

    private String getRecipeDatajsonString() {
        return mPrefs.getString(PREF_RECIPE_DATA, null);
    }

    public List<RecipeData> getRecipeData() {

        String jsonString = getRecipeDatajsonString();

        if (TextUtils.isEmpty(jsonString))
            return new ArrayList<>();
        else
            return new Gson().fromJson(jsonString, new TypeToken<List<RecipeData>>() {
            }.getType());
    }

    public boolean isRecipeDataAvailable() {
        return getRecipeDatajsonString() != null;
    }
}
