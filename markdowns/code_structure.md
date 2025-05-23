# Design Reference on Code Structure
[Back to README](../README.md)

## Activities
Stores the list of activities and the Application class.

## Fragments
Stores all the fragments.

## Data
- Core
  - Use-cases for fetching data from and storing data to repositories
  - AppModule: manages the initialization and fetching of all use-cases
- Datasource
  - Daos for data exchange with the underlying RoomDatabase
  - AppDatabase for management of all Daos and changes in the tables
- Model
  - Entities
    - Entities specified as table records within the database
  - Pojo
    - A collection of entities that represent information relevant to each functionality
- Repository
  - As its name suggests - stores all the repositories for data exchange
- Structure
  - Holds the classes that are currently not stored in the database
- Tags
  - Stores the enum classes for representing the states of entities and ui
- Util
  - Utility classes for data-related use

## Presentation
- Dialog
  - Holds classes that are related to the ui of a dialog
  - Components: subfolder within each xxdialog directory for a specific ui function that holds the ui-specific classes for building a component within the bigger ui
- Ui
  - Holds classes that are related to the ui of non-dialogs
  - Components: subfolder within each xxdialog directory for a specific ui function that holds the ui-specific classes for building a component within the bigger ui
- Widget
  - Holds classes that are components of another bigger ui element
  - Classes within have very specific functionality
  - Components: subfolder within each xxdialog directory for a specific ui function that holds the ui-specific classes for building a component within the bigger ui
- Util
  - Utility classes for ui-related use

## Util
Holds the other classes that are neither directly related to ui logic nor related to data fetching and editing.
- Notification features
- Permissions
- Styling (for text, or for retrieving image resources)
- Configs