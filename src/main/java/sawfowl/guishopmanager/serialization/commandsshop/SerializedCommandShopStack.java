package sawfowl.guishopmanager.serialization.commandsshop;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStackJsonNbt;

import java.io.Serializable;
import java.util.List;

@ConfigSerializable
public class SerializedCommandShopStack implements Serializable {

	SerializedCommandShopStack(){}
	public SerializedCommandShopStack(int slot, ItemStack itemStack, List<SerializedCommandShopPrice> serializedShopPrices) {
		this.slot = slot;
		this.serializedItemStack = new SerializedItemStackJsonNbt(itemStack);
		this.serializedShopPrices = serializedShopPrices;
	}

	private static final long serialVersionUID = 01;

	@Setting("Slot")
	private int slot;
	@Setting("Prices")
	private List<SerializedCommandShopPrice> serializedShopPrices;
	@Setting("ItemStack")
	private SerializedItemStackJsonNbt serializedItemStack;

	public SerializedItemStackJsonNbt getSerializedItemStack() {
		return serializedItemStack;
	}

	public List<SerializedCommandShopPrice> getSerializedShopPrices() {
		return serializedShopPrices;
	}

	public int getSlot() {
		return slot;
	}

	public CommandsList getCommands() {
		return serializedItemStack.getOrCreateTag().containsTag(GuiShopManager.getInstance().getPluginContainer(), "Commands") ? serializedItemStack.getOrCreateTag().getJsonObject(GuiShopManager.getInstance().getPluginContainer(), "Commands").filter(e -> e.isJsonArray()).map(e -> new CommandsList(e.getAsJsonArray())).orElse(new CommandsList()) : new CommandsList();
	}

	@Override
	public String toString() {
		return  "Slot: " + slot +
				"; Prices: " + serializedShopPrices +
				"; ItemStack: " + serializedItemStack;
	}

}