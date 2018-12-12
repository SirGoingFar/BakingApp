package com.eemf.sirgoingfar.bakingapp.data;

import com.eemf.sirgoingfar.bakingapp.models.RecipeData;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;

public interface RecipeDataClient {

    @GET("topher/2017/May/59121517_baking/baking.json")
    Call<List<RecipeData>> fetchRecipeData();

}
