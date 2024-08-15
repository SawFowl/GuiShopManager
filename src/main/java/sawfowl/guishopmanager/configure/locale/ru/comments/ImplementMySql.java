package sawfowl.guishopmanager.configure.locale.ru.comments;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.abstractlocale.comments.MainConfig.MySql;

@ConfigSerializable
public class ImplementMySql implements MySql {

	@Setting("Title")
	private String title = "Настройки подключения к серверу MySql/MariaDB.";
	@Setting("SyncInterval")
	private String syncInterval = "Частота опроса базы данных для загрузки новых или измененных данных. Время указывается в секундах.";
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
