package sawfowl.guishopmanager.configure.locale.ru.comments;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.abstractlocale.comments.BlackList;

@ConfigSerializable
public class ImplementBlackList implements BlackList {

	@Setting("Masks")
	private String masks = "Список масок. Применяются id модов или id предметов без указания мода, а также id типа minecraft:item.";
	@Setting("Items")
	private String items = "Список предметов.";
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
