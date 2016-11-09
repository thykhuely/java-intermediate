/*
 * InventorySystemMain connects the model, view and controller
 * 
 */

public class InventorySystemMain {

	public static void main(String[] args) {
		
		InventoryModel2 model = new InventoryModel2();
		InventoryView view = new InventoryView(model);
		
		model.setView(view);
		view.setModel(model);
		
		view.start();
		
	} // end main

} // end InventorySystemMain
