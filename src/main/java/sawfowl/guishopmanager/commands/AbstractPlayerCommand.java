package sawfowl.guishopmanager.commands;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.spongepowered.plugin.PluginContainer;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.commandpack.api.commands.parameterized.ParameterizedPlayerCommand;
import sawfowl.guishopmanager.GuiShopManager;

public abstract class AbstractPlayerCommand extends AbstractCommand implements ParameterizedPlayerCommand {

	public AbstractPlayerCommand(GuiShopManager instance) {
		super(instance);
	}

	public abstract List<ParameterSettings> getArguments();
/*
	@Override
	public Settings applyCommandSettings() {
		return null;
	}
*/
	@Override
	public PluginContainer getContainer() {
		return plugin.getPluginContainer();
	}

	@Override
	public Map<String, ParameterSettings> getSettingsMap() {
		return getArguments().stream().collect(Collectors.toMap(arg -> arg.getKey(), arg -> arg));
	}

}
