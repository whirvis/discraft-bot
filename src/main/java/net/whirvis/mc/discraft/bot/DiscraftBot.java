package net.whirvis.mc.discraft.bot;

import java.io.File;

import io.javalin.Javalin;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.AnnotatedEventManager;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscraftBot {
	
	private JDA bot;
	private DiscraftBotConfig config;

	public DiscraftBot(File configFile) {
		this.config = new DiscraftBotConfig(configFile);
	}

	public void startup() throws Exception {
		config.load();
		
		JDABuilder builder = JDABuilder.createLight(config.getBotToken());
		builder.setEnabledIntents(GatewayIntent.GUILD_PRESENCES,
				GatewayIntent.GUILD_MESSAGES, GatewayIntent.DIRECT_MESSAGES);
		builder.enableCache(CacheFlag.ACTIVITY);
		builder.setEventManager(new AnnotatedEventManager());
		builder.addEventListeners(this);

		this.bot = builder.build().awaitReady();
		
		Javalin app = Javalin.create().start(30851);
        app.get("/", ctx -> {
        	String status = ctx.req.getParameter("status");
        	
        	bot.getPresence().setActivity(Activity.playing(status));
        	
        	ctx.result("Set bot status to: " + status);	
        });
	}
	
	public void shutdown() {
		bot.shutdown();
	}
	
	public static void main(String[] args) throws Exception {
		File configFile = new File(args[0]);
		DiscraftBot bot = new DiscraftBot(configFile);
		bot.startup();
	}

}
