package sawfowl.guishopmanager.configure.locale.abstractlocale;

import org.spongepowered.api.item.inventory.ItemStack;

public interface Debug {

	String errorTakeMoney(String player);

	String errorGiveMoney(String player);

	String infoTakeMoney(ItemStack itemStack, String player, double removed, double balance);

	String infoGiveMoney(ItemStack itemStack, String player, double added, double balance);

}
