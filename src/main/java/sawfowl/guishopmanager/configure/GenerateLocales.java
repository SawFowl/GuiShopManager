package sawfowl.guishopmanager.configure;

import java.util.Arrays;
import org.spongepowered.api.util.locale.Locales;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.localeapi.api.ConfigTypes;
import sawfowl.localeapi.utils.AbstractLocaleUtil;

public class GenerateLocales {

	private GuiShopManager plugin;
	private boolean save = false;
	public GenerateLocales(GuiShopManager guiShopManager) {
		plugin = guiShopManager;
		if(!plugin.getLocaleAPI().localesExist("guishopmanager")) {
			createLocales();
		}
		generateDefaultLocale();
		generateLocaleRu();
	}

	private void createLocales() {
		plugin.getLocaleAPI().createPluginLocale("guishopmanager", ConfigTypes.YAML, Locales.DEFAULT);
		plugin.getLocaleAPI().createPluginLocale("guishopmanager", ConfigTypes.YAML, Locales.RU_RU);
	}

	private void generateLocaleRu() {
		updateSave(getRuLocale().checkString("&eНазад", "", "FillItems", "Back"));
		updateSave(getRuLocale().checkString("&eДалее", "", "FillItems", "Next"));
		updateSave(getRuLocale().checkString("&eДобавить страницу", "", "FillItems", "AddPage"));
		updateSave(getRuLocale().checkString("&eКупить и выйти", "", "FillItems", "Buy"));
		updateSave(getRuLocale().checkString("&eПродать и выйти", "", "FillItems", "Sell"));
		updateSave(getRuLocale().checkString("&eСохранить и выйти", "", "FillItems", "Exit"));
		updateSave(getRuLocale().checkString("&eКупить и вернуться назад", "", "FillItems", "BuyAndBack"));
		updateSave(getRuLocale().checkString("&eПродать и вернуться назад", "", "FillItems", "SellAndBack"));
		updateSave(getRuLocale().checkString("&eОбъем: &a%value%", "", "FillItems", "Size"));
		updateSave(getRuLocale().checkString("&eЦена: &a%value%", "", "FillItems", "Price"));
		updateSave(getRuLocale().checkString("&eСброс", "", "FillItems", "Clear"));
		updateSave(getRuLocale().checkString("&eСменить валюту", "", "FillItems", "ChangeCurrency"));
		updateSave(getRuLocale().checkString("&eТекущая валюта", "", "FillItems", "CurrentCurrency"));
		updateSave(getRuLocale().checkString("&eПереключить режим", "", "FillItems", "SwitchMode"));
		updateSave(getRuLocale().checkString("&eВыставить предмет на продажу", "", "FillItems", "AuctionAddItem"));
		updateSave(getRuLocale().checkString("&eВернуть ваши предметы", "", "FillItems", "ReturnAuctionItem"));

		updateSave(getRuLocale().checkListStrings(Arrays.asList("&aЛевый клик увеличит цену", "&aПравый клик уменьшит цену"), "", "Lore", "ChangePrice"));
		updateSave(getRuLocale().checkListStrings(Arrays.asList("&aЛевый клик увеличит объем", "&aПравый клик уменьшит объем"), "", "Lore", "ChangeSize"));
		updateSave(getRuLocale().checkString("&eВарианты транзакций: ▼", "", "Lore", "TransactionVariants"));
		updateSave(getRuLocale().checkString("&eТекущая валюта: &a%currency%", "", "Lore", "CurrentCurrency"));
		updateSave(getRuLocale().checkString("&eВыбранный объем: &a%size%", "", "Lore", "CurrentSize"));
		updateSave(getRuLocale().checkString("&eИтого: &a%size%", "", "Lore", "CurrentSum"));
		updateSave(getRuLocale().checkString("&eВалюта: &a%currency%&e. Покупка: &a%buyprice%&e. Продажа: &a%sellprice%", "", "Lore", "Price"));
		updateSave(getRuLocale().checkString("&eВалюта: &a%currency%&e. Покупка: &a%buyprice%&e.", "", "Lore", "CommandPrice"));
		updateSave(getRuLocale().checkString("&eВалюта: &a%currency%&e. Цена за штуку: &a%price%&e. Итого: &a%total%", "", "Lore", "AuctionPrice"));
		updateSave(getRuLocale().checkString("&eВалюта: &a%currency%&e. Ставка штуку: &a%price%&e. Итого: &a%total%", "", "Lore", "AuctionBet"));
		updateSave(getRuLocale().checkString("&eВаша ставка: &a%size%. Итого: &a%total%", "", "Lore", "YourBet"));
		updateSave(getRuLocale().checkString("&eНалог на прибыль: &a%size%", "", "Lore", "Tax"));
		updateSave(getRuLocale().checkString("&eПошлина: &a%size%", "", "Lore", "Fee"));
		updateSave(getRuLocale().checkString("&eВключить/выключить бесплатное использование", "", "Lore", "SwitchFree"));
		updateSave(getRuLocale().checkString("&eДоступно бесплатно", "", "Lore", "AllowFree"));
		updateSave(getRuLocale().checkListStrings(Arrays.asList("&eЛевый клик переключит тип цены", "&eПравый клик переключит время и комиссии"), "", "Lore", "AuctionSwitchMode"));
		updateSave(getRuLocale().checkString("&eПродавец: &b%seller%&e.", "", "Lore", "Seller"));
		updateSave(getRuLocale().checkString("&eСнимется с продажи через: &a%expired%&e.", "", "Lore", "Expired"));
		updateSave(getRuLocale().checkString("&eТекущий покупатель по ставке: &b%buyer%&e.", "", "Lore", "CurrentBuyer"));
		updateSave(getRuLocale().checkString("&eТекущая ставка: &a%bet%&e.", "", "Lore", "CurrentBet"));
		updateSave(getRuLocale().checkString("&dЛКМ &f- &dназначить ставку", "", "Lore", "BetClick"));
		updateSave(getRuLocale().checkString("&dПКМ &f- &dкупить предмет", "", "Lore", "BuyClick"));
		
		updateSave(getRuLocale().checkString("&cУ вас недостаточно денег.", "", "Messages", "NoMoney"));
		updateSave(getRuLocale().checkString("&cУ вас недостаточно денег для выставления предмета на аукцион.", "", "Messages", "NoMoneyForFee"));
		updateSave(getRuLocale().checkString("&cУ вас недостаточно предметов.", "", "Messages", "NoItems"));
		updateSave(getRuLocale().checkString("&aВы продали &7[&r%item%&7]&ax%amount% за %added%. Ваш баланс %balance%.", "", "Messages", "ItemSell"));
		updateSave(getRuLocale().checkString("&aВы купили &7[&r%item%&7]&ax%amount% за %removed%. Ваш баланс %balance%.", "", "Messages", "ItemBuy"));
		updateSave(getRuLocale().checkString("&aВы купили выполнение комманд консолью за %removed%. Ваш баланс %balance%.", "", "Messages", "BuyCommands"));
		updateSave(getRuLocale().checkString("&cЭта команда может выполняться только игроком.", "", "Messages", "OnlyPlayer"));
		updateSave(getRuLocale().checkString("&cНе указан id магазина.", "", "Messages", "ShopIDNotPresent"));
		updateSave(getRuLocale().checkString("&cМагазин с таким id уже существует.", "", "Messages", "ShopIDAlreadyExists"));
		updateSave(getRuLocale().checkString("&cМагазина с таким id не существует.", "", "Messages", "ShopIDNotExists"));
		updateSave(getRuLocale().checkString("&cВведено не допустимое имя магазина.", "", "Messages", "InvalidShopID"));
		updateSave(getRuLocale().checkString("&cНе указан номер меню.", "", "Messages", "MenuNotPresent"));
		updateSave(getRuLocale().checkString("&cМеню с таким номером не существует, создайте его через GUI.", "", "Messages", "InvalidMenuId"));
		updateSave(getRuLocale().checkString("&cНе указан слот для размещения товара.", "", "Messages", "SlotNotPresent"));
		updateSave(getRuLocale().checkString("&cНомер слота должен быть в диапазоне от 0 до 44.", "", "Messages", "InvalidSlot"));
		updateSave(getRuLocale().checkString("&cВы не указали цену покупки.", "", "Messages", "BuyPriceNotPresent"));
		updateSave(getRuLocale().checkString("&cВы не указали цену продажи.", "", "Messages", "SellPriceNotPresent"));
		updateSave(getRuLocale().checkString("&cПожалуйста предъявите итем ┬──┬ ノ(゜-゜ノ)", "", "Messages", "ItemNotPresent"));
		updateSave(getRuLocale().checkString("&aПредмет &7[&r%item%&7]&a добавлен в магазин %shop%.", "", "Messages", "ShopItemAdded"));
		updateSave(getRuLocale().checkString("&cНужно быть игроком или указать ник игрока.", "", "Messages", "PlayerIsNotPresent"));
		updateSave(getRuLocale().checkString("&cВы не можете открывать меню другому игроку.", "", "Messages", "DontOpenOther"));
		updateSave(getRuLocale().checkString("&aМагазин удален.", "", "Messages", "SuccessDelete"));
		updateSave(getRuLocale().checkString("&eСписок магазинов сейчас пуст. Обратитесь к администрации.", "", "Messages", "ShopListEmpty"));
		updateSave(getRuLocale().checkString("&eСписок магазинов пуст. Создайте хотя бы 1 магазин.", "", "Messages", "ShopListEmptyEditor"));
		updateSave(getRuLocale().checkString("&aДебаг включен.", "", "Messages", "DebugOn"));
		updateSave(getRuLocale().checkString("&aДебаг выключен.", "", "Messages", "DebugOff"));
		updateSave(getRuLocale().checkString("&aПлагин перезагружен.", "", "Messages", "Reload"));
		updateSave(getRuLocale().checkString("&aНе указано имя локализации.", "", "Messages", "EmptyTranslateName"));
		updateSave(getRuLocale().checkString("&cТакой локализации не существует. Формат en-US, ru-RU и так далее.", "", "Messages", "LocaleNotExist"));
		updateSave(getRuLocale().checkString("&cУкажите требуемую локализацию. Формат en-US, ru-RU и так далее.", "", "Messages", "LocaleNotPresent"));
		updateSave(getRuLocale().checkString("&cВведите отображаемое имя для магазина. Принимаются цветовые коды.", "", "Messages", "TranslateNotPresent"));
		updateSave(getRuLocale().checkString("&aВы успешно установили отображаемое имя для магазина.", "", "Messages", "TranslateAdded"));
		updateSave(getRuLocale().checkString("&3Список магазинов", "", "Messages", "ShopListTitle"));
		updateSave(getRuLocale().checkString("&3=", "", "Messages", "ShopListPadding"));
		updateSave(getRuLocale().checkString("&3Список команд", "", "Messages", "CommandsTitle"));
		updateSave(getRuLocale().checkString("&3=", "", "Messages", "CommandsPadding"));
		updateSave(getRuLocale().checkString("&cВы выставили максимальный объем предметов на продажу.", "", "Messages", "AuctionMaxVolume"));
		updateSave(getRuLocale().checkString("&aВы купили на аукционе &7[&r%item%&7]&ax%amount% за %removed%. Ваш баланс %balance%. Продавец &b%seller%&a.", "", "Messages", "AuctionBuy"));
		updateSave(getRuLocale().checkString("&aВы продали на аукционе &7[&r%item%&7]&ax%amount% за %added%. Ваш баланс %balance%. Покупатель &b%buyer%&a.", "", "Messages", "AuctionSell"));
		updateSave(getRuLocale().checkString("&aСрок выставления ваших предметов на продажу истек. Кликните на это сообщение, чтобы получить их обратно.", "", "Messages", "AuctionExpired"));
		updateSave(getRuLocale().checkString("&aЗавершена сделка по выкупу предметов на аукционе. Кликните на это сообщение, чтобы получить их.", "", "Messages", "AuctionBetExpired"));
		updateSave(getRuLocale().checkString("&aУдержанный налог: %amount%.", "", "Messages", "Tax"));
		updateSave(getRuLocale().checkString("&cВ вашем инвентаре недостаточно пустых слотов. Освободите хотя бы %value%.", "", "Messages", "NoEmptySlots"));
		updateSave(getRuLocale().checkString("&cПредмет был куплен кем-то другим или снят с продажи.", "", "Messages", "AuctionItemNotFound"));
		updateSave(getRuLocale().checkString("&eВы не указали ставку для вашего лота. Для подтверждения выставления лота кликните на это сообщение.", "", "Messages", "AuctionBetNotPresent"));
		updateSave(getRuLocale().checkString("&eВы не указали цену для вашего лота. Для подтверждения выставления лота кликните на это сообщение.", "", "Messages", "AuctionPriceNotPresent"));
		updateSave(getRuLocale().checkString("&eВы не указали валюту для вашего лота. Будет использована валюта по умолчанию. Для подтверждения выставления лота кликните на это сообщение. \n&eДоступные валюты: &6%currencies%&e.", "", "Messages", "AuctionCurrencyNotPresent"));
		updateSave(getRuLocale().checkString("&cНельзя продать предмет без цены/ставки или с нулевой ценой/ставкой. Одно из значений должно быть больше нуля.", "", "Messages", "AuctionZeroOrNullPrices"));
		updateSave(getRuLocale().checkString("&cНельзя купить или назначить ставку на свой предмет.", "", "Messages", "AuctionCancelBuy"));
		updateSave(getRuLocale().checkString("&aВы выставили предмет на продажу.", "", "Messages", "AuctionItemAdded"));
		updateSave(getRuLocale().checkString("&eВы не установили свою ставку на товар.", "", "Messages", "BetIsNotSet"));
		updateSave(getRuLocale().checkString("&cЭтот предмет нельзя выставить на продажу.", "", "Messages", "ItemBlocked"));
		updateSave(getRuLocale().checkString("&aПредмет заблокирован.", "", "Messages", "AddedItemBlocking"));
		updateSave(getRuLocale().checkString("&aДобавленна маска блокировки предметов.", "", "Messages", "AddedMaskBlocking"));
		updateSave(getRuLocale().checkString("&cУ предмета слишком длинный NBT тег.", "", "Messages", "LongNBT"));
		updateSave(getRuLocale().checkString("&cПредмет добавлен в черный список.", "", "Messages", "AddedToBlackList"));
		updateSave(getRuLocale().checkString("&cПредмет уже заблокирован.", "", "Messages", "ItemIsAlreadyBlocked"));
		updateSave(getRuLocale().checkString("&cНа сервере нет плагина экономики. Функционал плагина будет не доступен.", "", "Messages", "EconomyNotFound"));
		
		updateSave(getRuLocale().checkString("&eКлик для открытия этого магазина в редакторе.", "", "Hover", "OpenShopEdit"));
		updateSave(getRuLocale().checkString("&eКлик для открытия этого магазина.", "", "Hover", "OpenShop"));
		updateSave(getRuLocale().checkString("&eКлик для выполнения команды.", "", "Hover", "RunCommand"));

		updateSave(getRuLocale().checkString("&2Аукцион", "", "Gui", "Auction"));
		updateSave(getRuLocale().checkString("&2Ставка", "", "Gui", "AuctionBet"));
		updateSave(getRuLocale().checkString("&2Возврат предметов", "", "Gui", "AuctionReturn"));
		updateSave(getRuLocale().checkString("&2Настройка покупки товара", "", "Gui", "EditBuyItem"));
		updateSave(getRuLocale().checkString("&2Настройка продажи товара", "", "Gui", "EditSellItem"));
		updateSave(getRuLocale().checkString("&2Настройка покупки комманд", "", "Gui", "EditBuyCommandItem"));
		updateSave(getRuLocale().checkString("&2Настройка товара", "", "Gui", "EditAuctionItem"));
		updateSave(getRuLocale().checkString("&2Покупка", "", "Gui", "EditBuyTransaction"));
		updateSave(getRuLocale().checkString("&2Продажа", "", "Gui", "EditSellTransaction"));
		
		updateSave(getRuLocale().checkString("&cНе удалось списать деньги с баланса игрока &e%player%&c.", "", "Debug", "ErrorTakeMoney"));
		updateSave(getRuLocale().checkString("&cНе удалось начислить деньги на баланс игрока &e%player%&c.", "", "Debug", "ErrorGiveMoney"));
		updateSave(getRuLocale().checkString("&aПредмет &7[&r%item%]&ax%amount% удален из инвентаря игрока &e%player%&a. Начисленно денег %added%. Баланс %balance%.", "", "Debug", "InfoTakeItems"));
		updateSave(getRuLocale().checkString("&aПредмет &7[&r%item%]&ax%amount% добавлен в инвентарь игрока &e%player%&a. Списано денег %removed%. Баланс %balance%.", "", "Debug", "InfoGiveItems"));
		
		if(save) getRuLocale().saveLocaleNode();
		save = false;
	}

	private void generateDefaultLocale() {
		updateSave(getDefaultLocale().checkString("&eBack", "", "FillItems", "Back"));
		updateSave(getDefaultLocale().checkString("&eNext", "", "FillItems", "Next"));
		updateSave(getDefaultLocale().checkString("&eAdd page", "", "FillItems", "AddPage"));
		updateSave(getDefaultLocale().checkString("&eBuy and exit", "", "FillItems", "Buy"));
		updateSave(getDefaultLocale().checkString("&eSell and exit", "", "FillItems", "Sell"));
		updateSave(getDefaultLocale().checkString("&eSave and exit", "", "FillItems", "Exit"));
		updateSave(getDefaultLocale().checkString("&eBuy and go back", "", "FillItems", "BuyAndBack"));
		updateSave(getDefaultLocale().checkString("&eSell and go back", "", "FillItems", "SellAndBack"));
		updateSave(getDefaultLocale().checkString("&eSize: &a%value%", "", "FillItems", "Size"));
		updateSave(getDefaultLocale().checkString("&ePrice: &a%value%", "", "FillItems", "Price"));
		updateSave(getDefaultLocale().checkString("&eClear", "", "FillItems", "Clear"));
		updateSave(getDefaultLocale().checkString("&eChange currency", "", "FillItems", "ChangeCurrency"));
		updateSave(getDefaultLocale().checkString("&eSwitch mode", "", "FillItems", "SwitchMode"));
		updateSave(getDefaultLocale().checkString("&eSale your item", "", "FillItems", "AuctionAddItem"));
		updateSave(getDefaultLocale().checkString("&eReturn your items", "", "FillItems", "ReturnAuctionItem"));

		updateSave(getDefaultLocale().checkListStrings(Arrays.asList("&aLeft click will increase the price ", "&aRight click will decrease the price "), "", "Lore", "ChangePrice"));
		updateSave(getDefaultLocale().checkListStrings(Arrays.asList("&aLeft click will increase the size", "&aRight click will decrease the size"), "", "Lore", "ChangeSize"));
		updateSave(getDefaultLocale().checkString("&eTransaction variants: ▼", "", "Lore", "TransactionVariants"));
		updateSave(getDefaultLocale().checkString("&eCurrent currency: &a%currency%", "", "Lore", "CurrentCurrency"));
		updateSave(getDefaultLocale().checkString("&eSelected size: &a%size%", "", "Lore", "CurrentSize"));
		updateSave(getDefaultLocale().checkString("&eTotal: &a%size%", "", "Lore", "CurrentSum"));
		updateSave(getDefaultLocale().checkString("&eCurrency: &a%currency%&e. Buy: &a%buyprice%&e. Sell: &a%sellprice%", "", "Lore", "Price"));
		updateSave(getDefaultLocale().checkString("&eCurrency: &a%currency%&e. Buy: &a%buyprice%&e.", "", "Lore", "CommandPrice"));
		updateSave(getDefaultLocale().checkString("&eCurrency: &a%currency%&e. Price for one: &a%price%&e. Total: &a%total%", "", "Lore", "AuctionPrice"));
		updateSave(getDefaultLocale().checkString("&eCurrency: &a%currency%&e. Bet for one: &a%price%&e. Total: &a%total%", "", "Lore", "AuctionBet"));
		updateSave(getDefaultLocale().checkString("&eYour bet: &a%size%. Total: &a%total%", "", "Lore", "YourBet"));
		updateSave(getDefaultLocale().checkString("&eTax: &a%size%", "", "Lore", "Tax"));
		updateSave(getDefaultLocale().checkString("&eFee: &a%size%", "", "Lore", "Fee"));
		updateSave(getDefaultLocale().checkString("&eEnable/disable free use", "", "Lore", "SwitchFree"));
		updateSave(getDefaultLocale().checkString("&eAvailable for free", "", "Lore", "AllowFree"));
		updateSave(getDefaultLocale().checkListStrings(Arrays.asList("&eLeft click to switch the price type", "&eRight click to switch time and commissions"), "", "Lore", "AuctionSwitchMode"));
		updateSave(getDefaultLocale().checkString("&eSeller: &b%seller%&e.", "", "Lore", "Seller"));
		updateSave(getDefaultLocale().checkString("&eExpired: &a%expired%&e.", "", "Lore", "Expired"));
		updateSave(getDefaultLocale().checkString("&eCurrent buyer at the bet: &b%buyer%&e.", "", "Lore", "CurrentBuyer"));
		updateSave(getDefaultLocale().checkString("&eCurrent bet: &a%bet%&e.", "", "Lore", "CurrentBet"));
		updateSave(getDefaultLocale().checkString("&dLeft click &f- &dplace your bet", "", "Lore", "BetClick"));
		updateSave(getDefaultLocale().checkString("&dRight click &f- &dbuy an item", "", "Lore", "BuyClick"));
		
		updateSave(getDefaultLocale().checkString("&cYou don't have enough money.", "", "Messages", "NoMoney"));
		updateSave(getDefaultLocale().checkString("&cYou do not have enough money to list the item for sale at the auction.", "", "Messages", "NoMoneyForFee"));
		updateSave(getDefaultLocale().checkString("&cYou don't have enough items.", "", "Messages", "NoItems"));
		updateSave(getDefaultLocale().checkString("&aYou have successfully sold &7[&r%item%&7]&ax%amount% for %added%. Your balance %balance%.", "", "Messages", "ItemSell"));
		updateSave(getDefaultLocale().checkString("&aYou have successfully purchased &7[&r%item%&7]&ax%amount% for %removed%. Your balance %balance%.", "", "Messages", "ItemBuy"));
		updateSave(getDefaultLocale().checkString("&aYou paid %removed% to execute console commands. Your balance %balance%.", "", "Messages", "BuyCommands"));
		updateSave(getDefaultLocale().checkString("&cThis command can only be executed by the player.", "", "Messages", "OnlyPlayer"));
		updateSave(getDefaultLocale().checkString("&cShop id not specified.", "", "Messages", "ShopIDNotPresent"));
		updateSave(getDefaultLocale().checkString("&cA shop with this id already exists.", "", "Messages", "ShopIDAlreadyExists"));
		updateSave(getDefaultLocale().checkString("&cThere is no shop with this id.", "", "Messages", "ShopIDNotExists"));
		updateSave(getDefaultLocale().checkString("&cВведено не допустимое имя магазина.", "", "Messages", "InvalidShopID"));
		updateSave(getDefaultLocale().checkString("&cMenu number not specified.", "", "Messages", "MenuNotPresent"));
		updateSave(getDefaultLocale().checkString("&cMenu with this number does not exist, create it via GUI.", "", "Messages", "InvalidMenuId"));
		updateSave(getDefaultLocale().checkString("&cThe slot for placing the item is not specified.", "", "Messages", "SlotNotPresent"));
		updateSave(getDefaultLocale().checkString("&cThe slot number must be between 0 and 44.", "", "Messages", "InvalidSlot"));
		updateSave(getDefaultLocale().checkString("&cSpecify the purchase price.", "", "Messages", "BuyPriceNotPresent"));
		updateSave(getDefaultLocale().checkString("&cSpecify the sale price.", "", "Messages", "SellPriceNotPresent"));
		updateSave(getDefaultLocale().checkString("&cPlease present the item ┬──┬ ノ(゜-゜ノ)", "", "Messages", "ItemNotPresent"));
		updateSave(getDefaultLocale().checkString("&cItem &7[&r%item%&7]&a added to shop %shop%.", "", "Messages", "ShopItemAdded"));
		updateSave(getDefaultLocale().checkString("&cThis item cannot be sold.", "", "Messages", "InvalidItem"));
		updateSave(getDefaultLocale().checkString("&cYou need to be a player or specify the player's nickname.", "", "Messages", "PlayerIsNotPresent"));
		updateSave(getDefaultLocale().checkString("&cYou can not open the menu to another player.", "", "Messages", "DontOpenOther"));
		updateSave(getDefaultLocale().checkString("&aThe shop was deleted.", "", "Messages", "SuccessDelete"));
		updateSave(getDefaultLocale().checkString("&eThe shop list is now empty. Contact the administration.", "", "Messages", "ShopListEmpty"));
		updateSave(getDefaultLocale().checkString("&eThe shop list is empty. Create at least 1 store. ", "", "Messages", "ShopListEmptyEditor"));
		updateSave(getDefaultLocale().checkString("&aDebug on.", "", "Messages", "DebugOn"));
		updateSave(getDefaultLocale().checkString("&aDebug off.", "", "Messages", "DebugOff"));
		updateSave(getDefaultLocale().checkString("&aThe plugin has been reloaded.", "", "Messages", "Reload"));
		updateSave(getDefaultLocale().checkString("&aLocalization name not specified.", "", "Messages", "EmptyTranslateName"));
		updateSave(getDefaultLocale().checkString("&cThere is no such localization. The format is en-US, ru-RU, etc.", "", "Messages", "LocaleNotExist"));
		updateSave(getDefaultLocale().checkString("&cSpecify the required localization. The format is en-US, ru-RU, etc.", "", "Messages", "LocaleNotPresent"));
		updateSave(getDefaultLocale().checkString("&cEnter a display name for your shop. Color codes accepted.", "", "Messages", "TranslateNotPresent"));
		updateSave(getDefaultLocale().checkString("&aYou have successfully set the display name for your shop.", "", "Messages", "TranslateAdded"));
		updateSave(getDefaultLocale().checkString("&3List of shops", "", "Messages", "ShopListTitle"));
		updateSave(getDefaultLocale().checkString("&3=", "", "Messages", "ShopListPadding"));
		updateSave(getDefaultLocale().checkString("&3Command list", "", "Messages", "CommandsTitle"));
		updateSave(getDefaultLocale().checkString("&3=", "", "Messages", "CommandsPadding"));
		updateSave(getDefaultLocale().checkString("&cYou are already selling the maximum amount of items.", "", "Messages", "AuctionMaxVolume"));
		updateSave(getDefaultLocale().checkString("&aYou have successfully sold on auction &7[&r%item%&7]&ax%amount% за %removed%. Your balance %balance%. Seller &b%seller%&a.", "", "Messages", "AuctionBuy"));
		updateSave(getDefaultLocale().checkString("&aYou have successfully sold on auction &7[&r%item%&7]&ax%amount% за %added%. Your balance %balance%. Buyer &b%buyer%&a.", "", "Messages", "AuctionSell"));
		updateSave(getDefaultLocale().checkString("&aYour items have expired. Click on this message to get them back.", "", "Messages", "AuctionExpired"));
		updateSave(getDefaultLocale().checkString("&aCompleted transaction for redemption of items at auction. Click on this message to get them.", "", "Messages", "AuctionBetExpired"));
		updateSave(getDefaultLocale().checkString("&aWithholding tax: %amount%.", "", "Messages", "Tax"));
		updateSave(getDefaultLocale().checkString("&cThere are not enough empty slots in your inventory. Free at least %value%.", "", "Messages", "NoEmptySlots"));
		updateSave(getDefaultLocale().checkString("&cItem was purchased by someone else or removed from sale.", "", "Messages", "AuctionItemNotFound"));
		updateSave(getDefaultLocale().checkString("&eYou didn't specify a bet for your lot. To confirm the lot placement, click on this message.", "", "Messages", "AuctionBetNotPresent"));
		updateSave(getDefaultLocale().checkString("&eYou didn't specify a price for your lot. To confirm the lot placement, click on this message.", "", "Messages", "AuctionPriceNotPresent"));
		updateSave(getDefaultLocale().checkString("&eYou did not specify the currency for your lot. The default currency will be used. To confirm the lot placement, click on this message. \n&eAvailable currencies: &6%currencies%&e.", "", "Messages", "AuctionCurrencyNotPresent"));
		updateSave(getDefaultLocale().checkString("&cYou cannot sell an item without a price/bet or with a zero price/bet. One of the values must be greater than zero.", "", "Messages", "AuctionZeroOrNullPrices"));
		updateSave(getDefaultLocale().checkString("&cYou can't buy or assign a bet on your item.", "", "Messages", "AuctionCancelBuy"));
		updateSave(getDefaultLocale().checkString("&aYou putted the item up for sale.", "", "Messages", "AuctionItemAdded"));
		updateSave(getDefaultLocale().checkString("&eYou did not set your bet on the goods.", "", "Messages", "BetIsNotSet"));
		updateSave(getDefaultLocale().checkString("&cThis item cannot be put up for sale.", "", "Messages", "ItemBlocked"));
		updateSave(getDefaultLocale().checkString("&cThe item is now locked.", "", "Messages", "AddedItemBlocking "));
		updateSave(getDefaultLocale().checkString("&cBlocking mask added.", "", "Messages", "AddedMaskBlocking "));
		updateSave(getDefaultLocale().checkString("&cThe item has an NBT tag that is too long.", "", "Messages", "LongNBT"));
		updateSave(getDefaultLocale().checkString("&cItem added to the blacklist.", "", "Messages", "AddedToBlackList"));
		updateSave(getDefaultLocale().checkString("&cThe item is already blocked.", "", "Messages", "ItemIsAlreadyBlocked"));
		updateSave(getDefaultLocale().checkString("There is no economy plugin on the server. The functions of the plugin will not be available.", "", "Messages", "EconomyNotFound"));
		
		updateSave(getDefaultLocale().checkString("&eClick to open this shop in the editor.", "", "Hover", "OpenShopEdit"));
		updateSave(getDefaultLocale().checkString("&eClick to open this store.", "", "Hover", "OpenShop"));
		updateSave(getDefaultLocale().checkString("&eClick to execute this command.", "", "Hover", "RunCommand"));

		updateSave(getDefaultLocale().checkString("&2Auction", "", "Gui", "Auction"));
		updateSave(getDefaultLocale().checkString("&2Bet", "", "Gui", "AuctionBet"));
		updateSave(getDefaultLocale().checkString("&2Return items", "", "Gui", "AuctionReturn"));
		updateSave(getDefaultLocale().checkString("&2Setting of purchase item", "", "Gui", "EditBuyItem"));
		updateSave(getDefaultLocale().checkString("&2Setting of sell item", "", "Gui", "EditSellItem"));
		updateSave(getDefaultLocale().checkString("&2Setting of purchase commands", "", "Gui", "EditBuyCommandItem"));
		updateSave(getDefaultLocale().checkString("&2Setting an item", "", "Gui", "EditAuctionItem"));
		updateSave(getDefaultLocale().checkString("&2Buy", "", "Gui", "EditBuyTransaction"));
		updateSave(getDefaultLocale().checkString("&2Sell", "", "Gui", "EditSellTransaction"));
		
		updateSave(getDefaultLocale().checkString("&cFailed to remove money from balance of player &e%player%&c.", "", "Debug", "ErrorTakeMoney"));
		updateSave(getDefaultLocale().checkString("&cFailed to add money to the balance of player &e%player%&c.", "", "Debug", "ErrorGiveMoney"));
		updateSave(getDefaultLocale().checkString("&aItem &7[&r%item%]&ax%amount% removed from inventory of player &e%player%&a. Added money %added%. Balance %balance%.", "", "Debug", "InfoTakeItems"));
		updateSave(getDefaultLocale().checkString("&aItem &7[&r%item%]&ax%amount% added to inventory of player &e%player%&a. Removed money %removed%. Balance %balance%.", "", "Debug", "InfoGiveItems"));
		
		if(save) getDefaultLocale().saveLocaleNode();
		save = false;
	}

	private AbstractLocaleUtil getRuLocale() {
		return plugin.getLocales().getOrDefaultLocale(Locales.RU_RU);
	}
	private AbstractLocaleUtil getDefaultLocale() {
		return plugin.getLocales().getOrDefaultLocale(Locales.DEFAULT);
	}
	private void updateSave(boolean check) {
		if(check) save = true;
	}

}
