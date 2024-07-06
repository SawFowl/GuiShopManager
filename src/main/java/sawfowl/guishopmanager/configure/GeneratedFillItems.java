package sawfowl.guishopmanager.configure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.loader.ConfigurationLoader;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.TypeTokens;
import sawfowl.localeapi.api.serializetools.SerializeOptions;

public class GeneratedFillItems {

	GuiShopManager plugin;
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private CommentedConfigurationNode itemsNode;
	private Map<FillItems, ItemStack> items;
	public GeneratedFillItems(GuiShopManager instance){
		plugin = instance;
		items = new HashMap<FillItems, ItemStack>();
		configLoader = SerializeOptions.createHoconConfigurationLoader(2).path(plugin.getConfigDir().resolve("FillItems.conf")).build();
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

	public void addItem(FillItems item, ItemStack itemStack) {
		items.put(item, itemStack);
	}

	public ItemStack getItemStack(FillItems item) {
		return items.get(item);
	}

	private void generate() throws IOException {
		boolean save = false;
		ItemStack basicFill;
		if(itemsNode.node("BasicFill").empty()) {
			basicFill = ItemStack.of(ItemTypes.WHITE_STAINED_GLASS_PANE);
			basicFill.offer(Keys.CUSTOM_NAME, Component.empty());
			itemsNode.node("BasicFill").set(basicFill);
			save = true;
		} else {
			basicFill = itemsNode.node("BasicFill").get(ItemStack.class);
		}
		
		ItemStack bottomFill;
		if(itemsNode.node("BottomFill").empty()) {
			bottomFill = ItemStack.of(ItemTypes.BLACK_STAINED_GLASS_PANE);
			bottomFill.offer(Keys.CUSTOM_NAME, Component.empty());
			itemsNode.node("BottomFill").set(bottomFill);
			save = true;
		} else {
			bottomFill = itemsNode.node("BottomFill").get(ItemStack.class);
		}
		
		ItemStack back;
		if(itemsNode.node("PreviousMenu").empty()) {
			back = ItemStack.of(ItemTypes.CHEST_MINECART);
			itemsNode.node("PreviousMenu").set(back);
			save = true;
		} else {
			back = itemsNode.node("PreviousMenu").get(ItemStack.class);
		}
		
		ItemStack next;
		if(itemsNode.node("NextMenu").empty()) {
			next = ItemStack.of(ItemTypes.CHEST_MINECART);
			itemsNode.node("NextMenu").set(next);
			save = true;
		} else {
			next = itemsNode.node("NextMenu").get(ItemStack.class);
		}
		
		ItemStack buy;
		if(itemsNode.node("BuyItem").empty()) {
			buy = ItemStack.of(ItemTypes.WRITABLE_BOOK);
			itemsNode.node("BuyItem").set(buy);
			save = true;
		} else {
			buy = itemsNode.node("BuyItem").get(ItemStack.class);
		}
		
		ItemStack sell;
		if(itemsNode.node("SellItem").empty()) {
			sell = ItemStack.of(ItemTypes.WRITABLE_BOOK);
			itemsNode.node("SellItem").set(sell);
			save = true;
		} else {
			sell = itemsNode.node("SellItem").get(ItemStack.class);
		}
		
		ItemStack clear;
		if(itemsNode.node("ClearItem").empty()) {
			clear = ItemStack.of(ItemTypes.PAPER);
			itemsNode.node("ClearItem").set(clear);
			save = true;
		} else {
			clear = itemsNode.node("ClearItem").get(ItemStack.class);
		}
		
		ItemStack changeCurrency;
		if(itemsNode.node("ChangeCurrency").empty()) {
			changeCurrency = ItemStack.of(ItemTypes.KNOWLEDGE_BOOK);
			itemsNode.node("ChangeCurrency").set(changeCurrency);
			save = true;
		} else {
			changeCurrency = itemsNode.node("ChangeCurrency").get(ItemStack.class);
		}
		
		ItemStack switchMode;
		if(itemsNode.node("SwitchMode").empty()) {
			switchMode = ItemStack.of(ItemTypes.COMPASS);
			itemsNode.node("SwitchMode").set(switchMode);
			save = true;
		} else {
			switchMode = itemsNode.node("SwitchMode").get(ItemStack.class);
		}
		
		ItemStack exit;
		if(itemsNode.node("Exit").empty()) {
			exit = ItemStack.of(ItemTypes.BARRIER);
			itemsNode.node("Exit").set(exit);
			save = true;
		} else {
			exit = itemsNode.node("Exit").get(ItemStack.class);
		}
		
		ItemStack auctionAdd;
		if(itemsNode.node("AuctionAddItem").empty()) {
			auctionAdd = ItemStack.of(ItemTypes.GREEN_DYE);
			itemsNode.node("AuctionAddItem").set(auctionAdd);
			save = true;
		} else {
			auctionAdd = itemsNode.node("AuctionAddItem").get(ItemStack.class);
		}
		
		ItemStack auctionReturn;
		if(itemsNode.node("AuctionReturnItem").empty()) {
			auctionReturn = ItemStack.of(ItemTypes.RED_DYE);
			itemsNode.node("AuctionReturnItem").set(auctionReturn);
			save = true;
		} else {
			auctionReturn = itemsNode.node("AuctionReturnItem").get(ItemStack.class);
		}
		
		ItemStack changePrice = null;
		if(itemsNode.node("ChangePrice").empty()) {
			Map<String, ItemStack> items = new HashMap<String, ItemStack>();
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
				this.items.put(FillItems.valueOf("CHANGEPRICE" + i), changePrice);
				items.put(String.valueOf(i), changePrice);
				itemsNode.node("ChangePrice").set(TypeTokens.MAP_ITEMSTACK_TOKEN, items);
			}
			save = true;
		} else {
			Map<String, ItemStack> items = new HashMap<String, ItemStack>();
			items.putAll(itemsNode.node("ChangePrice").get(TypeTokens.MAP_ITEMSTACK_TOKEN));
			for(Entry<String, ItemStack> entry : items.entrySet()) {
				this.items.put(FillItems.valueOf("CHANGEPRICE" + entry.getKey()), entry.getValue());
			}
		}
		
		ItemStack changeSize = null;
		if(itemsNode.node("ChangeSize").empty()) {
			Map<String, ItemStack> items = new HashMap<String, ItemStack>();
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
				this.items.put(FillItems.valueOf("CHANGESIZE" + i), changeSize);
				items.put(String.valueOf(i), changeSize);
				itemsNode.node("ChangeSize").set(TypeTokens.MAP_ITEMSTACK_TOKEN, items);
			}
			save = true;
		} else {
			Map<String, ItemStack> items = new HashMap<String, ItemStack>();
			items.putAll(itemsNode.node("ChangeSize").get(TypeTokens.MAP_ITEMSTACK_TOKEN));
			for(Entry<String, ItemStack> entry : items.entrySet()) {
				addItem(FillItems.valueOf("CHANGESIZE" + entry.getKey()), entry.getValue());
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
		if(save) configLoader.save(itemsNode);
	}
}
