package sawfowl.guishopmanager.utils.data;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.spongepowered.api.scheduler.Task;
import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.utils.serialization.shop.SerializedShop;

public class WorkConfigs extends WorkData {

	private GuiShopManager plugin;
	private ConfigurationLoader<CommentedConfigurationNode> auctionConfigLoader;
	private CommentedConfigurationNode auctionNode;
	private boolean isLoaded = false;
	public WorkConfigs(GuiShopManager instance) {
		plugin = instance;
		auctionConfigLoader = HoconConfigurationLoader.builder().setPath(plugin.getConfigDir().resolve("Auction.conf")).build();
		try {
			auctionNode = auctionConfigLoader.load();
		} catch (IOException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		plugin.getRootNode().getNode("Auction", "Server").getString();
	}

	@Override
	public void saveShop(String shopId) {
		Task.builder().async().execute(() -> {
			SerializedShop serializableShop = plugin.getShop(shopId).serialize();
			ConfigurationLoader<CommentedConfigurationNode> shopConfigLoader = HoconConfigurationLoader.builder().setPath(plugin.getConfigDir().resolve(plugin.getRootNode().getNode("StorageFolder").getString() + File.separator + shopId + ".conf")).build();
			try {
				CommentedConfigurationNode shopNode = shopConfigLoader.load();
				shopNode.getNode("ShopData").setValue(TypeToken.of(SerializedShop.class), serializableShop);
				shopConfigLoader.save(shopNode);
				List<String> enabledShops = new ArrayList<String>();
				if(!plugin.getRootNode().getNode("ShopList").isEmpty()) {
					try {
						enabledShops.addAll(plugin.getRootNode().getNode("ShopList").getValue(new TypeToken<List<String>>() {
							private static final long serialVersionUID = 01;}));
					} catch (ObjectMappingException e) {
						plugin.getLogger().error(e.getLocalizedMessage());
					}
				}
				if(!enabledShops.contains(shopId)) {
					enabledShops.add(shopId);
				}
				plugin.getRootNode().getNode("ShopList").setValue(new TypeToken<List<String>>() {
					private static final long serialVersionUID = 01;}, enabledShops);
				plugin.updateConfigs();
			} catch (IOException | ObjectMappingException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}).submit(plugin);
	}

	@Override
	public void loadShops() {
		Task.builder().async().execute(() -> {
			if(!plugin.getRootNode().getNode("ShopList").isEmpty()) {
				try {
					List<String> remove = new ArrayList<String>();
					List<String> enabledShops = plugin.getRootNode().getNode("ShopList").getValue(new TypeToken<List<String>>() {
						private static final long serialVersionUID = 01;});
					for(String shopId : enabledShops) {
						if((plugin.getConfigDir().resolve(plugin.getRootNode().getNode("StorageFolder").getString() + File.separator + shopId + ".conf")).toFile().exists()) {
							ConfigurationLoader<CommentedConfigurationNode> shopConfigLoader = HoconConfigurationLoader.builder().setPath(plugin.getConfigDir().resolve(plugin.getRootNode().getNode("StorageFolder").getString() + File.separator + shopId + ".conf")).build();
							CommentedConfigurationNode shopNode = shopConfigLoader.load();
							plugin.addShop(shopId, shopNode.getNode("ShopData").getValue(TypeToken.of(SerializedShop.class)).deserialize());
						} else {
							remove.add(shopId);
						}
					}
					if(!remove.isEmpty()) {
						enabledShops.removeAll(remove);
						plugin.getRootNode().getNode("ShopList").setValue(new TypeToken<List<String>>() {
							private static final long serialVersionUID = 01;}, enabledShops);
						plugin.updateConfigs();
					}
				} catch (IOException | ObjectMappingException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
		}).submit(plugin);
	}

	@Override
	public void deleteShop(String shopId) {
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
		File shopConfigFile = new File(plugin.getConfigDir() + File.separator + plugin.getRootNode().getNode("StorageFolder").getString() + File.separator + shopId + ".conf");
		if(shopConfigFile.exists()) {
			shopConfigFile.delete();
		}
	}

	@Override
	public void loadAuction() {
		Task.builder().async().execute(() -> {
			if(isLoaded) {
				return;
			}
			if(!auctionNode.getNode("ActualData").isVirtual() && !auctionNode.getNode("ActualData").isEmpty()) {
				try {
					List<SerializedAuctionStack> loaded = auctionNode.getNode("ActualData").getValue(new TypeToken<List<SerializedAuctionStack>>() {
						private static final long serialVersionUID = 01;
					});
					List<SerializedAuctionStack> toAdd = new ArrayList<SerializedAuctionStack>();
					for(SerializedAuctionStack serializedAuctionStack : loaded) {
			            if(serializedAuctionStack.getSerializedItemStack().isPresent()) {
			            	toAdd.add(serializedAuctionStack);
			            }
					}
					plugin.getAuctionItems().addAll(toAdd);
				} catch (ObjectMappingException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
			if(!auctionNode.getNode("DataExpired").isVirtual() && !auctionNode.getNode("DataExpired").isEmpty()) {
				try {
					plugin.getExpiredAuctionItems().putAll(auctionNode.getNode("DataExpired").getValue(new TypeToken<Map<UUID, List<SerializedAuctionStack>>>() {
						private static final long serialVersionUID = 01;
					}));
				} catch (ObjectMappingException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
			isLoaded  = true;
		}).submit(plugin);
	}

	@Override
	public void saveAuctionStack(SerializedAuctionStack serializedAuctionStack) {
		Task.builder().delayTicks(20).async().execute(() -> {
			try {
				auctionNode.getNode("ActualData").setValue(new TypeToken<List<SerializedAuctionStack>>() {
					private static final long serialVersionUID = 01;
				}, plugin.getAuctionItems());
				auctionConfigLoader.save(auctionNode);
			} catch (ObjectMappingException | IOException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}).submit(plugin);
	}

	@Override
	public void removeAuctionStack(UUID stackUUID) {
		saveAuctionStack(null);
	}

	@Override
	public void saveExpireAuctionData(SerializedAuctionStack serializedAuctionStack) {
		try {
			auctionNode.getNode("DataExpired").setValue(new TypeToken<Map<UUID, List<SerializedAuctionStack>>>() {
				private static final long serialVersionUID = 01;
			}, plugin.getExpiredAuctionItems());
			auctionConfigLoader.save(auctionNode);
		} catch (ObjectMappingException | IOException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void removeExpireAuctionData(UUID owner) {
		saveExpireAuctionData(null);
	}

	@Override
	public void saveExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack) {
		try {
			auctionNode.getNode("DataBetExpired").setValue(new TypeToken<Map<UUID, List<SerializedAuctionStack>>>() {
				private static final long serialVersionUID = 01;
			}, plugin.getExpiredBetAuctionItems());
			auctionConfigLoader.save(auctionNode);
		} catch (ObjectMappingException | IOException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void removeExpireBetAuctionData(UUID owner) {
		saveExpireBetAuctionData(null);
	}

}
