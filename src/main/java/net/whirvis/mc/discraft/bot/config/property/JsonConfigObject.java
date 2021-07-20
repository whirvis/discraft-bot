package net.whirvis.mc.discraft.bot.config.property;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * A {@code JsonObject} JSON config property for Discraft.
 */
public class JsonConfigObject extends JsonConfigProperty<JsonObject> {

	/**
	 * Creates a new {@code JsonObject} config property.
	 * 
	 * @param key
	 *            the property key.
	 * @throws NullPointerException
	 *             if {@code key} is {@code null}.
	 */
	public JsonConfigObject(String key) {
		super("JSON object", key);
	}

	@Override
	protected JsonObject parseJson(JsonElement element) {
		return element.getAsJsonObject();
	}

}
