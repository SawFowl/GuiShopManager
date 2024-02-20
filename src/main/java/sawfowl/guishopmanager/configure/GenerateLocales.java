package sawfowl.guishopmanager.configure;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.util.locale.Locales;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.localeapi.api.ConfigTypes;
import sawfowl.localeapi.api.PluginLocale;
import sawfowl.localeapi.api.TextUtils;

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
		check(getRuLocale(), "&eНазад", null, "FillItems", "Back");
		check(getRuLocale(), "&eДалее", null, "FillItems", "Next");
		check(getRuLocale(), "&eДобавить страницу", null, "FillItems", "AddPage");
		check(getRuLocale(), "&eКупить и выйти", null, "FillItems", "Buy");
		check(getRuLocale(), "&eПродать и выйти", null, "FillItems", "Sell");
		check(getRuLocale(), "&eСохранить и выйти", null, "FillItems", "Exit");
		check(getRuLocale(), "&eКупить и вернуться назад", null, "FillItems", "BuyAndBack");
		check(getRuLocale(), "&eПродать и вернуться назад", null, "FillItems", "SellAndBack");
		check(getRuLocale(), "&eОбъем: &a%value%", null, "FillItems", "Size");
		check(getRuLocale(), "&eЦена: &a%value%", null, "FillItems", "Price");
		check(getRuLocale(), "&eСброс", null, "FillItems", "Clear");
		check(getRuLocale(), "&eСменить валюту", null, "FillItems", "ChangeCurrency");
		check(getRuLocale(), "&eТекущая валюта", null, "FillItems", "CurrentCurrency");
		check(getRuLocale(), "&eПереключить режим", null, "FillItems", "SwitchMode");
		check(getRuLocale(), "&eВыставить предмет на продажу", null, "FillItems", "AuctionAddItem");
		check(getRuLocale(), "&eВернуть ваши предметы", null, "FillItems", "ReturnAuctionItem");

		checkList(getRuLocale(), Arrays.asList("&aЛевый клик увеличит цену", "&aПравый клик уменьшит цену"), null, "Lore", "ChangePrice");
		checkList(getRuLocale(), Arrays.asList("&aЛевый клик увеличит объем", "&aПравый клик уменьшит объем"), null, "Lore", "ChangeSize");
		check(getRuLocale(), "&eВарианты транзакций: ▼", null, "Lore", "TransactionVariants");
		check(getRuLocale(), "&eТекущая валюта: &a%currency%", null, "Lore", "CurrentCurrency");
		check(getRuLocale(), "&eВыбранный объем: &a%size%", null, "Lore", "CurrentSize");
		check(getRuLocale(), "&eИтого: &a%size%", null, "Lore", "CurrentSum");
		check(getRuLocale(), "&eВалюта: &a%currency%&e. Покупка: &a%buyprice%&e. Продажа: &a%sellprice%", null, "Lore", "Price");
		check(getRuLocale(), "&eВалюта: &a%currency%&e. Покупка: &a%buyprice%&e.", null, "Lore", "CommandPrice");
		check(getRuLocale(), "&eВалюта: &a%currency%&e. Цена за штуку: &a%price%&e. Итого: &a%total%", null, "Lore", "AuctionPrice");
		check(getRuLocale(), "&eВалюта: &a%currency%&e. Ставка штуку: &a%price%&e. Итого: &a%total%", null, "Lore", "AuctionBet");
		check(getRuLocale(), "&eВаша ставка: &a%size%. Итого: &a%total%", null, "Lore", "YourBet");
		check(getRuLocale(), "&eНалог на прибыль: &a%size%", null, "Lore", "Tax");
		check(getRuLocale(), "&eПошлина: &a%size%", null, "Lore", "Fee");
		check(getRuLocale(), "&eВключить/выключить бесплатное использование", null, "Lore", "SwitchFree");
		check(getRuLocale(), "&eДоступно бесплатно", null, "Lore", "AllowFree");
		checkList(getRuLocale(), Arrays.asList("&eЛевый клик переключит тип цены", "&eПравый клик переключит время и комиссии"), null, "Lore", "AuctionSwitchMode");
		check(getRuLocale(), "&eПродавец: &b%seller%&e.", null, "Lore", "Seller");
		check(getRuLocale(), "&eСнимется с продажи через: &a%expired%&e.", null, "Lore", "Expired");
		check(getRuLocale(), "&eТекущий покупатель по ставке: &b%buyer%&e.", null, "Lore", "CurrentBuyer");
		check(getRuLocale(), "&eТекущая ставка: &a%bet%&e.", null, "Lore", "CurrentBet");
		check(getRuLocale(), "&dЛКМ &f- &dназначить ставку", null, "Lore", "BetClick");
		check(getRuLocale(), "&dПКМ &f- &dкупить предмет", null, "Lore", "BuyClick");
		
		check(getRuLocale(), "&cУ вас недостаточно денег.", null, "Messages", "NoMoney");
		check(getRuLocale(), "&cУ вас недостаточно денег для выставления предмета на аукцион.", null, "Messages", "NoMoneyForFee");
		check(getRuLocale(), "&cУ вас недостаточно предметов.", null, "Messages", "NoItems");
		check(getRuLocale(), "&aВы продали &7[&r%item%&7]&ax%amount% за %added%. Ваш баланс %balance%.", null, "Messages", "ItemSell");
		check(getRuLocale(), "&aВы купили &7[&r%item%&7]&ax%amount% за %removed%. Ваш баланс %balance%.", null, "Messages", "ItemBuy");
		check(getRuLocale(), "&aВы купили выполнение комманд консолью за %removed%. Ваш баланс %balance%.", null, "Messages", "BuyCommands");
		check(getRuLocale(), "&cЭта команда может выполняться только игроком.", null, "Messages", "OnlyPlayer");
		check(getRuLocale(), "&cНе указан id магазина.", null, "Messages", "ShopIDNotPresent");
		check(getRuLocale(), "&cМагазин с таким id уже существует.", null, "Messages", "ShopIDAlreadyExists");
		check(getRuLocale(), "&cМагазина с таким id не существует.", null, "Messages", "ShopIDNotExists");
		check(getRuLocale(), "&cВведено не допустимое имя магазина.", null, "Messages", "InvalidShopID");
		check(getRuLocale(), "&cНе указан номер меню.", null, "Messages", "MenuNotPresent");
		check(getRuLocale(), "&cМеню с таким номером не существует, создайте его через GUI.", null, "Messages", "InvalidMenuId");
		check(getRuLocale(), "&cНе указан слот для размещения товара.", null, "Messages", "SlotNotPresent");
		check(getRuLocale(), "&cНомер слота должен быть в диапазоне от 0 до 44.", null, "Messages", "InvalidSlot");
		check(getRuLocale(), "&cВы не указали цену покупки.", null, "Messages", "BuyPriceNotPresent");
		check(getRuLocale(), "&cВы не указали цену продажи.", null, "Messages", "SellPriceNotPresent");
		check(getRuLocale(), "&cПожалуйста предъявите итем ┬──┬ ノ(゜-゜ノ)",  null, "Messages", "ItemNotPresent");
		check(getRuLocale(), "&aВы добавили команду к предмету.", null, "Messages", "CommandAdded");
		check(getRuLocale(), "&aПредмет &7[&r%item%&7]&a добавлен в магазин %shop%.", null, "Messages", "ShopItemAdded");
		check(getRuLocale(), "&cНужно быть игроком или указать ник игрока.", null, "Messages", "PlayerIsNotPresent");
		check(getRuLocale(), "&cВы не можете открывать меню другому игроку.", null, "Messages", "DontOpenOther");
		check(getRuLocale(), "&aМагазин удален.", null, "Messages", "SuccessDelete");
		check(getRuLocale(), "&eСписок магазинов сейчас пуст. Обратитесь к администрации.", null, "Messages", "ShopListEmpty");
		check(getRuLocale(), "&eСписок магазинов пуст. Создайте хотя бы 1 магазин.", null, "Messages", "ShopListEmptyEditor");
		check(getRuLocale(), "&aДебаг включен.", null, "Messages", "DebugOn");
		check(getRuLocale(), "&aДебаг выключен.", null, "Messages", "DebugOff");
		check(getRuLocale(), "&aПлагин перезагружен.", null, "Messages", "Reload");
		check(getRuLocale(), "&aНе указано имя локализации.", null, "Messages", "EmptyTranslateName");
		check(getRuLocale(), "&cТакой локализации не существует. Формат en-US, ru-RU и так далее.", null, "Messages", "LocaleNotExist");
		check(getRuLocale(), "&cУкажите требуемую локализацию. Формат en-US, ru-RU и так далее.", null, "Messages", "LocaleNotPresent");
		check(getRuLocale(), "&cВведите отображаемое имя для магазина. Принимаются цветовые коды.", null, "Messages", "TranslateNotPresent");
		check(getRuLocale(), "&aВы успешно установили отображаемое имя для магазина.", null, "Messages", "TranslateAdded");
		check(getRuLocale(), "&3Список магазинов", null, "Messages", "ShopListTitle");
		check(getRuLocale(), "&3=", null, "Messages", "ShopListPadding");
		check(getRuLocale(), "&3Список команд", null, "Messages", "CommandsTitle");
		check(getRuLocale(), "&3=", null, "Messages", "CommandsPadding");
		check(getRuLocale(), "&cВы выставили максимальный объем предметов на продажу.", null, "Messages", "AuctionMaxVolume");
		check(getRuLocale(), "&aВы купили на аукционе &7[&r%item%&7]&ax%amount% за %removed%. Ваш баланс %balance%. Продавец &b%seller%&a.", null, "Messages", "AuctionBuy");
		check(getRuLocale(), "&aВы продали на аукционе &7[&r%item%&7]&ax%amount% за %added%. Ваш баланс %balance%. Покупатель &b%buyer%&a.", null, "Messages", "AuctionSell");
		check(getRuLocale(), "&aСрок выставления ваших предметов на продажу истек. Кликните на это сообщение, чтобы получить их обратно.", null, "Messages", "AuctionExpired");
		check(getRuLocale(), "&aЗавершена сделка по выкупу предметов на аукционе. Кликните на это сообщение, чтобы получить их.", null, "Messages", "AuctionBetExpired");
		check(getRuLocale(), "&aУдержанный налог: %amount%.", null, "Messages", "Tax");
		check(getRuLocale(), "&cВ вашем инвентаре недостаточно пустых слотов. Освободите хотя бы %value%.", null, "Messages", "NoEmptySlots");
		check(getRuLocale(), "&cПредмет был куплен кем-то другим или снят с продажи.", null, "Messages", "AuctionItemNotFound");
		check(getRuLocale(), "&eВы не указали ставку для вашего лота. Для подтверждения выставления лота кликните на это сообщение.", null, "Messages", "AuctionBetNotPresent");
		check(getRuLocale(), "&eВы не указали цену для вашего лота. Для подтверждения выставления лота кликните на это сообщение.", null, "Messages", "AuctionPriceNotPresent");
		check(getRuLocale(), "&eВы не указали валюту для вашего лота. Будет использована валюта по умолчанию. Для подтверждения выставления лота кликните на это сообщение. \n&eДоступные валюты: &6%currencies%&e.", null, "Messages", "AuctionCurrencyNotPresent");
		check(getRuLocale(), "&cНельзя продать предмет без цены/ставки или с нулевой ценой/ставкой. Одно из значений должно быть больше нуля.", null, "Messages", "AuctionZeroOrNullPrices");
		check(getRuLocale(), "&cНельзя купить или назначить ставку на свой предмет.", null, "Messages", "AuctionCancelBuy");
		check(getRuLocale(), "&aВы выставили предмет на продажу.", null, "Messages", "AuctionItemAdded");
		check(getRuLocale(), "&eВы не установили свою ставку на товар.", null, "Messages", "BetIsNotSet");
		check(getRuLocale(), "&cЭтот предмет нельзя выставить на продажу.", null, "Messages", "ItemBlocked");
		check(getRuLocale(), "&aПредмет заблокирован.", null, "Messages", "AddedItemBlocking");
		check(getRuLocale(), "&aДобавленна маска блокировки предметов.", null, "Messages", "AddedMaskBlocking");
		check(getRuLocale(), "&cУ предмета слишком длинный NBT тег.", null, "Messages", "LongNBT");
		check(getRuLocale(), "&cПредмет добавлен в черный список.", null, "Messages", "AddedToBlackList");
		check(getRuLocale(), "&cПредмет уже заблокирован.", null, "Messages", "ItemIsAlreadyBlocked");
		check(getRuLocale(), "&cНа сервере нет плагина экономики. Функционал плагина будет не доступен.", null, "Messages", "EconomyNotFound");
		
		check(getRuLocale(), "&eКлик для открытия этого магазина в редакторе.", null, "Hover", "OpenShopEdit");
		check(getRuLocale(), "&eКлик для открытия этого магазина.", null, "Hover", "OpenShop");
		check(getRuLocale(), "&eКлик для выполнения команды.", null, "Hover", "RunCommand");

		check(getRuLocale(), "&2Аукцион", null, "Gui", "Auction");
		check(getRuLocale(), "&2Ставка", null, "Gui", "AuctionBet");
		check(getRuLocale(), "&2Возврат предметов", null, "Gui", "AuctionReturn");
		check(getRuLocale(), "&2Настройка покупки товара", null, "Gui", "EditBuyItem");
		check(getRuLocale(), "&2Настройка продажи товара", null, "Gui", "EditSellItem");
		check(getRuLocale(), "&2Настройка покупки комманд", null, "Gui", "EditBuyCommandItem");
		check(getRuLocale(), "&2Настройка товара", null, "Gui", "EditAuctionItem");
		check(getRuLocale(), "&2Покупка", null, "Gui", "EditBuyTransaction");
		check(getRuLocale(), "&2Продажа", null, "Gui", "EditSellTransaction");
		
		check(getRuLocale(), "&cНе удалось списать деньги с баланса игрока &e%player%&c.", null, "Debug", "ErrorTakeMoney");
		check(getRuLocale(), "&cНе удалось начислить деньги на баланс игрока &e%player%&c.", null, "Debug", "ErrorGiveMoney");
		check(getRuLocale(), "&aПредмет &7[&r%item%]&ax%amount% удален из инвентаря игрока &e%player%&a. Начисленно денег %added%. Баланс %balance%.", null, "Debug", "InfoTakeItems");
		check(getRuLocale(), "&aПредмет &7[&r%item%]&ax%amount% добавлен в инвентарь игрока &e%player%&a. Списано денег %removed%. Баланс %balance%.", null, "Debug", "InfoGiveItems");
		
		if(save) getRuLocale().saveLocaleNode();
		save = false;
	}

	private void generateDefaultLocale() {
		check(getDefaultLocale(), "&eBack", null, "FillItems", "Back");
		check(getDefaultLocale(), "&eNext", null, "FillItems", "Next");
		check(getDefaultLocale(), "&eAdd page", null, "FillItems", "AddPage");
		check(getDefaultLocale(), "&eBuy and exit", null, "FillItems", "Buy");
		check(getDefaultLocale(), "&eSell and exit", null, "FillItems", "Sell");
		check(getDefaultLocale(), "&eSave and exit", null, "FillItems", "Exit");
		check(getDefaultLocale(), "&eBuy and go back", null, "FillItems", "BuyAndBack");
		check(getDefaultLocale(), "&eSell and go back", null, "FillItems", "SellAndBack");
		check(getDefaultLocale(), "&eSize: &a%value%", null, "FillItems", "Size");
		check(getDefaultLocale(), "&ePrice: &a%value%", null, "FillItems", "Price");
		check(getDefaultLocale(), "&eClear", null, "FillItems", "Clear");
		check(getDefaultLocale(), "&eChange currency", null, "FillItems", "ChangeCurrency");
		check(getDefaultLocale(), "&eSwitch mode", null, "FillItems", "SwitchMode");
		check(getDefaultLocale(), "&eSale your item", null, "FillItems", "AuctionAddItem");
		check(getDefaultLocale(), "&eReturn your items", null, "FillItems", "ReturnAuctionItem");

		checkList(getDefaultLocale(), Arrays.asList("&aLeft click will increase the price ", "&aRight click will decrease the price "), null, "Lore", "ChangePrice");
		checkList(getDefaultLocale(), Arrays.asList("&aLeft click will increase the size", "&aRight click will decrease the size"), null, "Lore", "ChangeSize");
		check(getDefaultLocale(), "&eTransaction variants: ▼", null, "Lore", "TransactionVariants");
		check(getDefaultLocale(), "&eCurrent currency: &a%currency%", null, "Lore", "CurrentCurrency");
		check(getDefaultLocale(), "&eSelected size: &a%size%", null, "Lore", "CurrentSize");
		check(getDefaultLocale(), "&eTotal: &a%size%", null, "Lore", "CurrentSum");
		check(getDefaultLocale(), "&eCurrency: &a%currency%&e. Buy: &a%buyprice%&e. Sell: &a%sellprice%", null, "Lore", "Price");
		check(getDefaultLocale(), "&eCurrency: &a%currency%&e. Buy: &a%buyprice%&e.", null, "Lore", "CommandPrice");
		check(getDefaultLocale(), "&eCurrency: &a%currency%&e. Price for one: &a%price%&e. Total: &a%total%", null, "Lore", "AuctionPrice");
		check(getDefaultLocale(), "&eCurrency: &a%currency%&e. Bet for one: &a%price%&e. Total: &a%total%", null, "Lore", "AuctionBet");
		check(getDefaultLocale(), "&eYour bet: &a%size%. Total: &a%total%", null, "Lore", "YourBet");
		check(getDefaultLocale(), "&eTax: &a%size%", null, "Lore", "Tax");
		check(getDefaultLocale(), "&eFee: &a%size%", null, "Lore", "Fee");
		check(getDefaultLocale(), "&eEnable/disable free use", null, "Lore", "SwitchFree");
		check(getDefaultLocale(), "&eAvailable for free", null, "Lore", "AllowFree");
		checkList(getDefaultLocale(), Arrays.asList("&eLeft click to switch the price type", "&eRight click to switch time and commissions"), null, "Lore", "AuctionSwitchMode");
		check(getDefaultLocale(), "&eSeller: &b%seller%&e.", null, "Lore", "Seller");
		check(getDefaultLocale(), "&eExpired: &a%expired%&e.", null, "Lore", "Expired");
		check(getDefaultLocale(), "&eCurrent buyer at the bet: &b%buyer%&e.", null, "Lore", "CurrentBuyer");
		check(getDefaultLocale(), "&eCurrent bet: &a%bet%&e.", null, "Lore", "CurrentBet");
		check(getDefaultLocale(), "&dLeft click &f- &dplace your bet", null, "Lore", "BetClick");
		check(getDefaultLocale(), "&dRight click &f- &dbuy an item", null, "Lore", "BuyClick");
		
		check(getDefaultLocale(), "&cYou don't have enough money.", null, "Messages", "NoMoney");
		check(getDefaultLocale(), "&cYou do not have enough money to list the item for sale at the auction.", null, "Messages", "NoMoneyForFee");
		check(getDefaultLocale(), "&cYou don't have enough items.", null, "Messages", "NoItems");
		check(getDefaultLocale(), "&aYou have successfully sold &7[&r%item%&7]&ax%amount% for %added%. Your balance %balance%.", null, "Messages", "ItemSell");
		check(getDefaultLocale(), "&aYou have successfully purchased &7[&r%item%&7]&ax%amount% for %removed%. Your balance %balance%.", null, "Messages", "ItemBuy");
		check(getDefaultLocale(), "&aYou paid %removed% to execute console commands. Your balance %balance%.", null, "Messages", "BuyCommands");
		check(getDefaultLocale(), "&cThis command can only be executed by the player.", null, "Messages", "OnlyPlayer");
		check(getDefaultLocale(), "&cShop id not specified.", null, "Messages", "ShopIDNotPresent");
		check(getDefaultLocale(), "&cA shop with this id already exists.", null, "Messages", "ShopIDAlreadyExists");
		check(getDefaultLocale(), "&cThere is no shop with this id.", null, "Messages", "ShopIDNotExists");
		check(getDefaultLocale(), "&cВведено не допустимое имя магазина.", null, "Messages", "InvalidShopID");
		check(getDefaultLocale(), "&cMenu number not specified.", null, "Messages", "MenuNotPresent");
		check(getDefaultLocale(), "&cMenu with this number does not exist, create it via GUI.", null, "Messages", "InvalidMenuId");
		check(getDefaultLocale(), "&cThe slot for placing the item is not specified.", null, "Messages", "SlotNotPresent");
		check(getDefaultLocale(), "&cThe slot number must be between 0 and 44.", null, "Messages", "InvalidSlot");
		check(getDefaultLocale(), "&cSpecify the purchase price.", null, "Messages", "BuyPriceNotPresent");
		check(getDefaultLocale(), "&cSpecify the sale price.", null, "Messages", "SellPriceNotPresent");
		check(getDefaultLocale(), "&cPlease present the item ┬──┬ ノ(゜-゜ノ)", null, "Messages", "ItemNotPresent");
		check(getDefaultLocale(), "&aYou have added a command to an item.", null, "Messages", "CommandAdded");
		check(getDefaultLocale(), "&cItem &7[&r%item%&7]&a added to shop %shop%.", null, "Messages", "ShopItemAdded");
		check(getDefaultLocale(), "&cThis item cannot be sold.", null, "Messages", "InvalidItem");
		check(getDefaultLocale(), "&cYou need to be a player or specify the player's nickname.", null, "Messages", "PlayerIsNotPresent");
		check(getDefaultLocale(), "&cYou can not open the menu to another player.", null, "Messages", "DontOpenOther");
		check(getDefaultLocale(), "&aThe shop was deleted.", null, "Messages", "SuccessDelete");
		check(getDefaultLocale(), "&eThe shop list is now empty. Contact the administration.", null, "Messages", "ShopListEmpty");
		check(getDefaultLocale(), "&eThe shop list is empty. Create at least 1 store. ", null, "Messages", "ShopListEmptyEditor");
		check(getDefaultLocale(), "&aDebug on.", null, "Messages", "DebugOn");
		check(getDefaultLocale(), "&aDebug off.", null, "Messages", "DebugOff");
		check(getDefaultLocale(), "&aThe plugin has been reloaded.", null, "Messages", "Reload");
		check(getDefaultLocale(), "&aLocalization name not specified.", null, "Messages", "EmptyTranslateName");
		check(getDefaultLocale(), "&cThere is no such localization. The format is en-US, ru-RU, etc.", null, "Messages", "LocaleNotExist");
		check(getDefaultLocale(), "&cSpecify the required localization. The format is en-US, ru-RU, etc.", null, "Messages", "LocaleNotPresent");
		check(getDefaultLocale(), "&cEnter a display name for your shop. Color codes accepted.", null, "Messages", "TranslateNotPresent");
		check(getDefaultLocale(), "&aYou have successfully set the display name for your shop.", null, "Messages", "TranslateAdded");
		check(getDefaultLocale(), "&3List of shops", null, "Messages", "ShopListTitle");
		check(getDefaultLocale(), "&3=", null, "Messages", "ShopListPadding");
		check(getDefaultLocale(), "&3Command list", null, "Messages", "CommandsTitle");
		check(getDefaultLocale(), "&3=", null, "Messages", "CommandsPadding");
		check(getDefaultLocale(), "&cYou are already selling the maximum amount of items.", null, "Messages", "AuctionMaxVolume");
		check(getDefaultLocale(), "&aYou have successfully sold on auction &7[&r%item%&7]&ax%amount% за %removed%. Your balance %balance%. Seller &b%seller%&a.", null, "Messages", "AuctionBuy");
		check(getDefaultLocale(), "&aYou have successfully sold on auction &7[&r%item%&7]&ax%amount% за %added%. Your balance %balance%. Buyer &b%buyer%&a.", null, "Messages", "AuctionSell");
		check(getDefaultLocale(), "&aYour items have expired. Click on this message to get them back.", null, "Messages", "AuctionExpired");
		check(getDefaultLocale(), "&aCompleted transaction for redemption of items at auction. Click on this message to get them.", null, "Messages", "AuctionBetExpired");
		check(getDefaultLocale(), "&aWithholding tax: %amount%.", null, "Messages", "Tax");
		check(getDefaultLocale(), "&cThere are not enough empty slots in your inventory. Free at least %value%.", null, "Messages", "NoEmptySlots");
		check(getDefaultLocale(), "&cItem was purchased by someone else or removed from sale.", null, "Messages", "AuctionItemNotFound");
		check(getDefaultLocale(), "&eYou didn't specify a bet for your lot. To confirm the lot placement, click on this message.", null, "Messages", "AuctionBetNotPresent");
		check(getDefaultLocale(), "&eYou didn't specify a price for your lot. To confirm the lot placement, click on this message.", null, "Messages", "AuctionPriceNotPresent");
		check(getDefaultLocale(), "&eYou did not specify the currency for your lot. The default currency will be used. To confirm the lot placement, click on this message. \n&eAvailable currencies: &6%currencies%&e.", null, "Messages", "AuctionCurrencyNotPresent");
		check(getDefaultLocale(), "&cYou cannot sell an item without a price/bet or with a zero price/bet. One of the values must be greater than zero.", null, "Messages", "AuctionZeroOrNullPrices");
		check(getDefaultLocale(), "&cYou can't buy or assign a bet on your item.", null, "Messages", "AuctionCancelBuy");
		check(getDefaultLocale(), "&aYou putted the item up for sale.", null, "Messages", "AuctionItemAdded");
		check(getDefaultLocale(), "&eYou did not set your bet on the goods.", null, "Messages", "BetIsNotSet");
		check(getDefaultLocale(), "&cThis item cannot be put up for sale.", null, "Messages", "ItemBlocked");
		check(getDefaultLocale(), "&cThe item is now locked.", null, "Messages", "AddedItemBlocking ");
		check(getDefaultLocale(), "&cBlocking mask added.", null, "Messages", "AddedMaskBlocking ");
		check(getDefaultLocale(), "&cThe item has an NBT tag that is too long.", null, "Messages", "LongNBT");
		check(getDefaultLocale(), "&cItem added to the blacklist.", null, "Messages", "AddedToBlackList");
		check(getDefaultLocale(), "&cThe item is already blocked.", null, "Messages", "ItemIsAlreadyBlocked");
		check(getDefaultLocale(), "There is no economy plugin on the server. The functions of the plugin will not be available.", null, "Messages", "EconomyNotFound");
		
		check(getDefaultLocale(), "&eClick to open this shop in the editor.", null, "Hover", "OpenShopEdit");
		check(getDefaultLocale(), "&eClick to open this store.", null, "Hover", "OpenShop");
		check(getDefaultLocale(), "&eClick to execute this command.", null, "Hover", "RunCommand");

		check(getDefaultLocale(), "&2Auction", null, "Gui", "Auction");
		check(getDefaultLocale(), "&2Bet", null, "Gui", "AuctionBet");
		check(getDefaultLocale(), "&2Return items", null, "Gui", "AuctionReturn");
		check(getDefaultLocale(), "&2Setting of purchase item", null, "Gui", "EditBuyItem");
		check(getDefaultLocale(), "&2Setting of sell item", null, "Gui", "EditSellItem");
		check(getDefaultLocale(), "&2Setting of purchase commands", null, "Gui", "EditBuyCommandItem");
		check(getDefaultLocale(), "&2Setting an item", null, "Gui", "EditAuctionItem");
		check(getDefaultLocale(), "&2Buy", null, "Gui", "EditBuyTransaction");
		check(getDefaultLocale(), "&2Sell", null, "Gui", "EditSellTransaction");
		
		check(getDefaultLocale(), "&cFailed to remove money from balance of player &e%player%&c.", null, "Debug", "ErrorTakeMoney");
		check(getDefaultLocale(), "&cFailed to add money to the balance of player &e%player%&c.", null, "Debug", "ErrorGiveMoney");
		check(getDefaultLocale(), "&aItem &7[&r%item%]&ax%amount% removed from inventory of player &e%player%&a. Added money %added%. Balance %balance%.", null, "Debug", "InfoTakeItems");
		check(getDefaultLocale(), "&aItem &7[&r%item%]&ax%amount% added to inventory of player &e%player%&a. Removed money %removed%. Balance %balance%.", null, "Debug", "InfoGiveItems");
		
		if(save) getDefaultLocale().saveLocaleNode();
		save = false;
	}

	private void check(PluginLocale locale, String text, String comment, Object... path) {
		updateSave(locale.checkComponent(true, TextUtils.deserialize(text), comment, path));
	}
	private void checkList(PluginLocale locale, List<String> texts, String comment, Object... path) {
		updateSave(locale.checkListComponents(true, texts.stream().map(TextUtils::deserialize).toList(), comment, path));
	}
	private PluginLocale getRuLocale() {
		return plugin.getLocales().getOrDefaultLocale(Locales.RU_RU);
	}
	private PluginLocale getDefaultLocale() {
		return plugin.getLocales().getOrDefaultLocale(Locales.DEFAULT);
	}
	private void updateSave(boolean check) {
		if(check) save = true;
	}

}
