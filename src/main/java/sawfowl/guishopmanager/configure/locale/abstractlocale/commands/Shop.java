package sawfowl.guishopmanager.configure.locale.abstractlocale.commands;

import org.spongepowered.api.item.inventory.ItemStack;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.LocaleReference;

public interface Shop extends LocaleReference {

	Component delete();

	Component listTitle();

	Component listPadding();

	Component translateAdded();

	Component listEmpty();

	Component listEmptyEditor();

	Component itemAdded(ItemStack itemStack, Component shop);

	Component open();

	Component openForEdit();

}
