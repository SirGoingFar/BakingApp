package com.eemf.sirgoingfar.bakingapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RecipeData {

    @SerializedName("image")
    private String image;

    @SerializedName("servings")
    private int servings;

    @SerializedName("name")
    private String name;

    @SerializedName("ingredients")
    private List<Ingredient> ingredients;

    @SerializedName("id")
    private int id;

    @SerializedName("steps")
    private List<Step> steps;

    public void setImage(String image) {
        this.image = image;
    }

    public String getImage() {
        return image;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public int getServings() {
        return servings;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setIngredients(List<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public class Step {

        @SerializedName("videoURL")
        private String videoURL;

        @SerializedName("description")
        private String description;

        @SerializedName("id")
        private int id;

        @SerializedName("shortDescription")
        private String shortDescription;

        @SerializedName("thumbnailUrl")
        private String thumbnailUrl;

        public void setVideoURL(String videoURL) {
            this.videoURL = videoURL;
        }

        public String getVideoURL() {
            return videoURL;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }

        public void setShortDescription(String shortDescription) {
            this.shortDescription = shortDescription;
        }

        public String getShortDescription() {
            return shortDescription;
        }

        public void setThumbnailUrl(String thumbnailUrl) {
            this.thumbnailUrl = thumbnailUrl;
        }

        public String getThumbnailUrl() {
            return thumbnailUrl;
        }
    }

    public class Ingredient {

        @SerializedName("quantity")
        private double quantity;

        @SerializedName("measure")
        private String measure;

        @SerializedName("ingredient")
        private String ingredient;

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setMeasure(String measure) {
            this.measure = measure;
        }

        public String getMeasure() {
            return measure;
        }

        public void setIngredient(String ingredient) {
            this.ingredient = ingredient;
        }

        public String getIngredient() {
            return ingredient;
        }
    }
}