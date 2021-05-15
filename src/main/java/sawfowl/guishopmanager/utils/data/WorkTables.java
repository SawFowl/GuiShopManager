package sawfowl.guishopmanager.utils.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.api.scheduler.Task;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.utils.serialization.shop.SerializedShop;

public class WorkTables extends WorkData {

	private GuiShopManager plugin;
	private long lastLoad;
	private String prefix;
	public WorkTables(GuiShopManager instance) {
		plugin = instance;
		prefix = plugin.getRootNode().getNode("MySQL", "Prefix").getString();
		createTables();
	}

	private void createTables() {
		String createShopsTable = "CREATE TABLE IF NOT EXISTS " + prefix  + "shops(shop_id VARCHAR(128) UNIQUE, shop_data LONGTEXT, PRIMARY KEY(shop_id));";
		String createAuctionTable = "CREATE TABLE IF NOT EXISTS " + prefix + "auction(stack_uuid CHAR(36) UNIQUE, auction_stack LONGTEXT, PRIMARY KEY(stack_uuid));";
		String createAuctionExpiredTable = "CREATE TABLE IF NOT EXISTS " + prefix + "auction_expired(stack_uuid CHAR(36) UNIQUE, auction_stack LONGTEXT, PRIMARY KEY(stack_uuid));";
		String createAuctionExpiredBetTable = "CREATE TABLE IF NOT EXISTS " + prefix + "auction_expired_bet(stack_uuid CHAR(36) UNIQUE, auction_stack LONGTEXT, PRIMARY KEY(stack_uuid));";
		try {
			Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
			statement.executeQuery(createShopsTable);
			statement.close();
		}
		catch (SQLException e) {
			plugin.getLogger().error(createShopsTable);
			plugin.getLogger().error("Create shop table...");
			
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		if(plugin.getRootNode().getNode("Auction", "Enable").getBoolean()) {
			try {
				Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
				statement.executeQuery(createAuctionTable);
				statement.executeQuery(createAuctionExpiredTable);
				statement.executeQuery(createAuctionExpiredBetTable);
				statement.close();
			}
			catch (SQLException e) {
				plugin.getLogger().error(createAuctionTable);
				plugin.getLogger().error(createAuctionExpiredTable);
				plugin.getLogger().error(createAuctionExpiredBetTable);
				plugin.getLogger().error("Create tables...");
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void saveShop(String shopId) {
		Task.builder().async().execute(() -> {
			SerializedShop serializableShop = plugin.getShop(shopId).serialize();
	        try {
	            StringWriter sink = new StringWriter();
	            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
	            ConfigurationNode node = loader.createEmptyNode();
	            node.getNode("Content").setValue(TypeToken.of(SerializedShop.class), serializableShop);
	            loader.save(node);
	    		String sql = "REPLACE INTO " + prefix + "shops(shop_id, shop_data) VALUES(?, ?);";
	    		try (PreparedStatement statement = plugin.getMySQL().getOrOpenConnection().prepareStatement(sql)) {
	    			statement.setString(1, shopId);
	    		    statement.setString(2, sink.toString());
	    		    statement.execute();
	    		} catch (SQLException e) {
	    			plugin.getLogger().error("Write shop data to database");
	    			plugin.getLogger().error(e.getLocalizedMessage());
	    		}
	        } catch (Exception e) {
    			plugin.getLogger().error(e.getLocalizedMessage());
	        }
			try {
				List<String> enabledShops = plugin.getRootNode().getNode("ShopList").getValue(new TypeToken<List<String>>() {
					private static final long serialVersionUID = 01;});
				if(!enabledShops.contains(shopId)) {
					enabledShops.add(shopId);
				}
				plugin.getRootNode().getNode("ShopList").setValue(new TypeToken<List<String>>() {
					private static final long serialVersionUID = 01;}, enabledShops);
				plugin.updateConfigs();
			} catch (ObjectMappingException e) {
    			plugin.getLogger().error(e.getLocalizedMessage());
			}
		}).submit(plugin);
	}

	@Override
	public void loadShops() {
		Task.builder().async().execute(() -> {
			if(!plugin.getRootNode().getNode("ShopList").isEmpty()) {
				try {
					Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
					ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "shops;");
					List<String> enabledShops = plugin.getRootNode().getNode("ShopList").getValue(new TypeToken<List<String>>() {
						private static final long serialVersionUID = 01;});
					while(results.next()) {
						String shopId = results.getString("shop_id");
						if(enabledShops.contains(shopId)) {
							String shopData = results.getString("shop_data");
				            StringReader source = new StringReader(shopData);
				            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
				            ConfigurationNode node = loader.load();
							plugin.addShop(shopId, node.getNode("Content").getValue(TypeToken.of(SerializedShop.class)).deserialize());
						}
					}
					statement.close();
				}
				catch (SQLException | ObjectMappingException | IOException e) {
					plugin.getLogger().error("Get shops data");
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
		}).submit(plugin);
	}

	@Override
	public void deleteShop(String shopId) {
		Task.builder().async().execute(() -> {
			if(!plugin.getRootNode().getNode("ShopList").isEmpty()) {
				List<String> enabledShops = new ArrayList<String>();
				try {
					enabledShops.addAll(plugin.getRootNode().getNode("ShopList").getValue(new TypeToken<List<String>>() {
						private static final long serialVersionUID = 01;}));
				} catch (ObjectMappingException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
				if(enabledShops.contains(shopId)) {
					enabledShops.remove(shopId);
					try {
						plugin.getRootNode().getNode("ShopList").setValue(new TypeToken<List<String>>() {
							private static final long serialVersionUID = 01;}, enabledShops);
						plugin.updateConfigs();
					} catch (ObjectMappingException e) {
						plugin.getLogger().error(e.getLocalizedMessage());
					}
				}
			}
			try {
				Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
				statement.executeUpdate("DELETE FROM `" + prefix + "shops` WHERE `" + prefix + "shops`.`shop_id` = \'" + shopId + "\'");
			} catch (SQLException e) {
				plugin.getLogger().error("Delete shop data");
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}).submit(plugin);
	}

	@Override
	public void loadAuction() {
		if(lastLoad >= (System.currentTimeMillis() / 1000) - 20) {
			return;
		}
		lastLoad = System.currentTimeMillis() / 1000;
		loadActualAuctionData();
		loadExpiredAuctionData();
		loadExpiredBetAuctionData();
	}

	@Override
	public void saveAuctionStack(SerializedAuctionStack serializedAuctionStack) {
		String sql = "REPLACE INTO " + prefix + "auction(stack_uuid, auction_stack) VALUES(?, ?);";
        StringWriter sink = new StringWriter();
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
        ConfigurationNode node = loader.createEmptyNode();
        try {
            node.getNode("Content").setValue(TypeToken.of(SerializedAuctionStack.class), serializedAuctionStack);
			loader.save(node);
		} catch (IOException | ObjectMappingException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		try (PreparedStatement statement = plugin.getMySQL().getOrOpenConnection().prepareStatement(sql)) {
			statement.setString(1, serializedAuctionStack.getStackUUID().toString());
		    statement.setString(2, sink.toString());
		    statement.execute();
		} catch (SQLException e) {
			plugin.getLogger().error("Write AuctionStack to database");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void removeAuctionStack(UUID stackUUID) {
		try {
			Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
			statement.executeQuery("DELETE FROM `" + prefix + "auction` WHERE `" + prefix + "auction`.`stack_uuid` = \'" + stackUUID +"\'");
			statement.close();
		} catch (SQLException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void saveExpireAuctionData(SerializedAuctionStack serializedAuctionStack) {
		String sql = "REPLACE INTO " + prefix + "auction_expired(stack_uuid, auction_stack) VALUES(?, ?);";
        StringWriter sink = new StringWriter();
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
        ConfigurationNode node = loader.createEmptyNode();
        try {
            node.getNode("Content").setValue(new TypeToken<SerializedAuctionStack>() {
				private static final long serialVersionUID = 01;
			}, serializedAuctionStack);
			loader.save(node);
		} catch (IOException | ObjectMappingException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		try (PreparedStatement statement = plugin.getMySQL().getOrOpenConnection().prepareStatement(sql)) {
			statement.setString(1, serializedAuctionStack.getStackUUID().toString());
		    statement.setString(2, sink.toString());
		    statement.execute();
		} catch (SQLException e) {
			plugin.getLogger().error("Write AuctionStack to database");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void removeExpireAuctionData(UUID stackUUID) {
		try {
			Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
			;
			statement.executeQuery("DELETE FROM `" + prefix + "auction_expired` WHERE `" + prefix + "auction_expired`.`stack_uuid` = \'" + stackUUID +"\'");
			statement.close();
		} catch (SQLException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void saveExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack) {
		String sql = "REPLACE INTO " + prefix + "auction_expired_bet(stack_uuid, auction_stack) VALUES(?, ?);";
        StringWriter sink = new StringWriter();
        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSink(() -> new BufferedWriter(sink)).build();
        ConfigurationNode node = loader.createEmptyNode();
        try {
            node.getNode("Content").setValue(new TypeToken<SerializedAuctionStack>() {
				private static final long serialVersionUID = 01;
			}, serializedAuctionStack);
			loader.save(node);
		} catch (IOException | ObjectMappingException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		try (PreparedStatement statement = plugin.getMySQL().getOrOpenConnection().prepareStatement(sql)) {
			statement.setString(1, serializedAuctionStack.getStackUUID().toString());
		    statement.setString(2, sink.toString());
		    statement.execute();
		} catch (SQLException e) {
			plugin.getLogger().error("Write AuctionStack to database");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void removeExpireBetAuctionData(UUID stackUUID) {
		try {
			Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
			statement.executeQuery("DELETE FROM `" + prefix + "auction_expired_bet` WHERE `" + prefix + "auction_expired_bet`.`stack_uuid` = \'" + stackUUID +"\'");
			statement.close();
		} catch (SQLException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	private void loadActualAuctionData() {
		try {
			Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "auction;");
			List<SerializedAuctionStack> loaded = new ArrayList<SerializedAuctionStack>();
			while(results.next()) {
				UUID stackUUID = UUID.fromString(results.getString("stack_uuid"));
				String content = results.getString("auction_stack");
	            StringReader source = new StringReader(content);
	            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
	            ConfigurationNode node = loader.load();
	            SerializedAuctionStack serializedAuctionStack = node.getNode("Content").getValue(TypeToken.of(SerializedAuctionStack.class));
	            if(serializedAuctionStack.getSerializedItemStack().isPresent()) {
	            	serializedAuctionStack.setStackUUID(stackUUID);
		            loaded.add(serializedAuctionStack);
	            }
			}
			statement.close();
			plugin.getAuctionItems().clear();
			plugin.getAuctionItems().addAll(loaded);
		} catch (SQLException | IOException | ObjectMappingException e) {
			plugin.getLogger().error("Get actual auction data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	private void loadExpiredAuctionData() {
		try {
			Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "auction_expired;");
			Map<UUID, List<SerializedAuctionStack>> loadedExpireData = new HashMap<UUID, List<SerializedAuctionStack>>();
			while(results.next()) {
				String content = results.getString("auction_stack");
	            StringReader source = new StringReader(content);
	            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
	            ConfigurationNode node = loader.load();
	            SerializedAuctionStack serializedAuctionStacks = node.getNode("Content").getValue(TypeToken.of(SerializedAuctionStack.class));
            	if(serializedAuctionStacks.getSerializedItemStack().isPresent()) {
            		if(!loadedExpireData.containsKey(serializedAuctionStacks.getOwnerUUID())) {
						List<SerializedAuctionStack> newList = new ArrayList<SerializedAuctionStack>();
						newList.add(serializedAuctionStacks);
						loadedExpireData.put(serializedAuctionStacks.getOwnerUUID(), newList);
            		} else {
            			loadedExpireData.get(serializedAuctionStacks.getOwnerUUID()).add(serializedAuctionStacks);
            		}
            	}
			}
			plugin.getExpiredAuctionItems().clear();
			plugin.getExpiredAuctionItems().putAll(loadedExpireData);
			statement.close();
		} catch (SQLException | IOException | ObjectMappingException e) {
			plugin.getLogger().error("Get expired auction data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	private void loadExpiredBetAuctionData() {
		try {
			Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "auction_expired_bet;");
			Map<UUID, List<SerializedAuctionStack>> loadedExpireBetData = new HashMap<UUID, List<SerializedAuctionStack>>();
			while(results.next()) {
				String content = results.getString("auction_stack");
	            StringReader source = new StringReader(content);
	            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().setSource(() -> new BufferedReader(source)).build();
	            ConfigurationNode node = loader.load();
	            SerializedAuctionStack serializedAuctionStacks = node.getNode("Content").getValue(TypeToken.of(SerializedAuctionStack.class));
            	if(serializedAuctionStacks.getSerializedItemStack().isPresent()) {
            		if(!loadedExpireBetData.containsKey(serializedAuctionStacks.getOwnerUUID())) {
						List<SerializedAuctionStack> newList = new ArrayList<SerializedAuctionStack>();
						newList.add(serializedAuctionStacks);
            			loadedExpireBetData.put(serializedAuctionStacks.getOwnerUUID(), newList);
            		} else {
            			loadedExpireBetData.get(serializedAuctionStacks.getOwnerUUID()).add(serializedAuctionStacks);
            		}
            	}
			}
			plugin.getExpiredBetAuctionItems().clear();
			plugin.getExpiredBetAuctionItems().putAll(loadedExpireBetData);
			statement.close();
		} catch (SQLException | IOException | ObjectMappingException e) {
			plugin.getLogger().error("Get expired auction data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

}
