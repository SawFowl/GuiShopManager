package sawfowl.guishopmanager.storage;

import java.io.IOException;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShop;
import sawfowl.guishopmanager.serialization.shop.SerializedShop;
import sawfowl.guishopmanager.utils.TypeTokens;

public class H2Storage extends Thread implements DBStorage {

	private GuiShopManager plugin;
	private String prefix;
	private Connection connection;
	private Statement statement;
	public H2Storage(GuiShopManager instance) {
		plugin = instance;
		prefix = plugin.getRootNode().node("MySQL", "Prefix").getString();
		try {
			statement = createStatement();
		} catch (SQLException e) {
		}
		createTables();
	}

	private void createTables() {
		String createShopsTable = "CREATE TABLE IF NOT EXISTS " + prefix  + "SHOPS(SHOP_ID VARCHAR(128) UNIQUE, SHOP_DATA LONGTEXT, PRIMARY KEY(SHOP_ID));";
		String createCommandsShopsTable = "CREATE TABLE IF NOT EXISTS " + prefix  + "COMMANDS(SHOP_ID VARCHAR(128) UNIQUE, SHOP_DATA LONGTEXT, PRIMARY KEY(SHOP_ID));";
		String createAuctionTable = "CREATE TABLE IF NOT EXISTS " + prefix + "AUCTION(STACK_UUID CHAR(36) UNIQUE, AUCTION_STACK LONGTEXT, PRIMARY KEY(STACK_UUID));";
		String createAuctionExpiredTable = "CREATE TABLE IF NOT EXISTS " + prefix + "AUCTION_EXPIRED(STACK_UUID CHAR(36) UNIQUE, AUCTION_STACK LONGTEXT, PRIMARY KEY(STACK_UUID));";
		String createAuctionExpiredBetTable = "CREATE TABLE IF NOT EXISTS " + prefix + "AUCTION_EXPIRED_BET(STACK_UUID CHAR(36) UNIQUE, AUCTION_STACK LONGTEXT, PRIMARY KEY(STACK_UUID));";
		try {
			getStatement().execute(createShopsTable);
		} catch (SQLException e) {
			plugin.getLogger().error("Create shop table...");
			plugin.getLogger().error(createShopsTable);
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		try {
			getStatement().execute(createCommandsShopsTable);
		} catch (SQLException e) {
			plugin.getLogger().error("Create commands shop table...");
			plugin.getLogger().error(createCommandsShopsTable);
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		if(plugin.getRootNode().node("Auction", "Enable").getBoolean()) {
			try {
				getStatement().execute(createAuctionTable);
				getStatement().execute(createAuctionExpiredTable);
				getStatement().execute(createAuctionExpiredBetTable);
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
			String sql = "MERGE INTO " + prefix + "SHOPS(SHOP_ID, SHOP_DATA) VALUES(?, ?);";
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
			ResultSet results = getStatement().executeQuery("SELECT * FROM " + prefix + "SHOPS;");
			while(!results.isClosed() && results.next()) {
				plugin.addShop(results.getString("SHOP_ID"), setShopCurrencies(plugin, createNode(results.getString("SHOP_DATA")).get(TypeTokens.SHOP_TOKEN).deserialize()));
			}
		}
		catch (SQLException | IOException e) {
			plugin.getLogger().error("Get shops data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void deleteShop(String shopId) {
		try {
			getStatement().executeUpdate("DELETE FROM `" + prefix + "SHOPS` WHERE `" + prefix + "SHOPS`.`SHOP_ID` = \'" + shopId + "\'");
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
			String sql = "MERGE INTO " + prefix + "COMMANDS(SHOP_ID, SHOP_DATA) VALUES(?, ?);";
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
			ResultSet results = getStatement().executeQuery("SELECT * FROM " + prefix + "COMMANDS;");
			while(!results.isClosed() && results.next()) {
				plugin.addCommandShopData(results.getString("SHOP_ID"), setCommandShopCurrencies(plugin, createNode(results.getString("SHOP_DATA")).get(TypeTokens.COMMANDS_SHOP_TOKEN).deserialize()));
			}
		}
		catch (SQLException | IOException e) {
			plugin.getLogger().error("Get shops data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void deleteCommandsShop(String shopId) {
		try {
			getStatement().executeUpdate("DELETE FROM `" + prefix + "COMMANDS` WHERE `" + prefix + "COMMANDS`.`SHOP_ID` = \'" + shopId + "\'");
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
		String sql = "MERGE INTO " + prefix + "AUCTION(STACK_UUID, AUCTION_STACK) VALUES(?, ?);";
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
			getStatement().execute("DELETE FROM `" + prefix + "AUCTION` WHERE `" + prefix + "AUCTION`.`STACK_UUID` = \'" + stackUUID +"\'");
		} catch (SQLException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void saveExpireAuctionData(SerializedAuctionStack serializedAuctionStack) {
		String sql = "MERGE INTO " + prefix + "AUCTION_EXPIRED(STACK_UUID, AUCTION_STACK) VALUES(?, ?);";
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
			getStatement().execute("DELETE FROM `" + prefix + "AUCTION_EXPIRED` WHERE `" + prefix + "AUCTION_EXPIRED`.`STACK_UUID` = \'" + serializedAuctionStack.getStackUUID() +"\'");
		} catch (SQLException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	
	}

	@Override
	public void saveExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack) {
		String sql = "REPLACE INTO " + prefix + "AUCTION_EXPIRED_BET(STACK_UUID, AUCTION_STACK) VALUES(?, ?);";
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
			getStatement().execute("DELETE FROM `" + prefix + "AUCTION_EXPIRED_BET` WHERE `" + prefix + "AUCTION_EXPIRED_BET`.`STACK_UUID` = \'" + serializedAuctionStack.getStackUUID() +"\'");
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
			ResultSet results = getStatement().executeQuery("SELECT * FROM " + prefix + "AUCTION;");
			Map<UUID, SerializedAuctionStack> loaded = new HashMap<UUID, SerializedAuctionStack>();
			while(!results.isClosed() && results.next()) {
				UUID stackUUID = UUID.fromString(results.getString("STACK_UUID"));
				SerializedAuctionStack serializedAuctionStack = setAuctionCurrencies(plugin, createNode(results.getString("AUCTION_STACK")).get(TypeTokens.AUCTIONSTACK_TOKEN));
				serializedAuctionStack.setStackUUID(stackUUID);
				if(serializedAuctionStack.getSerializedItemStack().getItemType().isPresent()) {
					serializedAuctionStack.setStackUUID(stackUUID);
					loaded.put(stackUUID, serializedAuctionStack);
				}
			}
			plugin.getAuctionItems().clear();
			plugin.getAuctionItems().putAll(loaded);
		} catch (SQLException | IOException e) {
			plugin.getLogger().error("Get actual auction data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	private void loadExpiredAuctionData() {
		try {
			ResultSet results = getStatement().executeQuery("SELECT * FROM " + prefix + "AUCTION_EXPIRED;");
			Map<UUID, Set<SerializedAuctionStack>> loadedExpireData = new HashMap<UUID, Set<SerializedAuctionStack>>();
			while(!results.isClosed() && results.next()) {
				SerializedAuctionStack serializedAuctionStack = setAuctionCurrencies(plugin, createNode(results.getString("AUCTION_STACK")).get(TypeTokens.AUCTIONSTACK_TOKEN));
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
		} catch (SQLException | IOException e) {
			plugin.getLogger().error("Get expired auction data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	private void loadExpiredBetAuctionData() {
		try {
			ResultSet results = getStatement().executeQuery("SELECT * FROM " + prefix + "AUCTION_EXPIRED_BET;");
			Map<UUID, Set<SerializedAuctionStack>> loadedExpireBetData = new HashMap<UUID, Set<SerializedAuctionStack>>();
			while(!results.isClosed() && results.next()) {
				SerializedAuctionStack serializedAuctionStack = setAuctionCurrencies(plugin, createNode(results.getString("AUCTION_STACK")).get(TypeTokens.AUCTIONSTACK_TOKEN));
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
		} catch (SQLException | IOException e) {
			plugin.getLogger().error("Get expired auction data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	private Statement getStatement() throws SQLException {
		return statement == null || statement.isClosed() ? statement = createStatement() : statement;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connection == null || connection.isClosed() ? connection = DriverManager.getConnection("jdbc:h2:" + plugin.getConfigDir().resolve("StorageData").toFile().getAbsolutePath(), "", "") : connection;
	}

}
