package sawfowl.guishopmanager.gui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
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
import org.spongepowered.api.item.inventory.menu.ClickType;
import org.spongepowered.api.item.inventory.menu.ClickTypes;
import org.spongepowered.api.item.inventory.menu.InventoryMenu;
import org.spongepowered.api.item.inventory.menu.handler.CloseHandler;
import org.spongepowered.api.item.inventory.menu.handler.SlotClickHandler;
import org.spongepowered.api.item.inventory.type.ViewableInventory;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.Currency;
import org.spongepowered.api.util.Ticks;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.configure.FillItems;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Gui;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Items;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Messages;
import sawfowl.guishopmanager.data.commandshop.CommandItemData;
import sawfowl.guishopmanager.data.commandshop.CommandShopMenuData;
import sawfowl.guishopmanager.serialization.commandsshop.CommandsList;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStack;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShopPrice;

public class CommandShopMenus {

	private final GuiShopManager plugin;
	public CommandShopMenus(GuiShopManager plugin) {
		this.plugin = plugin;
	}

	public void createInventoryToPlayer(CommandShopMenuData shopMenu, ServerPlayer player, String shopId, int menuId) {
		Component menuTitle = menuId == 1 ? plugin.getCommandShopData(shopId).getOrDefaultTitle(player.locale()) : plugin.getCommandShopData(shopId).getOrDefaultTitle(player.locale()).append(Component.text(" || " + menuId));
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X6).completeStructure().carrier(player).plugin(plugin.getPluginContainer()).build();
		InventoryMenu menu = viewableInventory.asMenu();
		menu.setReadOnly(true);
		menu.setTitle(menuTitle);
		List<Currency> currencies = new ArrayList<Currency>();
		currencies.add(plugin.getEconomyService().defaultCurrency());
		EditData editData = new EditData();
		for(Currency currency : plugin.getEconomy().getCurrencies()) if(Permissions.commandShopCurrencyPermission(player, shopId, currency) && !currency.equals(plugin.getEconomyService().defaultCurrency())) currencies.add(currency);
		for(Slot slot : menu.inventory().slots()) {
			int id = slot.get(Keys.SLOT_INDEX).get();
			if(id < 45) {
				if(shopMenu.containsCommandItem(id)) {
					CommandItemData shopItemStack = shopMenu.getCommandItem(id);
					ItemStack itemStack = shopItemStack.getItemStack();
					List<Component> itemLore = new ArrayList<Component>(itemStack.get(Keys.LORE).orElse(new ArrayList<Component>()));
					if(itemStack.get(Keys.LORE).isPresent()) {
						itemStack.remove(Keys.LORE);
						itemLore.add(Component.empty());
					}
					itemLore.add(getItems(player).lore().transactionVariants());
					for(SerializedCommandShopPrice serializablePrice : shopItemStack.getPrices()) {
						if(serializablePrice.isAllowFree()) itemLore.add(getItems(player).lore().allowFree());
						itemLore.add(getItems(player).lore().commandPrice(serializablePrice.getCurrency(), serializablePrice.getBuyPrice().doubleValue()));
					}
					itemLore.add(Component.empty());
					itemStack.offer(Keys.LORE, itemLore);
					slot.offer(itemStack);
				} else slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
			} else if(id <= 53) {
				slot.set(plugin.getFillItems().getItemStack(FillItems.BOTTOM));
				if(id == 45 && plugin.getCommandShopData(shopId).hasPreviousExist(menuId)) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.BACK);
					itemStack.offer(Keys.CUSTOM_NAME, getItems(player).name().back());
					slot.set(itemStack);
				}
				if(id == 49) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
					itemStack.offer(Keys.CUSTOM_NAME, getItems(player).name().changeCurrency());
					itemStack.offer(Keys.LORE, Arrays.asList(getItems(player).lore().currency(currencies.get(editData.priceNumber))));
					slot.set(itemStack);
				}
				if(id == 53 && plugin.getCommandShopData(shopId).hasNextExist(menuId) && !plugin.getCommandShopData(shopId).getCommandShopMenuData(menuId + 1).isEmpty()) {
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
							createInventoryToPlayer(plugin.getCommandShopData(shopId).getCommandShopMenuData(menuId - 1), player, shopId, menuId - 1);
						}).build());
					} else if(slotIndex == 53 && plugin.getCommandShopData(shopId).hasNextExist(menuId) && !plugin.getCommandShopData(shopId).getCommandShopMenuData(menuId + 1).isEmpty()) {
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							createInventoryToPlayer(plugin.getCommandShopData(shopId).getCommandShopMenuData(menuId + 1), player, shopId, menuId + 1);
						}).build());
					} else if(slotIndex < 45 && shopMenu.containsCommandItem(slotIndex) && shopMenu.getCommandItem(slotIndex).getPrices().size() > 0) {
						if(currencies.size() != 0) shopMenu.getCommandItem(slotIndex).getPrices().stream().filter(price -> (toPlain(price.getCurrency().symbol()).equals(toPlain(currencies.get(editData.priceNumber).symbol())))).findFirst().ifPresent(price -> {
							if(price.isAllowFree() || !price.isZero()) transactionItem(player, price, shopMenu.getCommandItem(slotIndex).getCommands());
						});
					} else if(slotIndex == 49) {
						editData.nextPrice(currencies.size());
						ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
						itemStack.offer(Keys.CUSTOM_NAME, getItems(player).name().changeCurrency());
						itemStack.offer(Keys.LORE, Arrays.asList(getItems(player).lore().currency(currencies.get(editData.priceNumber))));
						slot.set(itemStack);
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

	public void createInventoryToEditor(CommandShopMenuData shopMenu, ServerPlayer player, String shopId, int menuId) {
		Component menuTitle = menuId == 1 ? plugin.getCommandShopData(shopId).getOrDefaultTitle(player.locale()) : plugin.getCommandShopData(shopId).getOrDefaultTitle(player.locale()).append(Component.text(" || " + menuId));
		ViewableInventory viewableInventory = ViewableInventory.builder().type(ContainerTypes.GENERIC_9X6).completeStructure().carrier(player).plugin(plugin.getPluginContainer()).build();
		InventoryMenu menu = viewableInventory.asMenu();
		menu.setReadOnly(true);
		menu.setTitle(menuTitle);
		for(Slot slot : menu.inventory().slots()) {
			int id = slot.get(Keys.SLOT_INDEX).get();
			if(id < 45) {
				if(shopMenu.containsCommandItem(id)) {
					CommandItemData shopItemStack = shopMenu.getCommandItem(id);
					ItemStack itemStack = shopItemStack.getItemStack();
					List<Component> itemLore = new ArrayList<Component>(itemStack.get(Keys.LORE).orElse(new ArrayList<Component>()));
					if(itemStack.get(Keys.LORE).isPresent()) {
						itemStack.remove(Keys.LORE);
						itemLore.add(Component.empty());
					}
					itemLore.add(getItems(player).lore().transactionVariants());
					for(SerializedCommandShopPrice serializablePrice : shopItemStack.getPrices()) {
						if(serializablePrice.isAllowFree()) itemLore.add(getItems(player).lore().allowFree());
						itemLore.add(getItems(player).lore().commandPrice(serializablePrice.getCurrency(), serializablePrice.getBuyPrice().doubleValue()));
					}
					itemLore.add(Component.empty());
					itemStack.offer(Keys.LORE, itemLore);
					slot.offer(itemStack);
				} else slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
			} else if(id <= 53) {
				slot.set(plugin.getFillItems().getItemStack(FillItems.BOTTOM));
				if(id == 45 && plugin.getCommandShopData(shopId).hasPreviousExist(menuId)) {
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
								createInventoryToEditor(plugin.getCommandShopData(shopId).getCommandShopMenuData(menuId - 1), player, shopId, menuId - 1);
							}).build());
					} else if(slotIndex == 53) {
						int nextMenu = menuId + 1;
						if(!plugin.getCommandShopData(shopId).hasNextExist(menuId)) {
							plugin.getCommandShopData(shopId).addMenu(nextMenu, new CommandShopMenuData());
						}
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							createInventoryToEditor(plugin.getCommandShopData(shopId).getCommandShopMenuData(nextMenu), player, shopId, nextMenu);
						}).build());
					} else if(slotIndex < 45) {
						ItemStack itemStack = null;
						if(shopMenu.containsCommandItem(slotIndex)) {
							itemStack = shopMenu.getCommandItem(slotIndex).getItemStack();
						}
						ItemStack finalStack = itemStack != null ? itemStack.copy() : null;
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							editItem(shopMenu, player, shopId, menuId, slotIndex, finalStack);
						}).build());
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

	public void editItem(CommandShopMenuData shopMenu, ServerPlayer player, String shopId, int menuID, int shopSlot, ItemStack itemStack) {
		Component menuTitle = getGui(player).edit();
		EditData editData = new EditData();
		List<SerializedCommandShopPrice> prices = new ArrayList<SerializedCommandShopPrice>();
		if(!shopMenu.containsCommandItem(shopSlot) || shopMenu.getCommandItem(shopSlot).getPrices().isEmpty()) {
			prices.add(new SerializedCommandShopPrice(plugin.getEconomyService().defaultCurrency()));
			for(Currency currency : plugin.getEconomy().getCurrencies()) {
				if(!currency.equals(plugin.getEconomyService().defaultCurrency())) {
					prices.add(new SerializedCommandShopPrice(currency));
				}
			}
		} else {
			prices.addAll(shopMenu.getCommandItem(shopSlot).getPrices());
			for(SerializedCommandShopPrice serializedShopPrice : shopMenu.getCommandItem(shopSlot).getPrices()) {
				if(serializedShopPrice.getCurrency().equals(plugin.getEconomyService().defaultCurrency())) {
					prices.remove(serializedShopPrice);
					prices.add(0, serializedShopPrice);
				}
			}
			for(Currency currency : plugin.getEconomy().getCurrencies()) {
				if(!prices.stream().filter(p -> p.getCurrency().equals(currency)).findFirst().isPresent()) {
					prices.add(new SerializedCommandShopPrice(currency));
				}
			}
		}
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
				switchMode.offer(Keys.LORE, Arrays.asList(getItems(player).lore().switchFree()));
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
						editData.remove = false;
						boolean increase = clickType == ClickTypes.CLICK_LEFT.get();
						if(slotIndex == 0) {
							prices.get(editData.priceNumber).setPrice(BigDecimal.valueOf(0.01), increase);
						} else if(slotIndex == 1) {
							prices.get(editData.priceNumber).setPrice(BigDecimal.valueOf(0.1), increase);
						} else if(slotIndex == 2) {
							prices.get(editData.priceNumber).setPrice(BigDecimal.valueOf(0.5), increase);
						} else if(slotIndex == 3) {
							prices.get(editData.priceNumber).setPrice(BigDecimal.valueOf(1), increase);
						} else if(slotIndex == 4) {
							prices.get(editData.priceNumber).setPrice(BigDecimal.valueOf(5), increase);
						} else if(slotIndex == 5) {
							prices.get(editData.priceNumber).setPrice(BigDecimal.valueOf(10), increase);
						} else if(slotIndex == 6) {
							prices.get(editData.priceNumber).setPrice(BigDecimal.valueOf(100), increase);
						} else if(slotIndex == 7) {
							prices.get(editData.priceNumber).setPrice(BigDecimal.valueOf(1000), increase);
						} else if(slotIndex == 8) {
							prices.get(editData.priceNumber).setPrice(BigDecimal.valueOf(10000), increase);
						}
						menu.inventory().slot(13).get().set(updateDisplayItemEdit(player, prices, editData));
					} else if(slotIndex == 18) {
						if(!plugin.commandShopExists(shopId)) {
							player.sendMessage(getMessages(player).shopNotExists(shopId));
							player.closeInventory();
						}
						if(editData.remove) {
							plugin.getCommandShopData(shopId).getCommandShopMenuData(menuID).removeItem(shopSlot);
						} else {
							if(editData.itemStack.type() != ItemTypes.AIR) {
								plugin.getCommandShopData(shopId).getCommandShopMenuData(menuID).addOrUpdateItem(shopSlot, new CommandItemData(new SerializedItemStack(editData.itemStack), prices));
							}
						}
						plugin.getCommandsShopStorage().saveCommandsShop(shopId);
						Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(5)).plugin(plugin.getPluginContainer()).execute(() -> {
							createInventoryToEditor(shopMenu, player, shopId, menuID);
						}).build());
					} else if(slotIndex == 21) {
						for(SerializedCommandShopPrice price : prices) {
							price.setZero();
							editData.remove = true;
						}
						menu.inventory().slot(13).get().set(updateDisplayItemEdit(player, prices, editData));
						editData.remove = true;
					} else if(slotIndex == 22) {
						prices.get(editData.priceNumber).switchFree();
						menu.inventory().slot(13).get().set(updateDisplayItemEdit(player, prices, editData));
					} else if(slotIndex == 23) {
						editData.nextPrice(prices.size());
						menu.inventory().slot(13).get().set(updateDisplayItemEdit(player, prices, editData));
					}
					else if(slotIndex == 26) {
						if(!plugin.commandShopExists(shopId)) {
							player.sendMessage(getMessages(player).shopNotExists(shopId));
							closePlayerInventory(player);
							return false;
						}
						if(editData.remove) {
							plugin.getCommandShopData(shopId).getCommandShopMenuData(menuID).removeItem(shopSlot);
						} else {
							if(editData.itemStack.type() != ItemTypes.AIR) {
								plugin.getCommandShopData(shopId).getCommandShopMenuData(menuID).addOrUpdateItem(shopSlot, new CommandItemData(new SerializedItemStack(editData.itemStack), prices));
							}
						}
						plugin.getCommandsShopStorage().saveCommandsShop(shopId);
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

	private ItemStack updateDisplayItemEdit(ServerPlayer player, List<SerializedCommandShopPrice> prices, EditData editData) {
		ItemStack itemStack = editData.itemStack.copy();
		List<Component> lore = new ArrayList<Component>(itemStack.get(Keys.LORE).orElse(new ArrayList<Component>()));
		if(itemStack.get(Keys.LORE).isPresent()) {
			itemStack.remove(Keys.LORE);
			lore.add(Component.empty());
		}
		lore.add(getItems(player).lore().currency(prices.get(editData.priceNumber).getCurrency()));
		for(SerializedCommandShopPrice price : prices) {
			if(price.isAllowFree()) lore.add(getItems(player).lore().allowFree());
			lore.add(getItems(player).lore().commandPrice(price.getCurrency(), price.getBuyPrice().doubleValue()));
		}
		itemStack.offer(Keys.LORE, lore);
		return itemStack;
	}

	private void closePlayerInventory(ServerPlayer player) {
		Sponge.server().scheduler().submit(Task.builder().delay(Ticks.of(4)).plugin(plugin.getPluginContainer()).execute(() -> {
			player.closeInventory();
		}).build());
	}

	private void transactionItem(ServerPlayer player, SerializedCommandShopPrice serializedCommandShopPrice, CommandsList serializedCommandsList) {
		if(!serializedCommandShopPrice.isAllowFree() && !plugin.getEconomy().checkPlayerBalance(player.uniqueId(), serializedCommandShopPrice.getCurrency(), serializedCommandShopPrice.getBuyPrice())) {
			player.sendMessage(getExceptions(player).noMoney());
			return;
		}
		plugin.getEconomy().buyCommands(player, serializedCommandShopPrice.getCurrency(), serializedCommandShopPrice.getBuyPrice());
		serializedCommandsList.executeAll(player);
	}

	private String toPlain(Component component) {
		return LegacyComponentSerializer.legacyAmpersand().serialize(component);
	}

	private Gui.CommandShop getGui(ServerPlayer player) {
		return plugin.getLocales().getLocale(player).gui().commandShop();
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
		ItemStack itemStack = ItemStack.of(ItemTypes.AIR);
		boolean remove = false;
		void nextPrice(int pricesSize) {
			priceNumber = priceNumber + 1;
			if(pricesSize <= priceNumber) priceNumber = 0;
		}
	}

}
