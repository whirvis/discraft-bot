package net.whirvis.mc.discraft.bot;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Objects;

import javax.security.auth.login.LoginException;

import com.whirvex.args.Args;
import com.whirvex.args.ArgsParser;
import com.whirvex.cmd.HelpCommand;
import com.whirvex.config.ConfigException;
import com.whirvex.event.EventManager;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.whirvis.mc.discraft.bot.cmd.DiscordCommandCenter;
import net.whirvis.mc.discraft.bot.cmd.LangCommand;
import net.whirvis.mc.discraft.bot.cmd.RegisterCommand;
import net.whirvis.mc.discraft.bot.config.DBConfig;
import net.whirvis.mc.discraft.bot.config.DBUser;
import net.whirvis.mc.discraft.bot.user.UserManager;
import net.whirvis.mc.discraft.bot.web.DiscraftWebserver;

/**
 * The Discraft bot.
 */
public class DiscraftBot {

	private final File configFile;
	private final EventManager events;
	private final DiscordCommandCenter guildCmds;

	private DiscraftBotConfig botConfig;
	private DBConfig dbConfig;

	private JDA discord;
	private UserManager userManager;
	private DiscraftWebserver webserver;

	/**
	 * Constructs a new {@code DiscraftBot}.
	 * 
	 * @param configFile
	 *            the bot config file.
	 * @throws NullPointerException
	 *             if {@code configFile} is {@code null}.
	 * @see #start()
	 */
	public DiscraftBot(File configFile) {
		this.configFile = Objects.requireNonNull(configFile, "configFile");
		this.events = new EventManager();
		this.guildCmds = new DiscordCommandCenter("!discraft", "guild", events);
	}

	private JDA createDiscord() throws LoginException {
		JDABuilder builder = JDABuilder.createLight(botConfig.getBotToken());
		builder.setEnabledIntents(GatewayIntent.GUILD_PRESENCES,
				GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES);
		builder.enableCache(CacheFlag.ACTIVITY);
		builder.setEventManager(new AnnotatedEventManager());
		builder.addEventListeners(this);
		builder.addEventListeners(guildCmds);
		return builder.build();
	}

	private DiscraftWebserver createWebserver() {
		DiscraftWebserver webserver = new DiscraftWebserver(botConfig.getWebserverPort());
		/* TODO: Register endpoints, etc. */
		return webserver;
	}

	/**
	 * Starts the Discraft bot.
	 * 
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws SQLException
	 *             if an SQL error occurs.
	 * @throws DiscraftException
	 *             if a Discraft error occurs.
	 */
	public void start() throws IOException, SQLException {
		guildCmds.register(new HelpCommand());

		this.botConfig = DiscraftBotConfig.load(configFile);
		this.dbConfig = DBConfig.load(botConfig.getDBConfigFile());

		try {
			this.discord = this.createDiscord();
			discord.awaitReady();
		} catch (LoginException | InterruptedException e) {
			throw new DiscraftException("Discord login failure", e);
		}

		DBUser dbum = dbConfig.getUser("user-manager");
		this.userManager = new UserManager(dbum.conn());
		guildCmds.register(new RegisterCommand(userManager));
		guildCmds.register(new LangCommand(userManager));

		this.webserver = this.createWebserver();
		webserver.start();
		
		while(true) {
			try {
				Thread.sleep(0, 1);
			} catch (InterruptedException e) {
				/* ignore */
			}
			userManager.update();
		}
	}

	public static void main(String[] jvmArgs) throws Exception {
		Args args = ArgsParser.parse(jvmArgs);
		args.require.indexc(1); /* config file */

		try {
			File configFile = new File(args.get(0));
			DiscraftBot bot = new DiscraftBot(configFile);
			bot.start();
		} catch (ConfigException e) {
			/* TODO: Use Log4j2 */
			System.err.println(e.getMessage());
		}
	}

}
