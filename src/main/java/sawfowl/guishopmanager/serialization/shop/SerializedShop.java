package sawfowl.guishopmanager.serialization.shop;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.data.shop.ShopItem;
import sawfowl.guishopmanager.data.shop.ShopMenuData;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

@ConfigSerializable
public class SerializedShop implements Serializable {

	SerializedShop(){}
	public SerializedShop(Component defaultName) {
		if(!localizedNames.containsKey("DEFAULT")) {
			localizedNames.put("DEFAULT", serialize(defaultName));
		}
	}

	private static final long serialVersionUID = 01;

	@Setting("ShopMenuList")
	private Map<String, List<SerializedShopStack>> shopMenus = new HashMap<String, List<SerializedShopStack>>();
	@Setting("LocalizedNames")
	private Map<String, String> localizedNames = new HashMap<String, String>();

	public List<SerializedShopStack> getShopStacks(int menu) {
		return shopMenus.get(String.valueOf(menu));
	}

	public void addShopStack(int menu, SerializedShopStack serializableShopStack) {
		shopMenus.get(String.valueOf(menu)).add(serializableShopStack);
	}

	public Component getLocalizedName(String localeKey) {
		return localizedNames.containsKey(localeKey) ? deserialize(localizedNames.get(localeKey)) : deserialize(localizedNames.get("DEFAULT"));
	}

	public Component getLocalizedName(Locale locale) {
		return localizedNames.containsKey(locale.toLanguageTag()) ? deserialize(localizedNames.get(locale.toLanguageTag())) : deserialize(localizedNames.get("DEFAULT"));
	}

	public void setLocalizedName(Locale locale, Component localeName) {
		localizedNames.put(locale.toLanguageTag(), serialize(localeName));
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

	public void updateLocalizedNames(String locale, Component component) {
		if(localizedNames.containsKey(locale)) {
			localizedNames.replace(locale, serialize(component));
		} else {
			localizedNames.put(locale, serialize(component));
		}
	}
	public Shop deserialize() {
		Shop shop = new Shop(deserialize(localizedNames.get("DEFAULT")));
		for(Entry<Integer, List<SerializedShopStack>> menuEntry : getShopMenus().entrySet()) {
			ShopMenuData shopMenu = new ShopMenuData();
			for(SerializedShopStack serializedShopStack : menuEntry.getValue()) {
				if(serializedShopStack.getSerializedItemStack().getOptItemType().isPresent()) {
					shopMenu.addOrUpdateItem(serializedShopStack.getSlot(), new ShopItem(serializedShopStack.getSerializedItemStack().getItemStack(), serializedShopStack.getSerializedShopPrices()));
				}
			}
			shop.addMenu(menuEntry.getKey(), shopMenu);
		}
		for(Entry<String, String> localeEntry : localizedNames.entrySet()) {
			shop.addTitle(localeEntry.getKey(), deserialize(localeEntry.getValue()));
		}
		return shop;
	}

	private String serialize(Component component) {
		return LegacyComponentSerializer.legacyAmpersand().serialize(component);
	}

	private Component deserialize(String string) {
		return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
	}

	@Override
	public String toString() {
		return localizedNames.toString() + " " + shopMenus.toString();
	}

}
