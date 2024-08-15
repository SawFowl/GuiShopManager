package sawfowl.guishopmanager.configure.locale.def.comments;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.abstractlocale.comments.BlackList;

@ConfigSerializable
public class ImplementBlackList implements BlackList {

	@Setting("Masks")
	private String masks = "List of masks. The id of mods or id of items without specifying the mod are applied, as well as the id of the type minecraft:item.";
	@Setting("Items")
	private String items = "List of items.";
	public ImplementBlackList() {}

	@Override
	public String masks() {
		return masks;
	}

	@Override
	public String items() {
		return items;
	}

}
