package cc.mi.app.server;

import cc.mi.core.binlog.data.BinlogData;
import cc.mi.core.constance.IdentityConst;
import cc.mi.core.packet.Packet;
import cc.mi.core.server.GuidManager;
import cc.mi.core.server.ServerObjectManager;

public class AppObjectManager extends ServerObjectManager {
	public static final AppObjectManager INSTANCE = new AppObjectManager();
	private AppObjectManager() {
		super(IdentityConst.SERVER_TYPE_APP);
	}

	public AppContextPlayer findPlayer(String guid) {
		if (!GuidManager.INSTANCE.isPlayerGuid(guid)) {
			return null;
		}
		return (AppContextPlayer)this.get(guid);
	}
	
	@Override
	protected BinlogData createBinlogData(String guid) {
		if (GuidManager.INSTANCE.isPlayerGuid(guid)) {
			return new AppContextPlayer();
		}
		return new BinlogData(1 << 6, 1 << 6);
	}
	
	@Override
	public boolean update(int diff) {
		Packet packet = this.getUpdatePacket();
		if (packet != null)
			AppServerManager.getInstance().sendToCenter(packet);
		return true;
	}
}
