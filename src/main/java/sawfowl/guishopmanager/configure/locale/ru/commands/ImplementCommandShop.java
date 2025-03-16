package sawfowl.guishopmanager.configure.locale.ru.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.CommandShop;
import sawfowl.localeapi.api.TextUtils;

@ConfigSerializable
public class ImplementCommandShop implements CommandShop {

	@Setting("Title")
	private Component title = TextUtils.deserializeLegacy("&3Магазины");
	@Setting("Padding")
	private Component padding = TextUtils.deserializeLegacy("&3=");
	@Setting("CommandAdded")
	private Component commandAdded = TextUtils.deserializeLegacy("&aВы добавили команду к предмету.");
	@Setting("CommandsRemoved")
	private Component commandsRemoved = TextUtils.deserializeLegacy("&aСписок команд на предмете очищен.");
	@Setting("Empty")
	private Component empty = TextUtils.deserializeLegacy("&eСписок магазинов сейчас пуст. Обратитесь к администрации.");
	@Setting("EmptyEditor")
	private Component emptyEditor = TextUtils.deserializeLegacy("&eСписок магазинов сейчас пуст. Создайте по крайней мере 1 магазин.");
	@Setting("Delete")
	private Component delete = TextUtils.deserializeLegacy("&aМагазин удален.");
	public ImplementCommandShop() {}

	@Override
	public Component title() {
		return title;
	}

	@Override
	public Component padding() {
		return padding;
	}

	@Override
	public Component commandAdded() {
		return commandAdded;
	}

	@Override
	public Component empty() {
		return empty;
	}

	@Override
	public Component emptyEditor() {
		return emptyEditor;
	}

	@Override
	public Component delete() {
		return delete;
	}

	@Override
	public Component commandsRemoved() {
		return commandsRemoved;
	}

}
