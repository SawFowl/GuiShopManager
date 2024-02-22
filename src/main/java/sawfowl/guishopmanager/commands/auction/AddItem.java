package sawfowl.guishopmanager.commands.auction;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.Command.Parameterized;
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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.commandpack.api.data.command.Settings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractPlayerCommand;
import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionPrice;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;

public class AddItem extends AbstractPlayerCommand {

	GuiShopManager plugin;
	public AddItem(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public void execute(CommandContext context, ServerPlayer player, Locale locale) throws CommandException {
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
				if(TextUtils.clearDecorations(currency.displayName()).equalsIgnoreCase(context.one(CommandParameters.CURRENCY).get()) || TextUtils.clearDecorations(currency.displayName()).equalsIgnoreCase(context.one(CommandParameters.CURRENCY).get()) || TextUtils.clearDecorations(currency.symbol()).equalsIgnoreCase(context.one(CommandParameters.CURRENCY).get())) {
					currencyPresent = true;
					break;
				} else {
					currencyPresent = false;
				}
			}
		}
		Component currenciesList = Component.text().append(plugin.getEconomyService().defaultCurrency().displayName()).build();
		if(!currencyPresent || (!plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()).equals(plugin.getEconomyService().defaultCurrency()) && Permissions.auctionCurrencyPermission(player, plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()), false))) {
			int currentCurrency = 1;
			for(Currency currency : plugin.getEconomy().getCurrencies()) {
				if(Permissions.auctionCurrencyPermission(player, currency, false) && !currency.equals(plugin.getEconomyService().defaultCurrency())) {
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
			exception(locale, "Messages", "ItemNotPresent");
		} else {
			if(!betPresent && !pricePresent || (!pricePresent && context.one(CommandParameters.AUCTION_BET).get() <= 0) || (!betPresent && context.one(CommandParameters.AUCTION_PRICE).get() <= 0) || (context.one(CommandParameters.AUCTION_BET).get() <= 0 && context.one(CommandParameters.AUCTION_PRICE).get() <= 0)) {
				exception(locale, "Messages", "AuctionZeroOrNullPrices");
			} else {
				if(!betPresent && pricePresent && context.one(CommandParameters.AUCTION_PRICE).get() > 0) {
					if(!currencyPresent || (!plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()).equals(plugin.getEconomyService().defaultCurrency()) && !Permissions.auctionCurrencyPermission(player, plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()), false))) {
						Component message = getComponent(player.locale(), "Messages", "AuctionBetNotPresent").clickEvent(SpongeComponents.executeCallback(cause -> {
							SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency());
							auctionPrice.setPrice(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_PRICE).get()));
							SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getRootNode().node("Auction", "Server").getString());
							run(player, auctionStack);
						}));
						player.sendMessage(message);
						Component message2 = getComponent(player.locale(), "Messages", "AuctionCurrencyNotPresent").replaceText(TextReplacementConfig.builder().match("%currencies%").replacement(currenciesList).build()).clickEvent(SpongeComponents.executeCallback(cause -> {
							SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency());
							auctionPrice.setPrice(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_PRICE).get()));
							SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getRootNode().node("Auction", "Server").getString());
							run(player, auctionStack);
						}));
						player.sendMessage(message2);
					} else {
						Component message = getComponent(player.locale(), "Messages", "AuctionBetNotPresent").clickEvent(SpongeComponents.executeCallback(cause -> {
							SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()));
							auctionPrice.setPrice(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_PRICE).get()));
							SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getRootNode().node("Auction", "Server").getString());
							run(player, auctionStack);
						}));
						player.sendMessage(message);
					}
				} else if(!pricePresent && betPresent && context.one(CommandParameters.AUCTION_BET).get() > 0) {
					Component message = getComponent(player.locale(), "Messages", "AuctionPriceNotPresent").clickEvent(SpongeComponents.executeCallback(cause -> {
						SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency());
						auctionPrice.setBet(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_BET).get()));
						SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getRootNode().node("Auction", "Server").getString());
						run(player, auctionStack);
					}));
					player.sendMessage(message);
				} else if(context.one(CommandParameters.AUCTION_PRICE).get() > 0 && context.one(CommandParameters.AUCTION_BET).get() > 0) {
					if(!currencyPresent || (!plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()).equals(plugin.getEconomyService().defaultCurrency()) && !Permissions.auctionCurrencyPermission(player, plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()), false))) {
						Component message = getComponent(player.locale(), "Messages", "AuctionPriceNotPresent").clickEvent(SpongeComponents.executeCallback(cause -> {
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
					if(!currencyPresent || (!plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()).equals(plugin.getEconomyService().defaultCurrency()) && !Permissions.auctionCurrencyPermission(player, plugin.getEconomy().checkCurrency(context.one(CommandParameters.CURRENCY).get()), false))) {
						Component message = getComponent(player.locale(), "Messages", "AuctionCurrencyNotPresent").replaceText(TextReplacementConfig.builder().match("%currencies%").replacement(currenciesList).build()).clickEvent(SpongeComponents.executeCallback(cause -> {
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

	@Override
	public Parameterized build() {
		return fastBuild();
	}

	@Override
	public String command() {
		return "additem";
	}

	@Override
	public String permission() {
		return Permissions.AUCTION_ADD_ITEM;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return Arrays.asList(
			ParameterSettings.of(CommandParameters.AUCTION_BET, true, "Messages", "AuctionZeroOrNullPrices"),
			ParameterSettings.of(CommandParameters.AUCTION_PRICE, true, "Messages", "AuctionZeroOrNullPrices"),
			ParameterSettings.of(CommandParameters.CURRENCY, true, "Messages", "AuctionZeroOrNullPrices")
		);
	}

	@Override
	public Settings applyCommandSettings() {
		return null;
	}

	private void run(ServerPlayer player, SerializedAuctionStack auctionStack) {
		if(plugin.maskIsBlackList(auctionStack.getSerializedItemStack().getItemTypeAsString()) || plugin.itemIsBlackList(auctionStack.getSerializedItemStack().getItemStack())) {
			player.sendMessage(getComponent(player.locale(), "Messages", "ItemBlocked"));
			return;
		}
		if(player.inventory().query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY.get().of(auctionStack.getSerializedItemStack().getItemStack())).totalQuantity() < auctionStack.getSerializedItemStack().getQuantity()) {
			player.sendMessage(getComponent(player.locale(), "Messages", "ItemNotPresent"));
			return;
		}
		if(checkNbtLength(auctionStack)) {
			player.sendMessage(getComponent(player.locale(), "Messages", "LongNBT"));
			return;
		}
		if(plugin.getExpire(0).isFee()) {
			if(!plugin.getEconomy().fee(player, BigDecimal.valueOf(plugin.getExpire(0).getFee()))) {
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
			plugin.getAuctionStorage().saveAuctionStack(auctionStack);
		}).plugin(plugin.getPluginContainer()).build());
		player.inventory().query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY.get().of(auctionStack.getSerializedItemStack().getItemStack())).poll(auctionStack.getSerializedItemStack().getQuantity());
		player.sendMessage(getComponent(player.locale(), "Messages", "AuctionItemAdded"));
	}

	private boolean checkNbtLength(SerializedAuctionStack auctionStack) {
		return auctionStack.getSerializedItemStack().getNBT().toString().length() > plugin.getRootNode().node("Auction", "NbtLimit").getInt();
	}

	private long time() {
		return plugin.getExpire(0).getTime() + System.currentTimeMillis();
	}

}
