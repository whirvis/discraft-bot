package net.whirvis.mc.discraft.bot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
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

/**
 * Used to get localized text for sending messages to users.
 * <p>
 * TODO: At the moment, the bot can only work with one language at a time (two
 * if you include the fallback language). In the future, this class should be
 * capable of fetching localized text for Discord users also.
 * 
 * @see #addFile(File, boolean)
 * @see #getLang(String)
 */
public class DiscraftLang {

	private static final Gson GSON = new GsonBuilder().create();
	private static final Map<String, String> FALLBACK = new HashMap<>();
	private static final Map<String, String> LANG = new HashMap<>();

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
	 * Registers a language mapping from its JSON element to the specified
	 * destination map.
	 * 
	 * @param key
	 *            the language key.
	 * @param json
	 *            the value JSON.
	 * @param dest
	 *            where to add the mappings to.
	 * @throws JsonIOException
	 *             if {@code json} is not a JSON primitive or array or an
	 *             element of {@code json} is not a JSON primitive.
	 */
	private static void addLang(String key, JsonElement json,
			Map<String, String> dest) {
		if (json.isJsonPrimitive()) {
			String message = json.getAsString();
			dest.put(key, message);
		} else if (json.isJsonArray()) {
			JsonArray jsonArr = json.getAsJsonArray();
			dest.put(key, getMessage(jsonArr));
		} else {
			throw new JsonIOException("JSON can only contain primitives"
					+ " or arrays of primitives");
		}
	}

	/**
	 * Registers a language file's mappings to the specified destination map.
	 * <p>
	 * If the given file is a directory, this function will be called
	 * recursively for all files contained within it.<br>
	 * All files also must be valid JSON files that end with the extension
	 * {@code .json}, lest a {@code JsonIOException} shall be thrown.
	 * 
	 * @param file
	 *            the language file.
	 * @param dest
	 *            where to add the mappings to.
	 * @throws NullPointerException
	 *             if {@code file} or {@code dest} are {@code null}.
	 * @throws JsonIOException
	 *             if {@code file} is not a valid JSON file.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	private static void addFile(File file, Map<String, String> dest)
			throws IOException {
		Objects.requireNonNull(file, "file");
		Objects.requireNonNull(dest, "dest");
		if (file.isDirectory()) {
			for (File sub : file.listFiles()) {
				addFile(sub, dest);
			}
			return;
		} else if (!file.getName().endsWith(".json")) {
			throw new JsonIOException("must be a JSON file");
		}

		try (FileReader reader = new FileReader(file)) {
			JsonObject langJson = GSON.fromJson(reader, JsonObject.class);
			for (String key : langJson.keySet()) {
				addLang(key, langJson.get(key), dest);
			}
		} catch (JsonSyntaxException e) {
			throw new JsonIOException("must be a valid JSON file", e);
		}
	}

	/**
	 * Registers a language file's mappings.
	 * <p>
	 * If the given file is a directory, this function will be called
	 * recursively for all files contained within it.<br>
	 * All files also must be valid JSON files that end with the extension
	 * {@code .json}, lest a {@code JsonIOException} shall be thrown.
	 * 
	 * @param file
	 *            the language file.
	 * @param fallback
	 *            {@code true} if the mappings should be considered fallback
	 *            values for other mappings, {@code false} otherwise.
	 * @throws NullPointerException
	 *             if {@code file} or {@code dest} are {@code null}.
	 * @throws JsonIOException
	 *             if {@code file} is not a valid JSON file.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	public static void addFile(@NotNull File file, boolean fallback)
			throws IOException {
		addFile(file, fallback ? FALLBACK : LANG);
	}

	/**
	 * Returns a language mapping by its key.
	 * <p>
	 * If the current language does not contain a mapping, the fallback language
	 * will be used instead. If no language mapping exists within the fallback
	 * language, {@code null} will be returned instead.
	 * 
	 * @param key
	 *            the language key.
	 * @return the language mapping, {@code null} if none exists.
	 */
	@Nullable
	public static String getLang(@Nullable String key) {
		if (key == null) {
			return null;
		}
		String lang = LANG.get(key);
		if (lang == null) {
			lang = FALLBACK.get(key);
		}
		return lang;
	}

}
