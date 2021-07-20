package net.whirvis.mc.discraft.bot.config;

import net.whirvis.mc.discraft.bot.config.property.ConfigProperty;

/**
 * Signals that a config property is missing.
 */
public class MissingConfigException extends ConfigException {

	private static final long serialVersionUID = 5543471555408713742L;

	private static String calcMessage() {
		/* later, this will be multi-lingual */
		return "missing config";
	}

	private static String calcMessage(ConfigProperty<?, ?> property) {
		StringBuilder msg = new StringBuilder("missing config");
		msg.append(" \"" + property.getKey() + "\"");

		String desc = property.getDesc();
		if (desc != null) {
			msg.append(" (" + desc + ")");
		}
		return msg.toString();
	}

	/**
	 * Constructs a new {@code MissingConfigException} with
	 * {@code "missing config"} as its detail message. The cause is not
	 * initialized, and may subsequently be initialized by a call to
	 * {@link #initCause(Throwable)}.
	 */
	public MissingConfigException() {
		super(calcMessage());
	}

	/**
	 * Constructs a new {@code MissingConfigException} with the specified
	 * config property. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause(Throwable)}.
	 *
	 * @param config
	 *            the missing config property (which is used to generated the
	 *            detail message of this exception).
	 */
	public MissingConfigException(ConfigProperty<?, ?> config) {
		super(calcMessage(config));
	}

	/**
	 * Constructs a new {@code MissingConfigException} with the specified
	 * config property and cause.
	 *
	 * @param config
	 *            the missing config property (which is used to generated the
	 *            detail message of this exception).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public MissingConfigException(ConfigProperty<?, ?> config,
			Throwable cause) {
		super(calcMessage(config), cause);
	}

	/**
	 * Constructs a new {@code MissingConfigException} with the specified cause
	 * and a detail message of {@code "missing config"}. This constructor is
	 * useful for exceptions that are little more than wrappers for other
	 * throwables (for example,
	 * {@link java.security.PrivilegedActionException}).
	 *
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public MissingConfigException(Throwable cause) {
		super(calcMessage(), cause);
	}

}
