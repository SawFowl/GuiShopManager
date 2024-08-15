package sawfowl.guishopmanager.commands;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.plugin.PluginContainer;

import net.kyori.adventure.text.Component;

import sawfowl.commandpack.api.commands.AbstractPluginCommand;
import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.commandpack.api.commands.parameterized.ParameterizedCommand;
import sawfowl.commandpack.api.data.command.Settings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.configure.locale.abstractlocale.Commands;
import sawfowl.guishopmanager.configure.locale.abstractlocale.commands.Exceptions;

public abstract class AbstractCommand extends AbstractPluginCommand<GuiShopManager> implements ParameterizedCommand {

	public AbstractCommand(GuiShopManager instance) {
		super(instance);
	}

	public abstract List<ParameterSettings> getArguments();

	@Override
	public Component getComponent(Object[] arg0) {
		return null;
	}

	@Override
	public Settings applyCommandSettings() {
		return null;
	}

	@Override
	public PluginContainer getContainer() {
		return plugin.getPluginContainer();
	}

	@Override
	public Map<String, ParameterSettings> getSettingsMap() {
		return getArguments() == null ? null : getArguments().stream().collect(Collectors.toMap(arg -> arg.getKey(), arg -> arg));
	}

	protected Commands getCommands(Locale locale) {
		return plugin.getLocales().getLocale(locale).commands();
	}

	protected Commands getCommands(ServerPlayer player) {
		return getCommands(player.locale());
	}

	protected Exceptions getExceptions(Locale locale) {
		return getCommands(locale).exceptions();
	}

	protected Exceptions getExceptions(ServerPlayer player) {
		return getExceptions(player.locale());
	}

}
