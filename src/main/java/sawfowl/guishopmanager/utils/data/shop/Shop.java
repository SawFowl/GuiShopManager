package sawfowl.guishopmanager.utils.data.shop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.text.Text;

import sawfowl.guishopmanager.utils.serialization.SerializedItemStack;
import sawfowl.guishopmanager.utils.serialization.shop.SerializedShop;
import sawfowl.guishopmanager.utils.serialization.shop.SerializedShopStack;

public class Shop {

	public Shop(Text defaultName){
		menus = new HashMap<Integer, ShopMenuData>();
		titles = new HashMap<String, Text>();
		this.defaultName = defaultName;
	}

	private Map<Integer, ShopMenuData> menus;
	private Map<String, Text> titles;
	private Text defaultName;

	public void addMenu(Integer id, ShopMenuData shopMenu) {
		menus.put(id, shopMenu);
	}

	public ShopMenuData getShopMenuData(int id) {
		return menus.get(id);
	}

	public void addTitle(String locale, Text text) {
		if(titles.containsKey(locale)) {
			titles.remove(locale);
		}
		titles.put(locale, text);
	}

	public Text getOrDefaultTitle(Locale locale) {
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

	public Map<String, Text> getTitles() {
		return titles;
	}

	public Text getDefaultName() {
		return defaultName;
	}

	public SerializedShop serialize() {
		SerializedShop serializableShop = new SerializedShop(defaultName);
		for(Entry<Integer, ShopMenuData> menuEntry : menus.entrySet()) {
			List<SerializedShopStack> serializedShopStack = new ArrayList<SerializedShopStack>();
			for(Entry<Integer, ShopItem> itemsEntry : menuEntry.getValue().getItems().entrySet()) {
				serializedShopStack.add(new SerializedShopStack(itemsEntry.getKey(), new SerializedItemStack(itemsEntry.getValue().getItemStack()), itemsEntry.getValue().getPrices()));
			}
			serializableShop.updateMenu(menuEntry.getKey(), serializedShopStack);
			for(Entry<String, Text> titlesEntry : titles.entrySet()) {
				serializableShop.updateLocalizedNames(titlesEntry.getKey(), titlesEntry.getValue());
			}
			serializableShop.updateMenu(menuEntry.getKey(), serializedShopStack);
		}
		return serializableShop;
	}

}
