package sawfowl.guishopmanager.configure.locale.def.gui;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.abstractlocale.Gui.CommandShop;
import sawfowl.localeapi.api.TextUtils;

@ConfigSerializable
public class ImplementCommandShop implements CommandShop {

	@Setting("Edit")
	private Component edit = TextUtils.deserializeLegacy("&2Setting of purchase commands");
	public ImplementCommandShop() {}

	@Override
	public Component edit() {
		return edit;
	}

}
