package sawfowl.guishopmanager.configure.locale.def;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.abstractlocale.Items;
import sawfowl.guishopmanager.configure.locale.def.items.*;

@ConfigSerializable
public class ImplementItems implements Items {

	@Setting("Name")
	private ImplementName names = new ImplementName();
	@Setting("Lore")
	private ImplementLore lore = new ImplementLore();
	public ImplementItems() {}

	@Override
	public Name name() {
		return names;
	}

	@Override
	public Lore lore() {
		return lore;
	}

}
