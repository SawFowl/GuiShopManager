package sawfowl.guishopmanager.gui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
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

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.configure.FillItems;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Gui;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Items;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Messages;
import sawfowl.guishopmanager.data.shop.ShopItem;
import sawfowl.guishopmanager.data.shop.ShopMenuData;
import sawfowl.guishopmanager.serialization.shop.SerializedShopPrice;

public class ShopMenus {

	private GuiShopManager plugin;
	public ShopMenus(GuiShopManager instance) {
		this.plugin = instance;
	}

	public void createInventoryToEditor(ShopMenuData shopMenu, ServerPlayer player, String shopId, int menuId) {
		Component menuTitle = menuId == 1 ? plugin.getShop(shopId).getOrDefaultTitle(player.locale()) : plugin.getShop(shopId).getOrDefaultTitle(player.locale()).append(Component.text(" || " + menuId));
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X6).completeStructure().carrier(player).plugin(plugin.getPluginContainer()).build();
		InventoryMenu menu = viewableInventory.asMenu();
		menu.setReadOnly(true);
		menu.setTitle(menuTitle);
		for(Slot slot : menu.inventory().slots()) {
			int id = slot.get(Keys.SLOT_INDEX).get();
			if(id < 45) {
				if(shopMenu.containsShopItem(id)) {
					ShopItem shopItemStack = shopMenu.getShopItem(id);
					ItemStack itemStack = shopItemStack.getItemStack();
					List<Component> itemLore = itemStack.get(Keys.LORE).orElse(new ArrayList<Component>());
					if(itemStack.get(Keys.LORE).isPresent()) {
						itemStack.remove(Keys.LORE);
						itemLore.add(Component.empty());
					}
					itemLore.add(getItems(player).lore().transactionVariants());
					for(SerializedShopPrice serializablePrice : shopItemStack.getPrices()) {
						itemLore.add(getItems(player).lore().price(serializablePrice.getCurrency(), serializablePrice.getBuyPrice().doubleValue(), serializablePrice.getSellPrice().doubleValue()));
					}
					itemLore.add(Component.empty());
					itemStack.offer(Keys.LORE, itemLore);
					slot.offer(itemStack);
				} else slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
			} else if(id <= 53) {
				slot.set(plugin.getFillItems().getItemStack(FillItems.BOTTOM));
				if(id == 45 && plugin.getShop(shopId).hasPreviousExist(menuId)) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.BACK);
					itemStack.offer(Keys.CUSTOM_NAME, getItems(player).name().back());
					slot.set(itemStack);
				}
				if(id == 53) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.NEXT);
					itemStack.offer(Keys.CUSTOM_NAME, getItems(player).name().next());
					slot.set(itemStack);
				}
			}
		}
		Set<Integer> indexes = menu.inventory().slots().stream().map(slot -> slot.get(Keys.SLOT_INDEX).get()).collect(Collectors.toSet());
		menu.registerSlotClick(new SlotClickHandler() {
			@Override
			public boolean handle(Cause cause, Container container, Slot slot, int slotIndex, ClickType<?> clickType) {
				if(indexes.contains(slotIndex) && slotIndex <= 53) {
					if(clickType != ClickTypes.CLICK_LEFT.get() && clickType != ClickTypes.CLICK_RIGHT.get()) return false;
					if(slotIndex == 45 && menuId > 1) {
							Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
								createInventoryToEditor(plugin.getShop(shopId).getShopMenuData(menuId - 1), player, shopId, menuId - 1);
							}).build());
					} else if(slotIndex == 53) {
						int nextMenu = menuId + 1;
						if(!plugin.getShop(shopId).hasNextExist(menuId)) {
							plugin.getShop(shopId).addMenu(nextMenu, new ShopMenuData());
						}
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							createInventoryToEditor(plugin.getShop(shopId).getShopMenuData(nextMenu), player, shopId, nextMenu);
						}).build());
					} else if(slotIndex < 45) {
						if(clickType.equals(ClickTypes.CLICK_LEFT.get())) {
							ItemStack itemStack = null;
							if(shopMenu.containsShopItem(slotIndex)) {
								itemStack = shopMenu.getShopItem(slotIndex).getItemStack();
							}
							ItemStack finalStack = itemStack != null ? itemStack.copy() : null;
							Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
								editItem(shopMenu, player, shopId, menuId, slotIndex, finalStack, true);
							}).build());
						} else if(clickType == ClickTypes.CLICK_RIGHT.get()) {
							ItemStack itemStack = null;
							if(shopMenu.containsShopItem(slotIndex)) {
								itemStack = shopMenu.getShopItem(slotIndex).getItemStack();
							}
							ItemStack finalStack = itemStack != null ? itemStack.copy() : null;
							player.closeInventory();
							Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
								editItem(shopMenu, player, shopId, menuId, slotIndex, finalStack, false);
							}).build());
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

	public void createInventoryToPlayer(ShopMenuData shopMenu, ServerPlayer player, String shopId, int menuId) {
		Component menuTitle = menuId == 1 ? plugin.getShop(shopId).getOrDefaultTitle(player.locale()) : plugin.getShop(shopId).getOrDefaultTitle(player.locale()).append(Component.text(" || " + menuId));
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X6)
				.completeStructure().carrier(player).plugin(plugin.getPluginContainer()).build();
		InventoryMenu menu = viewableInventory.asMenu();
		menu.setReadOnly(true);
		menu.setTitle(menuTitle);
		for(Slot slot : menu.inventory().slots()) {
			int id = slot.get(Keys.SLOT_INDEX).get();
			if(id < 45) {
				if(shopMenu.containsShopItem(id)) {
					ShopItem shopItemStack = shopMenu.getShopItem(id);
					ItemStack itemStack = shopItemStack.getItemStack();
					List<Component> itemLore = itemStack.get(Keys.LORE).orElse(new ArrayList<Component>());
					if(itemStack.get(Keys.LORE).isPresent()) {
						itemStack.remove(Keys.LORE);
						itemLore.add(Component.empty());
					}
					itemLore.add(getItems(player).lore().transactionVariants());
					for(SerializedShopPrice serializablePrice : shopItemStack.getPrices()) {
						itemLore.add(getItems(player).lore().price(serializablePrice.getCurrency(), serializablePrice.getBuyPrice().doubleValue(), serializablePrice.getSellPrice().doubleValue()));
					}
					itemLore.add(Component.empty());
					itemStack.offer(Keys.LORE, itemLore);
					slot.offer(itemStack);
				} else slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
			} else if(id <= 53) {
				slot.set(plugin.getFillItems().getItemStack(FillItems.BOTTOM));
				if(id == 45 && plugin.getShop(shopId).hasPreviousExist(menuId)) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.BACK);
					itemStack.offer(Keys.CUSTOM_NAME, getItems(player).name().back());
					slot.set(itemStack);
				}
				if(id == 53 && plugin.getShop(shopId).hasNextExist(menuId) && !plugin.getShop(shopId).getShopMenuData(menuId + 1).isEmpty()) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.NEXT);
					itemStack.offer(Keys.CUSTOM_NAME, getItems(player).name().next());
					slot.set(itemStack);
				}
			}
		}
		Set<Integer> indexes = menu.inventory().slots().stream().map(slot -> slot.get(Keys.SLOT_INDEX).get()).collect(Collectors.toSet());
		menu.registerSlotClick(new SlotClickHandler() {
			@Override
			public boolean handle(Cause cause, Container container, Slot slot, int slotIndex, ClickType<?> clickType) {
				if(indexes.contains(slotIndex) && slotIndex <= 53) {
					if(clickType != ClickTypes.CLICK_LEFT.get() && clickType != ClickTypes.CLICK_RIGHT.get()) return false;
					if(slotIndex == 45 && menuId > 1) {
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							createInventoryToPlayer(plugin.getShop(shopId).getShopMenuData(menuId - 1), player, shopId, menuId - 1);
						}).build());
					} else if(slotIndex == 53 && plugin.getShop(shopId).hasNextExist(menuId) && !plugin.getShop(shopId).getShopMenuData(menuId + 1).isEmpty()) {
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							createInventoryToPlayer(plugin.getShop(shopId).getShopMenuData(menuId + 1), player, shopId, menuId + 1);
						}).build());
					} else if(slotIndex < 45) {
						if(!shopMenu.containsShopItem(slotIndex)) return false;
						if(clickType == ClickTypes.CLICK_LEFT.get()) {
							if(!shopMenu.getShopItem(slotIndex).isBuy()) return false;
							player.closeInventory();
							Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
								transactionItem(shopMenu, player, shopId, menuId, slotIndex, shopMenu.getShopItem(slotIndex).getItemStack(), true);
							}).build());
						} else if(clickType == ClickTypes.CLICK_RIGHT.get()) {
							if(!shopMenu.getShopItem(slotIndex).isSell()) return false;
							player.closeInventory();
							Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
								transactionItem(shopMenu, player, shopId, menuId, slotIndex, shopMenu.getShopItem(slotIndex).getItemStack(), false);
							}).build());
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

	public void editItem(ShopMenuData shopMenu, ServerPlayer player, String shopId, int menuID, int shopSlot, ItemStack itemStack, boolean buy) {
		Component menuTitle = Component.text("Edit Prices");
		EditData editData = new EditData();
		List<SerializedShopPrice> prices = new ArrayList<SerializedShopPrice>();
		if(!shopMenu.containsShopItem(shopSlot) || shopMenu.getShopItem(shopSlot).getPrices().isEmpty()) {
			prices.add(new SerializedShopPrice(plugin.getEconomyService().defaultCurrency()));
			for(Currency currency : plugin.getEconomy().getCurrencies()) {
				if(!currency.equals(plugin.getEconomyService().defaultCurrency())) {
					prices.add(new SerializedShopPrice(currency));
				}
			}
		} else {
			prices.addAll(shopMenu.getShopItem(shopSlot).getPrices());
			for(SerializedShopPrice serializedShopPrice : shopMenu.getShopItem(shopSlot).getPrices()) {
				if(serializedShopPrice.getCurrency().equals(plugin.getEconomyService().defaultCurrency())) {
					prices.remove(serializedShopPrice);
					prices.add(0, serializedShopPrice);
				}
			}
			for(Currency currency : plugin.getEconomy().getCurrencies()) {
				if(!prices.stream().filter(p -> p.getCurrency().equals(currency)).findFirst().isPresent()) {
					prices.add(new SerializedShopPrice(currency));
				}
			}
		}
		menuTitle = buy ? getGui(player).editBuy() : getGui(player).editSell();
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X3)
			.completeStructure().carrier(player).plugin(plugin.getPluginContainer()).build();
		InventoryMenu menu = viewableInventory.asMenu();
		menu.setReadOnly(true);
		menu.setTitle(menuTitle);
		for(Slot slot : menu.inventory().slots()) {
			int id = slot.get(Keys.SLOT_INDEX).get();
			if(id != 13) {
				slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
			}
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
			} else if(id == 13 && itemStack != null) {
				editData.itemStack = itemStack;
				slot.offer(updateDisplayItemEdit(player, prices, editData));
			} else if(id == 18) {
				ItemStack back = plugin.getFillItems().getItemStack(FillItems.BACK);
				back.offer(Keys.CUSTOM_NAME, getItems(player).name().back());
				slot.set(back);
			} else if(id == 21) {
				ItemStack clear = plugin.getFillItems().getItemStack(FillItems.CLEAR);
				clear.offer(Keys.CUSTOM_NAME, getItems(player).name().clear());
				slot.set(clear);
			} else if(id == 22) {
				ItemStack switchMode = plugin.getFillItems().getItemStack(FillItems.SWITCHMODE);
				switchMode.offer(Keys.CUSTOM_NAME, getItems(player).name().switchMode());
				slot.set(switchMode);
			} else if(id == 23) {
				ItemStack changeCurrency = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
				changeCurrency.offer(Keys.CUSTOM_NAME, getItems(player).name().changeCurrency());
				slot.set(changeCurrency);
			} else if(id == 26) {
				ItemStack exit = plugin.getFillItems().getItemStack(FillItems.EXIT);
				exit.offer(Keys.CUSTOM_NAME, getItems(player).name().exit());
				slot.set(exit);
			}
		}
		Set<Integer> indexes = menu.inventory().slots().stream().map(slot -> slot.get(Keys.SLOT_INDEX).get()).collect(Collectors.toSet());
		menu.registerSlotClick(new SlotClickHandler() {
			@Override
			public boolean handle(Cause cause, Container container, Slot slot, int slotIndex, ClickType<?> clickType) {
				if(indexes.contains(slotIndex) && slotIndex <= 26) {
					if(clickType != ClickTypes.CLICK_LEFT.get() && clickType != ClickTypes.CLICK_RIGHT.get()) return false;
					if(slotIndex <= 8) {
						boolean increase = clickType == ClickTypes.CLICK_LEFT.get();
						editData.remove = increase ? false : isPricesNonZeroValue(prices);
						if(slotIndex == 0) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(BigDecimal.valueOf(0.01), buy, increase);
						} else if(slotIndex == 1) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(BigDecimal.valueOf(0.1), buy, increase);
						} else if(slotIndex == 2) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(BigDecimal.valueOf(0.5), buy, increase);
						} else if(slotIndex == 3) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(BigDecimal.valueOf(1), buy, increase);
						} else if(slotIndex == 4) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(BigDecimal.valueOf(5), buy, increase);
						} else if(slotIndex == 5) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(BigDecimal.valueOf(10), buy, increase);
						} else if(slotIndex == 6) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(BigDecimal.valueOf(100), buy, increase);
						} else if(slotIndex == 7) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(BigDecimal.valueOf(1000), buy, increase);
						} else if(slotIndex == 8) {
							prices.get(editData.priceNumber).setBuyOrSellPrice(BigDecimal.valueOf(10000), buy, increase);
						}
						menu.inventory().slot(13).get().set(updateDisplayItemEdit(player, prices, editData));
					} else if(slotIndex == 18) {
						if(!plugin.shopExists(shopId)) {
							player.sendMessage(getMessages(player).shopNotExists(shopId));
							player.closeInventory();
						}
						if(editData.remove) {
							shopMenu.removeItem(shopSlot);
						} else {
							if(!editData.itemStack.type().equals(ItemTypes.AIR.get())) {
								plugin.getShop(shopId).getShopMenuData(menuID).addOrUpdateItem(shopSlot, new ShopItem(editData.itemStack, prices));
							} else shopMenu.removeItem(shopSlot);
						}
						plugin.getShopStorage().saveShop(shopId);
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							createInventoryToEditor(plugin.getShop(shopId).getShopMenuData(menuID), player, shopId, menuID);
						}).build());
					} else if(slotIndex == 21) {
						for(SerializedShopPrice price : prices) {
							price.setZero();
							editData.remove = true;
							plugin.getShop(shopId).getShopMenuData(menuID).removeItem(shopSlot);
						}
						menu.inventory().slot(13).get().set(ItemStack.empty());
					} else if(slotIndex == 22) {
						plugin.getShop(shopId).getShopMenuData(menuID).addOrUpdateItem(shopSlot, new ShopItem(editData.itemStack, prices));
						closePlayerInventory(player);
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							editItem(plugin.getShop(shopId).getShopMenuData(menuID), player, shopId, menuID, shopSlot, editData.itemStack, !buy);
						}).build());
					} else if(slotIndex == 23) {
						editData.nextPrice(prices);
						menu.inventory().slot(13).get().set(updateDisplayItemEdit(player, prices, editData));
					} else if(slotIndex == 26) {
						if(!plugin.shopExists(shopId)) {
							player.sendMessage(getMessages(player).shopNotExists(shopId));
							closePlayerInventory(player);
							return false;
						}
						if(editData.remove) {
							shopMenu.removeItem(shopSlot);
						} else {
							if(!editData.itemStack.type().equals(ItemTypes.AIR.get())) {
								plugin.getShop(shopId).getShopMenuData(menuID).addOrUpdateItem(shopSlot, new ShopItem(editData.itemStack, prices));
							} else shopMenu.removeItem(shopSlot);
						}
						plugin.getShopStorage().saveShop(shopId);
						closePlayerInventory(player);
					}
				} else {
					if(slot.totalQuantity() > 0) {
						editData.itemStack = slot.peek();
						editData.itemStack.setQuantity(1);
						menu.inventory().slot(13).get().set(updateDisplayItemEdit(player, prices, editData));
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

	public void transactionItem(ShopMenuData shopMenu, ServerPlayer player, String shopId, int menuID, int shopSlot, ItemStack itemStack, boolean buy) {
		Component menuTitle = Component.text("Edit Transaction");
		EditData editData = new EditData();
		editData.buy = buy;
		List<SerializedShopPrice> prices = new ArrayList<SerializedShopPrice>();
		for(SerializedShopPrice serializedPrice : shopMenu.getShopItem(shopSlot).getPrices()) {
			boolean isDefaultCurrency = serializedPrice.getCurrency().equals(plugin.getEconomyService().defaultCurrency());
			if(buy) {
				if(shopMenu.getShopItem(shopSlot).isBuyForPrice(serializedPrice) && (Permissions.shopCurrencyPermission(player, shopId, serializedPrice.getCurrency(), buy) || isDefaultCurrency)) {
					if(isDefaultCurrency) {
						prices.add(0, serializedPrice);
					} else prices.add(serializedPrice);
				}
			} else {
				if(shopMenu.getShopItem(shopSlot).isSellForPrice(serializedPrice) && (Permissions.shopCurrencyPermission(player, shopId, serializedPrice.getCurrency(), buy) || isDefaultCurrency)) {
					if(isDefaultCurrency) {
						prices.add(0, serializedPrice);
					} else prices.add(serializedPrice);
				}
			}
		}
		menuTitle = buy ? getGui(player).buyTransaction() : getGui(player).sellTransaction();
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X3).completeStructure().carrier(player).plugin(plugin.getPluginContainer()).build();
		InventoryMenu menu = viewableInventory.asMenu();
		menu.setTitle(menuTitle);
		menu.setReadOnly(true);
		for(Slot slot : menu.inventory().slots()) {
			int id = slot.get(Keys.SLOT_INDEX).get();
			if(id != 13) slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
			if(id <= 8) {
				Component size = Component.text("1");
				if(id == 1) {
					size = Component.text("2");
				} else if(id == 2) {
					size = Component.text("4");
				} else if(id == 3) {
					size = Component.text("8");
				} else if(id == 4) {
					size = Component.text("16");
				} else if(id == 5) {
					size = Component.text("32");
				} else if(id == 6) {
					size = Component.text("64");
				} else if(id == 7) {
					size = Component.text("128");
				} else if(id == 8) size = Component.text("MAX");
				ItemStack changeSize = plugin.getFillItems().getItemStack(FillItems.valueOf("CHANGESIZE" + id));
				changeSize.offer(Keys.LORE, getItems(player).lore().changeSize());
				changeSize.offer(Keys.CUSTOM_NAME, getItems(player).name().size(size));
				slot.set(changeSize);
			} else if(id == 13 && itemStack != null) {
				editData.itemStack = itemStack;
				slot.offer(updateDisplayItemTransaction(player, prices, editData));
			} else if(id == 18) {
				ItemStack back = plugin.getFillItems().getItemStack(FillItems.BACK);
				if(buy) {
					back.offer(Keys.CUSTOM_NAME, getItems(player).name().buyAndBack());
				} else {
					back.offer(Keys.CUSTOM_NAME, getItems(player).name().sellAndBack());
				}
				slot.set(back);
			} else if(id == 21) {
				ItemStack clear = plugin.getFillItems().getItemStack(FillItems.CLEAR);
				clear.offer(Keys.CUSTOM_NAME, getItems(player).name().clear());
				slot.set(clear);
			} else if(id == 22) {
				ItemStack switchMode = plugin.getFillItems().getItemStack(FillItems.SWITCHMODE);
				switchMode.offer(Keys.CUSTOM_NAME, getItems(player).name().switchMode());
				slot.set(switchMode);
			} else if(id == 23) {
				ItemStack changeCurrency = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
				changeCurrency.offer(Keys.CUSTOM_NAME, getItems(player).name().changeCurrency());
				slot.set(changeCurrency);
			} else if(id == 26) {
				if(buy) {
					ItemStack buyItem = plugin.getFillItems().getItemStack(FillItems.BUY);
					buyItem.offer(Keys.CUSTOM_NAME, getItems(player).name().buy());
					slot.set(buyItem);
				} else {
					ItemStack sellItem = plugin.getFillItems().getItemStack(FillItems.SELL);
					sellItem.offer(Keys.CUSTOM_NAME, getItems(player).name().sell());
					slot.set(sellItem);
				}
			}
		}
		Set<Integer> indexes = menu.inventory().slots().stream().map(slot -> slot.get(Keys.SLOT_INDEX).get()).collect(Collectors.toSet());
		menu.registerSlotClick(new SlotClickHandler() {
			@Override
			public boolean handle(Cause cause, Container container, Slot slot, int slotIndex, ClickType<?> clickType) {
				if(indexes.contains(slotIndex) && slotIndex <= 26) {
					if(clickType != ClickTypes.CLICK_LEFT.get() && clickType != ClickTypes.CLICK_RIGHT.get()) return true;
					if(slotIndex <= 8) {
						boolean increase = clickType == ClickTypes.CLICK_LEFT.get();
						menu.inventory().slot(13).get().clear();
						if(slotIndex == 0) {
							editData.size++;
						} else if(slotIndex == 1) {
							editData.size = increase ? editData.size + 2 : editData.size - 2;
						} else if(slotIndex == 2) {
							editData.size = increase ? editData.size + 4 : editData.size - 4;
						} else if(slotIndex == 3) {
							editData.size = increase ? editData.size + 8 : editData.size - 8;
						} else if(slotIndex == 4) {
							editData.size = increase ? editData.size + 16 : editData.size - 16;
						} else if(slotIndex == 5) {
							editData.size = increase ? editData.size + 32 : editData.size - 32 ;
						} else if(slotIndex == 6) {
							editData.size = increase ? editData.size + 64 : editData.size - 64;
						} else if(slotIndex == 7) {
							editData.size = increase ? editData.size + 128 : editData.size - 128;
						} else if(slotIndex == 8) editData.size = buy ? calculateMaxBuyItems(player, itemStack, prices.get(editData.priceNumber)) : totalItemsInPlayerInventory(player, itemStack);
						if(buy) {
							int maxBuyItems = calculateMaxBuyItems(player, itemStack, prices.get(editData.priceNumber));
							if(editData.size > maxBuyItems) editData.size = maxBuyItems;
						} else {
							int totalItemsInPlayerInventory = totalItemsInPlayerInventory(player, itemStack);
							if(editData.size > totalItemsInPlayerInventory) editData.size = totalItemsInPlayerInventory;
						}
						if(editData.size < 0) editData.size = 0;
						menu.inventory().slot(13).get().set(updateDisplayItemTransaction(player, prices, editData));
					} else if(slotIndex == 21) {
						editData.size = 0;
						menu.inventory().slot(13).get().set(updateDisplayItemTransaction(player, prices, editData));
					} else if(slotIndex == 23) {
						editData.nextPrice(prices);
						if(buy) {
							int check = calculateMaxBuyItems(player, itemStack, prices.get(editData.priceNumber));
							if(editData.size > check) editData.size = check;
						}
						menu.inventory().slot(13).get().set(updateDisplayItemTransaction(player, prices, editData));
					} else if(slotIndex == 18) {
						if(editData.size > 0) {
							editData.itemStack.setQuantity(editData.size);
							editData.size = 0;
							if(buy) {
								if(plugin.getEconomy().checkPlayerBalance(player.uniqueId(), prices.get(editData.priceNumber).getCurrency(), prices.get(editData.priceNumber).getBuyPrice().multiply(BigDecimal.valueOf(editData.itemStack.quantity()))) && plugin.getEconomy().removeFromPlayerBalance(player, prices.get(editData.priceNumber).getCurrency(), prices.get(editData.priceNumber).getBuyPrice(), editData.itemStack)) {
									player.inventory().offer(editData.itemStack.copy());
								} else player.sendMessage(getExceptions(player).noMoney());
							} else {
								if(plugin.getEconomy().addToPlayerBalance(player, prices.get(editData.priceNumber).getCurrency(), prices.get(editData.priceNumber).getSellPrice(), editData.itemStack)) player.inventory().query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY.get().of(editData.itemStack)).poll(editData.itemStack.quantity());
							}
						}
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							createInventoryToPlayer(shopMenu, player, shopId, menuID);
						}).build());
					} else if(slotIndex == 22) {
						if(buy) {
							if(shopMenu.getShopItem(shopSlot).isSell()) {
								Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
									transactionItem(shopMenu, player, shopId, menuID, shopSlot, editData.itemStack, !buy);
								}).build());
							}
						} else {
							if(shopMenu.getShopItem(shopSlot).isBuy()) {
								Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
									transactionItem(shopMenu, player, shopId, menuID, shopSlot, editData.itemStack, !buy);
								}).build());
							}
						}
					} else if(slotIndex == 26) {
						if(editData.size > 0) {
							editData.itemStack.setQuantity(editData.size);
							editData.size = 0;
							closePlayerInventory(player);
							if(buy) {
								if(plugin.getEconomy().checkPlayerBalance(player.uniqueId(), prices.get(editData.priceNumber).getCurrency(), prices.get(editData.priceNumber).getBuyPrice().multiply(BigDecimal.valueOf(editData.itemStack.quantity()))) && plugin.getEconomy().removeFromPlayerBalance(player, prices.get(editData.priceNumber).getCurrency(), prices.get(editData.priceNumber).getBuyPrice(), editData.itemStack)) {
									player.inventory().offer(editData.itemStack.copy());
								} else player.sendMessage(getExceptions(player).noMoney());
							} else {
								if(plugin.getEconomy().addToPlayerBalance(player, prices.get(editData.priceNumber).getCurrency(), prices.get(editData.priceNumber).getSellPrice(), editData.itemStack)) player.inventory().query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY.get().of(editData.itemStack)).poll(editData.itemStack.quantity());
							}
						}
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

	private ItemStack updateDisplayItemEdit(ServerPlayer player, List<SerializedShopPrice> prices, EditData editData) {
		ItemStack itemStack = editData.itemStack.copy();
		List<Component> lore = itemStack.get(Keys.LORE).orElse(new ArrayList<Component>());
		if(itemStack.get(Keys.LORE).isPresent()) {
			itemStack.remove(Keys.LORE);
			lore.add(Component.empty());
		}
		lore.add(getItems(player).lore().currency(prices.get(editData.priceNumber).getCurrency()));
		for(SerializedShopPrice price : prices) {
			lore.add(getItems(player).lore().price(price.getCurrency(), price.getBuyPrice().doubleValue(), price.getSellPrice().doubleValue()));
		}
		itemStack.offer(Keys.LORE, lore);
		return itemStack;
	}

	private ItemStack updateDisplayItemTransaction(ServerPlayer player, List<SerializedShopPrice> prices, EditData editData) {
		ItemStack itemStack = editData.itemStack.copy();
		List<Component> lore = itemStack.get(Keys.LORE).orElse(new ArrayList<Component>());
		if(itemStack.get(Keys.LORE).isPresent()) {
			itemStack.remove(Keys.LORE);
			lore.add(Component.empty());
		}
		lore.add(getItems(player).lore().currency(prices.get(editData.priceNumber).getCurrency()));
		lore.add(getItems(player).lore().size(editData.size));
		if(editData.buy) {
			lore.add(getItems(player).lore().sum(prices.get(editData.priceNumber).getCurrency(), prices.get(editData.priceNumber).getBuyPrice().multiply(BigDecimal.valueOf(editData.size)).doubleValue()));
		} else lore.add(getItems(player).lore().sum(prices.get(editData.priceNumber).getCurrency(), prices.get(editData.priceNumber).getSellPrice().multiply(BigDecimal.valueOf(editData.size)).doubleValue()));
		for(SerializedShopPrice price : prices) {
			if(price.getBuyPrice().doubleValue() > 0 || price.getSellPrice().doubleValue() > 0) {
				lore.add(getItems(player).lore().price(price.getCurrency(), price.getBuyPrice().doubleValue(), price.getSellPrice().doubleValue()));
			}
		}
		itemStack.offer(Keys.LORE, lore);
		return itemStack;
	}

	private Integer calculateMaxBuyItems(ServerPlayer player, ItemStack itemStack, SerializedShopPrice serializedPrice) {
		if(plugin.getEconomy().getPlayerBalance(player.uniqueId(), serializedPrice.getCurrency()).doubleValue() < serializedPrice.getBuyPrice().doubleValue()) return 0;
		int value = player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).freeCapacity() * itemStack.maxStackQuantity();
		for(Slot playerSlot : player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).slots()) {
			if(playerSlot.contains(itemStack)) {
				int difference = itemStack.maxStackQuantity() - playerSlot.query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY.get().of(itemStack)).totalQuantity();
				if(playerSlot.query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY.get().of(itemStack)).totalQuantity() != itemStack.maxStackQuantity()) {
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

	private BigDecimal calculateMoney(ItemStack itemStack, SerializedShopPrice serializedPrice) {
		BigDecimal price = serializedPrice.getBuyPrice();
		return price.pow(itemStack.quantity());
	}

	private Integer totalItemsInPlayerInventory(ServerPlayer player, ItemStack itemStack) {
		return player.inventory().query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY.get().of(itemStack)).totalQuantity();
	}

	private boolean isPricesNonZeroValue(List<SerializedShopPrice> prices) {
		for(SerializedShopPrice price : prices) {
			if(!price.isZero()) return false;
		}
		return true;
	}

	private void closePlayerInventory(ServerPlayer player) {
		Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(4)).plugin(plugin.getPluginContainer()).execute(() -> {
			player.closeInventory();
		}).build());
	}

	private Gui.Shop getGui(ServerPlayer player) {
		return plugin.getLocales().getLocale(player).gui().shop();
	}

	private Messages.Shop getMessages(ServerPlayer player) {
		return plugin.getLocales().getLocale(player).messages().shop();
	}

	private Messages.Exceptions getExceptions(ServerPlayer player) {
		return plugin.getLocales().getLocale(player).messages().exceptions();
	}

	private Items getItems(ServerPlayer player) {
		return plugin.getLocales().getLocale(player).items();
	}

	private class EditData {
		int priceNumber = 0;
		int size = 0;
		ItemStack itemStack = ItemStack.of(ItemTypes.AIR);
		boolean buy;
		boolean remove = false;
		void nextPrice(List<SerializedShopPrice> prices) {
			priceNumber = priceNumber + 1;
			if(prices.size() <= priceNumber) priceNumber = 0;
		}
	}
}
