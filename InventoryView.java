import java.util.Scanner;

/*
 * InventoryView takes user event-changing inputs 
 * and pass them to controller to trigger actions in InventoryModel.
 * After receiving the result from the mode, it will display the changes to users.
 */

public class InventoryView {

	private InventoryModel model;
	private InventoryController controller;
	
	// Variables to be stored and trigger action in Model via Controller
	private int categoryNum;
	private int operationNum;
	
	private String prodId;
	private String category;
	private String newEntries;
	private int	fieldNum;
	private String updatedEntry;
	private String[] prodFields;


	// Constructor
	public InventoryView( InventoryController controller ) {
		this.controller = controller; 
	}	

	public void start() {
	
	    // Display Main Window		
		System.out.println(" ********* BORDER'S INVENTORY SYSTEM ********* ");
		try {
			Scanner input = new Scanner(System.in);	
			do {		
			// reset categoryNum and operationNum	
			categoryNum = 0;
			operationNum = 0;
			
	    	// User selects or inputs the category number that will be set in Controller and Model
			System.out.println("\n\t\t MAIN MENU \t\t");
			System.out.println("Select and enter your selection as follow: \n" + 
					"\t(1) BOOK\n" + 
					"\t(2) CD\n" + 
					"\t(3) DVD\n" +
					"\t(0) EXIT\n" );
			
			try {
				categoryNum = Integer.parseInt(input.nextLine());
			} catch (NumberFormatException e) {
			} // end try-catch 
			
			// Selected category number is passed onto Controller and Model  
			try {
				if (categoryNum != 0) {
					controller.setCategory(categoryNum);
															
					System.out.println("\n\t\t OPERATION MENU \t\t");
					System.out.println("Select an operation to update the inventory databse: \n" + 
						"\t(1) CREATE a new inventory item\n" + 
						"\t(2) RETRIEVE an inventory item\n" + 
						"\t(3) UPDATE an inventory item\n" +
						"\t(4) DELETE an inventory item\n");
				
					try {
						operationNum = Integer.parseInt(input.nextLine());
					} catch (NumberFormatException e) {			
					} // end try-catch 
					
					// user inputs product ID 
					if (operationNum <= 4 && operationNum != 0) {
						System.out.println("Enter the product ID (ISBN for Books, or UPC for CD/DVD):  ");
						prodId = input.nextLine();
						controller.setProdId(prodId);
												
						/* CREATE ITEMS - NEW DATA ENTRY INPUTS */
						if (operationNum == 1) {
							// check if the product exists in the database - if it does, prompts the user the message
							if (model.searchItem(prodId)) {
								System.out.println("The product already exists in the database");
								controller.setOperation(2); //  retrieve information of product in the database
							} // end if searchItem 
							
							// otherwise, prompts user to input data for the product's relevant fields
							else {
								System.out.println("CREAT NEW INVENTORY");
			
							// print options based on specific type	
							 String[] newEntriesList = new String[prodFields.length];
							 
							 for ( int i=0; i < prodFields.length; i++ ) {
								System.out.println("Enter the product's " +  prodFields[i] +":");
								newEntriesList[i] = input.nextLine();
							} // end for loop
							
							 	/* convert the array to list for properties's values
							 	 * wires the values to Controller and View */
							 	newEntries = String.join(",", newEntriesList); 
							 	controller.setNewEntries(newEntries);
							 	controller.setOperation(operationNum);
				
							} // end else
						} // end if operation == 1
						
						/* RETRIEVE ITEMS */ 
						if (operationNum == 2) {
							System.out.println("RETRIEVE INVENTORY");
							controller.setOperation(operationNum);
						}
						
						/* UPDATE ITEMS - MODIFY EXISTING DATA ENTRIES */
						if (operationNum == 3) {
							System.out.println("UPDATE INVENTORY");
							
							// check if product exists in the database - if it does, prompts the user the message
							if (model.searchItem(prodId)) {
								System.out.println("Select the field you would like to update: "); 
							
							// print options based on specific type
							for (int i=0; i < prodFields.length; i++) {				
									System.out.printf("\t(%d) %s\t\n", i+1, prodFields[i]);		
							} // end for loop
							
							try {
								// collect the field number code input by user
								fieldNum = Integer.parseInt(input.nextLine());
								controller.setFieldNum(fieldNum-1); // array first index is 0
								
								// prompts user to input updated entry for the selected field
								if (fieldNum-1 < prodFields.length && fieldNum != 0 ) {
									System.out.println("Enter the updated entry: "); 
									updatedEntry = input.nextLine();
								
									controller.setUpdatedEntry(updatedEntry);
									controller.setOperation(operationNum); 
								
								} else {
									System.out.println("Invalid input, choose between 1 and " +
										prodFields.length);
								} // end else
								
								} catch (Exception e) {
									System.out.println("Invalid input, choose between 1 and " +
														prodFields.length);
								} // end try-catch
							} else model.retrieveItem(prodId, prodFields);
						} // end if operation = 3  
					 	
						/* DELETE ITEMS */
						if (operationNum == 4) {
							System.out.println("DELETE INVENTORY");
							controller.setOperation(operationNum);
						} // end if operation == 4
						
					} else { // if user inputs invalid operation code number 
						System.out.println("Invalid input for operation, choose between 1 and 4");
						} // end if operation <=4 
				} else break; // exit if user chooses to exit
			} catch (Exception e) {
					System.out.println("Invalid input for category");
					System.out.println("Choose between 1 and 3, or enter 0 or any other key to exit");
			} // end try-catch validating input for category
		
		} while (categoryNum != 0);
			System.out.println("Exiting the program ...\n"
			+ "Thank you for using Border's inventory system");		
			input.close(); 
		} // end try
		
		catch (Exception e) {
			System.out.println("Invalid input for category number");
		} // end catch
		
	} // end start
	
	
	
	public void setModel( InventoryModel model ) {
		this.model = model;
	} // end setModel
	
	
	/** Notify changes in product Category to Model */
	public void notifyCategory(int num) {
		model.getCurrentList();
		prodFields = model.getProdFields();
		category = model.getCategory();
	} // end notifyCategory

	/** Notify changes in operation to Model */
	 public void notifyOperation(int num) {
		model.implementOperation();	
	 } // end notifyCategory

	
} // end InventoryView class 
