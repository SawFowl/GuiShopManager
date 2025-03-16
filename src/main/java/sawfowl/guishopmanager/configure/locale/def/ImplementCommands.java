package sawfowl.guishopmanager.configure.locale.def;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.abstractlocale.Commands;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Auction;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Backup;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.CommandShop;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Exceptions;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Shop;
import sawfowl.guishopmanager.configure.locale.def.commands.ImplementAuction;
import sawfowl.guishopmanager.configure.locale.def.commands.ImplementBackup;
import sawfowl.guishopmanager.configure.locale.def.commands.ImplementCommandShop;
import sawfowl.guishopmanager.configure.locale.def.commands.ImplementExceptions;
import sawfowl.guishopmanager.configure.locale.def.commands.ImplementShop;
import sawfowl.localeapi.api.TextUtils;

@ConfigSerializable
public class ImplementCommands implements Commands {

	@Setting("Auction")
	private ImplementAuction auction = new ImplementAuction();
	@Setting("Backup")
	private ImplementBackup backup = new ImplementBackup();
	@Setting("CommandShop")
	private ImplementCommandShop commandShop = new ImplementCommandShop();
	@Setting("Exceptions")
	private ImplementExceptions exceptions = new ImplementExceptions();
	@Setting("Shop")
	private ImplementShop shop = new ImplementShop();
	@Setting("Run")
	private Component run = TextUtils.deserialize("&eClick to execute this command.");
	@Setting("Title")
	private Component title = TextUtils.deserialize("&3Commands");
	@Setting("Run")
	private Component padding = TextUtils.deserialize("&3=");
	@Setting("Reload")
	private Component reload = TextUtils.deserialize("&aThe plugin has been reloaded.");
	public ImplementCommands() {}

	@Override
	public Auction auction() {
		return auction;
	}

	@Override
	public CommandShop commandShop() {
		return commandShop;
	}

	@Override
	public Exceptions exceptions() {
		return exceptions;
	}

	@Override
	public Shop shop() {
		return shop;
	}

	@Override
	public Component run() {
		return run;
	}

	@Override
	public Component title() {
		return title;
	}

	@Override
	public Component padding() {
		return padding;
	}

	@Override
	public Component reload() {
		return reload;
	}

	@Override
	public Backup backup() {
		return backup;
	}

}
