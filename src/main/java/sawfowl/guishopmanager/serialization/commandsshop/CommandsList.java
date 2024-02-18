package sawfowl.guishopmanager.serialization.commandsshop;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;

import com.google.gson.JsonArray;

import sawfowl.guishopmanager.GuiShopManager;

public class CommandsList {

	private List<String> commands = new ArrayList<>();

	public CommandsList(){}

	public CommandsList(JsonArray commands) {
		this.commands = commands.asList().stream().map(e -> e.getAsString()).collect(Collectors.toList());
	}

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
			Sponge.server().scheduler().executor(GuiShopManager.getInstance().getPluginContainer()).execute(() -> {
				try {
					Sponge.server().commandManager().process(Sponge.systemSubject(), command.replace("%player%", player.name()).replace("%uuid%", player.uniqueId().toString()));
				} catch (CommandException e) {
					e.printStackTrace();
				}
			});
		});
	}

	public JsonArray asJsonArray() {
		JsonArray array = new JsonArray();
		commands.forEach(array::add);
		return array;
	}

	@Override
	public String toString() {
		return commands.toString();
	}

}
