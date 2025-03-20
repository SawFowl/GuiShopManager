package sawfowl.guishopmanager.configure.locale.ru.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Exceptions;
import sawfowl.localeapi.api.TextUtils;

@ConfigSerializable
public class ImplementExceptions implements Exceptions {

	@Setting("PlayerIsNotPresent")
	private Component playerIsNotPresent = TextUtils.deserializeLegacy("&cНужно указать ник игрока");
	@Setting("ItemNotPresent")
	private Component itemNotPresent = TextUtils.deserializeLegacy("&cПожалуйста предъявите предмет ┬──┬ ノ(゜-゜ノ)");
	@Setting("ZeroOrNullPrices")
	private Component zeroOrNullPrices = TextUtils.deserializeLegacy("&cВы не можете продать предмет без цены/ставки или с нулевой ценой/ставкой. Одно из значений должно быть больше нуля.");
	@Setting("CurrencyNotPresent")
	private Component currencyNotPresent = TextUtils.deserializeLegacy("&cВы должны указать валюту.");
	@Setting("DontOpenOther")
	private Component dontOpenOther = TextUtils.deserializeLegacy("&cВы не можете открыть меню для другого игрока.");
	@Setting("ShopNotPresent")
	private Component shopNotPresent = TextUtils.deserializeLegacy("&cИдентификатор магазина не указан.");
	@Setting("ShopAlreadyExists")
	private Component shopAlreadyExists = TextUtils.deserializeLegacy("&cМагазин с таким идентификатором уже существует.");
	@Setting("MenuNotPresent")
	private Component menuNotPresent = TextUtils.deserializeLegacy("&cНе указан номер меню.");
	@Setting("InvalidMenuId")
	private Component invalidMenuId = TextUtils.deserializeLegacy("&cМеню с таким номером не существует, создайте его через GUI.");
	@Setting("SlotNotPresent")
	private Component slotNotPresent = TextUtils.deserializeLegacy("&cСлот для размещения предмета не указан.");
	@Setting("InvalidSlot")
	private Component invalidSlot = TextUtils.deserializeLegacy("&cНомер слота должен находиться в диапазоне от 0 до 44.");
	@Setting("BuyPriceNotPresent")
	private Component buyPriceNotPresent = TextUtils.deserializeLegacy("&cУкажите цену покупки.");
	@Setting("SellPriceNotPresent")
	private Component sellPriceNotPresent = TextUtils.deserializeLegacy("&cУкажите цену продажи.");
	@Setting("EmptyTranslateName")
	private Component emptyTranslateName = TextUtils.deserializeLegacy("&cИмя локализации не указано.");
	@Setting("LocaleNotExist")
	private Component localeNotExist = TextUtils.deserializeLegacy("&cТакой локализации не существует. Формат - en-US, ru-RU и т. д.");
	@Setting("LocaleNotPresent")
	private Component localeNotPresent = TextUtils.deserializeLegacy("&cУкажите необходимую локализацию. Формат - en-US, ru-RU и т. д.");
	@Setting("TranslateNotPresent")
	private Component translateNotPresent = TextUtils.deserializeLegacy("&cВведите отображаемое имя для вашего магазина.");
	@Setting("H2NotPresent")
	private Component h2NotPresent = TextUtils.deserializeLegacy("&cОтсутствует плагин с драйвером h2. Пожалуйста загрузите его с сайта SpongeORE.");
	@Setting("BackupNotPresent")
	private Component backupPresent = TextUtils.deserializeLegacy("&cФайл бэкапа не найден. Нечего загружать.");
	public ImplementExceptions() {}

	@Override
	public Component playerIsNotPresent() {
		return playerIsNotPresent;
	}

	@Override
	public Component itemNotPresent() {
		return itemNotPresent;
	}

	@Override
	public Component zeroOrNullPrices() {
		return zeroOrNullPrices;
	}

	@Override
	public Component currencyNotPresent() {
		return currencyNotPresent;
	}

	@Override
	public Component dontOpenOther() {
		return dontOpenOther;
	}

	@Override
	public Component shopNotPresent() {
		return shopNotPresent;
	}

	@Override
	public Component shopAlreadyExists() {
		return shopAlreadyExists;
	}

	@Override
	public Component menuNotPresent() {
		return menuNotPresent;
	}

	@Override
	public Component invalidMenuId() {
		return invalidMenuId;
	}

	@Override
	public Component slotNotPresent() {
		return slotNotPresent;
	}

	@Override
	public Component invalidSlot() {
		return invalidSlot;
	}

	@Override
	public Component buyPriceNotPresent() {
		return buyPriceNotPresent;
	}

	@Override
	public Component sellPriceNotPresent() {
		return sellPriceNotPresent;
	}

	@Override
	public Component emptyTranslateName() {
		return null;
	}

	@Override
	public Component localeNotExist() {
		return emptyTranslateName;
	}

	@Override
	public Component localeNotPresent() {
		return localeNotPresent;
	}

	@Override
	public Component translateNotPresent() {
		return translateNotPresent;
	}

	@Override
	public Component h2NotPresent() {
		return h2NotPresent;
	}

	@Override
	public Component backupNotPresent() {
		return backupPresent;
	}

}
