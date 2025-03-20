package sawfowl.guishopmanager.commands.backup;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.Command.Parameterized;
import org.spongepowered.api.command.exception.CommandException;
import org.spongepowered.api.command.parameter.CommandContext;

import net.kyori.adventure.audience.Audience;
import sawfowl.commandpack.api.commands.parameterized.ParameterSettings;
import sawfowl.guishopmanager.GuiShopManager;
import sawfowl.guishopmanager.Permissions;
import sawfowl.guishopmanager.commands.AbstractCommand;
import sawfowl.guishopmanager.data.commandshop.CommandShopData;
import sawfowl.guishopmanager.data.shop.Shop;
import sawfowl.guishopmanager.serialization.auction.SerializedAuctionStack;
import sawfowl.guishopmanager.storage.H2Storage;

public class Load extends AbstractCommand {

	private boolean driverExist = false;
	public Load(GuiShopManager instance) {
		super(instance);
		driverExist = Sponge.pluginManager().plugin("h2driver").isPresent();
	}

	@Override
	public void execute(CommandContext context, Audience audience, Locale locale, boolean isPlayer) throws CommandException {
		if(!driverExist) exception(plugin.getLocales().getLocale(locale).commands().exceptions().h2NotPresent());
		if(!plugin.getConfigDir().resolve("Backup.mv.db").toFile().exists()) exception(plugin.getLocales().getLocale(locale).commands().exceptions().backupNotPresent());
		H2Storage h2Storage = new H2Storage(plugin, "Backup");
		h2Storage.loadAuction();
		h2Storage.loadShops();
		h2Storage.loadCommandsShops();
		for(Shop shop : plugin.getAllShops()) {
			plugin.getShopStorage().saveShop(shop.getID());
		}
		for(CommandShopData shop : plugin.getAllCommandShops()) {
			plugin.getCommandsShopStorage().saveCommandsShop(shop.getID());
		}
		for(SerializedAuctionStack stack : plugin.getAuctionItems().values()) {
			plugin.getAuctionStorage().saveAuctionStack(stack);
		}
		for(Set<SerializedAuctionStack> stacks : plugin.getExpiredAuctionItems().values()) {
			for(SerializedAuctionStack stack : stacks) {
				plugin.getAuctionStorage().saveExpireAuctionData(stack);
			}
		}
		for(Set<SerializedAuctionStack> stacks : plugin.getExpiredBetAuctionItems().values()) {
			for(SerializedAuctionStack stack : stacks) {
				plugin.getAuctionStorage().saveExpireBetAuctionData(stack);
			}
		}
		try {
			h2Storage.getConnection().close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		audience.sendMessage(plugin.getLocales().getLocale(locale).commands().backup().load());
	}

	@Override
	public Parameterized build() {
		return fastBuild();
	}

	@Override
	public String permission() {
		return Permissions.BACKUP;
	}

	@Override
	public String command() {
		return "load";
	}

	@Override
	public List<ParameterSettings> getArguments() {
		return null;
	}

}
