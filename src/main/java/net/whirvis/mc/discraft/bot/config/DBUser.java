package net.whirvis.mc.discraft.bot.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a database user in the Discraft config.
 */
public class DBUser {

	private String db;
	private String user;
	private String pass;
	
	private boolean open;

	/**
	 * Constructs a new {@code DBUser}.
	 * 
	 * @param db
	 *            the database URL.
	 * @param user
	 *            the database user.
	 * @param pass
	 *            the user password, may be {@code null}.
	 * @throws NullPointerException
	 *             if {@code db} or {@code user} are {@code null}.
	 */
	public DBUser(@NotNull String db, @NotNull String user,
			@Nullable String pass) {
		this.db = Objects.requireNonNull(db, "db");
		this.user = Objects.requireNonNull(user, "user");
		this.pass = pass;
	}

	/**
	 * Constructs a new {@code DBUser}.
	 * 
	 * @param db
	 *            the database URL.
	 * @param user
	 *            the database user.
	 * @throws NullPointerException
	 *             if {@code db} or {@code user} are {@code null}.
	 */
	public DBUser(@NotNull String db, @NotNull String user) {
		this(db, user, null);
	}

	/**
	 * Opens the database connection and returns.
	 * <p>
	 * After the first invocation, the connection can no longer be retrieved via
	 * this method. This is done for security reasons. It is also up to the new
	 * owner of the connection to close it once they are done with it.
	 *
	 * @return the open database connection.
	 * @throws SQLException
	 *             if an SQL error occurs.
	 */
	@NotNull
	public Connection conn() throws SQLException {
		if (open) {
			throw new SQLException("already connected");
		}

		Connection conn = DriverManager.getConnection(db, user, pass);
		this.db = null;
		this.user = null;
		this.pass = null;
		this.open = true;
		return conn;
	}

}
