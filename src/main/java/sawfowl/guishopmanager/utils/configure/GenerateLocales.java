package sawfowl.guishopmanager.utils.configure;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.TypeTokens;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import sawfowl.guishopmanager.GuiShopManager;

public class GenerateLocales {

	private GuiShopManager plugin;
	private Locale RU_RU = null;
	private boolean save = false;
	public GenerateLocales(GuiShopManager guiShopManager) {
		plugin = guiShopManager;
		createLocales();
		generateDefaultLocale();
		generateLocaleRu();
		if(save) {
			plugin.getLocaleAPI().saveYamlLocales("guishopmanager");
		}
	}

	private void createLocales() {
		for(Locale locale : plugin.getLocaleAPI().getLocalesList()) {
			if(locale.toLanguageTag().equals("ru-RU")) {
				RU_RU = locale;
				break;
			}
		}
		if(!plugin.getLocaleAPI().yamlLocaleExist("guishopmanager", RU_RU)) {
			plugin.getLocaleAPI().createYamlLocale("guishopmanager", RU_RU);
		}
		if(!plugin.getLocaleAPI().yamlLocaleExist("guishopmanager", plugin.getLocaleAPI().getDefaultLocale())) {
			plugin.getLocaleAPI().createYamlLocale("guishopmanager", plugin.getLocaleAPI().getDefaultLocale());
		}
		plugin.getLocaleAPI().saveYamlLocales("guishopmanager");
	}

	private void generateLocaleRu() {
		checkText(getRuNode("FillItems", "Back"), serializeText("&eНазад"));
		checkText(getRuNode("FillItems", "Next"), serializeText("&eДалее"));
		checkText(getRuNode("FillItems", "AddPage"), serializeText("&eДобавить страницу"));
		checkText(getRuNode("FillItems", "Buy"), serializeText("&eКупить и выйти"));
		checkText(getRuNode("FillItems", "Sell"), serializeText("&eПродать и выйти"));
		checkText(getRuNode("FillItems", "Exit"), serializeText("&eСохранить и выйти"));
		checkText(getRuNode("FillItems", "BuyAndBack"), serializeText("&eКупить и вернуться назад"));
		checkText(getRuNode("FillItems", "SellAndBack"), serializeText("&eПродать и вернуться назад"));
		checkText(getRuNode("FillItems", "Size"), serializeText("&eОбъем: &a%value%"));
		checkText(getRuNode("FillItems", "Price"), serializeText("&eЦена: &a%value%"));
		checkText(getRuNode("FillItems", "Clear"), serializeText("&eСброс"));
		checkText(getRuNode("FillItems", "ChangeCurrency"), serializeText("&eСменить валюту"));
		checkText(getRuNode("FillItems", "CurrentCurrency"), serializeText("&eТекущая валюта"));
		checkText(getRuNode("FillItems", "SwitchMode"), serializeText("&eПереключить режим"));
		checkText(getRuNode("FillItems", "AuctionAddItem"), serializeText("&eВыставить предмет на продажу"));
		checkText(getRuNode("FillItems", "ReturnAuctionItem"), serializeText("&eВернуть ваши предметы"));

		checkListText(getRuNode("Lore", "ChangePrice"), Arrays.asList(serializeText("&aЛевый клик увеличит цену"), serializeText("&aПравый клик уменьшит цену")));
		checkListText(getRuNode("Lore", "ChangeSize"), Arrays.asList(serializeText("&aЛевый клик увеличит объем"), serializeText("&aПравый клик уменьшит объем")));
		checkText(getRuNode("Lore", "TransactionVariants"), serializeText("&eВарианты транзакций: ▼"));
		checkText(getRuNode("Lore", "CurrentCurrency"), serializeText("&eТекущая валюта: &a%currency%"));
		checkText(getRuNode("Lore", "CurrentSize"), serializeText("&eВыбранный объем: &a%size%"));
		checkText(getRuNode("Lore", "CurrentSum"), serializeText("&eИтого: &a%size%"));
		checkText(getRuNode("Lore", "Price"), serializeText("&eВалюта: &a%currency%&e. Покупка: &a%buyprice%&e. Продажа: &a%sellprice%"));
		checkText(getRuNode("Lore", "AuctionPrice"), serializeText("&eВалюта: &a%currency%&e. Цена за штуку: &a%price%&e. Итого: &a%total%"));
		checkText(getRuNode("Lore", "AuctionBet"), serializeText("&eВалюта: &a%currency%&e. Ставка штуку: &a%price%&e. Итого: &a%total%"));
		checkText(getRuNode("Lore", "YourBet"), serializeText("&eВаша ставка: &a%size%. Итого: &a%total%"));
		checkText(getRuNode("Lore", "Tax"), serializeText("&eНалог на прибыль: &a%size%"));
		checkText(getRuNode("Lore", "Fee"), serializeText("&eПошлина: &a%size%"));
		checkListText(getRuNode("Lore", "AuctionSwitchMode"), Arrays.asList(serializeText("&eЛевый клик переключит тип цены"), serializeText("&eПравый клик переключит время и комиссии")));
		checkText(getRuNode("Lore", "Seller"), serializeText("&eПродавец: &b%seller%&e."));
		checkText(getRuNode("Lore", "Expired"), serializeText("&eСнимется с продажи через: &a%expired%&e."));
		checkText(getRuNode("Lore", "CurrentBuyer"), serializeText("&eТекущий покупатель по ставке: &b%buyer%&e."));
		checkText(getRuNode("Lore", "CurrentBet"), serializeText("&eТекущая ставка: &a%bet%&e."));
		
		checkText(getRuNode("Messages", "NoMoney"), serializeText("&cУ вас недостаточно денег."));
		checkText(getRuNode("Messages", "NoMoneyForFee"), serializeText("&cУ вас недостаточно денег для выставления предмета на аукцион."));
		checkText(getRuNode("Messages", "NoItems"), serializeText("&cУ вас недостаточно предметов."));
		checkText(getRuNode("Messages", "ItemSell"), serializeText("&aВы продали &7[&r%item%&7]&ax%amount% за %added%. Ваш баланс %balance%."));
		checkText(getRuNode("Messages", "ItemBuy"), serializeText("&aВы купили &7[&r%item%&7]&ax%amount% за %removed%. Ваш баланс %balance%."));
		checkText(getRuNode("Messages", "OnlyPlayer"), serializeText("&cЭта команда может выполняться только игроком."));
		checkText(getRuNode("Messages", "ShopIDNotPresent"), serializeText("&cНе указан id магазина."));
		checkText(getRuNode("Messages", "ShopIDAlreadyExists"), serializeText("&cМагазин с таким id уже существует."));
		checkText(getRuNode("Messages", "ShopIDNotExists"), serializeText("&cМагазина с таким id не существует."));
		checkText(getRuNode("Messages", "InvalidShopID"), serializeText("&cВведено не допустимое имя магазина."));
		checkText(getRuNode("Messages", "MenuNotPresent"), serializeText("&cНе указан номер меню."));
		checkText(getRuNode("Messages", "InvalidMenuId"), serializeText("&cМеню с таким номером не существует, создайте его через GUI."));
		checkText(getRuNode("Messages", "SlotNotPresent"), serializeText("&cНе указан слот для размещения товара."));
		checkText(getRuNode("Messages", "InvalidSlot"), serializeText("&cНомер слота должен быть в диапазоне от 0 до 44."));
		checkText(getRuNode("Messages", "BuyPriceNotPresent"), serializeText("&cВы не указали цену покупки."));
		checkText(getRuNode("Messages", "SellPriceNotPresent"), serializeText("&cВы не указали цену продажи."));
		checkText(getRuNode("Messages", "ItemNotPresent"), serializeText("&cПожалуйста предъявите итем ┬──┬ ノ(゜-゜ノ)"));
		checkText(getRuNode("Messages", "ShopItemAdded"), serializeText("&aПредмет &7[&r%item%&7]&a добавлен в магазин %shop%."));
		checkText(getRuNode("Messages", "PlayerIsNotPresent"), serializeText("&cНужно быть игроком или указать ник игрока."));
		checkText(getRuNode("Messages", "DontOpenOther"), serializeText("&cВы не можете открывать магазин другому игроку."));
		checkText(getRuNode("Messages", "SuccessDelete"), serializeText("&aМагазин удален."));
		checkText(getRuNode("Messages", "ShopListEmpty"), serializeText("&eСписок магазинов сейчас пуст. Обратитесь к администрации."));
		checkText(getRuNode("Messages", "ShopListEmptyEditor"), serializeText("&eСписок магазинов пуст. Создайте хотя бы 1 магазин."));
		checkText(getRuNode("Messages", "DebugOn"), serializeText("&aДебаг включен."));
		checkText(getRuNode("Messages", "DebugOff"), serializeText("&aДебаг выключен."));
		checkText(getRuNode("Messages", "Reload"), serializeText("&aПлагин перезагружен."));
		checkText(getRuNode("Messages", "EmptyTranslateName"), serializeText("&aНе указано имя локализации."));
		checkText(getRuNode("Messages", "LocaleNotExist"), serializeText("&cТакой локализации не существует. Формат en-US, ru-RU и так далее."));
		checkText(getRuNode("Messages", "LocaleNotPresent"), serializeText("&cУкажите требуемую локализацию. Формат en-US, ru-RU и так далее."));
		checkText(getRuNode("Messages", "TranslateNotPresent"), serializeText("&cВведите отображаемое имя для магазина. Принимаются цветовые коды."));
		checkText(getRuNode("Messages", "TranslateAdded"), serializeText("&aВы успешно установили отображаемое имя для магазина."));
		checkText(getRuNode("Messages", "ShopListTitle"), serializeText("&3Список магазинов"));
		checkText(getRuNode("Messages", "ShopListPadding"), serializeText("&3="));
		checkText(getRuNode("Messages", "CommandsTitle"), serializeText("&3Список команд"));
		checkText(getRuNode("Messages", "CommandsPadding"), serializeText("&3="));
		checkText(getRuNode("Messages", "AuctionMaxVolume"), serializeText("&cВы выставили максимальный объем предметов на продажу."));
		checkText(getRuNode("Messages", "AuctionBuy"), serializeText("&aВы купили на аукционе &7[&r%item%&7]&ax%amount% за %removed%. Ваш баланс %balance%. Продавец &b%seller%&a."));
		checkText(getRuNode("Messages", "AuctionSell"), serializeText("&aВы продали на аукционе &7[&r%item%&7]&ax%amount% за %added%. Ваш баланс %balance%. Покупатель &b%buyer%&a."));
		checkText(getRuNode("Messages", "AuctionExpired"), serializeText("&aСрок выставления ваших предметов на продажу истек. Кликните на это сообщение, чтобы получить их обратно."));
		checkText(getRuNode("Messages", "AuctionBetExpired"), serializeText("&aЗавершена сделка по выкупу предметов на аукционе. Кликните на это сообщение, чтобы получить их."));
		checkText(getRuNode("Messages", "Tax"), serializeText("&aУдержанный налог: %amount%."));
		checkText(getRuNode("Messages", "NoEmptySlots"), serializeText("&cВ вашем инвентаре недостаточно пустых слотов. Освободите хотя бы %value%."));
		checkText(getRuNode("Messages", "AuctionItemNotFound"), serializeText("&cПредмет был куплен кем-то другим или снят с продажи."));
		checkText(getRuNode("Messages", "AuctionBetNotPresent"), serializeText("&eВы не указали ставку для вашего лота. Для подтверждения выставления лота кликните на это сообщение."));
		checkText(getRuNode("Messages", "AuctionPriceNotPresent"), serializeText("&eВы не указали цену для вашего лота. Для подтверждения выставления лота кликните на это сообщение."));
		checkText(getRuNode("Messages", "AuctionCurrencyNotPresent"), serializeText("&eВы не указали валюту для вашего лота. Будет использована валюта по умолчанию. Для подтверждения выставления лота кликните на это сообщение. \n&eДоступные валюты: &6%currencies%&e."));
		checkText(getRuNode("Messages", "AuctionZeroOrNullPrices"), serializeText("&cНельзя продать предмет без цены/ставки или с нулевой ценой/ставкой. Одно из значений должно быть больше нуля."));
		checkText(getRuNode("Messages", "AuctionCancelBuy"), serializeText("&cНельзя купить или назначить ставку на свой предмет."));
		checkText(getRuNode("Messages", "AuctionItemAdded"), serializeText("&aВы выставили предмет на продажу."));
		checkText(getRuNode("Messages", "ItemBlocked"), serializeText("&cЭтот предмет нельзя выставить на продажу."));
		checkText(getRuNode("Messages", "AddedItemBlocking "), serializeText("&aПредмет заблокирован."));
		checkText(getRuNode("Messages", "AddedMaskBlocking "), serializeText("&aДобавленна маска блокировки предметов."));
		checkText(getRuNode("Messages", "LongNBT"), serializeText("&cУ предмета слишком длинный NBT тег."));
		checkText(getRuNode("Messages", "AddedToBlackList"), serializeText("&cПредмет добавлен в черный список."));
		checkText(getRuNode("Messages", "ItemIsAlreadyBlocked"), serializeText("&cПредмет уже заблокирован."));
		
		checkText(getRuNode("Hover", "OpenShopEdit"), serializeText("&eКлик для открытия этого магазина в редакторе."));
		checkText(getRuNode("Hover", "OpenShop"), serializeText("&eКлик для открытия этого магазина."));
		checkText(getRuNode("Hover", "RunCommand"), serializeText("&eКлик для выполнения команды."));

		checkText(getRuNode("Gui", "Auction"), serializeText("&2Аукцион"));
		checkText(getRuNode("Gui", "AuctionBet"), serializeText("&2Ставка"));
		checkText(getRuNode("Gui", "AuctionReturn"), serializeText("&2Возврат предметов"));
		checkText(getRuNode("Gui", "EditBuyItem"), serializeText("&2Настройка покупки товара"));
		checkText(getRuNode("Gui", "EditSellItem"), serializeText("&2Настройка продажи товара"));
		checkText(getRuNode("Gui", "EditAuctionItem"), serializeText("&2Настройка товара"));
		checkText(getRuNode("Gui", "EditBuyTransaction"), serializeText("&2Покупка"));
		checkText(getRuNode("Gui", "EditSellTransaction"), serializeText("&2Продажа"));
		
		checkText(getRuNode("Debug", "ErrorTakeMoney"), serializeText("&cНе удалось списать деньги с баланса игрока &e%player%&c."));
		checkText(getRuNode("Debug", "ErrorGiveMoney"), serializeText("&cНе удалось начислить деньги на баланс игрока &e%player%&c."));
		checkText(getRuNode("Debug", "InfoTakeItems"), serializeText("&aПредмет &7[&r%item%]&ax%amount% удален из инвентаря игрока &e%player%&a. Начисленно денег %added%. Баланс %balance%."));
		checkText(getRuNode("Debug", "InfoGiveItems"), serializeText("&aПредмет &7[&r%item%]&ax%amount% добавлен в инвентарь игрока &e%player%&a. Списано денег %removed%. Баланс %balance%."));
		plugin.getLocales().getLocale(RU_RU).saveLocaleNode();
	}

	private void generateDefaultLocale() {
		checkText(getDefaultNode("FillItems", "Back"), serializeText("&eBack"));
		checkText(getDefaultNode("FillItems", "Next"), serializeText("&eNext"));
		checkText(getDefaultNode("FillItems", "AddPage"), serializeText("&eAdd page"));
		checkText(getDefaultNode("FillItems", "Buy"), serializeText("&eBuy and exit"));
		checkText(getDefaultNode("FillItems", "Sell"), serializeText("&eSell and exit"));
		checkText(getDefaultNode("FillItems", "Exit"), serializeText("&eSave and exit"));
		checkText(getDefaultNode("FillItems", "BuyAndBack"), serializeText("&eBuy and go back"));
		checkText(getDefaultNode("FillItems", "SellAndBack"), serializeText("&eSell and go back"));
		checkText(getDefaultNode("FillItems", "Size"), serializeText("&eSize: &a%value%"));
		checkText(getDefaultNode("FillItems", "Price"), serializeText("&ePrice: &a%value%"));
		checkText(getDefaultNode("FillItems", "Clear"), serializeText("&eClear"));
		checkText(getDefaultNode("FillItems", "ChangeCurrency"), serializeText("&eChange currency"));
		checkText(getDefaultNode("FillItems", "SwitchMode"), serializeText("&eSwitch mode"));
		checkText(getDefaultNode("FillItems", "AuctionAddItem"), serializeText("&eSale your item"));
		checkText(getDefaultNode("FillItems", "ReturnAuctionItem"), serializeText("&eReturn your items"));

		checkListText(getDefaultNode("Lore", "ChangePrice"), Arrays.asList(serializeText("&aLeft click will increase the price "), serializeText("&aRight click will decrease the price ")));
		checkListText(getDefaultNode("Lore", "ChangeSize"), Arrays.asList(serializeText("&aLeft click will increase the size"), serializeText("&aRight click will decrease the size")));
		checkText(getDefaultNode("Lore", "TransactionVariants"), serializeText("&eTransaction variants: ▼"));
		checkText(getDefaultNode("Lore", "CurrentCurrency"), serializeText("&eCurrent currency: &a%currency%"));
		checkText(getDefaultNode("Lore", "CurrentSize"), serializeText("&eSelected size: &a%size%"));
		checkText(getDefaultNode("Lore", "CurrentSum"), serializeText("&eTotal: &a%size%"));
		checkText(getDefaultNode("Lore", "Price"), serializeText("&eCurrency: &a%currency%&e. Buy: &a%buyprice%&e. Sell: &a%sellprice%"));
		checkText(getDefaultNode("Lore", "AuctionPrice"), serializeText("&eCurrency: &a%currency%&e. Price for one: &a%price%&e. Total: &a%total%"));
		checkText(getDefaultNode("Lore", "AuctionBet"), serializeText("&eCurrency: &a%currency%&e. Bet for one: &a%price%&e. Total: &a%total%"));
		checkText(getDefaultNode("Lore", "YourBet"), serializeText("&eYour bet: &a%size%. Total: &a%total%"));
		checkText(getDefaultNode("Lore", "Tax"), serializeText("&eTax: &a%size%"));
		checkText(getDefaultNode("Lore", "Fee"), serializeText("&eFee: &a%size%"));
		checkListText(getDefaultNode("Lore", "AuctionSwitchMode"), Arrays.asList(serializeText("&eLeft click to switch the price type"), serializeText("&eRight click to switch time and commissions")));
		checkText(getDefaultNode("Lore", "Seller"), serializeText("&eSeller: &b%seller%&e."));
		checkText(getDefaultNode("Lore", "Expired"), serializeText("&eExpired: &a%expired%&e."));
		checkText(getDefaultNode("Lore", "CurrentBuyer"), serializeText("&eCurrent buyer at the bet: &b%buyer%&e."));
		checkText(getDefaultNode("Lore", "CurrentBet"), serializeText("&eCurrent bet: &a%bet%&e."));
		
		checkText(getDefaultNode("Messages", "NoMoney"), serializeText("&cYou don't have enough money."));
		checkText(getDefaultNode("Messages", "NoMoneyForFee"), serializeText("&cYou do not have enough money to list the item for sale at the auction."));
		checkText(getDefaultNode("Messages", "NoItems"), serializeText("&cYou don't have enough items."));
		checkText(getDefaultNode("Messages", "ItemSell"), serializeText("&aYou have successfully sold &7[&r%item%&7]&ax%amount% for %added%. Your balance %balance%."));
		checkText(getDefaultNode("Messages", "ItemBuy"), serializeText("&aYou have successfully purchased &7[&r%item%&7]&ax%amount% for %removed%. Your balance %balance%."));
		checkText(getDefaultNode("Messages", "OnlyPlayer"), serializeText("&cThis command can only be executed by the player."));
		checkText(getDefaultNode("Messages", "ShopIDNotPresent"), serializeText("&cShop id not specified."));
		checkText(getDefaultNode("Messages", "ShopIDAlreadyExists"), serializeText("&cA shop with this id already exists."));
		checkText(getDefaultNode("Messages", "ShopIDNotExists"), serializeText("&cThere is no shop with this id."));
		checkText(getDefaultNode("Messages", "InvalidShopID"), serializeText("&cThe specified shop name is not permissible."));
		checkText(getDefaultNode("Messages", "MenuNotPresent"), serializeText("&cMenu number not specified."));
		checkText(getDefaultNode("Messages", "InvalidMenuId"), serializeText("&cMenu with this number does not exist, create it via GUI."));
		checkText(getDefaultNode("Messages", "SlotNotPresent"), serializeText("&cThe slot for placing the item is not specified."));
		checkText(getDefaultNode("Messages", "InvalidSlot"), serializeText("&cThe slot number must be between 0 and 44."));
		checkText(getDefaultNode("Messages", "BuyPriceNotPresent"), serializeText("&cSpecify the purchase price."));
		checkText(getDefaultNode("Messages", "SellPriceNotPresent"), serializeText("&cSpecify the sale price."));
		checkText(getDefaultNode("Messages", "ItemNotPresent"), serializeText("&cPlease present the item ┬──┬ ノ(゜-゜ノ)"));
		checkText(getDefaultNode("Messages", "ShopItemAdded"), serializeText("&cItem &7[&r%item%&7]&a added to shop %shop%."));
		checkText(getDefaultNode("Messages", "InvalidItem"), serializeText("&cThis item cannot be sold."));
		checkText(getDefaultNode("Messages", "PlayerIsNotPresent"), serializeText("&cYou need to be a player or specify the player's nickname."));
		checkText(getDefaultNode("Messages", "DontOpenOther"), serializeText("&cYou can't open a store for another player."));
		checkText(getDefaultNode("Messages", "SuccessDelete"), serializeText("&aThe shop was deleted."));
		checkText(getDefaultNode("Messages", "ShopListEmpty"), serializeText("&eThe shop list is now empty. Contact the administration."));
		checkText(getDefaultNode("Messages", "ShopListEmptyEditor"), serializeText("&eThe shop list is empty. Create at least 1 store. "));
		checkText(getDefaultNode("Messages", "DebugOn"), serializeText("&aDebug on."));
		checkText(getDefaultNode("Messages", "DebugOff"), serializeText("&aDebug off."));
		checkText(getDefaultNode("Messages", "Reload"), serializeText("&aThe plugin has been reloaded."));
		checkText(getDefaultNode("Messages", "EmptyTranslateName"), serializeText("&aLocalization name not specified."));
		checkText(getDefaultNode("Messages", "LocaleNotExist"), serializeText("&cThere is no such localization. The format is en-US, ru-RU, etc."));
		checkText(getDefaultNode("Messages", "LocaleNotPresent"), serializeText("&cSpecify the required localization. The format is en-US, ru-RU, etc."));
		checkText(getDefaultNode("Messages", "TranslateNotPresent"), serializeText("&cEnter a display name for your shop. Color codes accepted."));
		checkText(getDefaultNode("Messages", "TranslateAdded"), serializeText("&aYou have successfully set the display name for your shop."));
		checkText(getDefaultNode("Messages", "ShopListTitle"), serializeText("&3List of shops"));
		checkText(getDefaultNode("Messages", "ShopListPadding"), serializeText("&3="));
		checkText(getDefaultNode("Messages", "CommandsTitle"), serializeText("&3Command list"));
		checkText(getDefaultNode("Messages", "CommandsPadding"), serializeText("&3="));
		checkText(getDefaultNode("Messages", "AuctionMaxVolume"), serializeText("&cYou are already selling the maximum amount of items."));
		checkText(getDefaultNode("Messages", "AuctionBuy"), serializeText("&aYou have successfully sold on auction &7[&r%item%&7]&ax%amount% за %removed%. Your balance %balance%. Seller &b%seller%&a."));
		checkText(getDefaultNode("Messages", "AuctionSell"), serializeText("&aYou have successfully sold on auction &7[&r%item%&7]&ax%amount% за %added%. Your balance %balance%. Buyer &b%buyer%&a."));
		checkText(getDefaultNode("Messages", "AuctionExpired"), serializeText("&aYour items have expired. Click on this message to get them back."));
		checkText(getDefaultNode("Messages", "AuctionBetExpired"), serializeText("&aCompleted transaction for redemption of items at auction. Click on this message to get them."));
		checkText(getDefaultNode("Messages", "Tax"), serializeText("&aWithholding tax: %amount%."));
		checkText(getDefaultNode("Messages", "NoEmptySlots"), serializeText("&cThere are not enough empty slots in your inventory. Free at least %value%."));
		checkText(getDefaultNode("Messages", "AuctionItemNotFound"), serializeText("&cItem was purchased by someone else or removed from sale."));
		checkText(getDefaultNode("Messages", "AuctionBetNotPresent"), serializeText("&eYou didn't specify a bet for your lot. To confirm the lot placement, click on this message."));
		checkText(getDefaultNode("Messages", "AuctionPriceNotPresent"), serializeText("&eYou didn't specify a price for your lot. To confirm the lot placement, click on this message."));
		checkText(getDefaultNode("Messages", "AuctionCurrencyNotPresent"), serializeText("&eYou did not specify the currency for your lot. The default currency will be used. To confirm the lot placement, click on this message. \n&eAvailable currencies: &6%currencies%&e."));
		checkText(getDefaultNode("Messages", "AuctionZeroOrNullPrices"), serializeText("&cYou cannot sell an item without a price/bet or with a zero price/bet. One of the values must be greater than zero."));
		checkText(getDefaultNode("Messages", "AuctionCancelBuy"), serializeText("&cYou can't buy or assign a bet on your item."));
		checkText(getDefaultNode("Messages", "AuctionItemAdded"), serializeText("&aYou putted the item up for sale."));
		checkText(getDefaultNode("Messages", "ItemBlocked"), serializeText("&cThis item cannot be put up for sale."));
		checkText(getDefaultNode("Messages", "AddedItemBlocking "), serializeText("&cThe item is now locked."));
		checkText(getDefaultNode("Messages", "AddedMaskBlocking "), serializeText("&cBlocking mask added."));
		checkText(getDefaultNode("Messages", "LongNBT"), serializeText("&cThe item has an NBT tag that is too long."));
		checkText(getDefaultNode("Messages", "AddedToBlackList"), serializeText("&cItem added to the blacklist."));
		checkText(getDefaultNode("Messages", "ItemIsAlreadyBlocked"), serializeText("&cThe item is already blocked."));
		
		checkText(getDefaultNode("Hover", "OpenShopEdit"), serializeText("&eClick to open this shop in the editor."));
		checkText(getDefaultNode("Hover", "OpenShop"), serializeText("&eClick to open this store."));
		checkText(getDefaultNode("Hover", "RunCommand"), serializeText("&eClick to execute this command."));

		checkText(getDefaultNode("Gui", "Auction"), serializeText("&2Auction"));
		checkText(getDefaultNode("Gui", "AuctionBet"), serializeText("&2Bet"));
		checkText(getDefaultNode("Gui", "AuctionReturn"), serializeText("&2Return items"));
		checkText(getDefaultNode("Gui", "EditBuyItem"), serializeText("&2Setting of purchase item"));
		checkText(getDefaultNode("Gui", "EditSellItem"), serializeText("&2Setting of sell item"));
		checkText(getDefaultNode("Gui", "EditAuctionItem"), serializeText("&2Setting an item"));
		checkText(getDefaultNode("Gui", "EditBuyTransaction"), serializeText("&2Buy"));
		checkText(getDefaultNode("Gui", "EditSellTransaction"), serializeText("&2Sell"));
		
		checkText(getDefaultNode("Debug", "ErrorTakeMoney"), serializeText("&cFailed to remove money from balance of player &e%player%&c."));
		checkText(getDefaultNode("Debug", "ErrorGiveMoney"), serializeText("&cFailed to add money to the balance of player &e%player%&c."));
		checkText(getDefaultNode("Debug", "InfoTakeItems"), serializeText("&aItem &7[&r%item%]&ax%amount% removed from inventory of player &e%player%&a. Added money %added%. Balance %balance%."));
		checkText(getDefaultNode("Debug", "InfoGiveItems"), serializeText("&aItem &7[&r%item%]&ax%amount% added to inventory of player &e%player%&a. Removed money %removed%. Balance %balance%."));
		plugin.getLocales().getDefaultLocale().saveLocaleNode();
	}

	private void checkListText(ConfigurationNode node, List<Text> texts) {
        if (node.isVirtual()) {
			save = true;
            try {
				node.setValue(TypeTokens.LIST_TEXT_TOKEN, texts);
			} catch (ObjectMappingException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
        }
	}
	private void checkText(ConfigurationNode node, Text text) {
        if (node.isVirtual()) {
			save = true;
            try {
				node.setValue(TypeTokens.TEXT_TOKEN, text);
			} catch (ObjectMappingException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
        }
    }
	private ConfigurationNode getRuNode(Object... node) {
		return plugin.getLocales().getLocale(RU_RU).getLocaleNode().getNode(node);
	}
	private ConfigurationNode getDefaultNode(Object... node) {
		return plugin.getLocales().getDefaultLocale().getLocaleNode().getNode(node);
	}
	private Text serializeText(String text) {
		return TextSerializers.FORMATTING_CODE.deserialize(text);
	}

}
