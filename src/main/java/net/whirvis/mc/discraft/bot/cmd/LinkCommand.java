package net.whirvis.mc.discraft.bot.cmd;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.whirvex.args.Args;
import com.whirvex.cmd.Command;
import com.whirvex.cmd.CommandCenter;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.PrivateChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.requests.RestAction;
import net.whirvis.mc.discraft.bot.DiscraftLang;
import net.whirvis.mc.discraft.bot.link.LinkManager;
import net.whirvis.mc.discraft.bot.link.LinkProcess;

/**
 * The {@code "link"} command, which initiates the linking process for a Discord
 * and Minecraft account together into a single Discraft profile.
 * 
 * @see DiscraftProfileCommand
 */
public class LinkCommand extends DiscordCommand {

	private final LinkManager linkManager;

	/**
	 * Creates a {@code "link"} command.
	 * 
	 * @param linkManager
	 *            the link manager.
	 * @throws NullPointerException
	 *             if {@code linkManager} is {@code null}.
	 */
	public LinkCommand(@NotNull LinkManager linkManager) {
		super("link", null, "Links together a Discord and a Minecraft account");
		this.linkManager = Objects.requireNonNull(linkManager, "linkManager");
	}

	@Override
	public boolean execute(CommandCenter center, DiscordCommandSender sender,
			Command cmd, Args args) {
		User user = sender.getUser();
		if (linkManager.isLinking(user)) {
			sender.sendMessage("You are already in the process of linking!");
			return true;
		}

		EmbedBuilder msg = new EmbedBuilder();

		msg.setAuthor("Whirvis - Discraft Author", "http://whirvis.com/",
				"https://i.imgur.com/8c8FwFE.png");
		msg.setTitle(DiscraftLang.getBotLang("link.intro.title"));
		msg.setThumbnail("https://i.imgur.com/5ogSj2q.png");

		LinkProcess link = linkManager.beginLinking(user);
		String linkIntro = DiscraftLang.getBotLang("link.intro.desc",
				user.getName(), link.getSecret());
		msg.setDescription(linkIntro);

		RestAction<PrivateChannel> action = user.openPrivateChannel();
		action.queue(channel -> {
			channel.sendMessage(msg.build()).queue();
		});
		return true;
	}

}
