package sawfowl.guishopmanager.configure.config;

import java.util.Arrays;
import java.util.List;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.localeapi.api.LocalisedComment;
import sawfowl.localeapi.api.serializetools.itemstack.SerializedItemStackJsonNbt;

@ConfigSerializable
public class BlackList {

	@Setting("Masks")
	@LocalisedComment(path = { "Comments", "BlackList", "Masks" }, plugin = "guishopmanager")
	private List<String> masks = Arrays.asList("dirt", "minecraft:barrier");
	@Setting("Items")
	@LocalisedComment(path = { "Comments", "BlackList", "Items" }, plugin = "guishopmanager")
	private List<SerializedItemStackJsonNbt> items = Arrays.asList(new SerializedItemStackJsonNbt("minecraft:bedrock", 1, null), new SerializedItemStackJsonNbt("minecraft:cobblestone", 1, null));
	public BlackList() {}

	public List<String> getMasks() {
		return masks;
	}

	public void setMasks(List<String> masks) {
		this.masks = masks;
	}

	public List<SerializedItemStackJsonNbt> getItems() {
		return items;
	}

	public void setItems(List<SerializedItemStackJsonNbt> items) {
		this.items = items;
	}

}
