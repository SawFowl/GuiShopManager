package sawfowl.guishopmanager.configure.locale.def.commands;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Exceptions;
import sawfowl.localeapi.api.TextUtils;

@ConfigSerializable
public class ImplementExceptions implements Exceptions {

	@Setting("PlayerIsNotPresent")
	private Component playerIsNotPresent = TextUtils.deserializeLegacy("&cYou need to be a player or specify the player's nickname.");
	@Setting("ItemNotPresent")
	private Component itemNotPresent = TextUtils.deserializeLegacy("&cPlease present the item ┬──┬ ノ(゜-゜ノ)");
	@Setting("ZeroOrNullPrices")
	private Component zeroOrNullPrices = TextUtils.deserializeLegacy("&cYou cannot sell an item without a price/bet or with a zero price/bet. One of the values must be greater than zero.");
	@Setting("CurrencyNotPresent")
	private Component currencyNotPresent = TextUtils.deserializeLegacy("&cYou must specify the currency.");
	@Setting("DontOpenOther")
	private Component dontOpenOther = TextUtils.deserializeLegacy("&cYou can not open the menu to another player.");
	@Setting("ShopNotPresent")
	private Component shopNotPresent = TextUtils.deserializeLegacy("&cShop id not specified.");
	@Setting("ShopAlreadyExists")
	private Component shopAlreadyExists = TextUtils.deserializeLegacy("&cA shop with this id already exists.");
	@Setting("MenuNotPresent")
	private Component menuNotPresent = TextUtils.deserializeLegacy("&cMenu number not specified.");
	@Setting("InvalidMenuId")
	private Component invalidMenuId = TextUtils.deserializeLegacy("&cMenu with this number does not exist, create it via GUI.");
	@Setting("SlotNotPresent")
	private Component slotNotPresent = TextUtils.deserializeLegacy("&cThe slot for placing the item is not specified.");
	@Setting("InvalidSlot")
	private Component invalidSlot = TextUtils.deserializeLegacy("&cThe slot number must be between 0 and 44.");
	@Setting("BuyPriceNotPresent")
	private Component buyPriceNotPresent = TextUtils.deserializeLegacy("&cSpecify the purchase price.");
	@Setting("SellPriceNotPresent")
	private Component sellPriceNotPresent = TextUtils.deserializeLegacy("&cSpecify the sale price.");
	@Setting("EmptyTranslateName")
	private Component emptyTranslateName = TextUtils.deserializeLegacy("&cLocalization name not specified.");
	@Setting("LocaleNotExist")
	private Component localeNotExist = TextUtils.deserializeLegacy("&cThere is no such localization. The format is en-US, ru-RU, etc.");
	@Setting("LocaleNotPresent")
	private Component localeNotPresent = TextUtils.deserializeLegacy("&cSpecify the required localization. The format is en-US, ru-RU, etc.");
	@Setting("TranslateNotPresent")
	private Component translateNotPresent = TextUtils.deserializeLegacy("&cEnter a display name for your shop.");
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

}
