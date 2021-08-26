package net.whirvis.mc.discraft.bot.web;

import java.util.Objects;

import io.javalin.Javalin;

/**
 * The Discraft HTTP server.
 * <p>
 * This webserver is used to handle requests coming in from the web. Usually,
 * these requests come from servers running the Discraft plugin.
 */
public class DiscraftWebserver {

	private final int port;
	private final Javalin webserver;

	/**
	 * Creates a Discraft HTTP server.
	 * 
	 * @param port
	 *            the webserver port.
	 */
	public DiscraftWebserver(int port) {
		this.port = port;
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
		webserver.start(port);
	}

	/**
	 * Stops the webserver.
	 */
	public void stop() {
		webserver.stop();
	}

}
