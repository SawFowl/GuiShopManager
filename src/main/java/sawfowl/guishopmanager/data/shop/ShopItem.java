package sawfowl.guishopmanager.data.shop;

import java.util.List;

import org.spongepowered.api.item.inventory.ItemStack;

import sawfowl.guishopmanager.serialization.shop.SerializedShopPrice;

public class ShopItem {

	public ShopItem(ItemStack itemStack, List<SerializedShopPrice> prices) {
		shopStack = itemStack;
		this.prices = prices;
	}

	private ItemStack shopStack;
	private List<SerializedShopPrice> prices;

	public ItemStack getItemStack() {
		return shopStack.copy();
	}
	public void setItemStack(ItemStack shopStacks) {
		this.shopStack = shopStacks;
	}
	public List<SerializedShopPrice> getPrices() {
		return prices;
	}
	public boolean isBuy() {
		boolean value = false;
		for(SerializedShopPrice price : prices) {
			if(isBuyForPrice(price)) {
				value = true;
				break;
			}
		}
		return value;
	}
	public boolean isSell() {
		boolean value = false;
		for(SerializedShopPrice price : prices) {
			if(isSellForPrice(price)) {
				value = true;
				break;
			}
		}
		return value;
	}
	public boolean isBuyForPrice(SerializedShopPrice price) {
		return price.getBuyPrice().doubleValue() > 0;
	}
	public boolean isSellForPrice(SerializedShopPrice price) {
		return price.getSellPrice().doubleValue() > 0;
	}

}
