package cc.mi.app.handler;

import cc.mi.app.server.AppServerManager;
import cc.mi.core.constance.LoginActionEnum;
import cc.mi.core.generate.msg.AppPlayerLogoutMsg;
import cc.mi.core.handler.HandlerImpl;
import cc.mi.core.packet.Packet;
import cc.mi.core.server.ServerContext;
import io.netty.channel.Channel;

public class PlayerLogoutHandler extends HandlerImpl {

	@Override
	public void handle(ServerContext nil, Channel channel, Packet decoder) {
		AppPlayerLogoutMsg packet = (AppPlayerLogoutMsg) decoder;
		AppServerManager.getInstance().loginManager.pushAction(packet.getGuid(), packet.getClientFd(), LoginActionEnum.CONTEXT_LOGIN_ACTION_CLOSE);
	}

}
