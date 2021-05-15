package sawfowl.guishopmanager.utils.gui;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

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
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import net.minecraft.nbt.NBTTagCompound;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.utils.configure.FillItems;
import sawfowl.guishopmanager.utils.serialization.auction.SerializedAuctionPrice;
import sawfowl.guishopmanager.utils.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.utils.serialization.auction.SerializedBetData;

public class AuctionMenus {

	private GuiShopManager plugin;
	private String serverName;
	public AuctionMenus(GuiShopManager instance) {
		plugin = instance;
		serverName = plugin.getRootNode().getNode("Auction", "Server").getString();
	}

	public void createInventory(Player player, int page) {
		Text menuTitle = page == 1 ? Text.of("Auction") : Text.of("Auction" + page);
		menuTitle = page == 1 ? plugin.getLocales().getLocalizedText(player.getLocale(), "Gui", "Auction") : Text.of(plugin.getLocales().getLocalizedText(player.getLocale(), "Gui", "Auction"), " || ", page);
        int firstItem = (page * 45) - 45;
        int currentItem = firstItem;
		List<Currency> currencies = new ArrayList<Currency>();
		currencies.add(plugin.getEconomyService().getDefaultCurrency());
		for(Currency currency : plugin.getEconomyService().getCurrencies()) {
			if(player.hasPermission(Permissions.currencyPermission(currency.getName())) && !currency.getId().equals(plugin.getEconomyService().getDefaultCurrency().getId())) {
				currencies.add(currency);
			}
		}
		EditData editData = new EditData();
		Inventory inventory = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
				.property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(menuTitle))
				.listener(ClickInventoryEvent.class, event -> {
					if(event.getTransactions().isEmpty()) {
						return;
					}
					event.setCancelled(true);
					Slot slot = event.getTransactions().get(0).getSlot();
					Optional<SlotIndex> slotIndex = slot.getInventoryProperty(SlotIndex.class);
					int id = slotIndex.get().getValue();
					if(id <= 44) {
						if(plugin.getAuctionItems().isEmpty()) {
							return;
						}
						NBTTagCompound nbt = ItemStackUtil.toNative(event.getCursorTransaction().getDefault().createStack()).getTagCompound();
						if(nbt == null) return;
						if(nbt.getKeySet().contains("Id")) {
							int auctionId = nbt.getInteger("Id");
							if(plugin.getAuctionItems().size() >= auctionId) {
								if(plugin.getAuctionItems().get(auctionId).getOwnerUUID().equals(player.getUniqueId())) {
									player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AuctionCancelBuy"));
									return;
								}
								if(event instanceof ClickInventoryEvent.Primary && plugin.getAuctionItems().get(auctionId).getPrices().get(0).getBet().doubleValue() > 0) {
									Task.builder().delayTicks(5).execute(() -> {
										player.closeInventory();
										editBet(player, page, auctionId);
									}).submit(plugin);
								} else {
									SerializedAuctionStack auctionItem = plugin.getAuctionItems().get(auctionId);
									if(!auctionItem.containsCurrency(currencies.get(editData.priceNumber)) || auctionItem.getPrices().get(editData.priceNumber).getPrice().doubleValue() == 0.00 || auctionItem.getSerializedItemStack().getItemStack().getQuantity() > calculateMaxBuyItems(player, auctionItem.getSerializedItemStack().getItemStack(), auctionItem.getPrices().get(editData.priceNumber))) {
										return;
									}
									if(!plugin.getAuctionItems().get(auctionId).getSerializedItemStack().getItemStack().getType().getId().equals(event.getCursorTransaction().getDefault().getType().getId())) {
										Task.builder().delayTicks(5).execute(() -> {
											player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AuctionItemNotFound"));
											player.closeInventory();
											createInventory(player, page);
										}).submit(plugin);
										return;
									}
									if(plugin.getEconomy().checkPlayerBalance(player.getUniqueId(), auctionItem.getPrices().get(editData.priceNumber).getCurrency(), auctionItem.getPrices().get(editData.priceNumber).getPrice())) {
										plugin.getEconomy().auctionTransaction(player.getUniqueId(), auctionItem, editData.priceNumber, false);
										player.getInventory().offer(auctionItem.getSerializedItemStack().getItemStack());
										Task.builder().delayTicks(5).execute(() -> {
											plugin.getAuctionWorkData().removeAuctionStack(auctionItem.getStackUUID());
											plugin.getAuctionItems().remove(auctionItem);
											player.closeInventory();
											createInventory(player, page);
										}).submit(plugin);
									} else {
										player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "NoMoney"));
									}
								}
							} else {
								Task.builder().delayTicks(5).execute(() -> {
									player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AuctionItemNotFound"));
									player.closeInventory();
									createInventory(player, page);
								}).submit(plugin);
							}
						}
					}
					if(id == 45 && page >= 2) {
						Task.builder().delayTicks(1).execute(() -> {
							player.closeInventory();
						}).submit(plugin);
						Task.builder().delayTicks(5).execute(() -> {
							createInventory(player, page - 1);
						}).submit(plugin);
					}
					if(id == 47) {
						int currentSelling = plugin.getExpiredAuctionItems().containsKey(player.getUniqueId()) ? plugin.getExpiredAuctionItems().get(player.getUniqueId()).size() : 0;
						for(SerializedAuctionStack auctionItem : plugin.getAuctionItems()) {
							if(auctionItem.getOwnerUUID().equals(player.getUniqueId())) {
								currentSelling++;
							}
							if(currentSelling >= 53) {
								break;
							}
						}
						if(currentSelling >= 53) {
							player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "AuctionMaxVolume"));
							return;
						}
						Task.builder().delayTicks(5).execute(() -> {
							player.closeInventory();
							editItem(player, page);
						}).submit(plugin);
					}
					if(id == 49) {
						editData.priceNumber++;
						if(editData.priceNumber > currencies.size() - 1) {
							editData.priceNumber = 0;
						}
						ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
						itemStack.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "ChangeCurrency"));
						itemStack.offer(Keys.ITEM_LORE, Arrays.asList(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "CurrentCurrency")
								.replace("%currency%", currencies.get(editData.priceNumber).getDisplayName())));
						Task.builder().delayTicks(5).execute(() -> {
							slot.set(itemStack);
						}).submit(plugin);
					}
					if(id == 51) {
						Task.builder().delayTicks(5).execute(() -> {
							player.closeInventory();
							returnItems(player);
						}).submit(plugin);
					}
					if(id == 53 && event.getCursorTransaction().getDefault().getType() == plugin.getFillItems().getItemStack(FillItems.NEXT).getType()) {
						Task.builder().delayTicks(1).execute(() -> {
							player.closeInventory();
						}).submit(plugin);
						Task.builder().delayTicks(5).execute(() -> {
							createInventory(player, page + 1);
						}).submit(plugin);
					}
				}).build(plugin);
		for(Inventory slot : inventory.slots()) {
			int id = slot.getInventoryProperty(SlotIndex.class).get().getValue();
			if(id < 45) {
				if(plugin.getAuctionItems().isEmpty() || currentItem >= plugin.getAuctionItems().size()) {
					slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
				} else {
					SerializedAuctionStack auctionItem = plugin.getAuctionItems().get(currentItem);
					ItemStack itemStack = auctionItem.getSerializedItemStack().getItemStack();
					net.minecraft.item.ItemStack nmsStack = ItemStackUtil.toNative(itemStack);
					NBTTagCompound nbt = nmsStack.hasTagCompound() ? nmsStack.getTagCompound() : new NBTTagCompound();
					nbt.setInteger("Id", currentItem);
					nmsStack.setTagCompound(nbt);
					itemStack = ItemStackUtil.fromNative(nmsStack);
					if(itemStack.get(Keys.ITEM_LORE).isPresent()) {
						itemStack.remove(Keys.ITEM_LORE);
					}
					List<Text> itemLore = itemStack.get(Keys.ITEM_LORE).orElse(new ArrayList<Text>());
					itemLore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "TransactionVariants"));
					if(auctionItem.getPrices().get(0).getBet().doubleValue() > 0) {
						itemLore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "AuctionBet")
								.replace("%currency%", Text.of(auctionItem.getPrices().get(0).getCurrency().getDisplayName()))
								.replace("%price%", Text.of(auctionItem.getPrices().get(0).getBet().doubleValue()))
								.replace("%total%", Text.of(auctionItem.getPrices().get(0).getBet().doubleValue() * itemStack.getQuantity())));
						boolean addEmpty = false;
						for(SerializedAuctionPrice price : plugin.getAuctionItems().get(id).getPrices()) {
							if(price.getPrice().doubleValue() > 0) {
								addEmpty = true;
							}
						}
						if(addEmpty) {
							itemLore.add(Text.EMPTY);
						}
					}
					for(SerializedAuctionPrice price : plugin.getAuctionItems().get(id).getPrices()) {
						if(price.getPrice().doubleValue() > 0) {
							itemLore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "AuctionPrice")
									.replace("%currency%", price.getCurrency().getDisplayName())
									.replace("%price%", Text.of(price.getPrice().doubleValue()))
									.replace("%total%", Text.of(price.getPrice().doubleValue() * itemStack.getQuantity())));
						}
					}
					itemLore.add(Text.EMPTY);
					itemLore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "Expired")
							.replace("%expired%", Text.of(auctionItem.getExpireTimeFromNow())));
					itemLore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "Seller")
							.replace("%seller%", auctionItem.getOwnerName()));
					if(auctionItem.getBetData() != null) {
						itemLore.add(Text.EMPTY);
						itemLore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "CurrentBuyer")
								.replace("%buyer%", Text.of(auctionItem.getBetData().getBuyerName())));
						itemLore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "CurrentBet")
								.replace("%bet%", Text.of(auctionItem.getBetData().getMoney().doubleValue())));
					}
					itemStack.offer(Keys.ITEM_LORE, itemLore);
					if(!auctionItem.isExpired()) {
						currentItem++;
					}
					slot.offer(itemStack);
				}
			} else if(id >= 45 && id <= 53) {
				slot.set(plugin.getFillItems().getItemStack(FillItems.BOTTOM));
				if(id == 45 && page >= 2) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.BACK);
					itemStack.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Back"));
					slot.set(itemStack);
				}
				if(id == 47) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.ADD);
					itemStack.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "AuctionAddItem"));
					slot.set(itemStack);
				}
				if(id == 49) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.CHANGECURRENCY);
					itemStack.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "ChangeCurrency"));
					itemStack.offer(Keys.ITEM_LORE, Arrays.asList(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "CurrentCurrency")
							.replace("%currency%", currencies.get(editData.priceNumber).getDisplayName())));
					slot.set(itemStack);
				}
				if(id == 51) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.RETURN);
					itemStack.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "ReturnAuctionItem"));
					slot.set(itemStack);
				}
				if(id == 53 && plugin.getAuctionItems().size() >= (page * 45)) {
					ItemStack itemStack = plugin.getFillItems().getItemStack(FillItems.NEXT);
					itemStack.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Next"));
					slot.set(itemStack);
				}
			}
		}
		player.openInventory(inventory);
	}

	public void editBet(Player player, int page, int idAuctionItem) {
		Text menuTitle = Text.of("Edit Bet");
		menuTitle = plugin.getLocales().getLocalizedText(player.getLocale(), "Gui", "AuctionBet");
		EditData editData = new EditData();
		SerializedAuctionStack auctionStack = plugin.getAuctionItems().get(idAuctionItem);
		Currency currency = auctionStack.getPrices().get(0).getCurrency();
		editData.itemStack = auctionStack.getSerializedItemStack().getItemStack();
		SerializedBetData oldBetData = auctionStack.getBetData();
		BigDecimal minimalBet = oldBetData == null ? auctionStack.getPrices().get(0).getBet() : oldBetData.getMoney().add(BigDecimal.valueOf(0.01));
		SerializedBetData betData = new SerializedBetData(serverName, player.getUniqueId(), player.getName(), minimalBet, currency);
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
						if(plugin.getAuctionItems().contains(auctionStack) && betData.getMoney().doubleValue() > 0) {
							if(oldBetData != null) {
								if(betData.getMoney().doubleValue() > oldBetData.getMoney().doubleValue() && plugin.getEconomy().checkPlayerBalance(player.getUniqueId(), currency, betData.getMoney())) {
									auctionStack.setBetData(betData);
									Task.builder().delayTicks(5).execute(() -> {
										plugin.getAuctionWorkData().saveAuctionStack(auctionStack);
										player.closeInventory();
										createInventory(player, page);
									}).submit(plugin);
								}
							} else {
								auctionStack.setBetData(betData);
								Task.builder().delayTicks(5).execute(() -> {
									plugin.getAuctionWorkData().saveAuctionStack(auctionStack);
									player.closeInventory();
									createInventory(player, page);
								}).submit(plugin);
							}
						} else {
							Task.builder().delayTicks(5).execute(() -> {
								player.closeInventory();
								createInventory(player, page);
							}).submit(plugin);
						}
					}
					if(id == 26) {
						if(plugin.getAuctionItems().contains(auctionStack) && betData.getMoney().doubleValue() > 0) {
							if(oldBetData != null) {
								if(betData.getMoney().doubleValue() > oldBetData.getMoney().doubleValue() && plugin.getEconomy().checkPlayerBalance(player.getUniqueId(), currency, betData.getMoney())) {
									auctionStack.setBetData(betData);
									Task.builder().delayTicks(5).execute(() -> {
										plugin.getAuctionWorkData().saveAuctionStack(auctionStack);
										player.closeInventory();
										createInventory(player, page);
									}).submit(plugin);
								}
							} else {
								auctionStack.setBetData(betData);
								Task.builder().delayTicks(5).execute(() -> {
									plugin.getAuctionWorkData().saveAuctionStack(auctionStack);
									player.closeInventory();
									createInventory(player, page);
								}).submit(plugin);
							}
						}
					}
					if(event instanceof ClickInventoryEvent.Primary) {
						if(editData.itemStack.getType() == ItemTypes.AIR) return;
						if(id == 0) {
							betData.setMoney(betData.getMoney().add(BigDecimal.valueOf(0.01)));
						}
						if(id == 1) {
							betData.setMoney(betData.getMoney().add(BigDecimal.valueOf(0.1)));
						}
						if(id == 2) {
							betData.setMoney(betData.getMoney().add(BigDecimal.valueOf(0.5)));
						}
						if(id == 3) {
							betData.setMoney(betData.getMoney().add(BigDecimal.valueOf(1)));
						}
						if(id == 4) {
							betData.setMoney(betData.getMoney().add(BigDecimal.valueOf(5)));
						}
						if(id == 5) {
							betData.setMoney(betData.getMoney().add(BigDecimal.valueOf(10)));
						}
						if(id == 6) {
							betData.setMoney(betData.getMoney().add(BigDecimal.valueOf(100)));
						}
						if(id == 7) {
							betData.setMoney(betData.getMoney().add(BigDecimal.valueOf(1000)));
						}
						if(id == 8) {
							betData.setMoney(betData.getMoney().add(BigDecimal.valueOf(10000)));
						}
						if((id <= 8 || id == 13 || id == 21 || id == 23)) {
							if(id <= 8) {
								editData.save = true;
							}
							Inventory update = event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(13)));
							Slot slot13 = update.first();
							slot13.set(getDisplayBetItem(player, betData, editData));
						}
					} else if(event instanceof ClickInventoryEvent.Secondary) {
						if(editData.itemStack.getType() == ItemTypes.AIR) return;
						if(id == 0) {
							betData.setMoney(betData.getMoney().subtract(BigDecimal.valueOf(0.01)));
						}
						if(id == 1) {
							betData.setMoney(betData.getMoney().subtract(BigDecimal.valueOf(0.1)));
						}
						if(id == 2) {
							betData.setMoney(betData.getMoney().subtract(BigDecimal.valueOf(0.5)));
						}
						if(id == 3) {
							betData.setMoney(betData.getMoney().subtract(BigDecimal.valueOf(1)));
						}
						if(id == 4) {
							betData.setMoney(betData.getMoney().subtract(BigDecimal.valueOf(5)));
						}
						if(id == 5) {
							betData.setMoney(betData.getMoney().subtract(BigDecimal.valueOf(10)));
						}
						if(id == 6) {
							betData.setMoney(betData.getMoney().subtract(BigDecimal.valueOf(100)));
						}
						if(id == 7) {
							betData.setMoney(betData.getMoney().subtract(BigDecimal.valueOf(1000)));
						}
						if(id == 8) {
							betData.setMoney(betData.getMoney().subtract(BigDecimal.valueOf(10000)));
						}
						if((id <= 8 || id == 13 || id == 21 || id == 23)) {
							Inventory update = event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(13)));
							Slot slot13 = update.first();
							if(minimalBet.doubleValue() > betData.getMoney().doubleValue()) {
								betData.setMoney(minimalBet);
							}
							slot13.set(getDisplayBetItem(player, betData, editData));
						}
					}
				}).build(plugin);
		for(Inventory slot : inventory.slots()) {
			int id = slot.getInventoryProperty(SlotIndex.class).get().getValue();
			slot.offer(plugin.getFillItems().getItemStack(FillItems.BASIC));
			if(id == 13) {
				slot.set(getDisplayBetItem(player, betData, editData));
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
			if(id == 18) {
				ItemStack back = plugin.getFillItems().getItemStack(FillItems.BACK);
				back.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Back"));
				slot.set(back);
			}
			if(id == 26) {
				ItemStack exit = plugin.getFillItems().getItemStack(FillItems.EXIT);
				exit.offer(Keys.DISPLAY_NAME, plugin.getLocales().getLocalizedText(player.getLocale(), "FillItems", "Exit"));
				slot.set(exit);
			}
		}
		player.openInventory(inventory);
	}

	public void editItem(Player player, int page) {
		Text menuTitle = Text.of("Edit auction item");
		menuTitle = plugin.getLocales().getLocalizedText(player.getLocale(), "Gui", "EditAuctionItem");
		EditData editData = new EditData();
		List<SerializedAuctionPrice> prices = new ArrayList<SerializedAuctionPrice>();
		prices.add(new SerializedAuctionPrice(plugin.getEconomyService().getDefaultCurrency()));
		for(Currency currency : plugin.getEconomyService().getCurrencies()) {
			if(player.hasPermission(Permissions.currencyPermission(currency.getName())) && !currency.getId().equals(plugin.getEconomyService().getDefaultCurrency().getId())) {
				prices.add(new SerializedAuctionPrice(currency));
			}
		}
		SerializedAuctionStack auctionStack = new SerializedAuctionStack(editData.itemStack.copy(), prices, player.getUniqueId(), player.getName(), System.currentTimeMillis() + plugin.getExpire(editData.expire).getTime(), serverName, UUID.randomUUID());
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
						if(!editData.itemStack.getType().getId().equals(ItemTypes.AIR.getId()) && editData.save) {
							player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(editData.itemStack.copy())).poll(editData.itemStack.getQuantity());
							plugin.getAuctionItems().add(auctionStack);
							if(plugin.getRootNode().getNode("Auction", "Expire", String.valueOf(editData.expire), "Fee", "Enable").getBoolean()) {
								if(!plugin.getEconomy().fee(player, prices.get(editData.priceNumber).getCurrency(), BigDecimal.valueOf(plugin.getRootNode().getNode("Auction", "Expire", String.valueOf(editData.expire), "Fee", "Size").getDouble()))) {
									return;
								}
							}
							Task.builder().delayTicks(5).execute(() -> {
								plugin.getAuctionWorkData().saveAuctionStack(auctionStack);
								player.closeInventory();
								createInventory(player, page);
							}).submit(plugin);
						} else {
							Task.builder().delayTicks(5).execute(() -> {
								player.closeInventory();
								createInventory(player, page);
							}).submit(plugin);
						}
					}
					if(id == 21) {
						for(SerializedAuctionPrice price : prices) {
							price.setZero();;
						}
					}
					if(id == 23) {
						editData.priceNumber++;
						if(editData.priceNumber > prices.size() - 1) {
							editData.priceNumber = 0;
						}
						if(editData.priceNumber > 0) {
							editData.bet = false;
						}
					}
					if(id == 26) {
						if(!editData.itemStack.getType().getId().equals(ItemTypes.AIR.getId()) && editData.save) {
							player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(editData.itemStack.copy())).poll(editData.itemStack.getQuantity());
							plugin.getAuctionItems().add(auctionStack);
							if(plugin.getRootNode().getNode("Auction", "Expire", String.valueOf(editData.expire), "Fee", "Enable").getBoolean()) {
								if(!plugin.getEconomy().fee(player, prices.get(editData.priceNumber).getCurrency(), BigDecimal.valueOf(plugin.getRootNode().getNode("Auction", "Expire", String.valueOf(editData.expire), "Fee", "Size").getDouble()))) {
									return;
								}
							}
							Task.builder().delayTicks(5).execute(() -> {
								plugin.getAuctionWorkData().saveAuctionStack(auctionStack);
								player.closeInventory();
							}).submit(plugin);
						} else {
							Task.builder().delayTicks(5).execute(() -> {
								player.closeInventory();
							}).submit(plugin);
						}
					}
					if(id > 26) {
						Inventory update = event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(13)));
						if(!event.getCursorTransaction().getDefault().isEmpty() && !prices.isEmpty()) {
							ItemStack checkedStack = event.getCursorTransaction().getDefault().createStack().copy();
							if(plugin.maskIsBlackList(checkedStack.getType().getId()) || plugin.itemIsBlackList(checkedStack)) {
								player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "ItemBlocked"));
								return;
							}
							editData.itemStack = checkedStack;
							auctionStack.setItemStack(checkedStack);
							if(plugin.getRootNode().getNode("Auction", "NbtLimit").getInt() < auctionStack.getSerializedItemStack().getNBT().toString().length()) {
								player.sendMessage(plugin.getLocales().getLocalizedText(player.getLocale(), "Messages", "LongNBT"));
								return;
							}
							prices.get(editData.priceNumber).setTax(plugin.getExpire(editData.expire).getTax(), editData.itemStack.getQuantity());
							update.set(getDisplayItem(player, auctionStack, editData));
						}
					}
					if(event instanceof ClickInventoryEvent.Primary) {
						if(editData.itemStack.getType() == ItemTypes.AIR) return;
						if(id == 0) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).add(BigDecimal.valueOf(0.01)), editData.bet, editData.priceNumber);
						}
						if(id == 1) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).add(BigDecimal.valueOf(0.1)), editData.bet, editData.priceNumber);
						}
						if(id == 2) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).add(BigDecimal.valueOf(0.5)), editData.bet, editData.priceNumber);
						}
						if(id == 3) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).add(BigDecimal.valueOf(1)), editData.bet, editData.priceNumber);
						}
						if(id == 4) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).add(BigDecimal.valueOf(5)), editData.bet, editData.priceNumber);
						}
						if(id == 5) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).add(BigDecimal.valueOf(10)), editData.bet, editData.priceNumber);
						}
						if(id == 6) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).add(BigDecimal.valueOf(100)), editData.bet, editData.priceNumber);
						}
						if(id == 7) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).add(BigDecimal.valueOf(1000)), editData.bet, editData.priceNumber);
						}
						if(id == 8) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).add(BigDecimal.valueOf(10000)), editData.bet, editData.priceNumber);
						}
						if((id <= 8 || id == 13 || id == 21 || id == 22 || id == 23) && !prices.isEmpty()) {
							if(id == 22) {
								if(editData.bet) {
									editData.bet = false;
								} else {
									editData.bet = true;
									editData.priceNumber = 0;
								}
							}
							if(id <= 8) {
								prices.get(editData.priceNumber).setTax(plugin.getExpire(editData.expire).getTax(), editData.itemStack.getQuantity());
								editData.save = true;
							}
							Inventory update = event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(13)));
							Slot slot13 = update.first();
							slot13.set(getDisplayItem(player, auctionStack, editData));
						}
					} else if(event instanceof ClickInventoryEvent.Secondary) {
						if(editData.itemStack.getType() == ItemTypes.AIR) return;
						if(id == 0) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).subtract(BigDecimal.valueOf(0.01)), editData.bet, editData.priceNumber);
						}
						if(id == 1) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).subtract(BigDecimal.valueOf(0.1)), editData.bet, editData.priceNumber);
						}
						if(id == 2) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).subtract(BigDecimal.valueOf(0.5)), editData.bet, editData.priceNumber);
						}
						if(id == 3) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).subtract(BigDecimal.valueOf(1)), editData.bet, editData.priceNumber);
						}
						if(id == 4) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).subtract(BigDecimal.valueOf(5)), editData.bet, editData.priceNumber);
						}
						if(id == 5) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).subtract(BigDecimal.valueOf(10)), editData.bet, editData.priceNumber);
						}
						if(id == 6) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).subtract(BigDecimal.valueOf(100)), editData.bet, editData.priceNumber);
						}
						if(id == 7) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).subtract(BigDecimal.valueOf(1000)), editData.bet, editData.priceNumber);
						}
						if(id == 8) {
							prices.get(editData.priceNumber).setBetOrPrice(prices.get(editData.priceNumber).getBetOrPrice(editData.bet, editData.priceNumber).subtract(BigDecimal.valueOf(10000)), editData.bet, editData.priceNumber);
						}
						if((id <= 8 || id == 13 || id == 21 || id == 22 || id == 23) && !prices.isEmpty()) {
							if(id == 22) {
								if(editData.expire < plugin.getExpiresLastNumber()) {
									editData.expire++;
								} else {
									editData.expire = 0;
								}
							}
							if(id <= 8) {
								if(prices.get(editData.priceNumber).getPrice().doubleValue() < 0) {
									prices.get(editData.priceNumber).setZero();
								}
								for(SerializedAuctionPrice price : prices) {
									if(price.getPrice().doubleValue() <= 0) {
										editData.save = false;
									} else {
										editData.save = true;
										break;
									}
								}
							}
							prices.get(editData.priceNumber).setTax(plugin.getExpire(editData.expire).getTax(), editData.itemStack.getQuantity());
							Inventory update = event.getTargetInventory().query(QueryOperationTypes.INVENTORY_PROPERTY.of(SlotIndex.of(13)));
							Slot slot13 = update.first();
							slot13.set(getDisplayItem(player, auctionStack, editData));
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
				switchMode.offer(Keys.ITEM_LORE, plugin.getLocales().getLocalizedListText(player.getLocale(), "Lore", "AuctionSwitchMode"));
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

	private void returnItems(Player player) {
		Text menuTitle = Text.of("Return items");
		menuTitle = plugin.getLocales().getLocalizedText(player.getLocale(), "Gui", "AuctionReturn");
		Inventory inventory = Inventory.builder().of(InventoryArchetypes.DOUBLE_CHEST)
				.property(InventoryTitle.PROPERTY_NAME, InventoryTitle.of(menuTitle))
				.listener(ClickInventoryEvent.class, event -> {
					if(event.getTransactions().isEmpty()) {
						return;
					}
					event.setCancelled(true);
					Slot slot = event.getTransactions().get(0).getSlot();
					Optional<SlotIndex> slotIndex = slot.getInventoryProperty(SlotIndex.class);
					int slotId = slotIndex.get().getValue();
					if(slotId <= 53) {
						String itemId = event.getCursorTransaction().getDefault().getType().getId();
						net.minecraft.item.ItemStack nmsStack = ItemStackUtil.fromSnapshotToNative(event.getCursorTransaction().getDefault());
						NBTTagCompound nbt = nmsStack.getTagCompound();
						if(nbt == null) {
							return;
						}
						int id = nbt.getInteger("Id");
						if(plugin.getAuctionItems().size() > id && plugin.getAuctionItems().get(id).getOwnerUUID().equals(player.getUniqueId()) && itemId.equals(plugin.getAuctionItems().get(id).getSerializedItemStack().getType())) {
							ItemStack itemStack = plugin.getAuctionItems().get(id).getSerializedItemStack().getItemStack();
							for(Inventory playerSlot : player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class)).slots()) {
								if(playerSlot.totalItems() == 0) {
									Task.builder().delayTicks(5).execute(() -> {
										playerSlot.offer(itemStack);
										plugin.getAuctionWorkData().removeAuctionStack(plugin.getAuctionItems().get(id).getStackUUID());
										plugin.getAuctionItems().remove(id);
										slot.set(ItemStack.of(ItemTypes.AIR));
									}).submit(plugin);
									break;
								}
							}
						} else {
							Task.builder().delayTicks(5).execute(() -> {
								returnItems(player);
							}).submit(plugin);
						}
					}
				}).build(plugin);
		for(SerializedAuctionStack auctionItem : plugin.getAuctionItems()) {
			if(auctionItem.getServerName().equals(serverName) && auctionItem.getOwnerUUID().equals(player.getUniqueId())) {
				net.minecraft.item.ItemStack nmsStack = ItemStackUtil.toNative(auctionItem.getSerializedItemStack().getItemStack());
				NBTTagCompound nbt = nmsStack.hasTagCompound() ? nmsStack.getTagCompound() : new NBTTagCompound();
				nbt.setInteger("Id", plugin.getAuctionItems().indexOf(auctionItem));
				nmsStack.setTagCompound(nbt);
				inventory.offer(ItemStackUtil.fromNative(nmsStack));
			}
		}
		player.openInventory(inventory);
	}

	private ItemStack getDisplayItem(Player player, SerializedAuctionStack auctionStack, EditData editData) {
		ItemStack itemStack = editData.itemStack.copy();
		List<Text> lore = new ArrayList<Text>();
		if(itemStack.get(Keys.ITEM_LORE).isPresent()) {
			itemStack.remove(Keys.ITEM_LORE);
		}
		auctionStack.updateExpires(plugin.getExpire(editData.expire).getTime());
		lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "CurrentCurrency")
				.replace("%currency%", (editData.bet ? auctionStack.getPrices().get(0).getCurrency().getDisplayName() : auctionStack.getPrices().get(editData.priceNumber).getCurrency().getDisplayName())));
		if(auctionStack.getPrices().get(0).getBet().doubleValue() > 0) {
			lore.add(Text.EMPTY);
			lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "AuctionBet")
					.replace("%currency%", Text.of(auctionStack.getPrices().get(0).getCurrency().getDisplayName()))
					.replace("%price%", Text.of(auctionStack.getPrices().get(0).getBet().doubleValue()))
					.replace("%total%", Text.of(auctionStack.getPrices().get(0).getBet().doubleValue() * itemStack.getQuantity())));
			lore.add(Text.EMPTY);
		}
		boolean addEmpty = false;
		for(SerializedAuctionPrice price : auctionStack.getPrices()) {
			if(price.getPrice().doubleValue() > 0) {
				addEmpty = true;
				lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "AuctionPrice")
						.replace("%currency%", Text.of(price.getCurrency().getDisplayName()))
						.replace("%price%", Text.of(price.getPrice().doubleValue()))
						.replace("%total%", Text.of(price.getPrice().doubleValue() * itemStack.getQuantity())));
			}
		}
		if(plugin.getExpire(editData.expire).isTax()) {
			if(addEmpty) {
				lore.add(Text.EMPTY);
				addEmpty = false;
			}
			if(editData.bet) {
				lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "Tax").replace("%size%", Text.of(auctionStack.getPrices().get(0).getCurrency().getSymbol(), calculateTax(auctionStack, editData, auctionStack.getPrices().get(0).getBet().doubleValue()))));
			} else {
				lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "Tax").replace("%size%", Text.of(auctionStack.getPrices().get(editData.priceNumber).getCurrency().getSymbol(), auctionStack.getPrices().get(editData.priceNumber).getTax())));
			}
		}
		if(plugin.getExpire(editData.expire).isFee()) {
			if(addEmpty) {
				lore.add(Text.EMPTY);
			}
			lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "Fee").replace("%size%", Text.of(auctionStack.getPrices().get(0).getCurrency().getSymbol(), plugin.getExpire(editData.expire).getFee())));
			lore.add(Text.EMPTY);
		}
		lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "Expired")
				.replace("%expired%", Text.of(auctionStack.getExpireTimeFromNow())));
		itemStack.offer(Keys.ITEM_LORE, lore);
		return itemStack;
	}

	private ItemStack getDisplayBetItem(Player player, SerializedBetData betData, EditData editData) {
		ItemStack itemStack = editData.itemStack.copy();
		List<Text> lore = new ArrayList<Text>();
		if(itemStack.get(Keys.ITEM_LORE).isPresent()) {
			itemStack.remove(Keys.ITEM_LORE);
		}
		lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "YourBet")
				.replace("%size%", Text.of(betData.getCurrency().getSymbol(), betData.getMoney().doubleValue()))
				.replace("%total%", Text.of(betData.getCurrency().getSymbol(), betData.getMoney().doubleValue() * itemStack.getQuantity())));
		itemStack.offer(Keys.ITEM_LORE, lore);
		if(plugin.getRootNode().getNode("Auction", "Expire", String.valueOf(editData.expire), "Tax", "Enable").getBoolean()) {
			betData.setTax(plugin.getRootNode().getNode("Auction", "Expire", String.valueOf(editData.expire), "Tax", "Size").getDouble(), itemStack.getQuantity());
			lore.add(plugin.getLocales().getLocalizedText(player.getLocale(), "Lore", "Tax").replace("%size%", Text.of(betData.getCurrency().getSymbol(), betData.getTax())));
		}
		return itemStack;
	}

	private Integer calculateMaxBuyItems(Player player, ItemStack itemStack, SerializedAuctionPrice serializedPrice) {
		if(plugin.getEconomy().getPlayerBalance(player.getUniqueId(), serializedPrice.getCurrency()).doubleValue() < serializedPrice.getPrice().doubleValue()) return 0;
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

	private BigDecimal calculateMoney(ItemStack itemStack, SerializedAuctionPrice serializedPrice) {
		return BigDecimal.valueOf(serializedPrice.getPrice().doubleValue()).pow(itemStack.getQuantity());
	}

	private double calculateTax(SerializedAuctionStack auctionStack, EditData editData, double money) {
		if(plugin.getExpire(editData.expire).isTax()) {
			if(editData.bet) {
				return BigDecimal.valueOf(((money * auctionStack.getSerializedItemStack().getQuantity()) / 100) * plugin.getExpire(editData.expire).getTax()).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
			}
			auctionStack.getPrices().get(editData.priceNumber).setTax(plugin.getRootNode().getNode("Auction", "Expire", String.valueOf(editData.expire), "Tax", "Size").getDouble(), auctionStack.getSerializedItemStack().getQuantity());
			return auctionStack.getPrices().get(editData.priceNumber).getTax();
		}
		return 0;
	}

	private class EditData {
		boolean save = false;
		boolean bet = true;
		int expire = 0;
		int priceNumber = 0;
		ItemStack itemStack = ItemStack.of(ItemTypes.AIR);
	}

}
