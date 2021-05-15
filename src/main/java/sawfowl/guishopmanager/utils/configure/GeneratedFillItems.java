package sawfowl.guishopmanager.utils.configure;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.DyeColors;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.text.Text;

import com.google.common.reflect.TypeToken;

import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.utils.serialization.SerializedItemStack;

public class GeneratedFillItems {

	GuiShopManager plugin;
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;
	private CommentedConfigurationNode itemsNode;
	public GeneratedFillItems(GuiShopManager instance){
		plugin = instance;
		items = new HashMap<FillItems, ItemStack>();
		configLoader = HoconConfigurationLoader.builder().setPath(plugin.getConfigDir().resolve("FillItems.conf")).build();
		try {
			itemsNode = configLoader.load();
		} catch (IOException e) {
			plugin.getLogger().error(e.getLocalizedMessage());
		}
		try {
			generate();
		} catch (ObjectMappingException | IOException e) {
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

	private void generate() throws ObjectMappingException, IOException {
		boolean save = false;
		ItemStack basicFill;
		if(itemsNode.getNode("BasicFill").isEmpty()) {
			basicFill = ItemStack.of(ItemTypes.STAINED_GLASS_PANE);
			basicFill.offer(Keys.DISPLAY_NAME, Text.EMPTY);
			basicFill.offer(Keys.DYE_COLOR, DyeColors.WHITE);
			itemsNode.getNode("BasicFill").setValue(TypeToken.of(SerializedItemStack.class), new SerializedItemStack(basicFill));
			save = true;
		} else {
			basicFill = itemsNode.getNode("BasicFill").getValue(TypeToken.of(SerializedItemStack.class)).getItemStack();
		}
		
		ItemStack bottomFill;
		if(itemsNode.getNode("BottomFill").isEmpty()) {
			bottomFill = ItemStack.of(ItemTypes.STAINED_GLASS_PANE);
			bottomFill.offer(Keys.DISPLAY_NAME, Text.EMPTY);
			bottomFill.offer(Keys.DYE_COLOR, DyeColors.BLACK);
			itemsNode.getNode("BottomFill").setValue(TypeToken.of(SerializedItemStack.class), new SerializedItemStack(bottomFill));
			save = true;
		} else {
			bottomFill = itemsNode.getNode("BottomFill").getValue(TypeToken.of(SerializedItemStack.class)).getItemStack();
		}
		
		ItemStack back;
		if(itemsNode.getNode("PreviousMenu").isEmpty()) {
			back = ItemStack.of(ItemTypes.CHEST_MINECART);
			itemsNode.getNode("PreviousMenu").setValue(TypeToken.of(SerializedItemStack.class), new SerializedItemStack(back));
			save = true;
		} else {
			back = itemsNode.getNode("PreviousMenu").getValue(TypeToken.of(SerializedItemStack.class)).getItemStack();
		}
		
		ItemStack next;
		if(itemsNode.getNode("NextMenu").isEmpty()) {
			next = ItemStack.of(ItemTypes.CHEST_MINECART);
			itemsNode.getNode("NextMenu").setValue(TypeToken.of(SerializedItemStack.class), new SerializedItemStack(next));
			save = true;
		} else {
			next = itemsNode.getNode("NextMenu").getValue(TypeToken.of(SerializedItemStack.class)).getItemStack();
		}
		
		ItemStack buy;
		if(itemsNode.getNode("BuyItem").isEmpty()) {
			buy = ItemStack.of(ItemTypes.WRITABLE_BOOK);
			itemsNode.getNode("BuyItem").setValue(TypeToken.of(SerializedItemStack.class), new SerializedItemStack(buy));
			save = true;
		} else {
			buy = itemsNode.getNode("BuyItem").getValue(TypeToken.of(SerializedItemStack.class)).getItemStack();
		}
		
		ItemStack sell;
		if(itemsNode.getNode("SellItem").isEmpty()) {
			sell = ItemStack.of(ItemTypes.WRITABLE_BOOK);
			itemsNode.getNode("SellItem").setValue(TypeToken.of(SerializedItemStack.class), new SerializedItemStack(buy));
			save = true;
		} else {
			sell = itemsNode.getNode("SellItem").getValue(TypeToken.of(SerializedItemStack.class)).getItemStack();
		}
		
		ItemStack clear;
		if(itemsNode.getNode("ClearItem").isEmpty()) {
			clear = ItemStack.of(ItemTypes.PAPER);
			itemsNode.getNode("ClearItem").setValue(TypeToken.of(SerializedItemStack.class), new SerializedItemStack(clear));
			save = true;
		} else {
			clear = itemsNode.getNode("ClearItem").getValue(TypeToken.of(SerializedItemStack.class)).getItemStack();
		}
		
		ItemStack changeCurrency;
		if(itemsNode.getNode("ChangeCurrency").isEmpty()) {
			changeCurrency = ItemStack.of(ItemTypes.KNOWLEDGE_BOOK);
			itemsNode.getNode("ChangeCurrency").setValue(TypeToken.of(SerializedItemStack.class), new SerializedItemStack(changeCurrency));
			save = true;
		} else {
			changeCurrency = itemsNode.getNode("ChangeCurrency").getValue(TypeToken.of(SerializedItemStack.class)).getItemStack();
		}
		
		ItemStack switchMode;
		if(itemsNode.getNode("SwitchMode").isEmpty()) {
			switchMode = ItemStack.of(ItemTypes.COMPASS);
			itemsNode.getNode("SwitchMode").setValue(TypeToken.of(SerializedItemStack.class), new SerializedItemStack(switchMode));
			save = true;
		} else {
			switchMode = itemsNode.getNode("SwitchMode").getValue(TypeToken.of(SerializedItemStack.class)).getItemStack();
		}
		
		ItemStack exit;
		if(itemsNode.getNode("Exit").isEmpty()) {
			exit = ItemStack.of(ItemTypes.BARRIER);
			itemsNode.getNode("Exit").setValue(TypeToken.of(SerializedItemStack.class), new SerializedItemStack(exit));
			save = true;
		} else {
			exit = itemsNode.getNode("Exit").getValue(TypeToken.of(SerializedItemStack.class)).getItemStack();
		}
		
		ItemStack auctionAdd;
		if(itemsNode.getNode("AuctionAddItem").isEmpty()) {
			auctionAdd = ItemStack.of(ItemTypes.DYE);
			auctionAdd.offer(Keys.DYE_COLOR, DyeColors.GREEN);
			itemsNode.getNode("AuctionAddItem").setValue(TypeToken.of(SerializedItemStack.class), new SerializedItemStack(auctionAdd));
			save = true;
		} else {
			auctionAdd = itemsNode.getNode("AuctionAddItem").getValue(TypeToken.of(SerializedItemStack.class)).getItemStack();
		}
		
		ItemStack auctionReturn;
		if(itemsNode.getNode("AuctionReturnItem").isEmpty()) {
			auctionReturn = ItemStack.of(ItemTypes.DYE);
			auctionReturn.offer(Keys.DYE_COLOR, DyeColors.RED);
			itemsNode.getNode("AuctionReturnItem").setValue(TypeToken.of(SerializedItemStack.class), new SerializedItemStack(auctionReturn));
			save = true;
		} else {
			auctionReturn = itemsNode.getNode("AuctionReturnItem").getValue(TypeToken.of(SerializedItemStack.class)).getItemStack();
		}
		
		ItemStack changePrice = null;
		if(itemsNode.getNode("ChangePrice").isEmpty()) {
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
				itemsNode.getNode("ChangePrice").setValue(new TypeToken<Map<String, SerializedItemStack>>() {
					private static final long serialVersionUID = 1L;
				}, items);
			}
			save = true;
		} else {
			Map<String, SerializedItemStack> items = new HashMap<String, SerializedItemStack>();
			items.putAll(itemsNode.getNode("ChangePrice").getValue(new TypeToken<Map<String, SerializedItemStack>>() {
					private static final long serialVersionUID = 1L;
				}));
			for(Entry<String, SerializedItemStack> entry : items.entrySet()) {
				addItem(FillItems.valueOf("CHANGEPRICE" + entry.getKey()), entry.getValue().getItemStack());
			}
		}
		
		ItemStack changeSize = null;
		if(itemsNode.getNode("ChangeSize").isEmpty()) {
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
				itemsNode.getNode("ChangeSize").setValue(new TypeToken<Map<String, SerializedItemStack>>() {
					private static final long serialVersionUID = 1L;
				}, items);
			}
			save = true;
		} else {
			Map<String, SerializedItemStack> items = new HashMap<String, SerializedItemStack>();
			items.putAll(itemsNode.getNode("ChangeSize").getValue(new TypeToken<Map<String, SerializedItemStack>>() {
					private static final long serialVersionUID = 1L;
				}));
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
