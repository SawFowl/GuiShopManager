package sawfowl.guishopmanager.configure.locale.ru;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import net.kyori.adventure.text.Component;

import sawfowl.guishopmanager.configure.locale.abstractlocale.Commands;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Auction;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.CommandShop;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Exceptions;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Shop;
import sawfowl.guishopmanager.configure.locale.ru.commands.ImplementAuction;
import sawfowl.guishopmanager.configure.locale.ru.commands.ImplementCommandShop;
import sawfowl.guishopmanager.configure.locale.ru.commands.ImplementExceptions;
import sawfowl.guishopmanager.configure.locale.ru.commands.ImplementShop;
import sawfowl.localeapi.api.TextUtils;

@ConfigSerializable
public class ImplementCommands implements Commands {

	@Setting("Auction")
	private ImplementAuction auction = new ImplementAuction();
	@Setting("CommandShop")
	private ImplementCommandShop commandShop = new ImplementCommandShop();
	@Setting("Exceptions")
	private ImplementExceptions exceptions = new ImplementExceptions();
	@Setting("Shop")
	private ImplementShop shop = new ImplementShop();
	@Setting("Run")
	private Component run = TextUtils.deserialize("&eКлик для выполнения этой команды.");
	@Setting("Title")
	private Component title = TextUtils.deserialize("&3Команды");
	@Setting("Run")
	private Component padding = TextUtils.deserialize("&3=");
	@Setting("Reload")
	private Component reload = TextUtils.deserialize("&aПлагин перезагружен.");
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

}
