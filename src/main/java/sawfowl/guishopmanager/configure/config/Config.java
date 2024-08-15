package sawfowl.guishopmanager.configure.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.localeapi.api.LocalisedComment;

@ConfigSerializable
public class Config {

	@Setting("Aliases")
	@LocalisedComment(path = { "Comments", "MainConfig", "Aliases" }, plugin = "guishopmanager")
	private AliasesConfig aliases = new AliasesConfig();
	@Setting("Auction")
	private Auction auction = new Auction();
	@Setting("ConfigType")
	@LocalisedComment(path = { "Comments", "MainConfig", "ConfigType" }, plugin = "guishopmanager")
	private ConfigType configType = new ConfigType();
	@Setting("MySQL")
	@LocalisedComment(path = { "Comments", "MainConfig", "MySql", "Title" }, plugin = "guishopmanager")
	private MySQL mySQL = new MySQL();
	@Setting("SplitStorage")
	@LocalisedComment(path = { "Comments", "MainConfig", "SplitStorage" }, plugin = "guishopmanager")
	private SplitStorage splitStorage = new SplitStorage();
	@Setting("StorageFolders")
	@LocalisedComment(path = { "Comments", "MainConfig", "StorageFolders" }, plugin = "guishopmanager")
	private StorageFolders storageFolders = new StorageFolders();
	@Setting("DebugEconomy")
	@LocalisedComment(path = { "Comments", "MainConfig", "DebugEconomy" }, plugin = "guishopmanager")
	private boolean debugEconomy = false;
	@Setting("PlayerTransactionMessage")
	@LocalisedComment(path = { "Comments", "MainConfig", "PlayerTransactionMessage" }, plugin = "guishopmanager")
	private boolean playerTransactionMessage = true;
	public Config() {}

	public AliasesConfig getAliases() {
		return aliases;
	}

	public Auction getAuction() {
		return auction;
	}

	public ConfigType getConfigType() {
		return configType;
	}

	public MySQL getMySQL() {
		return mySQL;
	}

	public SplitStorage getSplitStorage() {
		return splitStorage;
	}

	public StorageFolders getStorageFolders() {
		return storageFolders;
	}

	public boolean isDebugEconomy() {
		return debugEconomy;
	}

	public boolean isPlayerTransactionMessage() {
		return playerTransactionMessage;
	}

}
