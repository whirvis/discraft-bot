package net.whirvis.mc.discraft.bot;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import com.google.gson.JsonObject;
import com.whirvex.config.Config;
import com.whirvex.config.ConfigException;
import com.whirvex.config.ConfigManager;
import com.whirvex.config.JsonConfigManager;

/**
 * The config for the Discraft bot.
 * 
 * @see #load(File)
 */
public class DiscraftBotConfig {

	public static final File LANG_DIR = new File("./lang");

	private static final ConfigManager<?> CONFIG = new JsonConfigManager();

	private static final Config<String> LANG =
			new Config<>(String.class, "lang").fallback("en_us");
	private static final Config<String> DB_CONFIG =
			new Config<>(String.class, "db-config");
	private static final Config<String> BOT_TOKEN =
			new Config<>(String.class, "bot-token");
	private static final Config<Integer> WEBSERVER_PORT =
			new Config<>(int.class, "webserver-port").fallback(8080);

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
		String langId = CONFIG.load(LANG, config);
		this.lang = DiscraftLang.init(langId);

		/* specify the DB config in relation to this config file */
		String dbConfig = CONFIG.load(DB_CONFIG, config);
		this.dbConfigFile = new File(file.getParentFile(), dbConfig);

		this.botToken = CONFIG.load(BOT_TOKEN, config);
		this.webserverPort = CONFIG.load(WEBSERVER_PORT, config);
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
	public DiscraftLang getLang() {
		return this.lang;
	}

	/**
	 * Returns the path to the database config file.
	 * 
	 * @return the path to the database config file.
	 */
	@NotNull
	public File getDBConfigFile() {
		return this.dbConfigFile;
	}

	/**
	 * Returns the Discord bot token.
	 * 
	 * @return the Discord bot token.
	 */
	@NotNull
	public String getBotToken() {
		return this.botToken;
	}

	/**
	 * Returns the webserver port.
	 * 
	 * @return the webserver port.
	 */
	public int getWebserverPort() {
		return this.webserverPort;
	}

}
