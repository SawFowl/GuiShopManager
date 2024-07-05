package sawfowl.guishopmanager.configure.locale.def.comments;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.abstractlocale.comments.MainConfig.MySql;

@ConfigSerializable
public class ImplementMySql implements MySql {

	@Setting("Title") private String title = "MySql/MariaDB server connection settings.";
	@Setting("SyncInterval") private String syncInterval = "The frequency of polling the database to load new or changed data. The time is specified in seconds.";
	public ImplementMySql() {}

	@Override
	public String title() {
		return title;
	}

	@Override
	public String syncInterval() {
		return syncInterval;
	}

}
