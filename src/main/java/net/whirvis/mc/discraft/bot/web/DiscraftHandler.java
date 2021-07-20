package net.whirvis.mc.discraft.bot.web;

import java.util.Objects;

import org.jetbrains.annotations.NotNull;

import io.javalin.http.Handler;
import io.javalin.http.HandlerType;

/**
 * A handler for a Discraft webserver endpoint.
 * 
 * @see DiscraftWebserver
 */
public abstract class DiscraftHandler implements Handler {

	public final String path;
	public final HandlerType type;

	/**
	 * Constructs a new {@code DiscraftHandler}.
	 * 
	 * @param path
	 *            the endpoint path.
	 * @param type
	 *            the endpoint type.
	 * @throws NullPointerException
	 *             if {@code path} or {@code type} are {@code null}.
	 */
	public DiscraftHandler(@NotNull String path, @NotNull HandlerType type) {
		this.path = Objects.requireNonNull(path, "path");
		this.type = Objects.requireNonNull(type, "type");
	}

}
