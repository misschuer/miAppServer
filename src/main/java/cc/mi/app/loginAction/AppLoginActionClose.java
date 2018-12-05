package cc.mi.app.loginAction;

import cc.mi.app.server.AppContextPlayer;
import cc.mi.app.server.AppObjectManager;
import cc.mi.core.constance.LoginActionEnum;
import cc.mi.core.loginAction.LoginActionBase;

public class AppLoginActionClose extends LoginActionBase {
	private boolean delWatched = false;
	
	public AppLoginActionClose(int fd, String guid) {
		super(fd, guid);
	}

	@Override
	public boolean update(int diff) {
		if (!delWatched) {
			AppContextPlayer player = AppObjectManager.INSTANCE.findPlayer(this.getGuid());
			if (player == null) {
//				tea_pwarn("AppdLoginActionClose:Update player not found, %s %u", m_guid.c_str(), m_fd);
				return false;
			}
			player.logout();
			delWatched = true;
			return true;
		}

		//一直到移除完毕才算真正退出完成
		return AppObjectManager.INSTANCE.contains(this.getGuid());
	}

	@Override
	public LoginActionEnum getType() {
		return LoginActionEnum.CONTEXT_LOGIN_ACTION_CLOSE;
	}

}
