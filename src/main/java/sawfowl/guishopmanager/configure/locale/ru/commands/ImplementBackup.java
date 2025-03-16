package sawfowl.guishopmanager.configure.locale.ru.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Backup;

@ConfigSerializable
public class ImplementBackup implements Backup {

	public ImplementBackup() {}

	@Setting("Save")
	private Component save = deserialize("&aСоздание бэкапа завершено.");
	@Setting("Load")
	private Component load = deserialize("&aДанные бэкапа загружены.");

	@Override
	public Component save() {
		return save;
	}

	@Override
	public Component load() {
		return load;
	}

}
