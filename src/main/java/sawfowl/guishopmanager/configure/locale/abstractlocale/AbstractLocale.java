package sawfowl.guishopmanager.configure.locale.abstractlocale;

import sawfowl.localeapi.api.LocaleReference;

public interface AbstractLocale extends LocaleReference {

	Commands commands();

	Debug debug();

	Gui gui();

	Items items();

	Messages messages();

}
