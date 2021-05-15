package sawfowl.guishopmanager.utils.data.shop;

import java.util.HashMap;
import java.util.Map;

public class ShopMenuData {

	public ShopMenuData() {
		items = new HashMap<Integer, ShopItem>();
	}

	private Map<Integer, ShopItem> items;

	public void addOrUpdateItem(int slotID, ShopItem itemStacks) {
		if(items.containsKey(slotID)) {
			removeItem(slotID);
		}
		items.put(slotID, itemStacks);
	}

	public void removeItem(int slotID) {
		if(containsShopItem(slotID)) {
			items.remove(slotID);
		}
	}

	public boolean containsShopItem(int slotID) {
		return items.containsKey(slotID);
	}

	public ShopItem getShopItem(int slotID) {
		return items.get(slotID);
	}

	public Map<Integer, ShopItem> getItems() {
		return items;
	}

}
