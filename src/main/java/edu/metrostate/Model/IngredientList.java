package edu.metrostate.Model;

import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;

public class IngredientList {

    private ArrayList<Ingredient> ingredients;
    private String name;
    private int listType;
    private static int lastId = 0;
    private boolean isLoaded;

    public IngredientList() {
        this.ingredients = new ArrayList<>();
        this.isLoaded = false;
        lastId = 0;
    }

    public IngredientList(ArrayList<Ingredient> ingredients, String name, int listType) {
        this.ingredients = ingredients;
        this.name = name;
        this.listType = listType;
        this.isLoaded = true;
    }

    public void addIngredient(Ingredient ingredient) {
        if (!checkIngredientInList(ingredient)) {
            ingredients.add(ingredient);
        }
    }

    public boolean checkIngredientInList(Ingredient ingredient) {
        for (Ingredient ingredient1 : ingredients) {
            if (ingredient1.getIngredientID() == ingredient.getIngredientID()) {
                return true;
            }
        }
        return false;
    }

    public void updateIngredient(Ingredient ingredient, int newQuantity, Date newDate){
        ingredient.UpdateIngredient(ingredient.getIngredientID(), newQuantity, newDate);
    }

    public ArrayList<Ingredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Ingredient> ingredients) {
        this.ingredients = ingredients;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getListType() {
        return listType;
    }

    public void setListType(int listType) {
        this.listType = listType;
    }
}

