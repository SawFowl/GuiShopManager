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

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionPrice;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
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

	public void addToPlayerBalance(Player player, Currency currency, BigDecimal money, ItemStack itemStack) {
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
                		plugin.getLogger().info(plugin.getLocales().getComponent(player.locale(), "Debug", "InfoTakeItems")
                				.replaceText(TextReplacementConfig.builder().match("%item%").replacement(getStackComponent(itemStack)).build())
                    			.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(String.valueOf(itemStack.quantity())).build())
                				.replaceText(TextReplacementConfig.builder().match("%player%").replacement(player.name()).build())
                				.replaceText(TextReplacementConfig.builder().match("%added%").replacement(Component.text().append(currency.symbol()).append(Component.text(money.doubleValue()))).build())
                				.replaceText(TextReplacementConfig.builder().match("%balance%").replacement(Component.text().append(currency.symbol()).append(Component.text(getPlayerBalance(player.uniqueId(), currency).doubleValue()))).build()));
                	}
                } else if ((result.result() == ResultType.FAILED || result.result() == ResultType.ACCOUNT_NO_FUNDS) && plugin.getRootNode().node("Debug").getBoolean()) {
                	plugin.getLogger().error(plugin.getLocales().getComponent(player.locale(), "Debug", "ErrorGiveMoney")
                				.replaceText(TextReplacementConfig.builder().match("%player%").replacement(player.name()).build()));
                } else {
                }
            	}
        	} catch (Exception e) {
        		e.printStackTrace();
        }
	}

	public void buyCommands(Player player, Currency currency, BigDecimal money) {
        try {
            Optional<UniqueAccount> uOpt = plugin.getEconomyService().findOrCreateAccount(player.uniqueId());
            if (uOpt.isPresent()) {
                TransactionResult result = uOpt.get().withdraw(currency, money);
                if (result.result() == ResultType.SUCCESS) {
                } else if ((result.result() == ResultType.FAILED || result.result() == ResultType.ACCOUNT_NO_FUNDS) && plugin.getRootNode().node("Debug").getBoolean()) {
                	plugin.getLogger().error(plugin.getLocales().getComponent(player.locale(), "Messages", "BuyCommands")
            				.replaceText(TextReplacementConfig.builder().match("%player%").replacement(player.name()).build()));
                } else {
                }
            	}
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }

	public void removeFromPlayerBalance(Player player, Currency currency, BigDecimal money, ItemStack itemStack) {
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
                		plugin.getLogger().info(plugin.getLocales().getComponent(player.locale(), "Debug", "InfoGiveItems")
                				.replaceText(TextReplacementConfig.builder().match("%item%").replacement(getStackComponent(itemStack)).build())
                    			.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(String.valueOf(itemStack.quantity())).build())
                				.replaceText(TextReplacementConfig.builder().match("%player%").replacement(player.name()).build())
                				.replaceText(TextReplacementConfig.builder().match("%removed%").replacement(Component.text().append(currency.symbol()).append(Component.text(money.doubleValue()))).build())
                				.replaceText(TextReplacementConfig.builder().match("%balance%").replacement(Component.text().append(currency.symbol()).append(Component.text(getPlayerBalance(player.uniqueId(), currency).doubleValue()))).build()));
                	}
                } else if ((result.result() == ResultType.FAILED || result.result() == ResultType.ACCOUNT_NO_FUNDS) && plugin.getRootNode().node("Debug").getBoolean()) {
                	plugin.getLogger().error(plugin.getLocales().getComponent(player.locale(), "Debug", "ErrorTakeMoney")
            				.replaceText(TextReplacementConfig.builder().match("%player%").replacement(player.name()).build()));
                } else {
                }
            	}
        	} catch (Exception e) {
        		e.printStackTrace();
        }
	}

	public void auctionTransaction(UUID buyerUUID, SerializedAuctionStack auctionItem, int priceNumber, boolean isBet) {
        try {
            Optional<UniqueAccount> bOpt = plugin.getEconomyService().findOrCreateAccount(buyerUUID);
            Optional<UniqueAccount> sOpt = plugin.getEconomyService().findOrCreateAccount(auctionItem.getOwnerUUID());
            SerializedAuctionPrice serializedAuctionPrice = auctionItem.getPrices().get(priceNumber);
            BigDecimal money = (isBet ? auctionItem.getPrices().get(0).getBet() : serializedAuctionPrice.getPrice()).setScale(2);
            Currency currency = isBet ? auctionItem.getPrices().get(0).getCurrency() : serializedAuctionPrice.getCurrency();
            ItemStack itemStack = auctionItem.getSerializedItemStack().getItemStack();
            boolean isTax = isBet ? auctionItem.getPrices().get(priceNumber).getBet().doubleValue() <= 100 : auctionItem.getPrices().get(priceNumber).getPrice().doubleValue() <= 100;
            BigDecimal tax = BigDecimal.valueOf(isTax ? (isBet ? auctionItem.getBetData().getTax() : auctionItem.getPrices().get(priceNumber).getTax()) : 0).setScale(2, RoundingMode.HALF_UP);
        	if(tax.doubleValue() < 0.01) {
        		tax = BigDecimal.ZERO;
        	}
        	money = money.multiply(BigDecimal.valueOf(itemStack.quantity()));
			Player buyer = Sponge.server().player(buyerUUID).get();
        	if(bOpt.isPresent()) {
                TransactionResult resultRemove = bOpt.get().withdraw(currency, money);
                if(resultRemove.result() == ResultType.SUCCESS) {
                	if(plugin.getRootNode().node("PlayerTransactionMessage").getBoolean()) {
            			buyer.sendMessage(plugin.getLocales().getComponent(buyer.locale(), "Messages", "AuctionBuy")
                    			.replaceText(TextReplacementConfig.builder().match("%item%").replacement(getStackComponent(itemStack)).build())
                    			.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(String.valueOf(itemStack.quantity())).build())
                    			.replaceText(TextReplacementConfig.builder().match("%removed%").replacement(Component.text().append(currency.symbol()).append(Component.text(money.doubleValue()))).build())
                    			.replaceText(TextReplacementConfig.builder().match("%balance%").replacement(Component.text().append(currency.symbol()).append(Component.text(getPlayerBalance(buyerUUID, currency).doubleValue()))).build())
                    			.replaceText(TextReplacementConfig.builder().match("%seller%").replacement(auctionItem.getOwnerName()).build()));
                	}
                } else if((resultRemove.result() == ResultType.FAILED || resultRemove.result() == ResultType.ACCOUNT_NO_FUNDS) && plugin.getRootNode().node("Debug").getBoolean()) {
                	plugin.getLogger().error(plugin.getLocales().getDefaultLocale().getComponent(false, "Debug", "ErrorTakeMoney")
            				.replaceText(TextReplacementConfig.builder().match("%player%").replacement(buyer.name()).build()));
            	}
        	}
            if(sOpt.isPresent()) {
                TransactionResult resultAdd;
        		if(isTax) {
                    resultAdd = sOpt.get().deposit(currency, money.subtract(tax));
        		} else {
                    resultAdd = sOpt.get().deposit(currency, money);
        		}
        		if(resultAdd.result() == ResultType.SUCCESS) {
                	if(plugin.getRootNode().node("PlayerTransactionMessage").getBoolean()) {
                		if(Sponge.server().player(buyerUUID).isPresent()) {
                        	if(Sponge.server().player(auctionItem.getOwnerUUID()).isPresent()) {
                        		Player seller = Sponge.server().player(auctionItem.getOwnerUUID()).get();
                        		seller.sendMessage(plugin.getLocales().getComponent(seller.locale(), "Messages", "AuctionSell")
                            			.replaceText(TextReplacementConfig.builder().match("%item%").replacement(getStackComponent(itemStack)).build())
                            			.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(String.valueOf(itemStack.quantity())).build())
                            			.replaceText(TextReplacementConfig.builder().match("%added%").replacement(Component.text().append(currency.symbol()).append(Component.text(money.doubleValue()))).build())
                            			.replaceText(TextReplacementConfig.builder().match("%balance%").replacement(Component.text().append(currency.symbol()).append(Component.text(getPlayerBalance(seller.uniqueId(), currency).doubleValue()))).build())
                            			.replaceText(TextReplacementConfig.builder().match("%buyer%").replacement(buyer.name()).build()));
                        		if(isTax && tax.doubleValue() > 0) {
                        			seller.sendMessage(plugin.getLocales().getComponent(seller.locale(), "Messages", "Tax")
                        					.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(Component.text().append(currency.symbol()).append(Component.text(tax.doubleValue()))).build()));
                        		}
                        	}
                		}
                	}
                } else if (((resultAdd.result() == ResultType.FAILED || resultAdd.result() == ResultType.ACCOUNT_NO_FUNDS)) && plugin.getRootNode().node("Debug").getBoolean()) {
                	plugin.getLogger().error(plugin.getLocales().getDefaultLocale().getComponent(false, "Debug", "ErrorGiveMoney")
            				.replaceText(TextReplacementConfig.builder().match("%player%").replacement(buyer.name()).build()));
                }
            }
        	if(plugin.getRootNode().node("Debug").getBoolean()) {
        		plugin.getLogger().info(plugin.getLocales().getDefaultLocale().getComponent(false, "Debug", "InfoTakeItems")
        				.replaceText(TextReplacementConfig.builder().match("%item%").replacement(getStackComponent(itemStack)).build())
            			.replaceText(TextReplacementConfig.builder().match("%amount%").replacement(String.valueOf(itemStack.quantity())).build())
        				.replaceText(TextReplacementConfig.builder().match("%player%").replacement(buyer.name()).build())
        				.replaceText(TextReplacementConfig.builder().match("%added%").replacement(Component.text().append(currency.symbol()).append(Component.text(money.doubleValue()))).build())
        				.replaceText(TextReplacementConfig.builder().match("%balance%").replacement(Component.text().append(currency.symbol()).append(Component.text(getPlayerBalance(buyerUUID, currency).doubleValue()))).build()));
        	}
        } catch (Exception e) {
        }
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
			if(toPlain(currency.displayName()).equalsIgnoreCase(check) || toPlain(currency.symbol()).equalsIgnoreCase(check)) {
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

	private String toPlain(Component component) {
		component.decorations().clear();
		return LegacyComponentSerializer.legacyAmpersand().serialize(component);
	}

	private Component getStackComponent(ItemStack itemStack) {
		return itemStack.type().asComponent().hoverEvent(HoverEvent.showItem((new SerializedItemStack(itemStack)).getItemKey(), itemStack.quantity()));
	}

}
