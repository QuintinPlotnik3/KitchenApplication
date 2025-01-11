CREATE TABLE IngredientRecipe (
    ingredientID INTEGER ,
    recipeID INTEGER,
    quantity INTEGER,
    PRIMARY KEY (ingredientID, recipeID),
    FOREIGN KEY (ingredientID) REFERENCES IngredientTable(ingredientID),
    FOREIGN KEY (recipeID) REFERENCES RecipeTable(recipeID)
)
