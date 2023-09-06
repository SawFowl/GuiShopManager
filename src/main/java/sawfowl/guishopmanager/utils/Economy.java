package sawfowl.guishopmanager.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.registry.RegistryTypes;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.service.economy.transaction.TransferResult;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionPrice;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.localeapi.serializetools.SerializedItemStack;

public class Economy {

	GuiShopManager plugin;
	public Economy(GuiShopManager instance) {
		plugin = instance;
	}

	public BigDecimal getPlayerBalance(UUID uuid, Currency currency) {
		try {
			Optional<UniqueAccount> uOpt = plugin.getEconomyService().findOrCreateAccount(uuid);
			if (uOpt.isPresent()) {
				return uOpt.get().balance(currency);
			}
		} catch (Exception e) {
		}
		return BigDecimal.ZERO;
	}

	public boolean checkPlayerBalance(UUID uuid, Currency currency, BigDecimal money) {
		return getPlayerBalance(uuid, currency).doubleValue() >= money.doubleValue();
	}

	public boolean addToPlayerBalance(Player player, Currency currency, BigDecimal money, ItemStack itemStack) {
		try {
			Optional<UniqueAccount> uOpt = plugin.getEconomyService().findOrCreateAccount(player.uniqueId());
			if (uOpt.isPresent()) {
				money = money.multiply(BigDecimal.valueOf(itemStack.quantity()));
				TransactionResult result = uOpt.get().deposit(currency, money);
				if (result.result() == ResultType.SUCCESS) {
					if(plugin.getRootNode().node("PlayerTransactionMessage").getBoolean()) {
						player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ItemSell")
								.replaceText(TextReplacementConfig.builder().match("%item%").replacement(getStackComponent(itemStack)).build())
								.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(String.valueOf(itemStack.quantity())).build())
								.replaceText(TextReplacementConfig.builder().match("%added%").replacement(Component.text().append(currency.symbol()).append(Component.text(money.doubleValue()))).build())
								.replaceText(TextReplacementConfig.builder().match("%balance%").replacement(Component.text().append(currency.symbol()).append(Component.text(getPlayerBalance(player.uniqueId(), currency).doubleValue()))).build()));
					}
					if(plugin.getRootNode().node("Debug").getBoolean()) {
						plugin.getLogger().info(TextUtils.clearDecorations(plugin.getLocales().getComponent(player.locale(), "Debug", "InfoTakeItems")
								.replaceText(TextReplacementConfig.builder().match("%item%").replacement(getStackComponent(itemStack)).build())
								.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(String.valueOf(itemStack.quantity())).build())
								.replaceText(TextReplacementConfig.builder().match("%player%").replacement(player.name()).build())
								.replaceText(TextReplacementConfig.builder().match("%added%").replacement(Component.text().append(currency.symbol()).append(Component.text(money.doubleValue()))).build())
								.replaceText(TextReplacementConfig.builder().match("%balance%").replacement(Component.text().append(currency.symbol()).append(Component.text(getPlayerBalance(player.uniqueId(), currency).doubleValue()))).build())));
					}
					return true;
				} else if ((result.result() == ResultType.FAILED || result.result() == ResultType.ACCOUNT_NO_FUNDS) && plugin.getRootNode().node("Debug").getBoolean()) {
					plugin.getLogger().error(TextUtils.clearDecorations(plugin.getLocales().getComponent(player.locale(), "Debug", "ErrorGiveMoney")
								.replaceText(TextReplacementConfig.builder().match("%player%").replacement(player.name()).build())));
				} else {
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
		}
		return false;
	}

	public boolean buyCommands(Player player, Currency currency, BigDecimal money) {
		if(!checkPlayerBalance(player.uniqueId(), currency, money)) return false;
		try {
			Optional<UniqueAccount> uOpt = plugin.getEconomyService().findOrCreateAccount(player.uniqueId());
			if (uOpt.isPresent()) {
				TransactionResult result = uOpt.get().withdraw(currency, money);
				if (result.result() == ResultType.SUCCESS) {
					return true;
				} else if ((result.result() == ResultType.FAILED || result.result() == ResultType.ACCOUNT_NO_FUNDS) && plugin.getRootNode().node("Debug").getBoolean()) {
					plugin.getLogger().error(TextUtils.clearDecorations(plugin.getLocales().getComponent(player.locale(), "Messages", "BuyCommands")
							.replaceText(TextReplacementConfig.builder().match("%player%").replacement(player.name()).build())));
				} else {
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		return false;
	}

	public boolean removeFromPlayerBalance(Player player, Currency currency, BigDecimal money, ItemStack itemStack) {
		try {
			Optional<UniqueAccount> uOpt = plugin.getEconomyService().findOrCreateAccount(player.uniqueId());
			if (uOpt.isPresent()) {
				money = money.multiply(BigDecimal.valueOf(itemStack.quantity()));
				TransactionResult result = uOpt.get().withdraw(currency, money);
				if (result.result() == ResultType.SUCCESS) {
					if(plugin.getRootNode().node("PlayerTransactionMessage").getBoolean()) {
						player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ItemBuy")
								.replaceText(TextReplacementConfig.builder().match("%item%").replacement(getStackComponent(itemStack)).build())
								.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(String.valueOf(itemStack.quantity())).build())
								.replaceText(TextReplacementConfig.builder().match("%removed%").replacement(Component.text().append(currency.symbol()).append(Component.text(money.doubleValue()))).build())
								.replaceText(TextReplacementConfig.builder().match("%balance%").replacement(Component.text().append(currency.symbol()).append(Component.text(getPlayerBalance(player.uniqueId(), currency).doubleValue()))).build()));
					}
					if(plugin.getRootNode().node("Debug").getBoolean()) {
						plugin.getLogger().info(TextUtils.clearDecorations(plugin.getLocales().getComponent(player.locale(), "Debug", "InfoGiveItems")
								.replaceText(TextReplacementConfig.builder().match("%item%").replacement(getStackComponent(itemStack)).build())
								.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(String.valueOf(itemStack.quantity())).build())
								.replaceText(TextReplacementConfig.builder().match("%player%").replacement(player.name()).build())
								.replaceText(TextReplacementConfig.builder().match("%removed%").replacement(Component.text().append(currency.symbol()).append(Component.text(money.doubleValue()))).build())
								.replaceText(TextReplacementConfig.builder().match("%balance%").replacement(Component.text().append(currency.symbol()).append(Component.text(getPlayerBalance(player.uniqueId(), currency).doubleValue()))).build())));
					}
					return true;
				} else if ((result.result() == ResultType.FAILED || result.result() == ResultType.ACCOUNT_NO_FUNDS) && plugin.getRootNode().node("Debug").getBoolean()) {
					plugin.getLogger().error(TextUtils.clearDecorations(plugin.getLocales().getComponent(player.locale(), "Debug", "ErrorTakeMoney")
							.replaceText(TextReplacementConfig.builder().match("%player%").replacement(player.name()).build())));
				} else {
				}
				}
			} catch (Exception e) {
				e.printStackTrace();
		}
		return false;
	}

	public boolean auctionTransaction(UUID buyerUUID, SerializedAuctionStack auctionItem, int priceNumber, boolean isBet) {
		try {
			Optional<UniqueAccount> bOpt = plugin.getEconomyService().findOrCreateAccount(buyerUUID);
			Optional<UniqueAccount> sOpt = plugin.getEconomyService().findOrCreateAccount(auctionItem.getOwnerUUID());
			SerializedAuctionPrice serializedAuctionPrice = auctionItem.getPrices().get(priceNumber);
			BigDecimal money = (isBet ? auctionItem.getPrices().get(0).getBet() : serializedAuctionPrice.getPrice()).setScale(2);
			Currency currency = isBet ? auctionItem.getPrices().get(0).getCurrency() : serializedAuctionPrice.getCurrency();
			ItemStack itemStack = auctionItem.getSerializedItemStack().getItemStack();
			boolean isTax = isBet ? auctionItem.getPrices().get(priceNumber).getBet().doubleValue() <= 100 : auctionItem.getPrices().get(priceNumber).getPrice().doubleValue() <= 100;
			BigDecimal tax = BigDecimal.valueOf(isTax ? (isBet ? auctionItem.getBetData().getTax() : auctionItem.getPrices().get(priceNumber).getTax()) : 0).setScale(2, RoundingMode.HALF_UP);
			if(tax.doubleValue() < 0.01) tax = BigDecimal.ZERO;
			money = money.multiply(BigDecimal.valueOf(itemStack.quantity()));
			if(!bOpt.isPresent() || !sOpt.isPresent()) return false;
			if(isTax) money = money.subtract(tax);
			if(!checkPlayerBalance(buyerUUID, currency, money)) return false;
			TransferResult transferResult = sOpt.get().transfer(bOpt.get(), currency, money);
			if(plugin.getRootNode().node("Debug").getBoolean()) {
				plugin.getLogger().info(TextUtils.clearDecorations(plugin.getLocales().getDefaultLocale().getComponent(false, "Debug", "InfoTakeItems")
						.replaceText(TextReplacementConfig.builder().match("%item%").replacement(getStackComponent(itemStack)).build())
						.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(String.valueOf(itemStack.quantity())).build())
						.replaceText(TextReplacementConfig.builder().match("%player%").replacement(bOpt.get().identifier()).build())
						.replaceText(TextReplacementConfig.builder().match("%added%").replacement(Component.text().append(currency.symbol()).append(Component.text(money.doubleValue()))).build())
						.replaceText(TextReplacementConfig.builder().match("%balance%").replacement(Component.text().append(currency.symbol()).append(Component.text(getPlayerBalance(buyerUUID, currency).doubleValue()))).build())));
			}
			if(transferResult.result() == ResultType.SUCCESS) {
				if(plugin.getRootNode().node("PlayerTransactionMessage").getBoolean()) {
					Sponge.server().player(buyerUUID).ifPresent(buyer -> {
						buyer.sendMessage(plugin.getLocales().getComponent(buyer.locale(), "Messages", "AuctionBuy")
								.replaceText(TextReplacementConfig.builder().match("%item%").replacement(getStackComponent(itemStack)).build())
								.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(String.valueOf(itemStack.quantity())).build())
								.replaceText(TextReplacementConfig.builder().match("%removed%").replacement(Component.text().append(currency.symbol()).append(Component.text(transferResult.amount().doubleValue()))).build())
								.replaceText(TextReplacementConfig.builder().match("%balance%").replacement(Component.text().append(currency.symbol()).append(Component.text(getPlayerBalance(buyerUUID, currency).doubleValue()))).build())
								.replaceText(TextReplacementConfig.builder().match("%seller%").replacement(auctionItem.getOwnerName()).build()));
					});
				}
				double finalTax = tax.doubleValue();
				Sponge.server().player(auctionItem.getOwnerUUID()).ifPresent(seller -> {
					seller.sendMessage(plugin.getLocales().getComponent(seller.locale(), "Messages", "AuctionSell")
							.replaceText(TextReplacementConfig.builder().match("%item%").replacement(getStackComponent(itemStack)).build())
							.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(String.valueOf(itemStack.quantity())).build())
							.replaceText(TextReplacementConfig.builder().match("%added%").replacement(Component.text().append(currency.symbol()).append(Component.text(transferResult.amount().doubleValue()))).build())
							.replaceText(TextReplacementConfig.builder().match("%balance%").replacement(Component.text().append(currency.symbol()).append(Component.text(getPlayerBalance(seller.uniqueId(), currency).doubleValue()))).build())
							.replaceText(TextReplacementConfig.builder().match("%buyer%").replacement(bOpt.get().identifier()).build()));
					if(isTax && finalTax > 0) {
						seller.sendMessage(plugin.getLocales().getComponent(seller.locale(), "Messages", "Tax")
								.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(Component.text().append(currency.symbol()).append(Component.text(finalTax))).build()));
					}
				});
				return true;
			} else if((transferResult.result() == ResultType.FAILED || transferResult.result() == ResultType.ACCOUNT_NO_FUNDS) && plugin.getRootNode().node("Debug").getBoolean()) {
				plugin.getLogger().error(TextUtils.clearDecorations(plugin.getLocales().getDefaultLocale().getComponent(false, "Debug", "ErrorTakeMoney")
						.replaceText(TextReplacementConfig.builder().match("%player%").replacement(bOpt.get().identifier()).build())));
			}
		} catch (Exception e) {
		}
		return false;
	}

	public boolean fee(Player player, Currency currency, BigDecimal money) {
		if(money.doubleValue() > 0) {
			if(money.doubleValue() > getPlayerBalance(player.uniqueId(), currency).doubleValue()) {
				player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "NoMoneyForFee"));
				return false;
			} else {
				try {
					Optional<UniqueAccount> uOpt = plugin.getEconomyService().findOrCreateAccount(player.uniqueId());
					if (uOpt.isPresent()) {
						uOpt.get().withdraw(currency, money, Cause.of(plugin.getEventContext(), plugin.getPluginContainer()));
						return true;
					}
				} catch (Exception e) {
				}
			}
		} else {
		}
		return false;
	}

	public Currency checkCurrency(String check) {
		Currency result = plugin.getEconomyService().defaultCurrency();
		for(Currency currency : getCurrencies()) {
			if(TextUtils.clearDecorations(TextUtils.clearDecorations(currency.displayName())).equalsIgnoreCase(TextUtils.clearDecorations(check)) || TextUtils.clearDecorations(TextUtils.clearDecorations(currency.symbol())).equalsIgnoreCase(TextUtils.clearDecorations(check))) {
				result = currency;
				break;
			}
		}
		return result;
	}

	public List<Currency> getCurrencies() {
		List<Currency> currencies = new ArrayList<Currency>();
		Sponge.game().findRegistry(RegistryTypes.CURRENCY).ifPresent(registry -> {
			if(registry.stream().count() > 0) currencies.addAll(registry.stream().collect(Collectors.toList()));
		});
		return !currencies.isEmpty() ? currencies : Arrays.asList(plugin.getEconomyService().defaultCurrency());
	}

	private Component getStackComponent(ItemStack itemStack) {
		return itemStack.type().asComponent().hoverEvent(HoverEvent.showItem((new SerializedItemStack(itemStack)).getItemKey(), itemStack.quantity()));
	}

}
