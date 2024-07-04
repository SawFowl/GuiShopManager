package sawfowl.guishopmanager.configure.locale.def.commands;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

import sawfowl.guishopmanager.configure.locale.PlaceholderKeys;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Shop;
import sawfowl.localeapi.api.Text;

@ConfigSerializable
public class ImplementShop implements Shop {

	@Setting("Delete")
	private Component delete = deserialize("&aThe shop was deleted.");
	@Setting("ListTitle")
	private Component listTitle = deserialize("&3Shops");
	@Setting("ListPadding")
	private Component listPadding = deserialize("&3=");
	@Setting("translateAdded")
	private Component translateAdded = deserialize("&aYou have successfully set the display name for your shop.");
	@Setting("ListEmpty")
	private Component listEmpty = deserialize("&eThe shop list is now empty. Contact the administration.");
	@Setting("ListEmptyEditor")
	private Component listEmptyEditor = deserialize("&eThe shop list is empty. Create at least 1 shop.");
	@Setting("ItemAdded")
	private Component itemAdded = deserialize("&cItem &7[&r%item%&7]&a added to shop %shop%.");
	@Setting("Open")
	private Component open = deserialize("&eClick to open this shop.");
	@Setting("OpenForEdit")
	private Component openForEdit = deserialize("&eClick to open this shop in the editor.");
	public ImplementShop() {}

	@Override
	public Component delete() {
		return delete;
	}

	@Override
	public Component title() {
		return listTitle;
	}

	@Override
	public Component padding() {
		return listPadding;
	}

	@Override
	public Component translateAdded() {
		return translateAdded;
	}

	@Override
	public Component listEmpty() {
		return listEmpty;
	}

	@Override
	public Component listEmptyEditor() {
		return listEmptyEditor;
	}

	@Override
	public Component itemAdded(ItemStack itemStack, Component shop) {
		return Text.of(itemAdded).replace(PlaceholderKeys.ITEM, itemStack.asComponent().hoverEvent(HoverEvent.showItem(Key.key(ItemTypes.registry().valueKey(itemStack.type()).asString()), itemStack.quantity()))).replace(PlaceholderKeys.SHOP, shop).get();
	}

	@Override
	public Component open() {
		return open;
	}

	@Override
	public Component openForEdit() {
		return openForEdit;
	}

}
