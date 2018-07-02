package cc.mi.app.net;

import cc.mi.app.server.AppServerManager;
import cc.mi.core.handler.ChannelHandlerGenerator;
import cc.mi.core.packet.Packet;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class AppToGateHandler extends SimpleChannelInboundHandler<Packet> implements ChannelHandlerGenerator {
	public void channelActive(final ChannelHandlerContext ctx) {
		AppServerManager.getInstance().onGateConnected(ctx.channel());
	}
	
	@Override
	public void channelRead0(final ChannelHandlerContext ctx, final Packet coder) throws Exception {
		//TODO: 这里应该不会有, 我们只做从本服到网关服的单向通信
	}
	
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		AppServerManager.getInstance().onGateDisconnected(ctx.channel());
		ctx.fireChannelInactive();
	}

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable throwable) {
		throwable.printStackTrace();
		ctx.close();
	}

	@Override
	public ChannelHandler newChannelHandler() {
		return new AppToGateHandler();
	}
}
