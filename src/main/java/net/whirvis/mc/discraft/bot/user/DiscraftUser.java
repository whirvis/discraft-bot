package net.whirvis.mc.discraft.bot.user;

import java.io.Closeable;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * A user of the Discraft Discord bot.
 * <p>
 * 
 */
public class DiscraftUser implements Closeable {

	private final Connection conn;
	private final long discordId;

	private UserSettings settings;
	
	private long refCount;
	private long lastReffed;
	private long lastUpdated;

	

	protected DiscraftUser(Connection conn, long discordId) {
		this.conn = conn;
		this.discordId = discordId;
		
		this.lastReffed = System.currentTimeMillis();
	}

	public UserSettings getSettings() throws SQLException {
		if (settings != null) {
			return settings;
		}

		/*
		 * 
		 */
		PreparedStatement stmt = conn.prepareStatement("SELECT discord_id "
				+ "FROM user_settings WHERE discord_id = ?");
		stmt.setLong(1, discordId);
		ResultSet set = stmt.executeQuery();
		if (!set.next()) {
			stmt = conn.prepareStatement(
					"INSERT INTO user_settings VALUES(?, DEFAULT)");
			stmt.setLong(1, discordId);
			stmt.execute();
		}

		this.settings = new UserSettings(conn, discordId);
		return settings;
	}
	
	public long lastUpdated() {
		return this.lastUpdated;
	}

	public void update() throws SQLException {
		long currentTime = System.currentTimeMillis();
		if (settings != null) {
			settings.update();
		}
		this.lastUpdated = currentTime;
	}

	public long getLastReffed() {
		return this.lastReffed;
	}
	
	public void updateRefCount() {
		this.refCount++;
		this.lastReffed = System.currentTimeMillis();
	}
	
	public boolean hasReference() {
		return refCount > 0;
	}

	@Override
	public void close() throws IOException {
		this.refCount--;
	}

}
