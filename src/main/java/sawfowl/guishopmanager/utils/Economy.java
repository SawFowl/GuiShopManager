package sawfowl.guishopmanager.utils;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.service.economy.account.UniqueAccount;
import org.spongepowered.api.service.economy.transaction.ResultType;
import org.spongepowered.api.service.economy.transaction.TransactionResult;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.serialization.auction.SerializedAuctionPrice;
import sawfowl.guishopmanager.utils.serialization.auction.SerializedAuctionStack;

public class Economy {

	GuiShopManager plugin;
	public Economy(GuiShopManager instance) {
		plugin = instance;
	}

	public BigDecimal getPlayerBalance(UUID uuid, Currency currency) {
        try {
            Optional<UniqueAccount> uOpt = plugin.getEconomyService().getOrCreateAccount(uuid);
            if (uOpt.isPresent()) {
                return uOpt.get().getBalance(currency);
            }
        } catch (Exception ignored) {
        }
        return BigDecimal.ZERO;
	}

	public boolean checkPlayerBalance(UUID uuid, Currency currency, BigDecimal money) {
		return getPlayerBalance(uuid, currency).doubleValue() >= money.doubleValue();
	}

	public void addToPlayerBalance(Player player, Currency currency, BigDecimal money, ItemStack itemStack) {
        try {
            Optional<UniqueAccount> uOpt = plugin.getEconomyService().getOrCreateAccount(player.getUniqueId());
            if (uOpt.isPresent()) {
            	money = money.multiply(BigDecimal.valueOf(itemStack.getQuantity()));
                TransactionResult result = uOpt.get().deposit(currency, money, Cause.of(plugin.getEventContext(), plugin.getContainer()));
                if (result.getResult() == ResultType.SUCCESS) {
                	if(plugin.getRootNode().getNode("PlayerTransactionMessage").getBoolean()) {
                    	player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ItemSell")
                    			.replace("%item%", Text.builder().append(Text.of(itemStack)).onHover(TextActions.showItem(itemStack.createSnapshot())).build())
                    			.replace("%amount%", Text.of(itemStack.getQuantity()))
                    			.replace("%added%", Text.of(money))
                    			.replace("%balance%", Text.of(getPlayerBalance(player.getUniqueId(), currency))));
                	}
                	if(plugin.getRootNode().getNode("Debug").getBoolean()) {
                		plugin.getLogger().info(plugin.getLocales().getLocalizedText(player.getLocale(), "Debug", "InfoTakeItems")
                				.replace("%item%", Text.of(itemStack))
                    			.replace("%amount%", Text.of(itemStack.getQuantity()))
                				.replace("%player%", Text.of(player.getName()))
                				.replace("%added%", Text.of(money))
                				.replace("%balance%", Text.of(getPlayerBalance(player.getUniqueId(), currency))).toPlain());
                	}
                } else if ((result.getResult() == ResultType.FAILED || result.getResult() == ResultType.ACCOUNT_NO_FUNDS) && plugin.getRootNode().getNode("Debug").getBoolean()) {
                	plugin.getLogger().error(plugin.getLocales().getLocalizedText(player.getLocale(), "Debug", "ErrorGiveMoney")
                				.replace("%player%", Text.of(player.getName())).toPlain());
                } else {
                }
            	}
        	} catch (Exception ignored) {
        }
	}

	public void removeFromPlayerBalance(Player player, Currency currency, BigDecimal money, ItemStack itemStack) {
        try {
            Optional<UniqueAccount> uOpt = plugin.getEconomyService().getOrCreateAccount(player.getUniqueId());
            if (uOpt.isPresent()) {
            	money = money.multiply(BigDecimal.valueOf(itemStack.getQuantity()));
                TransactionResult result = uOpt.get().withdraw(currency, money, Cause.of(plugin.getEventContext(), plugin.getContainer()));
                if (result.getResult() == ResultType.SUCCESS) {
                	if(plugin.getRootNode().getNode("PlayerTransactionMessage").getBoolean()) {
                    	player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ItemBuy")
                    			.replace("%item%", Text.builder().append(Text.of(itemStack)).onHover(TextActions.showItem(itemStack.createSnapshot())).build())
                    			.replace("%amount%", Text.of(itemStack.getQuantity()))
                    			.replace("%removed%", Text.of(money))
                    			.replace("%balance%", Text.of(getPlayerBalance(player.getUniqueId(), currency))));
                	}
                	if(plugin.getRootNode().getNode("Debug").getBoolean()) {
                		plugin.getLogger().info(plugin.getLocales().getLocalizedText(player.getLocale(), "Debug", "InfoGiveItems")
                				.replace("%item%", Text.of(itemStack))
                    			.replace("%amount%", Text.of(itemStack.getQuantity()))
                				.replace("%player%", Text.of(player.getName()))
                				.replace("%removed%", Text.of(money))
                				.replace("%balance%", Text.of(getPlayerBalance(player.getUniqueId(), currency))).toPlain());
                	}
                } else if ((result.getResult() == ResultType.FAILED || result.getResult() == ResultType.ACCOUNT_NO_FUNDS) && plugin.getRootNode().getNode("Debug").getBoolean()) {
                	plugin.getLogger().error(plugin.getLocales().getLocalizedText(player.getLocale(), "Debug", "ErrorTakeMoney")
            				.replace("%player%", Text.of(player.getName())).toPlain());
                } else {
                }
            	}
        	} catch (Exception ignored) {
        }
	}

	public void auctionTransaction(UUID buyerUUID, SerializedAuctionStack auctionItem, int priceNumber, boolean isBet) {
        try {
            Optional<UniqueAccount> bOpt = plugin.getEconomyService().getOrCreateAccount(buyerUUID);
            Optional<UniqueAccount> sOpt = plugin.getEconomyService().getOrCreateAccount(auctionItem.getOwnerUUID());
            SerializedAuctionPrice serializedAuctionPrice = auctionItem.getPrices().get(priceNumber);
            BigDecimal money = (isBet ? auctionItem.getPrices().get(0).getBet() : serializedAuctionPrice.getPrice()).setScale(2);
            Currency currency = isBet ? auctionItem.getPrices().get(0).getCurrency() : serializedAuctionPrice.getCurrency();
            ItemStack itemStack = auctionItem.getSerializedItemStack().getItemStack();
            boolean isTax = isBet ? auctionItem.getPrices().get(priceNumber).getBet().doubleValue() <= 100 : auctionItem.getPrices().get(priceNumber).getPrice().doubleValue() <= 100;
            BigDecimal tax = BigDecimal.valueOf(isTax ? (isBet ? auctionItem.getBetData().getTax() : auctionItem.getPrices().get(priceNumber).getTax()) : 0).setScale(2, BigDecimal.ROUND_HALF_UP);
        	if(tax.doubleValue() < 0.01) {
        		tax = BigDecimal.ZERO;
        	}
            if (bOpt.isPresent() && sOpt.isPresent()) {
            	money = money.multiply(BigDecimal.valueOf(itemStack.getQuantity()));
                TransactionResult resultRemove = bOpt.get().withdraw(currency, money, Cause.of(plugin.getEventContext(), plugin.getPluginContainer()));
                TransactionResult resultAdd;
        		if(isTax) {
                    resultAdd = sOpt.get().deposit(currency, money.subtract(tax), Cause.of(plugin.getEventContext(), plugin.getPluginContainer()));
        		} else {
                    resultAdd = sOpt.get().deposit(currency, money, Cause.of(plugin.getEventContext(), plugin.getPluginContainer()));
        		}
        		if(resultRemove.getResult() == ResultType.SUCCESS && resultAdd.getResult() == ResultType.SUCCESS) {
                	if(plugin.getRootNode().getNode("PlayerTransactionMessage").getBoolean()) {
                		if(Sponge.getServer().getPlayer(buyerUUID).isPresent()) {
                			Player buyer = Sponge.getServer().getPlayer(buyerUUID).get();
                			buyer.sendMessage(plugin.getLocales().getLocalizedText(buyer.getLocale(), "Messages", "AuctionBuy")
                        			.replace("%item%", Text.builder().append(Text.of(itemStack)).onHover(TextActions.showItem(itemStack.createSnapshot())).build())
                        			.replace("%amount%", Text.of(itemStack.getQuantity()))
                        			.replace("%removed%", Text.of(currency.getSymbol(), money.doubleValue()))
                        			.replace("%balance%", Text.of(currency.getSymbol(), getPlayerBalance(buyerUUID, currency)))
                        			.replace("%seller%", auctionItem.getOwnerName()));
                        	if(Sponge.getServer().getPlayer(auctionItem.getOwnerUUID()).isPresent()) {
                        		Player seller = Sponge.getServer().getPlayer(auctionItem.getOwnerUUID()).get();
                        		seller.sendMessage(plugin.getLocales().getLocalizedText(seller.getLocale(), "Messages", "AuctionSell")
                            			.replace("%item%", Text.builder().append(Text.of(itemStack)).onHover(TextActions.showItem(itemStack.createSnapshot())).build())
                            			.replace("%amount%", Text.of(itemStack.getQuantity()))
                            			.replace("%added%", Text.of(currency.getSymbol(), money.doubleValue()))
                            			.replace("%balance%", Text.of(currency.getSymbol(), getPlayerBalance(seller.getUniqueId(), currency)))
                            			.replace("%buyer%", Text.of(buyer.getName())));
                        		if(isTax && tax.doubleValue() > 0) {
                        			seller.sendMessage(plugin.getLocales().getLocalizedText(seller.getLocale(), "Messages", "Tax")
                        					.replace("%amount%", Text.of(currency.getSymbol(), tax.doubleValue())));
                        		}
                        	}
                        	if(plugin.getRootNode().getNode("Debug").getBoolean()) {
                        		plugin.getLogger().info(plugin.getLocales().getDefaultLocalizedText("Debug", "InfoTakeItems")
                        				.replace("%item%", Text.of(itemStack))
                            			.replace("%amount%", Text.of(itemStack.getQuantity()))
                        				.replace("%player%", Text.of(buyer.getName()))
                        				.replace("%removed%", Text.of(money))
                        				.replace("%balance%", Text.of(getPlayerBalance(buyerUUID, currency))).toPlain());
                        	}
                		}
                	}
                } else if (((resultRemove.getResult() == ResultType.FAILED || resultRemove.getResult() == ResultType.ACCOUNT_NO_FUNDS) || (resultAdd.getResult() == ResultType.FAILED || resultAdd.getResult() == ResultType.ACCOUNT_NO_FUNDS)) && plugin.getRootNode().getNode("Debug").getBoolean()) {
            		if(Sponge.getServer().getPlayer(buyerUUID).isPresent()) {
            			Player buyer = Sponge.getServer().getPlayer(buyerUUID).get();
                    	plugin.getLogger().error(plugin.getLocales().getDefaultLocalizedText("Debug", "ErrorTakeMoney")
                				.replace("%player%", Text.of(buyer.getName())).toPlain());
            		}
                } else {
                }
            	}
        	} catch (Exception ignored) {
        }
	}

	public boolean fee(Player player, Currency currency, BigDecimal money) {
		if(money.doubleValue() > 0) {
			if(money.doubleValue() > getPlayerBalance(player.getUniqueId(), currency).doubleValue()) {
	        	player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "NoMoneyForFee"));
			} else {
		        try {
		            Optional<UniqueAccount> uOpt = plugin.getEconomyService().getOrCreateAccount(player.getUniqueId());
		            if (uOpt.isPresent()) {
		                uOpt.get().withdraw(currency, money, Cause.of(plugin.getEventContext(), plugin.getPluginContainer()));
		                return true;
		            }
		        } catch (Exception ignored) {
				}
			}
		} else {
            return true;
		}
		return false;
	}

	public Currency checkCurrency(String check) {
		Currency result = plugin.getEconomyService().getDefaultCurrency();
		for(Currency currency : plugin.getEconomyService().getCurrencies()) {
			if(currency.getId().equalsIgnoreCase(check) || currency.getName().equalsIgnoreCase(check) || currency.getSymbol().toPlain().equalsIgnoreCase(check)) {
				result = currency;
				break;
			}
		}
		return result;
	}

}
