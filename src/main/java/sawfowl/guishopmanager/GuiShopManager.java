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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.RegisterCommandEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PrimaryPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.util.locale.LocaleSource;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import com.google.inject.Inject;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;

import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.guishopmanager.utils.Economy;
import sawfowl.guishopmanager.utils.Locales;
import sawfowl.guishopmanager.utils.MySQL;
import sawfowl.commandpack.utils.StorageType;
import sawfowl.guishopmanager.commands.MainCommand;
import sawfowl.guishopmanager.commands.auction.AddBlackList;
import sawfowl.guishopmanager.commands.auction.AddCommand;
import sawfowl.guishopmanager.commands.auction.AddItem;
import sawfowl.guishopmanager.commands.auction.Open;
import sawfowl.guishopmanager.commands.commandshop.CommandsShopCreate;
import sawfowl.guishopmanager.commands.commandshop.CommandsShopDelete;
import sawfowl.guishopmanager.commands.commandshop.CommandsShopEdit;
import sawfowl.guishopmanager.commands.commandshop.CommandsShopOpen;
import sawfowl.guishopmanager.commands.commandshop.CommandsShopTranslate;
import sawfowl.guishopmanager.commands.shop.ShopCreate;
import sawfowl.guishopmanager.commands.shop.ShopDelete;
import sawfowl.guishopmanager.commands.shop.ShopEdit;
import sawfowl.guishopmanager.commands.shop.ShopOpen;
import sawfowl.guishopmanager.commands.shop.ShopSetItem;
import sawfowl.guishopmanager.commands.shop.ShopTranslate;
import sawfowl.guishopmanager.configure.Expire;
import sawfowl.guishopmanager.configure.GenerateConfig;
import sawfowl.guishopmanager.configure.GenerateLocales;
import sawfowl.guishopmanager.configure.GeneratedFillItems;
import sawfowl.guishopmanager.data.commandshop.CommandShopData;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.gui.AuctionMenus;
import sawfowl.guishopmanager.gui.CommandShopMenus;
import sawfowl.guishopmanager.gui.ShopMenus;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.storage.ConfigStorage;
import sawfowl.guishopmanager.storage.DataStorage;
import sawfowl.guishopmanager.storage.H2Storage;
import sawfowl.guishopmanager.storage.MySqlStorage;
import sawfowl.localeapi.api.LocaleService;
import sawfowl.localeapi.api.event.LocaleServiseEvent;
import sawfowl.localeapi.api.serializetools.SerializeOptions;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStackJsonNbt;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStackPlainNBT;

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

	private static GuiShopManager instance;
	private GeneratedFillItems fillItems;
	private ShopMenus shopMenus;
	private CommandShopMenus commandShopMenus;
	private AuctionMenus auctionMenus;
	private DataStorage shopStorage;
	private DataStorage commandsShopStorage;
	private DataStorage auctionStorage;
	private MySQL mySQL;
	private Economy economy;
	private Locales locales;
	private GenerateConfig generateConfig;

	private Map<String, Shop> shops = new HashMap<String, Shop>();
	private Map<String, CommandShopData> commandShops = new HashMap<String, CommandShopData>();
	private LinkedHashMap<UUID, SerializedAuctionStack> auctionItems = new LinkedHashMap<UUID, SerializedAuctionStack>();
	private Map<UUID, Set<SerializedAuctionStack>> expiredAuctionItems = new HashMap<UUID, Set<SerializedAuctionStack>>();
	private Map<UUID, Set<SerializedAuctionStack>> expiredBetAuctionItems = new HashMap<UUID, Set<SerializedAuctionStack>>();
	private List<Expire> expires = new ArrayList<Expire>();
	private List<String> blackListMasks = new ArrayList<String>();
	private List<SerializedItemStackPlainNBT> blackListStacks = new ArrayList<SerializedItemStackPlainNBT>();

	//private ScheduledTask updateAuctionTask;

	@Inject
	public GuiShopManager(PluginContainer pluginContainer, @ConfigDir(sharedRoot = false) Path configDirectory) {
		configDir = configDirectory;
		configFile = configDirectory.toFile();
		container = pluginContainer;
	}

	public Logger getLogger() {
		return logger;
	}
	public static GuiShopManager getInstance() {
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
	public CommandShopMenus getCommandShopMenu() {
		return commandShopMenus;
	}

	public AuctionMenus getAuctionMenus() {
		return auctionMenus;
	}
	public DataStorage getShopStorage() {
		return shopStorage;
	}
	public DataStorage getCommandsShopStorage() {
		return commandsShopStorage;
	}
	public DataStorage getAuctionStorage() {
		return auctionStorage;
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
		shopStorage.saveShop(id);
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
		shopStorage.deleteShop(shopId);
		shops.remove(shopId);
	}
	public void addCommandShopData(String id, CommandShopData shop) {
		commandShops.put(id, shop);
		commandsShopStorage.saveCommandsShop(id);
	}
	public CommandShopData getCommandShopData(String id) {
		return commandShops.get(id);
	}
	public List<CommandShopData> getAllCommandShops() {
		return commandShops.values().stream().collect(Collectors.toList());
	}
	public boolean commandShopsEmpty() {
		return commandShops.isEmpty();
	}
	public boolean commandShopExists(String id) {
		return commandShops.containsKey(id);
	}
	public void removeCommandShopData(String shopId) {
		commandsShopStorage.deleteCommandsShop(shopId);
		commandShops.remove(shopId);
	}
	public Map<UUID, SerializedAuctionStack> getAuctionItems() {
		return auctionItems;
	}
	public void updateAuctionItems(Map<UUID, SerializedAuctionStack> items) {
		auctionItems.clear();
		auctionItems.putAll(items);
	}
	public Map<UUID, Set<SerializedAuctionStack>> getExpiredAuctionItems() {
		return expiredAuctionItems;
	}
	public Map<UUID, Set<SerializedAuctionStack>> getExpiredBetAuctionItems() {
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
		blackListStacks.add(new SerializedItemStackPlainNBT(itemStack));
		try {
			blackListNode.node("StacksList").setList(SerializedItemStackJsonNbt.class, blackListStacks.stream().map(s -> s.toSerializedItemStackJsonNbt()).toList());
		} catch (SerializationException e) {
			logger.error(e.getLocalizedMessage());
		}
	}
	public void setBlackListStacks(List<SerializedItemStackPlainNBT> blackListStacks) {
		this.blackListStacks = blackListStacks;
	}
	public boolean maskIsBlackList(String check) {
		return blackListMasks.toString().contains(check) || blackListMasks.toString().contains(check.split(":")[1]);
	}
	public boolean itemIsBlackList(ItemStack check) {
		SerializedItemStackPlainNBT serializedItemStack = new SerializedItemStackPlainNBT(check);
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
		configLoader = SerializeOptions.createHoconConfigurationLoader(2).path(configDir.resolve("Config.conf")).build();
		configLoaderBlackLists = SerializeOptions.createHoconConfigurationLoader(2).path(configDir.resolve("AuctionBlackList.conf")).build();
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
		commandShopMenus = new CommandShopMenus(instance);
		auctionMenus = new AuctionMenus(instance);
		shopStorage.loadShops();
		commandsShopStorage.loadCommandsShops();
		auctionStorage.loadAuction();
		Sponge.asyncScheduler().submit(Task.builder().plugin(container).interval(30, TimeUnit.SECONDS).execute(() -> {
			updateAuctionData();
			if(Sponge.server().onlinePlayers().size() == 0) return;
			if(!expiredAuctionItems.isEmpty()) Sponge.server().onlinePlayers().forEach(this::checkExpired);
			if(!expiredBetAuctionItems.isEmpty()) Sponge.server().onlinePlayers().forEach(this::checkExpiredBet);
		}).build());
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
		shopStorage.loadShops();
		commandsShopStorage.loadCommandsShops();
		if(rootNode.node("Auction", "Enable").getBoolean()) auctionStorage.loadAuction();
		//updateAuctionData();
	}

	private void setWorkDataClasses() {
		shopStorage = commandsShopStorage = auctionStorage = null;
		if(mySQL != null) {
			mySQL = null;
		}
		if(rootNode.node("SplitStorage", "Enable").getBoolean()) {
			if(rootNode.node("MySQL", "Enable").getBoolean()) {
				createMySQLConnect();
				switch(StorageType.getType(rootNode.node("SplitStorage", "Auction").getString())) {
					case H2:
						auctionStorage = new H2Storage(instance);
						if(StorageType.getType(rootNode.node("SplitStorage", "Shops").getString()) == StorageType.H2) {
							shopStorage = auctionStorage;
							if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.H2) {
								commandsShopStorage = shopStorage;
							} else if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.MYSQL) {
								commandsShopStorage = new MySqlStorage(instance);
							} else commandsShopStorage = new ConfigStorage(instance);
						} else if(StorageType.getType(rootNode.node("SplitStorage", "Shops").getString()) == StorageType.MYSQL) {
							shopStorage = new MySqlStorage(instance);
							if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.H2) {
								commandsShopStorage = new H2Storage(instance);
							} else if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.MYSQL) {
								commandsShopStorage = shopStorage;
							} else commandsShopStorage = new ConfigStorage(instance);
						} else {
							shopStorage = new ConfigStorage(instance);
							if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.H2) {
								commandsShopStorage = auctionStorage;
							} else if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.MYSQL) {
								commandsShopStorage = new MySqlStorage(instance);
							} else commandsShopStorage = shopStorage;
						}
						break;
					case MYSQL:
						auctionStorage = new MySqlStorage(instance);
						if(StorageType.getType(rootNode.node("SplitStorage", "Shops").getString()) == StorageType.H2) {
							shopStorage = new H2Storage(instance);
							if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.H2) {
								commandsShopStorage = shopStorage;
							} else if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.MYSQL) {
								commandsShopStorage = auctionStorage;
							} else commandsShopStorage = new ConfigStorage(instance);
						} else if(StorageType.getType(rootNode.node("SplitStorage", "Shops").getString()) == StorageType.MYSQL) {
							shopStorage = new MySqlStorage(instance);
							if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.H2) {
								commandsShopStorage = new H2Storage(instance);
							} else if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.MYSQL) {
								commandsShopStorage = shopStorage;
							} else commandsShopStorage = new ConfigStorage(instance);
						} else {
							shopStorage = new ConfigStorage(instance);
							if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.H2) {
								commandsShopStorage = new H2Storage(instance);
							} else if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.MYSQL) {
								commandsShopStorage = auctionStorage;
							} else commandsShopStorage = shopStorage;
						}
						break;
					default:
						auctionStorage = new ConfigStorage(instance);
						if(StorageType.getType(rootNode.node("SplitStorage", "Shops").getString()) == StorageType.H2) {
							shopStorage = new H2Storage(instance);
							if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.H2) {
								commandsShopStorage = shopStorage;
							} else if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.MYSQL) {
								commandsShopStorage = new MySqlStorage(instance);
							} else commandsShopStorage = auctionStorage;
						} else if(StorageType.getType(rootNode.node("SplitStorage", "Shops").getString()) == StorageType.MYSQL) {
							shopStorage = new MySqlStorage(instance);
							if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.H2) {
								commandsShopStorage = new H2Storage(instance);
							} else if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.MYSQL) {
								commandsShopStorage = shopStorage;
							} else commandsShopStorage = auctionStorage;
						} else {
							shopStorage = auctionStorage;
							if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.H2) {
								commandsShopStorage = new H2Storage(instance);
							} else if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.MYSQL) {
								commandsShopStorage = new MySqlStorage(instance);
							} else commandsShopStorage = auctionStorage;
						}
						break;
				}
			} else switch(StorageType.getType(rootNode.node("SplitStorage", "Auction").getString())) {
				case H2:
					auctionStorage = new H2Storage(instance);
					if(StorageType.getType(rootNode.node("SplitStorage", "Shops").getString()) == StorageType.H2) {
						shopStorage = auctionStorage;
						if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.H2) {
							commandsShopStorage = shopStorage;
						} else commandsShopStorage = new ConfigStorage(instance);
					} else {
						shopStorage = new ConfigStorage(instance);
						if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.H2) {
							commandsShopStorage = auctionStorage;
						} else commandsShopStorage = shopStorage;
					}
					break;
				default:
					auctionStorage = new ConfigStorage(instance);
					if(StorageType.getType(rootNode.node("SplitStorage", "Shops").getString()) == StorageType.H2) {
						shopStorage = new H2Storage(instance);
						if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.H2) {
							commandsShopStorage = shopStorage;
						} else commandsShopStorage = auctionStorage;
					} else {
						shopStorage = auctionStorage;
						if(StorageType.getType(rootNode.node("SplitStorage", "CommandsShops").getString()) == StorageType.H2) {
							commandsShopStorage = new H2Storage(instance);
						} else commandsShopStorage = shopStorage;
					}
					break;
			}
		} else if(rootNode.node("MySQL", "Enable").getBoolean()) {
			createMySQLConnect();
			shopStorage = commandsShopStorage = auctionStorage = new MySqlStorage(instance);
		} else shopStorage = commandsShopStorage = auctionStorage = new ConfigStorage(instance);
		if(rootNode.node("MySQL", "Enable").getBoolean()) {
			createMySQLConnect();
		}
		if(shopStorage instanceof ConfigStorage) {
			File folder = configDir.resolve(rootNode.node("StorageFolders", "Shops").getString()).toFile();
			if(!folder.exists() || !folder.isDirectory()) folder.mkdir();
		}
		if(commandsShopStorage instanceof ConfigStorage) {
			File folder = configDir.resolve(rootNode.node("StorageFolders", "CommandsShops").getString()).toFile();
			if(!folder.exists() || !folder.isDirectory()) folder.mkdir();
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
		if(mySQL.getOrOpenConnection() == null) mySQL = null;
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
			if(!auctionItems.isEmpty()) {
				Map<UUID, SerializedAuctionStack> items = new HashMap<UUID, SerializedAuctionStack>();
				items.putAll(auctionItems);
				for(SerializedAuctionStack auctionItem : items.values()) {
					if(auctionItem.isExpired()) {
						auctionItems.remove(auctionItem.getStackUUID());
						auctionStorage.removeAuctionStack(auctionItem.getStackUUID());
						if(!auctionItem.betIsNull() && auctionItem.getBetData().getServer().equals(rootNode.node("Auction", "Server").getString()) && economy.checkPlayerBalance(auctionItem.getBetData().getBuyerUUID(), auctionItem.getBetData().getCurrency(), auctionItem.getBetData().getMoney().multiply(BigDecimal.valueOf(auctionItem.getSerializedItemStack().getQuantity())))) {
							UUID uuid = auctionItem.getBetData().getBuyerUUID();
							economy.auctionTransaction(uuid, auctionItem, 0, true);
							auctionItem.setOwner(uuid, auctionItem.getBetData().getBuyerName());
							if(!expiredBetAuctionItems.containsKey(uuid)) {
								Set<SerializedAuctionStack> newAdded = new HashSet<SerializedAuctionStack>();
								newAdded.add(auctionItem);
								expiredBetAuctionItems.put(uuid, newAdded);
								auctionStorage.saveExpireBetAuctionData(auctionItem);
							} else {
								boolean add = true;
								for(SerializedAuctionStack stack : expiredBetAuctionItems.get(uuid)) {
									if(stack.getStackUUID().equals(auctionItem.getStackUUID())) {
										add = false;
									}
								}
								if(add) {
									expiredBetAuctionItems.get(uuid).add(auctionItem);
									auctionStorage.saveExpireBetAuctionData(auctionItem);
								}
							}
						} else {
							UUID uuid = auctionItem.getOwnerUUID();
							if(!expiredAuctionItems.containsKey(uuid)) {
								Set<SerializedAuctionStack> newAdded = new HashSet<SerializedAuctionStack>();
								newAdded.add(auctionItem);
								expiredAuctionItems.put(uuid, newAdded);
								auctionStorage.saveExpireAuctionData(auctionItem);
							} else {
								boolean add = true;
								for(SerializedAuctionStack stack : expiredAuctionItems.get(uuid)) {
									if(stack.getStackUUID().equals(auctionItem.getStackUUID())) {
										add = false;
									}
								}
								if(add) {
									expiredAuctionItems.get(uuid).add(auctionItem);
									auctionStorage.saveExpireAuctionData(auctionItem);
								}
							}
						}
					}
				}
				items.clear();
				items = null;
			}
		
		}
	}


	private void checkExpired(ServerPlayer player) {
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
											auctionStorage.removeExpireAuctionData(auctionItem);
										}
									}
								}
							}
						})));
			}
		}
	}

	private void checkExpiredBet(ServerPlayer player) {
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
											auctionStorage.removeExpireBetAuctionData(auctionItem);
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
				.executor(new AddItem(instance))
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
				.executor(new Open(instance))
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
		
		Command.Parameterized commandShop = Command.builder()
				.shortDescription(Component.text("Open a shop"))
				.permission(Permissions.HELP)
				.executor(new ShopOpen(instance))
				.addChild(commandShopCreate, "create")
				.addChild(commandShopDelete, "delete")
				.addChild(commandShopEdit, "edit")
				.addChild(commandShopSetItem, "setitem", "add", "set")
				.addChild(commandShopTranslate, "translate")
				.addChild(commandShopOpen, "open")
				.build();
		
		Command.Parameterized commandAddCommand = Command.builder()
				.shortDescription(Component.text("Add command to item"))
				.permission(Permissions.COMMANDSSHOP_ADD_COMMAND)
				.addParameters(CommandParameters.COMMAND)
				.executor(new AddCommand(instance))
				.build();
		
		Command.Parameterized commandCreateCommandsShop = Command.builder()
				.shortDescription(Component.text("Add command to item"))
				.permission(Permissions.COMMANDSSHOP_CREATE)
				.addParameter(CommandParameters.SHOP_ID)
				.executor(new CommandsShopCreate(instance))
				.build();
		
		Command.Parameterized commandEditCommandsShop = Command.builder()
				.shortDescription(Component.text("Add command to item"))
				.permission(Permissions.COMMANDSSHOP_EDIT)
				.addParameter(CommandParameters.SHOP_ID)
				.executor(new CommandsShopEdit(instance))
				.build();
		
		Command.Parameterized commandDeleteCommandsShop = Command.builder()
				.shortDescription(Component.text("Add command to item"))
				.permission(Permissions.COMMANDSSHOP_DELETE)
				.addParameter(CommandParameters.SHOP_ID)
				.executor(new CommandsShopDelete(instance))
				.build();
		
		Command.Parameterized commandTranslateCommandsShop = Command.builder()
				.shortDescription(Component.text("Add command to item"))
				.permission(Permissions.COMMANDSSHOP_CREATE)
				.addParameters(CommandParameters.SHOP_ID, CommandParameters.LOCALE, CommandParameters.TRANSLATE)
				.executor(new CommandsShopTranslate(instance))
				.build();
		
		Command.Parameterized commandOpenCommandsShop = Command.builder()
				.shortDescription(Component.text("Add command to item"))
				.permission(Permissions.COMMANDSSHOP_OPEN_SELF)
				.addParameters(CommandParameters.SHOP_ID, CommandParameters.PLAYER)
				.executor(new CommandsShopOpen(instance))
				.build();
		
		Command.Parameterized commandsShop = Command.builder()
				.shortDescription(Component.text("Commands shop"))
				.permission(Permissions.COMMANDSSHOP_OPEN_SELF)
				.addChild(commandAddCommand, "addcommand")
				.addChild(commandCreateCommandsShop, "create")
				.addChild(commandEditCommandsShop, "edit")
				.addChild(commandDeleteCommandsShop, "delete")
				.addChild(commandTranslateCommandsShop, "translate")
				.addChild(commandOpenCommandsShop, "open")
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
		
		Command.Parameterized mainCommand = rootNode.node("Auction", "Enable").getBoolean()
				? 
				Command.builder()
					.shortDescription(Component.text("Reload plugin"))
					.permission(Permissions.HELP)
					.addChild(commandAuction, "auction", "market")
					.addChild(commandShop, "shop")
					.addChild(commandsShop, "commandsshop", "cshop")
					.addChild(commandReload, "reload")
					.executor(new MainCommand(instance))
					.build() 
				:
				Command.builder()
					.shortDescription(Component.text("Reload plugin"))
					.permission(Permissions.HELP)
					.addChild(commandShop, "shop")
					.addChild(commandsShop, "commandsshop", "cshop")
					.addChild(commandReload, "reload")
					.executor(new MainCommand(instance))
					.build();
		
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