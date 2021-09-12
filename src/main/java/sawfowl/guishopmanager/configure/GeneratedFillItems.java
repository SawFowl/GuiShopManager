package sawfowl.guishopmanager.configure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationOptions;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.ConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.NodeResolver;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;

import net.kyori.adventure.text.Component;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.TypeTokens;
import sawfowl.localeapi.serializetools.SerializedItemStack;

public class GeneratedFillItems {

	GuiShopManager plugin;
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private CommentedConfigurationNode itemsNode;
	public GeneratedFillItems(GuiShopManager instance){
		plugin = instance;
		items = new HashMap<FillItems, ItemStack>();
		final ObjectMapper.Factory factory = ObjectMapper.factoryBuilder().addNodeResolver(NodeResolver.onlyWithSetting()).build();
		final TypeSerializerCollection child = TypeSerializerCollection.defaults().childBuilder().registerAnnotatedObjects(factory).build();
		final ConfigurationOptions options = ConfigurationOptions.defaults().serializers(child);
		configLoader = HoconConfigurationLoader.builder().defaultOptions(options).path(plugin.getConfigDir().resolve("FillItems.conf")).build();
		try {
			itemsNode = configLoader.load();
		} catch (IOException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		try {
			generate();
		} catch (IOException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
	}

	private Map<FillItems, ItemStack> items;

	public void addItem(FillItems item, ItemStack itemStack) {
		items.put(item, itemStack);
	}

	public ItemStack getItemStack(FillItems item) {
		return items.get(item).copy();
	}

	private void generate() throws IOException {
		boolean save = false;
		ItemStack basicFill;
		if(itemsNode.node("BasicFill").empty()) {
			basicFill = ItemStack.of(ItemTypes.WHITE_STAINED_GLASS_PANE);
			basicFill.offer(Keys.CUSTOM_NAME, Component.empty());
			itemsNode.node("BasicFill").set(TypeTokens.SERIALIZED_STACK_TOKEN, new SerializedItemStack(basicFill));
			save = true;
		} else {
			basicFill = itemsNode.node("BasicFill").get(TypeTokens.SERIALIZED_STACK_TOKEN).getItemStack();
		}
		
		ItemStack bottomFill;
		if(itemsNode.node("BottomFill").empty()) {
			bottomFill = ItemStack.of(ItemTypes.BLACK_STAINED_GLASS_PANE);
			bottomFill.offer(Keys.CUSTOM_NAME, Component.empty());
			itemsNode.node("BottomFill").set(TypeTokens.SERIALIZED_STACK_TOKEN, new SerializedItemStack(bottomFill));
			save = true;
		} else {
			bottomFill = itemsNode.node("BottomFill").get(TypeTokens.SERIALIZED_STACK_TOKEN).getItemStack();
		}
		
		ItemStack back;
		if(itemsNode.node("PreviousMenu").empty()) {
			back = ItemStack.of(ItemTypes.CHEST_MINECART);
			itemsNode.node("PreviousMenu").set(TypeTokens.SERIALIZED_STACK_TOKEN, new SerializedItemStack(back));
			save = true;
		} else {
			back = itemsNode.node("PreviousMenu").get(TypeTokens.SERIALIZED_STACK_TOKEN).getItemStack();
		}
		
		ItemStack next;
		if(itemsNode.node("NextMenu").empty()) {
			next = ItemStack.of(ItemTypes.CHEST_MINECART);
			itemsNode.node("NextMenu").set(TypeTokens.SERIALIZED_STACK_TOKEN, new SerializedItemStack(next));
			save = true;
		} else {
			next = itemsNode.node("NextMenu").get(TypeTokens.SERIALIZED_STACK_TOKEN).getItemStack();
		}
		
		ItemStack buy;
		if(itemsNode.node("BuyItem").empty()) {
			buy = ItemStack.of(ItemTypes.WRITABLE_BOOK);
			itemsNode.node("BuyItem").set(TypeTokens.SERIALIZED_STACK_TOKEN, new SerializedItemStack(buy));
			save = true;
		} else {
			buy = itemsNode.node("BuyItem").get(TypeTokens.SERIALIZED_STACK_TOKEN).getItemStack();
		}
		
		ItemStack sell;
		if(itemsNode.node("SellItem").empty()) {
			sell = ItemStack.of(ItemTypes.WRITABLE_BOOK);
			itemsNode.node("SellItem").set(TypeTokens.SERIALIZED_STACK_TOKEN, new SerializedItemStack(buy));
			save = true;
		} else {
			sell = itemsNode.node("SellItem").get(TypeTokens.SERIALIZED_STACK_TOKEN).getItemStack();
		}
		
		ItemStack clear;
		if(itemsNode.node("ClearItem").empty()) {
			clear = ItemStack.of(ItemTypes.PAPER);
			itemsNode.node("ClearItem").set(TypeTokens.SERIALIZED_STACK_TOKEN, new SerializedItemStack(clear));
			save = true;
		} else {
			clear = itemsNode.node("ClearItem").get(TypeTokens.SERIALIZED_STACK_TOKEN).getItemStack();
		}
		
		ItemStack changeCurrency;
		if(itemsNode.node("ChangeCurrency").empty()) {
			changeCurrency = ItemStack.of(ItemTypes.KNOWLEDGE_BOOK);
			itemsNode.node("ChangeCurrency").set(TypeTokens.SERIALIZED_STACK_TOKEN, new SerializedItemStack(changeCurrency));
			save = true;
		} else {
			changeCurrency = itemsNode.node("ChangeCurrency").get(TypeTokens.SERIALIZED_STACK_TOKEN).getItemStack();
		}
		
		ItemStack switchMode;
		if(itemsNode.node("SwitchMode").empty()) {
			switchMode = ItemStack.of(ItemTypes.COMPASS);
			itemsNode.node("SwitchMode").set(TypeTokens.SERIALIZED_STACK_TOKEN, new SerializedItemStack(switchMode));
			save = true;
		} else {
			switchMode = itemsNode.node("SwitchMode").get(TypeTokens.SERIALIZED_STACK_TOKEN).getItemStack();
		}
		
		ItemStack exit;
		if(itemsNode.node("Exit").empty()) {
			exit = ItemStack.of(ItemTypes.BARRIER);
			itemsNode.node("Exit").set(TypeTokens.SERIALIZED_STACK_TOKEN, new SerializedItemStack(exit));
			save = true;
		} else {
			exit = itemsNode.node("Exit").get(TypeTokens.SERIALIZED_STACK_TOKEN).getItemStack();
		}
		
		ItemStack auctionAdd;
		if(itemsNode.node("AuctionAddItem").empty()) {
			auctionAdd = ItemStack.of(ItemTypes.GREEN_DYE);
			itemsNode.node("AuctionAddItem").set(TypeTokens.SERIALIZED_STACK_TOKEN, new SerializedItemStack(auctionAdd));
			save = true;
		} else {
			auctionAdd = itemsNode.node("AuctionAddItem").get(TypeTokens.SERIALIZED_STACK_TOKEN).getItemStack();
		}
		
		ItemStack auctionReturn;
		if(itemsNode.node("AuctionReturnItem").empty()) {
			auctionReturn = ItemStack.of(ItemTypes.RED_DYE);
			itemsNode.node("AuctionReturnItem").set(TypeTokens.SERIALIZED_STACK_TOKEN, new SerializedItemStack(auctionReturn));
			save = true;
		} else {
			auctionReturn = itemsNode.node("AuctionReturnItem").get(TypeTokens.SERIALIZED_STACK_TOKEN).getItemStack();
		}
		
		ItemStack changePrice = null;
		if(itemsNode.node("ChangePrice").empty()) {
			Map<String, SerializedItemStack> items = new HashMap<String, SerializedItemStack>();
			for(int i = 0; i < 9; i++) {
				if(i == 0 || i == 1 || i == 2) {
					changePrice = ItemStack.of(ItemTypes.IRON_NUGGET);
				}
				if(i == 3 || i == 4 || i == 5) {
					changePrice = ItemStack.of(ItemTypes.GOLD_NUGGET);
				}
				if(i == 6 || i == 7 || i == 8) {
					changePrice = ItemStack.of(ItemTypes.DIAMOND);
				}
				addItem(FillItems.valueOf("CHANGEPRICE" + i), changePrice);
				items.put(String.valueOf(i), new SerializedItemStack(changePrice));
				itemsNode.node("ChangePrice").set(TypeTokens.MAP_SERIALIZED_STACKS_TOKEN, items);
			}
			save = true;
		} else {
			Map<String, SerializedItemStack> items = new HashMap<String, SerializedItemStack>();
			items.putAll(itemsNode.node("ChangePrice").get(TypeTokens.MAP_SERIALIZED_STACKS_TOKEN));
			for(Entry<String, SerializedItemStack> entry : items.entrySet()) {
				addItem(FillItems.valueOf("CHANGEPRICE" + entry.getKey()), entry.getValue().getItemStack());
			}
		}
		
		ItemStack changeSize = null;
		if(itemsNode.node("ChangeSize").empty()) {
			Map<String, SerializedItemStack> items = new HashMap<String, SerializedItemStack>();
			for(int i = 0; i < 9; i++) {
				if(i == 0 || i == 1 || i == 2) {
					changeSize = ItemStack.of(ItemTypes.IRON_NUGGET);
				}
				if(i == 3 || i == 4 || i == 5) {
					changeSize = ItemStack.of(ItemTypes.GOLD_NUGGET);
				}
				if(i == 6 || i == 7 || i == 8) {
					changeSize = ItemStack.of(ItemTypes.DIAMOND);
				}
				addItem(FillItems.valueOf("CHANGESIZE" + i), changeSize);
				items.put(String.valueOf(i), new SerializedItemStack(changeSize));
				itemsNode.node("ChangeSize").set(TypeTokens.MAP_SERIALIZED_STACKS_TOKEN, items);
			}
			save = true;
		} else {
			Map<String, SerializedItemStack> items = new HashMap<String, SerializedItemStack>();
			items.putAll(itemsNode.node("ChangeSize").get(TypeTokens.MAP_SERIALIZED_STACKS_TOKEN));
			for(Entry<String, SerializedItemStack> entry : items.entrySet()) {
				addItem(FillItems.valueOf("CHANGESIZE" + entry.getKey()), entry.getValue().getItemStack());
			}
		}
		addItem(FillItems.BASIC, basicFill);
		addItem(FillItems.BOTTOM, bottomFill);
		addItem(FillItems.BACK, back);
		addItem(FillItems.NEXT, next);
		addItem(FillItems.BUY, buy);
		addItem(FillItems.SELL, sell);
		addItem(FillItems.CLEAR, clear);
		addItem(FillItems.SWITCHMODE, switchMode);
		addItem(FillItems.CHANGECURRENCY, changeCurrency);
		addItem(FillItems.EXIT, exit);
		addItem(FillItems.ADD, auctionAdd);
		addItem(FillItems.RETURN, auctionReturn);
		if(save) {
			configLoader.save(itemsNode);
		}
	}
}
