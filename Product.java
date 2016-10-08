import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

/*
 * Abstract class Product implements interface InventoryRepository methods
 * to create, retrieve, update and delete items. It also includes outputString 
 * method that outputs product's key value fields
 */


public abstract class Product implements InventoryRepository{
	
	private Properties dataTable;
	private String inventoryFile;
	private Category category;
		
	/* Constructor and Accessories */
	
	// Constructor 	
	public Product(Properties dataTable, String inventoryFile) {
		this.inventoryFile = inventoryFile;
		this.dataTable = dataTable; 
	} // end constructor 
	
	/** Get and set inventory database table */
	public Properties getDataTable() {
		return dataTable;
	} // end getDataTable
	
	public void setDataTable( Properties dataTable ) {
		this.dataTable = dataTable;
	} // end setDataTable 
	
	/** Get and set inventory file */
	public String getInventoryFile()  {
		return inventoryFile;
	} // end getInventoryFile
	
	public void setInventoryFile( String inventoryFile ) {
		this.inventoryFile = inventoryFile;
	} // end setInventoryFile

	
	/** Create a file and/or Save properties to a file */
	public void saveProperties() { 
		// save contents of table
		try { 
			FileOutputStream output = new FileOutputStream( getInventoryFile() ); 
			getDataTable().store( output, "Inventory database was saved and/or updated." );
			output.close();  
			} // end try 
					
		catch ( IOException ioException ) {
			} // end catch		
		catch ( NullPointerException nofile ) {
			} // end catch

		} // end method saveProperties 
	
	/** Load properties to a file */
	public void loadProperties() { 
	// load contents of table	
		try { 
			FileInputStream input = new FileInputStream( getInventoryFile() ); 
			getDataTable().load( input );
			input.close();  
			} // end try 
				
		catch ( IOException ioException ) {
			// create a new file if not exist
			saveProperties();
			System.out.printf("\nThe file " + getInventoryFile() + " is opened\n"  );
			} // end catch
					
		catch ( NullPointerException nofile ) {
			// create a new file if not exist
			saveProperties();
			System.out.printf("\nThe file " + getInventoryFile() + "is opened\n" );
			} // end catch

		} // end method loadProperties
			
	/** Output properties values */
	public void listProperties() { 
		loadProperties();
		System.out.println("\n========= Current Inventory Listing =========\n");
		Set< Object > keys = getDataTable().keySet(); 
		System.out.printf("%s\t%s\n", "Product ID", "Title,Author/Artist/Rating,Quantity in stock" );
		for ( Object key : keys ) {
			System.out.printf("%s\t%s\n", key, getDataTable().getProperty( (String) key ));
		} // end for loop
		System.out.println("\n=============================================\n");
		
	} // end listProperties	
	
	/** Output the list of all products */
	@Override 
	public void getCurrentList() { 
		loadProperties();
		listProperties();
	}	
	
	/** Method to create new product data's entry in database */
	@Override
	public void createItem(String prodId, String prodDetails, String[] prodInfo) {
	  loadProperties();
		 getDataTable().setProperty(prodId, prodDetails);
	     saveProperties();
	     System.out.println("The product was added to the inventory database.");
	 
	} // end createItem method
	  
	/** Method to retrieve existing product data's entry in database */
	public void retrieveItem(String prodId, String[] prodInfo) {
		loadProperties(); 
		// find value in the properties that match the product id
		String dataEntry = getDataTable().getProperty( prodId );
		
		// check if the searched product exists in the database
		if ( searchItem(prodId) ) {
			// convert the comma-separated line to an array
			String[] list = dataEntry.split( "," );
			outputString(list, prodInfo);
		} // end if
		
		else {
			System.out.println("The product does not exist in the inventory database.");
		}
	} // end searchItem 

	/** Method to update existing product data's entry in database */
	@Override
	public void updateItem(String prodId, Integer fieldNum, 
			String updatedEntry, String[] prodInfo) {
		loadProperties(); 
		String dataEntry = getDataTable().getProperty( prodId );
		
		// check if the searched product exists in the database
		if ( dataEntry != null ) {
			// convert the comma-separated line to an array
			String[] list = dataEntry.split( "," );
			
			// update the list with the new data entry
			list[fieldNum] = updatedEntry;
			outputString(list, prodInfo);
			
			// convert the data list to comma-separated string
			String updatedInfo = String.join(",",list);
			
			// update and save the data table with the new entry
			getDataTable().setProperty(prodId, updatedInfo);
			saveProperties();
		} // end if
		
		else {
			System.out.println("The product does not exist, please try again.");
		} // end else	
	} // end updateItem method
	
	/** Method to delete existing product data's entry in database */
	@Override
	public void deleteItem(String prodId, String[] prodInfo) {
		loadProperties();

		// check if the searched product exists in the database
		if ( searchItem(prodId) ) {
			
			// find value in the properties that match the product id
			String dataEntry = getDataTable().getProperty( prodId );

			// convert the comma-separated line to an array
			String[] list = dataEntry.split(",");
			outputString(list, prodInfo);
	
			// remove product out of the database and save
			getDataTable().remove(prodId);
			System.out.println("The selected product was removed from inventory database.\n");	
			
			saveProperties();
		} // end if 
		else {
			System.out.println("The product does not exist, please try again.");
		} // end else	
	} // end method deleteItem

	/** Method to search for product data's entry in database 
	 * Returns boolean value, true means the product exists in the database */
	public boolean searchItem(String prodId) {
		loadProperties();
		String dataEntry = getDataTable().getProperty( prodId );
		boolean flag = (dataEntry != null);
		return flag;
	} // end searchItem	
	
	/** Method to produce output for the data entries for the product in the database */
	public void outputString(String[] fields, String[] prodInfo) {
		System.out.println("The latest up-to-date entry of the product is: ");
		for (int i= 0; i < fields.length; i++) {
			System.out.println(prodInfo[i] + ": " + fields[i] );
		} // end for loop
	} // end searchItem
	
	
	
} // end class 
