package sawfowl.guishopmanager.configure.config;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.localeapi.api.LocalisedComment;

@ConfigSerializable
public class MySQL {

	@Setting("Enable")
	private boolean enable = false;
	@Setting("Host")
	private String host = "localhost";
	@Setting("Port")
	private String port = "3306";
	@Setting("DataBase")
	private String dataBase = "guishopmanager";
	@Setting("Prefix")
	private String prefix = "srv_node1_";
	@Setting("User")
	private String user = "user";
	@Setting("Password")
	private String password = "UNSET";
	@Setting("SSL")
	private String ssl = "false";
	@Setting("SyncInterval")
	@LocalisedComment(path = { "Comments", "MainConfig", "MySql", "SyncInterval" }, plugin = "guishopmanager")
	private int syncInterval = 5;
	public MySQL() {}

	public boolean isEnable() {
		return enable;
	}

	public String getHost() {
		return host;
	}

	public String getPort() {
		return port;
	}

	public String getDataBase() {
		return dataBase;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}

	public String getSsl() {
		return ssl;
	}

	public int getSyncInterval() {
		if(syncInterval <= 0) syncInterval = 5;
		return syncInterval;
	}

}
