import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;


import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

/*
 * InventoryModel stores and handles data.
 * It receives the InventoryView's input from the InventoryController  
 * and notifies any changes to the View, so that InventoryView can display
 * the updates to user.
 */

public class InventoryModel2 extends AbstractTableModel {
	
	private InventoryView view;
	private int inventoryIndex;
	private String prodId;
	private String categoryTable;
	private String newEntries;
	private String field2;
	private int fieldNum;
	private String updateFieldName;
	private String updatedEntry;
	private String[] prodFields;

	private Operation operation;
	private enum Operation {CREATE, RETRIEVE, UPDATE, DELETE};
	
	private Connection connection;
	private ResultSet resultSet;
	private ResultSetMetaData metaData;
	private int numberOfRows;

	private Statement statement;
	private PreparedStatement selectAllProduct = null; 
	private PreparedStatement retrieveItem = null; 
	private PreparedStatement createNewItem = null; 
	private PreparedStatement deleteItem = null; 
	private PreparedStatement updateItem = null; 

	// keep track of database connection status
	 private boolean connectedToDatabase = false;
	private String query;
	
	public void setView(InventoryView view) {
			this.view = view;
	}

	public InventoryModel2 () {
		
	}
	 
	/** Constructor initializes resultSet and obtains its meta data object;
	 *  determines number of rows */
	public InventoryModel2 ( String url, String username,
		       String password, String query ) throws SQLException, ClassNotFoundException {
		try {
			
		 Class.forName("com.mysql.jdbc.Driver");
		
			//connect to database
		connection = DriverManager.getConnection( url, username, password );
		
		System.out.println("Database was connected");
		// create Statement to query database
	    
		statement = connection.createStatement(
	    ResultSet.TYPE_SCROLL_INSENSITIVE,
	    ResultSet.CONCUR_READ_ONLY );  
	      
	    // update database connection status
	    connectedToDatabase = true;
		System.out.println(connectedToDatabase);

	    
	    // create query that selects all entries in the data   
	    selectAllProduct = connection.prepareStatement(
	    		"SELECT * FROM " + categoryTable);

	    // create query that entries with a specific ID   
	    retrieveItem = connection.prepareStatement(
	    		"SELECT * FROM " + categoryTable + " WHERE ProductId = ?");

	    // create query that create new entries  
	    createNewItem = connection.prepareStatement(
	    		"INSERT INTO " + categoryTable +
	    		" (ProductId, " + field2 + " Year, Quantity, Price)" +
	    		" VALUES ( ?, ?, ?, ?, ? ) " );

	    // create query that delete current entries  
	    deleteItem = connection.prepareStatement(
	    		"DELETE FROM " + categoryTable + " WHERE ProductId = ?") ;
	    
	    // create query that update current entries  
	    updateItem = connection.prepareStatement(
	    		"UPDATE " + categoryTable +
	    		" SET "+ updateFieldName + " = ? " + 
	    		" WHERE ProductId = ?") ;
	    
		} catch ( SQLException sqlException ) {
			 sqlException.printStackTrace();
		} // end catch
	    
	  // set query and execute it
		setQuery( query );  
	      
	} // end constructor InventoryModel2	
	
	/** Get number of rows in ResultSet */
	@Override
	public int getRowCount() throws IllegalStateException {
		
		if (! connectedToDatabase ) 
			throw new IllegalStateException(" Not Conencted to Database");
		return numberOfRows;
	} // end method getRowCount
	
	/** Get number of columns in ResultSet */
	@Override
	public int getColumnCount() throws IllegalStateException {
		
		if (! connectedToDatabase ) 
			throw new IllegalStateException(" Not Conencted to Database");
		
		try {
			return metaData.getColumnCount();
		} catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		}
		return 0; // if problems occur above, return 0 for number of columns	
	} // end method getColumnCount
	
	/** Obtain value in particular row and column */
	@Override
	public Object getValueAt(int rowIndex, int columnIndex) throws IllegalStateException {
		
		if (! connectedToDatabase ) 
			throw new IllegalStateException(" Not Conencted to Database");
		
		// obtain a value at specified ResultSet row and column
		try {
			resultSet.absolute (rowIndex + 1);
			return resultSet.getObject( columnIndex + 1);
			
		} catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		} // end catch
		return ""; // if problems, return empty string object
	}
	
	/** Get name of a particular Column Name in ResultSet */
	@Override
	public String getColumnName( int columnIndex ) throws IllegalStateException {
		if (! connectedToDatabase ) 
			throw new IllegalStateException(" Not Conencted to Database");
			
		try {
			return metaData.getColumnName( columnIndex + 1 );
		} catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		} // end catch
		
		return ""; // if problems, return empty string for column name
		
	} // end method getColumnName
		
	/** Set new database query string */
	public void setQuery( String query ) throws SQLException, IllegalStateException {
	
	 if ( !connectedToDatabase )
	 	throw new IllegalStateException( "Not Connected to Database" );
	
	// specify query and execute it
	resultSet = statement.executeQuery( query );
	
	// obtain meta data for ResultSet
	metaData = resultSet.getMetaData();
	
	// determine number of rows in ResultSet
	resultSet.last(); // move to last row
	numberOfRows = resultSet.getRow(); // get row number
	
	// notify JTable that model has changed
	fireTableStructureChanged();
	
	} // end method setQuery
	
	
	/* Setting up queries and parameters as transferred from View */
	
	public void setInventoryIndex(int num) {
		inventoryIndex = num;
	}
	
	/** Select product Category following triggered action from View  */
	public void setCategory(int num) throws IllegalStateException, SQLException {
		System.out.println("MODEL WAS SENT CATEGORY #");

		// select the inventory items in the array 
		setInventoryIndex(num-1); 
		
		// set Category type to generate specific product fields
		switch (num) {
			case 1: categoryTable = "bookdata"; break;
			case 2: categoryTable = "cddata"; break;
			case 3: categoryTable = "dvddata"; break;
		} // end switch
		System.out.println("VIEW WAS ABOUT TO NOTIFY W CATEGORY #");	
		view.notifyCategory(num);
		System.out.println("VIEW WAS NOTIFY W CATEGORY #");

	} // end setCategory
	
	public String getCategory() {
		return categoryTable;
	}

	/** Select Operation following triggered action from View */
	public void setOperation(int num) throws IllegalStateException, SQLException {
		switch (num) {
			case 1: operation = Operation.CREATE; break;
			case 2: operation = Operation.RETRIEVE; break;
			case 3: operation = Operation.UPDATE; break;
			case 4: operation = Operation.DELETE; break;
		} // end switch
		view.notifyOperation(num);
	}	

	
	/** Method to get a list of column names in ResultSet */
	public String[] getColumnNames( String categoryTable ) throws SQLException, IllegalStateException {
		//if ( !connectedToDatabase ) 
		//	throw new IllegalStateException(" Not Conencted to Database");

		try {
			System.out.println("STRING WAS SET IN QUERY");
			System.out.println(categoryTable);
			System.out.println("QUERY WAS ABOUT TO BE EXECUTED");
			resultSet = selectAllProduct.executeQuery(); 
			System.out.println("QUERY WAS EXECUTED");

			metaData = resultSet.getMetaData();
			int numberOfColumns = metaData.getColumnCount();

			for ( int i = 1; i <= numberOfColumns; i++ ) {
				prodFields[i] = metaData.getColumnName(i); 
			} // end for loop
		
		} catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
			
		} // end catch
		return prodFields;		
		
	} // end method getColumnNames


	/** Collect and set product ID from View for all operations */ 
	public void setProdId(String inputId) {
		prodId = inputId;
	}
	
	public String getProdId() {
		return prodId;
	}
	
	/**  Accessor & Mutator for createItem Method:
	 *  Collect and set new product entries from user inputs in View */
	public void setNewEntries(String entries) {
		newEntries = entries;
	}
	
	public String getNewEntries() {
		return newEntries ;
	}
	
	/** Accessor & Mutator for updateItem Method: 
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
	
	
	/** Method to search existing product data's entry in database */
	public boolean searchItem(String inputId, String categoryTable) 
			throws SQLException, IllegalStateException  {
		if ( !connectedToDatabase ) 
			throw new IllegalStateException(" Not Conencted to Database");

		try {
			/* create query that delete current entries  
	    	retrieveItem = connection.prepareStatement(
	    		"SELECT FROM ? WHERE ProductId = ?") ;
			 */		
			retrieveItem.setString( 1, categoryTable );
			retrieveItem.setString( 2, inputId );
		
			resultSet = retrieveItem.executeQuery();
				
		} catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		} // end try-catch block 
		
		if (!resultSet.next() ) {
			return false;
		} else 
			return true; 
						
	} // end method searchItem
	
	/** Method to create new product data's entry in database */
	public void createItem(String inputId, String userInput, String categoryTable)
			   throws SQLException , IllegalStateException {
		
		if ( !connectedToDatabase ) 
			throw new IllegalStateException(" Not Conencted to Database");
			
		try {
			inputId = getProdId();
			userInput = getNewEntries();
			
			// get column names 
			String[] columnNames = getColumnNames( categoryTable );
			
			/* set parameters into query that creates new entries  
		    createNewItem = connection.prepareStatement(
		    		"INSERT INTO ? " +
		    		"(ProductId, ? , Year, Quantity, Price)" +
		    		" VALUES ( ?, ?, ?, ?, ? ) " );
			*/

			createNewItem.setString( 1, categoryTable );
			createNewItem.setString( 2, columnNames[1] );
			createNewItem.setString( 3, prodId );
			String[] list = userInput.split( "," ); 
			for ( int i = 0 ;  i <= list.length ; i++ )
				createNewItem.setString( i+4 , list[i] );
			
			resultSet = createNewItem.executeQuery(); 

			resultSet.moveToInsertRow();
		} catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		} // end try-catch block 
		
		metaData = resultSet.getMetaData();
		
		// determine number of rows in ResultSet
		resultSet.last();                   
		numberOfRows = resultSet.getRow();  
		
		// notify JTable that model has changed
		 fireTableStructureChanged();
	} // end method createItem

	
	/** Method to update existing product data's entry in database */
	public void updateItem(String inputId, int fieldNum, String updatedEntry, String categoryTable)
			throws SQLException, IllegalStateException  {
		if ( !connectedToDatabase ) 
			throw new IllegalStateException(" Not Conencted to Database");

		try {			
			// get column names 
			String[] columnNames = getColumnNames( categoryTable );

		    /* create query that update current entries  
		    updateItem = connection.prepareStatement(
		    		"UPDATE ? " +
		    		" SET ? = ? " + 
		    		"WHERE ProductId = ?") ;
			*/ 
			
			updateItem.setString( 1, categoryTable );
			updateItem.setString( 2, columnNames[fieldNum]);
			updateItem.setString( 3, updatedEntry);
			updateItem.setString( 4, inputId );
			
			resultSet = updateItem.executeQuery();
			
		} catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		} // end try-catch block 
		
		metaData = resultSet.getMetaData();
		
		// determine number of rows in ResultSet
		resultSet.last();                   
		numberOfRows = resultSet.getRow();  
		
		// notify JTable that model has changed
		 fireTableStructureChanged();
		
	} // end method updateItem
	
	
	/** Method to retrieve existing product data's entry in database */
	public void retrieveItem(String inputId, String categoryTable) 
			throws SQLException, IllegalStateException  {
		if ( !connectedToDatabase ) 
			throw new IllegalStateException(" Not Conencted to Database");

		try {
			
			/* create query that delete current entries  
	    	retrieveItem = connection.prepareStatement(
	    		"SELECT FROM ? WHERE ProductId = ?") ;
			 */
		
			retrieveItem.setString( 1, categoryTable );
			retrieveItem.setString( 2, inputId );
			
			resultSet = retrieveItem.executeQuery();
			
		} catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		} // end try-catch block 
		
		metaData = resultSet.getMetaData();
		
		// determine number of rows in ResultSet
		resultSet.last();                   
		numberOfRows = resultSet.getRow();  
		
		// notify JTable that model has changed
		 fireTableStructureChanged();
		
	} // end method retrieveItem
	
	/** Method to delete data's entry in database */
	
	/** Method to delete existing product data's entry in database */
	public void deleteItem(String inputId, String categoryTable) 
			throws SQLException, IllegalStateException  {
		if ( !connectedToDatabase ) 
			throw new IllegalStateException(" Not Conencted to Database");

		try {
			inputId = getProdId();
			
			/* create query that delete current entries  
	    	deleteItem = connection.prepareStatement(
	    		"DELETE FROM ? WHERE ProductId = ?") ;
			 */
		
			deleteItem.setString( 1, categoryTable );
			deleteItem.setString( 2, inputId );
			
			resultSet = deleteItem.executeQuery();
			
		} catch ( SQLException sqlException ) {
			sqlException.printStackTrace();
		} // end try-catch block 
		
		metaData = resultSet.getMetaData();
		
		// determine number of rows in ResultSet
		resultSet.last();                   
		numberOfRows = resultSet.getRow();  
		
		// notify JTable that model has changed
		 fireTableStructureChanged();
		
	} // end method deleteItem
		
	
	
	/** Implement operation to update View */
	public void implementOperation() throws IllegalStateException, SQLException {
		
		if (operation == Operation.CREATE) {
			this.createItem(getProdId(), getNewEntries(), getCategory()); 
		}
		else if (operation == Operation.RETRIEVE) {
			retrieveItem(getProdId(), getCategory());
			
		}
		else if (operation == Operation.UPDATE) {
			this.updateItem(getProdId(),getFieldNum(),getUpdatedEntry(), getCategory());
		}	
		else if (operation == Operation.DELETE) {
			this.searchItem(getProdId(), getCategory());
			this.deleteItem(getProdId(), getCategory());
		}	
	} // end implementAction
	
	
	 /** Method to close Statement and Connection */
	public void disconnectFromDatabase() {              
		if ( connectedToDatabase )  {
			// close Statement and Connection            
	         try   {                                            
	        	 resultSet.close();                        
	        	 statement.close();                        
	        	 connection.close();                       
	         } // end try                                 
	         catch ( SQLException sqlException ) {                                            
	            sqlException.printStackTrace();           
	         } // end catch 
	         
	         // update database connection status
	         finally {                                            
	            connectedToDatabase = false;              
	         } // end finally                             
	      } // end if
	   } // end method disconnectFromDatabase   
	
} // end InventoryModel class
