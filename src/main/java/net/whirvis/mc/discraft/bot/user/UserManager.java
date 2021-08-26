package net.whirvis.mc.discraft.bot.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

import net.dv8tion.jda.api.entities.User;

public class UserManager {

	private static final String FUCK =
			"SELECT discord_id FROM user WHERE discord_id = ?";

	private final Connection conn;

	private final Map<Long, DiscraftUser> cachedUsers;
	private long lastFetchClear;

	public UserManager(Connection conn) {
		this.conn = conn;
		this.cachedUsers = new HashMap<>();
	}

	public DiscraftUser getUser(long discordId) throws SQLException {
		DiscraftUser registered = cachedUsers.get(discordId);
		if (registered != null) {
			registered.updateRefCount();
			return registered;
		}

		PreparedStatement stmt = conn.prepareStatement(FUCK);
		stmt.setLong(1, discordId);
		ResultSet set = stmt.executeQuery();
		
		if (!set.next()) {
			return null;
		}
		
		/* sanity check */
		long storedId = set.getLong("discord_id");
		if(storedId != discordId) {
			throw new SQLException("failed sanity check");
		}
		
		registered = new DiscraftUser(conn, discordId);
		cachedUsers.put(discordId, registered);
		return registered;
	}
	
	public DiscraftUser getUser(User user) throws SQLException {
		return getUser(user.getIdLong());
	}

	public boolean isRegistered(long discordId) throws SQLException {
		return getUser(discordId) != null;
	}

	public boolean isRegistered(User user) throws SQLException {
		return this.isRegistered(user.getIdLong());
	}

	public DiscraftUser createUser(User user) throws SQLException {
		Objects.requireNonNull(user, "user");
		if (this.isRegistered(user)) {
			throw new IllegalStateException("user already registered");
		}

		Objects.requireNonNull(user, "user");

		long currentTime = System.currentTimeMillis();

		PreparedStatement stmt =
				conn.prepareStatement("INSERT INTO user VALUES(?, ?)");
		stmt.setLong(1, user.getIdLong());
		stmt.setTimestamp(2, new Timestamp(currentTime));
		stmt.execute();
		
		DiscraftUser registered = new DiscraftUser(conn, user.getIdLong());
		cachedUsers.put(user.getIdLong(), registered);
		return registered;
	}
	
	public void update() throws SQLException {
		long currentTime = System.currentTimeMillis();
		Iterator<DiscraftUser> usersI = cachedUsers.values().iterator();
		while(usersI.hasNext()) {
			DiscraftUser user = usersI.next();
			if(currentTime - user.lastUpdated() >= 1000L) {
				user.update();
			}
			
			if(currentTime - user.getLastReffed() >= 10000L) {
				if(!user.hasReference()) {
					user.update();
					usersI.remove();
					System.out.println("wiped from memory");
				}
			}
		}
	}

}
