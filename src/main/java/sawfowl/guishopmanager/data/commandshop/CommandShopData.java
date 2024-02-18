package sawfowl.guishopmanager.data.commandshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShop;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShopStack;

public class CommandShopData {

	public CommandShopData(Component defaultName){
		menus = new HashMap<Integer, CommandShopMenuData>();
		titles = new HashMap<String, Component>();
		this.defaultName = defaultName;
	}

	private Map<Integer, CommandShopMenuData> menus;
	private Map<String, Component> titles;
	private Component defaultName;

	public void addMenu(Integer id, CommandShopMenuData shopMenu) {
		menus.put(id, shopMenu);
	}

	public CommandShopMenuData getCommandShopMenuData(int id) {
		return menus.get(id);
	}

	public void addTitle(String locale, Component text) {
		if(titles.containsKey(locale)) {
			titles.remove(locale);
		}
		titles.put(locale, text);
	}

	public String getID() {
		return LegacyComponentSerializer.legacyAmpersand().serialize(defaultName);
	}

	public Component getOrDefaultTitle(Locale locale) {
		return titles.containsKey(locale.toLanguageTag()) ? titles.get(locale.toLanguageTag()) : defaultName;
	}

	public boolean hasNextExist(int id) {
		return menus.containsKey(id + 1);
	}

	public boolean hasPreviousExist(int id) {
		return menus.containsKey(id - 1);
	}

	public Map<Integer, CommandShopMenuData> getMenus() {
		return menus;
	}

	public Map<String, Component> getTitles() {
		return titles;
	}

	public Component getDefaultName() {
		return defaultName;
	}

	public SerializedCommandShop serialize() {
		SerializedCommandShop serializableShop = new SerializedCommandShop(defaultName);
		for(Entry<Integer, CommandShopMenuData> menuEntry : menus.entrySet()) {
			List<SerializedCommandShopStack> serializedShopStack = new ArrayList<SerializedCommandShopStack>();
			for(Entry<Integer, CommandItemData> itemsEntry : menuEntry.getValue().getItems().entrySet()) {
				serializedShopStack.add(new SerializedCommandShopStack(itemsEntry.getKey(), itemsEntry.getValue().getItemStack(), itemsEntry.getValue().getPrices()));
			}
			serializableShop.updateMenu(menuEntry.getKey(), serializedShopStack);
			for(Entry<String, Component> titlesEntry : titles.entrySet()) {
				serializableShop.updateLocalizedNames(titlesEntry.getKey(), titlesEntry.getValue());
			}
			serializableShop.updateMenu(menuEntry.getKey(), serializedShopStack);
		}
		return serializableShop;
	}

}
