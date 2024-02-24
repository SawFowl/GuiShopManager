package sawfowl.guishopmanager.storage;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.scheduler.ScheduledTask;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.TypeTokens;
import sawfowl.guishopmanager.data.commandshop.CommandItemData;
import sawfowl.guishopmanager.data.commandshop.CommandShopMenuData;
import sawfowl.guishopmanager.data.shop.ShopItem;
import sawfowl.guishopmanager.data.shop.ShopMenuData;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShop;
import sawfowl.guishopmanager.serialization.shop.SerializedShop;

public class MySqlStorage extends Thread implements DBStorage {

	private GuiShopManager plugin;
	private String prefix;
	private ScheduledTask task;
	private String updateTimeAuction;
	private String updateTimeAuctionExpired;
	private String updateTimeAuctionExpiredBet;
	private String updateTimeShops;
	private String updateTimeCommandShops;
	private Connection syncConnection;
	public MySqlStorage(GuiShopManager instance) {
		plugin = instance;
		prefix = plugin.getRootNode().node("MySQL", "Prefix").getString();
		createTables();
		sync();
	}

	private void createTables() {
		String createShopsTable = "CREATE TABLE IF NOT EXISTS " + prefix  + "shops(shop_id VARCHAR(128) UNIQUE, shop_data LONGTEXT, written DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(shop_id));";
		String createCommandsShopsTable = "CREATE TABLE IF NOT EXISTS " + prefix  + "commands(shop_id VARCHAR(128) UNIQUE, shop_data LONGTEXT, written DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(shop_id));";
		String createAuctionTable = "CREATE TABLE IF NOT EXISTS " + prefix + "auction(stack_uuid CHAR(36) UNIQUE, auction_stack LONGTEXT, written DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(stack_uuid));";
		String createAuctionExpiredTable = "CREATE TABLE IF NOT EXISTS " + prefix + "auction_expired(stack_uuid CHAR(36) UNIQUE, auction_stack LONGTEXT, written DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(stack_uuid));";
		String createAuctionExpiredBetTable = "CREATE TABLE IF NOT EXISTS " + prefix + "auction_expired_bet(stack_uuid CHAR(36) UNIQUE, auction_stack LONGTEXT, written DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, PRIMARY KEY(stack_uuid));";
		try {
			Statement statement = createStatement();
			statement.execute(createShopsTable);
			statement.close();
			statement = null;
		} catch (SQLException e) {
			plugin.getLogger().error("Create shop table...");
			plugin.getLogger().error(createShopsTable);
			plugin.getLogger().error(e.getLocalizedMessage());
		} try {
			Statement statement = createStatement();
			statement.execute(createCommandsShopsTable);
			statement.close();
			statement = null;
		} catch (SQLException e) {
			plugin.getLogger().error("Create commands shop table...");
			plugin.getLogger().error(createCommandsShopsTable);
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		if(plugin.getRootNode().node("Auction", "Enable").getBoolean()) {
			try {
				Statement statement = createStatement();
				statement.execute(createAuctionTable);
				statement.execute(createAuctionExpiredTable);
				statement.execute(createAuctionExpiredBetTable);
				statement.close();
				statement = null;
			} catch (SQLException e) {
				plugin.getLogger().error("Create auction tables...");
				plugin.getLogger().error(createAuctionTable);
				plugin.getLogger().error(createAuctionExpiredTable);
				plugin.getLogger().error(createAuctionExpiredBetTable);
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}
		createShopsTable = null;
		createCommandsShopsTable = null;
		createAuctionTable = null;
		createAuctionExpiredTable = null;
		createAuctionExpiredBetTable = null;
	}

	@Override
	public void saveShop(String shopId) {
		SerializedShop serializableShop = plugin.getShop(shopId).serialize();
		try {
			StringWriter sink = new StringWriter();
			ConfigurationLoader<? extends ConfigurationNode> loader = createLoader(sink);
			ConfigurationNode node = loader.createNode();
			node.set(TypeTokens.SHOP_TOKEN, serializableShop);
			loader.save(node);
			String sql = "REPLACE INTO " + prefix + "shops(shop_id, shop_data) VALUES(?, ?);";
			try(PreparedStatement statement = getConnection().prepareStatement(sql)) {
				statement.setString(1, shopId);
				statement.setString(2, sink.toString());
				statement.execute();
				statement.close();
			} catch (SQLException e) {
				plugin.getLogger().error("Write shop data to database");
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		} catch (Exception e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void loadShops() {
		try {
			Statement statement = createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "shops ORDER BY written;");
			while(results.next()) {
				if(updateTimeShops == null) updateTimeShops = results.getString("written");
				plugin.addShop(results.getString("shop_id"), setShopCurrencies(plugin, createNode(results.getString("shop_data")).get(TypeTokens.SHOP_TOKEN).deserialize()));
			}
			statement.close();
			statement = null;
		}
		catch (SQLException | IOException e) {
			plugin.getLogger().error("Get shops data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void deleteShop(String shopId) {
		try {
			Statement statement = createStatement();
			statement.executeUpdate("DELETE FROM `" + prefix + "shops` WHERE `" + prefix + "shops`.`shop_id` = \'" + shopId + "\'");
			statement.close();
			statement = null;
		} catch (SQLException e) {
			plugin.getLogger().error("Delete shop data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void saveCommandsShop(String shopId) {
		SerializedCommandShop serializableShop = plugin.getCommandShopData(shopId).serialize();
		try {
			StringWriter sink = new StringWriter();
			ConfigurationLoader<? extends ConfigurationNode> loader = createLoader(sink);
			ConfigurationNode node = loader.createNode();
			node.set(TypeTokens.COMMANDS_SHOP_TOKEN, serializableShop);
			loader.save(node);
			String sql = "REPLACE INTO " + prefix + "commands(shop_id, shop_data) VALUES(?, ?);";
			try(PreparedStatement statement = getConnection().prepareStatement(sql)) {
				statement.setString(1, shopId);
				statement.setString(2, sink.toString());
				statement.execute();
				statement.close();
			} catch (SQLException e) {
				plugin.getLogger().error("Write commands shop data to database");
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		} catch (Exception e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void loadCommandsShops() {
		try {
			Statement statement = createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "commands ORDER BY written;");
			while(results.next()) {
				if(updateTimeCommandShops == null) updateTimeCommandShops = results.getString("written");
				plugin.addCommandShopData(results.getString("shop_id"), setCommandShopCurrencies(plugin, createNode(results.getString("shop_data")).get(TypeTokens.COMMANDS_SHOP_TOKEN).deserialize()));
			}
			statement.close();
			statement = null;
		}
		catch (SQLException | IOException e) {
			plugin.getLogger().error("Get shops data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void deleteCommandsShop(String shopId) {
		try {
			Statement statement = createStatement();
			statement.executeUpdate("DELETE FROM `" + prefix + "commands` WHERE `" + prefix + "commands`.`shop_id` = \'" + shopId + "\'");
			statement.close();
			statement = null;
		} catch (SQLException e) {
			plugin.getLogger().error("Delete commands shop data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void loadAuction() {
		loadActualAuctionData();
		loadExpiredAuctionData();
		loadExpiredBetAuctionData();
	}

	@Override
	public void saveAuctionStack(SerializedAuctionStack serializedAuctionStack) {
		String sql = "REPLACE INTO " + prefix + "auction(stack_uuid, auction_stack) VALUES(?, ?);";
		StringWriter sink = new StringWriter();
		ConfigurationLoader<? extends ConfigurationNode> loader = createLoader(sink);
		ConfigurationNode node = loader.createNode();
		try {
			node.set(TypeTokens.AUCTIONSTACK_TOKEN, serializedAuctionStack);
			loader.save(node);
		} catch (IOException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		try(PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, serializedAuctionStack.getStackUUID().toString());
			statement.setString(2, sink.toString());
			statement.execute();
			statement.close();
		} catch (SQLException e) {
			plugin.getLogger().error("Write AuctionStack to database");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void removeAuctionStack(UUID stackUUID) {
		try {
			Statement statement = createStatement();
			statement.execute("DELETE FROM `" + prefix + "auction` WHERE `" + prefix + "auction`.`stack_uuid` = \'" + stackUUID +"\'");
			statement.close();
			statement = null;
		} catch (SQLException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void saveExpireAuctionData(SerializedAuctionStack serializedAuctionStack) {
		String sql = "REPLACE INTO " + prefix + "auction_expired(stack_uuid, auction_stack) VALUES(?, ?);";
		StringWriter sink = new StringWriter();
		ConfigurationLoader<? extends ConfigurationNode> loader = createLoader(sink);
		ConfigurationNode node = loader.createNode();
		try {
			node.set(TypeTokens.AUCTIONSTACK_TOKEN, serializedAuctionStack);
			loader.save(node);
		} catch (IOException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		try(PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, serializedAuctionStack.getStackUUID().toString());
			statement.setString(2, sink.toString());
			statement.execute();
			statement.close();
		} catch (SQLException e) {
			plugin.getLogger().error("Write AuctionStack to database");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void removeExpireAuctionData(SerializedAuctionStack serializedAuctionStack) {
		try {
			Statement statement = createStatement();
			statement.execute("DELETE FROM `" + prefix + "auction_expired` WHERE `" + prefix + "auction_expired`.`stack_uuid` = \'" + serializedAuctionStack.getStackUUID() +"\'");
			statement.close();
			statement = null;
		} catch (SQLException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	
	}

	@Override
	public void saveExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack) {
		String sql = "REPLACE INTO " + prefix + "auction_expired_bet(stack_uuid, auction_stack) VALUES(?, ?);";
		StringWriter sink = new StringWriter();
		ConfigurationLoader<? extends ConfigurationNode> loader = createLoader(sink);
		ConfigurationNode node = loader.createNode();
		try {
			node.set(TypeTokens.AUCTIONSTACK_TOKEN, serializedAuctionStack);
			loader.save(node);
		} catch (IOException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		try(PreparedStatement statement = getConnection().prepareStatement(sql)) {
			statement.setString(1, serializedAuctionStack.getStackUUID().toString());
			statement.setString(2, sink.toString());
			statement.execute();
			statement.close();
		} catch (SQLException e) {
			plugin.getLogger().error("Write AuctionStack to database");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void removeExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack) {
		try {
			Statement statement = createStatement();
			statement.execute("DELETE FROM `" + prefix + "auction_expired_bet` WHERE `" + prefix + "auction_expired_bet`.`stack_uuid` = \'" + serializedAuctionStack.getStackUUID() +"\'");
			statement.close();
			statement = null;
		} catch (SQLException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public Format getFormat() {
		return Format.find(plugin.getRootNode().node("ConfigTypes", "SqlFormat").getString());
	}

	private void loadActualAuctionData() {
		try {
			Statement statement = createStatement();
			ResultSet results = createStatement().executeQuery("SELECT * FROM " + prefix + "auction ORDER BY written;");
			Map<UUID, SerializedAuctionStack> loaded = new HashMap<UUID, SerializedAuctionStack>();
			while(results.next()) {
				if(updateTimeAuction == null) updateTimeAuction = results.getString("written");
				UUID stackUUID = UUID.fromString(results.getString("stack_uuid"));
				SerializedAuctionStack serializedAuctionStack = setAuctionCurrencies(plugin, createNode(results.getString("auction_stack")).get(TypeTokens.AUCTIONSTACK_TOKEN));
				serializedAuctionStack.setStackUUID(stackUUID);
				if(serializedAuctionStack.getSerializedItemStack().getItemType().isPresent()) {
					serializedAuctionStack.setStackUUID(stackUUID);
					loaded.put(stackUUID, serializedAuctionStack);
				}
			}
			statement.close();
			statement = null;
			plugin.getAuctionItems().clear();
			plugin.getAuctionItems().putAll(loaded);
		} catch (SQLException | IOException e) {
			plugin.getLogger().error("Get actual auction data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	private void loadExpiredAuctionData() {
		try {
			Statement statement = createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "auction_expired ORDER BY written;");
			Map<UUID, Set<SerializedAuctionStack>> loadedExpireData = new HashMap<UUID, Set<SerializedAuctionStack>>();
			while(results.next()) {
				if(updateTimeAuctionExpired == null) updateTimeAuctionExpired = results.getString("written");
				SerializedAuctionStack serializedAuctionStack = setAuctionCurrencies(plugin, createNode(results.getString("auction_stack")).get(TypeTokens.AUCTIONSTACK_TOKEN));
				if(serializedAuctionStack.getSerializedItemStack().getItemType().isPresent()) {
					if(!loadedExpireData.containsKey(serializedAuctionStack.getOwnerUUID())) {
						Set<SerializedAuctionStack> newList = new HashSet<SerializedAuctionStack>();
						newList.add(serializedAuctionStack);
						loadedExpireData.put(serializedAuctionStack.getOwnerUUID(), newList);
					} else {
						loadedExpireData.get(serializedAuctionStack.getOwnerUUID()).add(serializedAuctionStack);
					}
				}
			}
			plugin.getExpiredAuctionItems().clear();
			plugin.getExpiredAuctionItems().putAll(loadedExpireData);
			statement.close();
			statement = null;
		} catch (SQLException | IOException e) {
			plugin.getLogger().error("Get expired auction data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	private void loadExpiredBetAuctionData() {
		try {
			Statement statement = createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "auction_expired_bet ORDER BY written;");
			Map<UUID, Set<SerializedAuctionStack>> loadedExpireBetData = new HashMap<UUID, Set<SerializedAuctionStack>>();
			while(results.next()) {
				if(updateTimeAuctionExpiredBet == null) updateTimeAuctionExpiredBet = results.getString("written");
				SerializedAuctionStack serializedAuctionStack = setAuctionCurrencies(plugin, createNode(results.getString("auction_stack")).get(TypeTokens.AUCTIONSTACK_TOKEN));
				if(serializedAuctionStack.getSerializedItemStack().getItemType().isPresent()) {
					if(!loadedExpireBetData.containsKey(serializedAuctionStack.getOwnerUUID())) {
						Set<SerializedAuctionStack> newList = new HashSet<SerializedAuctionStack>();
						newList.add(serializedAuctionStack);
						loadedExpireBetData.put(serializedAuctionStack.getOwnerUUID(), newList);
					} else {
						loadedExpireBetData.get(serializedAuctionStack.getOwnerUUID()).add(serializedAuctionStack);
					}
				}
			}
			plugin.getExpiredBetAuctionItems().clear();
			plugin.getExpiredBetAuctionItems().putAll(loadedExpireBetData);
			statement.close();
			statement = null;
		} catch (SQLException | IOException e) {
			plugin.getLogger().error("Get expired auction data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public Connection getConnection() {
		return plugin.getMySQL().getOrOpenConnection();
	}

	private Statement createSyncStatement() throws SQLException {
		if(syncConnection == null || syncConnection.isClosed()) syncConnection = plugin.getMySQL().openConnection();
		return syncConnection.createStatement();
	}

	public void sync() {
		if(plugin.getRootNode().node("MySQL", "SyncInterval").getInt(5) < 1) return;
		if(task != null) {
			task.cancel();
			task = null;
		}
		task = syncTask();
	}

	private ScheduledTask syncTask() {
		return Sponge.asyncScheduler().submit(Task.builder().plugin(plugin.getPluginContainer()).interval(plugin.getRootNode().node("MySQL", "SyncInterval").getInt(5), TimeUnit.SECONDS).execute(() -> {
			try {
				if(plugin.getRootNode().node("Auction", "Enable").getBoolean() && (plugin.getAuctionStorage().getClass() == MySqlStorage.this.getClass())  || plugin.getAuctionStorage() instanceof MySqlStorage) {
					syncAuction();
					syncAuctionExpired();
					syncAuctionExpiredBet();
				}
				syncShops();
				syncCommandShops();
			} catch (SQLException | ConfigurateException e) {
				e.printStackTrace();
			}
		}).build());
	}

	private void syncAuction() throws SQLException, SerializationException, ConfigurateException {
		if(updateTimeAuction == null) {
			loadActualAuctionData();
			return;
		}
		Statement statement = createSyncStatement();
		ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "auction WHERE written > '" + updateTimeAuction + "' ORDER BY written;");
		boolean updateTime = false;
		while(results.next()) {
			if(!updateTime) {
				updateTimeAuction = results.getString("written");
				updateTime = true;
			}
			UUID stackUUID = UUID.fromString(results.getString("stack_uuid"));
			SerializedAuctionStack serializedAuctionStack = createNode(results.getString("auction_stack")).get(TypeTokens.AUCTIONSTACK_TOKEN);
			serializedAuctionStack.setStackUUID(stackUUID);
			if(serializedAuctionStack.getSerializedItemStack().getItemType().isPresent()) {
				serializedAuctionStack.setStackUUID(stackUUID);
				serializedAuctionStack.getPrices().forEach(price -> {
					price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyId()));
				});
				if(plugin.getAuctionItems().containsKey(stackUUID)) plugin.getAuctionItems().remove(stackUUID);
				plugin.getAuctionItems().put(stackUUID, serializedAuctionStack);
			}
		}
		statement.close();
		statement = null;
	}

	private void syncAuctionExpired() throws SQLException, SerializationException, ConfigurateException {
		if(updateTimeAuctionExpired == null) {
			loadExpiredAuctionData();
			return;
		}
		Statement statement = createSyncStatement();
		ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "auction_expired WHERE written > '" + updateTimeAuctionExpired + "' ORDER BY written;");
		boolean updateTime = false;
		while(results.next()) {
			if(!updateTime) {
				updateTimeAuctionExpired = results.getString("written");
				updateTime = true;
			}
			SerializedAuctionStack serializedAuctionStack = createNode(results.getString("auction_stack")).get(TypeTokens.AUCTIONSTACK_TOKEN);
			serializedAuctionStack.getBetData().setCurrency(plugin.getEconomy().checkCurrency(serializedAuctionStack.getBetData().getCurrencyId()));
			if(serializedAuctionStack.getSerializedItemStack().getItemType().isPresent()) {
				serializedAuctionStack.getPrices().forEach(price -> {
					price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyId()));
				});
				if(!plugin.getExpiredAuctionItems().containsKey(serializedAuctionStack.getOwnerUUID())) {
					Set<SerializedAuctionStack> newList = new HashSet<SerializedAuctionStack>();
					newList.add(serializedAuctionStack);
					plugin.getExpiredAuctionItems().put(serializedAuctionStack.getOwnerUUID(), newList);
				} else {
					plugin.getExpiredAuctionItems().get(serializedAuctionStack.getOwnerUUID()).add(serializedAuctionStack);
				}
			}
		}
		statement.close();
		statement = null;
	}

	private void syncAuctionExpiredBet() throws SQLException, SerializationException, ConfigurateException {
		if(updateTimeAuctionExpiredBet == null) {
			loadExpiredBetAuctionData();
			return;
		}
		Statement statement = createSyncStatement();
		ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "auction_expired_bet WHERE written > '" + updateTimeAuctionExpiredBet + "' ORDER BY written;");
		boolean updateTime = false;
		while(results.next()) {
			if(!updateTime) {
				updateTimeAuctionExpiredBet = results.getString("written");
				updateTime = true;
			}
			SerializedAuctionStack serializedAuctionStack = createNode(results.getString("auction_stack")).get(TypeTokens.AUCTIONSTACK_TOKEN);
			if(serializedAuctionStack.getSerializedItemStack().getItemType().isPresent()) {
				serializedAuctionStack.getBetData().setCurrency(plugin.getEconomy().checkCurrency(serializedAuctionStack.getBetData().getCurrencyId()));
				serializedAuctionStack.getPrices().forEach(price -> {
					price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyId()));
				});
				if(!plugin.getExpiredBetAuctionItems().containsKey(serializedAuctionStack.getOwnerUUID())) {
					Set<SerializedAuctionStack> newList = new HashSet<SerializedAuctionStack>();
					newList.add(serializedAuctionStack);
					plugin.getExpiredBetAuctionItems().put(serializedAuctionStack.getOwnerUUID(), newList);
				} else {
					plugin.getExpiredBetAuctionItems().get(serializedAuctionStack.getOwnerUUID()).add(serializedAuctionStack);
				}
			}
		}
		statement.close();
		statement = null;
	}

	private void syncShops() throws SQLException, SerializationException, ConfigurateException {
		if(updateTimeShops == null) {
			loadShops();
			return;
		}
		Statement statement = createSyncStatement();
		ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "shops WHERE written > '" + updateTimeShops + "' ORDER BY written;");
		boolean updateTime = false;
		while(results.next()) {
			if(!updateTime) {
				updateTimeShops = results.getString("written");
				updateTime = true;
			}
			String shopId = results.getString("shop_id");
			plugin.removeShop(shopId);
			plugin.addShop(shopId, createNode(results.getString("shop_data")).get(TypeTokens.SHOP_TOKEN).deserialize());
			for(ShopMenuData shopMenuData : plugin.getShop(shopId).getMenus().values()) {
				for(ShopItem shopItem : shopMenuData.getItems().values()) {
					shopItem.getPrices().forEach(price -> {
						price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyId()));
					});
				}
			}
		}
		statement.close();
		statement = null;
	}

	private void syncCommandShops() throws SQLException, SerializationException, ConfigurateException {
		if(updateTimeCommandShops == null) {
			loadCommandsShops();
			return;
		}
		Statement statement = createSyncStatement();
		ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "commands WHERE written > '" + updateTimeCommandShops + "' ORDER BY written;");
		boolean updateTime = false;
		while(results.next()) {
			if(!updateTime) {
				updateTimeCommandShops = results.getString("written");
				updateTime = true;
			}
			String shopId = results.getString("shop_id");
			plugin.removeCommandShopData(shopId);
			plugin.addCommandShopData(shopId, createNode(results.getString("shop_data")).get(TypeTokens.COMMANDS_SHOP_TOKEN).deserialize());
			for(CommandShopMenuData shopMenuData : plugin.getCommandShopData(shopId).getMenus().values()) {
				for(CommandItemData shopItem : shopMenuData.getItems().values()) {
					shopItem.getPrices().forEach(price -> {
						price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyId()));
					});
				}
			}
		}
		statement.close();
		statement = null;
	}

}
