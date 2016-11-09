import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.JTextComponent;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;

public class InventoryView extends JFrame {

	private InventoryModel2 resultSetTableModel;
	private InventoryModel2 model;

	
	// database URL, username and password
	static final String DATABASE_URL = "jdbc:mysql://localhost/inventoryDB";
	static final String USERNAME = "tkly";
	static final String PASSWORD = "tkly";
	static final String DEFAULT_QUERY = "SELECT * FROM BookData";


	private String[] categoryList = new String[] {"Please Select","Book", "CD", "DVD"};
	private String[] operationList = new String[] {"Create a new inventory item", 
										"Retrieve an inventory item", 
										"Update an inventory item",
										"Delete an inventory item"};
	
	private Panel titlePanel;
	private Panel categoryPanel;
	private Panel operationPanel;
	private Panel displayPanel;	
	private Panel createForm;
	private Panel updateForm;
	
	private JComboBox<String> jcboCategory;
	//private DefaultTableModel dataTableModel;
	private JButton[] buttonList;
	private JScrollPane scrollPane;

	private String newEntries;
	private int	fieldNum;
	private String categoryTable;
	private String updatedEntry;
	private String[] prodFields;
	private String prodInfoPrompt;
		
	/* CONSTRUCTOR */	
	public InventoryView(InventoryModel2 resultSetTableModel) {		
		
		super("Inventory System");
		this.resultSetTableModel = resultSetTableModel;
		
		try {
		model = new InventoryModel2(DATABASE_URL, USERNAME, PASSWORD, DEFAULT_QUERY);
			
		setLayout ( new BorderLayout() );
		
		/* TOP PANEL */
		Panel topPanel = new Panel(900, 550);
		topPanel.setLayout( new BorderLayout() );
		add(topPanel, BorderLayout.NORTH);
		
		/* TITLE PANEL */
		titlePanel = new Panel(900, 50) ;
		titlePanel.setLayout( new FlowLayout() );
		topPanel.add(titlePanel, BorderLayout.NORTH);

		JTextArea welcome = new JTextArea("BORDER'S INVENTORY SYSTEM");
		formatText( welcome, new Font("San Francisco Mono", Font.BOLD, 20) , false, false);
		titlePanel.add(welcome);
		
		/* DISPLAY PANEL */
		displayPanel = new Panel(900, 500);
		displayPanel.setLayout( new BorderLayout() );
		displayPanel.createBorder( "Display" );
		topPanel.add(displayPanel, BorderLayout.CENTER);	
		
		scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(
				   JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); 
		scrollPane.setVisible(false);
		displayPanel.add(scrollPane, BorderLayout.NORTH);
		
		JButton refreshButton = new JButton("Refresh Data");
		displayPanel.add(refreshButton, BorderLayout.SOUTH);
		
		// create table model
		updateTable();
		
		// Register listener for refresh button 
		CategoryActionHandler actionHandler = new CategoryActionHandler();
		refreshButton.addActionListener( actionHandler );	

		/* CATEGORY PANEL */ 
		categoryPanel = new Panel(400, 100);
		categoryPanel.setLayout(new FlowLayout());
		categoryPanel.createBorder( "Category" ); 
		add(categoryPanel, BorderLayout.WEST);		
		
		// Instruction prompt
		JTextArea categorySelectPrompt = 
				new JTextArea("Select the product category");
		formatText( categorySelectPrompt, new Font("San Francisco Mono", Font.PLAIN, 13) , false, false );
		categoryPanel.add(categorySelectPrompt);
		
		// Set up JComboBox for categories
		jcboCategory = new JComboBox<String>( categoryList );
		jcboCategory.setPreferredSize( new Dimension( categoryPanel.getPreferredWidth()-5, 30) );
		jcboCategory.setSelectedIndex(0);
		categoryPanel.add(jcboCategory);
		
		// Register listener 
		jcboCategory.addActionListener( actionHandler );	
		
		/* OPERATION PANEL */
		operationPanel = new Panel(500, 100);
		operationPanel.setLayout( new GridLayout(operationList.length/2, operationList.length, 2, 2) );
		operationPanel.createBorder( "Operation" ); 

		// Create JButtons for operations
		buttonList = new JButton[operationList.length]; 
		for ( int i = 0; i < operationList.length; i++ )	{
			// create new buttons for the action
			buttonList[i] = new JButton(operationList[i]); 
			operationPanel.add(buttonList[i]);
			
			OperationButtonHandler buttonHandler = new OperationButtonHandler();
			buttonList[i].addActionListener(buttonHandler);
		} // end for loop
		
		add(operationPanel, BorderLayout.EAST);		 
				
		this.pack();
		this.setVisible(true);
		} // end try
	    catch (ClassNotFoundException classNotFound) {
	    	  JOptionPane.showMessageDialog( null, "Unable to find Connector/J Driver class"); 
	     }

		catch ( SQLException sqlException ) {
	         JOptionPane.showMessageDialog( null, sqlException.getMessage(), 
	            "Database error", JOptionPane.ERROR_MESSAGE );              
	         // ensure database connection is closed
	         model.disconnectFromDatabase();        
	         System.exit( 1 ); // terminate application
	      } // end catch
	      
	      setDefaultCloseOperation( DISPOSE_ON_CLOSE );
	      
	      // ensure database connection is closed when user quits application
	      addWindowListener( new WindowAdapter()  {
	    	  // disconnect from database and exit when window has closed
	    	  public void windowClosed( WindowEvent event ) {
	    		  //model.disconnectFromDatabase();
	              System.exit( 0 );
	            } // end method windowClosed
	         } // end WindowAdapter inner class
	      ); // end call to addWindowListener
		
	} // END CONSTRUCTOR
	
	
	// Inner class to set up panels with customize widths and heights 
	class Panel extends JPanel {
		private int prefW;
		private int prefH;
		
		public Panel(int prefW, int prefH) {
			this.prefW = prefW;
			this.prefH = prefH;
		} // end constructor			
		
		public int getPreferredWidth() {
			return prefW;
		}
		public void createBorder(String title) {
			this.setBorder(BorderFactory.createTitledBorder(title));
		} // create border and title

		@Override
		public Dimension getPreferredSize() {
			return new Dimension(prefW, prefH);
		}
	} // end inner class
		
	/* Action event handling for selecting category */
	class CategoryActionHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				int i = jcboCategory.getSelectedIndex();
				if (i >= 1) {
					System.out.println("Setting Category");
					model.setCategory(i);
					updateTable();
					System.out.println("updateTable was called");
				}
			} catch (Exception exception) {}
		} // end actionPerform	
	} // end class CategoryActionHandler
	
		
	/** Action event handling for selecting operation */
	class OperationButtonHandler implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try { 
				int i = jcboCategory.getSelectedIndex();
				if (i >= 1) {
					model.setCategory(i);
					updateTable();	
					String prodId = JOptionPane.showInputDialog("Enter product ID (ISBN or UPC):");
					model.setProdId(prodId);
					Object action = e.getSource();
	        		
	        		// CREATE ITEM
		        	if (action == buttonList[0]) {
		        		if (model.searchItem(prodId, categoryTable)) {
			        		model.setOperation(2);
		        			JOptionPane.showMessageDialog(null, "The product already exists in the database\n" +
		        					prodInfoPrompt,"Error", JOptionPane.ERROR_MESSAGE);
		        		} else {
		        		collectNewEntries();
		        		model.setOperation(1);
	        			JOptionPane.showMessageDialog(null, "The product was added to the inventory database\n");
		        		} // end else
		        	} // end create item handling
		        	
		        	// RETRIEVE ITEM 
		        	else if  (action == buttonList[1]) {
		        		if (!model.searchItem(prodId, categoryTable)) {
		        			errorMessage();
		        		} else 	{
		        		model.setOperation(2);
	        			JOptionPane.showMessageDialog(null, prodInfoPrompt);
		        		}
		        	} // retrieve item handling
		        	
		        	// UPDATE ITEM 
		        	else if	(action == buttonList[2]) {
		        		if (!model.searchItem(prodId, categoryTable)) {
		        			errorMessage();
		        		} else {	
		        		collectUpdatedEntry();
		        		model.setOperation(3);
		        		JOptionPane.showMessageDialog(null, prodInfoPrompt);
		        		} // end else 
		        	} // end if action = update item
	
		        	// DELETE ITEM 
		        	else if (action == buttonList[3]) {
		        		if (!model.searchItem(prodId, categoryTable)) {
		        			errorMessage();
		        		} else  {
		        			int form = JOptionPane.showConfirmDialog(null,prodInfoPrompt + 
		        					"\nDo you want to delete the item?", null,JOptionPane.YES_NO_OPTION);
		        			if (form == JOptionPane.YES_OPTION) {
		        				JOptionPane.showMessageDialog(null,"The selected product was removed from inventory database\n");
		        				model.setOperation(4);
		        			} // end if yes
		        		} // end else
		        	} // end if action == delete item
				} else {
    			JOptionPane.showMessageDialog(null, "Please select a product category","Error", JOptionPane.ERROR_MESSAGE);
				} // end else	
			} catch (Exception exception) {}	
			
	      } // end actionPerformed   
	} // end class operationButtonHandler
	
	/** DATA TABLE CREATION */
	private void updateTable() { 
		JTable dataTable = new JTable(model);
		dataTable.getTableHeader().setFont(new Font("San Francisco Mono", Font.PLAIN, 12));
		dataTable.setFont(new Font("San Francisco Mono", Font.PLAIN, 13));
        dataTable.setFillsViewportHeight(true);

		scrollPane.getViewport().add(dataTable);
		scrollPane.setVisible(true);
		revalidate();
		//return dataTable;
	} // end method updateTable
	
	
	/** CREATE ITEM FORM */
	private void collectNewEntries() {
		createForm = new Panel(640, 360);
		createForm.setLayout(new GridLayout(0,1));
		
		JLabel[] fieldLabel = new JLabel[prodFields.length];
		JTextField[] textField = new JTextField[prodFields.length];

		
		for ( int i=0; i< prodFields.length; i++ ) {
			fieldLabel[i] = new JLabel(prodFields[i]);
			textField[i] = new JTextField();
			createForm.add(fieldLabel[i]);
			createForm.add(textField[i]);	
		} // end for loop
		
		int form = JOptionPane.showConfirmDialog(null, createForm,
				"Create Item Form",JOptionPane.OK_CANCEL_OPTION, 
				JOptionPane.PLAIN_MESSAGE);
		String[] newEntriesList = new String[prodFields.length];
		
		if (form == JOptionPane.OK_OPTION) {
			for ( int i=0; i< prodFields.length; i++ ) {
			newEntriesList[i] = textField[i].getText();
			} // end for loop
			
		 newEntries = String.join(",", newEntriesList); 
		 model.setNewEntries(newEntries);
		 updateTable();
		}	// end if
	
	} // end method collectNewEntries
	
	/** UPDATE ITEM FORM */
	private void collectUpdatedEntry() {
		updateForm = new Panel(640, 100);
		updateForm.setLayout(new GridLayout(0,1));
		
		JComboBox<String> jcboField = new JComboBox<String>( prodFields );
		jcboField.setPreferredSize( new Dimension( updateForm.getPreferredWidth()-5, 30) );		
		updateForm.add(jcboField);
		
		JLabel fieldLabel = new JLabel("Updated Entry");
		JTextField textField = new JTextField("");
		updateForm.add(fieldLabel);
		updateForm.add(textField);
		
		int form = JOptionPane.showConfirmDialog(null, updateForm,
				"Update Item Form",JOptionPane.OK_CANCEL_OPTION, 
				JOptionPane.PLAIN_MESSAGE);
		
		if (form == JOptionPane.OK_OPTION) {
			fieldNum = (Integer)jcboField.getSelectedIndex();
			model.setFieldNum(fieldNum); 
			updatedEntry = textField.getText();
			model.setUpdatedEntry(updatedEntry);
			updateTable();
		}	// end if
	} // end method collectUpdatedEntry
	
	
	public void setModel( InventoryModel2 model ) {
		this.model = model;
	} // end setModel
	
	/** Notify changes in product Category to Model */
	public void notifyCategory(int num) throws IllegalStateException, SQLException {
		categoryTable = model.getCategory();
		System.out.println("CATEGORY TABLE WAS SET");
		// TODO: dataTableModel = model.createDataTableModel(categoryTable);
		System.out.println("GETTING COLUMNS NAMES");
		prodFields = model.getColumnNames(categoryTable);

	} // end notifyCategory

	/** Notify changes in operation to Model */
	 public void notifyOperation(int num) throws IllegalStateException, SQLException {
		model.implementOperation();	
		//prodInfoPrompt = model.getProductInfo();
	 } // end notifyCategory

	/* Helper methods */
	private void formatText (JTextComponent text, Font font, boolean isEditable, boolean isOpaque) {
		text.setFont(font);
		text.setEditable(isEditable);
		text.setOpaque(isOpaque);
	} // end formatText
	
	private void errorMessage() {
		JOptionPane.showMessageDialog(null,"The product does not exist in the database", 
				"Error", JOptionPane.ERROR_MESSAGE);
	} // end errorMessage method
		
	public void start() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
	}

} // end InventoryView class
