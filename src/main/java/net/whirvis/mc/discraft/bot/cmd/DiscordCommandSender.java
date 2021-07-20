package net.whirvis.mc.discraft.bot.cmd;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.whirvex.cmd.CommandSender;

import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * A sender of a Discord command.
 * 
 * @see DiscordCommand
 */
public class DiscordCommandSender implements CommandSender {

	private final User user;
	private final MessageChannel channel;
	private boolean mention;

	/**
	 * Constructs a new {@code DiscordCommandSender}.
	 * 
	 * @param user
	 *            the Discord user.
	 * @param channel
	 *            the channel the command was sent in
	 *            ({@link #sendMessage(String)} will send messages to this
	 *            channel).
	 * @throws NullPointerException
	 *             if {@coder user} or {@code channel} are {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code user} is a Discord bot.
	 */
	public DiscordCommandSender(@NotNull User user,
			@NotNull MessageChannel channel) {
		this.user = Objects.requireNonNull(user, "user");
		this.channel = Objects.requireNonNull(channel, "channel");
		this.mention = true;
		if (user.isBot()) {
			throw new IllegalArgumentException(
					"bots cannot send Discord commands");
		}
	}

	/**
	 * Returns the Discord user.
	 * 
	 * @return the Discord user.
	 */
	@NotNull
	public User getUser() {
		return this.user;
	}

	@Override
	public String getName() {
		return user.getName();
	}

	/**
	 * Return the channel this command was sent in.
	 * <p>
	 * Calls to {@link #sendMessage(String)} send messages to this channel.
	 * 
	 * @return the channel the command was sent in.
	 */
	@NotNull
	public MessageChannel getChannel() {
		return this.channel;
	}

	/**
	 * Sets whether or not the user should be automatically mentioned at the
	 * beginning of message, assuming the message will be sent to a public text
	 * channel.
	 * <p>
	 * <b>Note:</b> If the message is not being sent to a public text channel,
	 * this method will have no effect on the message output.
	 * 
	 * @param mention
	 *            {@code true} if the user should be automatically mentioned,
	 *            {@code false} otherwise.
	 * @return this sender.
	 */
	@NotNull
	public DiscordCommandSender mention(boolean mention) {
		this.mention = mention;
		return this;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * If the channel this message is sent to is a public text channel, the user
	 * will be mentioned at the beginning of the message automatically.
	 */
	@Override
	public void sendMessage(String msg) {
		if (channel instanceof TextChannel && mention) {
			msg = user.getAsMention() + " " + msg;
		}
		channel.sendMessage(msg != null ? msg : "null").queue();
	}

}
