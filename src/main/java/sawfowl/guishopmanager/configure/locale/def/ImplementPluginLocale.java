package sawfowl.guishopmanager.configure.locale.def;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.configure.locale.abstractlocale.Commands;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Debug;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Gui;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Items;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Messages;
import sawfowl.guishopmanager.configure.locale.abstractlocale.AbstractLocale;

@ConfigSerializable
public class ImplementPluginLocale implements AbstractLocale {

	@Setting("Commands")
	private ImplementCommands commands = new ImplementCommands();
	@Setting("Debug")
	private ImplementDebug debug = new ImplementDebug();
	@Setting("Gui")
	private ImplementGui gui = new ImplementGui();
	@Setting("Items")
	private ImplementItems items = new ImplementItems();
	@Setting("Messages")
	private ImplementMessages messages = new ImplementMessages();
	public ImplementPluginLocale() {}

	@Override
	public Commands commands() {
		return commands;
	}

	@Override
	public Debug debug() {
		return debug;
	}

	@Override
	public Gui gui() {
		return gui;
	}

	@Override
	public Items items() {
		return items;
	}

	@Override
	public Messages messages() {
		return messages;
	}

}
