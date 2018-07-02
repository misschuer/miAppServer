package cc.mi.app.server;

import cc.mi.core.constance.IdentityConst;
import cc.mi.core.manager.ServerManager;

public class AppServerManager extends ServerManager {
	private static AppServerManager instance;
	
	public static AppServerManager getInstance() {
		if (instance == null) {
			instance = new AppServerManager();
		}
		return instance;
	}
	
	public AppServerManager() {
		super(IdentityConst.SERVER_TYPE_APP);
	}
}
