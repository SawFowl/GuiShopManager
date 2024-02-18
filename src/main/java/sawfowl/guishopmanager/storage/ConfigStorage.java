package sawfowl.guishopmanager.storage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.BasicConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import com.google.common.io.Files;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.TypeTokens;
import sawfowl.localeapi.api.ConfigTypes;
import sawfowl.localeapi.api.serializetools.SerializeOptions;
import sawfowl.guishopmanager.data.commandshop.CommandItemData;
import sawfowl.guishopmanager.data.commandshop.CommandShopData;
import sawfowl.guishopmanager.data.commandshop.CommandShopMenuData;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.data.shop.ShopItem;
import sawfowl.guishopmanager.data.shop.ShopMenuData;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShop;
import sawfowl.guishopmanager.serialization.shop.SerializedShop;

public class ConfigStorage implements DataStorage {

	private GuiShopManager plugin;
	private ConfigurationLoader<?> auctionConfigLoader;
	private ConfigurationNode auctionNode;
	private boolean isLoaded = false;
	public ConfigStorage(GuiShopManager instance) {
		plugin = instance;
		Optional<File> auctionConfig = Stream.of(plugin.getConfigDir().toFile().listFiles()).filter(file -> file.getName().contains("Auction")).findFirst();
		if(auctionConfig.isPresent()) {
			auctionConfigLoader = createConfigLoader(auctionConfig.get(), 2);
		} else auctionConfigLoader = createConfigLoader(plugin.getConfigDir().resolve("Auction" + plugin.getRootNode().node("ConfigTypes", "Auction").getString()), plugin.getRootNode().node("ConfigTypes", "Auction").getString(), 2);
		try {
			auctionNode = auctionConfigLoader.load();
			if(!plugin.getRootNode().node("ConfigTypes", "Auction").getString().equals("." + Files.getFileExtension(auctionConfig.get().getName()))) {
				BasicConfigurationNode copy = BasicConfigurationNode.root().from(auctionNode);
				auctionConfigLoader = createConfigLoader(plugin.getConfigDir().resolve("Auction" + plugin.getRootNode().node("ConfigTypes", "Auction").getString()), plugin.getRootNode().node("ConfigTypes", "Auction").getString(), 2);
				auctionConfigLoader.save(copy);
				auctionNode = auctionConfigLoader.load();
				auctionConfig.get().delete();
				auctionConfig = null;
				copy = null;
			}
		} catch (IOException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		plugin.getRootNode().node("Auction", "Server").getString();
	}

	@Override
	public void saveShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			try {
				createConfigLoader(plugin.getConfigDir().resolve(plugin.getRootNode().node("StorageFolders", "Shops").getString() + File.separator + shopId + plugin.getRootNode().node("ConfigTypes", "Shop").getString()), plugin.getRootNode().node("ConfigTypes", "Shop").getString(), 2).loadToReference().referenceTo(SerializedShop.class).setAndSave(plugin.getShop(shopId).serialize());
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void loadShops() {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			File shopsFolder = plugin.getConfigDir().resolve(plugin.getRootNode().node("StorageFolders", "Shops").getString()).toFile();
			if(!shopsFolder.exists()) return;
			for(File shopFile : Arrays.stream(shopsFolder.listFiles()).filter(file -> (file.getName().endsWith(".conf") || file.getName().endsWith(".json") || file.getName().endsWith(".yml"))).collect(Collectors.toList())) {
				try {
					Shop shop = createConfigLoader(shopFile, 2).loadToReference().referenceTo(SerializedShop.class).get().deserialize();
					plugin.addShop(shop.getID(), shop);
					for(ShopMenuData shopMenuData : plugin.getShop(shop.getID()).getMenus().values()) {
						for(ShopItem shopItem : shopMenuData.getItems().values()) shopItem.getPrices().forEach(price -> {
							price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyName()));
						});
					}
					if(!plugin.getRootNode().node("ConfigTypes", "Shop").getString().equals("." + Files.getFileExtension(shopFile.getName()))) {
						shopFile.delete();
						saveShop(shop.getID());
					}
				} catch (ConfigurateException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
				
			}
		});
	}

	@Override
	public void deleteShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			File shopsFolder = plugin.getConfigDir().resolve(plugin.getRootNode().node("StorageFolders", "Shops").getString()).toFile();
			if(!shopsFolder.exists()) return;
			Arrays.stream(shopsFolder.listFiles()).filter(file -> (file.getName().equals(shopId + ".conf") || file.getName().equals(shopId + ".json") || file.getName().equals(shopId + ".yml"))).forEach(File::delete);
		});
	}

	@Override
	public void saveCommandsShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			try {
				createConfigLoader(plugin.getConfigDir().resolve(plugin.getRootNode().node("StorageFolders", "CommandsShops").getString() + File.separator + shopId + plugin.getRootNode().node("ConfigTypes", "CommandShop").getString()), plugin.getRootNode().node("ConfigTypes", "CommandShop").getString(), 2).loadToReference().referenceTo(SerializedCommandShop.class).setAndSave(plugin.getCommandShopData(shopId).serialize());
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void loadCommandsShops() {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			File shopsFolder = plugin.getConfigDir().resolve(plugin.getRootNode().node("StorageFolders", "CommandsShops").getString()).toFile();
			if(!shopsFolder.exists()) return;
			for(File shopFile : Arrays.stream(shopsFolder.listFiles()).filter(file -> (file.getName().endsWith(".conf") || file.getName().endsWith(".json") || file.getName().endsWith(".yml"))).collect(Collectors.toList())) {
				try {
					CommandShopData shop = createConfigLoader(shopFile, 2).loadToReference().referenceTo(SerializedCommandShop.class).get().deserialize();
					String shopId = shop.getID();
					plugin.addCommandShopData(shopId, shop);
					for(CommandShopMenuData shopMenuData : plugin.getCommandShopData(shopId).getMenus().values()) {
						for(CommandItemData shopItem : shopMenuData.getItems().values()) {
							shopItem.getPrices().forEach(price -> {
								price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyName()));
							});
						}
					}
					if(!plugin.getRootNode().node("ConfigTypes", "CommandShop").getString().equals("." + Files.getFileExtension(shopFile.getName()))) {
						shopFile.delete();
						saveCommandsShop(shop.getID());
					}
				} catch (ConfigurateException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
		});
	}

	@Override
	public void deleteCommandsShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			File shopsFolder = plugin.getConfigDir().resolve(plugin.getRootNode().node("StorageFolders", "CommandsShops").getString()).toFile();
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
			if(!auctionNode.node("ActualData").virtual() && !auctionNode.node("ActualData").empty() && !auctionNode.node("ActualData").childrenMap().isEmpty()) {
				Map<UUID, SerializedAuctionStack> toAdd = new HashMap<UUID, SerializedAuctionStack>();
				auctionNode.node("ActualData").childrenMap().forEach((object, node) -> {
					try {
						SerializedAuctionStack auctionStack = node.get(TypeTokens.AUCTIONSTACK_TOKEN);
						auctionStack.getPrices().forEach(price -> {
							price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyName()));
						});
						if(auctionStack.getBetData() != null) auctionStack.getBetData().setCurrency(plugin.getEconomy().checkCurrency(auctionStack.getBetData().getCurrencyName()));
						toAdd.put(auctionStack.getStackUUID(), auctionStack);
					} catch (SerializationException e) {
						plugin.getLogger().error(e.getLocalizedMessage());
					}
				});
				plugin.getAuctionItems().putAll(toAdd);
			}
			if(!auctionNode.node("ExpiredData").virtual() && !auctionNode.node("ExpiredData").empty()) {
				try {
					plugin.getExpiredAuctionItems().putAll(auctionNode.node("ExpiredData").get(TypeTokens.MAP_EXPIRED_AUCTIONSTACKS_TOKEN));
					for(Set<SerializedAuctionStack> auctionStacks : plugin.getExpiredAuctionItems().values()) {
						for(SerializedAuctionStack auctionStack : auctionStacks) {
							auctionStack.getBetData().setCurrency(plugin.getEconomy().checkCurrency(auctionStack.getBetData().getCurrencyName()));
							auctionStack.getPrices().forEach(price -> {
								price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyName()));
							});
						}
					}
				} catch (SerializationException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
			if(!auctionNode.node("ExpiredDataBet").virtual() && !auctionNode.node("ExpiredDataBet").empty()) {
				try {
					plugin.getExpiredBetAuctionItems().putAll(auctionNode.node("ExpiredDataBet").get(TypeTokens.MAP_EXPIRED_AUCTIONSTACKS_TOKEN));
					for(Set<SerializedAuctionStack> auctionStacks : plugin.getExpiredBetAuctionItems().values()) {
						for(SerializedAuctionStack auctionStack : auctionStacks) {
							auctionStack.getBetData().setCurrency(plugin.getEconomy().checkCurrency(auctionStack.getBetData().getCurrencyName()));
							auctionStack.getPrices().forEach(price -> {
								price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyName()));
							});
						}
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
				auctionNode.node("ActualData", serializedAuctionStack.getStackUUID().toString()).set(TypeTokens.AUCTIONSTACK_TOKEN, serializedAuctionStack);
			} catch (SerializationException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
			try {
				auctionConfigLoader.save(auctionNode);
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void removeAuctionStack(UUID stackUUID) {
		if(auctionNode.node("ActualData").hasChild(stackUUID.toString())) {
			auctionNode.node("ActualData").removeChild(stackUUID.toString());
			try {
				auctionConfigLoader.save(auctionNode);
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void saveExpireAuctionData(SerializedAuctionStack serializedAuctionStack) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			try {
				auctionNode.node("ExpiredData", serializedAuctionStack.getOwnerUUID().toString()).set(TypeTokens.LIST_AUCTIONSTACK_TOKEN, plugin.getExpiredAuctionItems().get(serializedAuctionStack.getOwnerUUID()));
				auctionConfigLoader.save(auctionNode);
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		});
	}

	@Override
	public void removeExpireAuctionData(SerializedAuctionStack serializedAuctionStack) {
		UUID owner = serializedAuctionStack.getOwnerUUID();
		if(auctionNode.node("ExpiredData").hasChild(owner.toString())) {
			try {
				auctionNode.node("ExpiredData", owner.toString()).set(TypeTokens.LIST_AUCTIONSTACK_TOKEN, plugin.getExpiredAuctionItems().get(owner));
				if(auctionNode.node("ExpiredData", owner.toString()).empty()) {
					auctionNode.node("ExpiredData").removeChild(owner.toString());
				}
				auctionConfigLoader.save(auctionNode);
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}
	}

	@Override
	public void saveExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack) {
		try {
			auctionNode.node("ExpiredDataBet", serializedAuctionStack.getOwnerUUID().toString()).set(TypeTokens.LIST_AUCTIONSTACK_TOKEN, plugin.getExpiredBetAuctionItems().get(serializedAuctionStack.getOwnerUUID()));
			auctionConfigLoader.save(auctionNode);
		} catch (ConfigurateException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	@Override
	public void removeExpireBetAuctionData(SerializedAuctionStack serializedAuctionStack) {
		UUID owner = serializedAuctionStack.getOwnerUUID();
		if(auctionNode.node("ExpiredDataBet").hasChild(owner.toString())) {
			try {
				auctionNode.node("ExpiredDataBet", owner.toString()).set(TypeTokens.LIST_AUCTIONSTACK_TOKEN, plugin.getExpiredBetAuctionItems().get(owner));
				if(auctionNode.node("ExpiredDataBet", owner.toString()).empty()) {
					auctionNode.node("ExpiredDataBet").removeChild(owner.toString());
				}
				auctionConfigLoader.save(auctionNode);
			} catch (ConfigurateException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
		}
	}

	private ConfigTypes getConfigType(String string) {
		return Stream.of(ConfigTypes.values()).filter(t -> t.toString().equals(string)).findFirst().orElse(ConfigTypes.HOCON);
	}

	private ConfigurationLoader<? extends ConfigurationNode> createConfigLoader(Path path, String configType, int itemStackSerializerVariant) {
		switch (getConfigType(configType)) {
			case HOCON: return SerializeOptions.createHoconConfigurationLoader(itemStackSerializerVariant).path(path).build();
			case YAML: return SerializeOptions.createYamlConfigurationLoader(itemStackSerializerVariant).path(path).build();
			case JSON: return SerializeOptions.createJsonConfigurationLoader(itemStackSerializerVariant).path(path).build();
			default: return SerializeOptions.createHoconConfigurationLoader(itemStackSerializerVariant).path(path).build();
		}
	}

	private ConfigurationLoader<? extends ConfigurationNode> createConfigLoader(File file, int itemStackSerializerVariant) {
		switch (getConfigType("." + Files.getFileExtension(file.getName()))) {
			case HOCON: return SerializeOptions.createHoconConfigurationLoader(itemStackSerializerVariant).file(file).build();
			case YAML: return SerializeOptions.createYamlConfigurationLoader(itemStackSerializerVariant).file(file).build();
			case JSON: return SerializeOptions.createJsonConfigurationLoader(itemStackSerializerVariant).file(file).build();
			default: return SerializeOptions.createHoconConfigurationLoader(itemStackSerializerVariant).file(file).build();
		}
	}

}
