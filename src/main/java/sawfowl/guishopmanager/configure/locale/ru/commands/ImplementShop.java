package sawfowl.guishopmanager.configure.locale.ru.commands;

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
	private Component delete = deserialize("&aМагазин удален.");
	@Setting("ListTitle")
	private Component listTitle = deserialize("&3Магазины");
	@Setting("ListPadding")
	private Component listPadding = deserialize("&3=");
	@Setting("translateAdded")
	private Component translateAdded = deserialize("&aВы успешно установили магазину отображаемое имя.");
	@Setting("ListEmpty")
	private Component listEmpty = deserialize("&eСписок магазинов сейчас пуст. Обратитесь к администрации.");
	@Setting("ListEmptyEditor")
	private Component listEmptyEditor = deserialize("&eСписок магазинов сейчас пуст. Создайте по крайней мере 1 магазин. ");
	@Setting("ItemAdded")
	private Component itemAdded = deserialize("&cПредмет &r%item%&a добавлен в магазин %shop%.");
	@Setting("Open")
	private Component open = deserialize("&eНажмите, чтобы открыть этот магазин.");
	@Setting("OpenForEdit")
	private Component openForEdit = deserialize("&eНажмите, чтобы открыть этот магазин в редакторе.");
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
