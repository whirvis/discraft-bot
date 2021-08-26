package net.whirvis.mc.discraft.bot.link;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import net.dv8tion.jda.api.entities.User;

public class LinkManager {

	private final Map<User, LinkProcess> activeLinks;

	private final Connection conn;
	
	public LinkManager(Connection conn) {
		this.conn = conn;
		this.activeLinks = new HashMap<>();
	}
	
	public boolean isLinking(User user) {
		return activeLinks.containsKey(user);
	}
	
	public LinkProcess beginLinking(User user) {
		LinkProcess process = new LinkProcess(user);
		activeLinks.put(user, process);
		return process;
	}

	public LinkProcess activateCode(UUID uuid, String secretCode) throws SQLException {
		Objects.requireNonNull(uuid, "uuid");
		Objects.requireNonNull(secretCode, "secretCode");
		
		LinkProcess link = null;
		for (LinkProcess process : activeLinks.values()) {
			if (secretCode.equalsIgnoreCase(process.getSecret())) {
				link = process;
				break;
			}
		}
		
		if (link != null) {
			PreparedStatement stmt = conn.prepareStatement("INSERT INTO minecraft_links VALUES(?, ?)");
			stmt.setLong(1, link.getUser().getIdLong());
			stmt.setString(2, uuid.toString());
			stmt.execute();
		}
		return link;
	}

}
