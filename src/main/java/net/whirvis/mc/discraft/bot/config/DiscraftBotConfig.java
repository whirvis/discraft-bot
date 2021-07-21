package net.whirvis.mc.discraft.bot.config;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;

import net.whirvis.mc.discraft.bot.DiscraftLang;
import net.whirvis.mc.discraft.bot.DiscraftUtils;
import net.whirvis.mc.discraft.bot.config.property.JsonConfigInteger;
import net.whirvis.mc.discraft.bot.config.property.JsonConfigString;

/**
 * The config for the Discraft bot.
 * 
 * @see #load(File)
 */
public class DiscraftBotConfig {

	public static final File LANG_DIR = new File("./lang");

	private static final JsonConfigString LANG =
			new JsonConfigString("lang").fallback("en_us");
	private static final JsonConfigString DB_CONFIG =
			new JsonConfigString("db-config");
	private static final JsonConfigString BOT_TOKEN =
			new JsonConfigString("bot-token");
	private static final JsonConfigInteger WEBSERVER_PORT =
			new JsonConfigInteger("webserver-port").fallback(8080);

	private final File file;

	private DiscraftLang lang;
	private File dbConfigFile;
	private String botToken;
	private int webserverPort;

	/**
	 * Loads a Discord bot config.
	 * <p>
	 * Loading the bot config will also load the Discraft language files
	 * automatically. Both the fallback language and the requested langage are
	 * loaded, just in case the requested language is missing required text.
	 * 
	 * @param file
	 *            the config file.
	 * @throws NullPointerException
	 *             if {@code file} is {@code null}.
	 * @throws ConfigException
	 *             if a config error occurs.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	@NotNull
	public static DiscraftBotConfig load(@NotNull File file)
			throws IOException {
		DiscraftBotConfig config = new DiscraftBotConfig(file);
		config.load();
		return config;
	}

	private DiscraftBotConfig(File file) {
		this.file = Objects.requireNonNull(file, "file");
	}

	private void load() throws IOException {
		JsonObject config = DiscraftUtils.loadYaml(file);

		/*
		 * Immediately load the language files as soon as possible, in case
		 * there are any others relating to the configuration after this!
		 */
		String langId = LANG.get(config);
		this.lang = DiscraftLang.init(langId);

		/* specify the DB config in relation to this config file */
		String dbConfig = DB_CONFIG.get(config);
		this.dbConfigFile = new File(file.getParentFile(), dbConfig);

		this.botToken = BOT_TOKEN.get(config);
		this.webserverPort = WEBSERVER_PORT.get(config);
	}

	/**
	 * Returns the config file.
	 * 
	 * @return the config file.
	 */
	@NotNull
	public File getFile() {
		return this.file;
	}

	/**
	 * Returns the default language.
	 * 
	 * @return the default language.
	 */
	@NotNull
	@Config(key = "lang", fallback = "en_us")
	public DiscraftLang getLang() {
		return this.lang;
	}

	/**
	 * Returns the path to the database config file.
	 * 
	 * @return the path to the database config file.
	 */
	@NotNull
	@Config(key = "db-config")
	public File getDBConfigFile() {
		return this.dbConfigFile;
	}

	/**
	 * Returns the Discord bot token.
	 * 
	 * @return the Discord bot token.
	 */
	@NotNull
	@Config(key = "bot-token")
	public String getBotToken() {
		return this.botToken;
	}

	/**
	 * Returns the webserver port.
	 * 
	 * @return the webserver port.
	 */
	@Config(key = "webserver-port", fallback = "8080")
	public int getWebserverPort() {
		return this.webserverPort;
	}

}
