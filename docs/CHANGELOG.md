# Milestone 1

- [Teja] Created and orchestrated the repository for the group.
- [Quintin, Hieu] Constructed the description of the project and stated the participants. 
- [Quintin, Teja, Hieu, Banji, Hunter] Brainstormed on project ideas and features. 
- [Quintin, Hieu] Typed out the features. 
- [Teja] Created the wireframe on Figma.
- [Quintin] Edited then added the wireframe from Figma to Intellij then GitHub.
- [Hunter] Created and added Meal Plan Calendar Wireframe.

# Milestone 2

- [Teja] Created and added the UML class diagrams along with a brief description.
- [Hieu] Created and added use case diagram.
- [Banji] Wrote details about nonfunctional requirements.
- [Quintin] Wrote the use case descriptions.
- [Hunter] Wrote the entities. 
- [Teja] Changed the UML class diagram to better align with the requirements. Added pointers to explain the relations 
- of classes.

# Milestone 3
- [Quintin] Created and added the MVC sequence diagram.
- [Hieu] Created DailyMealPlanner and MealPlanner classes.
- [Teja] Created empty classes to represent the file structure.
- [Teja] Changed location of DailyMealPlanner and MealPlanner classes by Hieu, and Dairy by Hunter.
- [Quintin] Added methods to the view files and edited the diary class to match the rest of the package.
- [Hunter] Created the methods inside the ingredients class. Also removed the categories class to as they can be incorporated inside the ingredients.
- [Teja] Created RecipeListModel. Added methods to GroceryListController.
- [Quintin] Created and added the UML diagram
- [Banji] Created and added JavaFX application views for the Adding a Recipe use case.
- [Banji] Created and added JavaFX application View for the Recipe repository's model. The files are in the resources folder.
- [Banji] Discussed and shared approaches to refactoring the Recipe class to increase its cohesion and loosely couple it to Cuisine and Ingredients classes. 

# Milestone 4
- [Banji] Created package for refactoring models to separate responsibilities keeping track of inventory
          managing shopping list and Recipe ingredients. Minimized number of fields in refactored classes 
          with composition to minimize inheritance. Designed interfaces so differing food entities can implement
          NutritionalChart and Macronutrient. Confirmed code can be easier to manage without affecting current 
          functionality.
- [Quintin] Created the Inventory page and to inventory page. Also added code to enable the user to add items to the recipe
            and ingredients while ensuring that this would stay with the user while the app was open. 
- [Hieu] Created Implementation.md. outline.
- [Hunter] Created and implemented editing ingredient quantities. Also created the underlying functions in models it do that.
- [Teja] Created and linked FXML view files. Implemented recipe section of the application where the user can add a new recipe.
            Implemented detailed pop up views and filled in all the values. Implemented attach image in ingredient section.
            Fixed bugs.

# Milestone 5
- [Quintin] Implemented a sql lite database for data persistence for ingredients
- [Quintin] Added some exception handling on the add to ingredients to require the user to only enter a name, expiry date,
and quantity allowing them to skip the nutritional chart values and automatically assigning them to null if they do.
- [Quintin] Deleted the singleton pattern and created a list for the table view straight from the db for ingredients.
- [Teja] Migrated the recipe, nutritionalChart, Cuisine functionality to SQLite database.
- [Hunter] Implemented the edit ingredient function with the db. Which allows the user to change the quantity and expiration date of items.
- [Hieu] Implemented recipe delete feature.
- [Teja] Fixed data discrepancy with Nutritional chart and Cuisine tables.
- [Quintin] Added the ability to delete from the inventory table.

# Milestone 6
- [Quintin] Added the methods to both recipes and ingredients to be able to search the database.
- [Teja] Linked Ingredients and Recipes. Implemented search and add Ingredients to recipe.
- [Teja] Added Check Ingredients from Recipe pop up. Added functionality for primary ingredient.
- [Teja] Populated Recipes table for each Ingredient pop up and Ingredients table in Recipe pop up.
- [Teja] Fixed bugs. Refactored some methods to extract functionality into separate methods. Created frequently used methods in utility class.
- [Teja] Added type checking to 'add a new recipe screen'.