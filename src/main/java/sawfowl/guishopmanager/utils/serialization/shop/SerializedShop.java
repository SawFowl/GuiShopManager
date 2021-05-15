package sawfowl.guishopmanager.utils.serialization.shop;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.text.Text;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import sawfowl.guishopmanager.utils.data.shop.Shop;
import sawfowl.guishopmanager.utils.data.shop.ShopItem;
import sawfowl.guishopmanager.utils.data.shop.ShopMenuData;

@ConfigSerializable
public class SerializedShop implements Serializable {

	SerializedShop(){}
	public SerializedShop(Text defaultName) {
		if(!localizedNames.containsKey("DEFAULT")) {
			localizedNames.put("DEFAULT", defaultName);
		}
	}

	private static final long serialVersionUID = 01;

	@Setting("ShopMenuList")
	private Map<String, List<SerializedShopStack>> shopMenus = new HashMap<String, List<SerializedShopStack>>();
	@Setting("LocalizedNames")
	private Map<String, Text> localizedNames = new HashMap<String, Text>();

	public List<SerializedShopStack> getShopStacks(int menu) {
		return shopMenus.get(String.valueOf(menu));
	}

	public void addShopStack(int menu, SerializedShopStack serializableShopStack) {
		shopMenus.get(String.valueOf(menu)).add(serializableShopStack);
	}

	public Text getLocalizedName(String localeKey) {
		return localizedNames.containsKey(localeKey) ? localizedNames.get(localeKey) : localizedNames.get("DEFAULT");
	}

	public Text getLocalizedName(Locale locale) {
		return localizedNames.containsKey(locale.toLanguageTag()) ? localizedNames.get(locale.toLanguageTag()) : localizedNames.get("DEFAULT");
	}

	public void setLocalizedName(String localeKey, Text localeName) {
		localizedNames.put(localeKey, localeName);
	}

	public void setLocalizedName(Locale locale, Text localeName) {
		localizedNames.put(locale.toLanguageTag(), localeName);
	}

	public void updateMenu(int menu, List<SerializedShopStack> serializableShopStacks) {
		if(shopMenus.containsKey(String.valueOf(menu))) {
			shopMenus.replace(String.valueOf(menu), serializableShopStacks);
		} else {
			shopMenus.put(String.valueOf(menu), serializableShopStacks);
		}
	}

	public Map<Integer, List<SerializedShopStack>> getShopMenus() {
		Map<Integer, List<SerializedShopStack>> menus = new HashMap<Integer, List<SerializedShopStack>>();
		for(Entry<String, List<SerializedShopStack>> entry : shopMenus.entrySet()) {
			menus.put(Integer.parseInt(entry.getKey()), entry.getValue());
		}
		return menus;
	}

	public Map<String, Text> getLocalizedNames() {
		return localizedNames;
	}

	public void updateLocalizedNames(String locale, Text text) {
		if(localizedNames.containsKey(locale)) {
			localizedNames.replace(locale, text);
		} else {
			localizedNames.put(locale, text);
		}
	}
	public Shop deserialize() {
		Shop shop = new Shop(localizedNames.get("DEFAULT"));
		for(Entry<Integer, List<SerializedShopStack>> menuEntry : getShopMenus().entrySet()) {
			ShopMenuData shopMenu = new ShopMenuData();
			for(SerializedShopStack serializedShopStack : menuEntry.getValue()) {
				if(serializedShopStack.getSerializedItemStack().isPresent()) {
					shopMenu.addOrUpdateItem(serializedShopStack.getSlot(), new ShopItem(serializedShopStack.getSerializedItemStack().getItemStack(), serializedShopStack.getSerializedShopPrices()));
				}
			}
			shop.addMenu(menuEntry.getKey(), shopMenu);
		}
		for(Entry<String, Text> localeEntry : localizedNames.entrySet()) {
			shop.addTitle(localeEntry.getKey(), localeEntry.getValue());
		}
		return shop;
	}

	@Override
	public String toString() {
		return localizedNames.toString() + " " + shopMenus.toString();
	}

}
