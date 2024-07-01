package sawfowl.guishopmanager.configure.locale.def;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.PlaceholderKeys;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Debug;

@ConfigSerializable
public class ImplementDebug implements Debug {

	@Setting("ErrorTakeMoney")
	private String errorTakeMoney = "Failed to remove money from balance of player %player%.";
	@Setting("ErrorGiveMoney")
	private String errorGiveMoney = "Failed to add money to the balance of player %player%.";
	@Setting("InfoTakeMoney")
	private String infoTakeMoney = "Item [%item%]%amount% removed from inventory of player %player%&a. Added money %removed%. Balance %balance%.";
	@Setting("InfoGiveMoney")
	private String infoGiveMoney = "Item [%item%]%amount% added to inventory of player %player%. Removed money %removed%. Balance %balance%.";
	public ImplementDebug() {}

	@Override
	public String errorTakeMoney(String player) {
		return errorTakeMoney.replace(PlaceholderKeys.PLAYER, player);
	}

	@Override
	public String errorGiveMoney(String player) {
		return errorGiveMoney.replace(PlaceholderKeys.PLAYER, player);
	}

	@Override
	public String infoTakeMoney(ItemStack itemStack, String player, double removed, double balance) {
		return infoTakeMoney.replace(PlaceholderKeys.ITEM, ItemTypes.registry().valueKey(itemStack.type()).asString()).replace(PlaceholderKeys.AMOUNT, String.valueOf(itemStack.quantity())).replace(PlaceholderKeys.PLAYER, player).replace(PlaceholderKeys.REMOVED, String.valueOf(removed)).replace(PlaceholderKeys.BALANCE, String.valueOf(balance));
	}

	@Override
	public String infoGiveMoney(ItemStack itemStack, String player, double added, double balance) {
		return infoGiveMoney.replace(PlaceholderKeys.ITEM, ItemTypes.registry().valueKey(itemStack.type()).asString()).replace(PlaceholderKeys.AMOUNT, String.valueOf(itemStack.quantity())).replace(PlaceholderKeys.PLAYER, player).replace(PlaceholderKeys.ADDED, String.valueOf(added)).replace(PlaceholderKeys.BALANCE, String.valueOf(balance));
	}

}
