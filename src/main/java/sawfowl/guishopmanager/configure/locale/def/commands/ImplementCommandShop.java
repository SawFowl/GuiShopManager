package sawfowl.guishopmanager.configure.locale.def.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.CommandShop;
import sawfowl.localeapi.api.TextUtils;

@ConfigSerializable
public class ImplementCommandShop implements CommandShop {

	@Setting("Title")
	private Component title = TextUtils.deserializeLegacy("&3Shops");
	@Setting("Padding")
	private Component padding = TextUtils.deserializeLegacy("&3=");
	@Setting("CommandAdded")
	private Component commandAdded = TextUtils.deserializeLegacy("&aYou have added a command to an item.");
	@Setting("Empty")
	private Component empty = TextUtils.deserializeLegacy("&eThe shop list is now empty. Contact the administration.");
	@Setting("EmptyEditor")
	private Component emptyEditor = TextUtils.deserializeLegacy("&eThe shop list is empty. Create at least 1 shop. ");
	@Setting("Delete")
	private Component delete = TextUtils.deserializeLegacy("&aThe shop was deleted.");
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

}
