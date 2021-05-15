package sawfowl.guishopmanager.utils.commands;

import java.math.BigDecimal;
import java.util.Arrays;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.data.shop.Shop;
import sawfowl.guishopmanager.utils.data.shop.ShopItem;
import sawfowl.guishopmanager.utils.serialization.shop.SerializedShopPrice;

public class ShopSetItem implements CommandExecutor {

	GuiShopManager plugin;
	public ShopSetItem(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(plugin.getAllShops().isEmpty()) {
			src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "ShopListEmptyEditor"));
		} else {
			if(!(src instanceof Player)) {
				src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "OnlyPlayer"));
			} else {
				Player player = (Player) src;
				if(!args.<String>getOne(Text.of("Shop")).isPresent()) {
					player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ShopIDNotPresent"));
				} else {
					String shopId = args.<String>getOne(Text.of("Shop")).get();
					if(!plugin.shopExists(shopId)) {
						player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ShopIDNotExists"));
					} else {
						Shop shop = plugin.getShop(shopId);
						if(!args.<Integer>getOne(Text.of("Menu")).isPresent()) {
							player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "MenuNotPresent"));
						} else {
							int menuId = args.<Integer>getOne(Text.of("Menu")).get();
							if(!shop.getMenus().containsKey(menuId)) {
								player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "InvalidMenuId"));
							} else {
								if(!args.<Integer>getOne(Text.of("Slot")).isPresent()) {
									player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "SlotNotPresent"));
								} else {
									int slot = args.<Integer>getOne(Text.of("Slot")).get();
									if(slot < 0 || slot > 44) {
										player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "InvalidSlot"));
									} else {
										if(!args.<Double>getOne(Text.of("BuyPrice")).isPresent()) {
											player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "BuyPriceNotPresent"));
										} else {
											BigDecimal buyPrice = BigDecimal.valueOf(args.<Double>getOne(Text.of("BuyPrice")).get()).setScale(2);
											if(!args.<Double>getOne(Text.of("SellPrice")).isPresent()) {
												player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "SellPriceNotPresent"));
											} else {
												BigDecimal sellPrice = BigDecimal.valueOf(args.<Double>getOne(Text.of("SellPrice")).get()).setScale(2);
												if(!player.getItemInHand(HandTypes.MAIN_HAND).isPresent() && !player.getItemInHand(HandTypes.OFF_HAND).isPresent()) {
													player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ItemNotPresent"));
												} else {
													ItemStack itemStack = null;
													if(player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
														itemStack = player.getItemInHand(HandTypes.MAIN_HAND).get();
													} else if(player.getItemInHand(HandTypes.OFF_HAND).isPresent()){
														itemStack = player.getItemInHand(HandTypes.OFF_HAND).get();
													} 
													if(itemStack == null || itemStack.getType() == ItemTypes.AIR) {
														player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ItemNotPresent"));
													} else {
														Currency currency = plugin.getEconomy().checkCurrency(args.<String>getOne(Text.of("Currency")).isPresent() ? args.<String>getOne(Text.of("Currency")).get() : "");
														itemStack.setQuantity(1);
														SerializedShopPrice serializedShopPrice = new SerializedShopPrice(currency);
														serializedShopPrice.setBuyOrSellPrice(buyPrice, true);
														serializedShopPrice.setBuyOrSellPrice(sellPrice, false);
														shop.getMenus().get(menuId).getItems().put(slot, new ShopItem(itemStack, Arrays.asList(serializedShopPrice)));
														plugin.getWorkShopData().saveShop(shopId);
														player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ShopItemAdded").replace("%item%", Text.builder().append(Text.of(itemStack)).onHover(TextActions.showText(Text.of(itemStack))).build()).replace("%shop%", shop.getOrDefaultTitle(player.getLocale())));
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
