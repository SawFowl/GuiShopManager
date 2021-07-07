package sawfowl.guishopmanager.utils.gui;

import java.util.ArrayList;
import java.util.List;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.configure.FillItems;
import sawfowl.guishopmanager.utils.data.shop.ShopItem;
import sawfowl.guishopmanager.utils.data.shop.ShopMenuData;
import sawfowl.guishopmanager.utils.serialization.shop.SerializedShopPrice;

public class ShopMenu {

	private GuiShopManager plugin;
	public ShopMenu(GuiShopManager instance) {
		this.plugin = instance;
	}

	public void createInventoryToEditor(ShopMenuData shopMenu, Player player, String shopId, int menuId) {
		Text menuTitle = menuId == 1 ? plugin.getShop(shopId).getOrDefaultTitle(player.getLocale()) : Text.of(plugin.getShop(shopId).getOrDefaultTitle(player.getLocale()), " || ", menuId);
		Inventory inventory = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
				.property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(menuTitle))
				.listener(ClickInventoryEvent.class, event -> {
					if(event.getTransactions().isEmpty()) {
						return;
					}
					int id = event.getTransactions().get(0).getSlot().getInventoryProperty(SlotIndex.class).get().getValue();
					if(id <= 53) {
						event.setCancelled(true);
					}
					if(id == 45 && menuId > 1) {
						Task.builder().delayTicks(1).execute(() -> {
							player.closeInventory();
						}).submit(plugin);
						Task.builder().delayTicks(5).execute(() -> {
							createInventoryToEditor(plugin.getShop(shopId).getShopMenuData(menuId - 1), player, shopId, menuId - 1);
						}).submit(plugin);
					}
					if(id == 53) {
						int nextMenu = menuId + 1;
						if(!plugin.getShop(shopId).hasNextExist(menuId)) {
							plugin.getShop(shopId).addMenu(nextMenu, new ShopMenuData());
						}
						Task.builder().delayTicks(1).execute(() -> {
							player.closeInventory();
						}).submit(plugin);
						Task.builder().delayTicks(5).execute(() -> {
							createInventoryToEditor(plugin.getShop(shopId).getShopMenuData(nextMenu), player, shopId, nextMenu);
						}).submit(plugin);
					}
				if(event instanceof ClickInventoryEvent.Primary) {
					if(id < 45) {
						Task.builder().delayTicks(1).execute(() -> {
							player.closeInventory();
						}).submit(plugin);
						Task.builder().delayTicks(5).execute(() -> {
							ItemStack itemStack = null;
							if(shopMenu.containsShopItem(id)) {
								itemStack = shopMenu.getShopItem(id).getItemStack();
							}
							plugin.getShopItemMenu().editItem(shopMenu, player, shopId, menuId, id, itemStack, true);
						}).submit(plugin);
					}
				} else if(event instanceof ClickInventoryEvent.Secondary) {
					if(id < 45) {
						Task.builder().delayTicks(1).execute(() -> {
							player.closeInventory();
						}).submit(plugin);
						Task.builder().delayTicks(5).execute(() -> {
							ItemStack itemStack = null;
							if(shopMenu.containsShopItem(id)) {
								itemStack = shopMenu.getShopItem(id).getItemStack();
							}
							plugin.getShopItemMenu().editItem(shopMenu, player, shopId, menuId, id, itemStack, false);
						}).submit(plugin);
					}
				}
			}).build(plugin);
		for(Inventory slot : inventory.slots()) {
			int id = slot.getInventoryProperty(SlotIndex.class).get().getValue();
			if(id < 45) {
				if(shopMenu.containsShopItem(id)) {
					ShopItem shopItemStack = shopMenu.getShopItem(id);
					ItemStack itemStack = shopItemStack.getItemStack();
					List<Text> itemLore = itemStack.get(Keys.ITEM_LORE).orElse(new ArrayList<Text>());
					if(itemStack.get(Keys.ITEM_LORE).isPresent()) {
						itemStack.remove(Keys.ITEM_LORE);
						itemLore.add(Text.EMPTY);
					}
					itemLore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "TransactionVariants"));
					for(SerializedShopPrice serializablePrice : shopItemStack.getPrices()) {
						itemLore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "Price")
								.replace("%currency%", serializablePrice.getCurrency().getDisplayName())
								.replace("%buyprice%", Text.of(serializablePrice.getBuyPrice()))
								.replace("%sellprice%", Text.of(serializablePrice.getSellPrice())));
						itemLore.add(Text.EMPTY);
					}
					itemStack.offer(Keys.ITEM_LORE, itemLore);
					slot.offer(itemStack);
				} else {
					slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
				}
			} else if(id >= 45 && id <= 53) {
				slot.set(plugin.getFillItems().getItemStack(FillItems.BOTTOM));
				if(id == 45 && plugin.getShop(shopId).hasPreviousExist(menuId)) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.BACK);
					itemStack.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Back"));
					slot.set(itemStack);
				}
				if(id == 53) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.NEXT);
					itemStack.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Next"));
					slot.set(itemStack);
				}
			}
		}
		player.openInventory(inventory);
	}

	public void createInventoryToPlayer(ShopMenuData shopMenu, Player player, String shopId, int menuId) {
		Text menuTitle = menuId == 1 ? plugin.getShop(shopId).getOrDefaultTitle(player.getLocale()) : Text.of(plugin.getShop(shopId).getOrDefaultTitle(player.getLocale()), " || ", menuId);
		Inventory inventory = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
				.property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(menuTitle))
				.listener(ClickInventoryEvent.class, event -> {
					if(event.getTransactions().isEmpty()) {
						return;
					}
					int id = event.getTransactions().get(0).getSlot().getInventoryProperty(SlotIndex.class).get().getValue();
					if(id <= 53) {
						event.setCancelled(true);
					}
					if(id == 45 && menuId > 1) {
						Task.builder().delayTicks(1).execute(() -> {
							player.closeInventory();
						}).submit(plugin);
						Task.builder().delayTicks(5).execute(() -> {
							createInventoryToPlayer(plugin.getShop(shopId).getShopMenuData(menuId - 1), player, shopId, menuId - 1);
						}).submit(plugin);
					}
					if(id == 53 && plugin.getShop(shopId).hasNextExist(menuId)) {
						Task.builder().delayTicks(1).execute(() -> {
							player.closeInventory();
						}).submit(plugin);
						Task.builder().delayTicks(5).execute(() -> {
							createInventoryToPlayer(plugin.getShop(shopId).getShopMenuData(menuId + 1), player, shopId, menuId + 1);
						}).submit(plugin);
					}
				if(event instanceof ClickInventoryEvent.Primary) {
					if(id < 45) {
						if(shopMenu.containsShopItem(id)) {
							if(shopMenu.getShopItem(id).isBuy()) {
								Task.builder().delayTicks(1).execute(() -> {
									player.closeInventory();
								}).submit(plugin);
								Task.builder().delayTicks(5).execute(() -> {
											plugin.getShopItemMenu().transactionItem(shopMenu, player, shopId, menuId, id, shopMenu.getShopItem(id).getItemStack(), true);
								}).submit(plugin);
							}
						}
					}
				} else if(event instanceof ClickInventoryEvent.Secondary) {
					if(id < 45) {
						if(shopMenu.containsShopItem(id)) {
							if(shopMenu.getShopItem(id).isSell()) {
								Task.builder().delayTicks(1).execute(() -> {
									player.closeInventory();
								}).submit(plugin);
								Task.builder().delayTicks(5).execute(() -> {
											plugin.getShopItemMenu().transactionItem(shopMenu, player, shopId, menuId, id, shopMenu.getShopItem(id).getItemStack(), false);
								}).submit(plugin);
							}
						}
					}
				}
			}).build(plugin);
		for(Inventory slot : inventory.slots()) {
			int id = slot.getInventoryProperty(SlotIndex.class).get().getValue();
			if(id < 45) {
				if(shopMenu.containsShopItem(id)) {
					ShopItem shopItemStack = shopMenu.getShopItem(id);
					ItemStack itemStack = shopItemStack.getItemStack();
					List<Text> itemLore = itemStack.get(Keys.ITEM_LORE).orElse(new ArrayList<Text>());
					if(itemStack.get(Keys.ITEM_LORE).isPresent()) {
						itemStack.remove(Keys.ITEM_LORE);
						itemLore.add(Text.EMPTY);
					}
					itemLore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "TransactionVariants"));
					for(SerializedShopPrice serializablePrice : shopItemStack.getPrices()) {
						itemLore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "Price")
								.replace("%currency%", serializablePrice.getCurrency().getDisplayName())
								.replace("%buyprice%", Text.of(serializablePrice.getBuyPrice()))
								.replace("%sellprice%", Text.of(serializablePrice.getSellPrice())));
						itemLore.add(Text.EMPTY);
					}
					itemStack.offer(Keys.ITEM_LORE, itemLore);
					slot.offer(itemStack);
				} else {
					slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
				}
			} else if(id >= 45 && id <= 53) {
				slot.set(plugin.getFillItems().getItemStack(FillItems.BOTTOM));
				if(id == 45 && plugin.getShop(shopId).hasPreviousExist(menuId)) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.BACK);
					itemStack.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Back"));
					slot.set(itemStack);
				}
				if(id == 53 && plugin.getShop(shopId).hasNextExist(menuId)) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.NEXT);
					itemStack.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Next"));
					slot.set(itemStack);
				}
			}
		}
		player.openInventory(inventory);
	}
}
