package cc.mi.app.server;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cc.mi.app.handler.BinlogDataModifyHandler;
import cc.mi.core.callback.AbstractCallback;
import cc.mi.core.callback.Callback;
import cc.mi.core.constance.IdentityConst;
import cc.mi.core.constance.ObjectType;
import cc.mi.core.generate.Opcodes;
import cc.mi.core.generate.stru.BinlogInfo;
import cc.mi.core.handler.Handler;
import cc.mi.core.log.CustomLogger;
import cc.mi.core.manager.ServerManager;
import cc.mi.core.packet.Packet;
import cc.mi.core.utils.ServerProcessBlock;

public class AppServerManager extends ServerManager {
	static final CustomLogger logger = CustomLogger.getLogger(AppServerManager.class);
	private static AppServerManager instance;
	// 消息收到以后的回调
	private static final Map<Integer, Handler> handlers = new HashMap<>();
	private static final List<Integer> opcodes;
	
	// 帧刷新
	private static final ScheduledExecutorService excutor = Executors.newScheduledThreadPool(1);
	// 消息包队列
	private static final Queue<Packet> packetQueue = new LinkedList<>();
	// 当前帧刷新执行的代码逻辑
	protected ServerProcessBlock process;
	// 最后一次执行帧刷新的时间戳
	protected long timestamp = 0;
	
	// 对象管理
	private final AppObjectManager objManager = new AppObjectManager();
	
	static {
		handlers.put(Opcodes.MSG_BINLOGDATAMODIFY, new BinlogDataModifyHandler());
		
		opcodes = new LinkedList<>();
		opcodes.addAll(handlers.keySet());
	}
	
	public static AppServerManager getInstance() {
		if (instance == null) {
			instance = new AppServerManager();
			instance.process = new ServerProcessBlock() {
				@Override
				public void run(int diff) {
					instance.doInit();
				}
			};
		}
		return instance;
	}
	
	public AppServerManager() {
		super(IdentityConst.SERVER_TYPE_APP, opcodes);
		excutor.scheduleWithFixedDelay(new Runnable() {
			@Override
			public void run() {
				long prev = instance.timestamp;
				long now = System.currentTimeMillis();
				int diff = 0;
				if (prev > 0) diff = (int) (now - prev);
				instance.timestamp = now;
				if (diff < 0 || diff > 1000) {
					logger.warnLog("too heavy logical that execute");
				}
				try {
					instance.doWork(diff);
				} catch (Throwable t) {
					t.printStackTrace();
				}
			}
		}, 1000, 100, TimeUnit.MILLISECONDS);
	}
	
	/**
	 * 进行帧刷新
	 */
	private void doWork(int diff) {
		// 初始化服务器
		this.doProcess(diff);
		// 处理包信息
		this.dealPacket();
	}
	
	private void doInit() {
		if (this.centerChannel == null) {
			return;
		}
		logger.devLog("do init");
		this.addTagWatchAndCall(ObjectType.FACTION_BINLOG_OWNER_STRING);
		this.addTagWatchAndCall(ObjectType.GROUP_BINLOG_OWNER_STRING);
		this.addTagWatchAndCall(ObjectType.GLOBAL_VALUE_OWNER_STRING, new AbstractCallback<Void>() {
			@Override
			public void invoke(Void value) {
				instance.onDataReady();
			}
		});
		instance.process = null;
	}
	
	public void onBinlogDatasUpdated(List<BinlogInfo> binlogInfoList) {
		for (BinlogInfo binlogInfo : binlogInfoList) {
			objManager.parseBinlogInfo(binlogInfo);
		}
	}
	
	private void onDataReady() {
		this.process = new ServerProcessBlock() {
			@Override
			public void run(int diff) {
			}
		};
		this.startReady();
	}
	
	private void doProcess(int diff) {
		if (this.process != null) {
			this.process.run(diff);
		}
	}
	
	private void dealPacket() {
		while (!packetQueue.isEmpty()) {
			Packet packet = packetQueue.poll();
			this.invokeHandler(packet);
		}
	}
	
	private void invokeHandler(Packet packet) {
		int opcode = packet.getOpcode();
		Handler handle = handlers.get(opcode);
		if (handle != null) {
			handle.handle(null, this.centerChannel, packet);
		}
	}
	
	public void pushPacket(Packet packet) {
		//TODO: 检测包的频率(这里需要么?)
		synchronized (this) {
			packetQueue.add(packet);
		}
	}
	
	protected void addTagWatchCallback(String ownerTag, Callback<Void> callback) {
		objManager.addCreateCallback(ownerTag, callback);
	}
}
