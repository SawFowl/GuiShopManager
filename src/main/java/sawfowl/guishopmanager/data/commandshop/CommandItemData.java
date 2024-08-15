package sawfowl.guishopmanager.data.commandshop;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.item.inventory.ItemStack;

import sawfowl.guishopmanager.serialization.commandsshop.CommandsList;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShopPrice;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStack;

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
	public CommandsList getCommands() {
		return shopStack.getOrCreateComponent().containsComponent(GuiShopManager.getInstance().getPluginContainer(), "Commands") ? new CommandsList(shopStack.getOrCreateComponent().getObjectsList(String.class, GuiShopManager.getInstance().getPluginContainer(), "Commands", new ArrayList<>())) : new CommandsList();
	}

}
