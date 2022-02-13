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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.api.Server;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.Command;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PrimaryPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryTypes;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.plugin.PluginContainer;

import com.google.inject.Inject;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import org.spongepowered.plugin.builtin.jvm.Plugin;
import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.guishopmanager.utils.Economy;
import sawfowl.guishopmanager.utils.Locales;
import sawfowl.guishopmanager.commands.AddBlackList;
import sawfowl.guishopmanager.commands.AuctionAddItem;
import sawfowl.guishopmanager.commands.AuctionOpen;
import sawfowl.guishopmanager.commands.MainCommand;
import sawfowl.guishopmanager.commands.ShopCreate;
import sawfowl.guishopmanager.commands.ShopDelete;
import sawfowl.guishopmanager.commands.ShopEdit;
import sawfowl.guishopmanager.commands.ShopOpen;
import sawfowl.guishopmanager.commands.ShopSetItem;
import sawfowl.guishopmanager.commands.ShopTranslate;
import sawfowl.guishopmanager.configure.Expire;
import sawfowl.guishopmanager.configure.GenerateConfig;
import sawfowl.guishopmanager.configure.GenerateLocales;
import sawfowl.guishopmanager.configure.GeneratedFillItems;
import sawfowl.guishopmanager.data.MySQL;
import sawfowl.guishopmanager.data.WorkConfigs;
import sawfowl.guishopmanager.data.WorkData;
import sawfowl.guishopmanager.data.WorkTables;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.gui.AuctionMenus;
import sawfowl.guishopmanager.gui.ShopMenus;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.localeapi.api.LocaleService;
import sawfowl.localeapi.event.LocaleServiseEvent;
import sawfowl.localeapi.serializetools.SerializedItemStack;

@Plugin("guishopmanager")
public class GuiShopManager {

	@Inject
	@DefaultConfig(sharedRoot = false)
	private Path defaultConfig;
	private Path configDir;
	private File configFile;
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private CommentedConfigurationNode rootNode;
	private ConfigurationLoader<CommentedConfigurationNode> configLoaderBlackLists;
	private CommentedConfigurationNode blackListNode;
	private Logger logger;
	private static PluginContainer container;
	private static EconomyService economyService;
	private static EventContext eventContext;
	private LocaleService localeAPI;

	private GuiShopManager instance;
	private GeneratedFillItems fillItems;
	private ShopMenus shopMenus;
	private AuctionMenus auctionMenus;
	private WorkData workShopData;
	private WorkData workAuctionData;
	private MySQL mySQL;
	private Economy economy;
	private Locales locales;
	private GenerateConfig generateConfig;

	private Map<String, Shop> shops = new HashMap<String, Shop>();
	private LinkedHashMap<UUID, SerializedAuctionStack> auctionItems = new LinkedHashMap<UUID, SerializedAuctionStack>();
	private Map<UUID, List<SerializedAuctionStack>> expiredAuctionItems = new HashMap<UUID, List<SerializedAuctionStack>>();
	private Map<UUID, List<SerializedAuctionStack>> expiredBetAuctionItems = new HashMap<UUID, List<SerializedAuctionStack>>();
	private List<Expire> expires = new ArrayList<Expire>();
	private List<String> blackListMasks = new ArrayList<String>();
	private List<SerializedItemStack> blackListStacks = new ArrayList<SerializedItemStack>();

	private ScheduledTask updateAuctionTask;

	@Inject
	public GuiShopManager(PluginContainer pluginContainer, @ConfigDir(sharedRoot = false) Path configDirectory) {
		configDir = configDirectory;
		configFile = configDirectory.toFile();
		container = pluginContainer;
	}

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
	public LocaleService getLocaleAPI() {
		return localeAPI;
	}
	public GeneratedFillItems getFillItems() {
		return fillItems;
	}
	public ShopMenus getShopMenu() {
		return shopMenus;
	}
	public AuctionMenus getAuctionMenus() {
		return auctionMenus;
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
	public boolean shopsEmpty() {
		return shops.isEmpty();
	}
	public boolean shopExists(String id) {
		return shops.containsKey(id);
	}
	public void removeShop(String shopId) {
		workShopData.deleteShop(shopId);
		shops.remove(shopId);
	}
	public Map<UUID, SerializedAuctionStack> getAuctionItems() {
		return auctionItems;
	}

	public void updateAuctionItems(Map<UUID, SerializedAuctionStack> items) {
		auctionItems.clear();
		auctionItems.putAll(items);
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
		blackListMasks.add(itemStack.type().toString());
		try {
			blackListNode.node("MasksList").setList(String.class, blackListMasks);
		} catch (SerializationException e) {
			logger.error(e.getLocalizedMessage());
		}
	}
	public void addBlackListStack(ItemStack itemStack) {
		blackListStacks.add(new SerializedItemStack(itemStack));
		try {
			blackListNode.node("StacksList").setList(SerializedItemStack.class, blackListStacks);
		} catch (SerializationException e) {
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
	public void onConstruct(LocaleServiseEvent.Construct event) {
		instance = this;
		logger = LogManager.getLogger("GuiShopManager");
		eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, container).build();
		localeAPI = event.getLocaleService();
		locales = new Locales(instance);
		new GenerateLocales(instance);
		configLoader = HoconConfigurationLoader.builder().defaultOptions(localeAPI.getConfigurationOptions()).path(configDir.resolve("Config.conf")).build();
		configLoaderBlackLists = HoconConfigurationLoader.builder().defaultOptions(localeAPI.getConfigurationOptions()).path(configDir.resolve("AuctionBlackList.conf")).build();
		loadConfigs();
		generateConfig = new GenerateConfig(instance);
	}

	@Listener
	public void onEnable(StartedEngineEvent<Server> event) throws IOException {
		if(generateConfig == null) return;
		generateConfig.generateBlackList();
		if(Sponge.server().serviceProvider().economyService().isPresent()) {
			economyService  = Sponge.server().serviceProvider().economyService().get();
		} else {
			logger.error(locales.getComponent(Sponge.server().locale(), "Messages", "EconomyNotFound"));
			if(Sponge.server().commandManager().commandMapping("guishopmanager").isPresent()) Sponge.server().commandManager().commandMapping("guishopmanager").get().allAliases().clear();
			return;
		}
		loadExpires();
		fillItems = new GeneratedFillItems(instance);
		economy = new Economy(instance);
		setWorkDataClasses();
		shopMenus = new ShopMenus(instance);
		auctionMenus = new AuctionMenus(instance);
		workShopData.loadShops();
		updateAuctionData();
	}

	@Listener
	public void onReload(RefreshGameEvent event) {
		reload();
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
		if(rootNode.node("SplitStorage", "Auction").getBoolean() || rootNode.node("SplitStorage", "Shops").getBoolean()) {
			if(rootNode.node("SplitStorage", "Auction").getBoolean()) {
				createMySQLConnect();
				workAuctionData = new WorkTables(instance);
			} else {
				workAuctionData = new WorkConfigs(instance);
			}
			if(rootNode.node("SplitStorage", "Shops").getBoolean()) {
				createMySQLConnect();
				workShopData = new WorkTables(instance);
			} else {
				File shopsDir = Paths.get(configDir + File.separator + rootNode.node("StorageFolder").getString()).toFile();
				if(!shopsDir.exists() || !shopsDir.isDirectory()) {
					shopsDir.mkdir();
				}
				workShopData = new WorkConfigs(instance);
			}
		} else {
			if(!rootNode.node("MySQLStorage").getBoolean()) {
				File shopsDir = Paths.get(configDir + File.separator + rootNode.node("StorageFolder").getString()).toFile();
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
		mySQL = new MySQL(
				instance,
				rootNode.node("MySQL", "Host").getString(),
				rootNode.node("MySQL", "Port").getString(),
				rootNode.node("MySQL", "DataBase").getString(),
				rootNode.node("MySQL", "User").getString(),
				rootNode.node("MySQL", "Password").getString(),
				rootNode.node("MySQL", "SSL").getString());
	}

	private void loadExpires() {
		for(int i = 1 ;  ; i++) {
			if(rootNode.node("Auction", "Expire", String.valueOf(i)).virtual()) {
				break;
			}
			expires.add(new Expire(rootNode.node("Auction", "Expire", String.valueOf(i), "Time").getInt(), rootNode.node("Auction", "Expire", String.valueOf(i), "Tax", "Size").getDouble(), rootNode.node("Auction", "Expire", String.valueOf(i), "Fee", "Size").getDouble(), rootNode.node("Auction", "Expire", String.valueOf(i), "Tax", "Enable").getBoolean(), rootNode.node("Auction", "Expire", String.valueOf(i), "Fee", "Enable").getBoolean()));
			
		}
	}

	private void updateAuctionData() {
		if(rootNode.node("Auction", "Enable").getBoolean()) {
			Task updateAuctionTask = Task.builder().interval(50, TimeUnit.SECONDS).execute(() -> {
				if(rootNode.node("MySQLStorage").getBoolean() || rootNode.node("SplitStorage", "Auction").getBoolean()) {
					workAuctionData.loadAuction();
				}
				Sponge.game().asyncScheduler().submit(Task.builder().delay(10, TimeUnit.SECONDS).execute(() -> {
					if(!auctionItems.isEmpty()) {
						Map<UUID, SerializedAuctionStack> items = new HashMap<UUID, SerializedAuctionStack>();
						items.putAll(auctionItems);
						for(SerializedAuctionStack auctionItem : items.values()) {
							if(auctionItem.isExpired()) {
								auctionItems.remove(auctionItem.getStackUUID());
								workAuctionData.removeAuctionStack(auctionItem.getStackUUID());
								if(!auctionItem.betIsNull() && auctionItem.getBetData().getServer().equals(rootNode.node("Auction", "Server").getString()) && 
										economy.checkPlayerBalance(auctionItem.getBetData().getBuyerUUID(), auctionItem.getBetData().getCurrency(), auctionItem.getBetData().getMoney().multiply(BigDecimal.valueOf(auctionItem.getSerializedItemStack().getQuantity())))) {
									UUID uuid = auctionItem.getBetData().getBuyerUUID();
									economy.auctionTransaction(uuid, auctionItem, 0, true);
									auctionItem.setOwner(uuid, auctionItem.getBetData().getBuyerName());
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
				}).plugin(container).build());
				if(Sponge.server().onlinePlayers().size() != 0) {
					if(!expiredAuctionItems.isEmpty()) {
						Sponge.server().onlinePlayers().forEach(this::checkExpired);
					}
					if(!expiredBetAuctionItems.isEmpty()) {
						Sponge.server().onlinePlayers().forEach(this::checkExpiredBet);
					}
				}
			}).plugin(container).build();
			this.updateAuctionTask = Sponge.game().asyncScheduler().submit(updateAuctionTask);
		} else {
			if(updateAuctionTask != null) {
				updateAuctionTask.cancel();
				Sponge.game().asyncScheduler().tasks(container).remove(updateAuctionTask);
				updateAuctionTask = null;
			}
		}
	}

	private void checkExpired(Player player) {
		if(expiredAuctionItems.containsKey(player.uniqueId())) {
			UUID uuid = player.uniqueId();
			boolean sendMessage = false;
			for(SerializedAuctionStack auctionStack : expiredAuctionItems.get(uuid)) {
				if(auctionStack.getServerName().equals(rootNode.node("Auction", "Server").getString())) {
					sendMessage = true;
					break;
				}
			}
			if(sendMessage) {
				player.sendMessage(locales.getComponent(player.locale(), "Messages", "AuctionExpired")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(expiredAuctionItems.containsKey(uuid)) {
								if(!expiredAuctionItems.get(uuid).isEmpty()) {
									int emptySlots = player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).freeCapacity();
									List<SerializedAuctionStack> toRemove = new ArrayList<SerializedAuctionStack>();
									toRemove.addAll(expiredAuctionItems.get(uuid));
									for(SerializedAuctionStack auctionItem : toRemove) {
										if(emptySlots <= 0) {
											player.sendMessage(getLocales().getComponent(player.locale(), "Messages", "NoEmptySlots").replaceText(TextReplacementConfig.builder().match("%value%").replacement(Component.text(expiredAuctionItems.get(uuid).size())).build()));
											return;
										}
										if(auctionItem.getServerName().equals(rootNode.node("Auction", "Server").getString())) {
											emptySlots--;
											player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).offer(auctionItem.getSerializedItemStack().getItemStack());
											expiredAuctionItems.get(uuid).remove(auctionItem);
											workAuctionData.removeExpireAuctionData(auctionItem);
										}
									}
								}
							}
						})));
			}
		}
	}

	private void checkExpiredBet(Player player) {
		if(expiredBetAuctionItems.containsKey(player.uniqueId())) {
			UUID uuid = player.uniqueId();
			boolean sendMessage = false;
			for(SerializedAuctionStack auctionStack : expiredBetAuctionItems.get(uuid)) {
				if(auctionStack.getBetData().getServer().equals(rootNode.node("Auction", "Server").getString())) {
					sendMessage = true;
					break;
				}
			}
			if(sendMessage) {
				player.sendMessage(locales.getComponent(player.locale(), "Messages", "AuctionBetExpired")
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(expiredBetAuctionItems.containsKey(uuid)) {
								if(!expiredBetAuctionItems.get(uuid).isEmpty()) {
									int emptySlots = player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).freeCapacity();
									List<SerializedAuctionStack> toRemove = new ArrayList<SerializedAuctionStack>();
									toRemove.addAll(expiredBetAuctionItems.get(uuid));
									for(SerializedAuctionStack auctionItem : toRemove) {
										if(emptySlots <= 0) {
											player.sendMessage(getLocales().getComponent(player.locale(), "Messages", "NoEmptySlots").replaceText(TextReplacementConfig.builder().match("%value%").replacement(Component.text(expiredAuctionItems.get(uuid).size())).build()));
											return;
										}
										if(auctionItem.getBetData().getServer().equals(rootNode.node("Auction", "Server").getString())) {
											emptySlots--;
											player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).offer(auctionItem.getSerializedItemStack().getItemStack());
											expiredBetAuctionItems.get(uuid).remove(auctionItem);
											workAuctionData.removeExpireBetAuctionData(auctionItem);
										}
									}
								}
							}
						})));
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
	public void commandRegister(RegisterCommandEvent<Command.Parameterized> event) {
		
		Command.Parameterized commandAuctionAdd = Command.builder()
				.shortDescription(Component.text("Add item to auction"))
				.permission(Permissions.AUCTION_ADD_ITEM)
				.addParameters(CommandParameters.AUCTION_BET, CommandParameters.AUCTION_PRICE, CommandParameters.CURRENCY)
				.executor(new AuctionAddItem(instance))
				.build();
		
		Command.Parameterized commandAuctionItemBlocking = Command.builder()
				.shortDescription(Component.text("Block item for sale in auction."))
				.permission(Permissions.AUCTION_BLOCK_ITEM)
				.addFlags(CommandParameters.MASK, CommandParameters.ITEM)
				.executor(new AddBlackList(instance))
				.build();
		
		Command.Parameterized commandAuction = Command.builder()
				.shortDescription(Component.text("Open auction"))
				.permission(Permissions.AUCTION_OPEN_SELF)
				.addParameters(CommandParameters.PLAYER)
				.executor(new AuctionOpen(instance))
				.addChild(commandAuctionAdd, "add", "additem")
				.addChild(commandAuctionItemBlocking, "blacklist", "block")
				.build();
		
		Command.Parameterized commandShopCreate = Command.builder()
				.shortDescription(Component.text("Create a shop"))
				.permission(Permissions.SHOP_CREATE)
				.addParameter(CommandParameters.SHOP_ID)
				.executor(new ShopCreate(instance))
				.build();
		
		Command.Parameterized commandShopDelete = Command.builder()
				.shortDescription(Component.text("Delete a shop"))
				.permission(Permissions.SHOP_DELETE)
				.addParameter(CommandParameters.SHOP_ID)
				.executor(new ShopDelete(instance))
				.build();
		
		Command.Parameterized commandShopEdit = Command.builder()
				.shortDescription(Component.text("Edit a shop"))
				.permission(Permissions.SHOP_EDIT)
				.addParameter(CommandParameters.SHOP_ID)
				.executor(new ShopEdit(instance))
				.build();
		
		Command.Parameterized commandShopTranslate = Command.builder()
				.shortDescription(Component.text("Add a translatable name for shop"))
				.permission(Permissions.SHOP_TRANSLATE)
				.addParameters(CommandParameters.SHOP_ID, CommandParameters.LOCALE, CommandParameters.TRANSLATE)
				.executor(new ShopTranslate(instance))
				.build();
		
		Command.Parameterized commandShopSetItem = Command.builder()
				.shortDescription(Component.text("Add or replace an item in the shop"))
				.permission(Permissions.SHOP_EDIT)
				.addParameters(CommandParameters.SHOP_ID, CommandParameters.SHOP_MENU_NUMBER, CommandParameters.SLOT, CommandParameters.SHOP_BUY_PRICE, CommandParameters.SHOP_SELL_PRICE, CommandParameters.CURRENCY)
				.executor(new ShopSetItem(instance))
				.build();
		
		Command.Parameterized commandShopOpen = Command.builder()
				.shortDescription(Component.text("Open a shop"))
				.permission(Permissions.SHOP_OPEN_SELF)
				.addParameters(CommandParameters.SHOP_ID, CommandParameters.PLAYER)
				.executor(new ShopOpen(instance))
				.build();
		
		Command.Parameterized commandReload = Command.builder()
				.shortDescription(Component.text("Reload plugin"))
				.permission(Permissions.RELOAD)
				.executor(new CommandExecutor() {
					@Override
					public CommandResult execute(CommandContext context) throws CommandException {
						if(!context.associatedObject().isPresent()) return CommandResult.success();
						reload();
						((Audience) context.associatedObject().get()).sendMessage(getLocales().getComponent(((LocaleSource) context.associatedObject().get()).locale(), "Messages", "Reload"));
						return CommandResult.success();
					}
				})
				.build();
		
		Command.Parameterized mainCommand;
		if(rootNode.node("Auction", "Enable").getBoolean()) {
			mainCommand = Command.builder()
					.shortDescription(Component.text("Reload plugin"))
					.permission(Permissions.HELP)
					.addChild(commandAuction, "auction", "market")
					.addChild(commandShopCreate, "create")
					.addChild(commandShopDelete, "delete")
					.addChild(commandShopEdit, "edit")
					.addChild(commandShopSetItem, "setitem", "add", "set")
					.addChild(commandShopTranslate, "translate")
					.addChild(commandShopOpen, "open")
					.addChild(commandReload, "reload")
					.executor(new MainCommand(instance))
					.build();
		} else {
			mainCommand = Command.builder()
					.shortDescription(Component.text("Reload plugin"))
					.permission(Permissions.HELP)
					.addChild(commandShopCreate, "create")
					.addChild(commandShopDelete, "delete")
					.addChild(commandShopEdit, "edit")
					.addChild(commandShopSetItem, "setitem", "add", "set")
					.addChild(commandShopTranslate, "translate")
					.addChild(commandShopOpen, "open")
					.addChild(commandReload, "reload")
					.executor(new MainCommand(instance))
					.build();
		}
		
		event.register(container, mainCommand, "guishopmanager", "gsm");
		
		if(rootNode.node("Auction", "Enable").getBoolean() && rootNode.node("Aliases", "Auction", "Enable").getBoolean()) {
			try {
				List<String> aliasesList = rootNode.node("Aliases", "Auction", "List").getList(String.class);
				if(!aliasesList.isEmpty()) {
					String first = aliasesList.remove(0);
					if(aliasesList.isEmpty()) {
						event.register(container, commandAuction, first);
					} else {
						String[] aliases = aliasesList.toArray(new String[0]);
						event.register(container, commandAuction, first, aliases);
					}
				}
			} catch (SerializationException e) {
				logger.error(e.getLocalizedMessage());
			}
		}
		if(rootNode.node("Aliases", "ShopOpen", "Enable").getBoolean()) {
			try {
				List<String> aliasesList = rootNode.node("Aliases", "ShopOpen", "List").getList(String.class);
				if(!aliasesList.isEmpty()) {
					String first = aliasesList.remove(0);
					if(aliasesList.isEmpty()) {
						event.register(container, commandShopOpen, first);
					} else {
						String[] aliases = aliasesList.toArray(new String[0]);
						event.register(container, commandShopOpen, first, aliases);
					}
				}
			} catch (SerializationException e) {
				logger.error(e.getLocalizedMessage());
			}
		}
	}

}