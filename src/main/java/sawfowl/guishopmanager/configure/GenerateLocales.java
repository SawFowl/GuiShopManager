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
		plugin.getLocaleAPI().createPluginLocale("guishopmanager", ConfigTypes.HOCON, Locales.DEFAULT);
		plugin.getLocaleAPI().createPluginLocale("guishopmanager", ConfigTypes.HOCON, Locales.RU_RU);
	}

	private void generateLocaleRu() {
		updateSave(getRuLocale().checkString("&eНазад", null, "FillItems", "Back"));
		updateSave(getRuLocale().checkString("&eДалее", null, "FillItems", "Next"));
		updateSave(getRuLocale().checkString("&eДобавить страницу", null, "FillItems", "AddPage"));
		updateSave(getRuLocale().checkString("&eКупить и выйти", null, "FillItems", "Buy"));
		updateSave(getRuLocale().checkString("&eПродать и выйти", null, "FillItems", "Sell"));
		updateSave(getRuLocale().checkString("&eСохранить и выйти", null, "FillItems", "Exit"));
		updateSave(getRuLocale().checkString("&eКупить и вернуться назад", null, "FillItems", "BuyAndBack"));
		updateSave(getRuLocale().checkString("&eПродать и вернуться назад", null, "FillItems", "SellAndBack"));
		updateSave(getRuLocale().checkString("&eОбъем: &a%value%", null, "FillItems", "Size"));
		updateSave(getRuLocale().checkString("&eЦена: &a%value%", null, "FillItems", "Price"));
		updateSave(getRuLocale().checkString("&eСброс", null, "FillItems", "Clear"));
		updateSave(getRuLocale().checkString("&eСменить валюту", null, "FillItems", "ChangeCurrency"));
		updateSave(getRuLocale().checkString("&eТекущая валюта", null, "FillItems", "CurrentCurrency"));
		updateSave(getRuLocale().checkString("&eПереключить режим", null, "FillItems", "SwitchMode"));
		updateSave(getRuLocale().checkString("&eВыставить предмет на продажу", null, "FillItems", "AuctionAddItem"));
		updateSave(getRuLocale().checkString("&eВернуть ваши предметы", null, "FillItems", "ReturnAuctionItem"));

		updateSave(getRuLocale().checkListStrings(Arrays.asList("&aЛевый клик увеличит цену", "&aПравый клик уменьшит цену"), null, "Lore", "ChangePrice"));
		updateSave(getRuLocale().checkListStrings(Arrays.asList("&aЛевый клик увеличит объем", "&aПравый клик уменьшит объем"), null, "Lore", "ChangeSize"));
		updateSave(getRuLocale().checkString("&eВарианты транзакций: ▼", null, "Lore", "TransactionVariants"));
		updateSave(getRuLocale().checkString("&eТекущая валюта: &a%currency%", null, "Lore", "CurrentCurrency"));
		updateSave(getRuLocale().checkString("&eВыбранный объем: &a%size%", null, "Lore", "CurrentSize"));
		updateSave(getRuLocale().checkString("&eИтого: &a%size%", null, "Lore", "CurrentSum"));
		updateSave(getRuLocale().checkString("&eВалюта: &a%currency%&e. Покупка: &a%buyprice%&e. Продажа: &a%sellprice%", null, "Lore", "Price"));
		updateSave(getRuLocale().checkString("&eВалюта: &a%currency%&e. Покупка: &a%buyprice%&e.", null, "Lore", "CommandPrice"));
		updateSave(getRuLocale().checkString("&eВалюта: &a%currency%&e. Цена за штуку: &a%price%&e. Итого: &a%total%", null, "Lore", "AuctionPrice"));
		updateSave(getRuLocale().checkString("&eВалюта: &a%currency%&e. Ставка штуку: &a%price%&e. Итого: &a%total%", null, "Lore", "AuctionBet"));
		updateSave(getRuLocale().checkString("&eВаша ставка: &a%size%. Итого: &a%total%", null, "Lore", "YourBet"));
		updateSave(getRuLocale().checkString("&eНалог на прибыль: &a%size%", null, "Lore", "Tax"));
		updateSave(getRuLocale().checkString("&eПошлина: &a%size%", null, "Lore", "Fee"));
		updateSave(getRuLocale().checkString("&eВключить/выключить бесплатное использование", null, "Lore", "SwitchFree"));
		updateSave(getRuLocale().checkString("&eДоступно бесплатно", null, "Lore", "AllowFree"));
		updateSave(getRuLocale().checkListStrings(Arrays.asList("&eЛевый клик переключит тип цены", "&eПравый клик переключит время и комиссии"), null, "Lore", "AuctionSwitchMode"));
		updateSave(getRuLocale().checkString("&eПродавец: &b%seller%&e.", null, "Lore", "Seller"));
		updateSave(getRuLocale().checkString("&eСнимется с продажи через: &a%expired%&e.", null, "Lore", "Expired"));
		updateSave(getRuLocale().checkString("&eТекущий покупатель по ставке: &b%buyer%&e.", null, "Lore", "CurrentBuyer"));
		updateSave(getRuLocale().checkString("&eТекущая ставка: &a%bet%&e.", null, "Lore", "CurrentBet"));
		updateSave(getRuLocale().checkString("&dЛКМ &f- &dназначить ставку", null, "Lore", "BetClick"));
		updateSave(getRuLocale().checkString("&dПКМ &f- &dкупить предмет", null, "Lore", "BuyClick"));
		
		updateSave(getRuLocale().checkString("&cУ вас недостаточно денег.", null, "Messages", "NoMoney"));
		updateSave(getRuLocale().checkString("&cУ вас недостаточно денег для выставления предмета на аукцион.", null, "Messages", "NoMoneyForFee"));
		updateSave(getRuLocale().checkString("&cУ вас недостаточно предметов.", null, "Messages", "NoItems"));
		updateSave(getRuLocale().checkString("&aВы продали &7[&r%item%&7]&ax%amount% за %added%. Ваш баланс %balance%.", null, "Messages", "ItemSell"));
		updateSave(getRuLocale().checkString("&aВы купили &7[&r%item%&7]&ax%amount% за %removed%. Ваш баланс %balance%.", null, "Messages", "ItemBuy"));
		updateSave(getRuLocale().checkString("&aВы купили выполнение комманд консолью за %removed%. Ваш баланс %balance%.", null, "Messages", "BuyCommands"));
		updateSave(getRuLocale().checkString("&cЭта команда может выполняться только игроком.", null, "Messages", "OnlyPlayer"));
		updateSave(getRuLocale().checkString("&cНе указан id магазина.", null, "Messages", "ShopIDNotPresent"));
		updateSave(getRuLocale().checkString("&cМагазин с таким id уже существует.", null, "Messages", "ShopIDAlreadyExists"));
		updateSave(getRuLocale().checkString("&cМагазина с таким id не существует.", null, "Messages", "ShopIDNotExists"));
		updateSave(getRuLocale().checkString("&cВведено не допустимое имя магазина.", null, "Messages", "InvalidShopID"));
		updateSave(getRuLocale().checkString("&cНе указан номер меню.", null, "Messages", "MenuNotPresent"));
		updateSave(getRuLocale().checkString("&cМеню с таким номером не существует, создайте его через GUI.", null, "Messages", "InvalidMenuId"));
		updateSave(getRuLocale().checkString("&cНе указан слот для размещения товара.", null, "Messages", "SlotNotPresent"));
		updateSave(getRuLocale().checkString("&cНомер слота должен быть в диапазоне от 0 до 44.", null, "Messages", "InvalidSlot"));
		updateSave(getRuLocale().checkString("&cВы не указали цену покупки.", null, "Messages", "BuyPriceNotPresent"));
		updateSave(getRuLocale().checkString("&cВы не указали цену продажи.", null, "Messages", "SellPriceNotPresent"));
		updateSave(getRuLocale().checkString("&cПожалуйста предъявите итем ┬──┬ ノ(゜-゜ノ)", null, "Messages", "ItemNotPresent"));
		updateSave(getRuLocale().checkString("&aПредмет &7[&r%item%&7]&a добавлен в магазин %shop%.", null, "Messages", "ShopItemAdded"));
		updateSave(getRuLocale().checkString("&cНужно быть игроком или указать ник игрока.", null, "Messages", "PlayerIsNotPresent"));
		updateSave(getRuLocale().checkString("&cВы не можете открывать меню другому игроку.", null, "Messages", "DontOpenOther"));
		updateSave(getRuLocale().checkString("&aМагазин удален.", null, "Messages", "SuccessDelete"));
		updateSave(getRuLocale().checkString("&eСписок магазинов сейчас пуст. Обратитесь к администрации.", null, "Messages", "ShopListEmpty"));
		updateSave(getRuLocale().checkString("&eСписок магазинов пуст. Создайте хотя бы 1 магазин.", null, "Messages", "ShopListEmptyEditor"));
		updateSave(getRuLocale().checkString("&aДебаг включен.", null, "Messages", "DebugOn"));
		updateSave(getRuLocale().checkString("&aДебаг выключен.", null, "Messages", "DebugOff"));
		updateSave(getRuLocale().checkString("&aПлагин перезагружен.", null, "Messages", "Reload"));
		updateSave(getRuLocale().checkString("&aНе указано имя локализации.", null, "Messages", "EmptyTranslateName"));
		updateSave(getRuLocale().checkString("&cТакой локализации не существует. Формат en-US, ru-RU и так далее.", null, "Messages", "LocaleNotExist"));
		updateSave(getRuLocale().checkString("&cУкажите требуемую локализацию. Формат en-US, ru-RU и так далее.", null, "Messages", "LocaleNotPresent"));
		updateSave(getRuLocale().checkString("&cВведите отображаемое имя для магазина. Принимаются цветовые коды.", null, "Messages", "TranslateNotPresent"));
		updateSave(getRuLocale().checkString("&aВы успешно установили отображаемое имя для магазина.", null, "Messages", "TranslateAdded"));
		updateSave(getRuLocale().checkString("&3Список магазинов", null, "Messages", "ShopListTitle"));
		updateSave(getRuLocale().checkString("&3=", null, "Messages", "ShopListPadding"));
		updateSave(getRuLocale().checkString("&3Список команд", null, "Messages", "CommandsTitle"));
		updateSave(getRuLocale().checkString("&3=", null, "Messages", "CommandsPadding"));
		updateSave(getRuLocale().checkString("&cВы выставили максимальный объем предметов на продажу.", null, "Messages", "AuctionMaxVolume"));
		updateSave(getRuLocale().checkString("&aВы купили на аукционе &7[&r%item%&7]&ax%amount% за %removed%. Ваш баланс %balance%. Продавец &b%seller%&a.", null, "Messages", "AuctionBuy"));
		updateSave(getRuLocale().checkString("&aВы продали на аукционе &7[&r%item%&7]&ax%amount% за %added%. Ваш баланс %balance%. Покупатель &b%buyer%&a.", null, "Messages", "AuctionSell"));
		updateSave(getRuLocale().checkString("&aСрок выставления ваших предметов на продажу истек. Кликните на это сообщение, чтобы получить их обратно.", null, "Messages", "AuctionExpired"));
		updateSave(getRuLocale().checkString("&aЗавершена сделка по выкупу предметов на аукционе. Кликните на это сообщение, чтобы получить их.", null, "Messages", "AuctionBetExpired"));
		updateSave(getRuLocale().checkString("&aУдержанный налог: %amount%.", null, "Messages", "Tax"));
		updateSave(getRuLocale().checkString("&cВ вашем инвентаре недостаточно пустых слотов. Освободите хотя бы %value%.", null, "Messages", "NoEmptySlots"));
		updateSave(getRuLocale().checkString("&cПредмет был куплен кем-то другим или снят с продажи.", null, "Messages", "AuctionItemNotFound"));
		updateSave(getRuLocale().checkString("&eВы не указали ставку для вашего лота. Для подтверждения выставления лота кликните на это сообщение.", null, "Messages", "AuctionBetNotPresent"));
		updateSave(getRuLocale().checkString("&eВы не указали цену для вашего лота. Для подтверждения выставления лота кликните на это сообщение.", null, "Messages", "AuctionPriceNotPresent"));
		updateSave(getRuLocale().checkString("&eВы не указали валюту для вашего лота. Будет использована валюта по умолчанию. Для подтверждения выставления лота кликните на это сообщение. \n&eДоступные валюты: &6%currencies%&e.", null, "Messages", "AuctionCurrencyNotPresent"));
		updateSave(getRuLocale().checkString("&cНельзя продать предмет без цены/ставки или с нулевой ценой/ставкой. Одно из значений должно быть больше нуля.", null, "Messages", "AuctionZeroOrNullPrices"));
		updateSave(getRuLocale().checkString("&cНельзя купить или назначить ставку на свой предмет.", null, "Messages", "AuctionCancelBuy"));
		updateSave(getRuLocale().checkString("&aВы выставили предмет на продажу.", null, "Messages", "AuctionItemAdded"));
		updateSave(getRuLocale().checkString("&eВы не установили свою ставку на товар.", null, "Messages", "BetIsNotSet"));
		updateSave(getRuLocale().checkString("&cЭтот предмет нельзя выставить на продажу.", null, "Messages", "ItemBlocked"));
		updateSave(getRuLocale().checkString("&aПредмет заблокирован.", null, "Messages", "AddedItemBlocking"));
		updateSave(getRuLocale().checkString("&aДобавленна маска блокировки предметов.", null, "Messages", "AddedMaskBlocking"));
		updateSave(getRuLocale().checkString("&cУ предмета слишком длинный NBT тег.", null, "Messages", "LongNBT"));
		updateSave(getRuLocale().checkString("&cПредмет добавлен в черный список.", null, "Messages", "AddedToBlackList"));
		updateSave(getRuLocale().checkString("&cПредмет уже заблокирован.", null, "Messages", "ItemIsAlreadyBlocked"));
		updateSave(getRuLocale().checkString("&cНа сервере нет плагина экономики. Функционал плагина будет не доступен.", null, "Messages", "EconomyNotFound"));
		
		updateSave(getRuLocale().checkString("&eКлик для открытия этого магазина в редакторе.", null, "Hover", "OpenShopEdit"));
		updateSave(getRuLocale().checkString("&eКлик для открытия этого магазина.", null, "Hover", "OpenShop"));
		updateSave(getRuLocale().checkString("&eКлик для выполнения команды.", null, "Hover", "RunCommand"));

		updateSave(getRuLocale().checkString("&2Аукцион", null, "Gui", "Auction"));
		updateSave(getRuLocale().checkString("&2Ставка", null, "Gui", "AuctionBet"));
		updateSave(getRuLocale().checkString("&2Возврат предметов", null, "Gui", "AuctionReturn"));
		updateSave(getRuLocale().checkString("&2Настройка покупки товара", null, "Gui", "EditBuyItem"));
		updateSave(getRuLocale().checkString("&2Настройка продажи товара", null, "Gui", "EditSellItem"));
		updateSave(getRuLocale().checkString("&2Настройка покупки комманд", null, "Gui", "EditBuyCommandItem"));
		updateSave(getRuLocale().checkString("&2Настройка товара", null, "Gui", "EditAuctionItem"));
		updateSave(getRuLocale().checkString("&2Покупка", null, "Gui", "EditBuyTransaction"));
		updateSave(getRuLocale().checkString("&2Продажа", null, "Gui", "EditSellTransaction"));
		
		updateSave(getRuLocale().checkString("&cНе удалось списать деньги с баланса игрока &e%player%&c.", null, "Debug", "ErrorTakeMoney"));
		updateSave(getRuLocale().checkString("&cНе удалось начислить деньги на баланс игрока &e%player%&c.", null, "Debug", "ErrorGiveMoney"));
		updateSave(getRuLocale().checkString("&aПредмет &7[&r%item%]&ax%amount% удален из инвентаря игрока &e%player%&a. Начисленно денег %added%. Баланс %balance%.", null, "Debug", "InfoTakeItems"));
		updateSave(getRuLocale().checkString("&aПредмет &7[&r%item%]&ax%amount% добавлен в инвентарь игрока &e%player%&a. Списано денег %removed%. Баланс %balance%.", null, "Debug", "InfoGiveItems"));
		
		if(save) getRuLocale().saveLocaleNode();
		save = false;
	}

	private void generateDefaultLocale() {
		updateSave(getDefaultLocale().checkString("&eBack", null, "FillItems", "Back"));
		updateSave(getDefaultLocale().checkString("&eNext", null, "FillItems", "Next"));
		updateSave(getDefaultLocale().checkString("&eAdd page", null, "FillItems", "AddPage"));
		updateSave(getDefaultLocale().checkString("&eBuy and exit", null, "FillItems", "Buy"));
		updateSave(getDefaultLocale().checkString("&eSell and exit", null, "FillItems", "Sell"));
		updateSave(getDefaultLocale().checkString("&eSave and exit", null, "FillItems", "Exit"));
		updateSave(getDefaultLocale().checkString("&eBuy and go back", null, "FillItems", "BuyAndBack"));
		updateSave(getDefaultLocale().checkString("&eSell and go back", null, "FillItems", "SellAndBack"));
		updateSave(getDefaultLocale().checkString("&eSize: &a%value%", null, "FillItems", "Size"));
		updateSave(getDefaultLocale().checkString("&ePrice: &a%value%", null, "FillItems", "Price"));
		updateSave(getDefaultLocale().checkString("&eClear", null, "FillItems", "Clear"));
		updateSave(getDefaultLocale().checkString("&eChange currency", null, "FillItems", "ChangeCurrency"));
		updateSave(getDefaultLocale().checkString("&eSwitch mode", null, "FillItems", "SwitchMode"));
		updateSave(getDefaultLocale().checkString("&eSale your item", null, "FillItems", "AuctionAddItem"));
		updateSave(getDefaultLocale().checkString("&eReturn your items", null, "FillItems", "ReturnAuctionItem"));

		updateSave(getDefaultLocale().checkListStrings(Arrays.asList("&aLeft click will increase the price ", "&aRight click will decrease the price "), null, "Lore", "ChangePrice"));
		updateSave(getDefaultLocale().checkListStrings(Arrays.asList("&aLeft click will increase the size", "&aRight click will decrease the size"), null, "Lore", "ChangeSize"));
		updateSave(getDefaultLocale().checkString("&eTransaction variants: ▼", null, "Lore", "TransactionVariants"));
		updateSave(getDefaultLocale().checkString("&eCurrent currency: &a%currency%", null, "Lore", "CurrentCurrency"));
		updateSave(getDefaultLocale().checkString("&eSelected size: &a%size%", null, "Lore", "CurrentSize"));
		updateSave(getDefaultLocale().checkString("&eTotal: &a%size%", null, "Lore", "CurrentSum"));
		updateSave(getDefaultLocale().checkString("&eCurrency: &a%currency%&e. Buy: &a%buyprice%&e. Sell: &a%sellprice%", null, "Lore", "Price"));
		updateSave(getDefaultLocale().checkString("&eCurrency: &a%currency%&e. Buy: &a%buyprice%&e.", null, "Lore", "CommandPrice"));
		updateSave(getDefaultLocale().checkString("&eCurrency: &a%currency%&e. Price for one: &a%price%&e. Total: &a%total%", null, "Lore", "AuctionPrice"));
		updateSave(getDefaultLocale().checkString("&eCurrency: &a%currency%&e. Bet for one: &a%price%&e. Total: &a%total%", null, "Lore", "AuctionBet"));
		updateSave(getDefaultLocale().checkString("&eYour bet: &a%size%. Total: &a%total%", null, "Lore", "YourBet"));
		updateSave(getDefaultLocale().checkString("&eTax: &a%size%", null, "Lore", "Tax"));
		updateSave(getDefaultLocale().checkString("&eFee: &a%size%", null, "Lore", "Fee"));
		updateSave(getDefaultLocale().checkString("&eEnable/disable free use", null, "Lore", "SwitchFree"));
		updateSave(getDefaultLocale().checkString("&eAvailable for free", null, "Lore", "AllowFree"));
		updateSave(getDefaultLocale().checkListStrings(Arrays.asList("&eLeft click to switch the price type", "&eRight click to switch time and commissions"), null, "Lore", "AuctionSwitchMode"));
		updateSave(getDefaultLocale().checkString("&eSeller: &b%seller%&e.", null, "Lore", "Seller"));
		updateSave(getDefaultLocale().checkString("&eExpired: &a%expired%&e.", null, "Lore", "Expired"));
		updateSave(getDefaultLocale().checkString("&eCurrent buyer at the bet: &b%buyer%&e.", null, "Lore", "CurrentBuyer"));
		updateSave(getDefaultLocale().checkString("&eCurrent bet: &a%bet%&e.", null, "Lore", "CurrentBet"));
		updateSave(getDefaultLocale().checkString("&dLeft click &f- &dplace your bet", null, "Lore", "BetClick"));
		updateSave(getDefaultLocale().checkString("&dRight click &f- &dbuy an item", null, "Lore", "BuyClick"));
		
		updateSave(getDefaultLocale().checkString("&cYou don't have enough money.", null, "Messages", "NoMoney"));
		updateSave(getDefaultLocale().checkString("&cYou do not have enough money to list the item for sale at the auction.", null, "Messages", "NoMoneyForFee"));
		updateSave(getDefaultLocale().checkString("&cYou don't have enough items.", null, "Messages", "NoItems"));
		updateSave(getDefaultLocale().checkString("&aYou have successfully sold &7[&r%item%&7]&ax%amount% for %added%. Your balance %balance%.", null, "Messages", "ItemSell"));
		updateSave(getDefaultLocale().checkString("&aYou have successfully purchased &7[&r%item%&7]&ax%amount% for %removed%. Your balance %balance%.", null, "Messages", "ItemBuy"));
		updateSave(getDefaultLocale().checkString("&aYou paid %removed% to execute console commands. Your balance %balance%.", null, "Messages", "BuyCommands"));
		updateSave(getDefaultLocale().checkString("&cThis command can only be executed by the player.", null, "Messages", "OnlyPlayer"));
		updateSave(getDefaultLocale().checkString("&cShop id not specified.", null, "Messages", "ShopIDNotPresent"));
		updateSave(getDefaultLocale().checkString("&cA shop with this id already exists.", null, "Messages", "ShopIDAlreadyExists"));
		updateSave(getDefaultLocale().checkString("&cThere is no shop with this id.", null, "Messages", "ShopIDNotExists"));
		updateSave(getDefaultLocale().checkString("&cВведено не допустимое имя магазина.", null, "Messages", "InvalidShopID"));
		updateSave(getDefaultLocale().checkString("&cMenu number not specified.", null, "Messages", "MenuNotPresent"));
		updateSave(getDefaultLocale().checkString("&cMenu with this number does not exist, create it via GUI.", null, "Messages", "InvalidMenuId"));
		updateSave(getDefaultLocale().checkString("&cThe slot for placing the item is not specified.", null, "Messages", "SlotNotPresent"));
		updateSave(getDefaultLocale().checkString("&cThe slot number must be between 0 and 44.", null, "Messages", "InvalidSlot"));
		updateSave(getDefaultLocale().checkString("&cSpecify the purchase price.", null, "Messages", "BuyPriceNotPresent"));
		updateSave(getDefaultLocale().checkString("&cSpecify the sale price.", null, "Messages", "SellPriceNotPresent"));
		updateSave(getDefaultLocale().checkString("&cPlease present the item ┬──┬ ノ(゜-゜ノ)", null, "Messages", "ItemNotPresent"));
		updateSave(getDefaultLocale().checkString("&cItem &7[&r%item%&7]&a added to shop %shop%.", null, "Messages", "ShopItemAdded"));
		updateSave(getDefaultLocale().checkString("&cThis item cannot be sold.", null, "Messages", "InvalidItem"));
		updateSave(getDefaultLocale().checkString("&cYou need to be a player or specify the player's nickname.", null, "Messages", "PlayerIsNotPresent"));
		updateSave(getDefaultLocale().checkString("&cYou can not open the menu to another player.", null, "Messages", "DontOpenOther"));
		updateSave(getDefaultLocale().checkString("&aThe shop was deleted.", null, "Messages", "SuccessDelete"));
		updateSave(getDefaultLocale().checkString("&eThe shop list is now empty. Contact the administration.", null, "Messages", "ShopListEmpty"));
		updateSave(getDefaultLocale().checkString("&eThe shop list is empty. Create at least 1 store. ", null, "Messages", "ShopListEmptyEditor"));
		updateSave(getDefaultLocale().checkString("&aDebug on.", null, "Messages", "DebugOn"));
		updateSave(getDefaultLocale().checkString("&aDebug off.", null, "Messages", "DebugOff"));
		updateSave(getDefaultLocale().checkString("&aThe plugin has been reloaded.", null, "Messages", "Reload"));
		updateSave(getDefaultLocale().checkString("&aLocalization name not specified.", null, "Messages", "EmptyTranslateName"));
		updateSave(getDefaultLocale().checkString("&cThere is no such localization. The format is en-US, ru-RU, etc.", null, "Messages", "LocaleNotExist"));
		updateSave(getDefaultLocale().checkString("&cSpecify the required localization. The format is en-US, ru-RU, etc.", null, "Messages", "LocaleNotPresent"));
		updateSave(getDefaultLocale().checkString("&cEnter a display name for your shop. Color codes accepted.", null, "Messages", "TranslateNotPresent"));
		updateSave(getDefaultLocale().checkString("&aYou have successfully set the display name for your shop.", null, "Messages", "TranslateAdded"));
		updateSave(getDefaultLocale().checkString("&3List of shops", null, "Messages", "ShopListTitle"));
		updateSave(getDefaultLocale().checkString("&3=", null, "Messages", "ShopListPadding"));
		updateSave(getDefaultLocale().checkString("&3Command list", null, "Messages", "CommandsTitle"));
		updateSave(getDefaultLocale().checkString("&3=", null, "Messages", "CommandsPadding"));
		updateSave(getDefaultLocale().checkString("&cYou are already selling the maximum amount of items.", null, "Messages", "AuctionMaxVolume"));
		updateSave(getDefaultLocale().checkString("&aYou have successfully sold on auction &7[&r%item%&7]&ax%amount% за %removed%. Your balance %balance%. Seller &b%seller%&a.", null, "Messages", "AuctionBuy"));
		updateSave(getDefaultLocale().checkString("&aYou have successfully sold on auction &7[&r%item%&7]&ax%amount% за %added%. Your balance %balance%. Buyer &b%buyer%&a.", null, "Messages", "AuctionSell"));
		updateSave(getDefaultLocale().checkString("&aYour items have expired. Click on this message to get them back.", null, "Messages", "AuctionExpired"));
		updateSave(getDefaultLocale().checkString("&aCompleted transaction for redemption of items at auction. Click on this message to get them.", null, "Messages", "AuctionBetExpired"));
		updateSave(getDefaultLocale().checkString("&aWithholding tax: %amount%.", null, "Messages", "Tax"));
		updateSave(getDefaultLocale().checkString("&cThere are not enough empty slots in your inventory. Free at least %value%.", null, "Messages", "NoEmptySlots"));
		updateSave(getDefaultLocale().checkString("&cItem was purchased by someone else or removed from sale.", null, "Messages", "AuctionItemNotFound"));
		updateSave(getDefaultLocale().checkString("&eYou didn't specify a bet for your lot. To confirm the lot placement, click on this message.", null, "Messages", "AuctionBetNotPresent"));
		updateSave(getDefaultLocale().checkString("&eYou didn't specify a price for your lot. To confirm the lot placement, click on this message.", null, "Messages", "AuctionPriceNotPresent"));
		updateSave(getDefaultLocale().checkString("&eYou did not specify the currency for your lot. The default currency will be used. To confirm the lot placement, click on this message. \n&eAvailable currencies: &6%currencies%&e.", null, "Messages", "AuctionCurrencyNotPresent"));
		updateSave(getDefaultLocale().checkString("&cYou cannot sell an item without a price/bet or with a zero price/bet. One of the values must be greater than zero.", null, "Messages", "AuctionZeroOrNullPrices"));
		updateSave(getDefaultLocale().checkString("&cYou can't buy or assign a bet on your item.", null, "Messages", "AuctionCancelBuy"));
		updateSave(getDefaultLocale().checkString("&aYou putted the item up for sale.", null, "Messages", "AuctionItemAdded"));
		updateSave(getDefaultLocale().checkString("&eYou did not set your bet on the goods.", null, "Messages", "BetIsNotSet"));
		updateSave(getDefaultLocale().checkString("&cThis item cannot be put up for sale.", null, "Messages", "ItemBlocked"));
		updateSave(getDefaultLocale().checkString("&cThe item is now locked.", null, "Messages", "AddedItemBlocking "));
		updateSave(getDefaultLocale().checkString("&cBlocking mask added.", null, "Messages", "AddedMaskBlocking "));
		updateSave(getDefaultLocale().checkString("&cThe item has an NBT tag that is too long.", null, "Messages", "LongNBT"));
		updateSave(getDefaultLocale().checkString("&cItem added to the blacklist.", null, "Messages", "AddedToBlackList"));
		updateSave(getDefaultLocale().checkString("&cThe item is already blocked.", null, "Messages", "ItemIsAlreadyBlocked"));
		updateSave(getDefaultLocale().checkString("There is no economy plugin on the server. The functions of the plugin will not be available.", null, "Messages", "EconomyNotFound"));
		
		updateSave(getDefaultLocale().checkString("&eClick to open this shop in the editor.", null, "Hover", "OpenShopEdit"));
		updateSave(getDefaultLocale().checkString("&eClick to open this store.", null, "Hover", "OpenShop"));
		updateSave(getDefaultLocale().checkString("&eClick to execute this command.", null, "Hover", "RunCommand"));

		updateSave(getDefaultLocale().checkString("&2Auction", null, "Gui", "Auction"));
		updateSave(getDefaultLocale().checkString("&2Bet", null, "Gui", "AuctionBet"));
		updateSave(getDefaultLocale().checkString("&2Return items", null, "Gui", "AuctionReturn"));
		updateSave(getDefaultLocale().checkString("&2Setting of purchase item", null, "Gui", "EditBuyItem"));
		updateSave(getDefaultLocale().checkString("&2Setting of sell item", null, "Gui", "EditSellItem"));
		updateSave(getDefaultLocale().checkString("&2Setting of purchase commands", null, "Gui", "EditBuyCommandItem"));
		updateSave(getDefaultLocale().checkString("&2Setting an item", null, "Gui", "EditAuctionItem"));
		updateSave(getDefaultLocale().checkString("&2Buy", null, "Gui", "EditBuyTransaction"));
		updateSave(getDefaultLocale().checkString("&2Sell", null, "Gui", "EditSellTransaction"));
		
		updateSave(getDefaultLocale().checkString("&cFailed to remove money from balance of player &e%player%&c.", null, "Debug", "ErrorTakeMoney"));
		updateSave(getDefaultLocale().checkString("&cFailed to add money to the balance of player &e%player%&c.", null, "Debug", "ErrorGiveMoney"));
		updateSave(getDefaultLocale().checkString("&aItem &7[&r%item%]&ax%amount% removed from inventory of player &e%player%&a. Added money %added%. Balance %balance%.", null, "Debug", "InfoTakeItems"));
		updateSave(getDefaultLocale().checkString("&aItem &7[&r%item%]&ax%amount% added to inventory of player &e%player%&a. Removed money %removed%. Balance %balance%.", null, "Debug", "InfoGiveItems"));
		
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
