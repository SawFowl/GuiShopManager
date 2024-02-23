package sawfowl.guishopmanager.commands.auction;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.serialize.SerializationException;

import net.kyori.adventure.audience.Audience;
import sawfowl.commandpack.api.CommandPack;
import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.commandpack.api.data.command.Settings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractCommand;
import sawfowl.guishopmanager.utils.CommandParameters;

public class Auction extends AbstractCommand {

	private String command;
	private List<String> aliases;
	public Auction(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public Parameterized build() {
		return builder()
				.addChild(new AddItem(plugin).build(), "add", "additem")
				.addChild(new AddBlackList(plugin).build(), "blacklist", "block")
				.executor(this)
				.build();
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale locale, boolean isPlayer) throws CommandException {
		if(isPlayer) {
			ServerPlayer player = getPlayer(context).orElse((ServerPlayer) audience);
			if(!((ServerPlayer) audience).hasPermission(Permissions.AUCTION_OPEN_OTHER) && !((ServerPlayer) audience).uniqueId().equals(player.uniqueId())) exception(locale, "Messages", "DontOpenOther");
			plugin.getAuctionMenus().createInventory(player, 1, plugin.getAuctionItems().values().stream().collect(Collectors.toList()));
		} else {
			ServerPlayer player = getPlayer(context).orElse(null);
			if(player == null) exception(locale, "Messages", "PlayerIsNotPresent");
			plugin.getAuctionMenus().createInventory(player, 1, plugin.getAuctionItems().values().stream().collect(Collectors.toList()));
		}
	}

	@Override
	public String command() {
		return "auction";
	}

	@Override
	public String permission() {
		return Permissions.AUCTION_OPEN_SELF;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return Arrays.asList(ParameterSettings.of(CommandParameters.PLAYER_FOR_AUCTION, false, "Messages", "PlayerIsNotPresent"));
	}

	@Override
	public Settings applyCommandSettings() {
		try {
			aliases = plugin.getRootNode().node("Aliases", "Auction", "List").getList(String.class);
			if(!aliases.isEmpty()) {
				command = aliases.get(0);
				aliases.remove(0);
				if(!aliases.isEmpty()) return Settings.builder().setAliases(aliases).build();
			}
		} catch (SerializationException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void register(CommandPack commandPack) {
		if(command != null) commandPack.registerCommand(this);
	}

}
