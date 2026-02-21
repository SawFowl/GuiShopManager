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
import java.math.BigDecimal;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
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
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.EventContext;
import org.spongepowered.api.event.EventContextKeys;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.lifecycle.RefreshGameEvent;
import org.spongepowered.api.event.lifecycle.StartedEngineEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.entity.PrimaryPlayerInventory;
import org.spongepowered.api.item.inventory.query.QueryTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.economy.EconomyService;
import org.spongepowered.api.util.locale.Locales;
import org.spongepowered.plugin.PluginContainer;
import org.spongepowered.plugin.builtin.jvm.Plugin;

import com.google.inject.Inject;

import sawfowl.guishopmanager.utils.Economy;
import sawfowl.guishopmanager.utils.MySQL;
import sawfowl.commandpack.api.CommandPack;
import sawfowl.commandpack.utils.StorageType;
import sawfowl.guishopmanager.commands.MainCommand;
import sawfowl.guishopmanager.commands.auction.Auction;
import sawfowl.guishopmanager.configure.Expire;
import sawfowl.guishopmanager.configure.FillItems;
import sawfowl.guishopmanager.configure.config.BlackList;
import sawfowl.guishopmanager.configure.config.Config;
import sawfowl.guishopmanager.configure.locale.abstractlocale.AbstractLocale;
import sawfowl.guishopmanager.configure.locale.def.ImplementPluginLocale;
import sawfowl.guishopmanager.configure.locale.ru.ImplementRuPluginLocale;
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
import sawfowl.localeapi.api.ConfigTypes;
import sawfowl.localeapi.api.LocaleService;
import sawfowl.localeapi.api.LocalesList;
import sawfowl.localeapi.api.config.ReferencedConfig;
import sawfowl.localeapi.api.serializetools.ItemStackSerializerType;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStack;

@Plugin("guishopmanager")
public class GuiShopManager {

	private Path configDir;
	private File configFile;
	private ReferencedConfig<Config> config;
	private ReferencedConfig<BlackList> configBlackLists;
	private ReferencedConfig<FillItems> fillItemsConfig;
	private Logger logger;
	private static PluginContainer container;
	private static EconomyService economyService;
	private static EventContext eventContext;
	private LocaleService localeAPI;
	private CommandPack commandPack;

	private static GuiShopManager instance;
	private ShopMenus shopMenus;
	private CommandShopMenus commandShopMenus;
	private AuctionMenus auctionMenus;
	private DataStorage shopStorage;
	private DataStorage commandsShopStorage;
	private DataStorage auctionStorage;
	private MySQL mySQL;
	private Economy economy;
	private LocalesList<AbstractLocale> locales;

	private Map<String, Shop> shops = new HashMap<String, Shop>();
	private Map<String, CommandShopData> commandShops = new HashMap<String, CommandShopData>();
	private LinkedHashMap<UUID, SerializedAuctionStack> auctionItems = new LinkedHashMap<UUID, SerializedAuctionStack>();
	private Map<UUID, Set<SerializedAuctionStack>> expiredAuctionItems = new HashMap<UUID, Set<SerializedAuctionStack>>();
	private Map<UUID, Set<SerializedAuctionStack>> expiredBetAuctionItems = new HashMap<UUID, Set<SerializedAuctionStack>>();
	private List<Expire> expires = new ArrayList<Expire>();
	private List<SerializedItemStack> blackListStacks = new ArrayList<SerializedItemStack>();

	//private ScheduledTask updateAuctionTask;

	@Inject
	public GuiShopManager(PluginContainer pluginContainer, @ConfigDir(sharedRoot = false) Path configDirectory) {
		instance = this;
		configDir = configDirectory;
		configFile = configDirectory.toFile();
		container = pluginContainer;
		logger = LogManager.getLogger("GuiShopManager");
		eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, container).build();
		localeAPI = LocaleService.getInstance();
		locales = localeAPI.createLocales(pluginContainer, ImplementPluginLocale.class);
		if(!locales.contains(Locales.DEFAULT)) locales.createReferencedTranslation(ConfigTypes.HOCON, Locales.DEFAULT, ImplementPluginLocale.class);
		if(!locales.contains(Locales.RU_RU)) locales.createReferencedTranslation(ConfigTypes.HOCON, Locales.RU_RU, ImplementRuPluginLocale.class);
		commandPack = CommandPack.getInstance();
		config = ReferencedConfig.create(pluginContainer, configDirectory, "Config", ConfigTypes.HOCON, ItemStackSerializerType.SIMPLE, null, Config.class);
		configBlackLists = ReferencedConfig.create(pluginContainer, configDirectory, "BlackList", ConfigTypes.HOCON, ItemStackSerializerType.SIMPLE, null, BlackList.class);
		loadConfigs();
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
	public Config getConfig() {
		return config.get();
	}
	public BlackList getBlackList() {
		return configBlackLists.get();
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
	public FillItems getFillItems() {
		return fillItemsConfig.get();
	}
	/**public GeneratedFillItems getFillItems() {
		return fillItems;
	}*/
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
	public LocalesList<AbstractLocale> getLocales() {
		return locales;
	}
	public void addShop(String id, Shop shop) {
		shops.put(id, shop);
	}
	public Shop getShop(String id) {
		return shops.get(id);
	}
	public Collection<Shop> getAllShops() {
		return shops.values();
	}
	public boolean shopsEmpty() {
		return shops.isEmpty();
	}
	public boolean shopExists(String id) {
		return shops.containsKey(id);
	}
	public void removeShop(String shopId) {
		shops.remove(shopId);
	}
	public void deleteShop(String shopId) {
		shopStorage.deleteShop(shopId);
		removeShop(shopId);
	}
	public Collection<String> availableShops() {
		return shops.keySet();
	}
	public void addCommandShopData(String id, CommandShopData shop) {
		commandShops.put(id, shop);
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
		commandShops.remove(shopId);
	}
	public void deleteCommandShopData(String shopId) {
		commandsShopStorage.deleteCommandsShop(shopId);
		removeCommandShopData(shopId);
	}
	public Collection<String> availableCommandShops() {
		return commandShops.keySet();
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
		getBlackList().setMasks(blackListMasks);
	}
	public void addBlackListMask(ItemStack itemStack) {
		getBlackList().getMasks().add(itemStack.type().toString().toLowerCase());
	}
	public void addBlackListStack(ItemStack itemStack) {
		blackListStacks.add(new SerializedItemStack(itemStack));
		getBlackList().getItems().add(new SerializedItemStack(itemStack));
	}
	public void setBlackListStacks(List<SerializedItemStack> blackListStacks) {
		this.blackListStacks = blackListStacks;
	}
	public boolean maskIsBlackList(String check) {
		return getBlackList().getMasks().contains(check) || getBlackList().getMasks().contains(check.split(":")[1]);
	}
	public boolean itemIsBlackList(ItemStack check) {
		SerializedItemStack serializedItemStack = new SerializedItemStack(check);
		serializedItemStack.setQuantity(1);
		return blackListStacks.contains(serializedItemStack) || blackListStacks.toString().contains(serializedItemStack.toString());
	}

	@Listener
	public void onStart(StartedEngineEvent<Server> event) {
		if(!Sponge.server().serviceProvider().economyService().isPresent()) {
			logger.error(locales.getSystemAsReferenced().messages().exceptions().economyNotFound());
			return;
		} else economyService  = Sponge.server().serviceProvider().economyService().get();
		loadExpires();
		//fillItems = new GeneratedFillItems(instance);
		fillItemsConfig = ReferencedConfig.create(container, configDir, "FillItems", ConfigTypes.HOCON, ItemStackSerializerType.JSON, null, FillItems.class);
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
		commandPack.registerCommand(new MainCommand(instance));
		if(getConfig().getAliases().getShop().isEnable()) new sawfowl.guishopmanager.commands.shop.Shop(instance).register(commandPack);
		if(getConfig().getAliases().getAuction().isEnable() && getConfig().getAuction().isEnable()) new Auction(instance).register(commandPack);
	}

	@Listener
	public void onReload(RefreshGameEvent event) {
		reload();
	}

	public void reload() {
		loadConfigs();
		setWorkDataClasses();
		//fillItems = null;
		//fillItems = new GeneratedFillItems(instance);
		if(fillItemsConfig != null) fillItemsConfig.load();
		expires.clear();
		loadExpires();
		shops.clear();
		shopStorage.loadShops();
		commandsShopStorage.loadCommandsShops();
		if(getConfig().getAuction().isEnable()) auctionStorage.loadAuction();
	}

	private void setWorkDataClasses() {
		shopStorage = commandsShopStorage = auctionStorage = null;
		if(mySQL != null) {
			mySQL = null;
		}
		if(getConfig().getSplitStorage().isEnable()) {
			if(getConfig().getMySQL().isEnable()) {
				createMySQLConnect();
				switch(StorageType.getType(getConfig().getSplitStorage().getAuction())) {
					case H2:
						auctionStorage = new H2Storage(instance);
						if(StorageType.getType(getConfig().getSplitStorage().getShops()) == StorageType.H2) {
							shopStorage = auctionStorage;
							if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.H2) {
								commandsShopStorage = shopStorage;
							} else if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.MYSQL) {
								commandsShopStorage = new MySqlStorage(instance);
							} else commandsShopStorage = new ConfigStorage(instance);
						} else if(StorageType.getType(getConfig().getSplitStorage().getShops()) == StorageType.MYSQL) {
							shopStorage = new MySqlStorage(instance);
							if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.H2) {
								commandsShopStorage = new H2Storage(instance);
							} else if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.MYSQL) {
								commandsShopStorage = shopStorage;
							} else commandsShopStorage = new ConfigStorage(instance);
						} else {
							shopStorage = new ConfigStorage(instance);
							if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.H2) {
								commandsShopStorage = auctionStorage;
							} else if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.MYSQL) {
								commandsShopStorage = new MySqlStorage(instance);
							} else commandsShopStorage = shopStorage;
						}
						break;
					case MYSQL:
						auctionStorage = new MySqlStorage(instance);
						if(StorageType.getType(getConfig().getSplitStorage().getShops()) == StorageType.H2) {
							shopStorage = new H2Storage(instance);
							if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.H2) {
								commandsShopStorage = shopStorage;
							} else if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.MYSQL) {
								commandsShopStorage = auctionStorage;
							} else commandsShopStorage = new ConfigStorage(instance);
						} else if(StorageType.getType(getConfig().getSplitStorage().getShops()) == StorageType.MYSQL) {
							shopStorage = new MySqlStorage(instance);
							if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.H2) {
								commandsShopStorage = new H2Storage(instance);
							} else if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.MYSQL) {
								commandsShopStorage = shopStorage;
							} else commandsShopStorage = new ConfigStorage(instance);
						} else {
							shopStorage = new ConfigStorage(instance);
							if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.H2) {
								commandsShopStorage = new H2Storage(instance);
							} else if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.MYSQL) {
								commandsShopStorage = auctionStorage;
							} else commandsShopStorage = shopStorage;
						}
						break;
					default:
						auctionStorage = new ConfigStorage(instance);
						if(StorageType.getType(getConfig().getSplitStorage().getShops()) == StorageType.H2) {
							shopStorage = new H2Storage(instance);
							if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.H2) {
								commandsShopStorage = shopStorage;
							} else if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.MYSQL) {
								commandsShopStorage = new MySqlStorage(instance);
							} else commandsShopStorage = auctionStorage;
						} else if(StorageType.getType(getConfig().getSplitStorage().getShops()) == StorageType.MYSQL) {
							shopStorage = new MySqlStorage(instance);
							if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.H2) {
								commandsShopStorage = new H2Storage(instance);
							} else if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.MYSQL) {
								commandsShopStorage = shopStorage;
							} else commandsShopStorage = auctionStorage;
						} else {
							shopStorage = auctionStorage;
							if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.H2) {
								commandsShopStorage = new H2Storage(instance);
							} else if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.MYSQL) {
								commandsShopStorage = new MySqlStorage(instance);
							} else commandsShopStorage = auctionStorage;
						}
						break;
				}
			} else switch(StorageType.getType(getConfig().getSplitStorage().getAuction())) {
				case H2:
					auctionStorage = new H2Storage(instance);
					if(StorageType.getType(getConfig().getSplitStorage().getShops()) == StorageType.H2) {
						shopStorage = auctionStorage;
						if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.H2) {
							commandsShopStorage = shopStorage;
						} else commandsShopStorage = new ConfigStorage(instance);
					} else {
						shopStorage = new ConfigStorage(instance);
						if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.H2) {
							commandsShopStorage = auctionStorage;
						} else commandsShopStorage = shopStorage;
					}
					break;
				default:
					auctionStorage = new ConfigStorage(instance);
					if(StorageType.getType(getConfig().getSplitStorage().getShops()) == StorageType.H2) {
						shopStorage = new H2Storage(instance);
						if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.H2) {
							commandsShopStorage = shopStorage;
						} else commandsShopStorage = auctionStorage;
					} else {
						shopStorage = auctionStorage;
						if(StorageType.getType(getConfig().getSplitStorage().getCommandsShops()) == StorageType.H2) {
							commandsShopStorage = new H2Storage(instance);
						} else commandsShopStorage = shopStorage;
					}
					break;
			}
		} else if(getConfig().getMySQL().isEnable()) {
			createMySQLConnect();
			shopStorage = commandsShopStorage = auctionStorage = new MySqlStorage(instance);
		} else shopStorage = commandsShopStorage = auctionStorage = new ConfigStorage(instance);
		if(getConfig().getMySQL().isEnable()) {
			createMySQLConnect();
		}
		if(shopStorage instanceof ConfigStorage) {
			File folder = configDir.resolve(getConfig().getStorageFolders().getShops()).toFile();
			if(!folder.exists() || !folder.isDirectory()) folder.mkdir();
		}
		if(commandsShopStorage instanceof ConfigStorage) {
			File folder = configDir.resolve(getConfig().getStorageFolders().getCommandsShops()).toFile();
			if(!folder.exists() || !folder.isDirectory()) folder.mkdir();
		}
	}

	private void createMySQLConnect() {
		mySQL = new MySQL(
			instance,
			getConfig().getMySQL().getHost(),
			getConfig().getMySQL().getPort(),
			getConfig().getMySQL().getDataBase(),
			getConfig().getMySQL().getUser(),
			getConfig().getMySQL().getPassword(),
			getConfig().getMySQL().getSsl()
		);
		if(mySQL.getOrOpenConnection() == null) mySQL = null;
	}

	private void loadExpires() {
		expires.addAll(getConfig().getAuction().getExpire().stream().map(e -> new Expire(e.getTime(), e.getTax().getSize(), e.getFee().getSize(), e.getTax().isEnable(), e.getFee().isEnable())).toList());
	}

	private void updateAuctionData() {
		if(getConfig().getAuction().isEnable()) {
			if(!auctionItems.isEmpty()) {
				Map<UUID, SerializedAuctionStack> items = new HashMap<UUID, SerializedAuctionStack>();
				items.putAll(auctionItems);
				for(SerializedAuctionStack auctionItem : items.values()) {
					if(auctionItem.isExpired()) {
						auctionItems.remove(auctionItem.getStackUUID());
						auctionStorage.removeAuctionStack(auctionItem.getStackUUID());
						if(!auctionItem.betIsNull() && auctionItem.getBetData().getServer().equals(getConfig().getAuction().getServer()) && economy.checkPlayerBalance(auctionItem.getBetData().getBuyerUUID(), auctionItem.getBetData().getCurrency(), auctionItem.getBetData().getMoney().multiply(BigDecimal.valueOf(auctionItem.getSerializedItemStack().getQuantity())))) {
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
				if(auctionStack.getServerName().equals(getConfig().getAuction().getServer())) {
					sendMessage = true;
					break;
				}
			}
			if(sendMessage) {
				player.sendMessage(locales.getAsReferenced(player).messages().auction().expired()
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(expiredAuctionItems.containsKey(uuid)) {
								if(!expiredAuctionItems.get(uuid).isEmpty()) {
									int emptySlots = player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).freeCapacity();
									List<SerializedAuctionStack> toRemove = new ArrayList<SerializedAuctionStack>();
									toRemove.addAll(expiredAuctionItems.get(uuid));
									for(SerializedAuctionStack auctionItem : toRemove) {
										if(emptySlots <= 0) {
											player.sendMessage(locales.getAsReferenced(player).messages().auction().noEmptySlots(expiredAuctionItems.get(uuid).size()));
											return;
										}
										if(auctionItem.getServerName().equals(getConfig().getAuction().getServer())) {
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
				if(auctionStack.getBetData().getServer().equals(getConfig().getAuction().getServer())) {
					sendMessage = true;
					break;
				}
			}
			if(sendMessage) {
				player.sendMessage(locales.getAsReferenced(player).messages().auction().betExpired()
						.clickEvent(SpongeComponents.executeCallback(cause -> {
							if(expiredBetAuctionItems.containsKey(uuid)) {
								if(!expiredBetAuctionItems.get(uuid).isEmpty()) {
									int emptySlots = player.inventory().query(QueryTypes.INVENTORY_TYPE.get().of(PrimaryPlayerInventory.class)).freeCapacity();
									List<SerializedAuctionStack> toRemove = new ArrayList<SerializedAuctionStack>();
									toRemove.addAll(expiredBetAuctionItems.get(uuid));
									for(SerializedAuctionStack auctionItem : toRemove) {
										if(emptySlots <= 0) {
											player.sendMessage(locales.getAsReferenced(player).messages().auction().noEmptySlots(expiredAuctionItems.get(uuid).size()));
											return;
										}
										if(auctionItem.getBetData().getServer().equals(getConfig().getAuction().getServer())) {
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
		configBlackLists.save();
	}

	public void loadConfigs() {
		config.load();
		configBlackLists.load();
	}

}