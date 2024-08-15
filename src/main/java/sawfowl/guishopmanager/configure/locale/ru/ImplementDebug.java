package sawfowl.guishopmanager.configure.locale.ru;

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
	private String errorTakeMoney = "Не удалось снять деньги с баланса игрока %player%.";
	@Setting("ErrorGiveMoney")
	private String errorGiveMoney = "Не удалось пополнить баланс игрока %player%.";
	@Setting("InfoTakeMoney")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n"  + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private String infoTakeMoney = "Итем [%item%]%amount% удален из инвентаря игрока %player%&a. Добавлено денег %currency-symbol%%removed%. Баланс %currency-symbol%%balance%.";
	@Setting("InfoGiveMoney")
	@Comment("Вы можете использовать следующие плейсхолдеры для отображения валюты:\n" + PlaceholderKeys.CURRENCY_SYMBOL + " - Отображение символа валюты.\n"  + PlaceholderKeys.CURRENCY_NAME + " - Отображение имени валюты.\n" + PlaceholderKeys.CURRENCY_PLURAL_NAME + " - Отображение имени валюты в множественном числе.")
	private String infoGiveMoney = "Итем [%item%]%amount% добавлен в инвентарь игрока %player%. Взято денег %currency-symbol%%removed%. Баланс %currency-symbol%%balance%.";
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
