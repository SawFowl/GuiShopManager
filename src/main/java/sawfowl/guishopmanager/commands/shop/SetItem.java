package sawfowl.guishopmanager.commands.shop;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractPlayerCommand;
import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.data.shop.ShopItem;
import sawfowl.guishopmanager.serialization.shop.SerializedShopPrice;

public class SetItem extends AbstractPlayerCommand {

	public SetItem(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public void execute(CommandContext context, ServerPlayer player, Locale locale) throws CommandException {
		if(!plugin.shopsEmpty()) {
			Shop shop = context.one(CommandParameters.SHOP).orElse(null);
			if(shop != null) {
				if(context.one(CommandParameters.SHOP_MENU_NUMBER).isPresent()) {
					int menuId = context.one(CommandParameters.SHOP_MENU_NUMBER).get();
					if(shop.getMenus().containsKey(menuId)) {
						if(context.one(CommandParameters.SLOT).isPresent()) {
							int slot = context.one(CommandParameters.SLOT).get();
							if(slot >= 0 && slot <= 44) {
								if(context.one(CommandParameters.SHOP_BUY_PRICE).isPresent()) {
									BigDecimal buyPrice = context.one(CommandParameters.SHOP_BUY_PRICE).get();
									if(context.one(CommandParameters.SHOP_SELL_PRICE).isPresent()) {
										BigDecimal sellPrice = context.one(CommandParameters.SHOP_SELL_PRICE).get();
										if(!player.itemInHand(HandTypes.MAIN_HAND).isEmpty() || !player.itemInHand(HandTypes.OFF_HAND).isEmpty()) {
											ItemStack itemStack = !player.itemInHand(HandTypes.MAIN_HAND).isEmpty() ? player.itemInHand(HandTypes.MAIN_HAND) : (!player.itemInHand(HandTypes.OFF_HAND).isEmpty() ? player.itemInHand(HandTypes.OFF_HAND) : null);
											if(itemStack != null && !itemStack.type().equals(ItemTypes.AIR.get())) {
												Currency currency = plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).isPresent() ? context.one(CommandParameters.CURRENCY).get() : "");
												itemStack.setQuantity(1);
												SerializedShopPrice serializedShopPrice = new SerializedShopPrice(currency);
												serializedShopPrice.setBuyOrSellPrice(buyPrice, true, true);
												serializedShopPrice.setBuyOrSellPrice(sellPrice, false, true);
												shop.getMenus().get(menuId).getItems().put(slot, new ShopItem(itemStack, Arrays.asList(serializedShopPrice)));
												plugin.getShopStorage().saveShop(shop.getID());
												//String[] splitedItemID = RegistryTypes.ITEM_TYPE.get().valueKey(itemStack.type()).asString().split(":");
												Component message = getText(locale, "Messages", "ShopItemAdded").replace(new String[] {"%item%", "%shop%"}, itemStack.type().asComponent().hoverEvent(HoverEvent.showItem(ShowItem.of(Key.key(ItemTypes.registry().valueKey(itemStack.type()).asString()), 1))), shop.getOrDefaultTitle(player.locale())).get();
												player.sendMessage(message);
											} else exception(locale, "Messages", "ItemNotPresent");
										} else exception(locale, "Messages", "ItemNotPresent");
									} else exception(locale, "Messages", "SellPriceNotPresent");
								} else exception(locale, "Messages", "BuyPriceNotPresent");
							} else exception(locale, "Messages", "InvalidSlot");
						} else exception(locale, "Messages", "SlotNotPresent");
					} else exception(locale, "Messages", "InvalidMenuId");
				} else exception(locale, "Messages", "MenuNotPresent");
			} else exception(locale, "Messages", "ShopIDNotPresent");
		} else exception(locale, "Messages", "ShopListEmptyEditor");
	}

	@Override
	public Parameterized build() {
		return Command.builder()
				.addParameters(CommandParameters.SHOP, CommandParameters.SHOP_MENU_NUMBER, CommandParameters.SLOT, CommandParameters.SHOP_BUY_PRICE, CommandParameters.SHOP_SELL_PRICE, CommandParameters.CURRENCY)
				.executor(this)
				.permission(permission())
				.build();
	}

	@Override
	public String command() {
		return "setitem";
	}

	@Override
	public String permission() {
		return Permissions.SHOP_EDIT;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return Arrays.asList(
			ParameterSettings.of(CommandParameters.SHOP, false, "Messages", "ShopIDNotPresent"),
			ParameterSettings.of(CommandParameters.SHOP_MENU_NUMBER, false, "Messages", "MenuNotPresent"),
			ParameterSettings.of(CommandParameters.SLOT, false, "Messages", "SlotNotPresent"),
			ParameterSettings.of(CommandParameters.SHOP_BUY_PRICE, false, "Messages", "BuyPriceNotPresent"),
			ParameterSettings.of(CommandParameters.SHOP_SELL_PRICE, false, "Messages", "SellPriceNotPresent"),
			ParameterSettings.of(CommandParameters.CURRENCY, false, new Object[] {})
		);
	}

}
