package sawfowl.guishopmanager.utils.commands;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.data.type.HandTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.utils.serialization.auction.SerializedAuctionPrice;
import sawfowl.guishopmanager.utils.serialization.auction.SerializedAuctionStack;

public class AuctionAddItem implements CommandExecutor {

	GuiShopManager plugin;
	public AuctionAddItem(GuiShopManager instance) {
		plugin = instance;
	}
	@Override
	public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
		if(!(src instanceof Player)) {
			src.sendMessage(plugin.getLocales().getLocalizedText(src.getLocale(), "Messages", "OnlyPlayer"));
		} else {
			Player player = (Player) src;
			ItemStack itemStack = null;
			if(player.getItemInHand(HandTypes.MAIN_HAND).isPresent()) {
				itemStack = player.getItemInHand(HandTypes.MAIN_HAND).get();
			} else if(player.getItemInHand(HandTypes.OFF_HAND).isPresent()) {
				itemStack = player.getItemInHand(HandTypes.OFF_HAND).get();
			}
			ItemStack toAdd = itemStack;
			boolean betPresent = args.<Double>getOne(Text.of("Bet")).isPresent();
			boolean pricePresent = args.<Double>getOne(Text.of("Price")).isPresent();
			boolean currencyPresent = args.<String>getOne(Text.of("Currency")).isPresent();
			if(currencyPresent) {
				for(Currency currency : plugin.getEconomyService().getCurrencies()) {
					if(currency.getId().equalsIgnoreCase(args.<String>getOne(Text.of("Currency")).get()) || currency.getName().equalsIgnoreCase(args.<String>getOne(Text.of("Currency")).get()) || currency.getSymbol().toPlain().equalsIgnoreCase(args.<String>getOne(Text.of("Currency")).get())) {
						currencyPresent = true;
						break;
					} else {
						currencyPresent = false;
					}
				}
			}
			Text currenciesList = Text.of(plugin.getEconomyService().getDefaultCurrency().getName(), ", ");
			if(!currencyPresent || (!plugin.getEconomy().checkCurrency(args.<String>getOne(Text.of("Currency")).get()).equals(plugin.getEconomyService().getDefaultCurrency()) && player.hasPermission(Permissions.currencyPermission(args.<String>getOne(Text.of("Currency")).get().toLowerCase())))) {
				int currentCurrency = 1;
				for(Currency currency : plugin.getEconomyService().getCurrencies()) {
					if(player.hasPermission(Permissions.currencyPermission(currency.getName())) && !currency.equals(plugin.getEconomyService().getDefaultCurrency())) {
						if(currentCurrency < plugin.getEconomyService().getCurrencies().size()) {
							currenciesList = Text.of(currenciesList, currency.getName(), ", ");
						} else {
							currenciesList = Text.of(currenciesList, currency.getName());
						}
					}
					currentCurrency++;
				}
			}
			if(itemStack == null || itemStack.getType() == ItemTypes.AIR) {
				player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ItemNotPresent"));
			} else {
				if(!betPresent && !pricePresent || (!pricePresent && args.<Double>getOne(Text.of("Bet")).get() <= 0) || (!betPresent && args.<Double>getOne(Text.of("Price")).get() <= 0) || (args.<Double>getOne(Text.of("Bet")).get() <= 0 && args.<Double>getOne(Text.of("Price")).get() <= 0)) {
					player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AuctionZeroOrNullPrices"));
				} else {
					if(!betPresent && pricePresent && args.<Double>getOne(Text.of("Price")).get() > 0) {
						if(!currencyPresent || (!plugin.getEconomy().checkCurrency(args.<String>getOne(Text.of("Currency")).get()).equals(plugin.getEconomyService().getDefaultCurrency()) && !player.hasPermission(Permissions.currencyPermission(args.<String>getOne(Text.of("Currency")).get().toLowerCase())))) {
							Text message = Text.builder().append(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AuctionBetNotPresent"))
									.onClick(TextActions.executeCallback(callback -> {
										SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().getDefaultCurrency());
										auctionPrice.setPrice(BigDecimal.valueOf(args.<Double>getOne(Text.of("Price")).get()));
										SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.getUniqueId(), player.getName(), time(), plugin.getRootNode().getNode("Auction", "Server").getString(), UUID.randomUUID());
										run(player, auctionStack);
									}))
									.build();
							player.sendMessage(message);
							Text message2 = Text.builder().append(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AuctionCurrencyNotPresent").replace("%currencies%", currenciesList))
									.onClick(TextActions.executeCallback(callback -> {
										SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().getDefaultCurrency());
										auctionPrice.setPrice(BigDecimal.valueOf(args.<Double>getOne(Text.of("Price")).get()));
										SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.getUniqueId(), player.getName(), time(), plugin.getRootNode().getNode("Auction", "Server").getString(), UUID.randomUUID());
										run(player, auctionStack);
									}))
									.build();
							player.sendMessage(message2);
						} else {
							Text message = Text.builder().append(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AuctionBetNotPresent"))
									.onClick(TextActions.executeCallback(callback -> {
										SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomy().checkCurrency(args.<String>getOne(Text.of("Currency")).get()));
										auctionPrice.setPrice(BigDecimal.valueOf(args.<Double>getOne(Text.of("Price")).get()));
										SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.getUniqueId(), player.getName(), time(), plugin.getRootNode().getNode("Auction", "Server").getString(), UUID.randomUUID());
										run(player, auctionStack);
									}))
									.build();
							player.sendMessage(message);
						}
					} else if(!pricePresent && betPresent && args.<Double>getOne(Text.of("Bet")).get() > 0) {
						Text message = Text.builder().append(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AuctionPriceNotPresent"))
								.onClick(TextActions.executeCallback(callback -> {
									SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().getDefaultCurrency());
									auctionPrice.setBet(BigDecimal.valueOf(args.<Double>getOne(Text.of("Bet")).get()));
									SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.getUniqueId(), player.getName(), time(), plugin.getRootNode().getNode("Auction", "Server").getString(), UUID.randomUUID());
									run(player, auctionStack);
								}))
								.build();
						player.sendMessage(message);
					} else if(args.<Double>getOne(Text.of("Price")).get() > 0 && args.<Double>getOne(Text.of("Bet")).get() > 0) {
						if(!currencyPresent || (!plugin.getEconomy().checkCurrency(args.<String>getOne(Text.of("Currency")).get()).equals(plugin.getEconomyService().getDefaultCurrency()) && !player.hasPermission(Permissions.currencyPermission(args.<String>getOne(Text.of("Currency")).get().toLowerCase())))) {
							Text message = Text.builder().append(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AuctionCurrencyNotPresent").replace("%currencies%", currenciesList))
									.onClick(TextActions.executeCallback(callback -> {
										SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().getDefaultCurrency());
										auctionPrice.setPrice(BigDecimal.valueOf(args.<Double>getOne(Text.of("Price")).get()));
										auctionPrice.setBet(BigDecimal.valueOf(args.<Double>getOne(Text.of("Bet")).get()));
										SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.getUniqueId(), player.getName(), time(), plugin.getRootNode().getNode("Auction", "Server").getString(), UUID.randomUUID());
										run(player, auctionStack);
									}))
									.build();
							player.sendMessage(message);
						} else {
							SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomy().checkCurrency(args.<String>getOne(Text.of("Currency")).get()));
							auctionPrice.setPrice(BigDecimal.valueOf(args.<Double>getOne(Text.of("Price")).get()));SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.getUniqueId(), player.getName(), time(), plugin.getRootNode().getNode("Auction", "Server").getString(), UUID.randomUUID());
							if(auctionPrice.getCurrency().getId().equals(plugin.getEconomyService().getDefaultCurrency().getId())) {
								auctionStack.getPrices().get(0).setBet(BigDecimal.valueOf(args.<Double>getOne(Text.of("Bet")).get()));
							} else {
								SerializedAuctionPrice betPrice = new SerializedAuctionPrice(plugin.getEconomyService().getDefaultCurrency());
								betPrice.setBet(BigDecimal.valueOf(args.<Double>getOne(Text.of("Bet")).get()));
								auctionStack.getPrices().add(betPrice);
							}
							run(player, auctionStack);
						}
					} else if(args.<Double>getOne(Text.of("Bet")).get() <= 0 && args.<Double>getOne(Text.of("Price")).get() > 0) {
						if(!currencyPresent || (!plugin.getEconomy().checkCurrency(args.<String>getOne(Text.of("Currency")).get()).equals(plugin.getEconomyService().getDefaultCurrency()) && !player.hasPermission(Permissions.currencyPermission(args.<String>getOne(Text.of("Currency")).get().toLowerCase())))) {
							Text message = Text.builder().append(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AuctionCurrencyNotPresent").replace("%currencies%", currenciesList))
									.onClick(TextActions.executeCallback(callback -> {
										SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().getDefaultCurrency());
										auctionPrice.setPrice(BigDecimal.valueOf(args.<Double>getOne(Text.of("Price")).get()));
										SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.getUniqueId(), player.getName(), time(), plugin.getRootNode().getNode("Auction", "Server").getString(), UUID.randomUUID());
										run(player, auctionStack);
									}))
									.build();
							player.sendMessage(message);
						} else {
							SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomy().checkCurrency(args.<String>getOne(Text.of("Currency")).get()));
							auctionPrice.setPrice(BigDecimal.valueOf(args.<Double>getOne(Text.of("Price")).get()));
							SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.getUniqueId(), player.getName(), time(), plugin.getRootNode().getNode("Auction", "Server").getString(), UUID.randomUUID());
							run(player, auctionStack);
						}
					} else if(args.<Double>getOne(Text.of("Bet")).get() > 0 && args.<Double>getOne(Text.of("Price")).get() <= 0) {
						SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().getDefaultCurrency());
						auctionPrice.setBet(BigDecimal.valueOf(args.<Double>getOne(Text.of("Bet")).get()));
						SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.getUniqueId(), player.getName(), time(), plugin.getRootNode().getNode("Auction", "Server").getString(), UUID.randomUUID());
						run(player, auctionStack);
					}
				}
			}
		}
		return CommandResult.success();
	}

	private void run(Player player, SerializedAuctionStack auctionStack) {
		if(plugin.maskIsBlackList(auctionStack.getSerializedItemStack().getType()) || plugin.itemIsBlackList(auctionStack.getSerializedItemStack().getItemStack())) {
			player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ItemBlocked"));
			return;
		}
		if(player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(auctionStack.getSerializedItemStack().getItemStack())).totalItems() < auctionStack.getSerializedItemStack().getQuantity()) {
			player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ItemNotPresent"));
			return;
		}
		if(checkNbnLength(auctionStack)) {
			player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "LongNBT"));
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
		plugin.getAuctionItems().add(auctionStack);
		Task.builder().async().execute(() -> {
			plugin.getAuctionWorkData().saveAuctionStack(auctionStack);
		}).submit(plugin);
		player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(auctionStack.getSerializedItemStack().getItemStack())).poll(auctionStack.getSerializedItemStack().getQuantity());
		player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AuctionItemAdded"));
	}

	private boolean checkNbnLength(SerializedAuctionStack auctionStack) {
		return auctionStack.getSerializedItemStack().getNBT().length() > plugin.getRootNode().getNode("Auction", "NbtLimit").getInt();
	}

	private long time() {
		return plugin.getExpire(0).getTime() + System.currentTimeMillis();
	}

}
