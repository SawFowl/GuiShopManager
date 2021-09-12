package sawfowl.guishopmanager.gui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Container;
import org.spongepowered.api.item.inventory.ContainerTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.PrimaryPlayerInventory;
import org.spongepowered.api.item.inventory.menu.ClickType;
import org.spongepowered.api.item.inventory.menu.ClickTypes;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.menu.handler.CloseHandler;
import org.spongepowered.api.item.inventory.menu.handler.SlotClickHandler;
import org.spongepowered.api.item.inventory.query.QueryTypes;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.util.Ticks;
import org.spongepowered.common.item.util.ItemStackUtil;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.minecraft.nbt.CompoundTag;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.configure.FillItems;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionPrice;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.serialization.auction.SerializedBetData;

import sawfowl.localeapi.serializetools.SerializedItemStack;

public class AuctionMenus {

	private GuiShopManager plugin;
	private String serverName;
	public AuctionMenus(GuiShopManager instance) {
		plugin = instance;
		serverName = plugin.getRootNode().node("Auction", "Server").getString();
	}

	public void createInventory(ServerPlayer player, int page, List<SerializedAuctionStack> auctionStacks) {
		Component menuTitle = page == 1 ? Component.text("Auction") : Component.text("Auction" + page);
		menuTitle = page == 1 ? plugin.getLocales().getComponent(player.locale(), "Gui", "Auction") : Component.text(plugin.getLocales().getComponent(player.locale(), "Gui", "Auction") + " || " + page);
        int firstItem = (page * 45) - 45;
        int currentItem = firstItem;
		List<Currency> currencies = new ArrayList<Currency>();
		currencies.add(plugin.getEconomyService().defaultCurrency());
		for(Currency currency : plugin.getEconomy().getCurrencies()) {
			if(player.hasPermission(Permissions.currencyPermission(currency.displayName())) && !currency.equals(plugin.getEconomyService().defaultCurrency())) {
				currencies.add(currency);
			}
		}
		EditData editData = new EditData();
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X6).completeStructure().carrier(player).build();
		InventoryMenu menu = viewableInventory.asMenu();
		menu.setTitle(menuTitle);
		menu.setReadOnly(true);
		for(Slot slot : menu.inventory().slots()) {
			int id = slot.get(Keys.SLOT_INDEX).get();
			if(id < 45) {
				if(plugin.getAuctionItems().isEmpty() || currentItem >= plugin.getAuctionItems().size()) {
					slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
				} else {
					SerializedAuctionStack auctionItem = auctionStacks.get(currentItem);
					ItemStack itemStack = auctionItem.getSerializedItemStack().getItemStack();
					net.minecraft.world.item.ItemStack nmsStack = ItemStackUtil.toNative(itemStack);
					CompoundTag nbt = nmsStack.hasTag() ? nmsStack.getTag() : new CompoundTag();
					nbt.putUUID("uuid", auctionItem.getStackUUID());
					nmsStack.setTag(nbt);
					itemStack = ItemStackUtil.fromNative(nmsStack);
					List<Component> itemLore = itemStack.get(Keys.LORE).orElse(new ArrayList<Component>());
					if(itemStack.get(Keys.LORE).isPresent()) {
						itemStack.remove(Keys.LORE);
					}
					itemLore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "TransactionVariants"));
					if(auctionItem.getPrices().get(0).getBet().doubleValue() > 0) {
						itemLore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "AuctionBet")
								.replaceText(TextReplacementConfig.builder().match("%currency%").replacement(auctionItem.getPrices().get(0).getCurrency().displayName()).build())
								.replaceText(TextReplacementConfig.builder().match("%price%").replacement(Component.text(auctionItem.getPrices().get(0).getBet().doubleValue())).build())
								.replaceText(TextReplacementConfig.builder().match("%total%").replacement(Component.text(auctionItem.getPrices().get(0).getBet().doubleValue() * itemStack.quantity())).build()));
						boolean addEmpty = false;
						for(SerializedAuctionPrice price : auctionItem.getPrices()) {
							if(price.getPrice().doubleValue() > 0) {
								addEmpty = true;
							}
						}
						if(addEmpty) {
							itemLore.add(Component.empty());
						}
					}
					for(SerializedAuctionPrice price : auctionItem.getPrices()) {
						if(price.getPrice().doubleValue() > 0) {
							itemLore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "AuctionPrice")
									.replaceText(TextReplacementConfig.builder().match("%currency%").replacement(price.getCurrency().displayName()).build())
									.replaceText(TextReplacementConfig.builder().match("%price%").replacement(Component.text(price.getPrice().doubleValue())).build())
									.replaceText(TextReplacementConfig.builder().match("%total%").replacement(Component.text(price.getPrice().doubleValue() * itemStack.quantity())).build()));
						}
					}
					itemLore.add(Component.empty());
					itemLore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "Expired")
							.replaceText(TextReplacementConfig.builder().match("%expired%").replacement(auctionItem.getExpireTimeFromNow()).build()));
					itemLore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "Seller")
							.replaceText(TextReplacementConfig.builder().match("%seller%").replacement(Component.text(auctionItem.getOwnerName())).build()));
					if(auctionItem.getBetData() != null) {
						if(!auctionItem.betIsNull()) {
							itemLore.add(Component.empty());
							itemLore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "CurrentBuyer")
									.replaceText(TextReplacementConfig.builder().match("%buyer%").replacement(Component.text(auctionItem.getBetData().getBuyerName())).build()));
							itemLore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "CurrentBet")
									.replaceText(TextReplacementConfig.builder().match("%bet%").replacement(Component.text().append(auctionItem.getPrices().get(0).getCurrency().symbol()).append(Component.text(auctionItem.getBetData().getMoney().doubleValue()))).build()));
						}
					}
					itemStack.offer(Keys.LORE, itemLore);
					if(!auctionItem.isExpired()) {
						currentItem++;
					}
					slot.offer(itemStack);
				}
			} else if(id >= 45 && id <= 53) {
				slot.set(plugin.getFillItems().getItemStack(FillItems.BOTTOM));
				if(id == 45 && page >= 2) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.BACK);
					itemStack.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "Back"));
					slot.set(itemStack);
				} else if(id == 47) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.ADD);
					itemStack.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "AuctionAddItem"));
					slot.set(itemStack);
				} else if(id == 49) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
					itemStack.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "ChangeCurrency"));
					itemStack.offer(Keys.LORE, Arrays.asList(plugin.getLocales().getComponent(player.locale(), "Lore", "CurrentCurrency")
							.replaceText(TextReplacementConfig.builder().match("%currency%").replacement(currencies.get(editData.priceNumber).displayName()).build())));
					slot.set(itemStack);
				} else if(id == 51) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.RETURN);
					itemStack.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "ReturnAuctionItem"));
					slot.set(itemStack);
				} else if(id == 53 && plugin.getAuctionItems().size() >= (page * 45)) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.NEXT);
					itemStack.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "Next"));
					slot.set(itemStack);
				}
			}
		}
		menu.registerSlotClick(new SlotClickHandler() {
			@Override
			public boolean handle(Cause cause, Container container, Slot slot, int slotIndex, ClickType<?> clickType) {
				if(menu.inventory().containsChild(slot) && slotIndex <= 53) {
					if(clickType != ClickTypes.CLICK_LEFT.get() && clickType != ClickTypes.CLICK_RIGHT.get()) return true;
					if(slotIndex == 45 && page >= 2) {
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							createInventory(player, page - 1, plugin.getAuctionItems().values().stream().collect(Collectors.toList()));
						}).build());
					} else if(slotIndex == 47) {
						int currentSelling = plugin.getExpiredAuctionItems().containsKey(player.uniqueId()) ? plugin.getExpiredAuctionItems().get(player.uniqueId()).size() : 0;
						for(SerializedAuctionStack auctionItem : plugin.getAuctionItems().values()) {
							if(auctionItem.getOwnerUUID().equals(player.uniqueId())) {
								currentSelling++;
							}
							if(currentSelling >= 53) {
								break;
							}
						}
						if(currentSelling >= 53) {
							player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "AuctionMaxVolume"));
							return false;
						}
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							editItem(player, page);
						}).build());
					} else if(slotIndex == 49) {
						editData.priceNumber++;
						if(editData.priceNumber > currencies.size() - 1) editData.priceNumber = 0;
						ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
						itemStack.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "ChangeCurrency"));
						itemStack.offer(Keys.LORE, Arrays.asList(plugin.getLocales().getComponent(player.locale(), "Lore", "CurrentCurrency")
								.replaceText(TextReplacementConfig.builder().match("%currency%").replacement(currencies.get(editData.priceNumber).displayName()).build())));
						slot.set(itemStack);
					} else if(slotIndex == 51) {
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							returnItems(player);
						}).build());
					} else if(slotIndex == 53 && slot.peek().type().equals(plugin.getFillItems().getItemStack(FillItems.NEXT).type())) {
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							createInventory(player, page + 1, plugin.getAuctionItems().values().stream().collect(Collectors.toList()));
						}).build());
					} else if(slotIndex <= 44) {
						net.minecraft.world.item.ItemStack nmsStack = ItemStackUtil.toNative(slot.peek());
						if(!nmsStack.hasTag() || nmsStack.getTag() == null || !nmsStack.getTag().contains("uuid")) return true;
						if(!plugin.getAuctionItems().containsKey(nmsStack.getTag().getUUID("uuid"))) {
							player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "AuctionItemNotFound"));
							Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
								slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
							}).build());
						}
						UUID stackUUID = nmsStack.getTag().getUUID("uuid");
						if(plugin.getAuctionItems().get(stackUUID).getOwnerUUID().equals(player.uniqueId())) {
							player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "AuctionCancelBuy"));
							return false;
						}
						if(clickType == ClickTypes.CLICK_LEFT.get()) {
							Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
								editBet(player, page, stackUUID);
							}).build());
						} else if(clickType == ClickTypes.CLICK_RIGHT.get()) {
							SerializedAuctionStack auctionItem = plugin.getAuctionItems().get(stackUUID);
							if(!auctionItem.containsCurrency(currencies.get(editData.priceNumber)) || auctionItem.getPrices().get(editData.priceNumber).getPrice().doubleValue() == 0 || auctionItem.getSerializedItemStack().getItemStack().quantity() > calculateMaxBuyItems(player, auctionItem.getSerializedItemStack().getItemStack(), auctionItem.getPrices().get(editData.priceNumber))) {
								return false;
							}
							if(plugin.getEconomy().checkPlayerBalance(player.uniqueId(), auctionItem.getPrices().get(editData.priceNumber).getCurrency(), auctionItem.getPrices().get(editData.priceNumber).getPrice())) {
								slot.set(plugin.getFillItems().getItemStack(FillItems.BASIC));
								plugin.getEconomy().auctionTransaction(player.uniqueId(), auctionItem, editData.priceNumber, false);
								ItemStack itemStack = auctionItem.getSerializedItemStack().getItemStack();
								plugin.getAuctionWorkData().removeAuctionStack(auctionItem.getStackUUID());
								plugin.getAuctionItems().remove(stackUUID);
								player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).offer(itemStack);
							} else {
								player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "NoMoney"));
							}
						}
					}
				}
				return true;
			}
			
		});
		menu.registerClose(new CloseHandler() {
			@Override
			public void handle(Cause cause, Container container) {
				menu.unregisterAll();
			}
		});
		menu.open(player);
	}

	public void editBet(ServerPlayer player, int page, UUID idAuctionItem) {
		Component menuTitle = plugin.getLocales().getComponent(player.locale(), "Gui", "AuctionBet");
		EditData editData = new EditData();
		Currency currency = plugin.getAuctionItems().get(idAuctionItem).getPrices().get(0).getCurrency();
		editData.itemStack = plugin.getAuctionItems().get(idAuctionItem).getSerializedItemStack().getItemStack();
		SerializedBetData oldBetData = plugin.getAuctionItems().get(idAuctionItem).getBetData();
		BigDecimal minimalBet = oldBetData == null ? plugin.getAuctionItems().get(idAuctionItem).getPrices().get(0).getBet() : oldBetData.getMoney().add(BigDecimal.valueOf(0.01));
		SerializedBetData betData = new SerializedBetData(serverName, player.uniqueId(), player.name(), minimalBet, currency);
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X3).completeStructure().carrier(player).build();
		InventoryMenu menu = viewableInventory.asMenu();
		menu.setTitle(menuTitle);
		menu.setReadOnly(true);
		for(Slot slot : menu.inventory().slots()) {
			int id = slot.get(Keys.SLOT_INDEX).get();
			slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
			if(id == 13) {
				slot.set(getDisplayBetItem(player, betData, editData));
			} else 
			if(id <= 8) {
				Component price = Component.text(" 0.01");
				if(id == 1) {
					price = Component.text(" 0.1");
				} else if(id == 2) {
					price = Component.text(" 0.5");
				} else if(id == 3) {
					price = Component.text(" 1");
				} else if(id == 4) {
					price = Component.text(" 5");
				} else if(id == 5) {
					price = Component.text(" 10");
				} else if(id == 6) {
					price = Component.text(" 100");
				} else if(id == 7) {
					price = Component.text(" 1000");
				} else if(id == 8) {
					price = Component.text(" 10000");
				}
				ItemStack changePrice = plugin.getFillItems().getItemStack(FillItems.valueOf("CHANGEPRICE" + id));
				changePrice.offer(Keys.LORE, plugin.getLocales().getComponents(player.locale(), "Lore", "ChangePrice"));
				changePrice.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "Price").replaceText(TextReplacementConfig.builder().match("%value%").replacement(price).build()));
				slot.set(changePrice);
			} else if(id == 18) {
				ItemStack back = plugin.getFillItems().getItemStack(FillItems.BACK);
				back.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "Back"));
				slot.set(back);
			} else if(id == 26) {
				ItemStack exit = plugin.getFillItems().getItemStack(FillItems.EXIT);
				exit.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "Exit"));
				slot.set(exit);
			}
		}
		menu.registerSlotClick(new SlotClickHandler() {
			@Override
			public boolean handle(Cause cause, Container container, Slot slot, int slotIndex, ClickType<?> clickType) {
				if(menu.inventory().containsChild(slot) && slotIndex <= 26) {
					if(clickType != ClickTypes.CLICK_LEFT.get() && clickType != ClickTypes.CLICK_RIGHT.get()) return false;
					if(!plugin.getAuctionItems().containsKey(idAuctionItem)) {
						player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "AuctionItemNotFound"));
						closePlayerInventory(player);
						return false;
					}
					editData.itemStack = plugin.getAuctionItems().get(idAuctionItem).getSerializedItemStack().getItemStack();
					if(slotIndex <= 8) {
						boolean increase = clickType == ClickTypes.CLICK_LEFT.get();
						if(slotIndex == 0) {
							betData.changeMoney(BigDecimal.valueOf(0.01), increase);
						} else if(slotIndex == 1) {
							betData.changeMoney(BigDecimal.valueOf(0.1), increase);
						} else if(slotIndex == 2) {
							betData.changeMoney(BigDecimal.valueOf(0.5), increase);
						} else if(slotIndex == 3) {
							betData.changeMoney(BigDecimal.valueOf(1), increase);
						} else if(slotIndex == 4) {
							betData.changeMoney(BigDecimal.valueOf(5), increase);
						} else if(slotIndex == 5) {
							betData.changeMoney(BigDecimal.valueOf(10), increase);
						} else if(slotIndex == 6) {
							betData.changeMoney(BigDecimal.valueOf(100), increase);
						} else if(slotIndex == 7) {
							betData.changeMoney(BigDecimal.valueOf(1000), increase);
						} else if(slotIndex == 8) {
							betData.changeMoney(BigDecimal.valueOf(10000), increase);
						}
						if(betData.getMoney().doubleValue() < 0) {
							betData.setMoney(BigDecimal.valueOf(0));
						}
						menu.inventory().slot(13).get().set(getDisplayBetItem(player, betData, editData));
					} else if(slotIndex == 18) {
						if(betData.getMoney().doubleValue() > minimalBet.doubleValue() && plugin.getEconomy().checkPlayerBalance(player.uniqueId(), currency, betData.getMoney())) {
							plugin.getAuctionItems().get(idAuctionItem).setBetData(betData);
							plugin.getAuctionWorkData().saveAuctionStack(plugin.getAuctionItems().get(idAuctionItem));
						} else {
							player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "BetIsNotSet"));
						}
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							createInventory(player, page, plugin.getAuctionItems().values().stream().collect(Collectors.toList()));
						}).build());
					} else if(slotIndex == 26) {
						closePlayerInventory(player);
						if(betData.getMoney().doubleValue() > minimalBet.doubleValue() && plugin.getEconomy().checkPlayerBalance(player.uniqueId(), currency, betData.getMoney())) {
							plugin.getAuctionItems().get(idAuctionItem).setBetData(betData);
							plugin.getAuctionWorkData().saveAuctionStack(plugin.getAuctionItems().get(idAuctionItem));
						} else {
							player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "BetIsNotSet"));
						}
					}
					return false;
				}
				return true;
			}
			
		});
		menu.registerClose(new CloseHandler() {
			@Override
			public void handle(Cause cause, Container container) {
				menu.unregisterAll();
			}
		});
		menu.open(player);
	}

	public void editItem(ServerPlayer player, int page) {
		Component menuTitle = Component.text("Edit auction item");
		menuTitle = plugin.getLocales().getComponent(player.locale(), "Gui", "EditAuctionItem");
		EditData editData = new EditData();
		List<SerializedAuctionPrice> prices = new ArrayList<SerializedAuctionPrice>();
		prices.add(new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency()));
		for(Currency currency : plugin.getEconomy().getCurrencies()) {
			if(player.hasPermission(Permissions.currencyPermission(currency.displayName())) && !currency.equals(plugin.getEconomyService().defaultCurrency())) {
				prices.add(new SerializedAuctionPrice(currency));
			}
		}
		SerializedAuctionStack auctionStack = new SerializedAuctionStack(editData.itemStack.copy(), prices, player.uniqueId(), player.name(), System.currentTimeMillis() + plugin.getExpire(editData.expire).getTime(), serverName);
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X3).completeStructure().carrier(player).build();
		InventoryMenu menu = viewableInventory.asMenu();
		menu.setTitle(menuTitle);
		menu.setReadOnly(true);
		for(Slot slot : menu.inventory().slots()) {
			int id = slot.get(Keys.SLOT_INDEX).get();
			if(id != 13) {
				slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
			}
			if(id <= 8) {
				Component price = Component.text(" 0.01");
				if(id == 1) {
					price = Component.text(" 0.1");
				}
				if(id == 2) {
					price = Component.text(" 0.5");
				}
				if(id == 3) {
					price = Component.text(" 1");
				}
				if(id == 4) {
					price = Component.text(" 5");
				}
				if(id == 5) {
					price = Component.text(" 10");
				}
				if(id == 6) {
					price = Component.text(" 100");
				}
				if(id == 7) {
					price = Component.text(" 1000");
				}
				if(id == 8) {
					price = Component.text(" 10000");
				}
				ItemStack changePrice = plugin.getFillItems().getItemStack(FillItems.valueOf("CHANGEPRICE" + id));
				changePrice.offer(Keys.LORE, plugin.getLocales().getComponents(player.locale(), "Lore", "ChangePrice"));
				changePrice.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "Price").replaceText(TextReplacementConfig.builder().match("%value%").replacement(price).build()));
				slot.set(changePrice);
			}
			if(id == 18) {
				ItemStack back = plugin.getFillItems().getItemStack(FillItems.BACK);
				back.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "Back"));
				slot.set(back);
			}
			if(id == 21) {
				ItemStack clear = plugin.getFillItems().getItemStack(FillItems.CLEAR);
				clear.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "Clear"));
				slot.set(clear);
			}
			if(id == 22) {
				ItemStack switchMode = plugin.getFillItems().getItemStack(FillItems.SWITCHMODE);
				switchMode.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "SwitchMode"));
				switchMode.offer(Keys.LORE, plugin.getLocales().getComponents(player.locale(), "Lore", "AuctionSwitchMode"));
				slot.set(switchMode);
			}
			if(id == 23) {
				ItemStack changeCurrency = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
				changeCurrency.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "ChangeCurrency"));
				slot.set(changeCurrency);
			}
			if(id == 26) {
				ItemStack exit = plugin.getFillItems().getItemStack(FillItems.EXIT);
				exit.offer(Keys.CUSTOM_NAME, plugin.getLocales().getComponent(player.locale(), "FillItems", "Exit"));
				slot.set(exit);
			}
		}
		menu.registerSlotClick(new SlotClickHandler() {
			@Override
			public boolean handle(Cause cause, Container container, Slot slot, int slotIndex, ClickType<?> clickType) {
				if(menu.inventory().containsChild(slot) && slotIndex <= 26) {
					if(clickType != ClickTypes.CLICK_LEFT.get() && clickType != ClickTypes.CLICK_RIGHT.get()) return false;
					if(slotIndex <= 8) {
						boolean increase = clickType == ClickTypes.CLICK_LEFT.get();
						if(slotIndex == 0) {
							auctionStack.getPrices().get(editData.priceNumber).updateBetOrPrice(BigDecimal.valueOf(0.01), editData.bet, editData.priceNumber, increase);
						} else if(slotIndex == 1) {
							auctionStack.getPrices().get(editData.priceNumber).updateBetOrPrice(BigDecimal.valueOf(0.1), editData.bet, editData.priceNumber, increase);
						} else if(slotIndex == 2) {
							auctionStack.getPrices().get(editData.priceNumber).updateBetOrPrice(BigDecimal.valueOf(0.5), editData.bet, editData.priceNumber, increase);
						} else if(slotIndex == 3) {
							auctionStack.getPrices().get(editData.priceNumber).updateBetOrPrice(BigDecimal.valueOf(1), editData.bet, editData.priceNumber, increase);
						} else if(slotIndex == 4) {
							auctionStack.getPrices().get(editData.priceNumber).updateBetOrPrice(BigDecimal.valueOf(5), editData.bet, editData.priceNumber, increase);
						} else if(slotIndex == 5) {
							auctionStack.getPrices().get(editData.priceNumber).updateBetOrPrice(BigDecimal.valueOf(10), editData.bet, editData.priceNumber, increase);
						} else if(slotIndex == 6) {
							auctionStack.getPrices().get(editData.priceNumber).updateBetOrPrice(BigDecimal.valueOf(100), editData.bet, editData.priceNumber, increase);
						} else if(slotIndex == 7) {
							auctionStack.getPrices().get(editData.priceNumber).updateBetOrPrice(BigDecimal.valueOf(1000), editData.bet, editData.priceNumber, increase);
						} else if(slotIndex == 8) {
							auctionStack.getPrices().get(editData.priceNumber).updateBetOrPrice(BigDecimal.valueOf(10000), editData.bet, editData.priceNumber, increase);
						}
						for(SerializedAuctionPrice price : prices) {
							if(price.isZero()) {
								 editData.save = false;
							} else {
								 editData.save = true;
								 break;
							}
						}
						menu.inventory().slot(13).get().set(getDisplayItem(player, auctionStack, editData));
					} else if(slotIndex == 18) {
						closePlayerInventory(player);
						if(!editData.itemStack.type().equals(ItemTypes.AIR.get()) && editData.save) {
							addItem(player, auctionStack);
							Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
								createInventory(player, page, plugin.getAuctionItems().values().stream().collect(Collectors.toList()));
							}).build());
						} else {
							Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
								createInventory(player, page, plugin.getAuctionItems().values().stream().collect(Collectors.toList()));
							}).build());
						}
					}
					if(slotIndex == 21) {
						for(SerializedAuctionPrice price : prices) {
							price.setZero();
						}
						editData.save = false;
						menu.inventory().slot(13).get().set(getDisplayItem(player, auctionStack, editData));
					}
					if(slotIndex == 22) {
						if(clickType == ClickTypes.CLICK_LEFT.get()) {
							if(editData.bet) {
								editData.bet = false;
							} else {
								editData.bet = true;
								editData.priceNumber = 0;
							}
						} else {
							if(editData.expire < plugin.getExpiresLastNumber()) {
								editData.expire++;
							} else {
								editData.expire = 0;
							}
						}
						menu.inventory().slot(13).get().set(getDisplayItem(player, auctionStack, editData));
					}
					if(slotIndex == 23) {
						editData.priceNumber++;
						if(editData.priceNumber > prices.size() - 1) {
							editData.priceNumber = 0;
						}
						if(editData.priceNumber > 0) {
							editData.bet = false;
						}
					}
					if(slotIndex == 26) {
						closePlayerInventory(player);
						if(!editData.itemStack.type().equals(ItemTypes.AIR.get()) && editData.save) {
							addItem(player, auctionStack);
						}
					}
				} else {
					if(slot.totalQuantity() > 0 && !prices.isEmpty()) {
						SerializedItemStack serializedItemStack = new SerializedItemStack(slot.peek());
						if(plugin.maskIsBlackList(serializedItemStack.getType()) || plugin.itemIsBlackList(slot.peek())) {
							player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "ItemBlocked"));
							return false;
						}
						if(plugin.getRootNode().node("Auction", "NbtLimit").getInt() < auctionStack.getSerializedItemStack().getNBT().toString().length()) {
							player.sendMessage(plugin.getLocales().getComponent(player.locale(), "Messages", "LongNBT"));
							return false;
						}
						editData.itemStack = slot.peek();
						auctionStack.setItemStack(editData.itemStack);
						auctionStack.getPrices().get(editData.priceNumber).setTax(plugin.getExpire(editData.expire).getTax(), editData.itemStack.quantity());
						menu.inventory().slot(13).get().set(getDisplayItem(player, auctionStack, editData));
					}
				}
				return false;
			}
			
		});
		menu.registerClose(new CloseHandler() {
			@Override
			public void handle(Cause cause, Container container) {
				menu.unregisterAll();
			}
		});
		menu.open(player);
	}

	private void returnItems(ServerPlayer player) {
		Component menuTitle = Component.text("Return items");
		menuTitle = plugin.getLocales().getComponent(player.locale(), "Gui", "AuctionReturn");
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X6).completeStructure().carrier(player).build();
		InventoryMenu menu = viewableInventory.asMenu();
		menu.setTitle(menuTitle);
		menu.setReadOnly(true);
		for(SerializedAuctionStack auctionItem : plugin.getAuctionItems().values()) {
			if(auctionItem.getServerName().equals(serverName) && auctionItem.getOwnerUUID().equals(player.uniqueId())) {
				net.minecraft.world.item.ItemStack nmsStack = ItemStackUtil.toNative(auctionItem.getSerializedItemStack().getItemStack());
				CompoundTag nbt = nmsStack.hasTag() ? nmsStack.getTag() : new CompoundTag();
				nbt.putUUID("uuid", auctionItem.getStackUUID());
				nmsStack.setTag(nbt);
				menu.inventory().offer(ItemStackUtil.fromNative(nmsStack));
			}
		}
		menu.registerSlotClick(new SlotClickHandler() {
			@Override
			public boolean handle(Cause cause, Container container, Slot slot, int slotIndex, ClickType<?> clickType) {
				if(menu.inventory().containsChild(slot) && slotIndex <= 53) {
					if(slot.totalQuantity() > 0 && ItemStackUtil.toNative(slot.peek()).hasTag() && ItemStackUtil.toNative(slot.peek()).getTag().contains("uuid")) {
						UUID uuid = ItemStackUtil.toNative(slot.peek()).getTag().getUUID("uuid");
						if(plugin.getAuctionItems().containsKey(uuid) && player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).freeCapacity() > 0) {
							slot.clear();
							ItemStack itemStack = plugin.getAuctionItems().get(uuid).getSerializedItemStack().getItemStack();
							plugin.getAuctionItems().remove(uuid);
							plugin.getAuctionWorkData().removeAuctionStack(uuid);
							player.inventory().offer(itemStack);
						}
					}
				}
				if(menu.inventory().totalQuantity() == 0) {
					closePlayerInventory(player);
				}
				return true;
			}
			
		});
		menu.registerClose(new CloseHandler() {
			@Override
			public void handle(Cause cause, Container container) {
				menu.unregisterAll();
			}
		});
		menu.open(player);
	}

	private ItemStack getDisplayItem(ServerPlayer player, SerializedAuctionStack auctionStack, EditData editData) {
		ItemStack itemStack = editData.itemStack.copy();
		List<Component> lore = itemStack.get(Keys.LORE).orElse(new ArrayList<Component>());
		if(itemStack.get(Keys.LORE).isPresent()) {
			itemStack.remove(Keys.LORE);
			lore.add(Component.empty());
		}
		auctionStack.updateExpires(plugin.getExpire(editData.expire).getTime());
		lore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "CurrentCurrency")
				.replaceText(TextReplacementConfig.builder().match("%currency%").replacement(editData.bet ? auctionStack.getPrices().get(0).getCurrency().displayName() : auctionStack.getPrices().get(editData.priceNumber).getCurrency().displayName()).build()));
		if(auctionStack.getPrices().get(0).getBet().doubleValue() > 0) {
			lore.add(Component.empty());
			lore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "AuctionBet")
					.replaceText(TextReplacementConfig.builder().match("%currency%").replacement(auctionStack.getPrices().get(0).getCurrency().displayName()).build())
					.replaceText(TextReplacementConfig.builder().match("%price%").replacement(Component.text(auctionStack.getPrices().get(0).getBet().doubleValue())).build())
					.replaceText(TextReplacementConfig.builder().match("%total%").replacement(Component.text(auctionStack.getPrices().get(0).getBet().doubleValue() * itemStack.quantity())).build()));
			lore.add(Component.empty());
		}
		boolean addEmpty = false;
		for(SerializedAuctionPrice price : auctionStack.getPrices()) {
			if(price.getPrice().doubleValue() > 0) {
				addEmpty = true;
				lore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "AuctionPrice")
						.replaceText(TextReplacementConfig.builder().match("%currency%").replacement(price.getCurrency().displayName()).build())
						.replaceText(TextReplacementConfig.builder().match("%price%").replacement(Component.text(price.getPrice().doubleValue())).build())
						.replaceText(TextReplacementConfig.builder().match("%total%").replacement(Component.text(price.getPrice().doubleValue() * itemStack.quantity())).build()));
			}
		}
		if(plugin.getExpire(editData.expire).isTax()) {
			if(addEmpty) {
				lore.add(Component.empty());
				addEmpty = false;
			}
			if(editData.bet) {
				lore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "Tax").replaceText(TextReplacementConfig.builder().match("%size%").replacement(auctionStack.getPrices().get(0).getCurrency().symbol().append(Component.text(":n/a"))).build()));
			} else {
				lore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "Tax").replaceText(TextReplacementConfig.builder().match("%size%").replacement(auctionStack.getPrices().get(editData.priceNumber).getCurrency().symbol().append(Component.text(auctionStack.getPrices().get(editData.priceNumber).getTax()))).build()));
			}
		}
		if(plugin.getExpire(editData.expire).isFee()) {
			if(addEmpty) {
				lore.add(Component.empty());
			}
			lore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "Fee").replaceText(TextReplacementConfig.builder().match("%size%").replacement(auctionStack.getPrices().get(0).getCurrency().symbol().append(Component.text(plugin.getExpire(editData.expire).getFee()))).build()));
			lore.add(Component.empty());
		}
		lore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "Expired")
				.replaceText(TextReplacementConfig.builder().match("%expired%").replacement(auctionStack.getExpireTimeFromNow()).build()));
		itemStack.offer(Keys.LORE, lore);
		return itemStack;
	}

	private ItemStack getDisplayBetItem(ServerPlayer player, SerializedBetData betData, EditData editData) {
		ItemStack itemStack = editData.itemStack.copy();
		List<Component> lore = itemStack.get(Keys.LORE).orElse(new ArrayList<Component>());
		if(itemStack.get(Keys.LORE).isPresent()) {
			itemStack.remove(Keys.LORE);
			lore.add(Component.empty());
		}
		
		lore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "YourBet")
				.replaceText(TextReplacementConfig.builder().match("%size%").replacement(Component.text().append(betData.getCurrency().symbol()).append(Component.text(betData.getMoney().doubleValue()))).build())
				.replaceText(TextReplacementConfig.builder().match("%total%").replacement(Component.text().append(betData.getCurrency().symbol()).append(Component.text(betData.getMoney().doubleValue() * itemStack.quantity()))).build()));
		itemStack.offer(Keys.LORE, lore);
		if(plugin.getExpire(editData.expire).isTax()) {
			betData.setTax(plugin.getExpire(editData.expire).getTax(), itemStack.quantity());
			lore.add(plugin.getLocales().getComponent(player.locale(), "Lore", "Tax").replaceText(TextReplacementConfig.builder().match("%size%").replacement(Component.text().append(betData.getCurrency().symbol()).append(Component.text(betData.getTax()))).build()));
		}
		return itemStack;
	}

	private Integer calculateMaxBuyItems(ServerPlayer player, ItemStack itemStack, SerializedAuctionPrice serializedPrice) {
		if(plugin.getEconomy().getPlayerBalance(player.uniqueId(), serializedPrice.getCurrency()).doubleValue() < serializedPrice.getPrice().doubleValue()) return 0;
		int value = player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).freeCapacity() * itemStack.maxStackQuantity();
		for(Slot playerSlot : player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).slots()) {
			if(playerSlot.contains(itemStack)) {
				int difference = itemStack.maxStackQuantity() - playerSlot.peek().quantity();
				if(playerSlot.peek().quantity() != itemStack.maxStackQuantity()) {
					value = value + difference;
				}
			}
		}
		BigDecimal requiredMoney = calculateMoney(itemStack, serializedPrice);
		if(plugin.getEconomy().checkPlayerBalance(player.uniqueId(), serializedPrice.getCurrency(), requiredMoney) && value >= Double.valueOf(plugin.getEconomy().getPlayerBalance(player.uniqueId(), serializedPrice.getCurrency()).doubleValue() / requiredMoney.doubleValue()).intValue()) {
			value = Double.valueOf(plugin.getEconomy().getPlayerBalance(player.uniqueId(), serializedPrice.getCurrency()).doubleValue() / requiredMoney.doubleValue()).intValue();
		}
		return value;
	}

	private BigDecimal calculateMoney(ItemStack itemStack, SerializedAuctionPrice serializedPrice) {
		return BigDecimal.valueOf(serializedPrice.getPrice().doubleValue()).pow(itemStack.quantity());
	}

	/*private double calculateTax(SerializedAuctionStack auctionStack, EditData editData, double money) {
		if(plugin.getExpire(editData.expire).isTax()) {
			if(editData.bet) {
				return BigDecimal.valueOf(((money * auctionStack.getSerializedItemStack().getQuantity()) / 100) * plugin.getExpire(editData.expire).getTax()).setScale(2, RoundingMode.HALF_UP).doubleValue();
			}
			auctionStack.getPrices().get(editData.priceNumber).setTax(plugin.getExpire(editData.expire).getTax(), auctionStack.getSerializedItemStack().getQuantity());
			return auctionStack.getPrices().get(editData.priceNumber).getTax();
		}
		return 0;
	}*/

	// Из-за бага в Sponge придется юзать это. Sponge не отправляет пакет закрытия инвентаря.
	private void closePlayerInventory(ServerPlayer player) {
		net.minecraft.server.level.ServerPlayer p = (net.minecraft.server.level.ServerPlayer) ((Object) player);
		Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(4)).plugin(plugin.getPluginContainer()).execute(() -> {
			player.closeInventory();
			p.closeContainer();
		}).build());
	}

	private void addItem(ServerPlayer player, SerializedAuctionStack auctionStack) {
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

	private class EditData {
		boolean save = false;
		boolean bet = true;
		int expire = 0;
		int priceNumber = 0;
		ItemStack itemStack = ItemStack.of(ItemTypes.AIR);
	}

}
