package sawfowl.guishopmanager.data.commandshop;

import java.util.List;

import org.spongepowered.api.item.inventory.ItemStack;

import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandsList;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShopPrice;
import sawfowl.localeapi.serializetools.CompoundTag;
import sawfowl.localeapi.serializetools.SerializedItemStack;

public class CommandItemData {

	public CommandItemData(SerializedItemStack itemStack, List<SerializedCommandShopPrice> prices) {
		shopStack = itemStack;
		this.prices = prices;
	}

	private SerializedItemStack shopStack;
	private List<SerializedCommandShopPrice> prices;

	public ItemStack getItemStack() {
		return shopStack.getItemStack();
	}
	public void setItemStack(SerializedItemStack shopStacks) {
		this.shopStack = shopStacks;
	}
	public List<SerializedCommandShopPrice> getPrices() {
		return prices;
	}
	public boolean isBuyForPrice(SerializedCommandShopPrice price) {
		return price.getBuyPrice().doubleValue() > 0;
	}
	public SerializedCommandsList getCommands() {
		return shopStack.getOrCreateTag().containsTag("guishopmanager") ? (SerializedCommandsList) shopStack.getOrCreateTag().getTag("guishopmanager", CompoundTag.getClass(SerializedCommandsList.class)).get() : new SerializedCommandsList();
	}

}
