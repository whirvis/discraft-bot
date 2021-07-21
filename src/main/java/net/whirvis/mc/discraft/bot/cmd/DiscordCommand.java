package net.whirvis.mc.discraft.bot.cmd;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.whirvex.args.Args;
import com.whirvex.cmd.Command;
import com.whirvex.cmd.CommandCenter;
import com.whirvex.cmd.CommandSender;

import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * An executable command for Discord.
 * <p>
 * Discord commands are commands meant to be run within the confines of a
 * Discord. As such, they can only be sent by Discord users (who are represented
 * as {@link DiscordCommandSender} at execution time).
 * 
 * @see #requirePrivate()
 * @see #requireGuild()
 */
public abstract class DiscordCommand extends Command {

	private static final int DONT_CARE = 0, IN_DM = 1, IN_GUILD = 2;

	private int requireLocation;

	/**
	 * Constructs a new guild command.
	 * 
	 * @param label
	 *            the name of the command.
	 * @param usage
	 *            how the command should be used, may be {@code null}.
	 * @param desc
	 *            what the command does, may be {@code null}.
	 * @throws NullPointerException
	 *             if {@code label} or any command aliases are {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code label} or any command aliases are empty or contain
	 *             any whitespace.
	 */
	public DiscordCommand(@NotNull String label, @Nullable String usage,
			@Nullable String desc) {
		super(label, usage, desc);
	}

	/**
	 * Makes it so this command can be invoked anywhere in Discord.
	 * 
	 * @return this command.
	 */
	@NotNull
	public DiscordCommand requireAnywhere() {
		this.requireLocation = DONT_CARE;
		return this;
	}

	/**
	 * Requires this command be run via private messaging.
	 * 
	 * @return this command.
	 */
	@NotNull
	public DiscordCommand requirePrivate() {
		this.requireLocation = IN_DM;
		return this;
	}

	/**
	 * Requires that this command be run from a guild.
	 * 
	 * @return this command.
	 */
	@NotNull
	public DiscordCommand requireGuild() {
		this.requireLocation = IN_GUILD;
		return this;
	}

	@Override
	public final boolean execute(CommandCenter center, CommandSender sender,
			Command cmd, Args args) {
		if (!(sender instanceof DiscordCommandSender)) {
			sender.sendMessage("This command must be sent in Discord.");
			return true;
		}

		DiscordCommandSender discordSender = (DiscordCommandSender) sender;
		if (requireLocation == IN_DM
				&& !(discordSender.getChannel() instanceof PrivateChannel)) {
			sender.sendMessage("This command must be sent via DM.");
			return true;
		} else if (requireLocation == IN_GUILD
				&& !(discordSender.getChannel() instanceof TextChannel)) {
			sender.sendMessage("This command must be sent in a server.");
			return true;
		}

		try {
			return this.execute(center, discordSender, cmd, args);
		} catch (Exception e) {
			StringBuilder msg = new StringBuilder();
			msg.append("A bot error has occured while trying to execute this"
					+ " command.\nApologies for the invonenience, but your"
					+ " command cannot be completed at this time.\n");
			sender.sendMessage(msg.toString());

			/*
			 * TODO: Log this error to the database. Stuff like the command
			 * text, who sent it, the time it was sent, etc. For now though,
			 * simply sending a message to the user and printing the stack trace
			 * will suffice.
			 */
			e.printStackTrace();
			return true;
		}
	}

	/**
	 * Executes a command.
	 * 
	 * @param center
	 *            the command center running this executable.
	 * @param sender
	 *            the command sender.
	 * @param cmd
	 *            the command being executed.
	 * @param args
	 *            the arguments for execution.
	 * @return {@code true} if execution was successful, {@code false}
	 *         otherwise.
	 * @throws Exception
	 *             if an error occurs.
	 */
	public abstract boolean execute(CommandCenter center,
			DiscordCommandSender sender, Command cmd, Args args)
			throws Exception;

}
