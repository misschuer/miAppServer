package cc.mi.app.loginAction;

import cc.mi.app.server.AppContextPlayer;
import cc.mi.app.server.AppServerManager;
import cc.mi.core.constance.LoginActionEnum;
import cc.mi.core.loginAction.LoginActionBase;

public class AppLoginActionLogin extends LoginActionBase {
	
	private boolean addWatched = false;
	
	public AppLoginActionLogin(int fd, String guid) {
		super(fd, guid);
	}

	@Override
	public boolean update(int diff) {
		AppContextPlayer player = (AppContextPlayer) AppServerManager.getInstance().objManager.get(this.getGuid());
		if (!this.addWatched) {
			if (player != null) {
				//有可能玩家退出的时候，应用服正好重启，玩家未正确退出
//				AppdApp::g_app->m_login_mgr->InsertCloseAction(m_guid);
				return true;
			}
			AppServerManager.getInstance().addTagWatchAndCall(this.getGuid());
			this.addWatched = true;
			return true;
		}
		
		if (player == null) {
			return true;
		}
		
		player.login(this.getFd());
		
		return false;
	}

	@Override
	public LoginActionEnum getType() {
		return LoginActionEnum.CONTEXT_LOGIN_ACTION_LOGIN;
	}

}
