package sawfowl.guishopmanager.utils.configure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.TypeTokens;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.serialization.SerializedItemStack;

public class GenerateConfig {

	private GuiShopManager plugin;
	private boolean save = false;
	private boolean saveBlackList = false;
	public GenerateConfig(GuiShopManager instance) {
		plugin = instance;
		generate();
		generateBlackList();
	}

	private void generate() {
		check(getNode("SplitStorage"), "Data storage methods. MySQL used if true.", null, null);
		check(getNode("SplitStorage", "Auction"), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("SplitStorage", "Shops"), null, false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("MySQLStorage"), "Data storage method.", false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("StorageFolder"), "This is used if \"MySQLStorage\" != \"true\"", "shops", TypeTokens.STRING_TOKEN);
		check(getNode("Debug"), "Debug messages.", false, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("PlayerTransactionMessage"), "Message to players on successful purchase/sale.", true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("ShopList"), "List of active shops.", new ArrayList<String>(), TypeTokens.LIST_STRING_VALUE_TOKEN);
		check(getNode("Aliases", "ShopOpen"), "Aliases for the \"/gsm open\" command.", null, null);
		check(getNode("Aliases", "ShopOpen", "Enable"), null, true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("Aliases", "ShopOpen", "List"), null, Arrays.asList("shop"), TypeTokens.LIST_STRING_VALUE_TOKEN);
		check(getNode("Aliases", "Auction"), "Aliases for the \"/gsm auction\" command.", null, null);
		check(getNode("Aliases", "Auction", "Enable"), null, true, TypeTokens.BOOLEAN_TOKEN);
		check(getNode("Aliases", "Auction", "List"), null, Arrays.asList("auction", "market"), TypeTokens.LIST_STRING_VALUE_TOKEN);
		check(getNode("MySQL"), "This is used if \"MySQLStorage\" == \"true\"", null, null);
		check(getNode("MySQL", "Host"), null, "localhost", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "Port"), null, "3306", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "DataBase"), null, "guishopmanager", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "Prefix"), null, "srv_node1_", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "User"), null, "user", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "Password"), null, "UNSET", TypeTokens.STRING_TOKEN);
		check(getNode("MySQL", "SSL"), null, "false", TypeTokens.STRING_TOKEN);
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

	private void generateBlackList() {
		checkBlackList(getBlackListNode("MasksList"), "List of masks. The id of mods or id of items without specifying the mod are applied, as well as the id of the type minecraft:item.", Arrays.asList("dirt", "minecraft:barrier"), TypeTokens.LIST_STRING_VALUE_TOKEN);
		checkBlackList(getBlackListNode("StacksList"), "List of items.", Arrays.asList(new SerializedItemStack(ItemStack.of(ItemTypes.BEDROCK)), new SerializedItemStack(ItemStack.of(ItemTypes.COBBLESTONE))), new TypeToken<List<SerializedItemStack>>() {
			private static final long serialVersionUID = 01;
		});
		if(saveBlackList) {
			plugin.updateConfigs();
		}
		try {
			plugin.setBlackListMasks(getBlackListNode("MasksList").getValue(new TypeToken<List<String>>() {
				private static final long serialVersionUID = 01;
			}));
			plugin.setBlackListStacks(getBlackListNode("StacksList").getValue(new TypeToken<List<SerializedItemStack>>() {
				private static final long serialVersionUID = 01;
			}));
		} catch (ObjectMappingException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	private CommentedConfigurationNode getNode(Object... node) {
		return plugin.getRootNode().getNode(node);
	}
	private CommentedConfigurationNode getBlackListNode(Object... node) {
		return plugin.getBlackListNode().getNode(node);
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void check(CommentedConfigurationNode node, String comment, Object value, TypeToken typeToken) {
        if (node.isVirtual()) {
        	save = true;
        	if(comment != null) {
            	node.setComment(comment);
        	}
        	if(value != null) {
                try {
    				node.setValue(typeToken, value);
    			} catch (ObjectMappingException e) {
    				plugin.getLogger().error(e.getLocalizedMessage());
    			}
        	}
        }
    }
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void checkBlackList(CommentedConfigurationNode node, String comment, Object value, TypeToken typeToken) {
        if (node.isVirtual()) {
        	saveBlackList = true;
        	if(comment != null) {
            	node.setComment(comment);
        	}
        	if(value != null) {
                try {
    				node.setValue(typeToken, value);
    			} catch (ObjectMappingException e) {
    				plugin.getLogger().error(e.getLocalizedMessage());
    			}
        	}
        }
    }

}
