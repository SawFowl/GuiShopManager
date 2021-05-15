package sawfowl.guishopmanager.utils.serialization;

import java.io.Serializable;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.common.item.inventory.util.ItemStackUtil;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class SerializedItemStack implements Serializable {

	SerializedItemStack(){}

	public SerializedItemStack(ItemStack itemStack) {
		itemType = itemStack.getType().getName();
		itemQuantity = itemStack.getQuantity();
		itemSubType = ItemStackUtil.toNative(itemStack).getMetadata();
		if(ItemStackUtil.toNative(itemStack).hasTagCompound()) {
			nbt = ItemStackUtil.toNative(itemStack).getTagCompound().toString();
		}
		this.itemStack = itemStack;
	}

	private static final long serialVersionUID = 01;

	@Setting("ItemType")
	private String itemType;
	@Setting("ItemSubType")
	private Integer itemSubType;
	@Setting("ItemQuantity")
	private Integer itemQuantity;
	@Setting("ItemNBT")
	private String nbt;
	private ItemStack itemStack;

	public String getType() {
		return itemType;
	}

	public Integer getSubType() {
		return itemSubType;
	}

	public Integer getQuantity() {
		return itemQuantity;
	}

	public String getNBT() {
		return nbt != null ? nbt : "";
	}

	public ItemStack getItemStack() {
		if(itemStack == null) {
			ItemStack itemStack = ItemStack.of(Sponge.getRegistry().getType(ItemType.class, itemType).get());
			itemStack.setQuantity(itemQuantity);
			net.minecraft.item.ItemStack nmsStack = ItemStackUtil.toNative(itemStack);
			nmsStack.setItemDamage(itemSubType);
			if(nbt != null) {
				try {
					nmsStack.setTagCompound(JsonToNBT.getTagFromJson(nbt));
				} catch (NBTException e) {
					e.printStackTrace();
				}
			}
			this.itemStack = ItemStackUtil.fromNative(nmsStack);
		}
		return itemStack.copy();
	}

	public void setQuantity(int quantity) {
		itemQuantity = quantity;
	}

	public boolean isPresent() {
		return Sponge.getRegistry().getType(ItemType.class, itemType).isPresent();
	}

	@Override
	public String toString() {
		return  "ItemType: " + itemType +
				", ItemSubType: " + itemSubType +
				", Quantity: " + itemQuantity + 
				", NBT: " + nbt;
	}

}