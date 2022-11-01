package sawfowl.guishopmanager.commands;

import java.math.BigDecimal;
import java.util.Arrays;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.query.QueryTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.api.util.locale.LocaleSource;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionPrice;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;

public class AuctionAddItem implements CommandExecutor {

	GuiShopManager plugin;
	public AuctionAddItem(GuiShopManager instance) {
		plugin = instance;
	}

	@Override
	public CommandResult execute(CommandContext context) throws CommandException {
		Object src = context.cause().root();
		if(!(src instanceof ServerPlayer)) {
			((Audience) src).sendMessage(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "OnlyPlayer"));
		} else {
			ServerPlayer player = (ServerPlayer) src;
			ItemStack itemStack = null;
			if(!player.itemInHand(HandTypes.MAIN_HAND).isEmpty()) {
				itemStack = player.itemInHand(HandTypes.MAIN_HAND);
			} else if(!player.itemInHand(HandTypes.OFF_HAND).isEmpty()) {
				itemStack = player.itemInHand(HandTypes.OFF_HAND);
			}
			ItemStack toAdd = itemStack;
			boolean betPresent = context.one(CommandParameters.AUCTION_BET).isPresent();
			boolean pricePresent = context.one(CommandParameters.AUCTION_PRICE).isPresent();
			boolean currencyPresent = context.one(CommandParameters.CURRENCY).isPresent();
			if(currencyPresent) {
				for(Currency currency : plugin.getEconomy().getCurrencies()) {
					if(toPlain(currency.displayName()).equalsIgnoreCase(context.one(CommandParameters.CURRENCY).get()) || toPlain(currency.displayName()).equalsIgnoreCase(context.one(CommandParameters.CURRENCY).get()) || toPlain(currency.symbol()).equalsIgnoreCase(context.one(CommandParameters.CURRENCY).get())) {
						currencyPresent = true;
						break;
					} else {
						currencyPresent = false;
					}
				}
			}
			Component currenciesList = Component.text().append(plugin.getEconomyService().defaultCurrency().displayName()).build();
			if(!currencyPresent || (!plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()).equals(plugin.getEconomyService().defaultCurrency()) && player.hasPermission(Permissions.currencyPermission(context.one(CommandParameters.CURRENCY).get().toLowerCase())))) {
				int currentCurrency = 1;
				for(Currency currency : plugin.getEconomy().getCurrencies()) {
					if(player.hasPermission(Permissions.currencyPermission(currency.displayName())) && !currency.equals(plugin.getEconomyService().defaultCurrency())) {
						if(currentCurrency < plugin.getEconomy().getCurrencies().size()) {
							currenciesList = currenciesList.append(Component.text(", ")).append(currency.displayName());
						} else {
							currenciesList = currenciesList.append(currency.displayName()).append(Component.text("."));
						}
					}
					currentCurrency++;
				}
			}
			if(itemStack == null || itemStack.type() == ItemTypes.AIR.get()) {
				player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ItemNotPresent"));
			} else {
				if(!betPresent && !pricePresent || (!pricePresent && context.one(CommandParameters.AUCTION_BET).get() <= 0) || (!betPresent && context.one(CommandParameters.AUCTION_PRICE).get() <= 0) || (context.one(CommandParameters.AUCTION_BET).get() <= 0 && context.one(CommandParameters.AUCTION_PRICE).get() <= 0)) {
					player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "AuctionZeroOrNullPrices"));
				} else {
					if(!betPresent && pricePresent && context.one(CommandParameters.AUCTION_PRICE).get() > 0) {
						if(!currencyPresent || (!plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()).equals(plugin.getEconomyService().defaultCurrency()) && !player.hasPermission(Permissions.currencyPermission(context.one(CommandParameters.CURRENCY).get().toLowerCase())))) {
							Component message = plugin.getLocales().getComponent(player.locale(), "Messages", "AuctionBetNotPresent").clickEvent(SpongeComponents.executeCallback(cause -> {
								SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency());
								auctionPrice.setPrice(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_PRICE).get()));
								SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getRootNode().node("Auction", "Server").getString());
								run(player, auctionStack);
							}));
							player.sendMessage(message);
							Component message2 = plugin.getLocales().getComponent(player.locale(), "Messages", "AuctionCurrencyNotPresent").replaceText(TextReplacementConfig.builder().match("%currencies%").replacement(currenciesList).build()).clickEvent(SpongeComponents.executeCallback(cause -> {
								SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency());
								auctionPrice.setPrice(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_PRICE).get()));
								SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getRootNode().node("Auction", "Server").getString());
								run(player, auctionStack);
							}));
							player.sendMessage(message2);
						} else {
							Component message = plugin.getLocales().getComponent(player.locale(), "Messages", "AuctionBetNotPresent").clickEvent(SpongeComponents.executeCallback(cause -> {
								SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()));
								auctionPrice.setPrice(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_PRICE).get()));
								SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getRootNode().node("Auction", "Server").getString());
								run(player, auctionStack);
							}));
							player.sendMessage(message);
						}
					} else if(!pricePresent && betPresent && context.one(CommandParameters.AUCTION_BET).get() > 0) {
						Component message = plugin.getLocales().getComponent(player.locale(), "Messages", "AuctionPriceNotPresent").clickEvent(SpongeComponents.executeCallback(cause -> {
							SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency());
							auctionPrice.setBet(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_BET).get()));
							SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getRootNode().node("Auction", "Server").getString());
							run(player, auctionStack);
						}));
						player.sendMessage(message);
					} else if(context.one(CommandParameters.AUCTION_PRICE).get() > 0 && context.one(CommandParameters.AUCTION_BET).get() > 0) {
						if(!currencyPresent || (!plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()).equals(plugin.getEconomyService().defaultCurrency()) && !player.hasPermission(Permissions.currencyPermission(context.one(CommandParameters.CURRENCY).get().toLowerCase())))) {
							Component message = plugin.getLocales().getComponent(player.locale(), "Messages", "AuctionPriceNotPresent").clickEvent(SpongeComponents.executeCallback(cause -> {
								SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency());
								auctionPrice.setPrice(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_PRICE).get()));
								auctionPrice.setBet(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_BET).get()));
								SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getRootNode().node("Auction", "Server").getString());
								run(player, auctionStack);
							}));
							player.sendMessage(message);
						} else {
							SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()));
							auctionPrice.setPrice(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_PRICE).get()));SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getRootNode().node("Auction", "Server").getString());
							if(auctionPrice.getCurrency().equals(plugin.getEconomyService().defaultCurrency())) {
								auctionStack.getPrices().get(0).setBet(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_BET).get()));
							} else {
								SerializedAuctionPrice betPrice = new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency());
								betPrice.setBet(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_BET).get()));
								auctionStack.getPrices().add(betPrice);
							}
							run(player, auctionStack);
						}
					} else if(context.one(CommandParameters.AUCTION_BET).get() <= 0 && context.one(CommandParameters.AUCTION_PRICE).get() > 0) {
						if(!currencyPresent || (!plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()).equals(plugin.getEconomyService().defaultCurrency()) && !player.hasPermission(Permissions.currencyPermission(context.one(CommandParameters.CURRENCY).get().toLowerCase())))) {
							Component message = plugin.getLocales().getComponent(player.locale(), "Messages", "AuctionCurrencyNotPresent").replaceText(TextReplacementConfig.builder().match("%currencies%").replacement(currenciesList).build()).clickEvent(SpongeComponents.executeCallback(cause -> {
								SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency());
								auctionPrice.setPrice(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_PRICE).get()));
								SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getRootNode().node("Auction", "Server").getString());
								run(player, auctionStack);
							}));
							player.sendMessage(message);
						} else {
							SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()));
							auctionPrice.setPrice(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_PRICE).get()));
							SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getRootNode().node("Auction", "Server").getString());
							run(player, auctionStack);
						}
					} else if(context.one(CommandParameters.AUCTION_BET).get() > 0 && context.one(CommandParameters.AUCTION_PRICE).get() <= 0) {
						SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency());
						auctionPrice.setBet(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_BET).get()));
						SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getRootNode().node("Auction", "Server").getString());
						run(player, auctionStack);
					}
				}
			}
		}
		
		return CommandResult.success();
	}

	private void run(ServerPlayer player, SerializedAuctionStack auctionStack) {
		if(plugin.maskIsBlackList(auctionStack.getSerializedItemStack().getType()) || plugin.itemIsBlackList(auctionStack.getSerializedItemStack().getItemStack())) {
			player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ItemBlocked"));
			return;
		}
		if(player.inventory().query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY.get().of(auctionStack.getSerializedItemStack().getItemStack())).totalQuantity() < auctionStack.getSerializedItemStack().getQuantity()) {
			player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ItemNotPresent"));
			return;
		}
		if(checkNbtLength(auctionStack)) {
			player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "LongNBT"));
			return;
		}
		if(plugin.getExpire(0).isFee()) {
			if(!plugin.getEconomy().fee(player, auctionStack.getPrices().get(0).getCurrency(), BigDecimal.valueOf(plugin.getExpire(0).getFee()))) {
				return;
			}
		}
		if(plugin.getExpire(0).isTax()) {
			for(SerializedAuctionPrice price : auctionStack.getPrices()) {
				if(price.getPrice().doubleValue() > 0) {
					price.setTax(plugin.getExpire(0).getTax(), auctionStack.getSerializedItemStack().getQuantity());
				}
			}
		}
		plugin.getAuctionItems().put(auctionStack.getStackUUID(), auctionStack);
		Sponge.game().asyncScheduler().submit(Task.builder().delay(Ticks.of(5)).execute(() -> {
			plugin.getAuctionWorkData().saveAuctionStack(auctionStack);
		}).plugin(plugin.getPluginContainer()).build());
		player.inventory().query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY.get().of(auctionStack.getSerializedItemStack().getItemStack())).poll(auctionStack.getSerializedItemStack().getQuantity());
		player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "AuctionItemAdded"));
	}

	private boolean checkNbtLength(SerializedAuctionStack auctionStack) {
		return auctionStack.getSerializedItemStack().getNBT().length() > plugin.getRootNode().node("Auction", "NbtLimit").getInt();
	}

	private long time() {
		return plugin.getExpire(0).getTime() + System.currentTimeMillis();
	}

	private String toPlain(Component component) {
		return removeCodes(LegacyComponentSerializer.legacyAmpersand().serialize(component));
	}

	private String removeCodes(String string) {
		while(string.indexOf('&') != -1 && !string.endsWith("&") && isStyleChar(string.charAt(string.indexOf("&") + 1))) string = string.replaceAll("&" + string.charAt(string.indexOf("&") + 1), "");
		return string;
	}

	private boolean isStyleChar(char ch) {
		return "0123456789abcdefklmnor".indexOf(ch) != -1;
	}

}
