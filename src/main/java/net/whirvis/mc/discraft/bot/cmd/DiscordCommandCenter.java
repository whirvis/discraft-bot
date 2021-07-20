package net.whirvis.mc.discraft.bot.cmd;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.whirvex.cmd.CommandCenter;
import com.whirvex.cmd.CommandSender;
import com.whirvex.event.EventManager;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

/**
 * A command center which can execute commands from Discord.
 * <p>
 * Instances of {@code DiscordCommandCenter} can be added as a listener to
 * {@code JDA}, as they listen for {@link MessageReceivedEvent}. When a message
 * is received in Discord, a check will be performed to see if it is a command.
 * If the message is a command, it will be executed automatically via
 * {@link #execute(CommandSender, String)}.
 * 
 * @see DiscordCommand
 * @see DiscordCommandSender
 */
public class DiscordCommandCenter extends CommandCenter {

	private final String prefix;

	/**
	 * Creates a Discord command center.
	 * 
	 * @param prefix
	 *            the command prefix, all command messages must begin with this.
	 *            This prefix is case isensitive at command executione time.
	 * @param name
	 *            the name of this command center, to be used in logging.
	 * @param events
	 *            the event manager to send events to, may be {@code null}.
	 * @throws NullPointerException
	 *             if {@code prefix} or {@code name} are {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code prefix} is empty or contains whitespace.
	 */
	public DiscordCommandCenter(@NotNull String prefix, @NotNull String name,
			@Nullable EventManager events) {
		super(name, events);
		this.prefix = Objects.requireNonNull(prefix, "prefix");
		if (prefix.isEmpty()) {
			throw new IllegalArgumentException("prefix cannot be empty");
		} else if (!prefix.matches("\\S+")) {
			throw new IllegalArgumentException(
					"prefix cannot contain whitespace");
		}
	}

	/**
	 * Creates a Discord command center.
	 * 
	 * @param prefix
	 *            the command prefix, all command messages must begin with this.
	 *            This prefix is case isensitive at command executione time.
	 * @param name
	 *            the name of this command center, to be used in logging.
	 * @throws NullPointerException
	 *             if {@code prefix} or {@code name} are {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code prefix} is empty contains whitespace.
	 */
	public DiscordCommandCenter(@NotNull String prefix, @NotNull String name) {
		this(prefix, name, null);
	}

	/**
	 * Creates a Discord command center.
	 * 
	 * @param prefix
	 *            the command prefix, all command messages must begin with this.
	 *            This prefix is case isensitive at command executione time.
	 * @param events
	 *            the event manager to send events to, may be {@code null}.
	 * @throws NullPointerException
	 *             if {@code prefix} is {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code prefix} is empty contains whitespace.
	 */
	public DiscordCommandCenter(@NotNull String prefix,
			@Nullable EventManager events) {
		this(prefix, null, events);
	}

	/**
	 * Creates a Discord command center.
	 * 
	 * @param prefix
	 *            the command prefix, all command messages must begin with this.
	 *            This prefix is case isensitive at command executione time.
	 * @throws NullPointerException
	 *             if {@code prefix} is {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code prefix} is empty contains whitespace.
	 */
	public DiscordCommandCenter(@NotNull String prefix) {
		this(prefix, null, null);
	}

	private boolean isDiscordCommand(String cmd) {
		if (cmd == null) {
			return false; /* no command */
		} else if (cmd.length() <= prefix.length()) {
			return false; /* no prefix */
		}

		char separator = cmd.charAt(prefix.length());
		if (!Character.isWhitespace(separator)) {
			return false; /* no separator */
		}

		String cmd_lc = cmd.toLowerCase();
		String prefix_lc = prefix.toLowerCase();
		if (!cmd_lc.startsWith(prefix_lc)) {
			return false; /* prefix absent */
		}
		return true;
	}

	@SubscribeEvent
	public void onMessage(MessageReceivedEvent e) {
		User author = e.getAuthor();
		if (author.isBot()) {
			return;
		}

		DiscordCommandSender sender =
				new DiscordCommandSender(author, e.getChannel());
		String cmd = e.getMessage().getContentRaw();
		if (this.isDiscordCommand(cmd)) {
			this.execute(sender, cmd);
		} else if (cmd.equalsIgnoreCase(prefix)) {
			sender.sendMessage("Please specify a command.");
		}
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The {@code sender} must be from Discord (that being, it must be an
	 * instance of {@link DiscordCommandSender}). The command must also begin
	 * with the prefix specified in the constructor of this command center.
	 * Otherwise, this method will not execute the command.
	 */
	@Override
	public boolean execute(CommandSender sender, String cmd) {
		Objects.requireNonNull(sender, "sender");
		Objects.requireNonNull(cmd, "cmd");

		if (!(sender instanceof DiscordCommandSender)) {
			sender.sendMessage("Sender must be from Discord.");
			return false;
		} else if (!this.isDiscordCommand(cmd)) {
			sender.sendMessage("Not a Discord command.");
			return false;
		}

		cmd = cmd.substring(prefix.length() + 1);
		return super.execute(sender, cmd);
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @throws UnsupportedOperationException
	 *             since a Discord command center cannot run its own commands.
	 */
	@Override
	public boolean execute(String cmd) {
		throw new UnsupportedOperationException(
				"sender cannot be command center");
	}

}
