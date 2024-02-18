package sawfowl.guishopmanager.commands.shop;

import java.math.BigDecimal;
import java.util.Arrays;

import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.util.locale.LocaleSource;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.event.HoverEvent.ShowItem;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.data.shop.ShopItem;
import sawfowl.guishopmanager.serialization.shop.SerializedShopPrice;

public class ShopSetItem implements CommandExecutor {

	GuiShopManager plugin;
	public ShopSetItem(GuiShopManager instance) {
		plugin = instance;
	}
	@Override
	public CommandResult execute(CommandContext context) throws CommandException {
		Object src = context.cause().root();
		if(plugin.shopsEmpty()) {
			((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "ShopListEmptyEditor"));
		} else {
			if(!(src instanceof ServerPlayer)) {
				((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "OnlyPlayer"));
			} else {
				ServerPlayer player = (ServerPlayer) src;
				if(!context.one(CommandParameters.SHOP_ID).isPresent()) {
					player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ShopIDNotPresent"));
				} else {
					String shopId = context.one(CommandParameters.SHOP_ID).get();
					if(!plugin.shopExists(shopId)) {
						player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ShopIDNotExists"));
					} else {
						Shop shop = plugin.getShop(shopId);
						if(!context.one(CommandParameters.SHOP_MENU_NUMBER).isPresent()) {
							player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "MenuNotPresent"));
						} else {
							int menuId = context.one(CommandParameters.SHOP_MENU_NUMBER).get();
							if(!shop.getMenus().containsKey(menuId)) {
								player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "InvalidMenuId"));
							} else {
								if(!context.one(CommandParameters.SLOT).isPresent()) {
									player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "SlotNotPresent"));
								} else {
									int slot = context.one(CommandParameters.SLOT).get();
									if(slot < 0 || slot > 44) {
										player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "InvalidSlot"));
									} else {
										if(!context.one(CommandParameters.SHOP_BUY_PRICE).isPresent()) {
											player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "BuyPriceNotPresent"));
										} else {
											BigDecimal buyPrice = BigDecimal.valueOf(context.one(CommandParameters.SHOP_BUY_PRICE).get()).setScale(2);
											if(!context.one(CommandParameters.SHOP_SELL_PRICE).isPresent()) {
												player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "SellPriceNotPresent"));
											} else {
												BigDecimal sellPrice = BigDecimal.valueOf(context.one(CommandParameters.SHOP_SELL_PRICE).get()).setScale(2);
												if(player.itemInHand(HandTypes.MAIN_HAND).isEmpty() && player.itemInHand(HandTypes.OFF_HAND).isEmpty()) {
													player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ItemNotPresent"));
												} else {
													ItemStack itemStack = null;
													if(!player.itemInHand(HandTypes.MAIN_HAND).isEmpty()) {
														itemStack = player.itemInHand(HandTypes.MAIN_HAND);
													} else if(!player.itemInHand(HandTypes.OFF_HAND).isEmpty()){
														itemStack = player.itemInHand(HandTypes.OFF_HAND);
													} 
													if(itemStack == null || itemStack.type() == ItemTypes.AIR.get()) {
														player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ItemNotPresent"));
													} else {
														Currency currency = plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).isPresent() ? context.one(CommandParameters.CURRENCY).get() : "");
														itemStack.setQuantity(1);
														SerializedShopPrice serializedShopPrice = new SerializedShopPrice(currency);
														serializedShopPrice.setBuyOrSellPrice(buyPrice, true, true);
														serializedShopPrice.setBuyOrSellPrice(sellPrice, true, false);
														shop.getMenus().get(menuId).getItems().put(slot, new ShopItem(itemStack, Arrays.asList(serializedShopPrice)));
														plugin.getShopStorage().saveShop(shopId);
														Component itemMessage = itemStack.type().asComponent();
														String[] splitedItemID = RegistryTypes.ITEM_TYPE.get().valueKey(itemStack.type()).asString().split(":");
														itemMessage.hoverEvent(HoverEvent.showItem(ShowItem.of(Key.key(splitedItemID[0], splitedItemID[1]), 1)));
														Component message = plugin.getLocales().getComponent(player.locale(), "Messages", "ShopItemAdded")
																.replaceText(TextReplacementConfig.builder().match("%item%").replacement(itemMessage).build())
																.replaceText(TextReplacementConfig.builder().match("%shop%").replacement(shop.getOrDefaultTitle(player.locale())).build());
														player.sendMessage(message);
													}
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}
		return CommandResult.success();
	}

}
