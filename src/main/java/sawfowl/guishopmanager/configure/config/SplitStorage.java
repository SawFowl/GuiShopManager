package sawfowl.guishopmanager.configure.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.commandpack.utils.StorageType;

@ConfigSerializable
public class SplitStorage {

	@Setting("Enable")
	private boolean enable = false;
	@Setting("Auction")
	private String auction = StorageType.FILE.typeName();
	@Setting("Shops")
	private String shops = StorageType.FILE.typeName();
	@Setting("CommandsShops")
	private String commandsShops = StorageType.FILE.typeName();
	public SplitStorage() {}

	public boolean isEnable() {
		return enable;
	}

	public String getAuction() {
		return auction;
	}

	public String getShops() {
		return shops;
	}

	public String getCommandsShops() {
		return commandsShops;
	}

}
