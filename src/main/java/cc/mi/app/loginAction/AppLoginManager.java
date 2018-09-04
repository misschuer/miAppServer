package cc.mi.app.loginAction;

import java.util.LinkedList;
import java.util.Queue;

import cc.mi.core.constance.LoginActionEnum;
import cc.mi.core.log.CustomLogger;
import cc.mi.core.loginAction.ContextLoginManager;
import cc.mi.core.loginAction.LoginActionBase;

public class AppLoginManager extends ContextLoginManager {
	
	static final CustomLogger logger = CustomLogger.getLogger(AppLoginManager.class);
	
	@Override
	public void pushAction(String guid, int fd, LoginActionEnum actionType) {
		logger.devLog("AppLoginManager PushAction guid={} fd={} type={}", guid, fd, actionType);
		
		LoginActionBase action = null;
		if (actionType == LoginActionEnum.CONTEXT_LOGIN_ACTION_LOGIN) {
			action = new AppLoginActionLogin(fd, guid);
		} else if (actionType == LoginActionEnum.CONTEXT_LOGIN_ACTION_CLOSE) {
			action = new AppLoginActionClose(fd, guid);
		}
		
		Queue<LoginActionBase> queue = null;
		if (this.actionHash.containsKey(guid)) {
			queue = this.actionHash.get(guid);
		} else {
			queue = new LinkedList<>();
			this.actionHash.put(guid, queue);
		}
		
		queue.add(action);
	}
}
