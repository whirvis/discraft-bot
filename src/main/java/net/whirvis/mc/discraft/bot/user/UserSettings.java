package net.whirvis.mc.discraft.bot.user;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.whirvis.mc.discraft.bot.util.Cached;
import net.whirvis.mc.discraft.bot.util.CachedException;

public class UserSettings {

	private static List<String> getList(String... values) {
		List<String> list = new ArrayList<>();
		for (String s : values) {
			list.add(s);
		}
		return Collections.unmodifiableList(list);
	}

	private static final String SQL_TABLE = "user_settings";
	private static final List<String> SQL_COLUMNS = getList("lang");

	private static void updateColumn(Connection conn, long discordId,
			String column, Object value) throws SQLException {
		if (!SQL_COLUMNS.contains(column)) {
			throw new IllegalArgumentException("no such column");
		}
		PreparedStatement stmt = conn.prepareStatement("UPDATE " + SQL_TABLE
				+ " SET " + column + " = ? WHERE discord_id = ?");
		stmt.setObject(1, value);
		stmt.setLong(2, discordId);
		stmt.execute();
	}

	@SuppressWarnings("unchecked")
	private static <T> T queryColumn(Connection conn, long discordId,
			String column) throws SQLException {
		if (!SQL_COLUMNS.contains(column)) {
			throw new IllegalArgumentException("no such column");
		}
		String querySql = "SELECT " + column + " FROM " + SQL_TABLE
				+ " WHERE discord_id = ?";
		PreparedStatement stmt = conn.prepareStatement(querySql,
				ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		ResultSet set = stmt.executeQuery();
		if (!set.next()) {
			return null;
		} else if (set.getLong("discord_id") != discordId) {
			// TODO: Sanity failure
		}
		return (T) set.getObject(column);
	}

	private final Connection conn;
	private final long discordId;
	private final Cached<String> dbLang;
	
	private class CachedSQL<T> extends Cached<T> {
		
		private final String column;
		
		public CachedSQL(String column) {
			super(null, null);
			this.column = column;
		}
		
		private String querySQL() {
			try {
				return queryColumn(conn, discordId, "lang");
			} catch (SQLException e) {
				throw new CachedException(e);
			}
		}

		private void flushSQL(String lang) {
			try {
				updateColumn(conn, discordId, "lang", lang);
			} catch (SQLException e) {
				throw new CachedException(e);
			}
		}
		
	}

	protected UserSettings(Connection conn, long discordId) {
		this.conn = conn;
		this.discordId = discordId;

		this.dbLang = null;//new Cached<>(this::queryLang, this::flushLang);
	}

	public void update() throws SQLException {
		dbLang.flush();
	}

	public String getLang() {
		return dbLang.get();
	}

	public void setLang(String lang) {
		dbLang.set(lang);
	}

}
