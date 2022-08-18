package sawfowl.guishopmanager.utils;

import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

public class CommandParameters {

	public static final Parameter.Value<ServerPlayer> PLAYER = Parameter.player().optional().key("Player").build();

	public static final Parameter.Value<String> SHOP_ID = Parameter.string().optional().key("ShopName").build();

	public static final Parameter.Value<String> LOCALE = Parameter.string().optional().key("Locale").build();

	public static final Parameter.Value<String> TRANSLATE = Parameter.remainingJoinedStrings().optional().key("Translate").build();

	public static final Parameter.Value<String> CURRENCY = Parameter.string().optional().key("Currency").build();

	public static final Parameter.Value<String> COMMAND = Parameter.remainingJoinedStrings().optional().key("Command").build();

	public static final Parameter.Value<Double> AUCTION_PRICE = Parameter.doubleNumber().optional().key("Price").build();

	public static final Parameter.Value<Double> AUCTION_BET = Parameter.doubleNumber().optional().key("Bet").build();

	public static final Parameter.Value<Integer> SHOP_MENU_NUMBER = Parameter.integerNumber().optional().key("Menu").build();

	public static final Parameter.Value<Integer> SLOT = Parameter.integerNumber().optional().key("Slot").build();

	public static final Parameter.Value<Double> SHOP_BUY_PRICE = Parameter.doubleNumber().optional().key("BuyPrice").build();

	public static final Parameter.Value<Double> SHOP_SELL_PRICE = Parameter.doubleNumber().optional().key("SellPrice").build();

	public static final Flag MASK = Flag.of("m", "mask");

	public static final Flag ITEM = Flag.of("i", "item");

}
