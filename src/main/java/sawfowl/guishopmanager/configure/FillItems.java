package sawfowl.guishopmanager.configure;

import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

@ConfigSerializable
public class FillItems {

	public FillItems(){}

	@Setting("BasicFill")
	private ItemStack basicFill = createFill(ItemStack.of(ItemTypes.WHITE_STAINED_GLASS_PANE));
	@Setting("BottomFill")
	private ItemStack bottomFill = createFill(ItemStack.of(ItemTypes.BLACK_STAINED_GLASS_PANE));
	@Setting("PreviousMenu")
	private ItemStack previousMenu = ItemStack.of(ItemTypes.CHEST_MINECART);
	@Setting("NextMenu")
	private ItemStack nextMenu = ItemStack.of(ItemTypes.CHEST_MINECART);
	@Setting("BuyItem")
	private ItemStack buyItem = ItemStack.of(ItemTypes.WRITABLE_BOOK);
	@Setting("SellItem")
	private ItemStack sellItem = ItemStack.of(ItemTypes.WRITABLE_BOOK);
	@Setting("ClearItem")
	private ItemStack clearItem = ItemStack.of(ItemTypes.PAPER);
	@Setting("ChangeCurrency")
	private ItemStack changeCurrency = ItemStack.of(ItemTypes.KNOWLEDGE_BOOK);
	@Setting("SwitchMode")
	private ItemStack switchMode = ItemStack.of(ItemTypes.COMPASS);
	@Setting("Exit")
	private ItemStack exit = ItemStack.of(ItemTypes.BARRIER);
	@Setting("AuctionAddItem")
	private ItemStack auctionAddItem = ItemStack.of(ItemTypes.GREEN_DYE);
	@Setting("AuctionReturnItem")
	private ItemStack auctionReturnItem = ItemStack.of(ItemTypes.RED_DYE);
	@Setting("ChangePrice")
	private PriceAndSizeItems changePrice = new PriceAndSizeItems();
	@Setting("ChangeSize")
	private PriceAndSizeItems changeSize = new PriceAndSizeItems();

	public ItemStack getBasicFill() {
		return basicFill;
	}

	public ItemStack getBottomFill() {
		return bottomFill;
	}

	public ItemStack getPreviousMenu() {
		return previousMenu;
	}

	public ItemStack getNextMenu() {
		return nextMenu;
	}

	public ItemStack getBuyItem() {
		return buyItem;
	}

	public ItemStack getSellItem() {
		return sellItem;
	}

	public ItemStack getClearItem() {
		return clearItem;
	}

	public ItemStack getChangeCurrency() {
		return changeCurrency;
	}

	public ItemStack getSwitchMode() {
		return switchMode;
	}

	public ItemStack getExit() {
		return exit;
	}

	public ItemStack getAuctionAddItem() {
		return auctionAddItem;
	}

	public ItemStack getAuctionReturnItem() {
		return auctionReturnItem;
	}

	public PriceAndSizeItems getChangePrice() {
		return changePrice;
	}

	public PriceAndSizeItems getChangeSize() {
		return changeSize;
	}

	private ItemStack createFill(ItemStack basicFill) {
		basicFill.offer(Keys.CUSTOM_NAME, Component.empty());
		return basicFill;
	}

}
