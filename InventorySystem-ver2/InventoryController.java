/*
 * InventoryController passes user inputs from InventoryView to the model
 */

public class InventoryController {
	private InventoryModel model;
	
	public InventoryController(InventoryModel model) {
		this.model = model;
	}
	
	public void setInventoryFile(String filename) {
		model.setInventoryFile(filename);
	}
	
	public void setCategory(int num) {
		model.setCategory(num);
	}
	
	public void setOperation(int num) {
		model.setOperation(num);
	}
	
	public void setProdId(String inputId) {
		model.setProdId(inputId);
	}
	
	public void setNewEntries(String entries) {
		model.setNewEntries(entries);
	}
	
	public void setFieldNum(int num) {
		model.setFieldNum(num);
	}

	public void setUpdatedEntry(String entry) {
		model.setUpdatedEntry(entry);
	}


}
