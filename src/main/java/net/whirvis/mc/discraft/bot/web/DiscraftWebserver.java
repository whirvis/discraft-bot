package net.whirvis.mc.discraft.bot.web;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import io.javalin.Javalin;
import net.whirvis.mc.discraft.bot.config.DiscraftBotConfig;

/**
 * The Discraft HTTP server.
 * <p>
 * This webserver is used to handle requests coming in from the web. Usually,
 * these requests come from servers running the Discraft plugin.
 */
public class DiscraftWebserver {

	private final DiscraftBotConfig config;
	private final Javalin webserver;

	/**
	 * Creates a Discraft HTTP server.
	 * 
	 * @param config
	 *            the bot config.
	 * @throws NullPointerException
	 *             if {@code config} is {@code null}.
	 */
	public DiscraftWebserver(@NotNull DiscraftBotConfig config) {
		this.config = Objects.requireNonNull(config, "config");
		this.webserver = Javalin.create();
	}

	/**
	 * Registers an endpoint to the webserver.
	 * 
	 * @param endpoint
	 *            the endpoint to register.
	 * @throws NullPointerException
	 *             if {@code endpoint} is {@code null}.
	 */
	public void registerEndpoint(DiscraftHandler endpoint) {
		Objects.requireNonNull(endpoint, "endpoint");
		webserver.addHandler(endpoint.type, endpoint.path, endpoint);
	}

	/**
	 * Starts the webserver.
	 */
	public void start() {
		webserver.start(config.getWebserverPort());
	}

	/**
	 * Stops the webserver.
	 */
	public void stop() {
		webserver.stop();
	}

}
