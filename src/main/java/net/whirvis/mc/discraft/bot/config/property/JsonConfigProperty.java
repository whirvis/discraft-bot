package net.whirvis.mc.discraft.bot.config.property;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.whirvis.mc.discraft.bot.config.MissingConfigException;
import net.whirvis.mc.discraft.bot.config.UnexpectedValueException;

/**
 * A JSON config property for Discraft.
 */
public abstract class JsonConfigProperty<T>
		extends ConfigProperty<JsonObject, T> {

	/**
	 * Creates a new JSON config property.
	 * 
	 * @param type
	 *            the property type name.
	 * @param key
	 *            the property key.
	 * @throws NullPointerException
	 *             if {@code type} or {@code key} are {@code null}.
	 */
	public JsonConfigProperty(String type, String key) {
		super(type, key);
	}

	/**
	 * Parses the config JSON to the property type.
	 * <p>
	 * The {@link #get(JsonObject)} method, which call this method after
	 * fetching the JSON element, will automatically handle some exceptions. For
	 * example: if a {@code ClassCastException} is encountered, it is caught and
	 * a {@link UnexpectedValueException} is thrown in its place instead.
	 * 
	 * @param element
	 *            the JSON element to parse.
	 * @return the parsed value, may be {@code null}.
	 * @throws UnexpectedValueException
	 *             if {@code element} cannot be parsed.
	 */
	@Nullable
	protected abstract T parseJson(JsonElement element);

	@Override
	public T get(JsonObject json) {
		JsonElement element = json.get(key);
		if (element == null) {
			if (fallback != null) {
				return fallback.value;
			} else {
				throw new MissingConfigException(this);
			}
		}

		try {
			return this.parseJson(element);
		} catch (ClassCastException e) {
			throw new UnexpectedValueException(this);
		}
	}

}
