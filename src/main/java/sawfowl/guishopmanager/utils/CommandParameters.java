package sawfowl.guishopmanager.utils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandCompletion;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.command.parameter.managed.ValueParameter;
import org.spongepowered.api.command.parameter.managed.standard.VariableValueParameters;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.EconomyService;

import sawfowl.commandpack.utils.CommandsUtil;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.data.commandshop.CommandShopData;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.localeapi.api.EnumLocales;
import sawfowl.localeapi.api.TextUtils;

public class CommandParameters {

	private static final Map<String, Locale> LOCALES_MAP = Stream.of(EnumLocales.values()).collect(Collectors.toMap(entry -> entry.getTag(), entry -> entry.get()));

	private static final List<CommandCompletion> EMPTY = new ArrayList<>();

	public static final Parameter.Value<ServerPlayer> PLAYER_FOR_SHOP = Parameter.player().optional().requiredPermission(Permissions.SHOP_OPEN_OTHER).key("Player").build();

	public static final Parameter.Value<ServerPlayer> PLAYER_FOR_AUCTION = Parameter.player().optional().requiredPermission(Permissions.AUCTION_OPEN_OTHER).key("Player").build();

	public static final Parameter.Value<ServerPlayer> PLAYER_FOR_COMMANDSHOP = Parameter.player().optional().requiredPermission(Permissions.COMMANDSSHOP_OPEN_OTHER).key("Player").build();

	public static final Parameter.Value<String> SHOP_ID = Parameter.string().optional().key("ShopName").build();

	public static final Parameter.Value<Shop> SHOP = Parameter.builder(Shop.class).key("Shop").optional().completer((context, input) -> GuiShopManager.getInstance().availableShops().stream().filter(shop -> input.isEmpty() || shop.contains(input)).map(CommandCompletion::of).toList()).addParser(VariableValueParameters.dynamicChoicesBuilder(Shop.class).choices(() -> GuiShopManager.getInstance().availableShops()).results(key -> GuiShopManager.getInstance().shopExists(key) ? GuiShopManager.getInstance().getShop(key) : null).build()).build();

	public static final Parameter.Value<CommandShopData> COMMAND_SHOP = Parameter.builder(CommandShopData.class).key("Shop").optional().completer((context, input) -> GuiShopManager.getInstance().availableCommandShops().stream().filter(shop -> input.isEmpty() || shop.contains(input)).map(CommandCompletion::of).toList()).addParser(VariableValueParameters.dynamicChoicesBuilder(CommandShopData.class).choices(() -> GuiShopManager.getInstance().availableCommandShops()).results(key -> GuiShopManager.getInstance().commandShopExists(key) ? GuiShopManager.getInstance().getCommandShopData(key) : null).build()).build();

	public static final Parameter.Value<Locale> LOCALE = Parameter.builder(Locale.class).key("Locale").completer((context, input) -> !context.one(SHOP).isPresent() && !context.one(COMMAND_SHOP).isPresent() ? EMPTY : LOCALES_MAP.values().stream().filter(locale -> input.isEmpty() || locale.toLanguageTag().contains(input)).map(Locale::toLanguageTag).map(CommandCompletion::of).toList()).addParser(VariableValueParameters.dynamicChoicesBuilder(Locale.class).choices(() -> LOCALES_MAP.keySet()).results(key -> LOCALES_MAP.containsKey(key) ? LOCALES_MAP.get(key) : null).build()).build();

	public static final Parameter.Value<String> TRANSLATE = Parameter.remainingJoinedStrings().optional().key("Translate").build();

	public static final Parameter.Value<Currency> CURRENCY = Parameter.builder(Currency.class).key("Currency").completer((context, input) -> curenciesCompleter()).addParser(currencyParser()).build();

	public static final Parameter.Value<String> COMMAND = Parameter.remainingJoinedStrings().key("Command").build();

	public static final Parameter.Value<Double> AUCTION_PRICE = Parameter.doubleNumber().optional().key("Price").build();

	public static final Parameter.Value<Double> AUCTION_BET = Parameter.doubleNumber().optional().key("Bet").build();

	public static final Parameter.Value<Currency> AUCTION_CURRENCY = Parameter.builder(Currency.class).key("Currency").completer((context, input) -> context.one(AUCTION_BET).isPresent() && context.one(AUCTION_PRICE).isPresent() ? curenciesCompleterAuction(context, input) : CommandsUtil.getEmptyList()).addParser(currencyParser()).optional().build();

	public static final Parameter.Value<Integer> SHOP_MENU_NUMBER = Parameter.integerNumber().optional().key("Menu").build();

	public static final Parameter.Value<Integer> SLOT = Parameter.rangedInteger(0, 44).optional().key("Slot").build();

	public static final Parameter.Value<BigDecimal> SHOP_BUY_PRICE = Parameter.bigDecimal().optional().key("BuyPrice").build();

	public static final Parameter.Value<BigDecimal> SHOP_SELL_PRICE = Parameter.bigDecimal().optional().key("SellPrice").build();

	public static final Flag MASK = Flag.of("m", "mask");

	public static final Flag ITEM = Flag.of("i", "item");

	private static List<CommandCompletion> CURENCIES_COMPLETER;

	private static Map<String, Currency> CURENCIES_MAP;

	private static List<CommandCompletion> curenciesCompleter() {
		if(CURENCIES_MAP == null) fillCurrenciesMap();
		return CURENCIES_COMPLETER == null ? CURENCIES_COMPLETER = CURENCIES_MAP.keySet().stream().map(c -> CommandCompletion.of(c)).toList() : CURENCIES_COMPLETER;
	}

	private static List<CommandCompletion> curenciesCompleterAuction(CommandContext context, String input) {
		if(CURENCIES_MAP == null) fillCurrenciesMap();
		return new ArrayList<>(CURENCIES_MAP.entrySet().stream().filter(e -> Permissions.auctionCurrencyPermission(context, e.getValue(), false)).collect(Collectors.toMap(e -> CommandCompletion.of(e.getKey()), e -> e.getValue())).keySet());
	}

	private static ValueParameter<Currency> currencyParser() {
		if(CURENCIES_MAP == null) fillCurrenciesMap();
		return VariableValueParameters.dynamicChoicesBuilder(Currency.class).choices(() -> CURENCIES_MAP.keySet()).results(key -> CURENCIES_MAP.containsKey(key) ? CURENCIES_MAP.get(key) : null).build();
	}

	private static void fillCurrenciesMap() {
		CURENCIES_MAP = Stream.concat(Sponge.serviceProvider().provide(EconomyService.class).map(e-> e.defaultCurrency()).stream(), RegistryTypes.CURRENCY.get().stream()).collect(Collectors.toMap(c -> TextUtils.clearDecorations(c.displayName()), c -> c));
	}

}
