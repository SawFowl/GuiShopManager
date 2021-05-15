package sawfowl.guishopmanager.utils.serialization.shop;

import java.io.Serializable;
import java.util.List;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;
import sawfowl.guishopmanager.utils.serialization.SerializedItemStack;

@ConfigSerializable
public class SerializedShopStack implements Serializable {

	SerializedShopStack(){}
	public SerializedShopStack(int slot, SerializedItemStack serializableItemStack, List<SerializedShopPrice> serializedShopPrices){
		this.slot = slot;
		this.serializedItemStack = serializableItemStack;
		this.serializedShopPrices = serializedShopPrices;
	}

	private static final long serialVersionUID = 01;

	@Setting("Slot")
	private int slot;
	@Setting("Prices")
	private List<SerializedShopPrice> serializedShopPrices;
	@Setting("ItemStack")
	private SerializedItemStack serializedItemStack;

	public SerializedItemStack getSerializedItemStack() {
		return serializedItemStack;
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
				"; ItemStack: " + serializedItemStack;
	}

}