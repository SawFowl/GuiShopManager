package sawfowl.guishopmanager.configure.locale.abstractlocale;

import sawfowl.localeapi.api.LocaleReference;

public interface PluginLocale extends LocaleReference {

	Commands commands();

	Items items();

	Messages messages();

}
