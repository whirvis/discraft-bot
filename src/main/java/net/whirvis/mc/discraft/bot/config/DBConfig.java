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

import net.whirvis.mc.discraft.bot.DiscraftUtils;
import net.whirvis.mc.discraft.bot.config.property.JsonConfigObject;
import net.whirvis.mc.discraft.bot.config.property.JsonConfigString;

/**
 * The database config for the Discraft bot.
 * 
 * @see #load(File)
 */
public class DBConfig {

	private static final JsonConfigString DB_URL =
			new JsonConfigString("db-url");
	private static final JsonConfigObject DB_USERS =
			new JsonConfigObject("db-users");

	private static final JsonConfigString DB_USER =
			new JsonConfigString("db-user");
	private static final JsonConfigString DB_PASS =
			new JsonConfigString("db-pass").fallback(null);

	private final File file;
	private String dbUrl;
	private Map<String, DBUser> dbUsers;

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

		this.dbUrl = DB_URL.get(config);

		JsonObject usersJson = DB_USERS.get(config);
		for (String name : usersJson.keySet()) {
			JsonConfigObject userConfig = new JsonConfigObject(name);
			JsonObject userJson = userConfig.get(usersJson);
			String dbUser = DB_USER.get(userJson);
			String dbPass = DB_PASS.get(userJson);

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
	@Config(key = "db-users")
	public DBUser getUser(String name) {
		return dbUsers.get(name);
	}

}
