package sawfowl.guishopmanager.commands;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.spongepowered.api.command.CommandExecutor;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.service.pagination.PaginationList;
import org.spongepowered.api.util.locale.LocaleSource;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;

public class MainCommand implements CommandExecutor {

	GuiShopManager plugin;
	boolean auctionEnable = false;
	public MainCommand(GuiShopManager instance) {
		plugin = instance;
		auctionEnable = plugin.getRootNode().node("Auction", "Enable").getBoolean();
	}

	@Override
	public CommandResult execute(CommandContext context) throws CommandException {
		Object src = context.cause().root();
		List<Component> messages = new ArrayList<Component>();
		if(src instanceof ServerPlayer) {
			ServerPlayer player = (ServerPlayer) src;
			Locale locale = player.locale();
			if(auctionEnable) {
				if(player.hasPermission(Permissions.SHOP_OPEN_SELF)) {
					if(player.hasPermission(Permissions.SHOP_OPEN_OTHER)) {
						messages.add(deserialize("&a/guishopmanager auction &e<Player>").clickEvent(ClickEvent.suggestCommand("/guishopmanager auction")).hoverEvent(HoverEvent.showText(plugin.getLocales().getComponent(locale, "Hover", "RunCommand"))));
					} else {
						messages.add(deserialize("&a/guishopmanager auction").clickEvent(ClickEvent.runCommand("/guishopmanager auction")).hoverEvent(HoverEvent.showText(plugin.getLocales().getComponent(locale, "Hover", "RunCommand"))));
					}
				}
				if(player.hasPermission(Permissions.AUCTION_ADD_ITEM)) {
					messages.add(deserialize("&a/guishopmanager auction additem &c<Bet> <Price> &e<Currency>").clickEvent(ClickEvent.suggestCommand("/guishopmanager additem ")).hoverEvent(HoverEvent.showText(plugin.getLocales().getComponent(locale, "Hover", "RunCommand"))));
				}
				if(player.hasPermission(Permissions.AUCTION_BLOCK_ITEM)) {
					messages.add(deserialize("&a/guishopmanager auction blacklist &e<flags [mask | item]>").clickEvent(ClickEvent.suggestCommand("/guishopmanager blacklist ")).hoverEvent(HoverEvent.showText(plugin.getLocales().getComponent(locale, "Hover", "RunCommand"))));
				}
			}
			if(player.hasPermission(Permissions.SHOP_CREATE)) {
				messages.add(deserialize("&a/guishopmanager create &c<Shop>").clickEvent(ClickEvent.suggestCommand("/guishopmanager create ")).hoverEvent(HoverEvent.showText(plugin.getLocales().getComponent(locale, "Hover", "RunCommand"))));
			}
			if(player.hasPermission(Permissions.SHOP_DELETE)) {
				messages.add(deserialize("&a/guishopmanager delete &c<Shop>").clickEvent(ClickEvent.suggestCommand("/guishopmanager delete ")).hoverEvent(HoverEvent.showText(plugin.getLocales().getComponent(locale, "Hover", "RunCommand"))));
			}
			if(player.hasPermission(Permissions.SHOP_EDIT)) {
				messages.add(deserialize("&a/guishopmanager edit &c<Shop>").clickEvent(ClickEvent.runCommand("/guishopmanager edit")).hoverEvent(HoverEvent.showText(plugin.getLocales().getComponent(locale, "Hover", "RunCommand"))));
				messages.add(deserialize("&a/guishopmanager setitem &c<Shop> <Menu> <Slot> <BuyPrice> <SellPrice> &e<Currency>").clickEvent(ClickEvent.suggestCommand("/guishopmanager setitem ")).hoverEvent(HoverEvent.showText(plugin.getLocales().getComponent(locale, "Hover", "RunCommand"))));
			}
			if(player.hasPermission(Permissions.SHOP_TRANSLATE)) {
				messages.add(deserialize("&a/guishopmanager translate &c<Shop>").clickEvent(ClickEvent.suggestCommand("/guishopmanager translate ")).hoverEvent(HoverEvent.showText(plugin.getLocales().getComponent(locale, "Hover", "RunCommand"))));
			}
			if(player.hasPermission(Permissions.SHOP_OPEN_SELF)) {
				if(player.hasPermission(Permissions.SHOP_OPEN_OTHER)) {
					messages.add(deserialize("&a/guishopmanager open &c<Shop> &e<Player>").clickEvent(ClickEvent.runCommand("/guishopmanager open")).hoverEvent(HoverEvent.showText(plugin.getLocales().getComponent(locale, "Hover", "RunCommand"))));
				} else {
					messages.add(deserialize("&a/guishopmanager open &c<Shop>").clickEvent(ClickEvent.runCommand("/guishopmanager open")).hoverEvent(HoverEvent.showText(plugin.getLocales().getComponent(locale, "Hover", "RunCommand"))));
				}
			}
			if(player.hasPermission(Permissions.RELOAD)) {
				messages.add(deserialize("&a/guishopmanager reload").clickEvent(ClickEvent.suggestCommand("/guishopmanager reload")).hoverEvent(HoverEvent.showText(plugin.getLocales().getComponent(locale, "Hover", "RunCommand"))));
			}
			PaginationList.builder()
				.contents(messages)
				.title(plugin.getLocales().getComponent(locale, "Messages", "CommandsTitle"))
				.padding(plugin.getLocales().getComponent(locale, "Messages", "CommandsPadding"))
				.linesPerPage(7)
				.sendTo(player);
		} else {
			if(auctionEnable) {
				messages.add(deserialize("&a/guishopmanager auction &e<Player>"));
				messages.add(deserialize("&a/guishopmanager auction additem &c<Bet> <Price> &e<Currency>"));
				messages.add(deserialize("&a/guishopmanager auction blacklist &e<flags [mask | item]>"));
				messages.add(deserialize("&a/guishopmanager create &c<Shop>"));
				messages.add(deserialize("&a/guishopmanager delete &c<Shop>"));
				messages.add(deserialize("&a/guishopmanager edit &c<Shop>"));
				messages.add(deserialize("&a/guishopmanager setitem &c<Shop> <Menu> <Slot> <BuyPrice> <SellPrice> &e<Currency>"));
				messages.add(deserialize("&a/guishopmanager translate &c<Shop>"));
				messages.add(deserialize("&a/guishopmanager open &c<Shop> &e<Player>"));
				messages.add(deserialize("&a/guishopmanager reload"));
			PaginationList.builder()
				.contents(messages)
				.title(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "CommandsTitle"))
				.padding(plugin.getLocales().getComponent(((LocaleSource) src).locale(), "Messages", "CommandsPadding"))
				.linesPerPage(20)
				.sendTo((Audience) src);
			}
		}
		return CommandResult.success();
	}

	private Component deserialize(String string) {
		return LegacyComponentSerializer.legacyAmpersand().deserialize(string);
	}

}
