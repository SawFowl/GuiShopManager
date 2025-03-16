package sawfowl.guishopmanager.configure.locale.def.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Backup;

@ConfigSerializable
public class ImplementBackup implements Backup {

	public ImplementBackup() {}

	@Setting("Save")
	private Component save = deserialize("&aThe creation of the backup is complete.");
	@Setting("Load")
	private Component load = deserialize("&aThe backup data has been loaded.");

	@Override
	public Component save() {
		return save;
	}

	@Override
	public Component load() {
		return load;
	}

}
