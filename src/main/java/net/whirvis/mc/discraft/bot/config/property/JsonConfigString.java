package net.whirvis.mc.discraft.bot.config.property;

import com.google.gson.JsonElement;

import net.whirvis.mc.discraft.bot.config.UnexpectedValueException;

/**
 * A {@code String} JSON config property for Discraft.
 */
public class JsonConfigString extends JsonConfigProperty<String> {

	/**
	 * Creates a new {@code String} config property.
	 * 
	 * @param key
	 *            the property key.
	 * @throws NullPointerException
	 *             if {@code key} is {@code null}.
	 */
	public JsonConfigString(String key) {
		super("string", key);
	}

	@Override
	protected String parseJson(JsonElement element) {
		if (!element.isJsonPrimitive()) {
			throw new UnexpectedValueException(this);
		}
		return element.getAsString();
	}

}
