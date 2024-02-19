package sawfowl.guishopmanager.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.pagination.PaginationList;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.commandpack.api.data.command.Settings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.auction.Open;

public class MainCommand extends AbstractCommand {

	boolean auctionEnable = false;
	public MainCommand(GuiShopManager instance) {
		super(instance);
		auctionEnable = plugin.getRootNode().node("Auction", "Enable").getBoolean();
	}

	@Override
	public Parameterized build() {
		return auctionEnable
				?
				builder()
					.addChild(new Open(plugin).build(), "auction", "market")
					//.addChild(commandShop, "shop")
					//.addChild(commandsShop, "commandsshop", "cshop")
					//.addChild(commandReload, "reload")
					.executor(this)
				.build()
				: 
				builder()
					//.addChild(commandShop, "shop")
					//.addChild(commandsShop, "commandsshop", "cshop")
					//.addChild(commandReload, "reload")
					.executor(this)
				.build();
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale locale, boolean isPlayer) throws CommandException {
		List<Component> messages = new ArrayList<Component>();
		if(isPlayer) {
			ServerPlayer player = (ServerPlayer) audience;
			if(auctionEnable) {
				if(player.hasPermission(Permissions.AUCTION_OPEN_SELF)) {
					if(player.hasPermission(Permissions.AUCTION_OPEN_OTHER)) {
						messages.add(deserialize("&a/guishopmanager auction &e<Player>").clickEvent(ClickEvent.suggestCommand("/guishopmanager auction")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
					} else {
						messages.add(deserialize("&a/guishopmanager auction").clickEvent(ClickEvent.runCommand("/guishopmanager auction")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
					}
				}
				if(player.hasPermission(Permissions.AUCTION_ADD_ITEM)) {
					messages.add(deserialize("&a/guishopmanager auction additem &c<Bet> <Price> &e<Currency>").clickEvent(ClickEvent.suggestCommand("/guishopmanager auction additem ")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
				}
				if(player.hasPermission(Permissions.AUCTION_BLOCK_ITEM)) {
					messages.add(deserialize("&a/guishopmanager auction blacklist &e<flags [mask | item]>").clickEvent(ClickEvent.suggestCommand("/guishopmanager auction blacklist ")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
				}
			}
			if(player.hasPermission(Permissions.SHOP_CREATE)) {
				messages.add(deserialize("&a/guishopmanager shop create &c<Shop>").clickEvent(ClickEvent.suggestCommand("/guishopmanager shop create ")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
			}
			if(player.hasPermission(Permissions.SHOP_DELETE)) {
				messages.add(deserialize("&a/guishopmanager shop delete &c<Shop>").clickEvent(ClickEvent.suggestCommand("/guishopmanager shop delete ")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
			}
			if(player.hasPermission(Permissions.SHOP_EDIT)) {
				messages.add(deserialize("&a/guishopmanager shop edit &c<Shop>").clickEvent(ClickEvent.runCommand("/guishopmanager shop edit")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
				messages.add(deserialize("&a/guishopmanager shop setitem &c<Shop> <Menu> <Slot> <BuyPrice> <SellPrice> &e<Currency>").clickEvent(ClickEvent.suggestCommand("/guishopmanager shop setitem ")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
			}
			if(player.hasPermission(Permissions.SHOP_TRANSLATE)) {
				messages.add(deserialize("&a/guishopmanager shop translate &c<Shop>").clickEvent(ClickEvent.suggestCommand("/guishopmanager shop translate ")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
			}
			if(player.hasPermission(Permissions.SHOP_OPEN_SELF)) {
				if(player.hasPermission(Permissions.SHOP_OPEN_OTHER)) {
					messages.add(deserialize("&a/guishopmanager shop open &c<Shop> &e<Player>").clickEvent(ClickEvent.runCommand("/guishopmanager shop open")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
				} else {
					messages.add(deserialize("&a/guishopmanager shop open &c<Shop>").clickEvent(ClickEvent.runCommand("/guishopmanager shop open")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
				}
			}
			if(player.hasPermission(Permissions.COMMANDSSHOP_CREATE)) {
				messages.add(deserialize("&a/guishopmanager cshop create &c<Shop>").clickEvent(ClickEvent.suggestCommand("/guishopmanager cshop create ")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
			}
			if(player.hasPermission(Permissions.COMMANDSSHOP_DELETE)) {
				messages.add(deserialize("&a/guishopmanager cshop delete &c<Shop>").clickEvent(ClickEvent.suggestCommand("/guishopmanager cshop delete ")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
			}
			if(player.hasPermission(Permissions.COMMANDSSHOP_EDIT)) {
				messages.add(deserialize("&a/guishopmanager cshop edit &c<Shop>").clickEvent(ClickEvent.runCommand("/guishopmanager cshop edit")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
				messages.add(deserialize("&a/guishopmanager cshop addcommand &c<Args>").clickEvent(ClickEvent.suggestCommand("/guishopmanager setitem ")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
			}
			if(player.hasPermission(Permissions.COMMANDSSHOP_TRANSLATE)) {
				messages.add(deserialize("&a/guishopmanager cshop translate &c<Shop>").clickEvent(ClickEvent.suggestCommand("/guishopmanager cshop translate ")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
			}
			if(player.hasPermission(Permissions.COMMANDSSHOP_OPEN_SELF)) {
				if(player.hasPermission(Permissions.COMMANDSSHOP_OPEN_OTHER)) {
					messages.add(deserialize("&a/guishopmanager cshop open &c<Shop> &e<Player>").clickEvent(ClickEvent.runCommand("/guishopmanager cshop open")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
				} else {
					messages.add(deserialize("&a/guishopmanager cshop open &c<Shop>").clickEvent(ClickEvent.runCommand("/guishopmanager cshop open")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
				}
			}
			if(player.hasPermission(Permissions.RELOAD)) {
				messages.add(deserialize("&a/guishopmanager reload").clickEvent(ClickEvent.runCommand("/guishopmanager reload")).hoverEvent(HoverEvent.showText(getComponent(locale, "Hover", "RunCommand"))));
			}
			PaginationList.builder()
				.contents(messages)
				.title(getComponent(locale, "Messages", "CommandsTitle"))
				.padding(getComponent(locale, "Messages", "CommandsPadding"))
				.linesPerPage(7)
				.sendTo(player);
		} else {
			if(auctionEnable) {
				messages.add(deserialize("&a/guishopmanager auction &e<Player>"));
				messages.add(deserialize("&a/guishopmanager auction additem &c<Bet> <Price> &e<Currency>"));
				messages.add(deserialize("&a/guishopmanager auction blacklist &e<flags [mask | item]>"));
			}
			messages.add(deserialize("&a/guishopmanager shop create &c<Shop>"));
			messages.add(deserialize("&a/guishopmanager shop delete &c<Shop>"));
			messages.add(deserialize("&a/guishopmanager shop edit &c<Shop>"));
			messages.add(deserialize("&a/guishopmanager shop setitem &c<Shop> <Menu> <Slot> <BuyPrice> <SellPrice> &e<Currency>"));
			messages.add(deserialize("&a/guishopmanager shop translate &c<Shop>"));
			messages.add(deserialize("&a/guishopmanager shop open &c<Shop> &e<Player>"));
			messages.add(deserialize("&a/guishopmanager cshop create &c<Shop>"));
			messages.add(deserialize("&a/guishopmanager cshop delete &c<Shop>"));
			messages.add(deserialize("&a/guishopmanager cshop edit &c<Shop>"));
			messages.add(deserialize("&a/guishopmanager cshop addcommand &c<Args>"));
			messages.add(deserialize("&a/guishopmanager cshop translate &c<Shop>"));
			messages.add(deserialize("&a/guishopmanager cshop open &c<Shop> &e<Player>"));
			messages.add(deserialize("&a/guishopmanager reload"));
			PaginationList.builder()
				.contents(messages)
				.title(getComponent(locale, "Messages", "CommandsTitle"))
				.padding(getComponent(locale, "Messages", "CommandsPadding"))
				.linesPerPage(20)
				.sendTo(audience);
		}
	}

	@Override
	public String command() {
		return "guishopmanager";
	}

	@Override
	public Component getComponent(Object[] arg0) {
		return null;
	}

	@Override
	public String permission() {
		return Permissions.HELP;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return null;
	}

	@Override
	public Settings applyCommandSettings() {
		return Settings.builder().setAliases("gsm").build();
	}

	private Component deserialize(String string) {
		return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
	}

}
