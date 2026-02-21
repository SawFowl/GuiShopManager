package sawfowl.guishopmanager.configure.locale.abstractlocale.commands;

import org.spongepowered.api.item.inventory.ItemStack;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.Translation;

public interface Shop extends Translation {

	Component delete();

	Component title();

	Component padding();

	Component translateAdded();

	Component listEmpty();

	Component listEmptyEditor();

	Component itemAdded(ItemStack itemStack, Component shop);

	Component open();

	Component openForEdit();

}
