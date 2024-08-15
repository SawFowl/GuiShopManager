package sawfowl.guishopmanager.configure.locale.ru.messages;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.abstractlocale.Messages.Exceptions;
import sawfowl.localeapi.api.TextUtils;

@ConfigSerializable
public class ImplementExceptions implements Exceptions {

	@Setting("NoMoney")
	private Component noMoney = TextUtils.deserializeLegacy("&cУ вас недостаточно денег.");
	@Setting("NoMoneyForFee")
	private Component noMoneyForFee = TextUtils.deserializeLegacy("&cУ вас недостаточно денег, чтобы оплатить пошлину за выставление предмета на продажу на аукционе.");
	@Setting("NoItems")
	private Component noItems = TextUtils.deserializeLegacy("&cУ вас не хватает предметов.");
	@Setting("EconomyNotFound")
	private String economyNotFound = "На сервере отсутствует плагин экономики. Функции плагина будут недоступны.";
	public ImplementExceptions() {}

	@Override
	public Component noMoney() {
		return noMoney;
	}

	@Override
	public Component noMoneyForFee() {
		return noMoneyForFee;
	}

	@Override
	public Component noItems() {
		return noItems;
	}

	@Override
	public String economyNotFound() {
		return economyNotFound;
	}

}
