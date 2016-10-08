##**INVENTORY SYSTEM PROJECT** 

###**1. INTRODUCTION**

The Inventory Management System (IMS) is a persistent storage system that tackled the inventory management issue of a multi‑media store (selling books, CDs and DVDs). The system uses a command-line based interface to display inventory data to the users. The system, based on a MVC architecture, helps to manage the inventory by keeping track of the inventory level using the basic functions to create, update, edit and delete items. It can be implemented as a runnable jar file. The database to store inventory data will use a DAT file, separated for each product category (book, CD and DVD).

###**2. SYSTEM ARCHITECT**
  ####**_1. Interface `InventoryRepository`:_**
  - The interface that contains method to create new item, search, retrieve information, update and delete products’ value fields in the database. These methods define the general approach of how the model can implement the service requests by users without specifying implementation detail

  ####**_2. Super abstract class `Product`:_**  
  - The class implements InventoryRepository interface service methods: getCurrentList, createItem, retrieveItem, updateItem, and deleteItem that perform basic CRUD functions
  - The class also run save, load and listProperties methods that produces output, load and save updated data entered by users.
  - The constructor for contains 2 parameters:    
        1. A `Properties` object that store persistent product data – with a unique key as the product’s ID (ISBN for books and Universal Product Code(UPC) for CDs and DVDs) and a comma-separated String of product’s relevant fields     
        2. A String contains the name of a DAT file which stores the products’ data and will be read and written by the system
  - The superclass is extended by 3 subclasses: `Book`, `Cd` and `Dvd`, each of which has its own `getCurrentList` method that accommodates the need to produce message outputs of particular fields

  ####**_3. Enum `Category`:_**
  - The enum type Category has 3 variables BOOK, CD and DVD – each represents the products that the media store is selling. Each enum constant is declared with:
     1. name of the product’s category (Book, CD and DVD) and
     2. product’s relevant fields (e.g. ISBN/UPC, Title, Author/Artist/Director, etc.) as a comma‐separated String.
  - Category also allows the system to retrieve the product fields through the accessor methods, on which the model will depend to wire the information to the View.


 ####**_4. MVC Architecture InventoryView–InventoryController-InventoryModel:_**
  - The program is designed to handle data‑changing events (whenever the users request to create/update/delete data). There are 3 components:
      1. `InventoryView`: The InventoryView component is a text-based console view that uses the Scanner class to manage keyboard input from users.
      2. `InventoryController`: It connects the user-generated events and data actions. The commands invoked in the View is passed on from the View to the Controller.
      3. `InventoryModel`: The model provides access to the database and the means by which product’s data is retrieved and manipulated. It is notified and accepts the commands from the Controller to change state, and then passes onto the information through the Controller to update the display
