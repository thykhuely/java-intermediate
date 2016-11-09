/*
 * InventorySystemMain connects the model, view and controller
 * 
 */

public class InventorySystemMain {

	public static void main(String[] args) {
		
		InventoryModel model = new InventoryModel();
		InventoryController controller = new InventoryController(model);
		InventoryView view = new InventoryView(controller);
		
		model.setView(view);
		view.setModel(model);
		
		view.start();
		
	} // end main

} // end InventorySystemMain
