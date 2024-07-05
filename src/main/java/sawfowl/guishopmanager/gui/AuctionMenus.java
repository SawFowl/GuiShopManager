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
import org.spongepowered.plugin.PluginContainer;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.configure.FillItems;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Gui;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Items;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Messages;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionPrice;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.serialization.auction.SerializedBetData;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStackJsonNbt;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStackPlainNBT;

public class AuctionMenus {

	private GuiShopManager plugin;
	private String serverName;
	public AuctionMenus(GuiShopManager instance) {
		plugin = instance;
		serverName = plugin.getConfig().getAuction().getServer();
	}

	public void createInventory(ServerPlayer player, int page, List<SerializedAuctionStack> auctionStacks) {
		Component menuTitle = page == 1 ? Component.text("Auction") : Component.text("Auction" + page);
		menuTitle = page == 1 ? getAuctionGui(player).auction() : getAuctionGui(player).auction().append(Component.text(" || " + page));
		int firstItem = (page * 45) - 45;
		int currentItem = firstItem;
		List<Currency> currencies = new ArrayList<Currency>();
		currencies.add(plugin.getEconomyService().defaultCurrency());
		for(Currency currency : plugin.getEconomy().getCurrencies()) if(Permissions.auctionCurrencyPermission(player, currency, true) && !currency.equals(plugin.getEconomyService().defaultCurrency())) currencies.add(currency);
		EditData editData = new EditData();
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X6).completeStructure().carrier(player).plugin(plugin.getPluginContainer()).build();
		InventoryMenu menu = viewableInventory.asMenu();
		menu.setTitle(menuTitle);
		menu.setReadOnly(true);
		for(Slot slot : menu.inventory().slots()) {
			int id = slot.get(Keys.SLOT_INDEX).get();
			if(id < 45) {
				while(currentItem < plugin.getAuctionItems().size() && auctionStacks.get(currentItem).isExpired()) currentItem++;
				if(plugin.getAuctionItems().isEmpty() || currentItem >= plugin.getAuctionItems().size()) {
					slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
				} else {
					SerializedAuctionStack auctionItem = auctionStacks.get(currentItem);
					SerializedItemStackJsonNbt configuredStack = new SerializedItemStackJsonNbt(auctionItem.getSerializedItemStack().getItemStack());
					configuredStack.getOrCreateComponent().putObject(getPluginContainer(), "uuid", auctionItem.getStackUUID().toString());
					List<Component> itemLore = configuredStack.getItemStack().get(Keys.LORE).orElse(new ArrayList<Component>());
					if(configuredStack.getItemStack().get(Keys.LORE).isPresent()) {
						configuredStack.getItemStack().remove(Keys.LORE);
					}
					if(!itemLore.isEmpty()) itemLore.add(Component.empty());
					itemLore.add(getItems(player).lore().betClick());
					itemLore.add(getItems(player).lore().buyClick());
					itemLore.add(Component.empty());
					itemLore.add(getItems(player).lore().transactionVariants());
					if(auctionItem.getPrices().get(0).getBet().doubleValue() > 0) {
						itemLore.add(
							getItems(player).lore().auctionBet(
								auctionItem.getPrices().get(0).getCurrency(),
								auctionItem.getPrices().get(0).getBet().doubleValue(),
								auctionItem.getPrices().get(0).getBet().doubleValue() * configuredStack.getItemStack().quantity()
							)
						);
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
							itemLore.add(getItems(player).lore().auctionPrice(price.getCurrency(), price.getPrice().doubleValue(), price.getPrice().doubleValue() * configuredStack.getItemStack().quantity()));
						}
					}
					itemLore.add(Component.empty());
					itemLore.add(Component.empty());
					itemLore.add(getItems(player).lore().expired(auctionItem.getExpireTimeFromNow()));
					itemLore.add(getItems(player).lore().seller(auctionItem.getOwnerName()));
					if(auctionItem.getBetData() != null) {
						if(!auctionItem.betIsNull()) {
							itemLore.add(Component.empty());
							itemLore.add(getItems(player).lore().currentBuyer(auctionItem.getBetData().getBuyerName()));
							itemLore.add(getItems(player).lore().currentBet(auctionItem.getPrices().get(0).getCurrency(), auctionItem.getBetData().getMoney().doubleValue()));
						}
					}
					ItemStack displayStack = configuredStack.getItemStack();
					displayStack.offer(Keys.LORE, itemLore);
					if(!auctionItem.isExpired()) {
						currentItem++;
					}
					slot.offer(displayStack);
				}
			} else if(id <= 53) {
				slot.set(plugin.getFillItems().getItemStack(FillItems.BOTTOM));
				if(id == 45 && page >= 2) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.BACK);
					itemStack.offer(Keys.CUSTOM_NAME, getItems(player).name().back());
					slot.set(itemStack);
				} else if(id == 47) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.ADD);
					itemStack.offer(Keys.CUSTOM_NAME, getItems(player).name().auctionAddItem());
					slot.set(itemStack);
				} else if(id == 49) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
					itemStack.offer(Keys.CUSTOM_NAME, getItems(player).name().changeCurrency());
					itemStack.offer(Keys.LORE, Arrays.asList(getItems(player).lore().currency(currencies.get(editData.priceNumber))));
					slot.set(itemStack);
				} else if(id == 51) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.RETURN);
					itemStack.offer(Keys.CUSTOM_NAME,  getItems(player).name().returnAuctionItem());
					slot.set(itemStack);
				} else if(id == 53 && plugin.getAuctionItems().size() >= (page * 45)) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.NEXT);
					itemStack.offer(Keys.CUSTOM_NAME,  getItems(player).name().next());
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
							if(auctionItem.getOwnerUUID().equals(player.uniqueId())) currentSelling++;
							if(currentSelling >= 53) break;
						}
						if(currentSelling >= 53) {
							player.sendMessage(getMessages(player).maxVolume());
							return false;
						}
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							editItem(player, page);
						}).build());
					} else if(slotIndex == 49) {
						editData.nextPrice(currencies.size());
						ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
						itemStack.offer(Keys.CUSTOM_NAME,  getItems(player).name().changeCurrency());
						itemStack.offer(Keys.LORE, Arrays.asList(getItems(player).lore().currency(currencies.get(editData.priceNumber))));
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
						SerializedItemStackJsonNbt itemStack = new SerializedItemStackJsonNbt(slot.peek());
						if(!itemStack.getOrCreateComponent().containsComponent(getPluginContainer(), "uuid")) return true;
						String uuid = itemStack.getOrCreateComponent().getObject(getPluginContainer(), "uuid", null);
						if(uuid == null) return true;
						UUID stackUUID = UUID.fromString(uuid);
						if(!plugin.getAuctionItems().containsKey(stackUUID)) {
							player.sendMessage(getMessages(player).itemNotFound());
							Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
								slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
							}).build());
						}
						if(plugin.getAuctionItems().get(stackUUID).getOwnerUUID().equals(player.uniqueId())) {
							player.sendMessage(getMessages(player).cancelBuy());
							return false;
						}
						if(clickType == ClickTypes.CLICK_LEFT.get()) {
							Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
								editBet(player, page, stackUUID);
							}).build());
						} else if(clickType == ClickTypes.CLICK_RIGHT.get()) {
							SerializedAuctionStack auctionItem = plugin.getAuctionItems().get(stackUUID);
							if(!auctionItem.containsCurrency(currencies.get(editData.priceNumber)) || auctionItem.getPrices().get(editData.priceNumber).getPrice().doubleValue() == 0 || auctionItem.getSerializedItemStack().getQuantity() > calculateMaxBuyItems(player, auctionItem.getSerializedItemStack().getItemStack(), auctionItem.getPrices().get(editData.priceNumber))) {
								return false;
							}
							if(plugin.getEconomy().checkPlayerBalance(player.uniqueId(), auctionItem.getPrices().get(editData.priceNumber).getCurrency(), auctionItem.getPrices().get(editData.priceNumber).getPrice())) {
								if(!plugin.getEconomy().auctionTransaction(player.uniqueId(), auctionItem, editData.priceNumber, false)) {
									player.sendMessage(getExceptions(player).noMoney());
									return false;
								}
								slot.set(plugin.getFillItems().getItemStack(FillItems.BASIC));
								ItemStack toOffer = auctionItem.getSerializedItemStack().getItemStack();
								plugin.getAuctionStorage().removeAuctionStack(auctionItem.getStackUUID());
								plugin.getAuctionItems().remove(stackUUID);
								player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).offer(toOffer);
							} else player.sendMessage(getExceptions(player).noMoney());
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
		Component menuTitle = getAuctionGui(player).bet();
		EditData editData = new EditData();
		Currency currency = plugin.getAuctionItems().get(idAuctionItem).getPrices().get(0).getCurrency();
		editData.itemStack = plugin.getAuctionItems().get(idAuctionItem).getSerializedItemStack().getItemStack();
		SerializedBetData oldBetData = plugin.getAuctionItems().get(idAuctionItem).getBetData();
		BigDecimal minimalBet = oldBetData == null ? plugin.getAuctionItems().get(idAuctionItem).getPrices().get(0).getBet() : oldBetData.getMoney().add(BigDecimal.valueOf(0.01));
		SerializedBetData betData = new SerializedBetData(serverName, player.uniqueId(), player.name(), minimalBet, currency);
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X3).completeStructure().carrier(player).plugin(plugin.getPluginContainer()).build();
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
				Component price = Component.text("0.01");
				if(id == 1) {
					price = Component.text("0.1");
				} else if(id == 2) {
					price = Component.text("0.5");
				} else if(id == 3) {
					price = Component.text("1");
				} else if(id == 4) {
					price = Component.text("5");
				} else if(id == 5) {
					price = Component.text("10");
				} else if(id == 6) {
					price = Component.text("100");
				} else if(id == 7) {
					price = Component.text("1000");
				} else if(id == 8) {
					price = Component.text("10000");
				}
				ItemStack changePrice = plugin.getFillItems().getItemStack(FillItems.valueOf("CHANGEPRICE" + id));
				changePrice.offer(Keys.LORE, getItems(player).lore().changePrice());
				changePrice.offer(Keys.CUSTOM_NAME, getItems(player).name().price(price));
				slot.set(changePrice);
			} else if(id == 18) {
				ItemStack back = plugin.getFillItems().getItemStack(FillItems.BACK);
				back.offer(Keys.CUSTOM_NAME, getItems(player).name().back());
				slot.set(back);
			} else if(id == 26) {
				ItemStack exit = plugin.getFillItems().getItemStack(FillItems.EXIT);
				exit.offer(Keys.CUSTOM_NAME, getItems(player).name().exit());
				slot.set(exit);
			}
		}
		menu.registerSlotClick(new SlotClickHandler() {
			@Override
			public boolean handle(Cause cause, Container container, Slot slot, int slotIndex, ClickType<?> clickType) {
				if(menu.inventory().containsChild(slot) && slotIndex <= 26) {
					if(clickType != ClickTypes.CLICK_LEFT.get() && clickType != ClickTypes.CLICK_RIGHT.get()) return false;
					if(!plugin.getAuctionItems().containsKey(idAuctionItem)) {
						player.sendMessage(getMessages(player).itemNotFound());
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
							plugin.getAuctionStorage().saveAuctionStack(plugin.getAuctionItems().get(idAuctionItem));
						} else player.sendMessage(getMessages(player).betIsNotSet());
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							createInventory(player, page, plugin.getAuctionItems().values().stream().collect(Collectors.toList()));
						}).build());
					} else if(slotIndex == 26) {
						closePlayerInventory(player);
						if(betData.getMoney().doubleValue() > minimalBet.doubleValue() && plugin.getEconomy().checkPlayerBalance(player.uniqueId(), currency, betData.getMoney())) {
							plugin.getAuctionItems().get(idAuctionItem).setBetData(betData);
							plugin.getAuctionStorage().saveAuctionStack(plugin.getAuctionItems().get(idAuctionItem));
						} else player.sendMessage(getMessages(player).betIsNotSet());
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
		menuTitle = getAuctionGui(player).edit();
		EditData editData = new EditData();
		List<SerializedAuctionPrice> prices = new ArrayList<SerializedAuctionPrice>();
		prices.add(new SerializedAuctionPrice(plugin.getEconomyService().defaultCurrency()));
		for(Currency currency : plugin.getEconomy().getCurrencies()) if(Permissions.auctionCurrencyPermission(player, currency, false) && !currency.equals(plugin.getEconomyService().defaultCurrency())) prices.add(new SerializedAuctionPrice(currency));;
		SerializedAuctionStack auctionStack = new SerializedAuctionStack(editData.itemStack.copy(), prices, player.uniqueId(), player.name(), System.currentTimeMillis() + plugin.getExpire(editData.expire).getTime(), serverName);
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X3).completeStructure().carrier(player).plugin(plugin.getPluginContainer()).build();
		InventoryMenu menu = viewableInventory.asMenu();
		menu.setTitle(menuTitle);
		menu.setReadOnly(true);
		for(Slot slot : menu.inventory().slots()) {
			int id = slot.get(Keys.SLOT_INDEX).get();
			if(id != 13) {
				slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
			}
			if(id <= 8) {
				Component price = Component.text("0.01");
				if(id == 1) {
					price = Component.text("0.1");
				}
				if(id == 2) {
					price = Component.text("0.5");
				}
				if(id == 3) {
					price = Component.text("1");
				}
				if(id == 4) {
					price = Component.text("5");
				}
				if(id == 5) {
					price = Component.text("10");
				}
				if(id == 6) {
					price = Component.text("100");
				}
				if(id == 7) {
					price = Component.text("1000");
				}
				if(id == 8) {
					price = Component.text("10000");
				}
				ItemStack changePrice = plugin.getFillItems().getItemStack(FillItems.valueOf("CHANGEPRICE" + id));
				changePrice.offer(Keys.LORE, getItems(player).lore().changePrice());
				changePrice.offer(Keys.CUSTOM_NAME, getItems(player).name().price(price));
				slot.set(changePrice);
			}
			if(id == 18) {
				ItemStack back = plugin.getFillItems().getItemStack(FillItems.BACK);
				back.offer(Keys.CUSTOM_NAME, getItems(player).name().back());
				slot.set(back);
			}
			if(id == 21) {
				ItemStack clear = plugin.getFillItems().getItemStack(FillItems.CLEAR);
				clear.offer(Keys.CUSTOM_NAME, getItems(player).name().clear());
				slot.set(clear);
			}
			if(id == 22) {
				ItemStack switchMode = plugin.getFillItems().getItemStack(FillItems.SWITCHMODE);
				switchMode.offer(Keys.CUSTOM_NAME, getItems(player).name().switchMode());
				switchMode.offer(Keys.LORE, getItems(player).lore().auctionSwitchMode());
				slot.set(switchMode);
			}
			if(id == 23) {
				ItemStack changeCurrency = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
				changeCurrency.offer(Keys.CUSTOM_NAME, getItems(player).name().changeCurrency());
				slot.set(changeCurrency);
			}
			if(id == 26) {
				ItemStack exit = plugin.getFillItems().getItemStack(FillItems.EXIT);
				exit.offer(Keys.CUSTOM_NAME, getItems(player).name().exit());
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
						for(SerializedAuctionPrice price : prices) price.setZero();
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
							} else editData.expire = 0;
						}
						menu.inventory().slot(13).get().set(getDisplayItem(player, auctionStack, editData));
					}
					if(slotIndex == 23) {
						editData.nextPrice(prices.size());
						menu.inventory().slot(13).get().set(getDisplayItem(player, auctionStack, editData));
					}
					if(slotIndex == 26) {
						closePlayerInventory(player);
						if(!editData.itemStack.type().equals(ItemTypes.AIR.get()) && editData.save) {
							addItem(player, auctionStack);
						}
					}
				} else {
					if(slot.totalQuantity() > 0 && !prices.isEmpty()) {
						SerializedItemStackJsonNbt serializedItemStack = new SerializedItemStackJsonNbt(slot.peek());
						if(plugin.maskIsBlackList(serializedItemStack.getItemTypeAsString()) || plugin.itemIsBlackList(slot.peek())) {
							player.sendMessage(getMessages(player).itemBlocked());
							return false;
						}
						if(checkNbtLength(auctionStack)) {
							player.sendMessage(getMessages(player).longNBT());
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
		menuTitle = getAuctionGui(player).returnItems();
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X6).completeStructure().carrier(player).plugin(plugin.getPluginContainer()).build();
		InventoryMenu menu = viewableInventory.asMenu();
		menu.setTitle(menuTitle);
		menu.setReadOnly(true);
		for(SerializedAuctionStack auctionItem : plugin.getAuctionItems().values()) {
			if(auctionItem.getServerName().equals(serverName) && auctionItem.getOwnerUUID().equals(player.uniqueId())) {
				SerializedItemStackJsonNbt itemStack = new SerializedItemStackJsonNbt(auctionItem.getSerializedItemStack().getItemStack());
				itemStack.getOrCreateComponent().putObject(getPluginContainer(), "uuid", auctionItem.getStackUUID().toString());
				menu.inventory().offer(itemStack.getItemStack());
			}
		}
		menu.registerSlotClick(new SlotClickHandler() {
			@Override
			public boolean handle(Cause cause, Container container, Slot slot, int slotIndex, ClickType<?> clickType) {
				if(menu.inventory().containsChild(slot) && slotIndex <= 53 && slot.totalQuantity() > 0) {
					SerializedItemStackJsonNbt itemStack = new SerializedItemStackJsonNbt(slot.peek());
					if(itemStack.getOrCreateComponent().containsComponent(getPluginContainer(), "uuid")) {
						String s = itemStack.getOrCreateComponent().getObject(getPluginContainer(), "uuid", null);
						if(s == null) return true;
						UUID uuid = UUID.fromString(s);
						if(uuid != null && plugin.getAuctionItems().containsKey(uuid) && player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).freeCapacity() > 0) {
							slot.clear();
							ItemStack toOffer = plugin.getAuctionItems().get(uuid).getSerializedItemStack().getItemStack();
							plugin.getAuctionItems().remove(uuid);
							plugin.getAuctionStorage().removeAuctionStack(uuid);
							player.inventory().offer(toOffer);
						}
					}
				}
				if(menu.inventory().totalQuantity() == 0) closePlayerInventory(player);
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
		auctionStack.getPrices().get(editData.priceNumber).setTax(plugin.getExpire(editData.expire).getTax(), auctionStack.getSerializedItemStack().getQuantity());
		if(auctionStack.getBetData() != null)auctionStack.getBetData().setTax(plugin.getExpire(editData.expire).getTax(), auctionStack.getSerializedItemStack().getQuantity());
		auctionStack.updateExpires(plugin.getExpire(editData.expire).getTime());
		lore.add(getItems(player).lore().currency(editData.bet ? auctionStack.getPrices().get(0).getCurrency() : auctionStack.getPrices().get(editData.priceNumber).getCurrency()));
		if(auctionStack.getPrices().get(0).getBet().doubleValue() > 0) {
			lore.add(Component.empty());
			lore.add(
				getItems(player).lore().auctionBet(
					auctionStack.getPrices().get(0).getCurrency(),
					auctionStack.getPrices().get(0).getBet().doubleValue(),
					auctionStack.getPrices().get(0).getBet().doubleValue() * itemStack.quantity()
				)
			);
			lore.add(Component.empty());
		}
		boolean addEmpty = false;
		for(SerializedAuctionPrice price : auctionStack.getPrices()) {
			if(price.getPrice().doubleValue() > 0) {
				addEmpty = true;
				lore.add(getItems(player).lore().auctionPrice(price.getCurrency(), price.getPrice().doubleValue(), price.getPrice().doubleValue() * itemStack.quantity()));
			}
		}
		if(plugin.getExpire(editData.expire).isTax()) {
			if(addEmpty) {
				lore.add(Component.empty());
				addEmpty = false;
			}
			if(editData.bet) {
				lore.add(getItems(player).lore().tax(auctionStack.getPrices().get(0).getCurrency(), auctionStack.getBetData() == null ? auctionStack.getPrices().get(0).getBetTax() : auctionStack.getBetData().getTax()));
			} else lore.add(getItems(player).lore().tax(auctionStack.getPrices().get(editData.priceNumber).getCurrency(), auctionStack.getPrices().get(editData.priceNumber).getTax()));
		}
		if(plugin.getExpire(editData.expire).isFee()) {
			if(addEmpty) lore.add(Component.empty());
			lore.add(getItems(player).lore().fee(auctionStack.getPrices().get(0).getCurrency(), plugin.getExpire(editData.expire).getFee()));
			lore.add(Component.empty());
		}
		lore.add(getItems(player).lore().expired(auctionStack.getExpireTimeFromNow()));
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
		lore.add(getItems(player).lore().yourBet(betData.getCurrency(), betData.getMoney().doubleValue(), betData.getMoney().doubleValue() * itemStack.quantity()));
		itemStack.offer(Keys.LORE, lore);
		if(plugin.getExpire(editData.expire).isTax()) {
			betData.setTax(plugin.getExpire(editData.expire).getTax(), itemStack.quantity());
			lore.add(getItems(player).lore().tax(betData.getCurrency(), betData.getTax()));
		}
		return itemStack;
	}

	private Integer calculateMaxBuyItems(ServerPlayer player, ItemStack itemStack, SerializedAuctionPrice serializedPrice) {
		if(plugin.getEconomy().getPlayerBalance(player.uniqueId(), serializedPrice.getCurrency()).doubleValue() < serializedPrice.getPrice().doubleValue()) return 0;
		int value = player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).freeCapacity() * itemStack.maxStackQuantity();
		for(Slot playerSlot : player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).slots()) {
			if(playerSlot.contains(itemStack)) {
				int difference = itemStack.maxStackQuantity() - playerSlot.peek().quantity();
				if(playerSlot.peek().quantity() != itemStack.maxStackQuantity()) value = value + difference;
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

	private void closePlayerInventory(ServerPlayer player) {
		Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(4)).plugin(plugin.getPluginContainer()).execute(() -> {
			player.closeInventory();
		}).build());
	}

	private void addItem(ServerPlayer player, SerializedAuctionStack auctionStack) {
		if(plugin.maskIsBlackList(new SerializedItemStackPlainNBT(auctionStack.getSerializedItemStack().getItemStack()).getItemTypeAsString()) || plugin.itemIsBlackList(auctionStack.getSerializedItemStack().getItemStack())) {
			player.sendMessage(getMessages(player).itemBlocked());
			return;
		}
		if(player.inventory().query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY.get().of(auctionStack.getSerializedItemStack().getItemStack())).totalQuantity() < auctionStack.getSerializedItemStack().getQuantity()) {
			player.sendMessage(plugin.getLocales().getLocale(player).commands().exceptions().itemNotPresent());
			return;
		}
		if(checkNbtLength(auctionStack)) {
			player.sendMessage(getMessages(player).longNBT());
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
		player.sendMessage(getMessages(player).itemAdded());
	}

	private boolean checkNbtLength(SerializedAuctionStack auctionStack) {
		return auctionStack.getSerializedItemStack().getComponents() != null && auctionStack.getSerializedItemStack().getComponents().toString().length() > plugin.getConfig().getAuction().getComponentLimit();
	}

	private PluginContainer getPluginContainer() {
		return plugin.getPluginContainer();
	}

	private Gui.Auction getAuctionGui(ServerPlayer player) {
		return plugin.getLocales().getLocale(player).gui().auction();
	}

	private Messages.Auction getMessages(ServerPlayer player) {
		return plugin.getLocales().getLocale(player).messages().auction();
	}

	private Messages.Exceptions getExceptions(ServerPlayer player) {
		return plugin.getLocales().getLocale(player).messages().exceptions();
	}

	private Items getItems(ServerPlayer player) {
		return plugin.getLocales().getLocale(player).items();
	}

	private class EditData {
		boolean save = false;
		boolean bet = true;
		int expire = 0;
		int priceNumber = 0;
		ItemStack itemStack = ItemStack.of(ItemTypes.AIR);
		void nextPrice(int pricesSize) {
			priceNumber = priceNumber + 1;
			if(pricesSize <= priceNumber) priceNumber = 0;
		}
	}

}
