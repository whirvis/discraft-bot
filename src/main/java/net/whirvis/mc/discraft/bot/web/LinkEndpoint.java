package net.whirvis.mc.discraft.bot.web;

import java.util.UUID;

import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import net.dv8tion.jda.api.entities.User;
import net.whirvis.mc.discraft.bot.link.LinkManager;
import net.whirvis.mc.discraft.bot.link.LinkProcess;

public class LinkEndpoint extends DiscraftHandler {

	private final LinkManager linkManager;

	public LinkEndpoint(LinkManager linkManager) {
		super("/link", HandlerType.GET);
		this.linkManager = linkManager;
	}

	@Override
	public void handle(Context ctx) throws Exception {
		String uuid = ctx.req.getParameter("user");
		String code = ctx.req.getParameter("secret");
		if (uuid == null || code == null) {
			ctx.res.setStatus(400);
			return;
		}
		
		LinkProcess link = linkManager.activateCode(UUID.fromString(uuid), code);
		if (link != null) {
			User user = link.getUser();
			user.openPrivateChannel().queue(channel -> channel
					.sendMessage("You have linked your account!").queue());
		} else {
			ctx.res.setStatus(403);
		}
	}

}
