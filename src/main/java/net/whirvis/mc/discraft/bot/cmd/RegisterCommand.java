package net.whirvis.mc.discraft.bot.cmd;

import java.sql.SQLException;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.whirvex.args.Args;
import com.whirvex.cmd.Command;
import com.whirvex.cmd.CommandCenter;

import net.dv8tion.jda.api.entities.User;
import net.whirvis.mc.discraft.bot.user.UserManager;

/**
 * The {@code "register"} command, which registers a Discord account into
 * Discraft. This will allow the user to change their language, link their
 * Minecraft account to Discord, etc.
 */
public class RegisterCommand extends DiscordCommand {

	private final UserManager userManager;

	/**
	 * Creates a {@code "register"} command.
	 * 
	 * @param userManager
	 *            the user manager.
	 * @throws NullPointerException
	 *             if {@code userManager} is {@code null}.
	 */
	public RegisterCommand(@NotNull UserManager userManager) {
		super("register", null, "Registers a user to Discraft");
		this.userManager = Objects.requireNonNull(userManager, "userManager");
	}

	@Override
	public boolean execute(CommandCenter center, DiscordCommandSender sender,
			Command cmd, Args args) throws SQLException {
		User user = sender.getUser();
		if (userManager.isRegistered(user)) {
			sender.sendMessage("You are already registered!");
			return true;
		}

		userManager.createUser(user);
		sender.sendMessage("You have been registered!");
		return true;
	}

}
