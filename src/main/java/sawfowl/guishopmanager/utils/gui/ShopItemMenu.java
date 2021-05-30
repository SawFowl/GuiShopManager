package sawfowl.guishopmanager.utils.gui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.text.Text;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.configure.FillItems;
import sawfowl.guishopmanager.utils.data.shop.ShopItem;
import sawfowl.guishopmanager.utils.data.shop.ShopMenuData;
import sawfowl.guishopmanager.utils.serialization.shop.SerializedShopPrice;

public class ShopItemMenu {

	private GuiShopManager plugin;
	public ShopItemMenu(GuiShopManager guiShopManager) {
		this.plugin = guiShopManager;
	}

	public void editItem(ShopMenuData shopMenu, Player player, String shopId, int menuID, int shopSlot, ItemStack itemStack, boolean buy) {
		Text menuTitle = Text.of("Edit Prices");
		EditData editData = new EditData();
		editData.buy = buy;
		List<SerializedShopPrice> prices = new ArrayList<SerializedShopPrice>();
		if(!shopMenu.containsShopItem(shopSlot) || shopMenu.getShopItem(shopSlot).getPrices().isEmpty()) {
			prices.add(new SerializedShopPrice(plugin.getEconomyService().getDefaultCurrency()));
			for(Currency currency : plugin.getEconomyService().getCurrencies()) {
				if(!currency.getId().equals(plugin.getEconomyService().getDefaultCurrency().getId())) {
					prices.add(new SerializedShopPrice(currency));
				}
			}
		} else {
			prices.addAll(shopMenu.getShopItem(shopSlot).getPrices());
			for(SerializedShopPrice serializedShopPrice : shopMenu.getShopItem(shopSlot).getPrices()) {
				if(serializedShopPrice.getCurrency().getId().equals(plugin.getEconomyService().getDefaultCurrency().getId())) {
					prices.remove(serializedShopPrice);
					prices.add(0, serializedShopPrice);
				}
			}
		}
		if(buy) {
			menuTitle = plugin.getLocales().getLocalizedText(player.getLocale(), "Gui", "EditBuyItem");
		} else {
			menuTitle = plugin.getLocales().getLocalizedText(player.getLocale(), "Gui", "EditSellItem");
		}
		Inventory inventory = Inventory.builder().of(InventoryArchetypes.CHEST)
				.property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(menuTitle))
				.listener(ClickInventoryEvent.class, event -> {
					if(event.getTransactions().isEmpty()) {
						return;
					}
					Optional<SlotIndex> slotIndex = event.getTransactions().get(0).getSlot().getInventoryProperty(SlotIndex.class);
					int id = slotIndex.get().getValue();
					event.setCancelled(true);
					if(id == 18) {
						if(!plugin.shopExists(shopId)) {
							player.sendMessage(Text.of(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ShopIDNotExists"), " ", shopId));
							Task.builder().delayTicks(5).execute(() -> {
								player.closeInventory();
							}).submit(plugin);
							return;
						}
						if(editData.remove) {
							plugin.getShop(shopId).getShopMenuData(menuID).removeItem(shopSlot);
						} else {
							if(editData.itemStack.getType() != ItemTypes.AIR) {
								plugin.getShop(shopId).getShopMenuData(menuID).addOrUpdateItem(shopSlot, new ShopItem(editData.itemStack, prices));
							}
						}
						plugin.getWorkShopData().saveShop(shopId);
						Task.builder().delayTicks(5).execute(() -> {
							player.closeInventory();
							plugin.getShopMenu().createInventoryToEditor(shopMenu, player, shopId, menuID);
						}).submit(plugin);
					}
					if(id == 21) {
						for(SerializedShopPrice price : prices) {
							price.setZero();
							editData.remove = true;
						}
					} else {
						for(SerializedShopPrice price : prices) {
							if(price.getBuyPrice().doubleValue() > 0 || price.getSellPrice().doubleValue() > 0) {
								editData.remove = false;
								break;
							}
						}
					}
					if(id == 22) {
						Task.builder().delayTicks(5).execute(() -> {
							player.closeInventory();
							shopMenu.addOrUpdateItem(shopSlot, new ShopItem(editData.itemStack, prices));
							editItem(shopMenu, player, shopId, menuID, shopSlot, editData.itemStack, !buy);
						}).submit(plugin);
					}
					if(id == 26) {
						if(!plugin.shopExists(shopId)) {
							player.sendMessage(Text.of(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ShopIDNotExists"), " ", shopId));
							Task.builder().delayTicks(5).execute(() -> {
								player.closeInventory();
							}).submit(plugin);
							return;
						}
						if(editData.remove) {
							plugin.getShop(shopId).getShopMenuData(menuID).removeItem(shopSlot);
						} else {
							if(editData.itemStack.getType() != ItemTypes.AIR) {
								plugin.getShop(shopId).getShopMenuData(menuID).addOrUpdateItem(shopSlot, new ShopItem(editData.itemStack, prices));
							}
						}
						plugin.getWorkShopData().saveShop(shopId);
						Task.builder().delayTicks(5).execute(() -> {
							player.closeInventory();
						}).submit(plugin);
					}
					if(id > 26) {
						Inventory update = event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(13)));
						if(!event.getCursorTransaction().getDefault().isEmpty()) {
							editData.itemStack = event.getCursorTransaction().getDefault().createStack().copy();
							editData.itemStack.setQuantity(1);
							update.set(updateDisplayItemEdit(player, prices, editData));
						}
					}
					if(event instanceof ClickInventoryEvent.Primary) {
						if(id <= 8) {
							editData.remove = false;
							if(id == 0) {
								prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).add(BigDecimal.valueOf(0.01)), buy);
							}
							if(id == 1) {
								prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).add(BigDecimal.valueOf(0.1)), buy);
							}
							if(id == 2) {
								prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).add(BigDecimal.valueOf(0.5)), buy);
							}
							if(id == 3) {
								prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).add(BigDecimal.valueOf(1)), buy);
							}
							if(id == 4) {
								prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).add(BigDecimal.valueOf(5)), buy);
							}
							if(id == 5) {
								prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).add(BigDecimal.valueOf(10)), buy);
							}
							if(id == 6) {
								prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).add(BigDecimal.valueOf(100)), buy);
							}
							if(id == 7) {
								prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).add(BigDecimal.valueOf(1000)), buy);
							}
							if(id == 8) {
								prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).add(BigDecimal.valueOf(10000)), buy);
							}
						}
						if(id <= 8 || id == 13 || id == 21 || id == 23) {
							if(id == 23) {
								if(editData.priceNumber >= prices.size() - 1) {
									editData.priceNumber = 0;
								} else {
									editData.priceNumber++;
								}
							}
							Inventory update = event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(13)));
							Slot slot13 = update.first();
							if(update.peek().isPresent()) {
								slot13.set(updateDisplayItemEdit(player, prices, editData));
							}
						}
					} else if(event instanceof ClickInventoryEvent.Secondary) {
						if(id == 0) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).subtract(BigDecimal.valueOf(0.01)), buy);
						}
						if(id == 1) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).subtract(BigDecimal.valueOf(0.1)), buy);
						}
						if(id == 2) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).subtract(BigDecimal.valueOf(0.5)), buy);
						}
						if(id == 3) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).subtract(BigDecimal.valueOf(1)), buy);
						}
						if(id == 4) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).subtract(BigDecimal.valueOf(5)), buy);
						}
						if(id == 5) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).subtract(BigDecimal.valueOf(10)), buy);
						}
						if(id == 6) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).subtract(BigDecimal.valueOf(100)), buy);
						}
						if(id == 7) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).subtract(BigDecimal.valueOf(1000)), buy);
						}
						if(id == 8) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(prices.get(editData.priceNumber).getBuyOrSellPrice(buy).subtract(BigDecimal.valueOf(10000)), buy);
						}
						if(id <= 8 || id == 13 || id == 21 || id == 23) {
							if(id <= 8 && prices.get(editData.priceNumber).getBuyOrSellPrice(buy).doubleValue() < 0) {
								prices.get(editData.priceNumber).setBuyOrSellPrice(BigDecimal.ZERO, buy);
							}
							if(id == 23) {
								if(editData.priceNumber >= prices.size() - 1) {
									editData.priceNumber = 0;
								} else {
									editData.priceNumber++;
								}
							}
							Inventory update = event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(13)));
							Slot slot13 = update.first();
							if(update.peek().isPresent()) {
								slot13.set(updateDisplayItemEdit(player, prices, editData));
							}
						}
					}
				}).build(plugin);
		for(Inventory slot : inventory.slots()) {
			int id = slot.getInventoryProperty(SlotIndex.class).get().getValue();
			if(id != 13) {
				slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
			}
			if(id <= 8) {
				Text price = Text.of(" 0.01");
				if(id == 1) {
					price = Text.of(" 0.1");
				}
				if(id == 2) {
					price = Text.of(" 0.5");
				}
				if(id == 3) {
					price = Text.of(" 1");
				}
				if(id == 4) {
					price = Text.of(" 5");
				}
				if(id == 5) {
					price = Text.of(" 10");
				}
				if(id == 6) {
					price = Text.of(" 100");
				}
				if(id == 7) {
					price = Text.of(" 1000");
				}
				if(id == 8) {
					price = Text.of(" 10000");
				}
				ItemStack changePrice = plugin.getFillItems().getItemStack(FillItems.valueOf("CHANGEPRICE" + id));
				changePrice.offer(Keys.ITEM_LORE, plugin.getLocales().getLocalizedListText(player.getLocale(), "Lore", "ChangePrice"));
				changePrice.offer(Keys.DISPLAY_NAME, Text.of(plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Price").replace("%value%", Text.of(price))));
				slot.set(changePrice);
			}
			if(id == 13 && itemStack != null) {
				editData.itemStack = itemStack;
				slot.offer(updateDisplayItemEdit(player, prices, editData));
			}
			if(id == 18) {
				ItemStack back = plugin.getFillItems().getItemStack(FillItems.BACK);
				back.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Back"));
				slot.set(back);
			}
			if(id == 21) {
				ItemStack clear = plugin.getFillItems().getItemStack(FillItems.CLEAR);
				clear.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Clear"));
				slot.set(clear);
			}
			if(id == 22) {
				ItemStack switchMode = plugin.getFillItems().getItemStack(FillItems.SWITCHMODE);
				switchMode.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "SwitchMode"));
				slot.set(switchMode);
			}
			if(id == 23) {
				ItemStack changeCurrency = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
				changeCurrency.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "ChangeCurrency"));
				slot.set(changeCurrency);
			}
			if(id == 26) {
				ItemStack exit = plugin.getFillItems().getItemStack(FillItems.EXIT);
				exit.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Exit"));
				slot.set(exit);
			}
		}
		player.openInventory(inventory);
	}

	public void transactionItem(ShopMenuData shopMenu, Player player, String shopId, int menuID, int shopSlot, ItemStack itemStack, boolean buy) {
		Text menuTitle = Text.of("Edit Transaction");
		EditData editData = new EditData();
		editData.buy = buy;
		List<SerializedShopPrice> prices = new ArrayList<SerializedShopPrice>();
		for(SerializedShopPrice serializedPrice : shopMenu.getShopItem(shopSlot).getPrices()) {
			if(buy) {
				if(shopMenu.getShopItem(shopSlot).isBuyForPrice(serializedPrice)) {
					if(serializedPrice.getCurrency().getId().equals(plugin.getEconomyService().getDefaultCurrency().getId())) {
						prices.add(0, serializedPrice);
					} else {
						prices.add(serializedPrice);
					}
				}
			} else {
				if(shopMenu.getShopItem(shopSlot).isSellForPrice(serializedPrice)) {
					if(serializedPrice.getCurrency().getId().equals(plugin.getEconomyService().getDefaultCurrency().getId())) {
						prices.add(0, serializedPrice);
					} else {
						prices.add(serializedPrice);
					}
				}
			}
		}
		if(buy) {
			menuTitle = plugin.getLocales().getLocalizedText(player.getLocale(), "Gui", "EditBuyTransaction");
		} else {
			menuTitle = plugin.getLocales().getLocalizedText(player.getLocale(), "Gui", "EditSellTransaction");
		}
		Inventory inventory = Inventory.builder().of(InventoryArchetypes.CHEST)
				.property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(menuTitle))
				.listener(ClickInventoryEvent.class, event -> {
					if(event.getTransactions().isEmpty()) {
						return;
					}
					Optional<SlotIndex> slotIndex = event.getTransactions().get(0).getSlot().getInventoryProperty(SlotIndex.class);
					int id = slotIndex.get().getValue();
					event.setCancelled(true);
					if(id == 18) {
						Task.builder().delayTicks(5).execute(() -> {
							if(editData.size > 0) {
								if(buy) {
									editData.itemStack.setQuantity(editData.size);
									player.getInventory().offer(editData.itemStack.copy());
									plugin.getEconomy().removeFromPlayerBalance(player, prices.get(editData.priceNumber).getCurrency(), prices.get(editData.priceNumber).getBuyPrice(), editData.itemStack);
								} else {
									player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(editData.itemStack.copy())).poll(editData.size);
									editData.itemStack.setQuantity(editData.size);
									plugin.getEconomy().addToPlayerBalance(player, prices.get(editData.priceNumber).getCurrency(), prices.get(editData.priceNumber).getSellPrice(), editData.itemStack);
								}
							}
							player.closeInventory();
							plugin.getShopMenu().createInventoryToPlayer(shopMenu, player, shopId, menuID);
							
						}).submit(plugin);
					}
					if(id == 22) {
						if(buy) {
							if(shopMenu.getShopItem(shopSlot).isSell()) {
								Task.builder().delayTicks(5).execute(() -> {
									player.closeInventory();
									transactionItem(shopMenu, player, shopId, menuID, shopSlot, editData.itemStack, !buy);
								}).submit(plugin);
							}
						} else {
							if(shopMenu.getShopItem(shopSlot).isBuy()) {
								Task.builder().delayTicks(5).execute(() -> {
									player.closeInventory();
									transactionItem(shopMenu, player, shopId, menuID, shopSlot, editData.itemStack, !buy);
								}).submit(plugin);
							}
						}
					}
					if(id == 26) {
						Task.builder().delayTicks(5).execute(() -> {
							if(editData.size > 0) {
								if(buy) {
									editData.itemStack.setQuantity(editData.size);
									player.getInventory().offer(editData.itemStack.copy());
									plugin.getEconomy().removeFromPlayerBalance(player, prices.get(editData.priceNumber).getCurrency(), prices.get(editData.priceNumber).getBuyPrice(), editData.itemStack);
								} else {
									player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(editData.itemStack.copy())).poll(editData.size);
									editData.itemStack.setQuantity(editData.size);
									plugin.getEconomy().addToPlayerBalance(player, prices.get(editData.priceNumber).getCurrency(), prices.get(editData.priceNumber).getSellPrice(), editData.itemStack);
								}
							}
							player.closeInventory();
						}).submit(plugin);
					}
					if(event instanceof ClickInventoryEvent.Primary) {
						if(id == 0) {
							editData.size++;
						}
						if(id == 1) {
							editData.size = editData.size + 2;
						}
						if(id == 2) {
							editData.size = editData.size + 4;
						}
						if(id == 3) {
							editData.size = editData.size + 8;
						}
						if(id == 4) {
							editData.size = editData.size + 16;
						}
						if(id == 5) {
							editData.size = editData.size + 32;
						}
						if(id == 6) {
							editData.size = editData.size + 64;
						}
						if(id == 7) {
							editData.size = editData.size + 128;
						}
						if(id == 8) {
							if(buy) {
								editData.size = calculateMaxBuyItems(player, itemStack, prices.get(editData.priceNumber));
							} else {
								editData.size = totalItemsInPlayerInventory(player, itemStack);
							}
						}
						if(id <= 8 || id == 13 || id == 21 || id == 23) {
							if(buy && editData.size > calculateMaxBuyItems(player, itemStack, prices.get(editData.priceNumber))) editData.size = calculateMaxBuyItems(player, itemStack, prices.get(editData.priceNumber));
							if(!buy && editData.size > totalItemsInPlayerInventory(player, itemStack)) editData.size = totalItemsInPlayerInventory(player, itemStack);
							if(id == 23) {
								if(editData.priceNumber >= prices.size() - 1) {
									editData.priceNumber = 0;
								} else {
									editData.priceNumber++;
								}
								if(buy) {
									int check = calculateMaxBuyItems(player, itemStack, prices.get(editData.priceNumber));
									if(editData.size > check) {
										editData.size = check;
									}
								}
							}
							if(id == 21) {
								editData.size = 0;
							}
							Inventory update = event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(13)));
							Slot slot13 = update.first();
							if(update.peek().isPresent()) {
								slot13.set(updateDisplayItemTransaction(player, prices, editData));
							}
						}
					} else if(event instanceof ClickInventoryEvent.Secondary) {
						if(id == 0) {
							editData.size--;
						}
						if(id == 1) {
							editData.size = editData.size - 2;
						}
						if(id == 2) {
							editData.size = editData.size - 4;
						}
						if(id == 3) {
							editData.size = editData.size - 8;
						}
						if(id == 4) {
							editData.size = editData.size - 16;
						}
						if(id == 5) {
							editData.size = editData.size - 32;
						}
						if(id == 6) {
							editData.size = editData.size - 64;
						}
						if(id == 7) {
							editData.size = editData.size - 128;
						}
						if(id == 8) {
							editData.size = 0;
						}
						if(id <= 8 || id == 13 || id == 21 || id == 23) {
							if(id == 23) {
								if(editData.priceNumber >= prices.size() - 1) {
									editData.priceNumber = 0;
								} else {
									editData.priceNumber++;
								}
							}
							if(editData.size < 0) {
								editData.size = 0;
							}
							if(id == 21) {
								editData.size = 0;
							}
							Inventory update = event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(13)));
							Slot slot13 = update.first();
							if(update.peek().isPresent()) {
								slot13.set(updateDisplayItemTransaction(player, prices, editData));
							}
						}
					}
				}).build(plugin);
		for(Inventory slot : inventory.slots()) {
			int id = slot.getInventoryProperty(SlotIndex.class).get().getValue();
			if(id != 13) {
				slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
			}
			if(id <= 8) {
				Text price = Text.of(" 1");
				if(id == 1) {
					price = Text.of(" 2");
				}
				if(id == 2) {
					price = Text.of(" 4");
				}
				if(id == 3) {
					price = Text.of(" 8");
				}
				if(id == 4) {
					price = Text.of(" 16");
				}
				if(id == 5) {
					price = Text.of(" 32");
				}
				if(id == 6) {
					price = Text.of(" 64");
				}
				if(id == 7) {
					price = Text.of(" 128");
				}
				if(id == 8) {
					price = Text.of(" MAX");
				}
				ItemStack changeSize = plugin.getFillItems().getItemStack(FillItems.valueOf("CHANGESIZE" + id));
				changeSize.offer(Keys.ITEM_LORE, plugin.getLocales().getLocalizedListText(player.getLocale(), "Lore", "ChangeSize"));
				changeSize.offer(Keys.DISPLAY_NAME, Text.of(plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Size").replace("%value%", Text.of(price))));
				slot.set(changeSize);
			}
			if(id == 13 && itemStack != null) {
				editData.itemStack = itemStack;
				slot.offer(updateDisplayItemTransaction(player, prices, editData));
			}
			if(id == 18) {
				ItemStack back = plugin.getFillItems().getItemStack(FillItems.BACK);
				if(buy) {
					back.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "BuyAndBack"));
				} else {
					back.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "SellAndBack"));
				}
				slot.set(back);
			}
			if(id == 21) {
				ItemStack clear = plugin.getFillItems().getItemStack(FillItems.CLEAR);
				clear.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Clear"));
				slot.set(clear);
			}
			if(id == 22) {
				ItemStack switchMode = plugin.getFillItems().getItemStack(FillItems.SWITCHMODE);
				switchMode.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "SwitchMode"));
				slot.set(switchMode);
			}
			if(id == 23) {
				ItemStack changeCurrency = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
				changeCurrency.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "ChangeCurrency"));
				slot.set(changeCurrency);
			}
			if(id == 26) {
				if(buy) {
					ItemStack buyItem = plugin.getFillItems().getItemStack(FillItems.BUY);
					buyItem.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Buy"));
					slot.set(buyItem);
				} else {
					ItemStack sellItem = plugin.getFillItems().getItemStack(FillItems.SELL);
					sellItem.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Sell"));
					slot.set(sellItem);
				}
			}
		}
		player.openInventory(inventory);
	}

	private ItemStack updateDisplayItemEdit(Player player, List<SerializedShopPrice> prices, EditData editData) {
		ItemStack itemStack = editData.itemStack.copy();
		List<Text> lore = itemStack.get(Keys.ITEM_LORE).orElse(new ArrayList<Text>());
		if(itemStack.get(Keys.ITEM_LORE).isPresent()) {
			itemStack.remove(Keys.ITEM_LORE);
		}
		lore.clear();
		
		lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "CurrentCurrency")
				.replace("%currency%", Text.of(prices.get(editData.priceNumber).getCurrency().getDisplayName())));
		for(SerializedShopPrice price : prices) {
			lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "Price")
					.replace("%currency%", Text.of(price.getCurrency().getDisplayName()))
					.replace("%buyprice%", Text.of(price.getBuyPrice()))
					.replace("%sellprice%", Text.of(price.getSellPrice())));
		}
		itemStack.offer(Keys.ITEM_LORE, lore);
		return itemStack;
	}

	private ItemStack updateDisplayItemTransaction(Player player, List<SerializedShopPrice> prices, EditData editData) {
		ItemStack itemStack = editData.itemStack.copy();
		List<Text> lore = itemStack.get(Keys.ITEM_LORE).orElse(new ArrayList<Text>());
		if(itemStack.get(Keys.ITEM_LORE).isPresent()) {
			itemStack.remove(Keys.ITEM_LORE);
		}
		lore.clear();
		lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "CurrentCurrency")
				.replace("%currency%", Text.of(prices.get(editData.priceNumber).getCurrency().getDisplayName())));
		lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "CurrentSize")
				.replace("%size%", Text.of(editData.size)));
		if(editData.buy) {
			lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "CurrentSum")
					.replace("%size%", Text.of(prices.get(editData.priceNumber).getCurrency().getSymbol(), prices.get(editData.priceNumber).getBuyPrice().multiply(BigDecimal.valueOf(editData.size)))));
		} else {
			lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "CurrentSum")
					.replace("%size%", Text.of(prices.get(editData.priceNumber).getCurrency().getSymbol(), prices.get(editData.priceNumber).getSellPrice().multiply(BigDecimal.valueOf(editData.size)))));
		}
		for(SerializedShopPrice price : prices) {
			if(price.getBuyPrice().doubleValue() > 0 || price.getSellPrice().doubleValue() > 0) {
				lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "Price")
						.replace("%currency%", Text.of(price.getCurrency().getDisplayName()))
						.replace("%buyprice%", Text.of(price.getBuyPrice()))
						.replace("%sellprice%", Text.of(price.getSellPrice())));
			}
		}
		itemStack.offer(Keys.ITEM_LORE, lore);
		return itemStack;
	}

	private Integer calculateMaxBuyItems(Player player, ItemStack itemStack, SerializedShopPrice serializedPrice) {
		if(plugin.getEconomy().getPlayerBalance(player.getUniqueId(), serializedPrice.getCurrency()).doubleValue() < serializedPrice.getBuyPrice().doubleValue()) return 0;
		int value = 0;
		MainPlayerInventory mainPlayerInventory = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class));
		Iterable<Slot> slots = mainPlayerInventory.slots();
		for(Slot playerSlot : slots) {
			if(playerSlot.totalItems() == 0) {
				value = value + itemStack.getMaxStackQuantity();
			}
			if(playerSlot.contains(itemStack)) {
				int difference = itemStack.getMaxStackQuantity() - playerSlot.query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(itemStack)).totalItems();
				if(playerSlot.query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(itemStack)).totalItems() != itemStack.getMaxStackQuantity()) {
					value = value + difference;
				}
			}
		}
		BigDecimal requiredMoney = calculateMoney(itemStack, serializedPrice);
		if(plugin.getEconomy().checkPlayerBalance(player.getUniqueId(), serializedPrice.getCurrency(), requiredMoney) && value >= Double.valueOf(plugin.getEconomy().getPlayerBalance(player.getUniqueId(), serializedPrice.getCurrency()).doubleValue() / requiredMoney.doubleValue()).intValue()) {
			value = Double.valueOf(plugin.getEconomy().getPlayerBalance(player.getUniqueId(), serializedPrice.getCurrency()).doubleValue() / requiredMoney.doubleValue()).intValue();
		}
		return value;
	}

	private BigDecimal calculateMoney(ItemStack itemStack, SerializedShopPrice serializedPrice) {
		BigDecimal price = serializedPrice.getBuyPrice();
		return price.pow(itemStack.getQuantity());
	}

	private Integer totalItemsInPlayerInventory(Player player, ItemStack itemStack) {
		return player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(itemStack)).totalItems();
	}

	private class EditData {
		int priceNumber = 0;
		int size = 0;
		ItemStack itemStack = ItemStack.of(ItemTypes.AIR);
		boolean buy;
		boolean remove = false;
	}
}
