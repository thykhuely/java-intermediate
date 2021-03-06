import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.swing.table.DefaultTableModel;

/*
 * InventoryModel stores and handles data.
 * It receives the InventoryView's input from the InventoryController  
 * and notifies any changes to the View, so that InventoryView can display
 * the updates to user.
 */

public class InventoryModel {
	
	private InventoryView view;
	
	//private String inventoryFile;
	private String inventoryFile; 
	private String prodId;
	private String newEntries;
	private int fieldNum;
	private String updatedEntry;
	private int inventoryIndex;
	
	private Category type;
	private Operation operation;
	private enum Operation {CREATE, RETRIEVE, UPDATE, DELETE};
	
	private Properties bookDataTable = new Properties(); 
	private Properties cdDataTable = new Properties(); 
	private Properties dvdDataTable = new Properties(); 
	
	private Book book = new Book( bookDataTable, "bookprops.dat" );
	private Cd cd = new Cd( cdDataTable, "cdprops.dat" );
	private Dvd dvd = new Dvd( dvdDataTable, "dvdprops.dat" );
	
	private Product[] inventoryItems = new Product[]{book, cd, dvd}; 
	
	// Constructor
	public InventoryModel() {
	}
	
	public void setView(InventoryView view) {
		this.view = view;
	}
	
	/** Set the inventory file following the user input from View */	
	public void setInventoryFile(String filename) {
		this.setInventoryFile(filename);
	}
	
	/** Set the inventory file following the user input from View */	
	public String getInventoryFile() {
		return inventoryFile;
	}

	/** Select product Category following triggered action from View */	
	public void setCategory(int num) {
		
		// select the inventory items in the array 
		setInventoryIndex(num-1); 
		
		// set Category type to generate specific product fields
		switch (num) {
			case 1: type = Category.BOOK; break;
			case 2: type = Category.CD; break;
			case 3: type = Category.DVD; break;
		} // end switch
			
		view.notifyCategory(num);
	} // end setCategory
		
	
	public void setInventoryIndex(int num) {
		inventoryIndex = num;
	}
	
	public int getInventoryIndex() {
		return inventoryIndex;
	} 
		
	/** Get the specific product fields for each category */
	public String[] getProdFields() {
		return type.getProdFields();
	}
	
	/** Get the specific product category  */
	public String getCategory() {
		return type.getCategory();
	}

	public void setOperation(int num) {
		switch (num) {
			case 1: operation = Operation.CREATE; break;
			case 2: operation = Operation.RETRIEVE; break;
			case 3: operation = Operation.UPDATE; break;
			case 4: operation = Operation.DELETE; break;
		} // end switch
		view.notifyOperation(num);
	}	
	
	/* 
	 * Set and get information between View-Controller-Model 
	 */
	
	/* Collect and set product ID from View for all operations */ 

	public void setProdId(String inputId) {
		prodId = inputId;
	}
	
	public String getProdId() {
		return prodId;
	}
	
	/*  Accessor & Mutator for createItem Method:
	 *  Collect and set new product entries from user inputs in View */
	
	public void setNewEntries(String entries) {
		newEntries = entries;
	}
	
	public String getNewEntries() {
		return newEntries ;
	}
	
	/* Accessor & Mutator for updateItem Method: 
	 * Collect & set the field and updated entries from user input in Views */
	
	public void setFieldNum(int num) {
		fieldNum = num;
	}
	
	public int getFieldNum() {
		return fieldNum;
	}
	
	public void setUpdatedEntry(String entry) {
		updatedEntry = entry;
	}
	
	public String getUpdatedEntry() {
		return updatedEntry ;
	}

	/* Methods to implement triggered action in View that includes  
	 * listing, create, retrieve, update and delete data entry */
	
	public void getCurrentList() {
		inventoryItems[getInventoryIndex()].getCurrentList();
	}
	
	
	public boolean searchItem(String prodId) {
		return inventoryItems[getInventoryIndex()].searchItem(prodId);
	}
	
	public void createItem(String prodId, String userinput, String[] prodInfo) {
		inventoryItems[getInventoryIndex()].createItem(prodId, userinput, prodInfo);
	}

	public void retrieveItem(String prodId, String[] prodInfo) {
		inventoryItems[getInventoryIndex()].retrieveItem(getProdId(), getProdFields());
	}

	public void updateItem(String inputId, int fieldNum, String updatedEntry, String[] prodInfo) {
		inventoryItems[getInventoryIndex()].updateItem(inputId, fieldNum, updatedEntry, prodInfo);

	}
	
	public void deleteItem(String inputId, String[] prodInfo) {
		inventoryItems[getInventoryIndex()].deleteItem(inputId, prodInfo);

	}
	
	/** Implement operation to update View */
	public void implementOperation() {
		
		if (operation == Operation.CREATE) {
			System.out.println("model implement operation being called");
			this.createItem(getProdId(), getNewEntries(), getProdFields()); 
		}
		else if (operation == Operation.RETRIEVE) {
			retrieveItem(getProdId(), getProdFields());
			
		}
		else if (operation == Operation.UPDATE) {
			this.updateItem(getProdId(),getFieldNum(),getUpdatedEntry(), getProdFields());
		}	
		else if (operation == Operation.DELETE) {
			this.searchItem(getProdId());
			this.deleteItem(getProdId(), getProdFields());
		}	
	} // end performAction
	 

	/** Method to create a Table Model */
	public Properties getDataTable() {
		inventoryItems[getInventoryIndex()].loadProperties();
		return inventoryItems[getInventoryIndex()].getDataTable();
	} // end getTable
	
	public DefaultTableModel createDataTableModel() {
		DefaultTableModel dataModel = new DefaultTableModel();
		dataModel.addColumn("Product ID");
		
		// create table headers
		for (int i=0; i < getProdFields().length; i++) {
			dataModel.addColumn(getProdFields()[i]);
		}
		Set< Object > keys = getDataTable().keySet(); 
		Iterator<Object> iter = keys.iterator();
		
		// add key (product ID) and corresponding values to the table
		while (iter.hasNext()) {
			String key = iter.next().toString();
			String value = getDataTable().getProperty(key);
			
			String[] row =  new String[getProdFields().length+1];
			row[0] = key; 		
			String[] dataEntryList = value.split(",");
			for (int i=0; i < dataEntryList.length; i++) {
				row[i+1] = dataEntryList[i];
			}
			dataModel.addRow(row);
		}
		return dataModel;
		
	} // end method createDataTableModel

	public String getProductInfo() {
		String value = getDataTable().getProperty(getProdId());
		String[] dataEntryList = value.split(",");
		String result = "The latest up-to-date entry of the product is: \n\n"
						+ "Product ID: " + getProdId() + "\n";	
		for (int i=0; i < getProdFields().length; i++) {
			result += getProdFields()[i] + ": " + dataEntryList[i] + "\n"; 
		}
		return result;
	}
	
} // end InventoryModel
