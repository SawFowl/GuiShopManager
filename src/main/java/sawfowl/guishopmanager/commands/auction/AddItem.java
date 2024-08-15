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

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.commandpack.api.data.command.Settings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractPlayerCommand;
import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionPrice;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;

public class AddItem extends AbstractPlayerCommand {

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
		if(itemStack == null || itemStack.type() == ItemTypes.AIR.get()) exception(plugin.getLocales().getLocale(locale).commands().exceptions().itemNotPresent());
		ItemStack toAdd = itemStack;
		boolean betPresent = context.one(CommandParameters.AUCTION_BET).isPresent();
		boolean pricePresent = context.one(CommandParameters.AUCTION_PRICE).isPresent();
		boolean currencyPresent = context.one(CommandParameters.AUCTION_CURRENCY).isPresent();
		if(!betPresent) exception(plugin.getLocales().getLocale(locale).commands().exceptions().zeroOrNullPrices());
		double bet = context.one(CommandParameters.AUCTION_BET).get();
		double price = context.one(CommandParameters.AUCTION_PRICE).orElse(0d);
		if(bet == 0 && price == 0) exception(plugin.getLocales().getLocale(locale).commands().exceptions().zeroOrNullPrices());
		if(!pricePresent || price == 0) {
			if(currencyPresent) {
				Component message = getCommands(locale).auction().priceNotPresent().clickEvent(SpongeComponents.executeCallback(cause -> {
					SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(context.one(CommandParameters.AUCTION_CURRENCY).get());
					auctionPrice.setBet(BigDecimal.valueOf(bet));
					SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getConfig().getAuction().getServer());
					run(player, auctionStack);
				}));
				player.sendMessage(message);
			} else {
				Component currenciesList = Component.text().append(plugin.getEconomyService().defaultCurrency().displayName()).build();
				if(!currencyPresent) {
					int currentCurrency = 1;
					for(Currency currency : plugin.getEconomy().getCurrencies()) {
						if(Permissions.auctionCurrencyPermission(player, currency, false) && !currency.equals(plugin.getEconomyService().defaultCurrency())) {
							if(currentCurrency < plugin.getEconomy().getCurrencies().size()) {
								currenciesList = currenciesList.append(Component.text(", ")).append(currency.displayName());
							} else currenciesList = currenciesList.append(currency.displayName()).append(Component.text("."));
						}
						currentCurrency++;
					}
				}
				Component message = getCommands(locale).auction().currencyNotPresent(currenciesList).clickEvent(SpongeComponents.executeCallback(cause -> {
					player.sendMessage(getCommands(locale).auction().priceNotPresent().clickEvent(SpongeComponents.executeCallback(cause2 -> {
						SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency());
						auctionPrice.setBet(BigDecimal.valueOf(bet));
						SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getConfig().getAuction().getServer());
						run(player, auctionStack);
					})));
				}));
				player.sendMessage(message);
			}
		} else if(!currencyPresent) {
			Component currenciesList = Component.text().append(plugin.getEconomyService().defaultCurrency().displayName()).build();
			if(!currencyPresent) {
				int currentCurrency = 1;
				for(Currency currency : plugin.getEconomy().getCurrencies()) {
					if(Permissions.auctionCurrencyPermission(player, currency, false) && !currency.equals(plugin.getEconomyService().defaultCurrency())) {
						if(currentCurrency < plugin.getEconomy().getCurrencies().size()) {
							currenciesList = currenciesList.append(Component.text(", ")).append(currency.displayName());
						} else currenciesList = currenciesList.append(currency.displayName()).append(Component.text("."));
					}
					currentCurrency++;
				}
			}
			Component message = getCommands(locale).auction().currencyNotPresent(currenciesList).clickEvent(SpongeComponents.executeCallback(cause -> {
				SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency());
				auctionPrice.setPrice(BigDecimal.valueOf(context.one(CommandParameters.AUCTION_PRICE).get()));
				SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getConfig().getAuction().getServer());
				run(player, auctionStack);
			}));
			player.sendMessage(message);
		} else {
			SerializedAuctionPrice auctionPrice = new SerializedAuctionPrice(context.one(CommandParameters.AUCTION_CURRENCY).get());
			auctionPrice.setPrice(BigDecimal.valueOf(price));
			auctionPrice.setBet(BigDecimal.valueOf(bet));
			SerializedAuctionStack auctionStack = new SerializedAuctionStack(toAdd, Arrays.asList(auctionPrice), player.uniqueId(), player.name(), time(), plugin.getConfig().getAuction().getServer());
			run(player, auctionStack);
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
			ParameterSettings.of(CommandParameters.AUCTION_BET, true, locale -> getExceptions(locale).zeroOrNullPrices()),
			ParameterSettings.of(CommandParameters.AUCTION_PRICE, true, locale -> getExceptions(locale).zeroOrNullPrices()),
			ParameterSettings.of(CommandParameters.AUCTION_CURRENCY, true, locale -> getExceptions(locale).currencyNotPresent())
		);
	}

	@Override
	public Settings applyCommandSettings() {
		return null;
	}

	private void run(ServerPlayer player, SerializedAuctionStack auctionStack) {
		if(player.inventory().query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY.get().of(auctionStack.getSerializedItemStack().getItemStack())).totalQuantity() < auctionStack.getSerializedItemStack().getQuantity()) {
			player.sendMessage(getExceptions(player).itemNotPresent());
			return;
		}
		if(plugin.maskIsBlackList(auctionStack.getSerializedItemStack().getItemTypeAsString()) || plugin.itemIsBlackList(auctionStack.getSerializedItemStack().getItemStack())) {
			player.sendMessage(plugin.getLocales().getLocale(player).messages().auction().itemBlocked());
			return;
		}
		if(checkNbtLength(auctionStack)) {
			player.sendMessage(plugin.getLocales().getLocale(player).messages().auction().longComponents());
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
		player.sendMessage(plugin.getLocales().getLocale(player).messages().auction().itemAdded());
	}

	private boolean checkNbtLength(SerializedAuctionStack auctionStack) {
		if(auctionStack.getSerializedItemStack().getComponentsAsJson() == null) return auctionStack.getSerializedItemStack().getComponentsAsString().length() > plugin.getConfig().getAuction().getComponentLimit();
		return auctionStack.getSerializedItemStack().getComponentsAsJson().toString().length() > plugin.getConfig().getAuction().getComponentLimit();
	}

	private long time() {
		return plugin.getExpire(0).getTime() + System.currentTimeMillis();
	}

}
