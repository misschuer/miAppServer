package cc.mi.app;

import cc.mi.app.config.ServerConfig;
import cc.mi.app.net.AppHandler;
import cc.mi.core.net.ClientCore;

public class Startup {
	
	private static void start() throws NumberFormatException, Exception {
		ServerConfig.loadConfig();
		ClientCore.start(ServerConfig.getIp(), ServerConfig.getPort(), new AppHandler());
	}

	public static void main(String[] args) throws NumberFormatException, Exception {
		start();
	}
}
