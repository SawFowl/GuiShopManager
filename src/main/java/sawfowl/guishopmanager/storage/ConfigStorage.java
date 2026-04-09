package sawfowl.guishopmanager.storage;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.serialize.SerializationException;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.TypeTokens;
import sawfowl.localeapi.api.ConfigTypes;
import sawfowl.localeapi.api.config.Config;
import sawfowl.localeapi.api.serializetools.ItemStackSerializerType;
import sawfowl.localeapi.api.services.ConfigurationService;
import sawfowl.guishopmanager.data.commandshop.CommandShopData;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShop;
import sawfowl.guishopmanager.serialization.shop.SerializedShop;

public class ConfigStorage implements DataStorage {

	private GuiShopManager plugin;
	private Config auctionConfig;
	private boolean isLoaded = false;
	public ConfigStorage(GuiShopManager instance) {
		plugin = instance;
		auctionConfig = ConfigurationService.getInstance().createSimpleConfig(plugin.getPluginContainer()).setPath(plugin.getConfigDir()).setName("Auction").setType(ConfigTypes.HOCON).setItemStackSerializerType(ItemStackSerializerType.JSON).build();
	}

	@Override
	public void saveShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			ConfigurationService.getInstance()
				.createReferencedConfig(plugin.getPluginContainer(), plugin.getShop(shopId).serialize())
				.setPath(plugin.getConfigDir().resolve(plugin.getConfig().getStorageFolders().getShops()))
				.setName(shopId)
				.setType(ConfigTypes.HOCON)
				.setItemStackSerializerType(ItemStackSerializerType.JSON)
				.build();
		});
	}

	@Override
	public void loadShops() {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			File shopsFolder = plugin.getConfigDir().resolve(plugin.getConfig().getStorageFolders().getShops()).toFile();
			if(!shopsFolder.exists()) return;
			for(File shopFile : shopsFolder.listFiles()) {
				if(!ConfigTypes.isValidExtension(ConfigTypes.getExtension(shopFile.getName()))) continue;
				SerializedShop serializedShop = ConfigurationService.getInstance()
					.createReferencedConfig(plugin.getPluginContainer(), SerializedShop.class)
					.fromFile(shopFile)
					.setItemStackSerializerType(ItemStackSerializerType.JSON)
					.build().get();
				if(serializedShop == null) continue;
				Shop shop = serializedShop.deserialize();
				setShopCurrencies(plugin, shop);
				plugin.addShop(shop.getID(), shop);
			}
		});
	}

	@Override
	public void deleteShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			File shopsFolder = plugin.getConfigDir().resolve(plugin.getConfig().getStorageFolders().getShops()).toFile();
			if(!shopsFolder.exists()) return;
			Arrays.stream(shopsFolder.listFiles()).filter(file -> (file.getName().equals(shopId + ".conf") || file.getName().equals(shopId + ".json") || file.getName().equals(shopId + ".yml"))).forEach(File::delete);
		});
	}

	@Override
	public void saveCommandsShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			ConfigurationService.getInstance()
				.createReferencedConfig(plugin.getPluginContainer(), plugin.getCommandShopData(shopId).serialize())
				.setPath(plugin.getConfigDir().resolve(plugin.getConfig().getStorageFolders().getShops()))
				.setName(shopId)
				.setType(ConfigTypes.HOCON)
				.setItemStackSerializerType(ItemStackSerializerType.JSON)
				.build();
		});
	}

	@Override
	public void loadCommandsShops() {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			File shopsFolder = plugin.getConfigDir().resolve(plugin.getConfig().getStorageFolders().getCommandsShops()).toFile();
			if(!shopsFolder.exists()) return;
			for(File shopFile : shopsFolder.listFiles()) {
				if(!ConfigTypes.isValidExtension(ConfigTypes.getExtension(shopFile.getName()))) continue;
				SerializedCommandShop serializedCommandShop = ConfigurationService.getInstance()
					.createReferencedConfig(plugin.getPluginContainer(), SerializedCommandShop.class)
					.fromFile(shopFile)
					.setItemStackSerializerType(ItemStackSerializerType.JSON)
					.build()
					.get();
				if(serializedCommandShop == null) continue;
				CommandShopData shop = serializedCommandShop.deserialize();
				setCommandShopCurrencies(plugin, shop);
				String shopId = shop.getID();
				plugin.addCommandShopData(shopId, shop);
			}
		});
	}

	@Override
	public void deleteCommandsShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			File shopsFolder = plugin.getConfigDir().resolve(plugin.getConfig().getStorageFolders().getCommandsShops()).toFile();
			if(!shopsFolder.exists()) return;
			Arrays.stream(shopsFolder.listFiles()).filter(file -> (file.getName().equals(shopId + ".conf") || file.getName().equals(shopId + ".json") || file.getName().equals(shopId + ".yml"))).forEach(File::delete);
		});
	}

	@Override
	public void loadAuction() {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			if(isLoaded) {
				return;
			}
			if(!auctionConfig.getRootNode().node("ActualData").virtual() && !auctionConfig.getRootNode().node("ActualData").empty() && !auctionConfig.getRootNode().node("ActualData").childrenMap().isEmpty()) {
				Map<UUID, SerializedAuctionStack> toAdd = new HashMap<UUID, SerializedAuctionStack>();
				auctionConfig.getRootNode().node("ActualData").childrenMap().forEach((object, node) -> {
					try {
						SerializedAuctionStack auctionStack = node.get(TypeTokens.AUCTIONSTACK_TOKEN);
						setAuctionCurrencies(plugin, auctionStack);
						toAdd.put(auctionStack.getStackUUID(), auctionStack);
					} catch (SerializationException e) {
						plugin.getLogger().error(e.getLocalizedMessage());
					}
				});
				plugin.getAuctionItems().putAll(toAdd);
			}
			if(!auctionConfig.getRootNode().node("ExpiredData").virtual() && !auctionConfig.getRootNode().node("ExpiredData").empty()) {
				try {
					plugin.getExpiredAuctionItems().putAll(auctionConfig.getRootNode().node("ExpiredData").get(TypeTokens.MAP_EXPIRED_AUCTIONSTACKS_TOKEN));
					for(Set<SerializedAuctionStack> auctionStacks : plugin.getExpiredAuctionItems().values()) {
						for(SerializedAuctionStack auctionStack : auctionStacks) setAuctionCurrencies(plugin, auctionStack);
					}
				} catch (SerializationException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
			if(!auctionConfig.getRootNode().node("ExpiredDataBet").virtual() && !auctionConfig.getRootNode().node("ExpiredDataBet").empty()) {
				try {
					plugin.getExpiredBetAuctionItems().putAll(auctionConfig.getRootNode().node("ExpiredDataBet").get(TypeTokens.MAP_EXPIRED_AUCTIONSTACKS_TOKEN));
					for(Set<SerializedAuctionStack> auctionStacks : plugin.getExpiredBetAuctionItems().values()) {
						for(SerializedAuctionStack auctionStack : auctionStacks) setAuctionCurrencies(plugin, auctionStack);
					}
				} catch (SerializationException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
			isLoaded  = true;
		});
	}

	@Override
	public void saveAuctionStack(SerializedAuctionStack serializedAuctionStack) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			try {
				auctionConfig.getRootNode().node("ActualData", serializedAuctionStack.getStackUUID().toString()).set(TypeTokens.AUCTIONSTACK_TOKEN, serializedAuctionStack);
				auctionConfig.save();
			} catch (SerializationException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void removeAuctionStack(UUID stackUUID) {
		if(auctionConfig.getRootNode().node("ActualData").hasChild(stackUUID.toString())) {
			auctionConfig.getRootNode().node("ActualData").removeChild(stackUUID.toString());
			auctionConfig.save();
		}
	}

	@Override
	public void saveExpireAuctionData(SerializedAuctionStack serializedAuctionStack) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			try {
				auctionConfig.getRootNode().node("ExpiredData", serializedAuctionStack.getOwnerUUID().toString()).set(TypeTokens.LIST_AUCTIONSTACK_TOKEN, plugin.getExpiredAuctionItems().get(serializedAuctionStack.getOwnerUUID()));
				auctionConfig.save();
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void removeExpireAuctionData(SerializedAuctionStack serializedAuctionStack) {
		UUID owner = serializedAuctionStack.getOwnerUUID();
		if(auctionConfig.getRootNode().node("ExpiredData").hasChild(owner.toString())) {
			try {
				auctionConfig.getRootNode().node("ExpiredData", owner.toString()).set(TypeTokens.LIST_AUCTIONSTACK_TOKEN, plugin.getExpiredAuctionItems().get(owner));
				if(auctionConfig.getRootNode().node("ExpiredData", owner.toString()).empty()) {
					auctionConfig.getRootNode().node("ExpiredData").removeChild(owner.toString());
				}
				auctionConfig.save();
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void saveExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack) {
		try {
			auctionConfig.getRootNode().node("ExpiredDataBet", serializedAuctionStack.getOwnerUUID().toString()).set(TypeTokens.LIST_AUCTIONSTACK_TOKEN, plugin.getExpiredBetAuctionItems().get(serializedAuctionStack.getOwnerUUID()));
			auctionConfig.save();
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void removeExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack) {
		UUID owner = serializedAuctionStack.getOwnerUUID();
		if(auctionConfig.getRootNode().node("ExpiredDataBet").hasChild(owner.toString())) {
			try {
				auctionConfig.getRootNode().node("ExpiredDataBet", owner.toString()).set(TypeTokens.LIST_AUCTIONSTACK_TOKEN, plugin.getExpiredBetAuctionItems().get(owner));
				if(auctionConfig.getRootNode().node("ExpiredDataBet", owner.toString()).empty()) {
					auctionConfig.getRootNode().node("ExpiredDataBet").removeChild(owner.toString());
				}
				auctionConfig.save();
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}
	}

}
