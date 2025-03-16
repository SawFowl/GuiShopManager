package sawfowl.guishopmanager.commands.backup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.service.pagination.PaginationList;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractCommand;

public class Backup extends AbstractCommand {

	public Backup(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public Parameterized build() {
		return builder()
		.addChild(new Save(plugin).build(), "save")
		.addChild(new Load(plugin).build(), "load")
		.build();
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale locale, boolean isPlayer) throws CommandException {
		List<Component> messages = new ArrayList<Component>();
		if(isPlayer) {
			messages.add(text("&a/guishopmanager backup load").clickEvent(ClickEvent.runCommand("/guishopmanager backup load")).hoverEvent(HoverEvent.showText(plugin.getLocales().getLocale(locale).commands().run())));
			messages.add(text("&a/guishopmanager backup save").clickEvent(ClickEvent.runCommand("/guishopmanager backup save")).hoverEvent(HoverEvent.showText(plugin.getLocales().getLocale(locale).commands().run())));
		} else {
			messages.add(text("&a/guishopmanager backup load"));
			messages.add(text("&a/guishopmanager backup save"));
		}
		PaginationList.builder()
		.contents(messages)
		.title(plugin.getLocales().getLocale(locale).commands().title())
		.padding(plugin.getLocales().getLocale(locale).commands().padding())
		.linesPerPage(5)
		.sendTo(audience);
	}

	@Override
	public String command() {
		return "backup";
	}

	@Override
	public String permission() {
		return Permissions.BACKUP;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return null;
	}

}
