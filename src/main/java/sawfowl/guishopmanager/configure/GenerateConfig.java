package sawfowl.guishopmanager.configure;

import java.util.Arrays;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.serialize.SerializationException;

import io.leangen.geantyref.TypeToken;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.TypeTokens;
import sawfowl.localeapi.serializetools.SerializedItemStack;

public class GenerateConfig {

	private GuiShopManager plugin;
	private boolean save = false;
	private boolean saveBlackList = false;
	public GenerateConfig(GuiShopManager instance) {
		plugin = instance;
		generate();
	}

	private void generate() {
		check(getNode("SplitStorage"), "Data storage methods. MySQL used if true.", null, null);
		check(getNode("SplitStorage", "Auction"), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("SplitStorage", "Shops"), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("SplitStorage", "CommandsShops"), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("SplitStorage", "Enable"), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("StorageFolders"), "This is used if you do not have MySQL enabled.", null, null);
		check(getNode("StorageFolders", "Shops"), null, "shops", TypeTokens.STRING_TOKEN);
		check(getNode("StorageFolders", "CommandsShops"), null, "commands", TypeTokens.STRING_TOKEN);
		check(getNode("Debug"), "Debug messages.", false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("PlayerTransactionMessage"), "Message to players on successful purchase/sale.", true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("Aliases", "ShopOpen"), "Aliases for the \"/gsm open\" command.", null, null);
		check(getNode("Aliases", "ShopOpen", "Enable"), null, true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("Aliases", "ShopOpen", "List"), null, Arrays.asList("shop"), TypeTokens.LIST_STRINGS_TOKEN);
		check(getNode("Aliases", "Auction"), "Aliases for the \"/gsm auction\" command.", null, null);
		check(getNode("Aliases", "Auction", "Enable"), null, true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("Aliases", "Auction", "List"), null, Arrays.asList("auction", "market"), TypeTokens.LIST_STRINGS_TOKEN);
		check(getNode("MySQL"), "", null, null);
		check(getNode("MySQL", "Host"), null, "localhost", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "Port"), null, "3306", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "DataBase"), null, "guishopmanager", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "Prefix"), null, "srv_node1_", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "User"), null, "user", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "Password"), null, "UNSET", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "SSL"), null, "false", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "Enable"), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("Auction", "Enable"), null, true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("Auction", "NbtLimit"), "Limit characters in NBT tag.", 1000, TypeTokens.INTEGER_TOKEN);
		check(getNode("Auction", "Server"), "The name of the server on which the return of the item to the player will be available. Use different names on different servers.", "ServerNumberOne", TypeTokens.STRING_TOKEN);
		check(getNode("Auction", "Expire", "1", "Time"), "Time until the item is removed from sale in minutes. The number of these sections can be increased.", 720, TypeTokens.INTEGER_TOKEN);
		check(getNode("Auction", "Expire", "2", "Time"), null, 1440, TypeTokens.INTEGER_TOKEN);
		check(getNode("Auction", "Expire", "3", "Time"), null, 2160, TypeTokens.INTEGER_TOKEN);
		check(getNode("Auction", "Expire", "1", "Tax"), "Income tax.", null, null);
		check(getNode("Auction", "Expire", "1", "Tax", "Enable"), null, true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("Auction", "Expire", "1", "Tax", "Size"), "The size of the commission from the sale. Max 100.", 5, TypeTokens.DOUBLE_TOKEN);
		check(getNode("Auction", "Expire", "2", "Tax", "Enable"), null, true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("Auction", "Expire", "2", "Tax", "Size"), null, 10, TypeTokens.DOUBLE_TOKEN);
		check(getNode("Auction", "Expire", "3", "Tax", "Enable"), null, true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("Auction", "Expire", "3", "Tax", "Size"), null, 15, TypeTokens.DOUBLE_TOKEN);
		check(getNode("Auction", "Expire", "1", "Fee"), "Payment for setting an item for sale.", null, null);
		check(getNode("Auction", "Expire", "1", "Fee", "Enable"), null, true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("Auction", "Expire", "1", "Fee", "Size"), "The amount required to list an item for sale.", 0.05, TypeTokens.DOUBLE_TOKEN);
		check(getNode("Auction", "Expire", "2", "Fee", "Enable"), null, true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("Auction", "Expire", "2", "Fee", "Size"), null, 0.15, TypeTokens.DOUBLE_TOKEN);
		check(getNode("Auction", "Expire", "3", "Fee", "Enable"), null, true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("Auction", "Expire", "3", "Fee", "Size"), null, 0.25, TypeTokens.DOUBLE_TOKEN);
		if(save) {
			plugin.updateConfigs();
		}
	}

	public void generateBlackList() {
		checkBlackList(getBlackListNode("MasksList"), "List of masks. The id of mods or id of items without specifying the mod are applied, as well as the id of the type minecraft:item.", Arrays.asList("dirt", "minecraft:barrier"), TypeTokens.LIST_STRINGS_TOKEN);
		checkBlackList(getBlackListNode("StacksList"), "List of items.", Arrays.asList(new SerializedItemStack(ItemStack.of(ItemTypes.BEDROCK)), new SerializedItemStack(ItemStack.of(ItemTypes.COBBLESTONE))), TypeTokens.LIST_SERIALIZED_STACKS_TOKEN);
		if(saveBlackList) {
			plugin.updateConfigs();
		}
			try {
				plugin.setBlackListMasks(getBlackListNode("MasksList").get(TypeTokens.LIST_STRINGS_TOKEN));
				plugin.setBlackListStacks(getBlackListNode("StacksList").get(TypeTokens.LIST_SERIALIZED_STACKS_TOKEN));
			} catch (SerializationException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
	}

	private CommentedConfigurationNode getNode(Object... node) {
		return plugin.getRootNode().node(node);
	}
	private CommentedConfigurationNode getBlackListNode(Object... node) {
		return plugin.getBlackListNode().node(node);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void check(CommentedConfigurationNode node, String comment, Object value, TypeToken typeToken) {
		if (node.virtual()) {
			save = true;
			if(comment != null) {
				node.comment(comment);
			}
			if(value != null) {
				try {
					node.set(typeToken, value);
				} catch (SerializationException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
		}
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void checkBlackList(CommentedConfigurationNode node, String comment, Object value, TypeToken typeToken) {
		if (node.virtual()) {
			saveBlackList = true;
			if(comment != null) {
				node.comment(comment);
			}
			if(value != null) {
				try {
					node.set(typeToken, value);
				} catch (SerializationException e) {
					plugin.getLogger().error(e.getLocalizedMessage());
				}
			}
		}
	}

}
