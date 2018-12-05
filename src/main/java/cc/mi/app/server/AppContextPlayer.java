package cc.mi.app.server;

import cc.mi.core.binlog.data.BinlogData;
import cc.mi.core.callback.AbstractCallback;
import cc.mi.core.constance.BinlogFlag;
import cc.mi.core.constance.ObjectType;
import cc.mi.core.constance.PlayerEnumFields;
import cc.mi.core.log.CustomLogger;
import cc.mi.core.server.GuidManager;
import cc.mi.core.server.PlayerBase;
import cc.mi.core.server.SessionStatus;

public class AppContextPlayer extends PlayerBase {
	static final CustomLogger logger = CustomLogger.getLogger(AppContextPlayer.class);
	private AppContext context = new AppContext();

	public AppContextPlayer() {
		super(PlayerEnumFields.PLAYER_INT_FIELDS_SIZE, PlayerEnumFields.PLAYER_STR_FIELDS_SIZE);
	}

	public boolean login(int fd) {
		logger.devLog("AppContextPlayer login guid={} fd={}", this.getGuid(), fd);
		
		if (this.context.getFd() != 0) {
			//上一个还没退出，是因为同步问题造成的，关了，几率较小
			logger.warnLog("AppdContext Login fd_ != 0  guid={}  fd= {}  prevFd={}", this.getGuid(), fd, this.context.getFd());
			this.context.closeSession(0);
			return false;
		}
		this.context.changeFd(fd);
		this.context.setGuid(this.getGuid());
		this.context.setStatus(SessionStatus.STATUS_AUTHED); //到这里的都是验证通过的

		this.newOtherBinlog();
//		//插入账号信息查询库
//		InsertPlayerMap();
		
//		AppdApp::g_app->RegSessionOpts(fd_);
		//登录完毕
		this.context.setStatus(SessionStatus.STATUS_LOGGEDIN); //登录成功
//		//发个登录应用服完毕的包给客户端
//		Call_join_or_leave_server(m_delegate_sendpkt, 0, SERVER_TYPE_APPD, getpid(), AppdApp::g_app->Get_Connection_ID(), uint32(time(nullptr)));
//
//		//玩家登录以后，做点什么。 TODO: 这里决定是否需要处理邀请的玩家
//		DoPlayerLogin();
//		if(!AppdApp::g_app->IsPKServer())
//		{
//			AppdApp::g_app->login_account_guid(this);
//		}
//
//		// 把自己加到等级列表中
//		this->online();
//
//		//同步时间
//		packet pkt;
//		Handle_Syns_Mstime(pkt);

		return true;
	}

	public void logout() {
		if (this.context.getStatus() == SessionStatus.STATUS_LOGGEDIN) {
//			//玩家下线以后，做点什么。
//			DoPlayerLogout();
//
//			//从账号map中删除
//			RemovePlayerMap();

			//状态置为下线
			this.context.setStatus(SessionStatus.STATUS_DELETE);

//			//还不需要释放，所以置0就好
//			AppdApp::g_app->ChangeSessionID(this,0);
//
//			// 把自己从等级列表清除
//			this->offline();
		}

		AppServerManager.getInstance().delTagWatch(this.getGuid());
	}
	
	private void newOtherBinlog() {
		// 社交信息ObjectTypeSocial
		this.newOtherBinlog(ObjectType.PLAYER_SOCIAL, BinlogFlag.PLAYER_APPD_INT_FIELD_FLAGS_SOCIAL_CREATE, null);
	}
	
	private void newOtherBinlog(char objectType, int flag, AbstractCallback<BinlogData> callback) {
		String newGuid = GuidManager.INSTANCE.replaceSuffix(this.context.getGuid(), objectType);
		this.newOtherBinlog(newGuid, flag, callback);
	}
	
	private void newOtherBinlog(final String newGuid, int flag, AbstractCallback<BinlogData> callback) {
		if (!AppObjectManager.INSTANCE.contains(newGuid)) {//如果不存在，重新建一个
			logger.devLog("AppContext newOtherBinlog guid ={} newGuid={}", this.context.getGuid(), newGuid);
			
			if (this.isBinlogCreated((short) flag)) {
				logger.devLog("AppContext newOtherBinlog err, player.isBinlogCreated(%u), guid ={} newGuid={}", flag, this.context.getGuid(), newGuid);
				AppServerManager.getInstance().addWatchAndCall(newGuid, new AbstractCallback<Void>() {
					@Override
					public void invoke(Void value) {
//						// 技能的话生成临时数据
//						if (GuidManager::GetPrefix(new_guid) == ObjectTypeSpell) {
//							player->generateTempSpellLevelInfo(ObjMgr.Get(new_guid));
//						}
					}
				});
				return;
			}
			//没有取到，就new个
			this.createOtherBinlog(newGuid, flag, callback);

		} else {
//			// 技能的话生成临时数据
//			if (GuidManager::GetPrefix(new_guid) == ObjectTypeSpell) {
//				string player_guid = guid();
//				AppdContext *player = ObjMgr.FindPlayer(player_guid);
//				if (player) {
//					player->generateTempSpellLevelInfo(ObjMgr.Get(new_guid));
//				}
//			}
		}
	}
	
	private void createOtherBinlog(final String newGuid, int flag, AbstractCallback<BinlogData> callback) {
		BinlogData binlogData = null;
		
		if (callback != null) {
			binlogData = callback.createObject();
		} else {
			binlogData = new BinlogData(1 << 6, 1 << 6);
		}
		
		binlogData.setGuid(newGuid);
		binlogData.setOwner(this.context.getGuid());
		this.setBinlogCreate((short) flag);
		
		AppServerManager.getInstance().putObject(this.context.getGuid(), binlogData, null);
//
//		// 如果创建的是技能对象那就让技能槽的对象拷贝过去
//		if (GuidManager::GetPrefix(new_guid) == ObjectTypeSpell) {
//			this->firstLoginAndCopySpellInfoToGuidObject(binlog);
//			// 第一次初始化任务对象
//		} else if (GuidManager::GetPrefix(new_guid) == ObjectTypeQuest) {
//			this->firstLoginAndInitQuestGuidObject();
//		}
	}
	
	public AppContext getContext() {
		return context;
	}

	public void setContext(AppContext context) {
		this.context = context;
	}
}
