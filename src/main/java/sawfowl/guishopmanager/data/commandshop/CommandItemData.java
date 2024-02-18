package sawfowl.guishopmanager.data.commandshop;

import java.util.List;

import org.spongepowered.api.item.inventory.ItemStack;

import sawfowl.guishopmanager.serialization.commandsshop.CommandsList;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.serialization.commandsshop.SerializedCommandShopPrice;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStackJsonNbt;

public class CommandItemData {

	public CommandItemData(SerializedItemStackJsonNbt itemStack, List<SerializedCommandShopPrice> prices) {
		shopStack = itemStack;
		this.prices = prices;
	}

	private SerializedItemStackJsonNbt shopStack;
	private List<SerializedCommandShopPrice> prices;

	public ItemStack getItemStack() {
		return shopStack.getItemStack();
	}
	public void setItemStack(SerializedItemStackJsonNbt shopStacks) {
		this.shopStack = shopStacks;
	}
	public List<SerializedCommandShopPrice> getPrices() {
		return prices;
	}
	public boolean isBuyForPrice(SerializedCommandShopPrice price) {
		return price.getBuyPrice().doubleValue() > 0;
	}
	public CommandsList getCommands() {
		return shopStack.getOrCreateTag().containsTag(GuiShopManager.getInstance().getPluginContainer(), "Commands") ? shopStack.getOrCreateTag().getJsonObject(GuiShopManager.getInstance().getPluginContainer(), "Commands").filter(e -> e.isJsonArray()).map(e -> new CommandsList(e.getAsJsonArray())).orElse(new CommandsList()) : new CommandsList();
	}

}
