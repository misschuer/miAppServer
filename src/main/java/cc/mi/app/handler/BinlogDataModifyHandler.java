package cc.mi.app.handler;

import cc.mi.app.server.AppServerManager;
import cc.mi.core.generate.msg.BinlogDataModify;
import cc.mi.core.handler.HandlerImpl;
import cc.mi.core.packet.Packet;
import cc.mi.core.server.ServerContext;
import io.netty.channel.Channel;

public class BinlogDataModifyHandler extends HandlerImpl {

	@Override
	public void handle(ServerContext player, Channel channel, Packet decoder) {
		BinlogDataModify bdm = (BinlogDataModify)decoder;
		AppServerManager.getInstance().onBinlogDatasUpdated(bdm.getBinlogInfoList());
	}
}