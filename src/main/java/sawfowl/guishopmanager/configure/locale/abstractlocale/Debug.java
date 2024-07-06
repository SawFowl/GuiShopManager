package sawfowl.guishopmanager.configure.locale.abstractlocale;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

public interface Debug {

	String errorTakeMoney(String player);

	String errorGiveMoney(String player);

	String infoTakeMoney(ItemStack itemStack, String player, Currency currency, double removed, double balance);

	String infoGiveMoney(ItemStack itemStack, String player, Currency currency, double added, double balance);

}
