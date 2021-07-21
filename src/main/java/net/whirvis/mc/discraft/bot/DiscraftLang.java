package net.whirvis.mc.discraft.bot;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import net.whirvis.mc.discraft.bot.config.property.JsonConfigArray;
import net.whirvis.mc.discraft.bot.config.property.JsonConfigString;

/**
 * Used to get localized text for sending messages to users.
 * 
 * @see #init()
 * @see #getRegistered()
 * @see #getLang(String, String)
 */
public class DiscraftLang {

	private static final File LANG_DIR = new File("./lang");
	private static final Gson GSON = new GsonBuilder().create();

	private static final JsonConfigString CONF_FALLBACK =
			new JsonConfigString("fallback").fallback("en_us");
	private static final JsonConfigArray CONF_LANGS =
			new JsonConfigArray("langs");

	private static final JsonConfigString CONF_LANG_ID =
			new JsonConfigString("id");
	private static final JsonConfigString CONF_LANG_NAME =
			new JsonConfigString("name");
	private static final JsonConfigString CONF_LANG_FALLBACK =
			new JsonConfigString("fallback").fallback(null);

	private static DiscraftLang botLang;
	private static final Map<String, DiscraftLang> REGISTERED = new HashMap<>();

	/**
	 * Converts a JSON array of primitives to a string.
	 * <p>
	 * This is done by taking each value in the JSON array and concatenating
	 * them together into one longer string. The only restriction to this is
	 * that each element <i>must</i> be a JSON primitive.
	 * 
	 * @param jsonArr
	 *            the JSON array whose elements to concatenate.
	 * @return the concatenated string.
	 * @throws JsonIOException
	 *             if an element of {@code jsonArr} is not a JSON primitive.
	 */
	private static String getMessage(JsonArray jsonArr) {
		if (jsonArr == null) {
			return null;
		}
		StringBuilder msg = new StringBuilder();
		for (JsonElement element : jsonArr) {
			if (!element.isJsonPrimitive()) {
				throw new JsonIOException("arrays can only contain primitives");
			}
			msg.append(element.getAsString());
		}
		return msg.toString();
	}

	/**
	 * Loads a language from the Discraft language config.
	 * <p>
	 * This method does <i>not</i> set the fallback language for the newly
	 * loaded language. Fallbacks must be set <i>after</i> all other languages
	 * have been loaded. This ensures that an existing language will not be
	 * mistakenly resolved to {@code null}.
	 * 
	 * @param langJson
	 *            the JSON object the language definition.
	 * @throws NullPointerException
	 *             if {@code id} or {@code langs} are {@code null}.
	 * @see #resolveFallback(DiscraftLang, JsonObject)
	 */
	@Nullable
	private static DiscraftLang loadLang(@NotNull JsonObject langJson) {
		Objects.requireNonNull(langJson, "langJson");

		String id = CONF_LANG_ID.get(langJson);
		if (!id.equals(id.toLowerCase())) {
			throw new JsonIOException("lang ID must be lowercase");
		}
		String name = CONF_LANG_NAME.get(langJson);
		String fallbackId = CONF_LANG_FALLBACK.get(langJson);

		File dir = new File(LANG_DIR, id);
		return new DiscraftLang(id, name, fallbackId, dir);
	}

	/**
	 * Initializes all Discraft language files as specified by the
	 * {@code langs.json} file in the {@code lang} directory of the bot.
	 * 
	 * @param botLangId
	 *            the bot language ID.
	 * @return the loaded bot language.
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws JsonIOException
	 *             if a language definition is not a JSON object or a language
	 *             ID is not in lowercase.
	 * @see #getRegistered()
	 * @see #getLang(String, String)
	 */
	public static DiscraftLang init(String botLangId) throws IOException {
		File langsFile = new File(LANG_DIR, "langs.json");
		if (!langsFile.exists() || !langsFile.isFile()) {
			throw new FileNotFoundException("missing langs file");
		}

		JsonObject config = DiscraftUtils.loadJson(langsFile);
		String fallbackId = CONF_FALLBACK.get(config);

		JsonArray langsArr = CONF_LANGS.get(config);
		for (JsonElement langJson : langsArr) {
			if (!langJson.isJsonObject()) {
				throw new JsonIOException(
						"lang definition must be a JSON object");
			}
			DiscraftLang lang = loadLang(langJson.getAsJsonObject());
			REGISTERED.put(lang.id, lang);
		}

		/*
		 * Fallbacks must be resolved *after* all the languages have actually
		 * been loaded in. Otherwise, existing languages could be mistakenly
		 * resolved to null.
		 */
		DiscraftLang panic = getRegistered(fallbackId);
		for (DiscraftLang lang : REGISTERED.values()) {
			lang.resolveFallback(panic).initDir(lang.dir);
		}

		botLang = getRegistered(botLangId);
		if (botLang == null) {
			throw new IllegalArgumentException("no such lang " + botLangId);
		}
		return botLang;
	}

	/**
	 * Returns the language of the bot.
	 * 
	 * @return the language of the bot.
	 * @see #getBotLang(String)
	 */
	@NotNull
	public static DiscraftLang getBotLang() {
		return botLang;
	}

	/**
	 * Returns a language mapping by its key.
	 * <p>
	 * This language mapping will come from the bot language.
	 * 
	 * @param key
	 *            the language key.
	 * @return the language mapping, {@code null} if none exists.
	 * @see #getBotLang()
	 */
	@Nullable
	public static String getBotLang(String key) {
		return botLang.getLang(key);
	}

	/**
	 * Returns all initialized Discraft languages.
	 * 
	 * @return all initialized Discraft languages.
	 */
	@NotNull
	public static Collection<DiscraftLang> getRegistered() {
		return Collections.unmodifiableCollection(REGISTERED.values());
	}

	/**
	 * Returns an initialized Discraft language by its ID.
	 * 
	 * @param id
	 *            the language ID.
	 * @return the language under {@code id}.
	 */
	@Nullable
	public static DiscraftLang getRegistered(@Nullable String id) {
		return id != null ? REGISTERED.get(id.toLowerCase()) : null;
	}

	/**
	 * Returns a language mapping by its key.
	 * <p>
	 * If the specified language does not contain a mapping, its fallback
	 * language will be queried for the mapping instead. If again no language
	 * mapping exists for the fallback language, {@code null} will be returned.
	 * 
	 * @param id
	 *            the language ID.
	 * @param key
	 *            the language key.
	 * @return the language mapping, {@code null} if none exists.
	 */
	@Nullable
	public static String getLang(@Nullable String id, @Nullable String key) {
		DiscraftLang lang = getRegistered(id);
		return lang != null ? lang.getLang(key) : null;
	}

	public final String id;
	public final String name;
	public final File dir;

	private final Map<String, String> lang;
	private String fallbackId;
	private DiscraftLang fallback;

	/**
	 * Constructs a new {@code DiscraftLang}.
	 * 
	 * @param id
	 *            the language ID.
	 * @param name
	 *            the language name.
	 * @param fallbackId
	 *            the fallback language ID.
	 * @param dir
	 *            the language directory.
	 * @throws NullPointerException
	 *             if {@code id}, {@code name}, or {@code dir} are {@code null}.
	 * @throws IllegalArgumentException
	 *             if {@code dir} does not exist or is not a directory.
	 * 
	 */
	private DiscraftLang(@NotNull String id, @NotNull String name,
			@Nullable String fallbackId, @NotNull File dir) {
		this.id = Objects.requireNonNull(id, "id");
		this.name = Objects.requireNonNull(name, "name");
		this.dir = Objects.requireNonNull(dir, "dir");
		if (!dir.exists()) {
			throw new IllegalArgumentException("dir must exist");
		} else if (!dir.isDirectory()) {
			throw new IllegalArgumentException("dir must be a directory");
		}
		this.lang = new HashMap<>();
	}

	/**
	 * Resolves the fallback language for a Discraft language.
	 * <p>
	 * This method <i>must</i> be called after all other languages found inside
	 * {@code langsJson} have been loaded. Otherwise, it is possible an existing
	 * language could be mistakenly resolved to {@code null}.
	 * 
	 * @param fallback
	 *            the fallback language to use if the intended fallback language
	 *            could not be resolved.
	 * @return this language.
	 * @throws IllegalStateException
	 *             if the fallback ID has already been resolved.
	 */
	@NotNull
	private DiscraftLang resolveFallback(DiscraftLang panic) {
		if (fallbackId == null) {
			return this;
		}
		DiscraftLang fallback = getRegistered(fallbackId);
		this.fallback(fallback != null ? fallback : panic);
		this.fallbackId = null;
		return this;
	}

	/**
	 * Updates the fallback language for this language.
	 * 
	 * @param fallback
	 *            the fallback language.
	 * @return this language.
	 * @throws IllegalArgumentException
	 *             if {@code fallback} is {@code this} or results in a circular
	 *             fallback. A circular fallbacks occurs when one language falls
	 *             back to another language that falls back to itself.
	 */
	@NotNull
	private DiscraftLang fallback(@Nullable DiscraftLang fallback) {
		if (fallback == this) {
			throw new IllegalArgumentException("fallback cannot be this");
		}

		/*
		 * This works by specifying a language that doesn't (or at least
		 * shouldn't?) exist in this language (or the fallback language). If
		 * calling this function results in a StackOverflowError from Java, that
		 * means a circular fallback has been specified.
		 */
		if (fallback != null) {
			try {
				fallback.getLang("The name's Swindle!\n"
						+ "I'm what you call an intergalatic arms dealer.\n"
						+ "And now, thanks to you...\n"
						+ "I now have the slickest weapon in the galaxy!");
			} catch (StackOverflowError e) {
				throw new IllegalArgumentException("circular fallbacks");
			}
		}

		this.fallback = fallback;
		return this;
	}

	/**
	 * Returns the fallback language.
	 * 
	 * @return the fallback language.
	 */
	@Nullable
	public DiscraftLang fallback() {
		return this.fallback;
	}

	private void addLang(String key, JsonElement langJson) {
		if (langJson.isJsonPrimitive()) {
			String message = langJson.getAsString();
			lang.put(key, message);
		} else if (langJson.isJsonArray()) {
			JsonArray jsonArr = langJson.getAsJsonArray();
			lang.put(key, getMessage(jsonArr));
		} else {
			throw new JsonIOException("JSON can only contain primitives"
					+ " or arrays of primitives");
		}
	}

	/**
	 * Initializes a file for this languages.
	 * 
	 * @param file
	 *            the file to initialize.
	 * @throws IOException
	 *             if an I/O error occurs.
	 * @throws JsonIOException
	 *             if {@code file} is not a valid JSON file.
	 */
	private void initFile(File file) throws IOException {
		if (!file.getName().endsWith(".json")) {
			throw new JsonIOException("must be a JSON file");
		}

		try (FileReader reader = new FileReader(file)) {
			JsonObject langJson = GSON.fromJson(reader, JsonObject.class);
			for (String key : langJson.keySet()) {
				this.addLang(key, langJson.get(key));
			}
		} catch (JsonSyntaxException e) {
			throw new JsonIOException("must be a valid JSON file", e);
		}
	}

	/**
	 * Initializes a directory for this language.
	 * <p>
	 * All files found within this directory will be initialized via
	 * {@link #initFile(File)}. Any other directories will be initialized
	 * recursively via this method.
	 * 
	 * @param dir
	 *            the directory to initialize.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private void initDir(File dir) throws IOException {
		for (File langFile : dir.listFiles()) {
			if (langFile.isDirectory()) {
				this.initDir(langFile);
				continue;
			}
			this.initFile(langFile);
		}
	}

	/**
	 * Returns a language mapping by its key.
	 * <p>
	 * If the current language does not contain a mapping, its fallback language
	 * will be queried for the mapping instead. If again no language mapping
	 * exists for the fallback language, {@code null} will be returned.
	 * 
	 * @param key
	 *            the language key.
	 * @return the language mapping, {@code null} if none exists.
	 */
	@Nullable
	public String getLang(@Nullable String key) {
		if (key == null) {
			return null;
		}
		String msg = lang.get(key);
		if (msg == null && fallback != null) {
			msg = fallback.getLang(key);
		}
		return msg;
	}

}
