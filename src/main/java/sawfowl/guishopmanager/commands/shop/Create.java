package sawfowl.guishopmanager.commands.shop;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.configurate.serialize.SerializationException;

import net.kyori.adventure.text.Component;

import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractPlayerCommand;
import sawfowl.guishopmanager.utils.CommandParameters;
import sawfowl.localeapi.api.TextUtils;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.data.shop.ShopMenuData;

public class Create extends AbstractPlayerCommand {

	public Create(GuiShopManager instance) {
		super(instance);
	}

	@Override
	public void execute(CommandContext context, ServerPlayer player, Locale locale) throws CommandException {
		if(context.one(CommandParameters.SHOP_ID).isPresent()) {
			String shopID = TextUtils.clearDecorations(context.one(CommandParameters.SHOP_ID).get().toLowerCase());
			try {
				if(!plugin.getRootNode().node("Aliases", "Shop", "List").empty() && plugin.getRootNode().node("Aliases", "Shop", "List").getList(String.class).stream().map(String::toLowerCase).collect(Collectors.toList()).contains(shopID)) exception(locale, "Messages", "InvalidShopID");
			} catch (SerializationException e) {
				plugin.getLogger().error(e.getLocalizedMessage());
			}
			if(!plugin.shopExists(shopID)) {
				Shop shop = new Shop(Component.text(shopID));
				shop.addMenu(1, new ShopMenuData());
				plugin.addShop(shopID, shop);
				plugin.getShopMenu().createInventoryToEditor(plugin.getShop(shopID).getShopMenuData(1), player, shopID, 1);
			} else exception(locale, "Messages", "ShopIDAlreadyExists");
		} else exception(locale, "Messages", "ShopIDNotPresent");
	
	}

	@Override
	public Parameterized build() {
		return fastBuild();
	}

	@Override
	public String command() {
		return "create";
	}

	@Override
	public String permission() {
		return Permissions.SHOP_CREATE;
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return Arrays.asList(ParameterSettings.of(CommandParameters.SHOP_ID, false, "Messages", "ShopIDNotPresent"));
	}

}
