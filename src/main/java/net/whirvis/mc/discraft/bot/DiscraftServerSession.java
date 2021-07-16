package net.whirvis.mc.discraft.bot;

import java.net.InetSocketAddress;

public class DiscraftServerSession {
	
	private final InetSocketAddress address;
	private long lastHeartbeat;
	
	public DiscraftServerSession(InetSocketAddress address) {
		this.address = address;
	}
	
	public void handleRequest() {
		
	}
	
}
