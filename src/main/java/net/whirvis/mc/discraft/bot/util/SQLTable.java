package net.whirvis.mc.discraft.bot.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SQLTable {

	private static final String SQL_ALPHABET =
			"^[abcdefhijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_]+$";

	private final String table;
	private final List<String> columns;

	public SQLTable(String table) {
		this.table = Objects.requireNonNull(table, "table");
		if (!table.matches(SQL_ALPHABET)) {
			throw new IllegalArgumentException("table name may only contain "
					+ "letters, numbers, and underscores");
		}
		this.columns = new ArrayList<>();
	}

	public void addColumn(String column) {
		Objects.requireNonNull(column, "column");
		if (column.length() > 32) {
			throw new IllegalArgumentException("column.length() > 32");
		} else if (column.matches(SQL_ALPHABET)) {
			throw new IllegalArgumentException("column name may only contain "
					+ "letters, numbers, and underscores");
		}
		columns.add(column);
	}

	public void removeColumn(String column) {
		columns.remove(column);
	}
	
	private String generateWhere(Where... where) {
		if(where == null) {
			return null;
		} else if(where.length <= 0) {
			return "";
		}
		
		StringBuilder whereBuilder = new StringBuilder("WHERE ");
		for(int i = 0; i < where.length; i++) {
			//whereBuilder.append();
		}
		return whereBuilder.toString();
	}

	public void updateColumn(@NotNull Connection conn, @NotNull String column,
			@Nullable Object value) throws SQLException {
		Objects.requireNonNull(conn, "conn");
		Objects.requireNonNull(column, "column");
		if (!columns.contains(column)) {
			throw new IllegalArgumentException("no such column");
		}

		PreparedStatement stmt = conn.prepareStatement("UPDATE " + table
				+ " SET " + column + " = ? WHERE discord_id = ?");
		
		stmt.setObject(1, value);
		//stmt.setLong(2, discordId);
		stmt.execute();
	}

	@SuppressWarnings("unchecked")
	private <T> T queryColumn(Connection conn, long discordId,
			String column) throws SQLException {
		//if (!SQL_COLUMNS.contains(column)) {
		//	throw new IllegalArgumentException("no such column");
		//}
		String querySql = "SELECT " + column + " FROM " + table;
			//	+ " WHERE discord_id = ?";
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

}
