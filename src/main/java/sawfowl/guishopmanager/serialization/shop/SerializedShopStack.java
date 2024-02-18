package sawfowl.guishopmanager.serialization.shop;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStackJsonNbt;

import java.io.Serializable;
import java.util.List;

@ConfigSerializable
public class SerializedShopStack implements Serializable {

	SerializedShopStack(){}
	public SerializedShopStack(int slot, ItemStack itemStack, List<SerializedShopPrice> serializedShopPrices) {
		this.slot = slot;
		this.itemStack = new SerializedItemStackJsonNbt(itemStack);
		this.serializedShopPrices = serializedShopPrices;
	}

	private static final long serialVersionUID = 01;

	@Setting("Slot")
	private int slot;
	@Setting("Prices")
	private List<SerializedShopPrice> serializedShopPrices;
	@Setting("ItemStack")
	private SerializedItemStackJsonNbt itemStack;

	public SerializedItemStackJsonNbt getSerializedItemStack() {
		return itemStack;
	}

	public List<SerializedShopPrice> getSerializedShopPrices() {
		return serializedShopPrices;
	}

	public int getSlot() {
		return slot;
	}

	@Override
	public String toString() {
		return  "Slot: " + slot +
				"; Prices: " + serializedShopPrices +
				"; ItemStack: " + itemStack;
	}

}