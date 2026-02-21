package sawfowl.guishopmanager.configure;

import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

@ConfigSerializable
public class PriceAndSizeItems {

	public PriceAndSizeItems(){}

	@Setting("0")
	private ItemStack i0 = ItemStack.of(ItemTypes.IRON_NUGGET);
	@Setting("1")
	private ItemStack i1 = ItemStack.of(ItemTypes.IRON_NUGGET);
	@Setting("2")
	private ItemStack i2 = ItemStack.of(ItemTypes.IRON_NUGGET);
	@Setting("3")
	private ItemStack i3 = ItemStack.of(ItemTypes.GOLD_NUGGET);
	@Setting("4")
	private ItemStack i4 = ItemStack.of(ItemTypes.GOLD_NUGGET);
	@Setting("5")
	private ItemStack i5 = ItemStack.of(ItemTypes.GOLD_NUGGET);
	@Setting("6")
	private ItemStack i6 = ItemStack.of(ItemTypes.DIAMOND);
	@Setting("7")
	private ItemStack i7 = ItemStack.of(ItemTypes.DIAMOND);
	@Setting("8")
	private ItemStack i8 = ItemStack.of(ItemTypes.DIAMOND);

	public ItemStack getItemStack(int number) {
		switch (number) {
		case 0: return i0;
		case 1: return i1;
		case 2: return i2;
		case 3: return i3;
		case 4: return i4;
		case 5: return i5;
		case 6: return i6;
		case 7: return i7;
		case 8: return i8;
		default: throw new IllegalArgumentException("Unexpected value: " + number);
		}
	}

}
