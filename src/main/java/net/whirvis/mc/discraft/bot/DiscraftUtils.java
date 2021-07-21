package net.whirvis.mc.discraft.bot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * Common functions used by Discraft.
 */
public class DiscraftUtils {

	/* SnakeYAML is annoying, just convert it to JSON */
	private static final Gson GSON = new GsonBuilder().create();
	private static final Yaml YAML = new Yaml();

	/**
	 * Loads a YAML file and converts it to a {@link JsonElement}.
	 * <p>
	 * This is done so that the Google GSON API can be used to retrieve
	 * information from the file, rather than having to deal with SnakeYAML.
	 * 
	 * @param <T>
	 *            the JSON element type.
	 * @param file
	 *            the file to read.
	 * @return the YAML converted to JSON.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public static <T extends JsonElement> T loadYaml(File file)
			throws IOException {
		try (FileReader reader = new FileReader(file)) {
			JsonElement json = GSON.toJsonTree(YAML.load(reader));
			return (T) json;
		}
	}

	/**
	 * Loads a JSON file.
	 * 
	 * @param <T>
	 *            the JSON element type.
	 * @param file
	 *            the file to read.
	 * @return the parsed JSON.
	 * @throws IOException
	 *             if an I/O error occurs.
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public static <T extends JsonElement> T loadJson(File file)
			throws IOException {
		try (FileReader reader = new FileReader(file)) {
			JsonElement json = GSON.fromJson(reader, JsonObject.class);
			return (T) json;
		}
	}

}
