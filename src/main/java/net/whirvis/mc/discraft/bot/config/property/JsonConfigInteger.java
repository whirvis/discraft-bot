package net.whirvis.mc.discraft.bot.config.property;

import com.google.gson.JsonElement;

import net.whirvis.mc.discraft.bot.config.UnexpectedValueException;

/**
 * An {@code int} JSON config property for Discraft.
 */
public class JsonConfigInteger extends JsonConfigProperty<Integer> {

	/**
	 * Creates a new {@code int} config property.
	 * 
	 * @param key
	 *            the property key.
	 * @throws NullPointerException
	 *             if {@code key} is {@code null}.
	 */
	public JsonConfigInteger(String key) {
		super("int", key);
	}

	@Override
	protected Integer parseJson(JsonElement element) {
		if (!element.isJsonPrimitive()) {
			throw new UnexpectedValueException(this);
		}
		return element.getAsInt();
	}

}
