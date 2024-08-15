package sawfowl.guishopmanager.data.commandshop;

import java.util.HashMap;
import java.util.Map;

public class CommandShopMenuData {

	public CommandShopMenuData() {
		items = new HashMap<Integer, CommandItemData>();
	}

	private Map<Integer, CommandItemData> items;

	public void addOrUpdateItem(int slotID, CommandItemData itemStacks) {
		removeItem(slotID);
		items.put(slotID, itemStacks);
	}

	public void removeItem(int slotID) {
		if(containsCommandItem(slotID)) items.remove(slotID);
	}

	public boolean containsCommandItem(int slotID) {
		return items.containsKey(slotID);
	}

	public CommandItemData getCommandItem(int slotID) {
		return items.get(slotID);
	}

	public Map<Integer, CommandItemData> getItems() {
		return items;
	}

	public boolean isEmpty() {
		return items.isEmpty();
	}

}
