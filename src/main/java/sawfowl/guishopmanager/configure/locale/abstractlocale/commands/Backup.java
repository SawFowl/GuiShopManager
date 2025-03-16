package sawfowl.guishopmanager.configure.locale.abstractlocale.commands;

import net.kyori.adventure.text.Component;
import sawfowl.localeapi.api.LocaleReference;

public interface Backup extends LocaleReference {

	Component save();

	Component load();

}
