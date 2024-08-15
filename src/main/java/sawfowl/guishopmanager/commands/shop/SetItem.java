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

import net.kyori.adventure.text.Component;

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
												Currency currency = context.one(CommandParameters.CURRENCY).orElse(plugin.getEconomyService().defaultCurrency());
												itemStack.setQuantity(1);
												SerializedShopPrice serializedShopPrice = new SerializedShopPrice(currency);
												serializedShopPrice.setBuyOrSellPrice(buyPrice, true, true);
												serializedShopPrice.setBuyOrSellPrice(sellPrice, false, true);
												shop.getMenus().get(menuId).getItems().put(slot, new ShopItem(itemStack, Arrays.asList(serializedShopPrice)));
												plugin.getShopStorage().saveShop(shop.getID());
												Component message = getCommands(locale).shop().itemAdded(itemStack, shop.getOrDefaultTitle(locale));
												player.sendMessage(message);
											} else exception(getExceptions(locale).itemNotPresent());
										} else exception(getExceptions(locale).itemNotPresent());
									} else exception(getExceptions(locale).sellPriceNotPresent());
								} else exception(getExceptions(locale).buyPriceNotPresent());
							} else exception(getExceptions(locale).invalidSlot());
						} else exception(getExceptions(locale).slotNotPresent());
					} else exception(getExceptions(locale).invalidMenuId());
				} else exception(getExceptions(locale).menuNotPresent());
			} else exception(getExceptions(locale).shopNotPresent());
		} else exception(getCommands(locale).shop().listEmptyEditor());
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
			ParameterSettings.of(CommandParameters.SHOP, false, locale -> getExceptions(locale).shopNotPresent()),
			ParameterSettings.of(CommandParameters.SHOP_MENU_NUMBER, false, locale -> getExceptions(locale).menuNotPresent()),
			ParameterSettings.of(CommandParameters.SLOT, false, locale -> getExceptions(locale).slotNotPresent()),
			ParameterSettings.of(CommandParameters.SHOP_BUY_PRICE, false, locale -> getExceptions(locale).buyPriceNotPresent()),
			ParameterSettings.of(CommandParameters.SHOP_SELL_PRICE, false, locale -> getExceptions(locale).sellPriceNotPresent()),
			ParameterSettings.of(CommandParameters.CURRENCY, false, locale -> getExceptions(locale).currencyNotPresent())
		);
	}

}
