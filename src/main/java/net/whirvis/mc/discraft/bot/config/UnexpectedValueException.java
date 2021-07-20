package net.whirvis.mc.discraft.bot.config;

import net.whirvis.mc.discraft.bot.config.property.ConfigProperty;

/**
 * Signals that a config property has an unexpected value.
 */
public class UnexpectedValueException extends ConfigException {

	private static final long serialVersionUID = -238702010140397071L;

	private static String calcMessage() {
		/* later, this will be multi-lingual */
		return "illegal value for config";
	}

	private static String calcMessage(ConfigProperty<?, ?> property) {
		StringBuilder msg = new StringBuilder("illegal value for config");
		msg.append(" \"" + property.getKey() + "\", ");
		msg.append("expected" + property.getTypeName());
		return msg.toString();
	}

	/**
	 * Constructs a new {@code UnexpectedValueException} with
	 * {@code "illegal value for config"} as its detail message. The cause is
	 * not initialized, and may subsequently be initialized by a call to
	 * {@link #initCause(Throwable)}.
	 */
	public UnexpectedValueException() {
		super(calcMessage());
	}

	/**
	 * Constructs a new {@code UnexpectedValueException} with the specified
	 * config property. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause(Throwable)}.
	 *
	 * @param config
	 *            the unexpected config property (which is used to generated the
	 *            detail message of this exception).
	 */
	public UnexpectedValueException(ConfigProperty<?, ?> property) {
		super(calcMessage(property));
	}

	/**
	 * Constructs a new {@code UnexpectedValueException} with the specified
	 * config property and cause.
	 *
	 * @param config
	 *            the unexpected config property (which is used to generated the
	 *            detail message of this exception).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public UnexpectedValueException(ConfigProperty<?, ?> property,
			Throwable cause) {
		super(calcMessage(property), cause);
	}

	/**
	 * Constructs a new {@code UnexpectedValueException} with the specified
	 * cause and a detail message of {@code "illegal value for config"}. This
	 * constructor is useful for exceptions that are little more than wrappers
	 * for other throwables (for example,
	 * {@link java.security.PrivilegedActionException}).
	 *
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public UnexpectedValueException(Throwable cause) {
		super(calcMessage(), cause);
	}

}
