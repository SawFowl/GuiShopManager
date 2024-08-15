package sawfowl.guishopmanager.configure.locale.def;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Comment;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.PlaceholderKeys;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Debug;
import sawfowl.localeapi.api.TextUtils;

@ConfigSerializable
public class ImplementDebug implements Debug {

	@Setting("ErrorTakeMoney")
	private String errorTakeMoney = "Failed to remove money from balance of player %player%.";
	@Setting("ErrorGiveMoney")
	private String errorGiveMoney = "Failed to add money to the balance of player %player%.";
	@Setting("InfoTakeMoney")
	@Comment("You can use the following placeholders to display the currency type:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Displays the currency symbol.\n" + PlaceholderKeys.CURRENCY_NAME + " - Displays the name of the currency.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Displays the currency name in the plural.")
	private String infoTakeMoney = "Item [%item%]%size% removed from inventory of player %player%&a. Added money %currency-symbol%%removed%. Balance %currency-symbol%%balance%.";
	@Setting("InfoGiveMoney")
	@Comment("You can use the following placeholders to display the currency type:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Displays the currency symbol.\n" + PlaceholderKeys.CURRENCY_NAME + " - Displays the name of the currency.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Displays the currency name in the plural.")
	private String infoGiveMoney = "Item [%item%]%size% added to inventory of player %player%. Removed money %currency-symbol%%removed%. Balance %currency-symbol%%balance%.";
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
	public String infoTakeMoney(ItemStack itemStack, String player, Currency currency, double removed, double balance) {
		return infoTakeMoney.replace(PlaceholderKeys.ITEM, ItemTypes.registry().valueKey(itemStack.type()).asString()).replace(PlaceholderKeys.SIZE, String.valueOf(itemStack.quantity())).replace(PlaceholderKeys.CURRENCY_SYMBOL, TextUtils.clearDecorations(currency.symbol())).replace(PlaceholderKeys.CURRENCY_NAME, TextUtils.clearDecorations(currency.displayName())).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, TextUtils.clearDecorations(currency.pluralDisplayName())).replace(PlaceholderKeys.AMOUNT, String.valueOf(itemStack.quantity())).replace(PlaceholderKeys.PLAYER, player).replace(PlaceholderKeys.REMOVED, String.valueOf(removed)).replace(PlaceholderKeys.BALANCE, String.valueOf(balance));
	}

	@Override
	public String infoGiveMoney(ItemStack itemStack, String player, Currency currency, double added, double balance) {
		return infoGiveMoney.replace(PlaceholderKeys.ITEM, ItemTypes.registry().valueKey(itemStack.type()).asString()).replace(PlaceholderKeys.SIZE, String.valueOf(itemStack.quantity())).replace(PlaceholderKeys.CURRENCY_SYMBOL, TextUtils.clearDecorations(currency.symbol())).replace(PlaceholderKeys.CURRENCY_NAME, TextUtils.clearDecorations(currency.displayName())).replace(PlaceholderKeys.CURRENCY_PLURAL_NAME, TextUtils.clearDecorations(currency.pluralDisplayName())).replace(PlaceholderKeys.AMOUNT, String.valueOf(itemStack.quantity())).replace(PlaceholderKeys.PLAYER, player).replace(PlaceholderKeys.ADDED, String.valueOf(added)).replace(PlaceholderKeys.BALANCE, String.valueOf(balance));
	}

}
