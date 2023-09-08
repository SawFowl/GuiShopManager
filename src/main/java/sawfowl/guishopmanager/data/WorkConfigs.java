package sawfowl.guishopmanager.data;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurateException;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.serialize.SerializationException;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.TypeTokens;
import sawfowl.guishopmanager.data.commandshop.CommandItemData;
import sawfowl.guishopmanager.data.commandshop.CommandShopData;
import sawfowl.guishopmanager.data.commandshop.CommandShopMenuData;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.data.shop.ShopItem;
import sawfowl.guishopmanager.data.shop.ShopMenuData;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShop;
import sawfowl.guishopmanager.serialization.shop.SerializedShop;

public class WorkConfigs extends WorkData {

	private GuiShopManager plugin;
	private ConfigurationLoader<CommentedConfigurationNode> auctionConfigLoader;
	private CommentedConfigurationNode auctionNode;
	private boolean isLoaded = false;
	public WorkConfigs(GuiShopManager instance) {
		plugin = instance;
		auctionConfigLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).path(plugin.getConfigDir().resolve("Auction.conf")).build();
		try {
			auctionNode = auctionConfigLoader.load();
		} catch (IOException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		plugin.getRootNode().node("Auction", "Server").getString();
	}

	@Override
	public void saveShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			SerializedShop serializableShop = plugin.getShop(shopId).serialize();
			ConfigurationLoader<CommentedConfigurationNode> shopConfigLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).path(plugin.getConfigDir().resolve(plugin.getRootNode().node("StorageFolders", "Shops").getString() + File.separator + shopId + ".conf")).build();
			try {
				CommentedConfigurationNode shopNode = shopConfigLoader.load();
				shopNode.node("ShopData").set(TypeTokens.SHOP_TOKEN, serializableShop);
				shopConfigLoader.save(shopNode);
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
			for(File shopFile : Arrays.stream(shopsFolder.listFiles()).filter(file -> (file.getName().contains(".conf"))).collect(Collectors.toList())) {
				ConfigurationLoader<CommentedConfigurationNode> shopConfigLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).file(shopFile).build();
				try {
					CommentedConfigurationNode shopNode = shopConfigLoader.load();
					Shop shop = shopNode.node("ShopData").get(TypeTokens.SHOP_TOKEN).deserialize();
					String shopId = shop.getID();
					plugin.addShop(shopId, shop);
					for(ShopMenuData shopMenuData : plugin.getShop(shopId).getMenus().values()) {
						for(ShopItem shopItem : shopMenuData.getItems().values()) shopItem.getPrices().forEach(price -> {
							price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyName()));
						});
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
			Arrays.stream(shopsFolder.listFiles()).filter(file -> (file.getName().equals(shopId + ".conf"))).findFirst().ifPresent(file -> {
				file.delete();
			});
		});
	}

	@Override
	public void saveCommandsShop(String shopId) {
		Sponge.asyncScheduler().executor(plugin.getPluginContainer()).execute(() -> {
			SerializedCommandShop serializableShop = plugin.getCommandShopData(shopId).serialize();
			ConfigurationLoader<CommentedConfigurationNode> shopConfigLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).path(plugin.getConfigDir().resolve(plugin.getRootNode().node("StorageFolders", "CommandsShops").getString() + File.separator + shopId + ".conf")).build();
			try {
				CommentedConfigurationNode shopNode = shopConfigLoader.load();
				shopNode.node("ShopData").set(TypeTokens.COMMANDS_SHOP_TOKEN, serializableShop);
				shopConfigLoader.save(shopNode);
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
			for(File shopFile : Arrays.stream(shopsFolder.listFiles()).filter(file -> (file.getName().contains(".conf"))).collect(Collectors.toList())) {
				ConfigurationLoader<CommentedConfigurationNode> shopConfigLoader = HoconConfigurationLoader.builder().defaultOptions(plugin.getLocaleAPI().getConfigurationOptions()).file(shopFile).build();
				try {
					CommentedConfigurationNode shopNode = shopConfigLoader.load();
					CommandShopData shop = shopNode.node("ShopData").get(TypeTokens.COMMANDS_SHOP_TOKEN).deserialize();
					String shopId = shop.getID();
					plugin.addCommandShopData(shopId, shop);
					for(CommandShopMenuData shopMenuData : plugin.getCommandShopData(shopId).getMenus().values()) {
						for(CommandItemData shopItem : shopMenuData.getItems().values()) {
							shopItem.getPrices().forEach(price -> {
								price.setCurrency(plugin.getEconomy().checkCurrency(price.getCurrencyName()));
							});
						}
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
			Arrays.stream(shopsFolder.listFiles()).filter(file -> (file.getName().equals(shopId + ".conf"))).findFirst().ifPresent(file -> {
				file.delete();
			});
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
					for(List<SerializedAuctionStack> auctionStacks : plugin.getExpiredAuctionItems().values()) {
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
					for(List<SerializedAuctionStack> auctionStacks : plugin.getExpiredBetAuctionItems().values()) {
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

}
