package net.whirvis.mc.discraft.bot;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class DiscraftBotConfig {
	
	private final File configFile;
	private final Properties properties;
	
	private String botToken;
	
	public DiscraftBotConfig(File configFile) {
		this.configFile = configFile;
		this.properties = new Properties();
	}
	
	public void load() throws IOException {
		FileReader configReader = new FileReader(configFile);
		properties.load(configReader);
		
		this.botToken = properties.getProperty("bot-token");
	}
	
	public String getBotToken() {
		return this.botToken;
	}
	
}
