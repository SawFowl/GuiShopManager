package sawfowl.guishopmanager.data;

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

import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.TypeTokens;
import sawfowl.guishopmanager.data.commandshop.CommandItemData;
import sawfowl.guishopmanager.data.commandshop.CommandShopMenuData;
import sawfowl.guishopmanager.data.shop.ShopItem;
import sawfowl.guishopmanager.data.shop.ShopMenuData;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShop;
import sawfowl.guishopmanager.serialization.shop.SerializedShop;

public class WorkTables extends WorkData {

	private GuiShopManager plugin;
	private long lastLoad;
	private String prefix;
	public WorkTables(GuiShopManager instance) {
		plugin = instance;
		prefix = plugin.getRootNode().node("MySQL", "Prefix").getString();
		createTables();
	}

	private void createTables() {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			String createShopsTable = "CREATE TABLE IF NOT EXISTS " + prefix  + "shops(shop_id VARCHAR(128) UNIQUE, shop_data LONGTEXT, PRIMARY KEY(shop_id));";
			String createCommandsShopsTable = "CREATE TABLE IF NOT EXISTS " + prefix  + "commands(shop_id VARCHAR(128) UNIQUE, shop_data LONGTEXT, PRIMARY KEY(shop_id));";
			String createAuctionTable = "CREATE TABLE IF NOT EXISTS " + prefix + "auction(stack_uuid CHAR(36) UNIQUE, auction_stack LONGTEXT, PRIMARY KEY(stack_uuid));";
			String createAuctionExpiredTable = "CREATE TABLE IF NOT EXISTS " + prefix + "auction_expired(stack_uuid CHAR(36) UNIQUE, auction_stack LONGTEXT, PRIMARY KEY(stack_uuid));";
			String createAuctionExpiredBetTable = "CREATE TABLE IF NOT EXISTS " + prefix + "auction_expired_bet(stack_uuid CHAR(36) UNIQUE, auction_stack LONGTEXT, PRIMARY KEY(stack_uuid));";
			try {
				Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
				statement.execute(createShopsTable);
				statement.close();
			}
			catch (SQLException e) {
				plugin.getLogger().error("Create shop table...");
				plugin.getLogger().error(createShopsTable);
				plugin.getLogger().error(e.getLocalizedMessage());
			}
			try {
				Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
				statement.execute(createCommandsShopsTable);
				statement.close();
			}
			catch (SQLException e) {
				plugin.getLogger().error("Create commands shop table...");
				plugin.getLogger().error(createCommandsShopsTable);
				plugin.getLogger().error(e.getLocalizedMessage());
			}
			if(plugin.getRootNode().node("Auction", "Enable").getBoolean()) {
				try {
					Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
					statement.execute(createAuctionTable);
					statement.execute(createAuctionExpiredTable);
					statement.execute(createAuctionExpiredBetTable);
					statement.close();
				}
				catch (SQLException e) {
					plugin.getLogger().error("Create auction tables...");
					plugin.getLogger().error(createAuctionTable);
					plugin.getLogger().error(createAuctionExpiredTable);
					plugin.getLogger().error(createAuctionExpiredBetTable);
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
		});
	}

	@Override
	public void saveShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			SerializedShop serializableShop = plugin.getShop(shopId).serialize();
	        try {
	            StringWriter sink = new StringWriter();
	            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).sink(() -> new BufferedWriter(sink)).build();
	            ConfigurationNode node = loader.createNode();
	            node.node("Content").set(TypeTokens.SHOP_TOKEN, serializableShop);
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
		});
	}

	@Override
	public void loadShops() {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			try {
				Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
				ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "shops;");
				while(results.next()) {
					String shopId = results.getString("shop_id");
					String shopData = results.getString("shop_data");
		            StringReader source = new StringReader(shopData);
		            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).source(() -> new BufferedReader(source)).build();
		            ConfigurationNode node = loader.load();
					plugin.addShop(shopId, node.node("Content").get(TypeTokens.SHOP_TOKEN).deserialize());
					for(ShopMenuData shopMenuData : plugin.getShop(shopId).getMenus().values()) {
						for(ShopItem shopItem : shopMenuData.getItems().values()) {
							shopItem.getPrices().forEach(price -> {
								price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyName()));
							});
						}
					}
				}
				statement.close();
			}
			catch (SQLException | IOException e) {
				plugin.getLogger().error("Get shops data");
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void deleteShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			try {
				Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
				statement.executeUpdate("DELETE FROM `" + prefix + "shops` WHERE `" + prefix + "shops`.`shop_id` = \'" + shopId + "\'");
			} catch (SQLException e) {
				plugin.getLogger().error("Delete shop data");
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void saveCommandsShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			SerializedCommandShop serializableShop = plugin.getCommandShopData(shopId).serialize();
	        try {
	            StringWriter sink = new StringWriter();
	            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).sink(() -> new BufferedWriter(sink)).build();
	            ConfigurationNode node = loader.createNode();
	            node.node("Content").set(TypeTokens.COMMANDS_SHOP_TOKEN, serializableShop);
	            loader.save(node);
	    		String sql = "REPLACE INTO " + prefix + "commands(shop_id, shop_data) VALUES(?, ?);";
	    		try(PreparedStatement statement = plugin.getMySQL().getOrOpenConnection().prepareStatement(sql)) {
	    			statement.setString(1, shopId);
	    		    statement.setString(2, sink.toString());
	    		    statement.execute();
	    		} catch (SQLException e) {
	    			plugin.getLogger().error("Write commands shop data to database");
	    			plugin.getLogger().error(e.getLocalizedMessage());
	    		}
	        } catch (Exception e) {
    			plugin.getLogger().error(e.getLocalizedMessage());
	        }
		});
	}

	@Override
	public void loadCommandsShops() {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			try {
				Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
				ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "commands;");
				while(results.next()) {
					String shopId = results.getString("shop_id");
					String shopData = results.getString("shop_data");
		            StringReader source = new StringReader(shopData);
		            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).source(() -> new BufferedReader(source)).build();
		            ConfigurationNode node = loader.load();
					plugin.addCommandShopData(shopId, node.node("Content").get(TypeTokens.COMMANDS_SHOP_TOKEN).deserialize());
					for(CommandShopMenuData shopMenuData : plugin.getCommandShopData(shopId).getMenus().values()) {
						for(CommandItemData shopItem : shopMenuData.getItems().values()) {
							shopItem.getPrices().forEach(price -> {
								price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyName()));
							});
						}
					}
				}
				statement.close();
			}
			catch (SQLException | IOException e) {
				plugin.getLogger().error("Get shops data");
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void deleteCommandsShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			try {
				Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
				statement.executeUpdate("DELETE FROM `" + prefix + "commands` WHERE `" + prefix + "commands`.`shop_id` = \'" + shopId + "\'");
			} catch (SQLException e) {
				plugin.getLogger().error("Delete commands shop data");
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void loadAuction() {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			if(lastLoad >= (System.currentTimeMillis() / 1000) - 20) {
				return;
			}
			lastLoad = System.currentTimeMillis() / 1000;
			loadActualAuctionData();
			loadExpiredAuctionData();
			loadExpiredBetAuctionData();
		});
	}

	@Override
	public void saveAuctionStack(SerializedAuctionStack serializedAuctionStack) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			String sql = "REPLACE INTO " + prefix + "auction(stack_uuid, auction_stack) VALUES(?, ?);";
	        StringWriter sink = new StringWriter();
	        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).sink(() -> new BufferedWriter(sink)).build();
	        ConfigurationNode node = loader.createNode();
	        try {
	            node.node("Content").set(TypeTokens.AUCTIONSTACK_TOKEN, serializedAuctionStack);
				loader.save(node);
			} catch (IOException e) {
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
		});
	}

	@Override
	public void removeAuctionStack(UUID stackUUID) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			try {
				Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
				statement.execute("DELETE FROM `" + prefix + "auction` WHERE `" + prefix + "auction`.`stack_uuid` = \'" + stackUUID +"\'");
				statement.close();
			} catch (SQLException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void saveExpireAuctionData(SerializedAuctionStack serializedAuctionStack) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			String sql = "REPLACE INTO " + prefix + "auction_expired(stack_uuid, auction_stack) VALUES(?, ?);";
	        StringWriter sink = new StringWriter();
	        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).sink(() -> new BufferedWriter(sink)).build();
	        ConfigurationNode node = loader.createNode();
	        try {
	            node.node("Content").set(TypeTokens.AUCTIONSTACK_TOKEN, serializedAuctionStack);
				loader.save(node);
			} catch (IOException e) {
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
		});
	}

	@Override
	public void removeExpireAuctionData(SerializedAuctionStack serializedAuctionStack) {
		UUID stackUUID = serializedAuctionStack.getStackUUID();
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			try {
				Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
				statement.execute("DELETE FROM `" + prefix + "auction_expired` WHERE `" + prefix + "auction_expired`.`stack_uuid` = \'" + stackUUID +"\'");
				statement.close();
			} catch (SQLException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void saveExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			String sql = "REPLACE INTO " + prefix + "auction_expired_bet(stack_uuid, auction_stack) VALUES(?, ?);";
	        StringWriter sink = new StringWriter();
	        HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).sink(() -> new BufferedWriter(sink)).build();
	        ConfigurationNode node = loader.createNode();
	        try {
	            node.node("Content").set(TypeTokens.AUCTIONSTACK_TOKEN, serializedAuctionStack);
				loader.save(node);
			} catch (IOException e) {
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
		});
	}

	@Override
	public void removeExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack) {
		UUID stackUUID = serializedAuctionStack.getStackUUID();
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			try {
				Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
				statement.execute("DELETE FROM `" + prefix + "auction_expired_bet` WHERE `" + prefix + "auction_expired_bet`.`stack_uuid` = \'" + stackUUID +"\'");
				statement.close();
			} catch (SQLException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		});
	}

	private void loadActualAuctionData() {
		try {
			Statement statement = plugin.getMySQL().getOrOpenConnection().createStatement();
			ResultSet results = statement.executeQuery("SELECT * FROM " + prefix + "auction;");
			Map<UUID, SerializedAuctionStack> loaded = new HashMap<UUID, SerializedAuctionStack>();
			while(results.next()) {
				UUID stackUUID = UUID.fromString(results.getString("stack_uuid"));
				String content = results.getString("auction_stack");
	            StringReader source = new StringReader(content);
	            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).source(() -> new BufferedReader(source)).build();
	            ConfigurationNode node = loader.load();
	            SerializedAuctionStack serializedAuctionStack = node.node("Content").get(TypeTokens.AUCTIONSTACK_TOKEN);
	            serializedAuctionStack.setStackUUID(stackUUID);
	            if(serializedAuctionStack.getSerializedItemStack().getOptItemType().isPresent()) {
	            	serializedAuctionStack.setStackUUID(stackUUID);
	            	serializedAuctionStack.getPrices().forEach(price -> {
						price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyName()));
	            	});
		            loaded.put(stackUUID, serializedAuctionStack);
	            }
			}
			statement.close();
			plugin.getAuctionItems().clear();
			plugin.getAuctionItems().putAll(loaded);
		} catch (SQLException | IOException e) {
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
	            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).source(() -> new BufferedReader(source)).build();
	            ConfigurationNode node = loader.load();
	            SerializedAuctionStack serializedAuctionStack = node.node("Content").get(TypeTokens.AUCTIONSTACK_TOKEN);
	            serializedAuctionStack.getBetData().setCurrency(plugin.getEconomy().checkCurrency(serializedAuctionStack.getBetData().getCurrencyName()));
            	if(serializedAuctionStack.getSerializedItemStack().getOptItemType().isPresent()) {
            		serializedAuctionStack.getPrices().forEach(price -> {
            			price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyName()));
            		});
            		if(!loadedExpireData.containsKey(serializedAuctionStack.getOwnerUUID())) {
						List<SerializedAuctionStack> newList = new ArrayList<SerializedAuctionStack>();
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
		} catch (SQLException | IOException e) {
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
	            HoconConfigurationLoader loader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).source(() -> new BufferedReader(source)).build();
	            ConfigurationNode node = loader.load();
	            SerializedAuctionStack serializedAuctionStack = node.node("Content").get(TypeTokens.AUCTIONSTACK_TOKEN);
            	if(serializedAuctionStack.getSerializedItemStack().getOptItemType().isPresent()) {
    	            serializedAuctionStack.getBetData().setCurrency(plugin.getEconomy().checkCurrency(serializedAuctionStack.getBetData().getCurrencyName()));
            		serializedAuctionStack.getPrices().forEach(price -> {
            			price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyName()));
            		});
            		if(!loadedExpireBetData.containsKey(serializedAuctionStack.getOwnerUUID())) {
						List<SerializedAuctionStack> newList = new ArrayList<SerializedAuctionStack>();
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
		} catch (SQLException | IOException e) {
			plugin.getLogger().error("Get expired auction data");
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

}
