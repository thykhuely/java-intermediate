
/* 
 * InventoryRepository interface contains methods 
 * to create, retrieve, update, and delete items using their product IDs
 */

public interface InventoryRepository {
	void getCurrentList();
	boolean searchItem(String prodId);
	void createItem(String prodId, String prodDetails, String[] prodInfo);
	void updateItem(String prodId, Integer fieldNum, String updatedEntry, String[] prodInfo);
	void retrieveItem(String prodId, String[] prodInfo);
	void deleteItem(String prodId, String[] prodInfo);
	 
}
