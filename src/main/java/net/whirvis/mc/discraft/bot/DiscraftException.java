package net.whirvis.mc.discraft.bot;

/**
 * Signals that an error has occured relating to Discraft.
 */
public class DiscraftException extends RuntimeException {

	private static final long serialVersionUID = -4175561948586549166L;

	/**
	 * Constructs a new {@code DiscraftException} with {@code null} as its
	 * detail message. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause(Throwable)}.
	 */
	public DiscraftException() {
		super();
	}

	/**
	 * Constructs a new {@code DiscraftException} with the specified detail
	 * message. The cause is not initialized, and may subsequently be
	 * initialized by a call to {@link #initCause(Throwable)}.
	 *
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 */
	public DiscraftException(String message) {
		super(message);
	}

	/**
	 * Constructs a new {@code DiscraftException} with the specified detail
	 * message and cause.
	 * <p>
	 * Note that the detail message associated with {@code cause} is <i>not</i>
	 * automatically incorporated in this exception's detail message.
	 *
	 * @param message
	 *            the detail message (which is saved for later retrieval by the
	 *            {@link #getMessage()} method).
	 * @param cause
	 *            the cause (which is saved for later retrieval by the
	 *            {@link #getCause()} method). (A {@code null} value is
	 *            permitted, and indicates that the cause is nonexistent or
	 *            unknown.)
	 */
	public DiscraftException(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * Constructs a new {@code DiscraftException} with the specified cause and a
	 * detail message of {@code (cause==null ? null : cause.toString())} (which
	 * typically contains the class and detail message of {@code cause}). This
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
	public DiscraftException(Throwable cause) {
		super(cause);
	}

}
