package edu.metrostate.Model;

public class IngredientStockData {

    private final String name;
    private final int inStock;
    private final int required;

    public IngredientStockData(String name, int inStock, int required) {
        this.name = name;
        this.inStock = inStock;
        this.required = required;
    }

    public String getName() {
        return name;
    }

    public int getInStock() {
        return inStock;
    }

    public int getRequired() {
        return required;
    }
}
