package net.whirvis.mc.discraft.bot.config.property;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

/**
 * A {@code JsonArray} JSON config property for Discraft.
 */
public class JsonConfigArray extends JsonConfigProperty<JsonArray> {
	
	/**
	 * Creates a new {@code JsonArray} config property.
	 * 
	 * @param key
	 *            the property key.
	 * @throws NullPointerException
	 *             if {@code key} is {@code null}.
	 */
	public JsonConfigArray(String key) {
		super("JSON array", key);
	}

	@Override
	protected JsonArray parseJson(JsonElement element) {
		return element.getAsJsonArray();
	}

}
