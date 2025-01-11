# Non-functional Requirements

## Non-functional requirement 1: Backup
Our system will have records about
1. Expiration dates and quantities of pantry items.
2. Weekly grocery lists
3. Recipes
4. Meal plans
The way all these records interact to solve the user's problems means our system has a database. This does not imply a 
relational SQL database. But to assure the records are not tightly coupled to a particular UI or architecture it should 
be possible to import/export the records. Supporting backup/restore operations can make our application RESTful so people
can use it from anywhere.

### Other Nonfunctional Requirements Provided by a Backup Feature 
Adding backup functionality also supports nonfunctional requirements for
* Disaster recovery
* Resilience
* Configuration management

### Mechanisms for Complying with the Backup Requirement
User Interfaces, Tools, processes, resources and files will be used to meet the backup requirement.

#### User Interface
The controls and records pertaining to backup/restore will only be accessible through the Backup/Restore menu.
There are two mutually exclusive ways of accessing the Backup/Restore Menu.
1. From the Backup button in the Main Menu.
2. The Backup button in the Navigation Bar.
The Navigation Bar is not present in the Main Menu so we need the Backup Button in both places.

##### Backup User Interface Layout
1. The Backup Button is removed from the Navigation Bar when we are in the Backup Menu.
2. Messages about the last 3 backup operations appear in an immutable Messages pane. The Messages Pane is at the top of the page.
3. A Dropdown list of successful backups in descending order of date. Selecting an item from this dropdown brings the BackupTransactionRecord 
page for the item.
4. A Backup button which launches the Backup process.

#### Tools
Java's native Serialization features might provide some of this requirement. Ideally we would use a database probably SQLite
for storing the records. Using a RDBMS is not required for enabling backup but making the operations consistent is easier to 
support with a RDMBS especially for restoring correctly. We will use an SQLLite implementation that supports exporting the database
to JSON or XML. 

#### Processes
There are two processes. Both processes are ACID transactions.
##### Backup Process 
This can only be launched by clicking the Backup button in the Backup/Restore menu. A Popup appears showing percent completing of the backup. The only results are success or failure.
Regardless of outcome control returns to the Backup Main Menu.

1. Success
   1. A message is at the top of the Messages stack reporting backup was successfully made.
   2. The new backup is at the top of the Backup History Dropdown.
2. Failure
   1. A message describing the failure's nature appears at the top of the Messages stack.
   2. If the backup fails a message describing the failure appears in the messages pane. 

##### Restore Process
The RestoreBackup process can only be launched by Clicking a record in Backup History Dropdown. Clicking a record brings the
BackupTransactionRecord pane to the foreground.

##### Layout of BackupTransactionRecord Pane
1. Textbox which shows date, size, and location of backup.
2. A button to delete the backup.
3. A button to restore from the backup.

Clicking the Restore button brings a Confirmation Dialog warning that restoring the backup will overwrite the current records
If the user confirms this a progress bar shows. All other system operations are halted until the restoration process completes.

##### Restoration Outcomes
Restorations are atomic, They either succeed or fail. Regardless of outcome control returns to Backup Main Menu
so we can see the messages. The only difference between them is the message describing the restoration outcome.

#### Canceling Backups and Restorations
Pressing the cancellation button during a backup or restoration rolls back operations up to that point and returns 
control to the Backup/Restore Menu.

#### Deletion
Deleting a backup deletes the directory of the backup. The backup will be removed from the Backup History Dropdown
but a record of the deletion, the backupId and DateTime of the deletion will be in the Deletion History Dropdown.

#### Resources
A directory will be made for each backup in the backups directory. The backup folder's name will be an id and timestamp.

## Non-functional requirement 2: Durability
Most of our use cases change the system by either creating, updating, or deleting records. It is essential any
and writes are committed. There are two aspects of durability.
1. Persistence during a session.
2. Persistence across restarts and shutdowns.
Durability on a running system is easy to support with either critical sections and locking or using a relational database
because they provide Atomicity, Immutability, Consistency, and Durability organically. A relational database would also
provide persistence across restarts and shutdowns.

### Mechanisms for Complying with the Durability Requirement
Writing a custom feature in Java to implement durability is a bad idea. A SQLLite database will carry most of the weight for durability.
SQLLite will also make thread safety easier to manage. The database can also have fine grained control when transactions get
recorded. Durability should be implemented seamlessly without the user being aware and having to intervene to maintain 
transaction consistency and durability.

#### Single versus Multi Threaded
A multithreaded system can be more responsive with the drawback of having to control access to shared resources with critical sections and locks.
To improve durability our goal is to make the application single threaded.

#


## Non-functional requirement 3: Usability
People have different abilities and ways of interacting with systems. We are also good at somethings and prefer the
computer assist us by dealing with things humans would prefer not to do. For our purposes usability's realms are
1. Predictable consistent interface
2. Legible controls, state, and outputs
3. High responsiveness
4. Configurable
5. Easy to troubleshoot
6. High mean time between failures
7. Outputs and feedback to accurately describe the system state.
A system which meets these criteria is easy to use. Designing it might be challenging.

### Mechanisms and Methods for Achieving High Usability

#### Creating a Predictable Consistent Interface
* The navigation bar will be on the left. 
* The top pane will have buttons corresponding to different components of the subsystem. 
* The center pane will show state, selectors, checkboxes, and buttons for doing CRUD operations.
* Arrows will be used to launch and processes and direct to the next part of the process.

#### Creating Legible Controls State and Outputs
In this system being readable is only one type of legibility. Visually impaired people should be find the system navigable.
Whenever possible pictures should accompany text. We cannot incorporate braille readers and keyboards into our system; but
if the UI is loosely coupled to the system we can add support for visually impaired folks or someone could use our APIs 
to develop one.

#### High Responsiveness
When people give input from the keyboard or mouse they want a response in under a second. We can do this by making sure our
transactions can run quickly. Even if we cannot process the user's input right away our system needs to give that information
to the user instead of delaying. This might require transactions having a timeout value.

#### Configurable
Our system is solving two related problems that are clearly defined. This does not require many options, menus, and configurations.
If people access it from a wall mounted touchscreen, their phone, or a website the interface should be fairly consistent on 
all of them. To make sure it works on all these systems a simple layout with few options is better and less distracting.
For our purpose configurable is more about being able to control the order they might want to put items in a menu deciding
where data can be imported from and exported to.

#### Easy to Troubleshoot
Because our system follows the ACID principle things either work or they fail. If they fail the system rolls things back 
so the old state is maintained. We should also prevent things like entering negative numbers or entering blank titles or names
Any error messages should be clear and easy to understand to people with no technical ability.

#### High Mean Time Between Failures
Ideally this system would be accessible through a dashboard on the fridge or by the pantry.  The software must be designed 
so it does not crash unless the hardware fails. It should not require resets unless an update is being pushed over the network.

#### Outputs Should Accurately Describe the System State
At the current time the only reason we can anticipate for outputs not corresponding to the state is inconsistency from race
conditions. To prevent this our application will not be multithreaded.


# Use Case Description 1

## Use case name
Add and subtract from kitchen inventory

## Actors
1. Actor 1
The user/person managing the kitchen inventory.

2. Actor 2
The system/our application handling inventory management.

## Use case goal
To allow the user to add new or to existing items in the kitchen inventory. Also to subtract from items to ensure that 
the inventory is up to date.

## Primary Actor
User

## Preconditions
The user has the application available to them.
The user knows the amount that they are about to add/subtract from the inventory. 

## Basic flow
1. The user navigates to the inventory management section of the application by pressing on it through the src.main.main screen.
2. The system will display the current inventory list, search bar to find items, add new items button, and filter to find
the item that they are updating the inventory for. 
3. The user selects the option to edit an item quantity from the inventory either through the add new button or edit 
quantity button through the list.
4. If adding to existing item .
   - 4.1 The user will enter the amount added to inventory and new expiration date and then press the + sign.
   - 4.2 The user submits the information by pressing the save button.
   - 4.3 The system validates the input (checking for valid quantity).
   - 4.4 The system updates the inventory with the increased quantity. 
   - 4.5 The system confirms the addition and displays the updated inventory list. 
5. If subtracting an item.
   - 5.1 The user enters the quantity to be subtracted.
   - 5.2 The user submits the information by pressing the save button.
   - 5.3 The system validates the information(ensuring the current quantity isn't negative).
   - 5.4 The system updates the inventory or removes the item if the quantity is zero.
   - 5.5 The system confirms the subtraction and displays the updated inventory list.
6. If adding a new item
   - 6.1 The user enters the item name, quantity, and expiration date.
   - 6.2 The user submits the information.
   - 6.3 The system validates the input(checks for valid quantity and item details).
   - 6.4 The system updates the inventory with the new item.
   - 6.5 The system confirms the addition and displays the updated inventory list

## Alternative flows

### Alternative flow 1
Invalid Item Details
1. Basic flow steps 1 through 6.3.
2. The user submits information for adding a new item to the inventory 
3. The system detects invalid details (entered negative quantity or expiration date prior to current date)
4. The system displays an error message indicating the issues with the item details.
5. The user corrects the details and resubmits the information.

### Alternative flow 2
Insufficient Quantity for subtraction
1. Basic flow steps 1 through 5.3.
2. The user enters a quantity to subtract that exceeds the available quantity.
3. The system detects the issue and displays and error message indicating insufficient quantity.
4. The user adjusts the quantity to be subtracted or cancels the operation.

# Use Case Description 2

## Use case name
Search Inventory

## Actors
1. Actor 1
The user/person managing the kitchen inventory.

2. Actor 2
The system/our application handling inventory management.

## Use case goal
To enable the user to search for specific items within the kitchen inventory, allowing them to quickly locate items.

## Primary Actor
User

## Preconditions
The user has the application available to them.
The user knows basic information for the item that they are looking for within the application.

## Basic flow
1. The user navigates to the inventory management section of the application by pressing on it through the src.main.main screen.
2. The system displays the inventory search bar, optional filter criteria, and inventory list.
3. The enters a search term (item name, category, primary macro nutrient)
4. The user can optionally apply sorting options to refine the search results (alphabetical, expiration date, quantity)
5. The user submits the search query.
6. The system process the search results displaying the item list that matches the user defined fields.
7. The user selects a search result to view more detailed information about the item if needed.

## Alternative flows

### Alternative flow 1
No matching results
1. Basic flow steps 1 through 5.
2. The system processes the search request but finds no matching items.
3. The system displays a message indicating that no items match the search criteria.
4. The user may adjust the search term or filters and retry the search.

### Alternative flow 2
Search Filter Application
1. Basic flow steps 1 through 4.
2. The user applies multiple sorting options to narrow down the search, without a search term.
3. The system processes the search with the applied filters
4. The system displays the sorting search results.
5. The user can further refine with a search term or adjust the filters as needed.

# Use Case Description 3

## Use case name
Sort Inventory

## Actors
1. Actor 1
The user/person managing the kitchen inventory.
2. Actor 2
The system/our application handling inventory management.

## Use case goal
To enable the user to search through the inventory by sorting the inventory list using filters

## Primary Actor
User

## Preconditions
The user has the application available to them.
The user knows basic information for the item/s that they are looking for within the application.

## Basic flow
1. The user navigates to the inventory management section of the application by pressing on it through the src.main.main screen.
2. The system displays the current inventory list along with the filter/sorting options.
3. The user selects a sorting criteria from the available options.
4. The user submits the sorting request.
5. The system processes the sorting request and arranges the inventory items based on the selected criteria.
6. The system updates the display to show the sorted inventory list. 

## Alternative flows

### Alternative flow 1
Invalid Sorting Criteria
1. Basic flow steps 1 through 3.
2. The user selects an invalid criteria (trying ti sort alphabetical descending and ascending).
3. The system displays an error message indicating that the selected criteria is not valid
4. The user selects a valid sorting criteria and resubmits the request.

### Alternative flow 2
No Inventory items
1. Basic flow steps 1 through 3.
2. The user submits the sorting request.
3. The systems detects that there are no inventory items to sort.
4. The system displays a message indicating that the inventory is empty and sorting cannot be performed.

# Use Case Description 4

## Use case name
Add and Remove Recipes

## Actors
1. Actor 1
The user/person managing the kitchen inventory.
2. Actor 2
The system/our application handling inventory management.

## Use case goal
To be enable the user to add and remove their own recipes along with the default applications recipes.

## Primary Actor
User

## Preconditions
The user has the application available to them.
The user has their own recipes details. This includes the quantity of items and the steps to make the meal.

## Basic flow
1. The user opens the recipe list through the src.main.main screen of the application.
2. To add a recipe
   - 2.1 The user presses on the add recipe button
   - 2.2 The user will fill out essential information to the recipe. This includes name, cuisine, ingredients and their 
   amount, and the steps to completing the dish.
   - 2.3 The user will save the recipe by pressing the save button.
   - 2.4 The system will validate the input and ensure that everything was filled in.
   - 2.t The system will add the recipe to the recipe list.
3. To remove a recipe
    - 3.1 The user will search for the recipe that they are looking to delete using the search bar or the filter/sort
   methods
    - 3.2 The user will press on the recipe that they were searching for.
    - The user will then press on the recipe delete button. 
    - The system will then removes the selected recipe and returns to the recipes list.


## Alternative flows

### Alternative flow 1
Recipe validation error
1. Basic flow steps 1 through 2.4.
2. The system detects that the user did not enter all of the required information.
3. The system will give the user another chance to enter the required information or allow the user to cancel returning 
them to the recipe list. 

### Alternative flow 2
Recipe addition conflict
1. Basic flow steps 1 through 2.4
2. The system detects that the user already has a recipe identical to the one that they are trying to enter 
3. The system will give the user another chance to enter a new recipe or allow the user to cancel returning them to the 
recipe list. 

# Use Case Description 5

## Use case name
Search through Recipes

## Actors
1. Actor 1,
The user/person managing the kitchen inventory.
2. Actor 2,
The system/our application handling inventory management.

## Use case goal
To enable the user to effectively search through their own recipes and the default applications recipes.

## Primary Actor
User

## Preconditions
The user has the application available to them.
The knows the details about the recipe they are searching for. This can include cuisine type and or name.

## Basic flow
1. The user opens the recipe list through the src.main.main menu by selecting recipes.
2. The user inputs search details such as recipe name or cuisine type.
3. The user submits the search request.
4. The system processes the search request and retrieves matching recipes from the list.
5. The system displays the search results to the users through the recipe list.
6. The user selects a recipe form the search results to view more details.

## Alternative flows

### Alternative flow 1
No Matching Recipes
1. Basic flow steps 1 through 3
2. The user inputs search details but no recipes match the criteria.
3. The system displays a message indicating that no recipes were found that match the search criteria.
4. The user can modify the search criteria and attempt again or cancel the search.

### Alternative flow 2
User Cancels Search 
1. Basic flow steps 1 through 4
2. Before the search results are returns the user decides to cancel the search process.
3. The system acknowledges the cancellation request and stops processing the search.
4. The system displays a confirmation message indicating that the search has been successfully canceled.
5. The user is returned to the previous state.

# Use Case Description 6

## Use case name
Check and Add Recipe Ingredients with Inventory

## Actors
1. Actor 1,
The user/person managing the kitchen inventory.
2. Actor 2,
The system/our application handling inventory management.

## Use case goal
To enable the user to identify what items are missing from their current inventory that the recipe requires. This way 
the user is easily prepared to create the whole recipe. 

## Primary Actor
User

## Preconditions
The user has the application available to them.
The user has decided what type of recipe that want to make and have selected that through the recipe screen. 

## Basic flow
1. The user selects recipes from the src.main.main screen.
2. The user selects a recipe from the recipe list.
3. The system displays all the information about the recipe including the list of ingredients required for the selected
recipe.
4. The system compares the recipe ingredients with the current inventory.
5. The system identifies which ingredients are missing in the current inventory.
6. The system displays a list of missing ingredients and their required quantities. 
7. The system allows the user to add to the inventory of the current missing items.

## Alternative flows

### Alternative flow 1
Recipe ingredients not found in inventory
1. Basic flow steps 1 through 5
2. The system finds that there is no data available for the item required in the inventory. 
3. The system displays an error message indicating that the inventory data in unavailable.
4. The user is prompted tio either update the inventory data or get a different recipe.

### Alternative flow 2
User cancels adding missing items
1. Basic flow steps 1 through 6.
2. The user decides not to add the missing ingredients to the inventory.
3. The system confirms that no changes will be made and the user is returned to the recipe list.
4. The user can continue to view or select other recipes without making changes to the inventory,