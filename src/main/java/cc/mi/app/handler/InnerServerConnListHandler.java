package cc.mi.app.handler;

import java.util.List;

import cc.mi.app.server.AppServerManager;
import cc.mi.core.generate.msg.InnerServerConnList;
import cc.mi.core.handler.HandlerImpl;
import cc.mi.core.packet.Packet;
import cc.mi.core.server.ServerContext;
import io.netty.channel.Channel;

public class InnerServerConnListHandler extends HandlerImpl {

	@Override
	public void handle(ServerContext nil, Channel channel, Packet decoder) {
		InnerServerConnList isc = (InnerServerConnList) decoder;
		
		int appConn    = isc.getAppConn();
		int loginConn  = isc.getLoginConn();
		int recordConn = isc.getRecordConn();
		List<Integer> sceneConns = isc.getSceneConns();
		
		AppServerManager.getInstance().getConnList().setAppConn(appConn);
		AppServerManager.getInstance().getConnList().setLoginConn(loginConn);
		AppServerManager.getInstance().getConnList().setRecordConn(recordConn);
		AppServerManager.getInstance().getConnList().setSceneConns(sceneConns);
	}

}
