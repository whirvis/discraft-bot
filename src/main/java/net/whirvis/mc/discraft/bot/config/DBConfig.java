package net.whirvis.mc.discraft.bot.config;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.whirvex.config.Config;
import com.whirvex.config.ConfigException;
import com.whirvex.config.ConfigManager;
import com.whirvex.config.JsonConfigManager;

import net.whirvis.mc.discraft.bot.DiscraftUtils;

/**
 * The database config for the Discraft bot.
 * 
 * @see #load(File)
 */
public class DBConfig {

	private static final ConfigManager<?> CONFIG = new JsonConfigManager();

	private static final Config<String> DB_URL =
			new Config<>(String.class, "db-url");
	private static final Config<JsonObject> DB_USERS =
			new Config<>(JsonObject.class, "db-users");
	private static final Config<String> DB_USER =
			new Config<>(String.class, "db-user");
	private static final Config<String> DB_PASS =
			new Config<>(String.class, "db-pass");

	private final File file;
	private Map<String, DBUser> dbUsers;
	private String dbUrl;

	/**
	 * Loads a database config.
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
	public static DBConfig load(@NotNull File file) throws IOException {
		DBConfig dbConfig = new DBConfig(file);
		dbConfig.load();
		return dbConfig;
	}

	private DBConfig(File file) {
		this.file = Objects.requireNonNull(file, "file");
		this.dbUsers = new HashMap<>();
	}

	private void load() throws IOException {
		JsonElement yaml = DiscraftUtils.loadYaml(file);
		JsonObject config = yaml.getAsJsonObject();
		this.dbUrl = CONFIG.load(DB_URL, config);

		JsonObject usersJson = CONFIG.load(DB_USERS, config);
		for (String name : usersJson.keySet()) {
			Config<JsonObject> userConfig =
					new Config<>(JsonObject.class, name);
			JsonObject userJson = CONFIG.load(userConfig, usersJson);
			String dbUser = CONFIG.load(DB_USER, userJson);
			String dbPass = CONFIG.load(DB_PASS, userJson);
			DBUser user = new DBUser(dbUrl, dbUser, dbPass);
			dbUsers.put(name, user);
		}
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
	 * Returns a database user by their name.
	 * 
	 * @param name
	 *            the user name, as specified in the config.
	 * @return the user, {@code null} if none exist by that name.
	 */
	@Nullable
	public DBUser getUser(String name) {
		return dbUsers.get(name);
	}

}
