package sawfowl.guishopmanager.configure.locale.abstractlocale;

import sawfowl.localeapi.api.Translation;

public interface AbstractLocale extends Translation {

	Commands commands();

	Debug debug();

	Gui gui();

	Items items();

	Messages messages();

	Comments comments();

}
