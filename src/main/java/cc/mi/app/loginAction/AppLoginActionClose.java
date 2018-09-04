package cc.mi.app.loginAction;

import cc.mi.core.constance.LoginActionEnum;
import cc.mi.core.loginAction.LoginActionBase;

public class AppLoginActionClose extends LoginActionBase {

	public AppLoginActionClose(int fd, String guid) {
		super(fd, guid);
	}

	@Override
	public boolean update(int diff) {
		return false;
	}

	@Override
	public LoginActionEnum getType() {
		return LoginActionEnum.CONTEXT_LOGIN_ACTION_CLOSE;
	}

}
