package net.whirvis.mc.discraft.bot.link;

import java.util.Random;

import net.dv8tion.jda.api.entities.User;

public class LinkProcess {

	private static final String SECRET_ALPHABET =
			"ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	private static String genSecret(int length) {
		Random rand = new Random();
		
		int max = SECRET_ALPHABET.length();
		char[] secret = new char[length];
		for (int i = 0; i < secret.length; i++) {
			int index = rand.nextInt(max);
			secret[i] = SECRET_ALPHABET.charAt(index);
		}
		
		return new String(secret);
	}

	private final User user;
	private final String secretCode;

	public LinkProcess(User user) {
		this.user = user;
		this.secretCode = genSecret(6);
	}

	public User getUser() {
		return this.user;
	}
	
	public String getSecret() {
		return this.secretCode;
	}

}
