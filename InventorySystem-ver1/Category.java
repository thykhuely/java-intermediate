
/*
 * Enum Category contains the item's fields specific to their types,
 * so that View can process operations based on the related fields 
 */


public enum Category {
	BOOK ("book","Title,Author,Year,Quantity,Price"),
	CD ("cd","Title,Artist,Year,Quantity,Price"),
	DVD ("dvd","Title,Director,Year,Quantity,Price");
	
	private String category;
	private String prodFields; 
	
	// Constructor
	Category(String category, String fields) {
		this.category = category;
		this.prodFields = fields;
	}
	
	public String getCategory() { 
		return category;
	}
	
	public String[] getProdFields() {
		String[] prodInfoList = prodFields.split(",");
		return prodInfoList;
	}
	
} // end enum

