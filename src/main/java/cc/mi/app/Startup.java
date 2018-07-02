package cc.mi.app;

import cc.mi.app.config.ServerConfig;

public class Startup {
	
	private static void start() throws NumberFormatException, Exception {
		ServerConfig.loadConfig();
//		ClientCore.start(ServerConfig.getGateIp(), ServerConfig.getGatePort(), new AppToGateHandler(), false);
//		ClientCore.start(ServerConfig.getCenterIp(), ServerConfig.getCenterPort(), new AppHandler());
	}

	public static void main(String[] args) throws NumberFormatException, Exception {
		start();
	}
}
