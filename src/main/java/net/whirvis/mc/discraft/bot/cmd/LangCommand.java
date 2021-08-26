package net.whirvis.mc.discraft.bot.cmd;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.whirvex.args.Args;
import com.whirvex.cmd.Command;
import com.whirvex.cmd.CommandCenter;

import net.whirvis.mc.discraft.bot.user.DiscraftUser;
import net.whirvis.mc.discraft.bot.user.UserManager;
import net.whirvis.mc.discraft.bot.user.UserSettings;

/**
 * The {@code "lang"} command, which allows for a Discord user to update their
 * Discraft language.
 */
public class LangCommand extends DiscordCommand {

	private final UserManager userManager;

	/**
	 * Creates a {@code "lang"} command.
	 * 
	 * @param userManager
	 *            the user manager.
	 * @throws NullPointerException
	 *             if {@code userManager} is {@code null}.
	 */
	public LangCommand(@NotNull UserManager userManager) {
		super("lang", null, "Updates a user's language");
		this.userManager = Objects.requireNonNull(userManager, "userManager");
	}

	@Override
	public boolean execute(CommandCenter center, DiscordCommandSender sender,
			Command cmd, Args args) throws IOException, SQLException {
		DiscraftUser user = userManager.getUser(sender.getUser());
		if (user == null) {
			sender.sendMessage("You are not registered");
			return true;
		}

		UserSettings settings = user.getSettings();

		user.close();
		if (args.indexc() < 1) {
			sender.sendMessage(
					"Your language is: `" + settings.getLang() + "`");
			return true;
		} else {
			settings.setLang(args.get(0));
			sender.sendMessage(
					"Updated your languge to: `" + settings.getLang() + "`");
			return true;
		}
	}

}
