package net.whirvis.mc.discraft.bot.config.property;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonObject;

import net.whirvis.mc.discraft.bot.DiscraftLang;
import net.whirvis.mc.discraft.bot.config.MissingConfigException;
import net.whirvis.mc.discraft.bot.config.UnexpectedValueException;

/**
 * A config property for Discraft.
 * 
 * @param <C>
 *            the config type.
 * @param <T>
 *            the config value type.
 */
public abstract class ConfigProperty<C, T> {

	protected static class Fallback<T> {

		public final T value;

		public Fallback(T value) {
			this.value = value;
		}

	}

	private static final String DESC_KEY = "config.%s";

	protected final String typeName;
	protected final String key;
	protected Fallback<T> fallback;

	/**
	 * Constructs a new {@code ConfigProperty}.
	 * 
	 * @param typeName
	 *            the property type name.
	 * @param key
	 *            the property key.
	 * @throws NullPointerException
	 *             if {@code typeName} or {@code key} are {@code null}.
	 */
	public ConfigProperty(@NotNull String typeName, @NotNull String key) {
		this.typeName = Objects.requireNonNull(typeName, "typeName");
		this.key = Objects.requireNonNull(key, "key");
	}

	/**
	 * In the event the property is not present for {@link #get(JsonObject)},
	 * the value set here will be returned instead. This is useful for
	 * properties that are not essential and/or can have a reasonable fallback
	 * value.
	 * 
	 * @param <H>
	 *            the config property type, for convenience.
	 * @param fallback
	 *            the value to fallback to.
	 * @return this property.
	 */
	@NotNull
	@SuppressWarnings("unchecked")
	public <H extends ConfigProperty<C, T>> H fallback(@Nullable T fallback) {
		this.fallback = new Fallback<T>(fallback);
		return (H) this;
	}

	/**
	 * Returns the fallback value that will be used in the event the property is
	 * not present for {@link #get(JsonObject)}. This is useful for properties
	 * that are not essential and/or can have a reasonable fallback value.
	 * 
	 * @return the fallback value, {@code null} if one has not been set.
	 * @see #fallback(Object)
	 */
	@Nullable
	public T fallback() {
		if (fallback == null) {
			return null;
		}
		return fallback.value;
	}

	/**
	 * Returns the property type name.
	 * 
	 * @return the property type name.
	 */
	@NotNull
	public final String getTypeName() {
		return this.typeName;
	}

	/**
	 * Returns the property key.
	 * 
	 * @return the property key.
	 */
	@NotNull
	public final String getKey() {
		return this.key;
	}

	/**
	 * Returns the description for this property.
	 * <p>
	 * Descriptions are returned in the current language of the bot. If a
	 * description does not exist for the current language, the fallback
	 * language will be used. If no description can be found still, {@code null}
	 * is returned instead.
	 * <p>
	 * To specify a description for a config property, create a new JSON file.
	 * The file name <i>must</i> end with {@code .json} (preferably, name it
	 * something along the lines of {@code config.json}). Next, insert it into
	 * the {@code lang/%lang%} folder, with {@code %lang%} being the desired
	 * language. If the folder doesn't exist, create it. Inside of this JSON
	 * file, name the property {@code config.%key%}, with {@code %key%} being
	 * the property key as specified in this constructor. The value will be the
	 * description for this property in that language.
	 * 
	 * @return the description for this property.
	 * @see DiscraftLang
	 */
	@Nullable
	public final String getDesc() {
		String langKey = String.format(DESC_KEY, key);
		return DiscraftLang.getLang(langKey);
	}

	/**
	 * Gets the property from the specified config.
	 * 
	 * @param config
	 *            the config to read from.
	 * @return the parsed property value, possibly {@code null}.
	 * @throws MissingConfigException
	 *             if {@code json} does not contain the property.
	 * @throws UnexpectedValueException
	 *             if the property value cannot be parsed.
	 * @see #fallback(Object)
	 */
	@Nullable
	public abstract T get(C config);

}
