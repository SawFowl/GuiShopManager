package sawfowl.guishopmanager.serialization.commandsshop;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.localeapi.serializetools.CompoundTag;

@ConfigSerializable
public class SerializedCommandsList extends CompoundTag {

	@Setting("Commands")
	List<String> commands = new ArrayList<>();

	public List<String> getCommands() {
		return commands;
	}

	public void removeCommand(int number) {
		if(commands.size() <= number - 1) commands.remove(number);
	}

	public void addCommand(String command) {
		commands.add(command);
	}

	public void executeAll(ServerPlayer player) {
		commands.forEach(command -> {
			String execute = command.replace("%player%", player.name()).replace("%uuid%", player.uniqueId().toString());
			Sponge.server().scheduler().executor(GuiShopManager.getInstance().getPluginContainer()).execute(() -> {
				try {
					Sponge.server().commandManager().process(Sponge.systemSubject(), execute);
				} catch (CommandException e) {
					e.printStackTrace();
				}
			});
		});
	}

	@Override
	public String toString() {
		return commands.toString();
	}

}
