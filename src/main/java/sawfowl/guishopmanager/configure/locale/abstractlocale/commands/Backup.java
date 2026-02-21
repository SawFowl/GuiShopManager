package sawfowl.guishopmanager.configure.locale.abstractlocale.commands;

import net.kyori.adventure.text.Component;

import sawfowl.localeapi.api.Translation;

public interface Backup extends Translation {

	Component save();

	Component load();

}
