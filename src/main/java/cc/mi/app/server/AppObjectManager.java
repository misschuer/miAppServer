package cc.mi.app.server;

import cc.mi.core.binlog.data.BinlogData;
import cc.mi.core.constance.IdentityConst;
import cc.mi.core.server.ServerObjectManager;

public class AppObjectManager extends ServerObjectManager {
	protected AppObjectManager() {
		super(IdentityConst.SERVER_TYPE_APP);
	}

	@Override
	protected BinlogData createBinlogData(String guid) {
		return new BinlogData(1 << 6, 1 << 6);
	}
}
