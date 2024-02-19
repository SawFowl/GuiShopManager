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
import net.kyori.adventure.text.Component;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.commandpack.api.data.command.Settings;

import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractCommand;
import sawfowl.guishopmanager.utils.CommandParameters;

public class Open extends AbstractCommand {

	public Open(GuiShopManager instance) {
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
		ServerPlayer player = getPlayer(context).orElse((ServerPlayer) audience);
		if(isPlayer && !((ServerPlayer) audience).hasPermission(Permissions.AUCTION_OPEN_OTHER) && !((ServerPlayer) audience).uniqueId().equals(player.uniqueId())) exception(locale, "Messages", "DontOpenOther");
		plugin.getAuctionMenus().createInventory(player, 1, plugin.getAuctionItems().values().stream().collect(Collectors.toList()));
	}

	@Override
	public String command() {
		return "auction";
	}

	@Override
	public Component getComponent(Object[] arg0) {
		return null;
	}

	@Override
	public String permission() {
		return Permissions.AUCTION_OPEN_SELF;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return Arrays.asList(ParameterSettings.of(CommandParameters.PLAYER, true, false, new Object[] {"Messages", "PlayerIsNotPresent"}));
	}

	@Override
	public Settings applyCommandSettings() {
		try {
			return Settings.builder().setAliases(plugin.getRootNode().node("Aliases", "Auction", "List").getList(String.class)).build();
		} catch (SerializationException e) {
			e.printStackTrace();
		}
		return null;
	}

}
