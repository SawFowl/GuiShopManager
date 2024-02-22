package sawfowl.guishopmanager.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.spongepowered.plugin.PluginContainer;

import net.kyori.adventure.text.Component;
import sawfowl.commandpack.api.commands.AbstractPluginCommand;
import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.commandpack.api.commands.parameterized.ParameterizedCommand;
import sawfowl.commandpack.api.data.command.Settings;
import sawfowl.guishopmanager.GuiShopManager;

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
		return getArguments().stream().collect(Collectors.toMap(arg -> arg.getKey(), arg -> arg));
	}

}
