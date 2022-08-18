package sawfowl.guishopmanager.serialization.commandsshop;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.localeapi.serializetools.CompoundTag;
import sawfowl.localeapi.serializetools.SerializedItemStack;

import java.io.Serializable;
import java.util.List;

@ConfigSerializable
public class SerializedCommandShopStack implements Serializable {

	SerializedCommandShopStack(){}
	public SerializedCommandShopStack(int slot, SerializedItemStack serializableItemStack, List<SerializedCommandShopPrice> serializedShopPrices) {
		this.slot = slot;
		this.serializedItemStack = serializableItemStack;
		this.serializedShopPrices = serializedShopPrices;
	}

	private static final long serialVersionUID = 01;

	@Setting("Slot")
	private int slot;
	@Setting("Prices")
	private List<SerializedCommandShopPrice> serializedShopPrices;
	@Setting("ItemStack")
	private SerializedItemStack serializedItemStack;

	public SerializedItemStack getSerializedItemStack() {
		return serializedItemStack;
	}

	public List<SerializedCommandShopPrice> getSerializedShopPrices() {
		return serializedShopPrices;
	}

	public int getSlot() {
		return slot;
	}

	public SerializedCommandsList getCommands() {
		return serializedItemStack.getOrCreateTag().containsTag("guishopmanager") ? (SerializedCommandsList) serializedItemStack.getOrCreateTag().getTag("guishopmanager", CompoundTag.getClass(SerializedCommandsList.class)).get() : new SerializedCommandsList();
	}

	@Override
	public String toString() {
		return  "Slot: " + slot +
				"; Prices: " + serializedShopPrices +
				"; ItemStack: " + serializedItemStack;
	}

}