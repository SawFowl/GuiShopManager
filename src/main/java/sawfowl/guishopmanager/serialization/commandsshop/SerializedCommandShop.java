package sawfowl.guishopmanager.serialization.commandsshop;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import sawfowl.guishopmanager.data.commandshop.CommandItemData;
import sawfowl.guishopmanager.data.commandshop.CommandShopData;
import sawfowl.guishopmanager.data.commandshop.CommandShopMenuData;

@ConfigSerializable
public class SerializedCommandShop {

	SerializedCommandShop(){}
	public SerializedCommandShop(Component defaultName) {
		if(!localizedNames.containsKey("DEFAULT")) {
			localizedNames.put("DEFAULT", serialize(defaultName));
		}
	}

	@Setting("ShopMenuList")
	private Map<String, List<SerializedCommandShopStack>> shopMenus = new HashMap<String, List<SerializedCommandShopStack>>();
	@Setting("LocalizedNames")
	private Map<String, String> localizedNames = new HashMap<String, String>();

	public List<SerializedCommandShopStack> getShopStacks(int menu) {
		return shopMenus.get(String.valueOf(menu));
	}

	public void addShopStack(int menu, SerializedCommandShopStack serializableShopStack) {
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

	public void updateMenu(int menu, List<SerializedCommandShopStack> serializableShopStacks) {
		if(shopMenus.containsKey(String.valueOf(menu))) {
			shopMenus.replace(String.valueOf(menu), serializableShopStacks);
		} else {
			shopMenus.put(String.valueOf(menu), serializableShopStacks);
		}
	}

	public Map<Integer, List<SerializedCommandShopStack>> getShopMenus() {
		Map<Integer, List<SerializedCommandShopStack>> menus = new HashMap<Integer, List<SerializedCommandShopStack>>();
		for(Entry<String, List<SerializedCommandShopStack>> entry : shopMenus.entrySet()) {
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
	public CommandShopData deserialize() {
		CommandShopData shop = new CommandShopData(deserialize(localizedNames.get("DEFAULT")));
		for(Entry<Integer, List<SerializedCommandShopStack>> menuEntry : getShopMenus().entrySet()) {
			CommandShopMenuData shopMenu = new CommandShopMenuData();
			for(SerializedCommandShopStack serializedShopStack : menuEntry.getValue()) {
				if(serializedShopStack.getSerializedItemStack().getOptItemType().isPresent()) {
					shopMenu.addOrUpdateItem(serializedShopStack.getSlot(), new CommandItemData(serializedShopStack.getSerializedItemStack(), serializedShopStack.getSerializedShopPrices()));
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
