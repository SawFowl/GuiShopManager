package sawfowl.guishopmanager.data.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import sawfowl.guishopmanager.serialization.shop.SerializedShop;
import sawfowl.guishopmanager.serialization.shop.SerializedShopStack;

public class Shop {

	public Shop(Component defaultName){
		menus = new HashMap<Integer, ShopMenuData>();
		titles = new HashMap<String, Component>();
		this.defaultName = defaultName;
	}

	private Map<Integer, ShopMenuData> menus;
	private Map<String, Component> titles;
	private Component defaultName;

	public void addMenu(Integer id, ShopMenuData shopMenu) {
		menus.put(id, shopMenu);
	}

	public ShopMenuData getShopMenuData(int id) {
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

	public Map<Integer, ShopMenuData> getMenus() {
		return menus;
	}

	public Map<String, Component> getTitles() {
		return titles;
	}

	public Component getDefaultName() {
		return defaultName;
	}

	public SerializedShop serialize() {
		SerializedShop serializableShop = new SerializedShop(defaultName);
		for(Entry<Integer, ShopMenuData> menuEntry : menus.entrySet()) {
			List<SerializedShopStack> serializedShopStack = new ArrayList<SerializedShopStack>();
			for(Entry<Integer, ShopItem> itemsEntry : menuEntry.getValue().getItems().entrySet()) {
				serializedShopStack.add(new SerializedShopStack(itemsEntry.getKey(), itemsEntry.getValue().getItemStack(), itemsEntry.getValue().getPrices()));
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
