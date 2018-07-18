package cc.mi.app.server;

import cc.mi.core.constance.IdentityConst;
import cc.mi.core.server.ServerObjectManager;

public class AppObjectManager extends ServerObjectManager {
	protected AppObjectManager() {
		super(IdentityConst.SERVER_TYPE_APP);
	}

}
