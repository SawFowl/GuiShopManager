/*
 * GuiShopManager - Plugin for create chest shops.
 * Copyright (C) 2021 sawfowl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyCraftings is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 */
 
package sawfowl.guishopmanager;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GamePostInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.service.ChangeServiceProviderEvent;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.MainPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.plugin.Dependency;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.plugin.PluginManager;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.action.TextActions;
import org.spongepowered.api.text.serializer.TextSerializers;
import org.spongepowered.api.util.TypeTokens;

import com.google.common.reflect.TypeToken;
import com.google.inject.Inject;

import mr_krab.localeapi.LocaleAPIMain;
import mr_krab.localeapi.utils.LocaleAPI;
import sawfowl.guishopmanager.utils.Economy;
import sawfowl.guishopmanager.utils.Locales;
import sawfowl.guishopmanager.utils.commands.AddBlackList;
import sawfowl.guishopmanager.utils.commands.AuctionAddItem;
import sawfowl.guishopmanager.utils.commands.AuctionOpen;
import sawfowl.guishopmanager.utils.commands.ShopCreate;
import sawfowl.guishopmanager.utils.commands.ShopDelete;
import sawfowl.guishopmanager.utils.commands.ShopEdit;
import sawfowl.guishopmanager.utils.commands.ShopOpen;
import sawfowl.guishopmanager.utils.commands.ShopSetItem;
import sawfowl.guishopmanager.utils.commands.ShopTranslate;
import sawfowl.guishopmanager.utils.configure.Expire;
import sawfowl.guishopmanager.utils.configure.GenerateConfig;
import sawfowl.guishopmanager.utils.configure.GenerateLocales;
import sawfowl.guishopmanager.utils.configure.GeneratedFillItems;
import sawfowl.guishopmanager.utils.data.MySQL;
import sawfowl.guishopmanager.utils.data.WorkConfigs;
import sawfowl.guishopmanager.utils.data.WorkData;
import sawfowl.guishopmanager.utils.data.WorkTables;
import sawfowl.guishopmanager.utils.data.shop.Shop;
import sawfowl.guishopmanager.utils.gui.AuctionMenus;
import sawfowl.guishopmanager.utils.gui.ShopItemMenu;
import sawfowl.guishopmanager.utils.gui.ShopMenu;
import sawfowl.guishopmanager.utils.serialization.SerializedItemStack;
import sawfowl.guishopmanager.utils.serialization.auction.SerializedAuctionStack;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;

@Plugin(id = "guishopmanager",
		name = "GuiShopManager",
		version = "1.0.4-S7.3",
		dependencies = {
				@Dependency(id = "localeapi@1.0.0")
		},
		authors = "SawFowl",
		description = "Auction as in various MMORPG games, in-game admin shops. ")
public class GuiShopManager {

	@Inject
	@DefaultConfig(sharedRoot = false)
	private Path defaultConfig;
	@Inject
	@ConfigDir(sharedRoot = false)
	private Path configDir;
	@Inject
	@ConfigDir(sharedRoot = false)
	private File configFile;
	@Inject
	@DefaultConfig(sharedRoot = false)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private CommentedConfigurationNode rootNode;
	private ConfigurationLoader<CommentedConfigurationNode> configLoaderBlackLists;
	private CommentedConfigurationNode blackListNode;
	@Inject
	private Logger logger;
	@Inject
	private PluginManager pluginManager;
	private static PluginContainer container;
	private static EconomyService economyService;
	private static EventContext eventContext;
	private LocaleAPI localeAPI;

	private GuiShopManager instance;
	private GeneratedFillItems fillItems;
	private ShopMenu shopMenu;
	private AuctionMenus auctionMenus;
	private ShopItemMenu shopItemEditor;
	private WorkData workShopData;
	private WorkData workAuctionData;
	private MySQL mySQL;
	private Economy economy;
	private Locales locales;

	private Map<String, Shop> shops = new HashMap<String, Shop>();
	private List<SerializedAuctionStack> auctionItems = new ArrayList<SerializedAuctionStack>();
	private Map<UUID, List<SerializedAuctionStack>> expiredAuctionItems = new HashMap<UUID, List<SerializedAuctionStack>>();
	private Map<UUID, List<SerializedAuctionStack>> expiredBetAuctionItems = new HashMap<UUID, List<SerializedAuctionStack>>();
	private List<Expire> expires = new ArrayList<Expire>();
	private List<String> blackListMasks = new ArrayList<String>();
	private List<SerializedItemStack> blackListStacks = new ArrayList<SerializedItemStack>();

	private Task updateAuctionTask;

	public Logger getLogger() {
		return logger;
	}
	public GuiShopManager getInstance() {
		return instance;
	}
	public File getConfigFile() {
		return configFile;
	}
	public Path getConfigDir() {
		return configDir;
	}
	public CommentedConfigurationNode getRootNode() {
		return rootNode;
	}
	public CommentedConfigurationNode getBlackListNode() {
		return blackListNode;
	}
	public PluginContainer getPluginContainer() {
		return container;
	}
	public EconomyService getEconomyService() {
		return economyService;
	}
	public EventContext getEventContext() {
		return eventContext;
	}
	public LocaleAPI getLocaleAPI() {
		return localeAPI;
	}
	public GeneratedFillItems getFillItems() {
		return fillItems;
	}
	public ShopMenu getShopMenu() {
		return shopMenu;
	}
	public AuctionMenus getAuctionMenus() {
		return auctionMenus;
	}
	public ShopItemMenu getShopItemMenu() {
		return shopItemEditor;
	}
	public WorkData getWorkShopData() {
		return workShopData;
	}
	public WorkData getAuctionWorkData() {
		return workAuctionData;
	}
	public MySQL getMySQL() {
		return mySQL;
	}
	public Economy getEconomy() {
		return economy;
	}
	public Locales getLocales() {
		return locales;
	}
	public void addShop(String id, Shop shop) {
		shops.put(id, shop);
		workShopData.saveShop(id);
	}
	public Shop getShop(String id) {
		return shops.get(id);
	}
	public List<Shop> getAllShops() {
		return shops.values().stream().collect(Collectors.toList());
	}
	public boolean shopExists(String id) {
		return shops.containsKey(id);
	}
	public void removeShop(String shopId) {
		workShopData.deleteShop(shopId);
		shops.remove(shopId);
	}
	public List<SerializedAuctionStack> getAuctionItems() {
		return auctionItems;
	}
	public void updateAuctionItems(List<SerializedAuctionStack>items) {
		auctionItems.clear();
		auctionItems.addAll(items);
	}
	public Map<UUID, List<SerializedAuctionStack>> getExpiredAuctionItems() {
		return expiredAuctionItems;
	}
	public Map<UUID, List<SerializedAuctionStack>> getExpiredBetAuctionItems() {
		return expiredBetAuctionItems;
	}
	public Expire getExpire(int expire) {
		return expires.get(expire);
	}
	public int getExpiresLastNumber() {
		return expires.size() - 1;
	}
	public void setBlackListMasks(List<String> blackListMasks) {
		this.blackListMasks = blackListMasks;
	}
	public void addBlackListMask(ItemStack itemStack) {
		blackListMasks.add(itemStack.getType().getId());
		try {
			blackListNode.getNode("MasksList").setValue(new TypeToken<List<String>>() {
				private static final long serialVersionUID = 01;
			}, blackListMasks);
		} catch (ObjectMappingException e) {
			logger.error(e.getLocalizedMessage());
		}
	}
	public void addBlackListStack(ItemStack itemStack) {
		blackListStacks.add(new SerializedItemStack(itemStack));
		try {
			blackListNode.getNode("StacksList").setValue(new TypeToken<List<SerializedItemStack>>() {
				private static final long serialVersionUID = 01;
			}, blackListStacks);
		} catch (ObjectMappingException e) {
			logger.error(e.getLocalizedMessage());
		}
	}
	public void setBlackListStacks(List<SerializedItemStack> blackListStacks) {
		this.blackListStacks = blackListStacks;
	}
	public boolean maskIsBlackList(String check) {
		return blackListMasks.toString().contains(check) || blackListMasks.toString().contains(check.split(":")[1]);
	}
	public boolean itemIsBlackList(ItemStack check) {
		SerializedItemStack serializedItemStack = new SerializedItemStack(check);
		serializedItemStack.setQuantity(1);
		return blackListStacks.contains(serializedItemStack) || blackListStacks.toString().contains(serializedItemStack.toString());
	}

	@Listener
	public void onPreInitialization(GamePreInitializationEvent event) throws IOException {
		instance = this;
		logger = LoggerFactory.getLogger("\033[36mGuiShopManager\033[0m");
		container = pluginManager.getPlugin("guishopmanager").orElse(null);
		eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, container).build();
		configLoader = HoconConfigurationLoader.builder().setPath(configDir.resolve("Config.conf")).build();
		configLoaderBlackLists = HoconConfigurationLoader.builder().setPath(configDir.resolve("AuctionBlackList.conf")).build();
		loadConfigs();
		new GenerateConfig(instance);
		loadExpires();
		fillItems = new GeneratedFillItems(instance);
		economy = new Economy(instance);
		locales = new Locales(instance);
		setWorkDataClasses();
		shopMenu = new ShopMenu(instance);
		auctionMenus = new AuctionMenus(instance);
		shopItemEditor = new ShopItemMenu(instance);
	}

	@Listener
	public void onPostInitialization(GamePostInitializationEvent event) {
		localeAPI = LocaleAPIMain.getInstance().getAPI();
		new GenerateLocales(instance);
		commandRegister();
	}

	@Listener
	public void gameStarted(GameStartedServerEvent event) {
		workShopData.loadShops();
		updateAuctionData();
	}

	@Listener
	public void gameReload(GameReloadEvent event) {
		Task.builder().async().execute(() -> {
			reload();
		}).submit(this);
	}

	private void reload() {
		loadConfigs();
		setWorkDataClasses();
    	fillItems = null;
		fillItems = new GeneratedFillItems(instance);
		expires.clear();
		loadExpires();
		shops.clear();
		workShopData.loadShops();
		updateAuctionData();
	}

	private void setWorkDataClasses() {
		workShopData = workAuctionData = null;
		if(mySQL != null) {
			mySQL = null;
		}
		if(rootNode.getNode("SplitStorage", "Auction").getBoolean() || rootNode.getNode("SplitStorage", "Shops").getBoolean()) {
			if(rootNode.getNode("SplitStorage", "Auction").getBoolean()) {
				createMySQLConnect();
				workAuctionData = new WorkTables(instance);
			} else {
				workAuctionData = new WorkConfigs(instance);
			}
			if(rootNode.getNode("SplitStorage", "Shops").getBoolean()) {
				createMySQLConnect();
				workShopData = new WorkTables(instance);
			} else {
				File shopsDir = Paths.get(configDir + File.separator + rootNode.getNode("StorageFolder").getString()).toFile();
				if(!shopsDir.exists() || !shopsDir.isDirectory()) {
					shopsDir.mkdir();
				}
				workShopData = new WorkConfigs(instance);
			}
		} else {
			if(!rootNode.getNode("MySQLStorage").getBoolean()) {
				File shopsDir = Paths.get(configDir + File.separator + rootNode.getNode("StorageFolder").getString()).toFile();
				if(!shopsDir.exists() || !shopsDir.isDirectory()) {
					shopsDir.mkdir();
				}
				workShopData = workAuctionData = new WorkConfigs(instance);
			} else {
				createMySQLConnect();
				workShopData = workAuctionData = new WorkTables(instance);
			}
		}
	}

	private void createMySQLConnect() {
		if(mySQL == null) 
		mySQL = new MySQL(
				instance,
				rootNode.getNode("MySQL", "Host").getString(),
				rootNode.getNode("MySQL", "Port").getString(),
				rootNode.getNode("MySQL", "DataBase").getString(),
				rootNode.getNode("MySQL", "User").getString(),
				rootNode.getNode("MySQL", "Password").getString(),
				rootNode.getNode("MySQL", "SSL").getString());
	}

	private void loadExpires() {
		for(int i = 1 ;  ; i++) {
			if(rootNode.getNode("Auction", "Expire", String.valueOf(i)).isVirtual()) {
				break;
			}
			expires.add(new Expire(rootNode.getNode("Auction", "Expire", String.valueOf(i), "Time").getInt(), rootNode.getNode("Auction", "Expire", String.valueOf(i), "Tax", "Size").getDouble(), rootNode.getNode("Auction", "Expire", String.valueOf(i), "Fee", "Size").getDouble(), rootNode.getNode("Auction", "Expire", String.valueOf(i), "Tax", "Enable").getBoolean(), rootNode.getNode("Auction", "Expire", String.valueOf(i), "Fee", "Enable").getBoolean()));
			
		}
	}

	private void updateAuctionData() {
		if(rootNode.getNode("Auction", "Enable").getBoolean()) {
			if(updateAuctionTask != null) {
				updateAuctionTask.cancel();
				updateAuctionTask = null;
			}
			updateAuctionTask = Task.builder().async().interval(55, TimeUnit.SECONDS).execute(() -> {
				workAuctionData.loadAuction();
				Task.builder().async().delay(5, TimeUnit.SECONDS).execute(() -> {
					if(!auctionItems.isEmpty()) {
						List<SerializedAuctionStack> items = new ArrayList<SerializedAuctionStack>();
						items.addAll(auctionItems);
						for(SerializedAuctionStack auctionItem : items) {
							if(auctionItem.isExpired()) {
								auctionItems.remove(auctionItem);
								workAuctionData.removeAuctionStack(auctionItem.getStackUUID());
								if(auctionItem.getBetData() != null && auctionItem.getBetData().getServer().equals(rootNode.getNode("Auction", "Server").getString()) && 
										economy.checkPlayerBalance(auctionItem.getBetData().getBuyerUUID(), auctionItem.getBetData().getCurrency(), auctionItem.getBetData().getMoney().multiply(BigDecimal.valueOf(auctionItem.getSerializedItemStack().getQuantity())))) {
									UUID uuid = auctionItem.getBetData().getBuyerUUID();
									economy.auctionTransaction(uuid, auctionItem, 0, true);
									auctionItem.setOwner(uuid, auctionItem.getBetData().getBuyerName().toPlain());
									if(!expiredBetAuctionItems.containsKey(uuid)) {
										List<SerializedAuctionStack> newAdded = new ArrayList<SerializedAuctionStack>();
										newAdded.add(auctionItem);
										expiredBetAuctionItems.put(uuid, newAdded);
										workAuctionData.saveExpireBetAuctionData(auctionItem);
									} else {
										boolean add = true;
										for(SerializedAuctionStack stack : expiredBetAuctionItems.get(uuid)) {
											if(stack.getStackUUID().equals(auctionItem.getStackUUID())) {
												add = false;
											}
										}
										if(add) {
											expiredBetAuctionItems.get(uuid).add(auctionItem);
											workAuctionData.saveExpireBetAuctionData(auctionItem);
										}
									}
								} else {
									UUID uuid = auctionItem.getOwnerUUID();
									if(!expiredAuctionItems.containsKey(uuid)) {
										List<SerializedAuctionStack> newAdded = new ArrayList<SerializedAuctionStack>();
										newAdded.add(auctionItem);
										expiredAuctionItems.put(uuid, newAdded);
										workAuctionData.saveExpireAuctionData(auctionItem);
									} else {
										boolean add = true;
										for(SerializedAuctionStack stack : expiredAuctionItems.get(uuid)) {
											if(stack.getStackUUID().equals(auctionItem.getStackUUID())) {
												add = false;
											}
										}
										if(add) {
											expiredAuctionItems.get(uuid).add(auctionItem);
											workAuctionData.saveExpireAuctionData(auctionItem);
										}
									}
								}
							}
						}
						items.clear();
						items = null;
					}
				}).submit(this);
				if(Sponge.getServer().getOnlinePlayers().size() != 0) {
					if(!expiredAuctionItems.isEmpty()) {
						Sponge.getServer().getOnlinePlayers().forEach(this::checkExpired);
					}
					if(!expiredBetAuctionItems.isEmpty()) {
						Sponge.getServer().getOnlinePlayers().forEach(this::checkExpiredBet);
					}
				}
			}).submit(this);
		} else {
			if(updateAuctionTask != null) {
				updateAuctionTask.cancel();
				updateAuctionTask = null;
			}
		}
	}

	private void checkExpired(Player player) {
		if(expiredAuctionItems.containsKey(player.getUniqueId())) {
			UUID uuid = player.getUniqueId();
			boolean sendMessage = false;
			for(SerializedAuctionStack auctionStack : expiredAuctionItems.get(uuid)) {
				if(auctionStack.getServerName().equals(rootNode.getNode("Auction", "Server").getString())) {
					sendMessage = true;
					break;
				}
			}
			if(sendMessage) {
				player.sendMessage(Text.builder().append(locales.getLocalizedText(player.getLocale(), "Messages", "AuctionExpired"))
						.onClick(TextActions.executeCallback(callback -> {
							if(expiredAuctionItems.containsKey(uuid)) {
								if(!expiredAuctionItems.get(uuid).isEmpty()) {
									MainPlayerInventory mainPlayerInventory = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class));
									int emptySlots = 0;
									for(Inventory slot : mainPlayerInventory.slots()) {
										if(slot.totalItems() == 0) {
											emptySlots++;
										}
									}
									List<SerializedAuctionStack> toRemove = new ArrayList<SerializedAuctionStack>();
									toRemove.addAll(expiredAuctionItems.get(uuid));
									for(SerializedAuctionStack auctionItem : toRemove) {
										if(emptySlots <= 0) {
											try {
												player.sendMessage(locales.getOrDefaultLocale(player.getLocale()).getLocaleNode().getNode("Messages", "NoEmptySlots").getValue(TypeTokens.TEXT_TOKEN).replace("%value%", Text.of(expiredAuctionItems.get(uuid).size())));
											} catch (ObjectMappingException e) {
												logger.error(e.getLocalizedMessage());
											}
											return;
										}
										if(auctionItem.getServerName().equals(rootNode.getNode("Auction", "Server").getString())) {
											emptySlots--;
											mainPlayerInventory.offer(auctionItem.getSerializedItemStack().getItemStack());
											expiredAuctionItems.get(uuid).remove(auctionItem);
											workAuctionData.removeExpireAuctionData(auctionItem.getStackUUID());
										}
									}
								}
							}
						})).build());
			}
		}
	}

	private void checkExpiredBet(Player player) {
		if(expiredBetAuctionItems.containsKey(player.getUniqueId())) {
			UUID uuid = player.getUniqueId();
			boolean sendMessage = false;
			for(SerializedAuctionStack auctionStack : expiredBetAuctionItems.get(uuid)) {
				if(auctionStack.getBetData().getServer().equals(rootNode.getNode("Auction", "Server").getString())) {
					sendMessage = true;
					break;
				}
			}
			if(sendMessage) {
				player.sendMessage(Text.builder().append(locales.getLocalizedText(player.getLocale(), "Messages", "AuctionBetExpired"))
						.onClick(TextActions.executeCallback(callback -> {
							if(expiredBetAuctionItems.containsKey(uuid)) {
								if(!expiredBetAuctionItems.get(uuid).isEmpty()) {
									MainPlayerInventory mainPlayerInventory = player.getInventory().query(QueryOperationTypes.INVENTORY_TYPE.of(MainPlayerInventory.class));
									int emptySlots = 0;
									for(Inventory slot : mainPlayerInventory.slots()) {
										if(slot.totalItems() == 0) {
											emptySlots++;
										}
									}
									List<SerializedAuctionStack> toRemove = new ArrayList<SerializedAuctionStack>();
									toRemove.addAll(expiredBetAuctionItems.get(uuid));
									for(SerializedAuctionStack auctionItem : toRemove) {
										if(emptySlots <= 0) {
											try {
												player.sendMessage(locales.getOrDefaultLocale(player.getLocale()).getLocaleNode().getNode("Messages", "NoEmptySlots").getValue(TypeTokens.TEXT_TOKEN).replace("%value%", Text.of(expiredAuctionItems.get(uuid).size())));
											} catch (ObjectMappingException e) {
												logger.error(e.getLocalizedMessage());
											}
											return;
										}
										if(auctionItem.getBetData().getServer().equals(rootNode.getNode("Auction", "Server").getString())) {
											emptySlots--;
											mainPlayerInventory.offer(auctionItem.getSerializedItemStack().getItemStack());
											expiredBetAuctionItems.get(uuid).remove(auctionItem);
											workAuctionData.removeExpireBetAuctionData(auctionItem.getStackUUID());
										}
									}
								}
							}
						})).build());
			}
		}
	}

	public void updateConfigs() {
		try {
			configLoaderBlackLists.save(blackListNode);
			configLoader.save(rootNode);
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
		}
	}

	public void loadConfigs() {
		try {
			rootNode = configLoader.load();
			blackListNode = configLoaderBlackLists.load();
		} catch (IOException e) {
			logger.error(e.getLocalizedMessage());
		}
	}

	@Listener
    public void onChangeServiceProvider(ChangeServiceProviderEvent event) {
        if(event.getService().equals(EconomyService.class)) {
            economyService = (EconomyService) event.getNewProviderRegistration().getProvider();
        }
	}

	public void commandRegister() {
		
		CommandSpec commandAuctionAdd = CommandSpec.builder()
		        .permission(Permissions.auctionadditem)
		        .arguments(GenericArguments.optional(GenericArguments.doubleNum(Text.of("Bet"))),
		        		GenericArguments.optional(GenericArguments.doubleNum(Text.of("Price"))),
		        		GenericArguments.optional(GenericArguments.string(Text.of("Currency"))))
		        .executor(new AuctionAddItem(instance))
		        .build();
		
		CommandSpec commandAuction = CommandSpec.builder()
		        .permission(Permissions.auctionopenself)
		        .arguments(GenericArguments.optional(GenericArguments.player(Text.of("Player"))))
		        .child(commandAuctionAdd, "add", "additem")
		        .executor(new AuctionOpen(instance))
		        .build();
		
		CommandSpec commandCreate = CommandSpec.builder()
		        .permission(Permissions.create)
		        .arguments(GenericArguments.optional(GenericArguments.string(Text.of("Shop"))))
		        .executor(new ShopCreate(instance))
		        .build();
		
		CommandSpec commandDelete = CommandSpec.builder()
		        .permission(Permissions.delete)
		        .arguments(GenericArguments.optional(GenericArguments.string(Text.of("Shop"))))
		        .executor(new ShopDelete(instance))
		        .build();
		
		CommandSpec commandEdit = CommandSpec.builder()
		        .permission(Permissions.edit)
		        .arguments(GenericArguments.optional(GenericArguments.string(Text.of("Shop"))))
		        .executor(new ShopEdit(instance))
		        .build();
		
		CommandSpec commandTranslate = CommandSpec.builder()
		        .permission(Permissions.edit)
		        .arguments(GenericArguments.optional(GenericArguments.string(Text.of("Shop"))), 
		        		GenericArguments.optional(GenericArguments.string(Text.of("Locale"))),
		        		GenericArguments.optional(GenericArguments.remainingJoinedStrings(Text.of("Translate"))))
		        .executor(new ShopTranslate(instance))
		        .build();
		
		CommandSpec commandSetShopItem = CommandSpec.builder()
		        .permission(Permissions.edit)
		        .arguments(GenericArguments.optional(GenericArguments.string(Text.of("Shop"))),
		        		GenericArguments.optional(GenericArguments.integer(Text.of("Menu"))),
		        		GenericArguments.optional(GenericArguments.integer(Text.of("Slot"))),
		        		GenericArguments.optional(GenericArguments.doubleNum(Text.of("BuyPrice"))),
		        		GenericArguments.optional(GenericArguments.doubleNum(Text.of("SellPrice"))),
		        		GenericArguments.optional(GenericArguments.string(Text.of("Currency"))))
		        .executor(new ShopSetItem(instance))
		        .build();
		
		CommandSpec commandOpen = CommandSpec.builder()
		        .permission(Permissions.openself)
		        .arguments(GenericArguments.optional(GenericArguments.string(Text.of("Shop"))), 
		        		GenericArguments.optional(GenericArguments.player(Text.of("Player"))))
		        .executor(new ShopOpen(instance))
		        .build();
		
		CommandSpec commandItemBlocking = CommandSpec.builder()
		        .permission(Permissions.auctionitemblocking)
		        .arguments(GenericArguments.flags().flag("mask").flag("item").flag("m").flag("i").buildWith(GenericArguments.none()))
		        .executor(new AddBlackList(instance))
		        .build();
		
		CommandSpec commandReload = CommandSpec.builder()
		        .permission(Permissions.reload)
		        .executor((src, args) -> {
					reload();
					try {
						src.sendMessage(locales.getOrDefaultLocale(src.getLocale()).getLocaleNode().getNode("Messages", "Reload").getValue(TypeTokens.TEXT_TOKEN));
					} catch (ObjectMappingException e) {
						logger.error(e.getLocalizedMessage());
					}
    	            return CommandResult.success();
		        })
		        .build();
		
		CommandSpec mainCommand;
		if(rootNode.getNode("Auction", "Enable").getBoolean()) {
			mainCommand = CommandSpec.builder()
			        .child(commandAuction, "auction", "market")
			        .child(commandItemBlocking, "blacklist", "block")
			        .child(commandCreate, "create")
			        .child(commandDelete, "delete")
			        .child(commandEdit, "edit")
			        .child(commandSetShopItem, "setshopitem", "set", "add")
			        .child(commandTranslate, "translate")
			        .child(commandOpen, "open")
			        .child(commandReload, "reload")
			        .executor((src, args) -> {
			        	List<Text> messages = new ArrayList<Text>();
				        try {
				        	if(src.hasPermission(Permissions.auctionopenother)) {
				        		Text create = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm auction &e[Player]")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.suggestCommand("/guishopmanager auction ")).build();
				        		messages.add(create);
				        		Text blacklist = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm blacklist &e<flags [mask | item]>")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.suggestCommand("/guishopmanager blacklist ")).build();
				        		messages.add(blacklist);
				        	} else if(src.hasPermission(Permissions.auctionopenself)) {
					        	Text create = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm auction")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.runCommand("/guishopmanager auction")).build();
					        	messages.add(create);
					        }
				        	if(src.hasPermission(Permissions.auctionadditem)) {
				        		Text additem = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm auction add &e<Bet> <Price> <Currency>")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.suggestCommand("/guishopmanager auction add 0 0 <currency>")).build();
				        		messages.add(additem);
				        	}
				        	if(src.hasPermission(Permissions.create)) {
				        		Text create = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm create &c<Shop>")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.suggestCommand("/guishopmanager create ")).build();
				        		messages.add(create);
				        	}
				        	if(src.hasPermission(Permissions.delete)) {
				        		Text delete = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm delete &c<Shop>")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.suggestCommand("/guishopmanager delete ")).build();
				        		messages.add(delete);
				        	}
				        	if(src.hasPermission(Permissions.edit)) {
				        		Text translate = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm translate &c<Shop> <Locale> <DisplayName>")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.suggestCommand("/guishopmanager translate")).build();
				        		messages.add(translate);
				        		Text edit = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm edit &e[shop]")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.runCommand("/guishopmanager edit")).build();
				        		messages.add(edit);
								Text setShopItem = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm setshopitem &c<Shop> <Menu> <Slot> <Buy.Price> <Sell.Price> &e[Currency]")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.suggestCommand("/guishopmanager setshopitem")).build();
								messages.add(setShopItem);
				        	}
				        	if(src.hasPermission(Permissions.openother)) {
				        		Text open = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm open &e[Shop] [Player]")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.runCommand("/guishopmanager open")).build();
				        		messages.add(open);
			        		} else if(src.hasPermission(Permissions.openself)) {
					        	Text open = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm open &e[Shop]")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.suggestCommand("/guishopmanager open")).build();
					        	messages.add(open);
				        	}
				        	if(src.hasPermission(Permissions.reload)) {
				        		Text reload = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm reload")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.runCommand("/guishopmanager reload")).build();
				        		messages.add(reload);
				        	}
				        	if(!messages.isEmpty()) {
								PaginationList.builder()
								.contents(messages)
								.title(locales.getOrDefaultLocale(src.getLocale()).getLocaleNode().getNode("Messages", "CommandsTitle").getValue(TypeTokens.TEXT_TOKEN))
								.padding(locales.getOrDefaultLocale(src.getLocale()).getLocaleNode().getNode("Messages", "CommandsPadding").getValue(TypeTokens.TEXT_TOKEN))
								.linesPerPage(10)
								.sendTo(src);
				        	}
						} catch (ObjectMappingException e) {
							logger.error(e.getLocalizedMessage());
						}
		        		return CommandResult.success();
			        })
			        .build();

			if(rootNode.getNode("Aliases", "Auction", "Enable").getBoolean() && !rootNode.getNode("Aliases", "Auction", "List").isEmpty()) {
				try {
					List<String> aliases = rootNode.getNode("Aliases", "Auction", "List").getValue(new TypeToken<List<String>>() {
						private static final long serialVersionUID = 01;});
						Sponge.getCommandManager().register(instance, commandAuction, aliases);
					} catch (ObjectMappingException e) {
					logger.error(e.getLocalizedMessage());
				}
			}
			
		} else {
			mainCommand = CommandSpec.builder()
			        .child(commandCreate, "create")
			        .child(commandDelete, "delete")
			        .child(commandEdit, "edit")
			        .child(commandSetShopItem, "setshopitem", "set", "add")
			        .child(commandTranslate, "translate")
			        .child(commandOpen, "open")
			        .child(commandReload, "reload")
			        .executor((src, args) -> {
			        	List<Text> messages = new ArrayList<Text>();
				        if(src.hasPermission(Permissions.create)) {
							Text create = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm create &c<Shop>")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.suggestCommand("/guishopmanager create ")).build();
							messages.add(create);
						}
						if(src.hasPermission(Permissions.delete)) {
							Text delete = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm delete &c<Shop>")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.suggestCommand("/guishopmanager delete ")).build();
							messages.add(delete);
						}
						if(src.hasPermission(Permissions.edit)) {
							Text translate = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm translate &c<Shop> <Locale> <DisplayName>")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.suggestCommand("/guishopmanager translate")).build();
							messages.add(translate);
							Text edit = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm edit &e[shop]")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.runCommand("/guishopmanager edit")).build();
							messages.add(edit);
							Text setShopItem = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm setshopitem &c<Shop> <Menu> <Slot> <Buy.Price> <Sell.Price> &e[Currency]")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.suggestCommand("/guishopmanager setshopitem")).build();
							messages.add(setShopItem);
						}
						if(src.hasPermission(Permissions.openother)) {
							Text open = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm open &e[Shop] [Player]")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.runCommand("/guishopmanager open")).build();
							messages.add(open);
						} else if(src.hasPermission(Permissions.openself)) {
							Text open = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm open &e[Shop]")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.suggestCommand("/guishopmanager open")).build();
							messages.add(open);
						}
						if(src.hasPermission(Permissions.reload)) {
							Text reload = Text.builder().append(TextSerializers.FORMATTING_CODE.deserialize("&a/gsm reload")).onHover(TextActions.showText(locales.getLocalizedText(src.getLocale(), "Hover", "RunCommand"))).onClick(TextActions.runCommand("/guishopmanager reload")).build();
							messages.add(reload);
						}
						if(!messages.isEmpty()) {
							PaginationList.builder()
							.contents(messages)
							.title(locales.getLocalizedText(src.getLocale(), "Messages", "CommandsTitle"))
							.padding(locales.getLocalizedText(src.getLocale(), "Messages", "CommandsPadding"))
							.linesPerPage(10)
							.sendTo(src);
						}
		        		return CommandResult.success();
			        })
			        .build();
		}
		
		Sponge.getCommandManager().register(instance, mainCommand, "guishopmanager", "gsm");
		
		if(rootNode.getNode("Aliases", "ShopOpen", "Enable").getBoolean() && !rootNode.getNode("Aliases", "ShopOpen", "List").isEmpty()) {
			try {
				List<String> aliases = rootNode.getNode("Aliases", "ShopOpen", "List").getValue(new TypeToken<List<String>>() {
					private static final long serialVersionUID = 01;});
					Sponge.getCommandManager().register(instance, commandOpen, aliases);
				} catch (ObjectMappingException e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}

}