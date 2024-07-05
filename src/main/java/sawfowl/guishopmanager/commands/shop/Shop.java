package sawfowl.guishopmanager.commands.shop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.adventure.SpongeComponents;
import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.util.locale.LocaleSource;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

import sawfowl.commandpack.api.CommandPack;
import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.commandpack.api.data.command.Settings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractCommand;
import sawfowl.guishopmanager.utils.CommandParameters;

public class Shop extends AbstractCommand implements CommandExecutor  {

	private String command;
	private List<String> aliases;
	public Shop(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public Parameterized build() {
		return builder()
				.executor(this)
				.addChild(new Create(plugin).build(), "create")
				.addChild(new Delete(plugin).build(), "delete")
				.addChild(new Edit(plugin).build(), "edit")
				.addChild(new SetItem(plugin).build(), "setitem")
				.addChild(new Translate(plugin).build(), "translate")
				.build();
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale locale, boolean isPlayer) throws CommandException {
		if(plugin.shopsEmpty()) {
			exception(getCommands(locale).shop().listEmpty());
		} else {
			if(context.one(CommandParameters.SHOP).isPresent()) {
				sawfowl.guishopmanager.data.shop.Shop shop = context.one(CommandParameters.SHOP).get();
				ServerPlayer srcPlayer = (audience instanceof ServerPlayer) ? (ServerPlayer) audience : null;
				if(context.one(CommandParameters.PLAYER_FOR_SHOP).isPresent()) {
					if(context.cause().hasPermission(Permissions.SHOP_OPEN_OTHER) || context.one(CommandParameters.PLAYER_FOR_SHOP).get().uniqueId().equals((audience instanceof ServerPlayer) ? ((ServerPlayer) audience).uniqueId() : null)) {
						run(context.one(CommandParameters.PLAYER_FOR_SHOP).get(), shop);
					} else {
						audience.sendMessage(getExceptions(locale).dontOpenOther());
					}
				} else {
					if(srcPlayer != null) {
						run(srcPlayer, shop);
					} else {
						audience.sendMessage(getExceptions(locale).playerIsNotPresent());
					}
				}
			
			} else {
				List<Component> messages = new ArrayList<Component>();
				ServerPlayer srcPlayer = isPlayer ? (ServerPlayer) audience : null;
				ServerPlayer player = null;
				if(srcPlayer == null && !context.one(CommandParameters.PLAYER_FOR_SHOP).isPresent()) {
					exception(locale, "Messages", "PlayerIsNotPresent");
				} else if(srcPlayer != null) {
					if(!context.one(CommandParameters.PLAYER_FOR_SHOP).isPresent()) {
						player = srcPlayer;
					} else {
						if(srcPlayer.uniqueId().equals(context.one(CommandParameters.PLAYER_FOR_SHOP).get().uniqueId())) {
							player = srcPlayer;
						} else {
							if(!srcPlayer.hasPermission(Permissions.SHOP_OPEN_OTHER)) {
								srcPlayer.sendMessage(getExceptions(locale).dontOpenOther());
							}
						}
					}
				} else if(srcPlayer == null && context.one(CommandParameters.PLAYER_FOR_SHOP).isPresent()) {
					player = context.one(CommandParameters.PLAYER_FOR_SHOP).get();
				}
				Component hover = getCommands(locale).shop().open();
				for(sawfowl.guishopmanager.data.shop.Shop shop : plugin.getAllShops()) {
					final ServerPlayer fPlayer = player;
					Component message = shop.getOrDefaultTitle(((LocaleSource) audience).locale()).clickEvent(SpongeComponents.executeCallback(cause -> {
						run(fPlayer, shop);
					})).hoverEvent(HoverEvent.showText(hover));
					messages.add(message);
				}
				PaginationList.builder()
				.title(getCommands(locale).shop().title())
				.padding(getCommands(locale).shop().padding())
				.contents(messages)
				.linesPerPage(10)
				.sendTo(audience);
			}
		}
	}

	@Override
	public String command() {
		return command;
	}

	@Override
	public String permission() {
		return Permissions.SHOP_OPEN_SELF;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return Arrays.asList(
			ParameterSettings.of(CommandParameters.SHOP, false, locale -> getExceptions(locale).shopNotPresent()),
			ParameterSettings.of(CommandParameters.PLAYER_FOR_SHOP, false, locale -> getExceptions(locale).playerIsNotPresent())
		);
	}

	@Override
	public Settings applyCommandSettings() {
		aliases = new ArrayList<>(plugin.getConfig().getAliases().getShop().getList());
		if(!aliases.isEmpty()) {
			command = aliases.get(0);
			aliases.remove(0);
			if(!aliases.isEmpty()) return Settings.builder().setAliases(aliases).build();
		}
		return null;
	}

	public void register(CommandPack commandPack) {
		if(command != null) commandPack.registerCommand(this);
	}

	private void run(ServerPlayer player, sawfowl.guishopmanager.data.shop.Shop shop) {
		if(player != null) plugin.getShopMenu().createInventoryToPlayer(shop.getShopMenuData(1), player, shop.getID(), 1);
	}

}
