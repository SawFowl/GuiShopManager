package sawfowl.guishopmanager.configure.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class StorageFolders {

	@Setting("Shops")
	private String shops = "shops";
	@Setting("CommandsShops")
	private String commandsShops = "commands";
	public StorageFolders() {}

	public String getShops() {
		return shops;
	}

	public String getCommandsShops() {
		return commandsShops;
	}

}
