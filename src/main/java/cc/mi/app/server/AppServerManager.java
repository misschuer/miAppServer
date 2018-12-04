package cc.mi.app.server;

import java.util.List;

import cc.mi.app.handler.BinlogDataModifyHandler;
import cc.mi.app.handler.InnerServerConnListHandler;
import cc.mi.app.handler.PlayerLoginHandler;
import cc.mi.app.handler.PlayerLogoutHandler;
import cc.mi.app.loginAction.AppLoginManager;
import cc.mi.core.binlog.data.BinlogData;
import cc.mi.core.callback.AbstractCallback;
import cc.mi.core.callback.Callback;
import cc.mi.core.constance.IdentityConst;
import cc.mi.core.constance.ObjectType;
import cc.mi.core.generate.Opcodes;
import cc.mi.core.generate.stru.BinlogInfo;
import cc.mi.core.log.CustomLogger;
import cc.mi.core.manager.ServerManager;
import cc.mi.core.utils.ServerProcessBlock;

public class AppServerManager extends ServerManager {
	static final CustomLogger logger = CustomLogger.getLogger(AppServerManager.class);
	private static AppServerManager instance = new AppServerManager();
	
	public final AppLoginManager loginManager = new AppLoginManager();
	// 对象管理
	public final AppObjectManager objManager = new AppObjectManager();
	
	public static AppServerManager getInstance() {
		return instance;
	}
	
	@Override
	protected void onOpcodeInit() {
		handlers.put(Opcodes.MSG_BINLOGDATAMODIFY, new BinlogDataModifyHandler());
		handlers.put(Opcodes.MSG_INNERSERVERCONNLIST, new InnerServerConnListHandler());
		handlers.put(Opcodes.MSG_APPPLAYERLOGINMSG, new PlayerLoginHandler());
		handlers.put(Opcodes.MSG_APPPLAYERLOGOUTMSG, new PlayerLogoutHandler());
		
		
		opcodes.addAll(handlers.keySet());
	}
	
	public AppServerManager() {
		super(IdentityConst.SERVER_TYPE_APP);
	}
	
	@Override
	protected void onProcessInit() {
		this.process = new ServerProcessBlock() {
			@Override
			public void run(int diff) {
				instance.doInit();
			}
		};
	}
	
	/**
	 * 进行帧刷新
	 */
	@Override
	protected void doWork(int diff) {
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
			this.objManager.parseBinlogInfo(binlogInfo);
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
	
	protected void addTagWatchCallback(String ownerTag, Callback<Void> callback) {
		objManager.addCreateCallback(ownerTag, callback);
	}
	
	public void putObjects(String ownerId, final List<BinlogData> result, AbstractCallback<Boolean> abstractCallback) {
		this.objManager.putObjects(this.centerChannel, ownerId, result, abstractCallback);
	}
	
	public void putObject(String ownerId, BinlogData result, Callback<Boolean> callback) {
		this.objManager.putObject(this.centerChannel, ownerId, result, callback);
	}
}
