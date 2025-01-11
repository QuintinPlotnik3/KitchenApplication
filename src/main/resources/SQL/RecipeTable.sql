CREATE TABLE RecipeTable (
    recipeID INTEGER PRIMARY KEY AUTOINCREMENT,--
    name TEXT,--
    cuisineID INTEGER,--
    description TEXT,--
    cookTime INTEGER,--
    servings INTEGER,--
    primaryIngredientID Integer,--
    nutritionID INTEGER,--
    ingredients TEXT,--
    instructions TEXT,--
    FOREIGN KEY (nutritionID) REFERENCES NutritionalChart(nutritionID) ON DELETE CASCADE,
    FOREIGN KEY (cuisineID) REFERENCES CuisineTable(cuisineID) ,
    FOREIGN KEY (primaryIngredientID) REFERENCES IngredientTable(ingredientID)
)