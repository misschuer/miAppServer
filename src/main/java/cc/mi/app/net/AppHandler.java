package cc.mi.app.net;

import cc.mi.app.server.AppServerManager;
import cc.mi.core.handler.ChannelHandlerGenerator;
import cc.mi.core.packet.Packet;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AppHandler extends SimpleChannelInboundHandler<Packet> implements ChannelHandlerGenerator {
	public void channelActive(final ChannelHandlerContext ctx) {
		AppServerManager.getInstance().onCenterConnected(ctx.channel());
	}
	
	@Override
	public void channelRead0(final ChannelHandlerContext ctx, final Packet coder) throws Exception {
		AppServerManager.getInstance().pushPacket(coder);
	}
	
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		AppServerManager.getInstance().onCenterDisconnected(ctx.channel());
		ctx.fireChannelInactive();
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
		throwable.printStackTrace();
		ctx.close();
	}

	@Override
	public ChannelHandler newChannelHandler() {
		return new AppHandler();
	}
}
