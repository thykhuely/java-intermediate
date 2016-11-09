import java.util.Properties;
import java.util.Set;

/*
 *  Class Dvd represents DVD items in the inventory.
 * It is the subclass of class Product and override listProperties method
 */

public class Dvd extends Product {
	
	// Constructor
	public Dvd (Properties dataTable, String inventoryFile) {
		super(dataTable, inventoryFile);
	}
	
	/** Output properties values */
	@Override
	public void listProperties() { 
		System.out.println("\n=========== Current Inventory Listing ===========\n");
		Set< Object > keys = getDataTable().keySet(); 
		System.out.printf("%s\t%s\n", "UPC", "Title,Director,Year,Quantity in Stock,Price" );
		for ( Object key : keys )
			System.out.printf("%s\t%s\n", key, getDataTable().getProperty( (String) key ));
		System.out.println("\n=================================================\n");

	} // end loadProperties

}