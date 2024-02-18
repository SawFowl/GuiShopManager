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
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStackJsonNbt;

public class GeneratedFillItems {

	GuiShopManager plugin;
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private CommentedConfigurationNode itemsNode;
	private Map<FillItems, SerializedItemStackJsonNbt> items;
	public GeneratedFillItems(GuiShopManager instance){
		plugin = instance;
		items = new HashMap<FillItems, SerializedItemStackJsonNbt>();
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
		items.put(item, new SerializedItemStackJsonNbt(itemStack));
	}

	public ItemStack getItemStack(FillItems item) {
		return items.get(item).getItemStack();
	}

	private void generate() throws IOException {
		boolean save = false;
		ItemStack basicFill;
		if(itemsNode.node("BasicFill").empty()) {
			basicFill = ItemStack.of(ItemTypes.WHITE_STAINED_GLASS_PANE);
			basicFill.offer(Keys.CUSTOM_NAME, Component.empty());
			itemsNode.node("BasicFill").set(new SerializedItemStackJsonNbt(basicFill));
			save = true;
		} else {
			basicFill = itemsNode.node("BasicFill").get(SerializedItemStackJsonNbt.class).getItemStack();
		}
		
		ItemStack bottomFill;
		if(itemsNode.node("BottomFill").empty()) {
			bottomFill = ItemStack.of(ItemTypes.BLACK_STAINED_GLASS_PANE);
			bottomFill.offer(Keys.CUSTOM_NAME, Component.empty());
			itemsNode.node("BottomFill").set(new SerializedItemStackJsonNbt(bottomFill));
			save = true;
		} else {
			bottomFill = itemsNode.node("BottomFill").get(SerializedItemStackJsonNbt.class).getItemStack();
		}
		
		ItemStack back;
		if(itemsNode.node("PreviousMenu").empty()) {
			back = ItemStack.of(ItemTypes.CHEST_MINECART);
			itemsNode.node("PreviousMenu").set(new SerializedItemStackJsonNbt(back));
			save = true;
		} else {
			back = itemsNode.node("PreviousMenu").get(SerializedItemStackJsonNbt.class).getItemStack();
		}
		
		ItemStack next;
		if(itemsNode.node("NextMenu").empty()) {
			next = ItemStack.of(ItemTypes.CHEST_MINECART);
			itemsNode.node("NextMenu").set(new SerializedItemStackJsonNbt(next));
			save = true;
		} else {
			next = itemsNode.node("NextMenu").get(SerializedItemStackJsonNbt.class).getItemStack();
		}
		
		ItemStack buy;
		if(itemsNode.node("BuyItem").empty()) {
			buy = ItemStack.of(ItemTypes.WRITABLE_BOOK);
			itemsNode.node("BuyItem").set(new SerializedItemStackJsonNbt(buy));
			save = true;
		} else {
			buy = itemsNode.node("BuyItem").get(SerializedItemStackJsonNbt.class).getItemStack();
		}
		
		ItemStack sell;
		if(itemsNode.node("SellItem").empty()) {
			sell = ItemStack.of(ItemTypes.WRITABLE_BOOK);
			itemsNode.node("SellItem").set(new SerializedItemStackJsonNbt(sell));
			save = true;
		} else {
			sell = itemsNode.node("SellItem").get(SerializedItemStackJsonNbt.class).getItemStack();
		}
		
		ItemStack clear;
		if(itemsNode.node("ClearItem").empty()) {
			clear = ItemStack.of(ItemTypes.PAPER);
			itemsNode.node("ClearItem").set(new SerializedItemStackJsonNbt(clear));
			save = true;
		} else {
			clear = itemsNode.node("ClearItem").get(SerializedItemStackJsonNbt.class).getItemStack();
		}
		
		ItemStack changeCurrency;
		if(itemsNode.node("ChangeCurrency").empty()) {
			changeCurrency = ItemStack.of(ItemTypes.KNOWLEDGE_BOOK);
			itemsNode.node("ChangeCurrency").set(new SerializedItemStackJsonNbt(changeCurrency));
			save = true;
		} else {
			changeCurrency = itemsNode.node("ChangeCurrency").get(SerializedItemStackJsonNbt.class).getItemStack();
		}
		
		ItemStack switchMode;
		if(itemsNode.node("SwitchMode").empty()) {
			switchMode = ItemStack.of(ItemTypes.COMPASS);
			itemsNode.node("SwitchMode").set(new SerializedItemStackJsonNbt(switchMode));
			save = true;
		} else {
			switchMode = itemsNode.node("SwitchMode").get(SerializedItemStackJsonNbt.class).getItemStack();
		}
		
		ItemStack exit;
		if(itemsNode.node("Exit").empty()) {
			exit = ItemStack.of(ItemTypes.BARRIER);
			itemsNode.node("Exit").set(new SerializedItemStackJsonNbt(exit));
			save = true;
		} else {
			exit = itemsNode.node("Exit").get(SerializedItemStackJsonNbt.class).getItemStack();
		}
		
		ItemStack auctionAdd;
		if(itemsNode.node("AuctionAddItem").empty()) {
			auctionAdd = ItemStack.of(ItemTypes.GREEN_DYE);
			itemsNode.node("AuctionAddItem").set(new SerializedItemStackJsonNbt(auctionAdd));
			save = true;
		} else {
			auctionAdd = itemsNode.node("AuctionAddItem").get(SerializedItemStackJsonNbt.class).getItemStack();
		}
		
		ItemStack auctionReturn;
		if(itemsNode.node("AuctionReturnItem").empty()) {
			auctionReturn = ItemStack.of(ItemTypes.RED_DYE);
			itemsNode.node("AuctionReturnItem").set(new SerializedItemStackJsonNbt(auctionReturn));
			save = true;
		} else {
			auctionReturn = itemsNode.node("AuctionReturnItem").get(SerializedItemStackJsonNbt.class).getItemStack();
		}
		
		SerializedItemStackJsonNbt changePrice = null;
		if(itemsNode.node("ChangePrice").empty()) {
			Map<String, SerializedItemStackJsonNbt> items = new HashMap<String, SerializedItemStackJsonNbt>();
			for(int i = 0; i < 9; i++) {
				if(i == 0 || i == 1 || i == 2) {
					changePrice = new SerializedItemStackJsonNbt(ItemStack.of(ItemTypes.IRON_NUGGET));
				}
				if(i == 3 || i == 4 || i == 5) {
					changePrice = new SerializedItemStackJsonNbt(ItemStack.of(ItemTypes.GOLD_NUGGET));
				}
				if(i == 6 || i == 7 || i == 8) {
					changePrice = new SerializedItemStackJsonNbt(ItemStack.of(ItemTypes.DIAMOND));
				}
				this.items.put(FillItems.valueOf("CHANGEPRICE" + i), changePrice);
				items.put(String.valueOf(i), changePrice);
				itemsNode.node("ChangePrice").set(TypeTokens.MAP_JSON_ITEMSTACK_TOKEN, items);
			}
			save = true;
		} else {
			Map<String, SerializedItemStackJsonNbt> items = new HashMap<String, SerializedItemStackJsonNbt>();
			items.putAll(itemsNode.node("ChangePrice").get(TypeTokens.MAP_JSON_ITEMSTACK_TOKEN));
			for(Entry<String, SerializedItemStackJsonNbt> entry : items.entrySet()) {
				this.items.put(FillItems.valueOf("CHANGEPRICE" + entry.getKey()), entry.getValue());
			}
		}
		
		SerializedItemStackJsonNbt changeSize = null;
		if(itemsNode.node("ChangeSize").empty()) {
			Map<String, SerializedItemStackJsonNbt> items = new HashMap<String, SerializedItemStackJsonNbt>();
			for(int i = 0; i < 9; i++) {
				if(i == 0 || i == 1 || i == 2) {
					changeSize = new SerializedItemStackJsonNbt(ItemStack.of(ItemTypes.IRON_NUGGET));
				}
				if(i == 3 || i == 4 || i == 5) {
					changeSize = new SerializedItemStackJsonNbt(ItemStack.of(ItemTypes.GOLD_NUGGET));
				}
				if(i == 6 || i == 7 || i == 8) {
					changeSize = new SerializedItemStackJsonNbt(ItemStack.of(ItemTypes.DIAMOND));
				}
				this.items.put(FillItems.valueOf("CHANGESIZE" + i), changeSize);
				items.put(String.valueOf(i), changeSize);
				itemsNode.node("ChangeSize").set(TypeTokens.MAP_JSON_ITEMSTACK_TOKEN, items);
			}
			save = true;
		} else {
			Map<String, SerializedItemStackJsonNbt> items = new HashMap<String, SerializedItemStackJsonNbt>();
			items.putAll(itemsNode.node("ChangeSize").get(TypeTokens.MAP_JSON_ITEMSTACK_TOKEN));
			for(Entry<String, SerializedItemStackJsonNbt> entry : items.entrySet()) {
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
		if(save) configLoader.save(itemsNode);
	}
}
